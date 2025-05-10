package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.common.exception.ResourceNotFoundException;
import com.zhtj.domain.Evaluation;
import com.zhtj.domain.EvaluationDetail;
import com.zhtj.mapper.EvaluationDetailMapper;
import com.zhtj.service.EvaluationDetailService;
import com.zhtj.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 团员评议详情服务实现类
 */
@Service
public class EvaluationDetailServiceImpl extends ServiceImpl<EvaluationDetailMapper, EvaluationDetail> implements EvaluationDetailService {

    @Autowired
    private EvaluationDetailMapper evaluationDetailMapper;
    
    // 移除直接依赖，使用ApplicationContext实现延迟加载
    @Autowired
    private ApplicationContext applicationContext;
    
    // 获取EvaluationService的方法，避免直接依赖
    private EvaluationService getEvaluationService() {
        return applicationContext.getBean(EvaluationService.class);
    }
    
    @Override
    public IPage<EvaluationDetail> getEvaluationDetailPage(Page<EvaluationDetail> page, Integer evaluationId, String status) {
        if (evaluationId == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        
        return evaluationDetailMapper.selectEvaluationDetailPage(page, evaluationId, status);
    }
    
    @Override
    public EvaluationDetail getEvaluationDetailById(Integer id) {
        if (id == null) {
            throw new BusinessException("评议详情ID不能为空");
        }
        
        EvaluationDetail detail = evaluationDetailMapper.selectEvaluationDetailById(id);
        if (detail == null) {
            throw new ResourceNotFoundException("评议详情", "id", id);
        }
        
        return detail;
    }
    
    @Override
    public EvaluationDetail getByEvaluationAndUser(Integer evaluationId, Integer userId) {
        if (evaluationId == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        
        return evaluationDetailMapper.selectByEvaluationAndUser(evaluationId, userId);
    }
    
    @Override
    @Transactional
    public boolean submitEvaluationResult(EvaluationDetail evaluationDetail) {
        // 参数验证
        if (evaluationDetail.getEvaluationId() == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        if (evaluationDetail.getUserId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (evaluationDetail.getResult() == null || evaluationDetail.getResult().isEmpty()) {
            throw new BusinessException("评议结果不能为空");
        }
        
        // 检查评议活动状态 - 使用延迟加载的方式获取EvaluationService
        Evaluation evaluation = getEvaluationService().getById(evaluationDetail.getEvaluationId());
        if (evaluation == null) {
            throw new ResourceNotFoundException("评议活动", "id", evaluationDetail.getEvaluationId());
        }
        
        // 只有进行中状态可以提交评议
        if (!"进行中".equals(evaluation.getStatus())) {
            throw new BusinessException("只有进行中状态的评议活动可以提交评议");
        }
        
        // 检查是否已有评议记录
        EvaluationDetail existingDetail = this.getByEvaluationAndUser(
                evaluationDetail.getEvaluationId(), evaluationDetail.getUserId());
        
        if (existingDetail != null) {
            // 更新现有记录
            existingDetail.setResult(evaluationDetail.getResult());
            existingDetail.setScore(evaluationDetail.getScore());
            existingDetail.setComment(evaluationDetail.getComment());
            existingDetail.setStatus("已评议");
            existingDetail.setEvaluateTime(LocalDateTime.now());
            existingDetail.setUpdateTime(LocalDateTime.now());
            
            return this.updateById(existingDetail);
        } else {
            // 创建新记录
            evaluationDetail.setStatus("已评议");
            evaluationDetail.setEvaluateTime(LocalDateTime.now());
            evaluationDetail.setCreateTime(LocalDateTime.now());
            evaluationDetail.setUpdateTime(LocalDateTime.now());
            
            return this.save(evaluationDetail);
        }
    }
    
    @Override
    @Transactional
    public boolean addEvaluationUsers(Integer evaluationId, Integer[] userIds) {
        // 参数验证
        if (evaluationId == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        if (userIds == null || userIds.length == 0) {
            throw new BusinessException("用户ID列表不能为空");
        }
        
        // 检查评议活动 - 使用延迟加载的方式获取EvaluationService
        Evaluation evaluation = getEvaluationService().getById(evaluationId);
        if (evaluation == null) {
            throw new ResourceNotFoundException("评议活动", "id", evaluationId);
        }
        
        // 只有草稿状态可以添加评议对象
        if (!"草稿".equals(evaluation.getStatus())) {
            throw new BusinessException("只有草稿状态的评议活动可以添加评议对象");
        }
        
        // 批量插入评议对象
        List<EvaluationDetail> detailList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Integer userId : userIds) {
            // 检查是否已存在
            LambdaQueryWrapper<EvaluationDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(EvaluationDetail::getEvaluationId, evaluationId)
                       .eq(EvaluationDetail::getUserId, userId);
            
            if (this.count(queryWrapper) == 0) {
                EvaluationDetail detail = new EvaluationDetail();
                detail.setEvaluationId(evaluationId);
                detail.setUserId(userId);
                detail.setStatus("待评议");
                detail.setCreateTime(now);
                detail.setUpdateTime(now);
                
                detailList.add(detail);
            }
        }
        
        if (!detailList.isEmpty()) {
            return this.saveBatch(detailList);
        }
        
        return true;
    }
} 