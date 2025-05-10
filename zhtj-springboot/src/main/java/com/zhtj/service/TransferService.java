package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.Transfer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 团员关系转接服务接口
 */
public interface TransferService extends IService<Transfer> {
    
    /**
     * 分页查询转接申请列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param statusCode 状态码
     * @param organizationId 组织ID
     * @return 分页结果
     */
    IPage<Transfer> getTransferPage(Page<Transfer> page, Integer userId, Integer statusCode, Integer organizationId);
    
    /**
     * 获取转接申请详情
     * 
     * @param id 转接申请ID
     * @return 转接申请详情
     */
    Transfer getTransferDetail(Integer id);
    
    /**
     * 创建转接申请
     * 
     * @param transfer 转接申请信息
     * @return 是否成功
     */
    boolean createTransfer(Transfer transfer);
    
    /**
     * 转出组织审批
     * 
     * @param id 转接申请ID
     * @param approved 是否批准
     * @param approverId 审批人ID
     * @param remark 审批备注
     * @return 是否成功
     */
    boolean outApprove(Integer id, boolean approved, Integer approverId, String remark);
    
    /**
     * 转入组织审批
     * 
     * @param id 转接申请ID
     * @param approved 是否批准
     * @param approverId 审批人ID
     * @param remark 审批备注
     * @return 是否成功
     */
    boolean inApprove(Integer id, boolean approved, Integer approverId, String remark);
    
    /**
     * 取消转接申请
     * 
     * @param id 转接申请ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean cancelTransfer(Integer id, Integer userId);
    
    /**
     * 查询用户的转接申请
     * 
     * @param userId 用户ID
     * @return 转接申请列表
     */
    List<Transfer> getTransfersByUserId(Integer userId);
    
    /**
     * 查询待转出组织审批的转接申请
     * 
     * @param organizationId 组织ID
     * @return 转接申请列表
     */
    List<Transfer> getOutgoingTransfersByOrgId(Integer organizationId);
    
    /**
     * 查询待转入组织审批的转接申请
     * 
     * @param organizationId 组织ID
     * @return 转接申请列表
     */
    List<Transfer> getIncomingTransfersByOrgId(Integer organizationId);
    
    /**
     * 查询已批准转出的转接申请
     * 
     * @param organizationId 组织ID
     * @return 已批准转出的转接申请列表
     */
    List<Transfer> getApprovedOutTransfersByOrgId(Integer organizationId);
    
    /**
     * 获取已过期的转接申请
     * 
     * @param currentTime 当前时间
     * @return 过期的转接申请列表
     */
    List<Transfer> getExpiredTransfers(LocalDateTime currentTime);
    
    /**
     * 检查转接申请是否存在
     * 
     * @param transferId 转接申请ID
     * @return 是否存在
     */
    boolean exists(Integer transferId);
    
    /**
     * 检查用户是否有权限访问该转接申请
     * 判断用户是否为申请人、转出组织管理员或转入组织管理员
     * 
     * @param transferId 转接申请ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean checkAccessPermission(Integer transferId, Integer userId);
} 