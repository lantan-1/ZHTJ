package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.common.exception.ResourceNotFoundException;
import com.zhtj.domain.Activity;
import com.zhtj.mapper.ActivityMapper;
import com.zhtj.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional
    public boolean save(Activity activity) {
        return super.save(activity);
    }

    @Override
    @Transactional
    public boolean delete(Integer id) {
        if (id == null) {
            throw new BusinessException("活动ID不能为空");
        }
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean update(Activity activity) {
        if (activity.getId() == null) {
            throw new BusinessException("活动ID不能为空");
        }
        
        Activity existingActivity = this.getById(activity.getId());
        if (existingActivity == null) {
            throw new ResourceNotFoundException("活动", "id", activity.getId());
        }
        
        return this.updateById(activity);
    }

    @Override
    public List<Activity> getAllByOrg(Integer organization) {
        if (organization == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getOrganization, organization);
        return this.list(queryWrapper);
    }

    @Override
    public List<Activity> getAllByDate(Integer organization, String date) {
        if (organization == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        if (date == null || date.trim().isEmpty()) {
            throw new BusinessException("日期不能为空");
        }
        
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getOrganization, organization)
                   .eq(Activity::getDate, date);
        return this.list(queryWrapper);
    }
    
    @Override
    public IPage<Activity> getActivityPage(Page<Activity> page, Integer organizationId, String category, String startDate, String endDate) {
        return activityMapper.selectActivityPage(page, organizationId, category, startDate, endDate);
    }
    
    @Override
    public Activity getActivityDetail(Integer id) {
        if (id == null) {
            throw new BusinessException("活动ID不能为空");
        }
        
        Activity activity = activityMapper.selectActivityDetail(id);
        if (activity == null) {
            throw new ResourceNotFoundException("活动", "id", id);
        }
        
        return activity;
    }
    
    @Override
    public int countByOrganization(Integer organizationId) {
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        return activityMapper.countByOrganization(organizationId);
    }
    
    @Override
    public Map<String, Integer> countByCategory(Integer organizationId) {
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        // 获取所有活动
        LambdaQueryWrapper<Activity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Activity::getOrganization, organizationId)
                   .select(Activity::getCategory);
        List<Activity> activities = this.list(queryWrapper);
        
        // 统计各类别的数量
        Map<String, Integer> categoryCount = new HashMap<>();
        // 预设常见类别，确保即使没有数据也会返回这些类别
        String[] defaultCategories = {"支部大会", "团课", "主题团日", "入团仪式", "组织生活会", "其他"};
        for (String category : defaultCategories) {
            categoryCount.put(category, 0);
        }
        
        // 统计实际数据
        for (Activity activity : activities) {
            String category = activity.getCategory();
            if (category != null && !category.isEmpty()) {
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            }
        }
        
        return categoryCount;
    }
} 