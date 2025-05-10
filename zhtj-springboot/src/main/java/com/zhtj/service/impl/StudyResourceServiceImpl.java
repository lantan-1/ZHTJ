package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.domain.Organization;
import com.zhtj.domain.StudyResource;
import com.zhtj.domain.User;
import com.zhtj.domain.dto.StudyResourceDTO;
import com.zhtj.domain.enums.ResourceCategory;
import com.zhtj.domain.vo.StudyResourceVO;
import com.zhtj.mapper.StudyResourceMapper;
import com.zhtj.service.FileService;
import com.zhtj.service.OrganizationService;
import com.zhtj.service.StudyResourceService;
import com.zhtj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习资源服务实现类
 */
@Slf4j
@Service
public class StudyResourceServiceImpl extends ServiceImpl<StudyResourceMapper, StudyResource> implements StudyResourceService {

    @Autowired
    private StudyResourceMapper studyResourceMapper;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganizationService organizationService;
    
    @Value("${file.upload.study-resource.max-size:104857600}")
    private long maxFileSize; // 默认100MB
    
    @Value("${file.upload.study-resource.path}")
    private String resourcePath;
    
    @Override
    public IPage<StudyResourceVO> pageResource(int current, int size, String title, Integer category, Integer orgId) {
        Page<StudyResource> page = new Page<>(current, size);
        return studyResourceMapper.selectResourcePage(page, title, category, orgId);
    }
    
    @Override
    public StudyResourceVO getResourceById(Long id) {
        return studyResourceMapper.selectResourceById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long uploadResource(StudyResourceDTO dto, Long userId, String userName) throws IOException {
        if (dto.getFile() == null || dto.getFile().isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        MultipartFile file = dto.getFile();
        
        // 检查文件大小
        if (file.getSize() > maxFileSize * 1024 * 1024) {
            throw new BusinessException("文件大小超过限制，最大支持" + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        // 获取用户信息和组织信息
        User user = userService.getById(userId.intValue());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 将Long类型的orgId转换为Integer类型（因为Organization使用Integer作为ID）
        Integer orgId = dto.getOrgId() != null ? dto.getOrgId().intValue() : user.getOrganization();
        Organization organization = organizationService.getById(orgId);
        if (organization == null) {
            throw new BusinessException("组织不存在");
        }
        
        // 上传文件
        String fileType;
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && (
                originalFilename.endsWith(".jpg") || 
                originalFilename.endsWith(".jpeg") || 
                originalFilename.endsWith(".png") || 
                originalFilename.endsWith(".gif"))) {
            fileType = "images";
        } else if (originalFilename != null && (
                originalFilename.endsWith(".mp4") || 
                originalFilename.endsWith(".avi") || 
                originalFilename.endsWith(".mov"))) {
            fileType = "videos";
        } else {
            fileType = "documents";
        }
        
        String filePath = fileService.uploadFile(file, fileType);
        
        // 创建资源实体
        StudyResource resource = new StudyResource();
        resource.setTitle(dto.getTitle());
        resource.setDescription(dto.getDescription());
        // 将ResourceCategory转换为对应的categoryId
        resource.setCategoryId(dto.getCategory().getCode());
        resource.setFileUrl(filePath);
        resource.setFileName(originalFilename);
        resource.setFileSize(file.getSize());
        resource.setFormat(getFileFormat(originalFilename));
        resource.setOrganizationId(orgId);
        resource.setOrganizationName(organization.getName());
        resource.setCreatorId(userId.intValue());
        resource.setCreatorName(userName);
        resource.setDownloads(0);
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        
        // 保存到数据库
        save(resource);
        
        return resource.getId().longValue();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateResource(StudyResourceDTO dto) throws IOException {
        if (dto.getId() == null) {
            throw new BusinessException("资源ID不能为空");
        }
        
        StudyResource resource = getById(dto.getId().intValue());
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        
        // 更新基本信息
        resource.setTitle(dto.getTitle());
        resource.setDescription(dto.getDescription());
        resource.setCategoryId(dto.getCategory().getCode());
        resource.setUpdateTime(new Date());
        
        // 如果有新文件，更新文件信息
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            MultipartFile file = dto.getFile();
            
            // 检查文件大小
            if (file.getSize() > maxFileSize * 1024 * 1024) {
                throw new BusinessException("文件大小超过限制，最大支持" + (maxFileSize / 1024 / 1024) + "MB");
            }
            
            // 删除旧文件
            if (StringUtils.hasText(resource.getFileUrl())) {
                fileService.deleteFile(resource.getFileUrl());
            }
            
            // 上传新文件
            String fileType;
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && (
                    originalFilename.endsWith(".jpg") || 
                    originalFilename.endsWith(".jpeg") || 
                    originalFilename.endsWith(".png") || 
                    originalFilename.endsWith(".gif"))) {
                fileType = "images";
            } else if (originalFilename != null && (
                    originalFilename.endsWith(".mp4") || 
                    originalFilename.endsWith(".avi") || 
                    originalFilename.endsWith(".mov"))) {
                fileType = "videos";
            } else {
                fileType = "documents";
            }
            
            String filePath = fileService.uploadFile(file, fileType);
            
            // 更新文件信息
            resource.setFileUrl(filePath);
            resource.setFileName(file.getOriginalFilename());
            resource.setFileSize(file.getSize());
            resource.setFormat(getFileFormat(file.getOriginalFilename()));
        }
        
        return updateById(resource);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteResource(Long id) {
        StudyResource resource = getById(id.intValue());
        if (resource == null) {
            return false;
        }
        
        // 删除物理文件
        if (resource.getFileUrl() != null && !resource.getFileUrl().isEmpty()) {
            try {
                // 调用文件服务删除物理文件
                fileService.deleteFile(resource.getFileUrl());
                log.info("物理文件删除成功: {}", resource.getFileUrl());
            } catch (Exception e) {
                log.error("删除物理文件失败: {}, 错误: {}", resource.getFileUrl(), e.getMessage());
                throw new BusinessException("删除物理文件失败: " + e.getMessage());
            }
        }
        
        // 删除数据库记录
        return removeById(id.intValue());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteResource(Long[] ids) {
        int count = 0;
        for (Long id : ids) {
            if (deleteResource(id)) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    public String getDownloadUrl(Long id) {
        StudyResource resource = getById(id.intValue());
        if (resource == null || !StringUtils.hasText(resource.getFileUrl())) {
            throw new BusinessException("资源不存在或文件路径为空");
        }
        
        // 生成下载链接
        return fileService.getPublicUrl(resource.getFileUrl(), 3600);
    }
    
    @Override
    public boolean recordDownload(Long id) {
        return studyResourceMapper.increaseDownloadCount(id) > 0;
    }
    
    @Override
    public Map<String, Object> getResourceStats(Integer orgId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 查询各分类资源数量
        for (ResourceCategory category : ResourceCategory.values()) {
            LambdaQueryWrapper<StudyResource> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StudyResource::getCategoryId, category.getCode());
            
            if (orgId != null) {
                wrapper.eq(StudyResource::getOrganizationId, orgId);
            }
            
            long count = count(wrapper);
            stats.put(category.getName(), count);
        }
        
        // 总资源数量
        LambdaQueryWrapper<StudyResource> wrapper = new LambdaQueryWrapper<>();
        if (orgId != null) {
            wrapper.eq(StudyResource::getOrganizationId, orgId);
        }
        stats.put("total", count(wrapper));
        
        return stats;
    }
    
    @Override
    public IPage<StudyResource> getResourceList(
            Page<StudyResource> page,
            String title,
            Integer categoryId,
            String format,
            Integer organizationId,
            String keyword,
            String startDate,
            String endDate,
            Integer currentUserId) {
        
        // 处理文件类型筛选 - 转换格式类型编码为具体格式列表
        List<String> formatList = getFormatListByFileType(format);
        
        // 如果传入了具体的组织ID，则只查询该组织的资源
        if (organizationId != null) {
            // 使用带格式列表的查询
            return getResourceListWithOrgAndFormat(
                page, title, categoryId, format, formatList, organizationId, 
                keyword, startDate, endDate, null);
        }
        
        // 获取当前用户的组织ID
        User user = null;
        Integer userOrgId = null;
        if (currentUserId != null) {
            user = userService.getById(currentUserId);
            if (user != null) {
                userOrgId = user.getOrganization();
            }
        }
        
        // 判断是否为系统管理员
        boolean isAdmin = currentUserId != null && userService.isAdmin(currentUserId);
        
        if (isAdmin) {
            // 管理员可以查看所有资源，使用常规查询
            LambdaQueryWrapper<StudyResource> wrapper = new LambdaQueryWrapper<>();
            if (StringUtils.hasText(title)) {
                wrapper.like(StudyResource::getTitle, title);
            }
            if (categoryId != null) {
                wrapper.eq(StudyResource::getCategoryId, categoryId);
            }
            
            // 根据文件格式筛选
            if (formatList != null && !formatList.isEmpty()) {
                // 如果是文件类型组，使用IN条件
                wrapper.in(StudyResource::getFormat, formatList);
            } else if (StringUtils.hasText(format)) {
                // 如果是具体格式，使用EQ条件
                wrapper.eq(StudyResource::getFormat, format);
            }
            
            if (StringUtils.hasText(keyword)) {
                wrapper.and(w -> w.like(StudyResource::getTitle, keyword)
                              .or()
                              .like(StudyResource::getDescription, keyword));
            }
            
            // 添加时间范围查询
            if (StringUtils.hasText(startDate)) {
                wrapper.ge(StudyResource::getCreateTime, startDate + " 00:00:00");
            }
            if (StringUtils.hasText(endDate)) {
                wrapper.le(StudyResource::getCreateTime, endDate + " 23:59:59");
            }
            
            // 默认按创建时间降序排序
            wrapper.orderByDesc(StudyResource::getCreateTime);
            
            return page(page, wrapper);
        } else {
            // 非管理员使用带权限控制的查询，同时处理格式筛选
            return getResourceListWithOrgAndFormat(
                page, title, categoryId, format, formatList, null, 
                keyword, startDate, endDate, userOrgId);
        }
    }
    
    /**
     * 获取文件类型编码对应的格式列表
     */
    private List<String> getFormatListByFileType(String format) {
        if (!StringUtils.hasText(format)) {
            return null;
        }
        
        try {
            com.zhtj.domain.enums.ResourceFileType fileType = com.zhtj.domain.enums.ResourceFileType.valueOf(format);
            List<String> formatList = fileType.getFormats();
            log.debug("按文件类型组筛选: {}, 包含格式: {}", format, formatList);
            return formatList;
        } catch (IllegalArgumentException e) {
            // 不是文件类型组代码，按具体格式筛选
            log.debug("按具体文件格式筛选: {}", format);
            return null;
        }
    }
    
    /**
     * 带组织权限和格式列表的资源查询
     */
    private IPage<StudyResource> getResourceListWithOrgAndFormat(
            Page<StudyResource> page,
            String title,
            Integer categoryId,
            String format,
            List<String> formatList,
            Integer organizationId,
            String keyword,
            String startDate,
            String endDate,
            Integer userOrgId) {
        
        // 如果没有格式列表筛选条件，直接调用Mapper方法
        if (formatList == null || formatList.isEmpty()) {
            return studyResourceMapper.selectResourcesWithOrgPermission(
                page, title, categoryId, format, organizationId, 
                keyword, startDate, endDate, userOrgId);
        } 
        
        // 有格式列表条件，先查询然后再过滤
        IPage<StudyResource> result = studyResourceMapper.selectResourcesWithOrgPermission(
            page, title, categoryId, null, organizationId, 
            keyword, startDate, endDate, userOrgId);
        
        // 过滤符合格式的记录
        List<StudyResource> filteredList = new ArrayList<>();
        for (StudyResource resource : result.getRecords()) {
            if (formatList.contains(resource.getFormat())) {
                filteredList.add(resource);
            }
        }
        
        // 创建新的分页结果
        Page<StudyResource> filteredPage = new Page<>(page.getCurrent(), page.getSize());
        filteredPage.setRecords(filteredList);
        filteredPage.setTotal(filteredList.size()); // 注意：这里的总数可能不准确
        
        return filteredPage;
    }
    
    @Override
    public StudyResource getResourceDetail(Integer id, Integer currentUserId) {
        StudyResource resource = getById(id);
        if (resource == null) {
            return null;
        }
        
        // 检查用户权限
        if (currentUserId != null) {
            // 系统管理员可以访问所有资源
            boolean isAdmin = userService.isAdmin(currentUserId);
            if (isAdmin) {
                return resource;
            }
            
            // 非系统管理员需要检查组织权限
            User user = userService.getById(currentUserId);
            if (user != null && user.getOrganization() != null) {
                Integer userOrgId = user.getOrganization();
                
                // 验证用户是否有权限访问该资源
                int hasAccess = studyResourceMapper.checkResourceAccess(id, userOrgId);
                if (hasAccess <= 0) {
                    throw new BusinessException("您无权访问该资源");
                }
            }
        }
        
        return resource;
    }
    
    @Override
    public Map<String, Object> getResourcePreviewInfo(Integer id, Integer currentUserId) {
        StudyResource resource = getById(id);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        
        // 检查用户权限
        if (currentUserId != null) {
            // 系统管理员可以访问所有资源
            boolean isAdmin = userService.isAdmin(currentUserId);
            if (!isAdmin) {
                // 非管理员需要检查组织权限
                User user = userService.getById(currentUserId);
                if (user != null && user.getOrganization() != null) {
                    Integer userOrgId = user.getOrganization();
                    
                    // 验证用户是否有权限访问该资源
                    int hasAccess = studyResourceMapper.checkResourceAccess(id, userOrgId);
                    if (hasAccess <= 0) {
                        throw new BusinessException("您无权访问该资源");
                    }
                }
            }
        }
        
        Map<String, Object> info = new HashMap<>();
        info.put("id", resource.getId());
        info.put("title", resource.getTitle());
        info.put("fileName", resource.getFileName());
        
        // 处理文件路径，返回标准的静态资源访问路径
        String fileUrl = resource.getFileUrl();
        if (fileUrl != null) {
            // 去掉路径前的斜杠（如果有）
            if (fileUrl.startsWith("/")) {
                fileUrl = fileUrl.substring(1);
            }
            
            // 对文件名进行URL编码
            try {
                fileUrl = URLEncoder.encode(fileUrl, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20"); // 空格编码为%20而非+
                // 加上/api/static/前缀，适配context-path配置
                fileUrl = "/api/static/" + fileUrl;
            } catch (UnsupportedEncodingException e) {
                // 编码失败时使用原始路径
                fileUrl = "/api/static/" + fileUrl;
            }
        }
        
        info.put("fileUrl", fileUrl);
        info.put("previewUrl", fileUrl); // 添加预览URL字段
        info.put("format", resource.getFormat());
        
        return info;
    }
    
    @Override
    public List<Map<String, Object>> getAccessibleOrganizations(Integer currentUserId) {
        User user = userService.getById(currentUserId);
        if (user == null) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取用户所属组织
        Integer orgId = user.getOrganization();
        Organization userOrg = organizationService.getById(orgId);
        if (userOrg == null) {
            return result;
        }
        
        // 添加用户所属组织
        Map<String, Object> userOrgMap = new HashMap<>();
        userOrgMap.put("id", userOrg.getId());
        userOrgMap.put("name", userOrg.getName());
        userOrgMap.put("level", 1); // 用户的组织为顶层
        userOrgMap.put("hasChildren", false); // 默认无子组织
        result.add(userOrgMap);
        
        // 获取用户组织的所有子组织
        List<Organization> childOrgs = getChildOrganizations(orgId);
        if (!childOrgs.isEmpty()) {
            userOrgMap.put("hasChildren", true); // 有子组织
            
            // 将子组织添加到结果列表
            for (Organization childOrg : childOrgs) {
                Map<String, Object> childOrgMap = new HashMap<>();
                childOrgMap.put("id", childOrg.getId());
                childOrgMap.put("name", childOrg.getName());
                childOrgMap.put("level", 2); // 子组织为第二层
                childOrgMap.put("parentId", orgId);
                childOrgMap.put("hasChildren", false); // 简化处理，不再递归获取孙子组织
                result.add(childOrgMap);
            }
        }
        
        return result;
    }
    
    /**
     * 获取指定组织的子组织
     * 
     * @param parentId 父组织ID
     * @return 子组织列表
     */
    private List<Organization> getChildOrganizations(Integer parentId) {
        if (parentId == null) {
            return new ArrayList<>();
        }
        
        // 查询条件：parentId等于指定值
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Organization::getParentId, parentId);
        
        // 执行查询
        return organizationService.list(queryWrapper);
    }
    
    @Override
    public boolean batchDeleteResources(List<Integer> ids, Integer currentUserId) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }
        
        // 循环删除每个资源及其物理文件
        for (Integer id : ids) {
            // 调用已实现的删除方法，确保物理文件也被删除
            deleteResource(id.longValue());
        }
        
        return true;
    }
    
    @Override
    public Map<String, Boolean> checkResourcePermissions(Integer id, Integer currentUserId) {
        Map<String, Boolean> permissions = new HashMap<>();
        permissions.put("canView", true);
        permissions.put("canDownload", true);
        permissions.put("canEdit", hasUpdatePermission(currentUserId, id));
        permissions.put("canDelete", hasDeletePermission(currentUserId, id));
        return permissions;
    }
    
    @Override
    public boolean hasUploadPermission(Integer currentUserId, Integer organizationId) {
        User user = userService.getById(currentUserId);
        if (user == null) {
            return false;
        }
        
        // 判断用户是否为团委书记或副书记
        String leaguePosition = user.getLeaguePosition();
        
        // 更全面的权限判断逻辑
        // 1. 职务判断：团委书记、副书记、组织部长具有上传权限
        boolean hasLeaderRole = (leaguePosition != null && 
                (leaguePosition.equals("团委书记") || 
                 leaguePosition.equals("副书记") || 
                 leaguePosition.equals("团委副书记") ||
                 leaguePosition.equals("组织部长")));
                 
        // 2. 管理员权限判断：系统管理员也具有上传权限
        boolean isAdmin = userService.isAdmin(currentUserId);
        
        // 3. 组织权限判断
        boolean hasOrgAccess = false;
        if (organizationId != null) {
            // 判断用户是否有权限访问该组织
            // 情况1：用户属于该组织
            boolean belongsToOrg = user.getOrganization().equals(organizationId);
            
            // 情况2：用户所属组织是该组织的上级组织
            boolean isParentOrg = false;
            if (!belongsToOrg) {
                Organization targetOrg = organizationService.getById(organizationId);
                if (targetOrg != null && targetOrg.getParentId() != null) {
                    isParentOrg = targetOrg.getParentId().equals(user.getOrganization());
                }
            }
            
            hasOrgAccess = belongsToOrg || isParentOrg;
        }
        
        // 返回最终权限判断结果：(具有管理员角色 或 具有团委领导职务) 且 具有组织访问权限
        return (isAdmin || hasLeaderRole) && hasOrgAccess;
    }
    
    @Override
    public boolean hasUpdatePermission(Integer currentUserId, Integer resourceId) {
        // 获取资源信息
        StudyResource resource = getById(resourceId);
        if (resource == null) {
            return false;
        }
        
        User user = userService.getById(currentUserId);
        if (user == null) {
            return false;
        }
        
        // 判断是否为系统管理员
        boolean isAdmin = userService.isAdmin(currentUserId);
        if (isAdmin) {
            return true;
        }
        
        // 判断是否为资源创建者
        boolean isCreator = resource.getCreatorId().equals(currentUserId);
        if (isCreator) {
            return true;
        }
        
        // 判断是否为团委书记或副书记，且属于资源所属组织或上级组织
        String leaguePosition = user.getLeaguePosition();
        boolean hasLeaderRole = (leaguePosition != null && 
                (leaguePosition.equals("团委书记") || 
                 leaguePosition.equals("副书记") ||
                 leaguePosition.equals("团委副书记")));
        
        // 组织权限判断：自己组织或下级组织的资源
        boolean hasOrgAccess = false;
        if (hasLeaderRole) {
            // 自己组织的资源
            boolean sameOrg = user.getOrganization().equals(resource.getOrganizationId());
            
            // 下级组织的资源
            boolean isChildOrg = false;
            if (!sameOrg) {
                Organization resourceOrg = organizationService.getById(resource.getOrganizationId());
                if (resourceOrg != null && resourceOrg.getParentId() != null) {
                    isChildOrg = resourceOrg.getParentId().equals(user.getOrganization());
                }
            }
            
            hasOrgAccess = sameOrg || isChildOrg;
        }
        
        return hasLeaderRole && hasOrgAccess;
    }
    
    @Override
    public boolean hasDeletePermission(Integer currentUserId, Integer resourceId) {
        // 删除权限与更新权限一致，复用逻辑
        return hasUpdatePermission(currentUserId, resourceId);
    }
    
    /**
     * 获取文件格式（扩展名）
     */
    private String getFileFormat(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase();
    }
    
    /**
     * 根据文件格式获取内容类型
     */
    private String getContentTypeByFormat(String format) {
        if (format == null) {
            return "application/octet-stream";
        }
        
        switch (format.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "doc":
            case "docx":
                return "application/msword";
            case "ppt":
            case "pptx":
                return "application/vnd.ms-powerpoint";
            case "txt":
                return "text/plain";
            case "mp4":
                return "video/mp4";
            case "mp3":
                return "audio/mpeg";
            case "zip":
                return "application/zip";
            case "rar":
                return "application/x-rar-compressed";
            default:
                return "application/octet-stream";
        }
    }
    
    @Override
    public Map<String, Object> getResourceForDownload(Integer id, Integer currentUserId) throws IOException {
        StudyResource resource = getById(id);
        if (resource == null) {
            throw new BusinessException("资源不存在");
        }
        
        // 检查用户权限
        if (currentUserId != null) {
            // 系统管理员可以访问所有资源
            boolean isAdmin = userService.isAdmin(currentUserId);
            if (!isAdmin) {
                // 非管理员需要检查组织权限
                User user = userService.getById(currentUserId);
                if (user != null && user.getOrganization() != null) {
                    Integer userOrgId = user.getOrganization();
                    
                    // 验证用户是否有权限访问该资源
                    int hasAccess = studyResourceMapper.checkResourceAccess(id, userOrgId);
                    if (hasAccess <= 0) {
                        throw new BusinessException("您无权下载该资源");
                    }
                }
            }
        }
        
        // 获取文件路径
        String filePath = resource.getFileUrl();
        if (filePath != null && filePath.startsWith("/")) {
            filePath = filePath.substring(1); // 去掉开头的斜杠
        }
        
        // 构建完整的文件系统路径 - 直接使用资源路径和文件路径
        Path fileSystemPath = Paths.get(resourcePath, filePath);
        byte[] fileData = Files.readAllBytes(fileSystemPath);
        
        // 处理下载URL
        String downloadUrl;
        try {
            String encodedPath = URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString())
                .replace("+", "%20");
            downloadUrl = "/api/static/" + encodedPath;
        } catch (UnsupportedEncodingException e) {
            downloadUrl = "/api/static/" + filePath;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("fileName", resource.getFileName());
        result.put("filePath", filePath);
        result.put("downloadUrl", downloadUrl);
        result.put("fileData", fileData);
        result.put("contentType", getContentTypeByFormat(resource.getFormat()));
        
        return result;
    }
    
    @Override
    public Map<String, Object> getResourceForDownload(Integer id) throws IOException {
        // 简化版不需要校验权限，直接调用完整版方法
        return getResourceForDownload(id, null);
    }
    
    @Override
    public boolean incrementDownloadCount(Integer id) {
        StudyResource resource = getById(id);
        if (resource == null) {
            return false;
        }
        
        // 增加下载次数
        resource.setDownloads(resource.getDownloads() + 1);
        return updateById(resource);
    }
    
    @Override
    public List<Map<String, Object>> getAllCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        for (ResourceCategory category : ResourceCategory.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", category.getCode());
            item.put("name", category.getName());
            categories.add(item);
        }
        
        return categories;
    }
} 