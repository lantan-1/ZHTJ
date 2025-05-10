package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 团员评议详情实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("evaluation_detail")
public class EvaluationDetail {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;                 // 评议详情ID
    
    private Integer evaluationId;       // 评议活动ID
    
    private Integer userId;             // 被评议用户ID
    
    private String result;              // 评议结果(优秀/合格/基本合格/不合格)
    
    private BigDecimal score;           // 评议得分
    
    private String comment;             // 评议意见
    
    private String status;              // 状态(待评议/已评议)
    
    private LocalDateTime evaluateTime; // 评议时间
    
    private LocalDateTime createTime;   // 创建时间
    
    private LocalDateTime updateTime;   // 更新时间
    
    // 非数据库字段
    @TableField(exist = false)
    private String userName;            // 用户姓名
    
    @TableField(exist = false)
    private String evaluationTitle;     // 评议活动标题
} 