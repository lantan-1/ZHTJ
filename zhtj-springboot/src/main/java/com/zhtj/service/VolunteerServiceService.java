package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.VolunteerService;

/**
 * 志愿服务记录业务接口
 */
public interface VolunteerServiceService {
    
    /**
     * 分页查询志愿服务记录
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @param serviceYear 服务年度
     * @return 分页结果
     */
    IPage<VolunteerService> getVolunteerServicePage(
        Page<VolunteerService> page,
        Integer userId,
        Integer organizationId,
        String serviceYear
    );
    
    /**
     * 根据ID获取志愿服务记录
     *
     * @param id 记录ID
     * @return 志愿服务记录
     */
    VolunteerService getById(Integer id);
    
    /**
     * 创建志愿服务记录
     *
     * @param volunteerService 志愿服务记录对象
     * @return 创建是否成功
     */
    Integer create(VolunteerService volunteerService);
    
    /**
     * 更新志愿服务记录
     *
     * @param volunteerService 志愿服务记录对象
     * @return 更新是否成功
     */
    boolean update(VolunteerService volunteerService);
    
    /**
     * 删除志愿服务记录
     *
     * @param id 记录ID
     * @return 删除是否成功
     */
    boolean delete(Integer id);
    
    /**
     * 验证志愿服务记录
     *
     * @param id 记录ID
     * @param verificationStatus 验证状态
     * @param verificationRemark 验证备注
     * @param verifierUserId 验证人ID
     * @return 验证是否成功
     */
    boolean verify(Integer id, Integer verificationStatus, String verificationRemark, Integer verifierUserId);
    
    /**
     * 获取用户志愿服务总时长
     *
     * @param userId 用户ID
     * @return 总时长
     */
    Double getTotalServiceHoursByUserId(Integer userId);
    
    /**
     * 获取组织志愿服务总时长
     *
     * @param organizationId 组织ID
     * @return 总时长
     */
    Double getTotalServiceHoursByOrganizationId(Integer organizationId);
} 