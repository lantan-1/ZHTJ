package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorApplication;

/**
 * 荣誉申请业务接口
 */
public interface HonorApplicationService {
    
    /**
     * 分页查询荣誉申请
     */
    IPage<HonorApplication> getHonorApplicationPage(
        Page<HonorApplication> page,
        String year,
        String status,
        Integer honorTypeId,
        Integer organizationId
    );
    
    /**
     * 根据ID获取荣誉申请详情
     */
    HonorApplication getById(Integer id);
    
    /**
     * 根据用户ID获取荣誉申请列表
     */
    IPage<HonorApplication> getByUserId(
        Page<HonorApplication> page,
        Integer userId
    );
    
    /**
     * 检查用户是否有资格申请荣誉（评议为优秀）
     */
    boolean checkUserEligibility(Integer userId, String year);
    
    /**
     * 创建荣誉申请
     */
    Integer create(HonorApplication honorApplication);
    
    /**
     * 更新荣誉申请
     * 
     * @param honorApplication 荣誉申请对象
     * @return 是否成功
     */
    boolean update(HonorApplication honorApplication);
    
    /**
     * 删除荣誉申请
     * 
     * @param id 申请ID
     * @return 是否成功
     */
    boolean delete(Integer id);
    
    /**
     * 支部审批荣誉申请
     * 
     * @param id 申请ID
     * @param approvalStatus 审批状态
     * @param approvalComments 审批意见
     * @param approverId 审批人ID
     * @return 是否成功
     */
    boolean branchApprove(Integer id, String approvalStatus, String approvalComments, Integer approverId);
    
    /**
     * 上级审批荣誉申请
     * 
     * @param id 申请ID
     * @param approvalStatus 审批状态
     * @param approvalComments 审批意见
     * @param approverId 审批人ID
     * @return 是否成功
     */
    boolean higherApprove(Integer id, String approvalStatus, String approvalComments, Integer approverId);
    
    /**
     * 审批荣誉申请
     */
    boolean approve(Integer id, String status, String comments, Integer approverId);
}