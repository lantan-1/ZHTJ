package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 团员教育评议活动实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("member_evaluation")
public class Evaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评议活动ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 评议标题
     */
    private String title;

    /**
     * 评议年度
     */
    private String evaluationYear;

    /**
     * 评议组织ID
     */
    private Integer organizationId;
    
    /**
     * 组织名称
     */
    @TableField(exist = false)
    private String organizationName;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 状态：草稿、进行中、已完成、已取消
     */
    private String status;

    /**
     * 评议说明
     */
    private String description;

    /**
     * 创建人ID
     */
    @TableField("initiator_id")
    private Integer creatorId;

    /**
     * 创建人姓名
     */
    @TableField("initiator_name")
    private String creatorName;

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