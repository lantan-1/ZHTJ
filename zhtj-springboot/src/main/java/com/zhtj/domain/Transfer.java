package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 团员关系转接实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("transfer")
public class Transfer {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                      // 转接ID
    
    private Integer transferUserId;          // 转接用户ID
    
    private Integer transferOutOrgId;        // 转出组织ID
    
    private Integer transferInOrgId;         // 转入组织ID
    
    private String transferReason;           // 转接理由
    
    private String transferRemark;           // 转接备注
    
    private LocalDateTime applicationTime;   // 申请时间
    
    private String status;                   // 状态(申请中/转出审批中/转入审批中/已通过/已拒绝)
    
    private Integer statusCode;              // 状态码(0:申请中,1:转出审批中,2:转入审批中,3:已通过,4:已拒绝)
    
    private LocalDateTime expireTime;        // 过期时间(申请时间+3个月)
    
    private LocalDateTime outApprovalTime;   // 转出审批时间
    
    private Integer outApproverId;           // 转出审批人ID
    
    private Boolean outApproved;             // 转出是否通过
    
    private String outApprovalRemark;        // 转出审批备注
    
    private LocalDateTime inApprovalTime;    // 转入审批时间
    
    private Integer inApproverId;            // 转入审批人ID
    
    private Boolean inApproved;              // 转入是否通过
    
    private String inApprovalRemark;         // 转入审批备注
    
    private LocalDateTime createTime;        // 创建时间
    
    private LocalDateTime updateTime;        // 更新时间
    
    // 转出组织管理员ID (数据库中不存在此字段)
    @TableField(exist = false)
    private Integer outOrganizationAdminId;
    
    // 转入组织管理员ID (数据库中不存在此字段)
    @TableField(exist = false)
    private Integer inOrganizationAdminId;
    
    // 转接用户姓名
    @TableField(exist = false)
    private String transferUserName;
    
    // 转出组织名称
    @TableField(exist = false)
    private String transferOutOrgName;
    
    // 转出组织全称
    @TableField(exist = false)
    private String transferOutOrgFullName;
    
    // 转入组织名称
    @TableField(exist = false)
    private String transferInOrgName;
    
    // 转入组织全称
    @TableField(exist = false)
    private String transferInOrgFullName;
    
    // 转出审批人姓名
    @TableField(exist = false)
    private String outApproverName;
    
    // 转入审批人姓名
    @TableField(exist = false)
    private String inApproverName;
    
    // 非数据库字段
    @TableField(exist = false)
    private User transferUser;               // 转接用户信息
    
    @TableField(exist = false)
    private Organization transferOutOrg;     // 转出组织信息
    
    @TableField(exist = false)
    private Organization transferInOrg;      // 转入组织信息
    
    @TableField(exist = false)
    private User outApprover;                // 转出审批人
    
    @TableField(exist = false)
    private User inApprover;                 // 转入审批人
    
    // 状态常量定义
    public static final String STATUS_APPLYING = "申请中";
    public static final String STATUS_OUT_APPROVING = "转出审批中";
    public static final String STATUS_IN_APPROVING = "转入审批中";
    public static final String STATUS_APPROVED = "已通过";
    public static final String STATUS_REJECTED = "已拒绝";
    
    public static final int STATUS_CODE_APPLYING = 0;
    public static final int STATUS_CODE_OUT_APPROVING = 1;
    public static final int STATUS_CODE_IN_APPROVING = 2;
    public static final int STATUS_CODE_APPROVED = 3;
    public static final int STATUS_CODE_REJECTED = 4;
    
    /**
     * 获取转出组织管理员ID
     * 
     * @return 转出组织管理员ID
     */
    public Integer getOutOrganizationAdminId() {
        return outOrganizationAdminId;
    }
    
    /**
     * 获取转入组织管理员ID
     * 
     * @return 转入组织管理员ID
     */
    public Integer getInOrganizationAdminId() {
        return inOrganizationAdminId;
    }
} 