package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.EvaluationDetail;

/**
 * 团员评议详情服务接口
 */
public interface EvaluationDetailService extends IService<EvaluationDetail> {
    
    /**
     * 分页查询评议详情列表
     * 
     * @param page 分页参数
     * @param evaluationId 评议活动ID
     * @param status 状态
     * @return 评议详情列表
     */
    IPage<EvaluationDetail> getEvaluationDetailPage(Page<EvaluationDetail> page, Integer evaluationId, String status);
    
    /**
     * 根据ID查询评议详情
     * 
     * @param id 评议详情ID
     * @return 评议详情
     */
    EvaluationDetail getEvaluationDetailById(Integer id);
    
    /**
     * 根据评议活动ID和用户ID查询评议详情
     * 
     * @param evaluationId 评议活动ID
     * @param userId 用户ID
     * @return 评议详情
     */
    EvaluationDetail getByEvaluationAndUser(Integer evaluationId, Integer userId);
    
    /**
     * 提交评议结果
     * 
     * @param evaluationDetail 评议详情
     * @return 是否成功
     */
    boolean submitEvaluationResult(EvaluationDetail evaluationDetail);
    
    /**
     * 批量添加评议用户
     * 
     * @param evaluationId 评议活动ID
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    boolean addEvaluationUsers(Integer evaluationId, Integer[] userIds);
} 