package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.StudyResource;
import com.zhtj.domain.vo.StudyResourceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 学习资源Mapper接口
 */
@Mapper
public interface StudyResourceMapper extends BaseMapper<StudyResource> {

    /**
     * 分页查询学习资源
     * 
     * @param page 分页参数
     * @param title 标题关键字
     * @param category 资源分类
     * @param orgId 组织ID
     * @return 分页结果
     */
    IPage<StudyResourceVO> selectResourcePage(Page<StudyResource> page, 
                                            @Param("title") String title,
                                            @Param("category") Integer category,
                                            @Param("orgId") Integer orgId);
    
    /**
     * 根据ID查询资源详情
     * 
     * @param id 资源ID
     * @return 资源视图对象
     */
    StudyResourceVO selectResourceById(@Param("id") Long id);
    
    /**
     * 增加下载次数
     * 
     * @param id 资源ID
     * @return 影响行数
     */
    int increaseDownloadCount(@Param("id") Long id);
    
    /**
     * 根据用户的组织权限查询学习资源
     * 
     * @param page 分页参数
     * @param title 资源标题关键字
     * @param categoryId 资源分类ID
     * @param format 文件格式
     * @param organizationId 指定查询特定组织的资源，如果为null则查询所有有权限的组织
     * @param keyword 关键词
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param userOrgId 用户所属组织ID
     * @return 分页结果
     */
    @Select("<script>SELECT sr.* FROM study_resource sr " +
            "WHERE 1=1 " +
            "<if test='title != null and title != \"\"'>AND sr.title LIKE CONCAT('%', #{title}, '%') </if> " +
            "<if test='categoryId != null'>AND sr.category_id = #{categoryId} </if> " +
            "<if test='format != null and format != \"\"'>AND sr.format = #{format} </if> " +
            "<if test='keyword != null and keyword != \"\"'>AND (sr.title LIKE CONCAT('%', #{keyword}, '%') OR sr.description LIKE CONCAT('%', #{keyword}, '%')) </if> " +
            "<if test='startDate != null and startDate != \"\"'>AND sr.create_time &gt;= #{startDate} </if> " +
            "<if test='endDate != null and endDate != \"\"'>AND sr.create_time &lt;= #{endDate} </if> " +
            "<if test='organizationId != null'>AND sr.organization_id = #{organizationId} </if> " +
            "<if test='organizationId == null and userOrgId != null'>" +
            "AND (sr.organization_id = #{userOrgId} " +
            "  OR EXISTS (SELECT 1 FROM organization_hierarchy oh1 WHERE oh1.organization_id = #{userOrgId} AND oh1.parent_organization_id = sr.organization_id) " +
            "  OR EXISTS (SELECT 1 FROM organization_hierarchy oh2 WHERE oh2.parent_organization_id = #{userOrgId} AND oh2.organization_id = sr.organization_id))" +
            "</if> " +
            "ORDER BY sr.create_time DESC" +
            "</script>")
    IPage<StudyResource> selectResourcesWithOrgPermission(
            Page<StudyResource> page, 
            @Param("title") String title,
            @Param("categoryId") Integer categoryId,
            @Param("format") String format,
            @Param("organizationId") Integer organizationId,
            @Param("keyword") String keyword,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("userOrgId") Integer userOrgId);
    
    /**
     * 检查用户是否有权限访问指定资源
     * 
     * @param resourceId 资源ID
     * @param userOrgId 用户所属组织ID
     * @return 返回1表示有权限，0表示无权限
     */
    @Select("SELECT COUNT(*) FROM study_resource sr " +
            "WHERE sr.id = #{resourceId} " +
            "AND (sr.organization_id = #{userOrgId} " +
            "  OR EXISTS (SELECT 1 FROM organization_hierarchy oh1 WHERE oh1.organization_id = #{userOrgId} AND oh1.parent_organization_id = sr.organization_id) " +
            "  OR EXISTS (SELECT 1 FROM organization_hierarchy oh2 WHERE oh2.parent_organization_id = #{userOrgId} AND oh2.organization_id = sr.organization_id))")
    int checkResourceAccess(@Param("resourceId") Integer resourceId, @Param("userOrgId") Integer userOrgId);
} 