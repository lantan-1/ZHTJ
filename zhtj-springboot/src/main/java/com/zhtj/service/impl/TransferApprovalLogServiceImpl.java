package com.zhtj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.domain.TransferApprovalLog;
import com.zhtj.mapper.TransferApprovalLogMapper;
import com.zhtj.service.TransferApprovalLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 转接申请审批日志服务实现类
 */
@Service
public class TransferApprovalLogServiceImpl extends ServiceImpl<TransferApprovalLogMapper, TransferApprovalLog> 
                                            implements TransferApprovalLogService {
    
    private static final Logger log = LoggerFactory.getLogger(TransferApprovalLogServiceImpl.class);
    
    @Override
    public boolean addLog(TransferApprovalLog logRecord) {
        if (logRecord == null) {
            log.error("审批日志记录不能为空");
            return false;
        }
        
        // 设置创建时间
        if (logRecord.getCreateTime() == null) {
            logRecord.setCreateTime(LocalDateTime.now());
        }
        
        // 设置审批时间
        if (logRecord.getApprovalTime() == null) {
            logRecord.setApprovalTime(LocalDateTime.now());
        }
        
        try {
            return this.save(logRecord);
        } catch (Exception e) {
            log.error("保存审批日志时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<TransferApprovalLog> getLogsByTransferId(Integer transferId) {
        if (transferId == null) {
            log.error("转接申请ID不能为空");
            return List.of();
        }
        
        try {
            return baseMapper.getLogsByTransferId(transferId);
        } catch (Exception e) {
            log.error("获取转接申请审批日志时发生错误: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public List<TransferApprovalLog> getLogsByApproverId(Integer approverId) {
        if (approverId == null) {
            log.error("审批人ID不能为空");
            return List.of();
        }
        
        try {
            return baseMapper.getLogsByApproverId(approverId);
        } catch (Exception e) {
            log.error("获取审批人审批日志时发生错误: {}", e.getMessage(), e);
            return List.of();
        }
    }
    
    @Override
    public TransferApprovalLog getLogByTransferIdAndType(Integer transferId, Integer approvalType) {
        if (transferId == null || approvalType == null) {
            log.error("转接申请ID和审批类型不能为空");
            return null;
        }
        
        try {
            return baseMapper.getLogByTransferIdAndType(transferId, approvalType);
        } catch (Exception e) {
            log.error("根据转接申请ID和审批类型获取审批日志时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean createOutApprovalLog(Integer transferId, Integer approverId, String approverName,
                                      Boolean approved, String approvalRemark, String operationIp) {
        return createApprovalLog(transferId, 1, approverId, approverName, approved, approvalRemark, operationIp);
    }
    
    @Override
    public boolean createInApprovalLog(Integer transferId, Integer approverId, String approverName,
                                     Boolean approved, String approvalRemark, String operationIp) {
        return createApprovalLog(transferId, 2, approverId, approverName, approved, approvalRemark, operationIp);
    }
    
    /**
     * 通用创建审批日志方法
     */
    private boolean createApprovalLog(Integer transferId, Integer approvalType, Integer approverId, 
                                    String approverName, Boolean approved, String approvalRemark, 
                                    String operationIp) {
        if (transferId == null || approvalType == null || approverId == null) {
            log.error("转接申请ID、审批类型和审批人ID不能为空");
            return false;
        }
        
        TransferApprovalLog logRecord = new TransferApprovalLog();
        logRecord.setTransferId(transferId);
        logRecord.setApprovalType(approvalType);
        logRecord.setApproverId(approverId);
        logRecord.setApproverName(approverName);
        logRecord.setApproved(approved);
        logRecord.setApprovalRemark(approvalRemark);
        logRecord.setOperationIp(operationIp);
        logRecord.setApprovalTime(LocalDateTime.now());
        logRecord.setCreateTime(LocalDateTime.now());
        
        return this.addLog(logRecord);
    }
} 