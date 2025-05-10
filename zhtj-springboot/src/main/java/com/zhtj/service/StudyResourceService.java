package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.StudyResource;
import com.zhtj.domain.dto.StudyResourceDTO;
import com.zhtj.domain.vo.StudyResourceVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 学习资源服务接口
 */
public interface StudyResourceService extends IService<StudyResource> {
    
    /**
     * 分页查询学习资源
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param title 标题关键字
     * @param category 资源分类
     * @param orgId 组织ID
     * @return 分页结果
     */
    IPage<StudyResourceVO> pageResource(int current, int size, String title, Integer category, Integer orgId);
    
    /**
     * 根据ID查询资源详情
     * 
     * @param id 资源ID
     * @return 资源视图对象
     */
    StudyResourceVO getResourceById(Long id);
    
    /**
     * 上传学习资源
     * 
     * @param dto 资源DTO
     * @param userId 用户ID
     * @param userName 用户名
     * @return 资源ID
     */
    Long uploadResource(StudyResourceDTO dto, Long userId, String userName) throws IOException;
    
    /**
     * 更新学习资源
     * 
     * @param dto 资源DTO
     * @return 是否成功
     */
    boolean updateResource(StudyResourceDTO dto) throws IOException;
    
    /**
     * 删除学习资源
     * 
     * @param id 资源ID
     * @return 是否成功
     */
    boolean deleteResource(Long id);
    
    /**
     * 批量删除学习资源
     * 
     * @param ids 资源ID数组
     * @return 成功删除的数量
     */
    int batchDeleteResource(Long[] ids);
    
    /**
     * 获取资源下载URL
     * 
     * @param id 资源ID
     * @return 下载URL
     */
    String getDownloadUrl(Long id);
    
    /**
     * 记录资源下载
     * 
     * @param id 资源ID
     * @return 是否成功
     */
    boolean recordDownload(Long id);
    
    /**
     * 获取资源统计数据
     * 
     * @param orgId 组织ID
     * @return 统计数据
     */
    Map<String, Object> getResourceStats(Integer orgId);
    
    /**
     * 获取资源列表
     *
     * @param page 分页对象
     * @param title 资源名称
     * @param categoryId 资源分类ID
     * @param format 文件格式
     * @param organizationId 组织ID
     * @param keyword 关键词
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param currentUserId 当前用户ID
     * @return 分页结果
     */
    IPage<StudyResource> getResourceList(
            Page<StudyResource> page,
            String title,
            Integer categoryId,
            String format,
            Integer organizationId,
            String keyword,
            String startDate,
            String endDate,
            Integer currentUserId
    );
    
    /**
     * 获取资源详情
     *
     * @param id 资源ID
     * @param currentUserId 当前用户ID
     * @return 资源详情
     */
    StudyResource getResourceDetail(Integer id, Integer currentUserId);
    
    /**
     * 获取资源下载信息
     *
     * @param id 资源ID
     * @param currentUserId 当前用户ID
     * @return 下载信息
     * @throws IOException IO异常
     */
    Map<String, Object> getResourceForDownload(Integer id, Integer currentUserId) throws IOException;
    
    /**
     * 获取资源下载信息(简化版，不验证权限)
     *
     * @param id 资源ID
     * @return 下载信息
     * @throws IOException IO异常
     */
    Map<String, Object> getResourceForDownload(Integer id) throws IOException;
    
    /**
     * 增加下载次数
     *
     * @param id 资源ID
     * @return 操作结果
     */
    boolean incrementDownloadCount(Integer id);
    
    /**
     * 获取所有资源分类
     *
     * @return 分类列表
     */
    List<Map<String, Object>> getAllCategories();
    
    /**
     * 获取资源预览信息
     *
     * @param id 资源ID
     * @param currentUserId 当前用户ID
     * @return 预览信息
     */
    Map<String, Object> getResourcePreviewInfo(Integer id, Integer currentUserId);
    
    /**
     * 获取当前用户可访问的组织列表
     *
     * @param currentUserId 当前用户ID
     * @return 组织列表
     */
    List<Map<String, Object>> getAccessibleOrganizations(Integer currentUserId);
    
    /**
     * 批量删除资源
     *
     * @param ids 资源ID列表
     * @param currentUserId 当前用户ID
     * @return 删除结果
     */
    boolean batchDeleteResources(List<Integer> ids, Integer currentUserId);
    
    /**
     * 检查资源操作权限
     *
     * @param id 资源ID
     * @param currentUserId 当前用户ID
     * @return 权限信息
     */
    Map<String, Boolean> checkResourcePermissions(Integer id, Integer currentUserId);
    
    /**
     * 检查上传权限
     *
     * @param currentUserId 当前用户ID
     * @param organizationId 组织ID
     * @return 是否有权限
     */
    boolean hasUploadPermission(Integer currentUserId, Integer organizationId);
    
    /**
     * 检查更新权限
     *
     * @param currentUserId 当前用户ID
     * @param resourceId 资源ID
     * @return 是否有权限
     */
    boolean hasUpdatePermission(Integer currentUserId, Integer resourceId);
    
    /**
     * 检查删除权限
     *
     * @param currentUserId 当前用户ID
     * @param resourceId 资源ID
     * @return 是否有权限
     */
    boolean hasDeletePermission(Integer currentUserId, Integer resourceId);
} 