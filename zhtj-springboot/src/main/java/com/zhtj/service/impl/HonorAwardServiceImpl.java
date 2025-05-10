package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorAward;
import com.zhtj.mapper.HonorAwardMapper;
import com.zhtj.service.HonorAwardService;
import com.zhtj.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 荣誉授予记录业务实现类
 */
@Service
public class HonorAwardServiceImpl implements HonorAwardService {
    
    @Autowired
    private HonorAwardMapper honorAwardMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    public IPage<HonorAward> getHonorAwardPage(
            Page<HonorAward> page, 
            Integer userId, 
            Integer organizationId, 
            String awardYear, 
            Integer honorTypeId) {
        return honorAwardMapper.selectHonorAwardPage(page, userId, organizationId, awardYear, honorTypeId);
    }
    
    @Override
    public HonorAward getById(Integer id) {
        return honorAwardMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public Integer create(HonorAward honorAward) {
        honorAward.setCreateTime(LocalDateTime.now());
        honorAward.setUpdateTime(LocalDateTime.now());
        
        honorAwardMapper.insert(honorAward);
        
        // 发送通知给获奖用户
        sendNotificationToAwardUser(honorAward);
        
        return honorAward.getId();
    }
    
    @Override
    @Transactional
    public boolean update(HonorAward honorAward) {
        honorAward.setUpdateTime(LocalDateTime.now());
        return honorAwardMapper.updateById(honorAward) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(Integer id) {
        return honorAwardMapper.deleteById(id) > 0;
    }
    
    @Override
    public HonorAward getByApplicationId(Integer honorApplicationId) {
        LambdaQueryWrapper<HonorAward> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HonorAward::getHonorApplicationId, honorApplicationId);
        return honorAwardMapper.selectOne(queryWrapper);
    }
    
    @Override
    public IPage<HonorAward> getByUserId(Page<HonorAward> page, Integer userId) {
        LambdaQueryWrapper<HonorAward> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HonorAward::getUserId, userId);
        queryWrapper.orderByDesc(HonorAward::getAwardDate);
        return honorAwardMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public IPage<HonorAward> getByOrganizationId(Page<HonorAward> page, Integer organizationId) {
        LambdaQueryWrapper<HonorAward> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HonorAward::getOrganizationId, organizationId);
        queryWrapper.orderByDesc(HonorAward::getAwardDate);
        return honorAwardMapper.selectPage(page, queryWrapper);
    }
    
    /**
     * 向获奖用户发送通知
     */
    private void sendNotificationToAwardUser(HonorAward honorAward) {
        notificationService.sendSystemNotification(
                "恭喜您获得荣誉",
                "您被授予了\"" + honorAward.getHonorName() + "\"荣誉，荣誉编号：" + honorAward.getHonorCode() + "，请查看详情。",
                honorAward.getUserId(),
                "荣誉通知",
                honorAward.getId(),
                "honor_award",
                "/honor/award/" + honorAward.getId()
        );
    }
} 