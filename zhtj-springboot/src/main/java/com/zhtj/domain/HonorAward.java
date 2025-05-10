package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 荣誉授予记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("honor_award")
public class HonorAward {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                        // 授予记录ID
    
    private Integer honorApplicationId;        // 荣誉申请ID
    
    private Integer honorTypeId;               // 荣誉类型ID
    
    private String honorName;                  // 荣誉名称
    
    private String honorCode;                  // 荣誉编号
    
    private String awardYear;                  // 授予年度
    
    private Integer userId;                    // 用户ID
    
    private Integer organizationId;            // 组织ID
    
    private LocalDate awardDate;               // 授予日期
    
    private String awardReason;                // 授予理由
    
    private String certificateUrl;             // 证书URL
    
    private String awardingUnit;               // 授予单位
    
    private Integer awardedById;               // 授予人ID
    
    private LocalDateTime createTime;          // 创建时间
    
    private LocalDateTime updateTime;          // 更新时间
    
    // 非数据库字段
    @TableField(exist = false)
    private String userName;                   // 用户姓名
    
    @TableField(exist = false)
    private String organizationName;           // 组织名称
    
    @TableField(exist = false)
    private String awardedByName;              // 授予人姓名
} 