package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.common.exception.ResourceNotFoundException;
import com.zhtj.domain.Evaluation;
import com.zhtj.domain.EvaluationDetail;
import com.zhtj.mapper.EvaluationMapper;
import com.zhtj.service.EvaluationDetailService;
import com.zhtj.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 团员教育评议服务实现
 */
@Service
public class EvaluationServiceImpl extends ServiceImpl<EvaluationMapper, Evaluation> implements EvaluationService {

    @Autowired
    private EvaluationMapper evaluationMapper;
    
    @Autowired
    private EvaluationDetailService evaluationDetailService;
    
    @Override
    public IPage<Evaluation> getEvaluationPage(Page<Evaluation> page, String year, Integer organizationId, String status) {
        return evaluationMapper.selectEvaluationPage(page, year, organizationId, status);
    }
    
    @Override
    public Evaluation getEvaluationDetail(Integer id) {
        if (id == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        
        Evaluation evaluation = evaluationMapper.selectEvaluationDetail(id);
        if (evaluation == null) {
            throw new ResourceNotFoundException("评议活动", "id", id);
        }
        
        return evaluation;
    }
    
    @Override
    @Transactional
    public boolean createEvaluation(Evaluation evaluation) {
        // 参数验证
        if (evaluation.getOrganizationId() == null) {
            throw new BusinessException("组织ID不能为空");
        }
        if (evaluation.getEvaluationYear() == null || evaluation.getEvaluationYear().isEmpty()) {
            throw new BusinessException("评议年度不能为空");
        }
        if (evaluation.getTitle() == null || evaluation.getTitle().isEmpty()) {
            throw new BusinessException("评议主题不能为空");
        }
        
        // 设置初始状态和时间
        evaluation.setStatus("草稿");
        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());
        
        return this.save(evaluation);
    }
    
    @Override
    @Transactional
    public boolean updateEvaluation(Evaluation evaluation) {
        // 参数验证
        if (evaluation.getId() == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        
        // 检查评议活动是否存在
        Evaluation existingEvaluation = this.getById(evaluation.getId());
        if (existingEvaluation == null) {
            throw new ResourceNotFoundException("评议活动", "id", evaluation.getId());
        }
        
        // 只有草稿状态可以更新
        if (!"草稿".equals(existingEvaluation.getStatus())) {
            throw new BusinessException("只有草稿状态的评议活动可以更新");
        }
        
        evaluation.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(evaluation);
    }
    
    @Override
    @Transactional
    public boolean deleteEvaluation(Integer id) {
        // 参数验证
        if (id == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        
        // 检查评议活动是否存在
        Evaluation evaluation = this.getById(id);
        if (evaluation == null) {
            throw new ResourceNotFoundException("评议活动", "id", id);
        }
        
        // 只有草稿状态可以删除
        if (!"草稿".equals(evaluation.getStatus())) {
            throw new BusinessException("只有草稿状态的评议活动可以删除");
        }
        
        // 删除关联的评议详情
        LambdaQueryWrapper<EvaluationDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EvaluationDetail::getEvaluationId, id);
        evaluationDetailService.remove(queryWrapper);
        
        // 删除评议活动
        return this.removeById(id);
    }
    
    @Override
    @Transactional
    public boolean startEvaluation(Integer id) {
        // 参数验证
        if (id == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        
        // 检查评议活动是否存在
        Evaluation evaluation = this.getById(id);
        if (evaluation == null) {
            throw new ResourceNotFoundException("评议活动", "id", id);
        }
        
        // 只有草稿状态可以启动
        if (!"草稿".equals(evaluation.getStatus())) {
            throw new BusinessException("只有草稿状态的评议活动可以启动");
        }
        
        // 检查是否有评议对象
        LambdaQueryWrapper<EvaluationDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EvaluationDetail::getEvaluationId, id);
        int count = (int) evaluationDetailService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException("评议活动没有评议对象，请先添加评议对象");
        }
        
        // 更新状态
        evaluation.setStatus("进行中");
        evaluation.setStartTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(evaluation);
    }
    
    @Override
    @Transactional
    public boolean completeEvaluation(Integer id) {
        // 参数验证
        if (id == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        
        // 检查评议活动是否存在
        Evaluation evaluation = this.getById(id);
        if (evaluation == null) {
            throw new ResourceNotFoundException("评议活动", "id", id);
        }
        
        // 只有进行中状态可以完成
        if (!"进行中".equals(evaluation.getStatus())) {
            throw new BusinessException("只有进行中状态的评议活动可以完成");
        }
        
        // 更新状态
        evaluation.setStatus("已完成");
        evaluation.setEndTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(evaluation);
    }
    
    @Override
    @Transactional
    public boolean cancelEvaluation(Integer id) {
        // 参数验证
        if (id == null) {
            throw new BusinessException("评议活动ID不能为空");
        }
        
        // 检查评议活动是否存在
        Evaluation evaluation = this.getById(id);
        if (evaluation == null) {
            throw new ResourceNotFoundException("评议活动", "id", id);
        }
        
        // 已完成状态不可取消
        if ("已完成".equals(evaluation.getStatus())) {
            throw new BusinessException("已完成状态的评议活动不可取消");
        }
        
        // 更新状态
        evaluation.setStatus("已取消");
        evaluation.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(evaluation);
    }

    @Override
    public List<Evaluation> getEvaluationList(Integer organizationId, String year, String status, String keyword) {
        LambdaQueryWrapper<Evaluation> queryWrapper = new LambdaQueryWrapper<>();
        
        // 条件筛选
        if (organizationId != null) {
            queryWrapper.eq(Evaluation::getOrganizationId, organizationId);
        }
        
        if (StringUtils.hasText(year)) {
            queryWrapper.eq(Evaluation::getEvaluationYear, year);
        }
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Evaluation::getStatus, status);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Evaluation::getTitle, keyword);
        }
        
        // 默认按创建时间降序排序
        queryWrapper.orderByDesc(Evaluation::getStartTime);
        
        return list(queryWrapper);
    }
} 