package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.common.api.Result;
import com.zhtj.domain.StudyResource;
import com.zhtj.domain.User;
import com.zhtj.domain.dto.StudyResourceDTO;
import com.zhtj.domain.enums.ResourceCategory;
import com.zhtj.domain.enums.ResourceFileType;
import com.zhtj.domain.vo.StudyResourceVO;
import com.zhtj.service.StudyResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习资源控制器
 */
@Slf4j
@RestController
@RequestMapping("/study-resources")
@Tag(name = "学习资源管理", description = "学习资源的增删改查接口")
public class StudyResourceController extends BaseController {
    
    @Autowired
    private StudyResourceService studyResourceService;
    
    @Value("${file.upload.study-resource.max-size}")
    private long maxFileSize; // 100MB
    
    /**
     * 分页查询学习资源
     */
    @GetMapping
    @Operation(summary = "分页查询学习资源", description = "根据条件分页查询学习资源")
    public Result<Map<String, Object>> listResources(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "资源标题关键字") @RequestParam(required = false) String title,
            @Parameter(description = "资源分类编码") @RequestParam(required = false) Integer categoryId,
            @Parameter(description = "文件格式") @RequestParam(required = false) String format,
            @Parameter(description = "组织ID") @RequestParam(required = false) Integer organizationId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate
    ) {
        // 从BaseController获取当前用户ID
        Integer currentUserId = getCurrentUserIdFromRequest();
        
        Page<StudyResource> pageParam = new Page<>(page, size);
        IPage<StudyResource> result = studyResourceService.getResourceList(
                pageParam, title, categoryId, format, organizationId, keyword, startDate, endDate, 
                currentUserId
        );
        
        // 转换为VO对象
        List<StudyResourceVO> voList = result.getRecords().stream().map(resource -> {
            StudyResourceVO vo = new StudyResourceVO();
            vo.setId(resource.getId());
            vo.setTitle(resource.getTitle());
            vo.setDescription(resource.getDescription());
            vo.setFileUrl(resource.getFileUrl());
            vo.setFileName(resource.getFileName());
            vo.setFileSize(resource.getFileSize());
            vo.setFormat(resource.getFormat());
            vo.setCategoryId(resource.getCategoryId());
            if (resource.getCategoryId() != null) {
                ResourceCategory category = ResourceCategory.getByCode(resource.getCategoryId());
                if (category != null) {
                    vo.setCategoryName(category.getName());
                }
            }
            vo.setOrganizationId(resource.getOrganizationId());
            vo.setCreatorId(resource.getCreatorId());
            vo.setCreatorName(resource.getCreatorName());
            vo.setDownloads(resource.getDownloads());
            vo.setCreateTime(resource.getCreateTime());
            vo.setUpdateTime(resource.getUpdateTime());
            return vo;
        }).collect(Collectors.toList());
        
        // 构建符合前端预期的响应格式
        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", result.getTotal());
        
        return Result.success(response, "查询成功");
    }
    
    /**
     * 获取资源详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取资源详情", description = "根据ID获取资源详情")
    public Result<StudyResourceVO> getResourceById(
            @Parameter(description = "资源ID", required = true) @PathVariable Long id
    ) {
        StudyResourceVO resource = studyResourceService.getResourceById(id);
        return Result.success(resource, "查询成功");
    }
    
    /**
     * 上传学习资源
     */
    @PostMapping("/upload")
    @Operation(summary = "上传学习资源", description = "上传新的学习资源")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'DEPUTY_SECRETARY')")
    public Result<Map<String, Object>> uploadResource(
            @Parameter(description = "资源标题", required = true) @RequestParam String title,
            @Parameter(description = "资源描述") @RequestParam(required = false) String description,
            @Parameter(description = "资源分类", required = true) @RequestParam Integer categoryId,
            @Parameter(description = "组织ID") @RequestParam(required = false) Long organizationId,
            @Parameter(description = "资源文件", required = true) @RequestParam MultipartFile file
    ) throws IOException {
        // 构建DTO
        StudyResourceDTO dto = new StudyResourceDTO();
        dto.setTitle(title);
        dto.setDescription(description);
        dto.setCategory(ResourceCategory.getByCode(categoryId));
        dto.setOrgId(organizationId);
        dto.setFile(file);
        
        // 从BaseController获取当前用户信息
        Integer userId = getCurrentUserIdFromRequest();
        if (userId == null) {
            return Result.failed("用户未登录");
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.failed("用户不存在");
        }
        
        Long resourceId = studyResourceService.uploadResource(dto, userId.longValue(), user.getName());
        
        // 上传成功后获取资源信息
        StudyResourceVO resource = studyResourceService.getResourceById(resourceId);
        
        // 返回需要的资源信息
        Map<String, Object> response = new HashMap<>();
        response.put("id", resourceId);
        response.put("url", resource.getFileUrl());
        response.put("fileName", resource.getFileName());
        response.put("fileSize", resource.getFileSize());
        response.put("format", resource.getFormat());
        
        return Result.success(response, "上传成功");
    }
    
    /**
     * 更新学习资源
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新学习资源", description = "更新已有的学习资源")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'DEPUTY_SECRETARY')")
    public Result<Boolean> updateResource(
            @Parameter(description = "资源ID", required = true) @PathVariable Long id,
            @RequestBody StudyResourceDTO dto
    ) throws IOException {
        dto.setId(id);
        boolean result = studyResourceService.updateResource(dto);
        return Result.success(result, result ? "更新成功" : "更新失败");
    }
    
    /**
     * 删除学习资源
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除学习资源", description = "根据ID删除学习资源")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'DEPUTY_SECRETARY')")
    public Result<Boolean> deleteResource(
            @Parameter(description = "资源ID", required = true) @PathVariable Long id
    ) {
        boolean result = studyResourceService.deleteResource(id);
        return Result.success(result, result ? "删除成功" : "资源不存在");
    }
    
    /**
     * 批量删除学习资源
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除学习资源", description = "批量删除学习资源")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'DEPUTY_SECRETARY')")
    public Result<Integer> batchDeleteResource(
            @Parameter(description = "资源ID列表", required = true) @RequestBody Map<String, List<Long>> params
    ) {
        List<Long> ids = params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.failed("请提供要删除的资源ID");
        }
        
        // 获取当前用户ID
        Integer currentUserId = getCurrentUserIdFromRequest();
        
        // 转换Long列表为Integer列表（batchDeleteResources方法需要Integer类型）
        List<Integer> intIds = ids.stream().map(Long::intValue).collect(Collectors.toList());
        
        // 调用新的batchDeleteResources方法
        boolean success = studyResourceService.batchDeleteResources(intIds, currentUserId);
        
        return Result.success(intIds.size(), success ? "成功删除" + intIds.size() + "条记录" : "删除失败");
    }
    
    /**
     * 下载资源
     */
    @GetMapping("/{id}/download")
    @Operation(summary = "下载资源", description = "下载资源文件")
    public ResponseEntity<byte[]> downloadResource(
            @Parameter(description = "资源ID", required = true) @PathVariable Long id
    ) throws IOException {
        // 使用简化版本的方法，不需要用户ID
        Map<String, Object> downloadInfo = studyResourceService.getResourceForDownload(id.intValue());
        
        byte[] fileData = (byte[]) downloadInfo.get("fileData");
        String fileName = (String) downloadInfo.get("fileName");
        String contentType = (String) downloadInfo.get("contentType");
        
        // 更新下载次数
        studyResourceService.recordDownload(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        
        // 对中文文件名进行URL编码处理
        try {
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replace("+", "%20"); // 空格编码为%20而非+
            
            // RFC 5987 标准编码方式
            String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName;
            headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
        } catch (UnsupportedEncodingException e) {
            // 编码失败时使用原始文件名
            headers.setContentDispositionFormData("attachment", fileName);
        }
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);
    }
    
    /**
     * 获取资源分类列表
     */
    @GetMapping("/categories")
    @Operation(summary = "获取资源分类列表", description = "获取所有的资源分类")
    public Result<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = Arrays.stream(ResourceCategory.values()).map(category -> {
            Map<String, Object> map = new HashMap<>();
            map.put("value", category.getCode());
            map.put("label", category.getName());
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(categories, "获取成功");
    }
    
    /**
     * 获取资源统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取资源统计数据", description = "获取资源的统计数据")
    public Result<Map<String, Object>> getResourceStats(
            @Parameter(description = "组织ID") @RequestParam(required = false) Integer orgId
    ) {
        Map<String, Object> stats = studyResourceService.getResourceStats(orgId);
        return Result.success(stats, "获取成功");
    }
    
    /**
     * 获取用户可访问的组织列表
     */
    @GetMapping("/accessible-organizations")
    @Operation(summary = "获取用户可访问的组织列表", description = "获取当前用户可访问的组织列表")
    public Result<List<Map<String, Object>>> getOrganizations() {
        // 从BaseController获取当前用户ID
        Integer currentUserId = getCurrentUserIdFromRequest();
        
        List<Map<String, Object>> organizations = studyResourceService.getAccessibleOrganizations(currentUserId);
        return Result.success(organizations, "获取组织列表成功");
    }
    
    /**
     * 获取资源预览信息
     */
    @GetMapping("/{id}/preview")
    @Operation(summary = "获取资源预览信息", description = "获取资源的预览信息")
    public Result<Map<String, Object>> getPreviewInfo(
            @Parameter(description = "资源ID", required = true) @PathVariable Integer id
    ) {
        // 从BaseController获取当前用户ID
        Integer currentUserId = getCurrentUserIdFromRequest();
        
        Map<String, Object> previewInfo = studyResourceService.getResourcePreviewInfo(id, currentUserId);
        return Result.success(previewInfo, "获取预览信息成功");
    }
    
    /**
     * 检查资源权限
     */
    @GetMapping("/{id}/permissions")
    @Operation(summary = "检查资源权限", description = "检查当前用户对资源的操作权限")
    public Result<Map<String, Boolean>> checkPermissions(
            @Parameter(description = "资源ID", required = true) @PathVariable Integer id
    ) {
        // 从BaseController获取当前用户ID
        Integer currentUserId = getCurrentUserIdFromRequest();
        
        Map<String, Boolean> permissions = studyResourceService.checkResourcePermissions(id, currentUserId);
        return Result.success(permissions, "获取权限成功");
    }
    
    /**
     * 获取文件类型列表
     */
    @GetMapping("/format-types")
    @Operation(summary = "获取文件类型列表", description = "获取所有的文件类型分组")
    public Result<List<Map<String, Object>>> getFileTypes() {
        List<Map<String, Object>> fileTypes = Arrays.stream(ResourceFileType.values()).map(fileType -> {
            Map<String, Object> map = new HashMap<>();
            map.put("code", fileType.getCode());
            map.put("name", fileType.getName());
            map.put("formats", fileType.getFormats());
            return map;
        }).collect(Collectors.toList());
        
        return Result.success(fileTypes, "获取成功");
    }
} 