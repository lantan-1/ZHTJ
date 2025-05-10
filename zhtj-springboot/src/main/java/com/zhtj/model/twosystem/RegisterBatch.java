package com.zhtj.model.twosystem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 团籍注册批次实体类
 */
@Data
@TableName("member_register_batch")
public class RegisterBatch implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 批次ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 批次名称
     */
    @TableField("batch_name")
    private String batchName;

    /**
     * 批次编号 (非数据库字段，用于前端显示)
     */
    @TableField(exist = false)
    private String batchCode;

    /**
     * 批次标题 (非数据库字段，用于前端显示)
     */
    @TableField(exist = false)
    private String title;

    /**
     * 组织ID
     */
    private Integer organizationId;

    /**
     * 组织名称
     */
    private String organizationName;

    /**
     * 注册年度
     */
    private String registerYear;
    
    /**
     * 前端兼容字段：注册年度 (非数据库字段)
     */
    @TableField(exist = false)
    private String year;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    /**
     * 前端兼容字段：开始日期 (非数据库字段)
     */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
    
    /**
     * 前端兼容字段：结束日期 (非数据库字段)
     */
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    /**
     * 状态：未开始、进行中、已结束
     */
    private String status;

    /**
     * 注册说明
     */
    private String description;

    /**
     * 创建人ID
     */
    private Integer creatorId;

    /**
     * 创建人姓名
     */
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
    
    /**
     * 目标组织ID列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Integer> targetOrganizationIds;
    
    /**
     * 注册总人数（非数据库字段）
     */
    @TableField(exist = false)
    private Integer totalCount;
    
    /**
     * 已注册人数（非数据库字段）
     */
    @TableField(exist = false)
    private Integer registeredCount;
    
    /**
     * 进度百分比（非数据库字段）
     */
    @TableField(exist = false)
    private BigDecimal progressPercentage;
} 