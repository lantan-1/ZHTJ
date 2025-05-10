package com.zhtj.dto;

import lombok.Data;

/**
 * 团课资源查询参数DTO
 */
@Data
public class StudyResourceQueryDTO {
    
    /**
     * 资源名称
     */
    private String title;
    
    /**
     * 资源分类ID
     */
    private Integer categoryId;
    
    /**
     * 文件格式
     */
    private String format;
    
    /**
     * 组织ID
     */
    private Integer organizationId;
    
    /**
     * 关键词
     */
    private String keyword;
    
    /**
     * 开始日期
     */
    private String startDate;
    
    /**
     * 结束日期
     */
    private String endDate;
    
    /**
     * 当前页码
     */
    private Integer page = 1;
    
    /**
     * 每页数量
     */
    private Integer size = 10;
} 