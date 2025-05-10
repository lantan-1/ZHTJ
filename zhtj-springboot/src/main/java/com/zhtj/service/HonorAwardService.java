package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorAward;

/**
 * 荣誉授予记录业务接口
 */
public interface HonorAwardService {
    
    /**
     * 分页查询荣誉授予记录
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @param awardYear 授予年度
     * @param honorTypeId 荣誉类型ID
     * @return 分页结果
     */
    IPage<HonorAward> getHonorAwardPage(
        Page<HonorAward> page,
        Integer userId,
        Integer organizationId,
        String awardYear,
        Integer honorTypeId
    );
    
    /**
     * 根据ID获取荣誉授予记录
     *
     * @param id 记录ID
     * @return 荣誉授予记录
     */
    HonorAward getById(Integer id);
    
    /**
     * 创建荣誉授予记录
     *
     * @param honorAward 荣誉授予记录对象
     * @return 新创建的记录ID
     */
    Integer create(HonorAward honorAward);
    
    /**
     * 更新荣誉授予记录
     *
     * @param honorAward 荣誉授予记录对象
     * @return 操作是否成功
     */
    boolean update(HonorAward honorAward);
    
    /**
     * 删除荣誉授予记录
     *
     * @param id 记录ID
     * @return 操作是否成功
     */
    boolean delete(Integer id);
    
    /**
     * 根据荣誉申请ID获取授予记录
     *
     * @param honorApplicationId 荣誉申请ID
     * @return 荣誉授予记录
     */
    HonorAward getByApplicationId(Integer honorApplicationId);
    
    /**
     * 根据用户ID获取所有荣誉授予记录
     *
     * @param userId 用户ID
     * @return 荣誉授予记录列表
     */
    IPage<HonorAward> getByUserId(Page<HonorAward> page, Integer userId);
    
    /**
     * 根据组织ID获取所有荣誉授予记录
     *
     * @param organizationId 组织ID
     * @return 荣誉授予记录列表
     */
    IPage<HonorAward> getByOrganizationId(Page<HonorAward> page, Integer organizationId);
} 