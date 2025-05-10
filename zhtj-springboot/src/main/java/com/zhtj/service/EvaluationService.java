package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.Evaluation;

import java.util.List;

/**
 * 团员评议服务接口
 */
public interface EvaluationService extends IService<Evaluation> {
    
    /**
     * 分页查询评议活动列表
     * 
     * @param page 分页参数
     * @param year 年度
     * @param organizationId 组织ID
     * @param status 状态
     * @return 评议活动列表
     */
    IPage<Evaluation> getEvaluationPage(Page<Evaluation> page, String year, Integer organizationId, String status);
    
    /**
     * 根据ID查询评议活动详情
     * 
     * @param id 评议活动ID
     * @return 评议活动详情
     */
    Evaluation getEvaluationDetail(Integer id);
    
    /**
     * 创建评议活动
     * 
     * @param evaluation 评议活动信息
     * @return 是否成功
     */
    boolean createEvaluation(Evaluation evaluation);
    
    /**
     * 更新评议活动
     * 
     * @param evaluation 评议活动信息
     * @return 是否成功
     */
    boolean updateEvaluation(Evaluation evaluation);
    
    /**
     * 删除评议活动
     * 
     * @param id 评议活动ID
     * @return 是否成功
     */
    boolean deleteEvaluation(Integer id);
    
    /**
     * 启动评议活动
     * 
     * @param id 评议活动ID
     * @return 是否成功
     */
    boolean startEvaluation(Integer id);
    
    /**
     * 完成评议活动
     * 
     * @param id 评议活动ID
     * @return 是否成功
     */
    boolean completeEvaluation(Integer id);
    
    /**
     * 取消评议活动
     * 
     * @param id 评议活动ID
     * @return 是否成功
     */
    boolean cancelEvaluation(Integer id);
    
    /**
     * 获取评议活动列表（不分页）
     * 
     * @param organizationId 组织ID
     * @param year 年度
     * @param status 状态
     * @param keyword 关键词
     * @return 评议活动列表
     */
    List<Evaluation> getEvaluationList(Integer organizationId, String year, String status, String keyword);
} 