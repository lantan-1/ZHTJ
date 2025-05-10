package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.VolunteerService;
import com.zhtj.mapper.VolunteerServiceMapper;
import com.zhtj.service.VolunteerServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 志愿服务记录业务实现类
 */
@Service
public class VolunteerServiceServiceImpl implements VolunteerServiceService {
    
    @Autowired
    private VolunteerServiceMapper volunteerServiceMapper;
    
    @Override
    public IPage<VolunteerService> getVolunteerServicePage(
            Page<VolunteerService> page, 
            Integer userId, 
            Integer organizationId, 
            String serviceYear) {
        return volunteerServiceMapper.selectVolunteerServicePage(page, userId, organizationId, serviceYear);
    }
    
    @Override
    public VolunteerService getById(Integer id) {
        return volunteerServiceMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public Integer create(VolunteerService volunteerService) {
        // 设置默认值
        volunteerService.setVerificationStatus(0); // 默认待验证
        volunteerService.setCreateTime(LocalDateTime.now());
        volunteerService.setUpdateTime(LocalDateTime.now());
        
        volunteerServiceMapper.insert(volunteerService);
        return volunteerService.getId();
    }
    
    @Override
    @Transactional
    public boolean update(VolunteerService volunteerService) {
        volunteerService.setUpdateTime(LocalDateTime.now());
        return volunteerServiceMapper.updateById(volunteerService) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(Integer id) {
        return volunteerServiceMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional
    public boolean verify(Integer id, Integer verificationStatus, String verificationRemark, Integer verifierUserId) {
        VolunteerService volunteerService = volunteerServiceMapper.selectById(id);
        if (volunteerService == null) {
            return false;
        }
        
        volunteerService.setVerificationStatus(verificationStatus);
        volunteerService.setVerificationRemark(verificationRemark);
        volunteerService.setVerifierUserId(verifierUserId);
        volunteerService.setVerificationTime(LocalDateTime.now());
        volunteerService.setUpdateTime(LocalDateTime.now());
        
        return volunteerServiceMapper.updateById(volunteerService) > 0;
    }
    
    @Override
    public Double getTotalServiceHoursByUserId(Integer userId) {
        return volunteerServiceMapper.selectTotalServiceHoursByUserId(userId);
    }
    
    @Override
    public Double getTotalServiceHoursByOrganizationId(Integer organizationId) {
        return volunteerServiceMapper.selectTotalServiceHoursByOrganizationId(organizationId);
    }
} 