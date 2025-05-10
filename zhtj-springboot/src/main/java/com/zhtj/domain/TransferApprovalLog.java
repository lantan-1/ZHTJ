package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 转接申请审批日志实体类
 */
@TableName("league_transfer_approval_log")
public class TransferApprovalLog {
    
    /**
     * 日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 转接申请ID
     */
    private Integer transferId;
    
    /**
     * 审批类型（1：转出审批，2：转入审批）
     */
    private Integer approvalType;
    
    /**
     * 审批人ID
     */
    private Integer approverId;
    
    /**
     * 审批人姓名
     */
    private String approverName;
    
    /**
     * 审批动作（true: 通过，false: 拒绝）
     */
    private Boolean approved;
    
    /**
     * 审批备注
     */
    private String approvalRemark;
    
    /**
     * 操作IP
     */
    private String operationIp;
    
    /**
     * 操作时间
     */
    private LocalDateTime approvalTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransferId() {
        return transferId;
    }

    public void setTransferId(Integer transferId) {
        this.transferId = transferId;
    }

    public Integer getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(Integer approvalType) {
        this.approvalType = approvalType;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getApprovalRemark() {
        return approvalRemark;
    }

    public void setApprovalRemark(String approvalRemark) {
        this.approvalRemark = approvalRemark;
    }

    public String getOperationIp() {
        return operationIp;
    }

    public void setOperationIp(String operationIp) {
        this.operationIp = operationIp;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "TransferApprovalLog{" +
                "id=" + id +
                ", transferId=" + transferId +
                ", approvalType=" + approvalType +
                ", approverId=" + approverId +
                ", approverName='" + approverName + '\'' +
                ", approved=" + approved +
                ", approvalRemark='" + approvalRemark + '\'' +
                ", operationIp='" + operationIp + '\'' +
                ", approvalTime=" + approvalTime +
                ", createTime=" + createTime +
                '}';
    }
} 