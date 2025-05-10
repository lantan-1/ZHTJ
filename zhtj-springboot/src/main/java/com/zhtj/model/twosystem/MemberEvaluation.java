package com.zhtj.model.twosystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 团员教育评议活动实体类
 */
@Data
@TableName("member_evaluation")
public class MemberEvaluation implements Serializable {

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
    @TableField(exist = true)
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
     * 发起人ID（映射到数据库中的initiator_id字段）
     */
    @TableField("initiator_id")
    private Integer initiatorId;

    /**
     * 发起人姓名（映射到数据库中的initiator_name字段）
     */
    @TableField("initiator_name")
    private String initiatorName;

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
    
    /**
     * 评议选项（非数据库字段）
     */
    @TableField(exist = false)
    private List<EvaluationOption> evaluationOptions;
    
    /**
     * 评议统计信息（非数据库字段）
     */
    @TableField(exist = false)
    private EvaluationStatistics statistics;
} 