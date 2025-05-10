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
 * 志愿服务记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("volunteer_service")
public class VolunteerService {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                      // 志愿服务记录ID
    
    private Integer userId;                  // 用户ID
    
    private Integer organizationId;          // 组织ID
    
    private String serviceYear;              // 服务年度
    
    private Double serviceDuration;          // 服务时长(小时)
    
    private String serviceDescription;       // 服务描述
    
    // 以下字段在数据库中不存在，标记为非数据库字段
    @TableField(exist = false)
    private String serviceLocation;          // 服务地点
    
    @TableField(exist = false)
    private String serviceType;              // 服务类型(环保/教育/医疗/其他)
    
    @TableField(exist = false)
    private LocalDateTime serviceStartTime;  // 服务开始时间
    
    @TableField(exist = false)
    private LocalDateTime serviceEndTime;    // 服务结束时间
    
    @TableField(exist = false)
    private String serviceProof;             // 服务证明URL(图片或文件)
    
    @TableField(exist = false)
    private Integer verificationStatus;      // 验证状态(0-待验证，1-已验证，2-已拒绝)
    
    @TableField(exist = false)
    private String verificationRemark;       // 验证备注
    
    @TableField(exist = false)
    private Integer verifierUserId;          // 验证人ID
    
    @TableField(exist = false)
    private LocalDateTime verificationTime;  // 验证时间
    
    private LocalDateTime createTime;        // 创建时间
    
    private LocalDateTime updateTime;        // 更新时间
    
    // 非数据库字段
    @TableField(exist = false)
    private String userName;                 // 用户姓名
    
    @TableField(exist = false)
    private String organizationName;         // 组织名称
    
    @TableField(exist = false)
    private String verifierUserName;         // 验证人姓名
} 