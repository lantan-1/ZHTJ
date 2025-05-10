package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.EvaluationDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 团员评议详情Mapper接口
 */
@Mapper
public interface EvaluationDetailMapper extends BaseMapper<EvaluationDetail> {
    
    /**
     * 分页查询评议详情列表
     * 
     * @param page 分页参数
     * @param evaluationId 评议活动ID
     * @param status 状态
     * @return 评议详情列表
     */
    @Select("<script>" +
            "SELECT d.*, u.name as user_name, e.title as evaluation_title " +
            "FROM evaluation_detail d " +
            "LEFT JOIN user u ON d.user_id = u.id " +
            "LEFT JOIN evaluation e ON d.evaluation_id = e.id " +
            "WHERE d.evaluation_id = #{evaluationId} " +
            "<if test='status != null and status != \"\"'> AND d.status = #{status} </if> " +
            "ORDER BY d.create_time DESC" +
            "</script>")
    IPage<EvaluationDetail> selectEvaluationDetailPage(Page<EvaluationDetail> page, 
                                                     @Param("evaluationId") Integer evaluationId,
                                                     @Param("status") String status);
    
    /**
     * 根据ID查询评议详情
     * 
     * @param id 评议详情ID
     * @return 评议详情
     */
    @Select("SELECT d.*, u.name as user_name, e.title as evaluation_title " +
            "FROM evaluation_detail d " +
            "LEFT JOIN user u ON d.user_id = u.id " +
            "LEFT JOIN evaluation e ON d.evaluation_id = e.id " +
            "WHERE d.id = #{id}")
    EvaluationDetail selectEvaluationDetailById(@Param("id") Integer id);
    
    /**
     * 根据评议活动ID和用户ID查询评议详情
     * 
     * @param evaluationId 评议活动ID
     * @param userId 用户ID
     * @return 评议详情
     */
    @Select("SELECT d.*, u.name as user_name, e.title as evaluation_title " +
            "FROM evaluation_detail d " +
            "LEFT JOIN user u ON d.user_id = u.id " +
            "LEFT JOIN evaluation e ON d.evaluation_id = e.id " +
            "WHERE d.evaluation_id = #{evaluationId} AND d.user_id = #{userId}")
    EvaluationDetail selectByEvaluationAndUser(@Param("evaluationId") Integer evaluationId,
                                            @Param("userId") Integer userId);
} 