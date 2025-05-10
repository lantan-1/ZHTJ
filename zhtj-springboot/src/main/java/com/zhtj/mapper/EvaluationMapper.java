package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.Evaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 团员评议活动Mapper接口
 */
@Mapper
public interface EvaluationMapper extends BaseMapper<Evaluation> {
    
    /**
     * 分页查询评议活动列表
     * 
     * @param page 分页参数
     * @param year 年度
     * @param organizationId 组织ID
     * @param status 状态
     * @return 评议活动列表
     */
    @Select("<script>" +
            "SELECT e.*, o.name as organization_name " +
            "FROM member_evaluation e " +
            "LEFT JOIN organization o ON e.organization_id = o.id " +
            "WHERE 1=1 " +
            "<if test='year != null and year != \"\"'> AND e.evaluation_year = #{year} </if> " +
            "<if test='organizationId != null'> AND e.organization_id = #{organizationId} </if> " +
            "<if test='status != null and status != \"\"'> AND e.status = #{status} </if> " +
            "ORDER BY e.create_time DESC" +
            "</script>")
    IPage<Evaluation> selectEvaluationPage(Page<Evaluation> page, 
                                         @Param("year") String year,
                                         @Param("organizationId") Integer organizationId,
                                         @Param("status") String status);
    
    /**
     * 根据ID查询评议活动详情
     * 
     * @param id 评议活动ID
     * @return 评议活动详情
     */
    @Select("SELECT e.*, o.name as organization_name " +
            "FROM member_evaluation e " +
            "LEFT JOIN organization o ON e.organization_id = o.id " +
            "WHERE e.id = #{id}")
    Evaluation selectEvaluationDetail(@Param("id") Integer id);
} 