package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhtj.model.twosystem.MemberEvaluation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 团员教育评议活动Mapper接口
 */
@Mapper
public interface MemberEvaluationMapper extends BaseMapper<MemberEvaluation> {
    
    /**
     * 根据ID查询评议活动详情，包括组织名称和发起人信息
     * 
     * @param id 评议活动ID
     * @return 评议活动详情
     */
    @Select("SELECT e.*, o.name as organization_name " +
            "FROM member_evaluation e " +
            "LEFT JOIN organization o ON e.organization_id = o.id " +
            "WHERE e.id = #{id}")
    MemberEvaluation selectMemberEvaluationDetail(@Param("id") Integer id);
} 