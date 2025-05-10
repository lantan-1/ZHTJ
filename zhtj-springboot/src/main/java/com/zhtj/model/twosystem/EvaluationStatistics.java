package com.zhtj.model.twosystem;

import lombok.Data;

import java.io.Serializable;

/**
 * 评议统计信息实体类
 */
@Data
public class EvaluationStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总人数
     */
    private Integer totalCount = 0;

    /**
     * 已完成评议人数
     */
    private Integer completedCount = 0;

    /**
     * 总体进度百分比
     */
    private Integer overallProgress = 0;

    /**
     * 优秀人数
     */
    private Integer excellentCount = 0;

    /**
     * 优秀率百分比
     */
    private Integer excellentRate = 0;

    /**
     * 合格人数
     */
    private Integer qualifiedCount = 0;

    /**
     * 合格率百分比
     */
    private Integer qualifiedRate = 0;

    /**
     * 基本合格人数
     */
    private Integer basicQualifiedCount = 0;

    /**
     * 基本合格率百分比
     */
    private Integer basicQualifiedRate = 0;

    /**
     * 不合格人数
     */
    private Integer unqualifiedCount = 0;

    /**
     * 不合格率百分比
     */
    private Integer unqualifiedRate = 0;
} 