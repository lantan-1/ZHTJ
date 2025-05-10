package com.zhtj.service;

import com.zhtj.domain.TransferApprovalLog;

import java.util.List;

/**
 * 转接申请审批日志服务接口
 */
public interface TransferApprovalLogService {
    
    /**
     * 记录审批日志
     * 
     * @param log 审批日志信息
     * @return 是否成功
     */
    boolean addLog(TransferApprovalLog log);
    
    /**
     * 获取指定转接申请的所有审批日志
     * 
     * @param transferId 转接申请ID
     * @return 审批日志列表
     */
    List<TransferApprovalLog> getLogsByTransferId(Integer transferId);
    
    /**
     * 获取指定用户的所有审批日志
     * 
     * @param approverId 审批人ID
     * @return 审批日志列表
     */
    List<TransferApprovalLog> getLogsByApproverId(Integer approverId);
    
    /**
     * 根据审批类型获取指定转接申请的审批日志
     * 
     * @param transferId 转接申请ID
     * @param approvalType 审批类型（1：转出审批，2：转入审批）
     * @return 审批日志
     */
    TransferApprovalLog getLogByTransferIdAndType(Integer transferId, Integer approvalType);
    
    /**
     * 创建转出审批日志
     * 
     * @param transferId 转接申请ID
     * @param approverId 审批人ID
     * @param approverName 审批人姓名
     * @param approved 是否通过
     * @param approvalRemark 审批备注
     * @param operationIp 操作IP
     * @return 是否成功
     */
    boolean createOutApprovalLog(Integer transferId, Integer approverId, String approverName, 
                               Boolean approved, String approvalRemark, String operationIp);
    
    /**
     * 创建转入审批日志
     * 
     * @param transferId 转接申请ID
     * @param approverId 审批人ID
     * @param approverName 审批人姓名
     * @param approved 是否通过
     * @param approvalRemark 审批备注
     * @param operationIp 操作IP
     * @return 是否成功
     */
    boolean createInApprovalLog(Integer transferId, Integer approverId, String approverName, 
                              Boolean approved, String approvalRemark, String operationIp);
} 