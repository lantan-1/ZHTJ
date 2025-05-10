package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhtj.domain.TransferApprovalLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 转接申请审批日志Mapper接口
 */
@Mapper
public interface TransferApprovalLogMapper extends BaseMapper<TransferApprovalLog> {
    
    /**
     * 获取指定转接申请的所有审批日志
     * 
     * @param transferId 转接申请ID
     * @return 审批日志列表
     */
    List<TransferApprovalLog> getLogsByTransferId(@Param("transferId") Integer transferId);
    
    /**
     * 获取指定用户的所有审批日志
     * 
     * @param approverId 审批人ID
     * @return 审批日志列表
     */
    List<TransferApprovalLog> getLogsByApproverId(@Param("approverId") Integer approverId);
    
    /**
     * 根据审批类型获取指定转接申请的审批日志
     * 
     * @param transferId 转接申请ID
     * @param approvalType 审批类型（1：转出审批，2：转入审批）
     * @return 审批日志
     */
    TransferApprovalLog getLogByTransferIdAndType(@Param("transferId") Integer transferId, 
                                                 @Param("approvalType") Integer approvalType);
} 