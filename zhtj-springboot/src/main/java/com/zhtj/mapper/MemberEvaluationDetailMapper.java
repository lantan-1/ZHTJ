package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.model.twosystem.MemberEvaluationDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 团员评议详情Mapper接口
 */
@Mapper
public interface MemberEvaluationDetailMapper extends BaseMapper<MemberEvaluationDetail> {
    
    /**
     * 分页查询评议结果列表
     * 
     * @param page 分页参数
     * @param evaluationId 评议活动ID
     * @param status 评议状态
     * @param memberName 团员姓名
     * @param result 评议结果
     * @return 评议结果分页列表
     */
    @Select("<script>" +
            "SELECT d.*, u.name as user_name, u.league_position as member_position, o.name as branch_name " +
            "FROM member_evaluation_detail d " +
            "LEFT JOIN user u ON d.user_id = u.id " +
            "LEFT JOIN organization o ON u.organization = o.id " +
            "WHERE d.evaluation_id = #{evaluationId} " +
            "<if test='status != null and status != \"\"'> AND d.status = #{status} </if> " +
            "<if test='memberName != null and memberName != \"\"'> AND u.name LIKE CONCAT('%', #{memberName}, '%') </if> " +
            "<if test='result != null and result != \"\"'> AND d.result = #{result} </if> " +
            "ORDER BY d.create_time DESC" +
            "</script>")
    IPage<MemberEvaluationDetail> selectResultList(Page<MemberEvaluationDetail> page, 
                                                @Param("evaluationId") Integer evaluationId,
                                                @Param("status") String status,
                                                @Param("memberName") String memberName,
                                                @Param("result") String result);
    
    /**
     * 获取评议详情列表（包含团员职务信息）
     * 
     * @param evaluationId 评议活动ID
     * @return 评议详情列表
     */
    @Select("SELECT d.*, u.name as user_name, u.league_position as member_position, o.name as branch_name " +
           "FROM member_evaluation_detail d " +
           "LEFT JOIN user u ON d.user_id = u.id " +
           "LEFT JOIN organization o ON u.organization = o.id " +
           "WHERE d.evaluation_id = #{evaluationId} " +
           "ORDER BY d.create_time DESC")
    List<MemberEvaluationDetail> getEvaluationDetailWithPosition(@Param("evaluationId") Integer evaluationId);
    
    /**
     * 统计评议结果总数
     * 
     * @param evaluationId 评议活动ID
     * @return 总数
     */
    @Select("SELECT COUNT(*) FROM member_evaluation_detail WHERE evaluation_id = #{evaluationId}")
    Integer countByEvaluationId(@Param("evaluationId") Integer evaluationId);
    
    /**
     * 统计已完成评议数量
     * 
     * @param evaluationId 评议活动ID
     * @return 已完成评议数量
     */
    @Select("SELECT COUNT(*) FROM member_evaluation_detail WHERE evaluation_id = #{evaluationId} AND status = '已评议'")
    Integer countCompletedByEvaluationId(@Param("evaluationId") Integer evaluationId);
    
    /**
     * 根据评议结果类型统计数量
     * 
     * @param evaluationId 评议活动ID
     * @param result 评议结果
     * @return 指定结果的数量
     */
    @Select("SELECT COUNT(*) FROM member_evaluation_detail WHERE evaluation_id = #{evaluationId} AND status = '已评议' AND result = #{result}")
    Integer countByResult(@Param("evaluationId") Integer evaluationId, @Param("result") String result);
    
    /**
     * 获取评议统计数据
     * 
     * @param evaluationId 评议活动ID
     * @return 评议统计数据
     */
    @Select("SELECT " +
            "COUNT(DISTINCT user_id) as totalCount, " +
            "SUM(CASE WHEN status = '已评议' THEN 1 ELSE 0 END) as completedCount, " +
            "SUM(CASE WHEN result = '优秀' THEN 1 ELSE 0 END) as excellentCount, " +
            "SUM(CASE WHEN result = '合格' THEN 1 ELSE 0 END) as qualifiedCount, " +
            "SUM(CASE WHEN result = '基本合格' THEN 1 ELSE 0 END) as basicQualifiedCount, " +
            "SUM(CASE WHEN result = '不合格' THEN 1 ELSE 0 END) as unqualifiedCount " +
            "FROM member_evaluation_detail " +
            "WHERE evaluation_id = #{evaluationId}")
    Map<String, Object> getEvaluationStatistics(@Param("evaluationId") Integer evaluationId);
    
    /**
     * 查询需要发送提醒的成员
     * 
     * @param evaluationId 评议活动ID
     * @return 需要发送提醒的成员列表
     */
    @Select("SELECT d.user_id, u.name, u.phone, u.email " +
            "FROM member_evaluation_detail d " +
            "JOIN user u ON d.user_id = u.id " +
            "WHERE d.evaluation_id = #{evaluationId} AND (d.status IS NULL OR d.status != '已评议')")
    List<Map<String, Object>> findMembersForReminder(@Param("evaluationId") Integer evaluationId);
} 