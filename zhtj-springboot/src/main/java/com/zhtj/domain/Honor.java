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
 * 荣誉激励实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("honor")
public class Honor {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                     // 荣誉ID
    
    private String title;                   // 荣誉标题
    
    private String type;                    // 荣誉类型(个人/集体)
    
    private String level;                   // 荣誉级别(国家级/省级/市级/区县级/乡镇级/其他)
    
    private String honorYear;               // 荣誉年度
    
    private Integer organizationId;         // 组织ID(集体荣誉关联)
    
    private Integer userId;                 // 用户ID(个人荣誉关联)
    
    private String description;             // 荣誉描述
    
    private String certificateUrl;          // 证书图片URL
    
    private String awardingUnit;            // 颁发单位
    
    private LocalDateTime awardingTime;     // 颁发时间

    private LocalDateTime applicationTime;     // 申请时间

    private LocalDateTime createTime;       // 创建时间
    
    private LocalDateTime updateTime;       // 更新时间
    
    // 非数据库字段
    @TableField(exist = false)
    private String organizationName;        // 组织名称
    
    @TableField(exist = false)
    private String userName;                // 用户姓名
} 