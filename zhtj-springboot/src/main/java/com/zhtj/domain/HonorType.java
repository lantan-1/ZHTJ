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
 * 荣誉类型实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("honor_type")
public class HonorType {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                     // 荣誉类型ID
    
    private String honorName;               // 荣誉名称
    
    private String honorCode;               // 荣誉代码
    
    private String honorCategory;           // 荣誉类别(团员荣誉/团干部荣誉/团组织荣誉)
    
    private String level;                   // 荣誉级别(院级/校级/市级/省级/国家级)
    
    private Integer requiresEvaluation;     // 是否需要评议结果(0-不需要，1-需要)
    
    private Double minEvaluationScore;      // 最低评议分数
    
    private String requiredResult;          // 要求评议结果(优秀/合格等)
    
    private String description;             // 描述
    
    private Integer status;                 // 状态(0-禁用，1-启用)
    
    private String iconUrl;                 // 图标URL
    
    private LocalDateTime createTime;       // 创建时间
    
    private LocalDateTime updateTime;       // 更新时间
} 