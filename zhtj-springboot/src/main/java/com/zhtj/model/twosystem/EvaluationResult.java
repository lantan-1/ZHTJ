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
 * 评议结果实体类
 */
@Data
@TableName("evaluation_result")
public class EvaluationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结果ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 评议活动ID
     */
    private Integer evaluationId;

    /**
     * 团员ID
     */
    private Integer memberId;
    
    /**
     * 团员姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String memberName;
    
    /**
     * 团员职务（非数据库字段）
     */
    @TableField(exist = false)
    private String memberPosition;
    
    /**
     * 团支部名称（非数据库字段）
     */
    @TableField(exist = false)
    private String branchName;

    /**
     * 评议结果
     */
    private String result;

    /**
     * 评议状态：未评议、已评议
     */
    private String status;

    /**
     * 评议意见
     */
    private String comment;

    /**
     * 评议时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime evaluationTime;

    /**
     * 评议人ID
     */
    private Integer evaluatorId;

    /**
     * 评议人姓名
     */
    private String evaluatorName;

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