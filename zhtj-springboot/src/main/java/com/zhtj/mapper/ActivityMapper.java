package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {
    /**
     * 根据组织ID和日期范围分页查询活动列表
     * @param page 分页对象
     * @param organizationId 组织ID
     * @param category 活动类别
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 活动列表
     */
    @Select("<script>" +
            "SELECT a.*, o.name as organization_name FROM activity a " +
            "LEFT JOIN organization o ON a.organization = o.id " +
            "WHERE 1=1 " +
            "<if test='organizationId != null'> AND a.organization = #{organizationId} </if> " +
            "<if test='category != null and category != \"\"'> AND a.category = #{category} </if> " +
            "<if test='startDate != null and startDate != \"\"'> AND a.date &gt;= #{startDate} </if> " +
            "<if test='endDate != null and endDate != \"\"'> AND a.date &lt;= #{endDate} </if> " +
            "ORDER BY a.date DESC" +
            "</script>")
    IPage<Activity> selectActivityPage(Page<Activity> page, 
                                      @Param("organizationId") Integer organizationId,
                                      @Param("category") String category,
                                      @Param("startDate") String startDate,
                                      @Param("endDate") String endDate);
    
    /**
     * 根据活动ID查询活动详情，包含组织名称
     * @param id 活动ID
     * @return 活动详情
     */
    @Select("SELECT a.*, o.name as organization_name FROM activity a " +
            "LEFT JOIN organization o ON a.organization = o.id " +
            "WHERE a.id = #{id}")
    Activity selectActivityDetail(@Param("id") Integer id);
    
    /**
     * 统计组织活动数量
     * @param organizationId 组织ID
     * @return 活动数量
     */
    @Select("SELECT COUNT(*) FROM activity WHERE organization = #{organizationId}")
    int countByOrganization(@Param("organizationId") Integer organizationId);
    
    /**
     * 按类别统计活动数量
     * @param organizationId 组织ID
     * @return 类别和数量的列表
     */
    @Select("SELECT category, COUNT(*) as count FROM activity " +
            "WHERE organization = #{organizationId} " +
            "GROUP BY category")
    List<Object> countByCategory(@Param("organizationId") Integer organizationId);
} 