package com.zhtj.model.twosystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 团员注册实体类
 */
@Data
@TableName("member_register")
public class MemberRegister implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 注册ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 批次ID
     */
    private Integer batchId;
    
    /**
     * 批次编号（非数据库字段）
     */
    @TableField(exist = false)
    private String batchCode;
    
    /**
     * 批次标题（非数据库字段）
     */
    @TableField(exist = false)
    private String title;
    
    /**
     * 注册年度
     */
    private String registerYear;

    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户姓名
     */
    private String userName;
    
    /**
     * 组织ID
     */
    private Integer organizationId;
    
    /**
     * 组织名称
     */
    private String organizationName;
    
    /**
     * 团员姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String memberName;
    
    /**
     * 团员学号/工号（非数据库字段）
     */
    @TableField(exist = false)
    private String memberCode;
    
    /**
     * 团支部ID（非数据库字段）
     */
    @TableField(exist = false)
    private Integer branchId;
    
    /**
     * 团委ID（非数据库字段）
     */
    @TableField(exist = false)
    private Integer committeeId;

    /**
     * 状态：未申请、待审核、已通过、已驳回、已完成
     */
    private String status;

    /**
     * 政治表现(1-5分)
     */
    @TableField(exist = false)
    private Integer politicalPerformance;

    /**
     * 学习表现(1-5分)
     */
    @TableField(exist = false)
    private Integer studyPerformance;

    /**
     * 工作表现(1-5分)
     */
    @TableField(exist = false)
    private Integer workPerformance;

    /**
     * 活动参与情况
     */
    @TableField(exist = false)
    private String activityParticipation;

    /**
     * 是否缴纳团费
     */
    @TableField(exist = false)
    private Boolean paidFees;

    /**
     * 自我评价
     */
    @TableField(exist = false)
    private String selfEvaluation;

    /**
     * 备注
     */
    private String remark;
    
    /**
     * 注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    /**
     * 申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private LocalDateTime applyTime;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalTime;

    /**
     * 审核人ID
     */
    private Integer approverId;

    /**
     * 审核人姓名
     */
    private String approverName;
    
    /**
     * 审核意见
     */
    private String approvalComment;
    
    /**
     * 注册截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerDeadline;
    
    /**
     * 是否已发送提醒
     */
    private Boolean isReminderSent;
    
    /**
     * 提醒时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reminderTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
} 