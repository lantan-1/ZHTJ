package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 组织Mapper接口
 */
@Mapper
public interface OrganizationMapper extends BaseMapper<Organization> {
    /**
     * 根据组织名称查询组织详情
     * @param name 组织名称
     * @return 组织详情
     */
    @Select("SELECT * FROM organization WHERE name = #{name}")
    Organization getByName(@Param("name") String name);
    
    /**
     * 获取所有根组织（没有父组织的组织）
     * @return 根组织列表
     */
    @Select("SELECT * FROM organization WHERE parent_id IS NULL ORDER BY sort_order")
    List<Organization> getRootOrganizations();
    
    /**
     * 根据父组织ID获取其直接子组织
     * @param parentId 父组织ID
     * @return 子组织列表
     */
    @Select("SELECT * FROM organization WHERE parent_id = #{parentId} ORDER BY sort_order")
    List<Organization> getChildrenByParentId(@Param("parentId") Integer parentId);
    
    /**
     * 更新组织的团员数量
     * @param organizationId 组织ID
     * @param count 团员数量
     * @return 影响行数
     */
    @Update("UPDATE organization SET member_count = #{count} WHERE id = #{organizationId}")
    int updateMemberCount(@Param("organizationId") Integer organizationId, @Param("count") Integer count);
    
    /**
     * 分页查询组织列表
     * 
     * @param page 分页参数
     * @param name 组织名称
     * @param parentId 上级组织ID
     * @return 组织列表
     */
    @Select("<script>" +
            "SELECT o.*, p.name as parent_name " +
            "FROM organization o " +
            "LEFT JOIN organization p ON o.parent_id = p.id " +
            "WHERE 1=1 " +
            "<if test='name != null and name != \"\"'> AND o.name LIKE CONCAT('%', #{name}, '%') </if> " +
            "<if test='parentId != null'> AND o.parent_id = #{parentId} </if> " +
            "ORDER BY o.id ASC" +
            "</script>")
    IPage<Organization> selectOrganizationPage(Page<Organization> page, 
                                             @Param("name") String name,
                                             @Param("parentId") Integer parentId);
    
    /**
     * 查询组织的子组织列表
     * 
     * @param parentId 父组织ID
     * @return 子组织列表
     */
    @Select("SELECT o.*, p.name as parent_name " +
            "FROM organization o " +
            "LEFT JOIN organization p ON o.parent_id = p.id " +
            "WHERE o.parent_id = #{parentId} " +
            "ORDER BY o.id ASC")
    List<Organization> selectChildrenByParentId(@Param("parentId") Integer parentId);
    
    /**
     * 根据ID查询组织详情
     * 
     * @param id 组织ID
     * @return 组织详情
     */
    @Select("SELECT o.*, p.name as parent_name " +
            "FROM organization o " +
            "LEFT JOIN organization p ON o.parent_id = p.id " +
            "WHERE o.id = #{id}")
    Organization selectOrganizationDetail(@Param("id") Integer id);
    
    /**
     * 查询所有顶级组织
     * 
     * @return 顶级组织列表
     */
    @Select("SELECT * FROM organization WHERE parent_id IS NULL OR parent_id = 0")
    List<Organization> selectRootOrganizations();
    
    /**
     * 统计组织下的用户数量
     * 
     * @param organizationId 组织ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM user WHERE organization = #{organizationId}")
    int countUsersByOrganizationId(@Param("organizationId") Integer organizationId);
    
    /**
     * 通过路径获取组织的所有子组织
     * @param path 组织路径
     * @return 子组织列表
     */
    @Select("SELECT * FROM organization WHERE path LIKE CONCAT(#{path}, ',%') ORDER BY level, sort_order")
    List<Organization> getAllChildrenByPath(@Param("path") String path);
    
    /**
     * 获取组织及其所有子组织的ID列表
     * @param organizationId 组织ID
     * @return 组织及其子组织ID列表
     */
    @Select("WITH RECURSIVE org_tree AS (" +
            "  SELECT id FROM organization WHERE id = #{organizationId} " +
            "  UNION ALL " +
            "  SELECT o.id FROM organization o " +
            "  JOIN org_tree ot ON o.parent_id = ot.id " +
            ") " +
            "SELECT id FROM org_tree")
    List<Integer> getSubOrganizationIds(@Param("organizationId") Integer organizationId);
    
    /**
     * 从组织路径中提取组织ID列表
     * @param path 组织路径（格式如：1,2,3）
     * @return 组织ID列表
     */
    @Select("SELECT CAST(value AS SIGNED) AS id " +
            "FROM STRING_SPLIT(#{path}, ',') " +
            "WHERE value <> ''")
    List<Integer> getOrganizationIdsFromPath(@Param("path") String path);
    
    /**
     * 获取祖先组织ID列表（包括自身）
     * @param organizationId 组织ID
     * @return 组织及其祖先组织ID列表
     */
    @Select("SELECT CAST(value AS SIGNED) AS id " +
            "FROM STRING_SPLIT((SELECT path FROM organization WHERE id = #{organizationId}), ',') " +
            "WHERE value <> ''")
    List<Integer> getAncestorOrganizationIds(@Param("organizationId") Integer organizationId);
} 