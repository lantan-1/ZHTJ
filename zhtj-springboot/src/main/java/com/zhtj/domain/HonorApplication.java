package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 荣誉申请实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("honor_application")
public class HonorApplication {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                      // 申请ID
    
    private String honorName;                // 荣誉名称
    
    private Integer userId;                  // 用户ID
    
    private String userName;                 // 用户姓名
    
    private Integer organizationId;          // 组织ID
    
    private String organizationName;         // 组织名称
    
    private String applicationYear;          // 申请年度
    
    private String applicationReason;        // 申请理由
    
    private Integer relatedEvaluationId;     // 关联评议ID
    
    private String relatedEvaluationResult;  // 关联评议结果
    
    private Double relatedEvaluationScore;   // 关联评议分数
    
    private Integer applicantId;             // 提名人ID
    
    private String applicantName;            // 提名人姓名
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime applicationTime;   // 申请时间
    
    private String status;                   // 状态(待审批/已通过/已拒绝)
    
    private String attachmentUrl;            // 附件URL
    
    private Integer approverId;              // 审批人ID
    
    private String approverName;             // 审批人姓名
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime approvalTime;      // 审批时间
    
    private String approvalComments;         // 审批意见
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;        // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;        // 更新时间

    // 非数据库字段
    @TableField(exist = false)
    private HonorType honorType;             // 荣誉类型信息
} 