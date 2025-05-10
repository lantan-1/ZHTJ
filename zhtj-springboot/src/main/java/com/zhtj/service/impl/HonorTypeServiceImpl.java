package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorType;
import com.zhtj.mapper.HonorTypeMapper;
import com.zhtj.service.HonorTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 荣誉类型业务实现类
 */
@Service
public class HonorTypeServiceImpl implements HonorTypeService {
    
    @Autowired
    private HonorTypeMapper honorTypeMapper;
    
    @Override
    public IPage<HonorType> getHonorTypePage(Page<HonorType> page, String category) {
        return honorTypeMapper.selectHonorTypePage(page, category);
    }
    
    @Override
    public HonorType getById(Integer id) {
        return honorTypeMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public Integer create(HonorType honorType) {
        // 设置默认值
        honorType.setStatus(1); // 默认启用
        honorType.setCreateTime(LocalDateTime.now());
        honorType.setUpdateTime(LocalDateTime.now());
        
        honorTypeMapper.insert(honorType);
        return honorType.getId();
    }
    
    @Override
    @Transactional
    public boolean update(HonorType honorType) {
        honorType.setUpdateTime(LocalDateTime.now());
        return honorTypeMapper.updateById(honorType) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(Integer id) {
        return honorTypeMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional
    public boolean updateStatus(Integer id, Integer status) {
        HonorType honorType = honorTypeMapper.selectById(id);
        if (honorType == null) {
            return false;
        }
        
        honorType.setStatus(status);
        honorType.setUpdateTime(LocalDateTime.now());
        
        return honorTypeMapper.updateById(honorType) > 0;
    }
    
    @Override
    public List<HonorType> getAllEnabledTypes() {
        LambdaQueryWrapper<HonorType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HonorType::getStatus, 1);
        return honorTypeMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<HonorType> getTypesByCategory(String category) {
        LambdaQueryWrapper<HonorType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HonorType::getStatus, 1);
        queryWrapper.eq(StringUtils.hasText(category), HonorType::getHonorCategory, category);
        return honorTypeMapper.selectList(queryWrapper);
    }
} 