package com.zhtj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 学习资源实体类
 */
@Data
@TableName("study_resource")
@Schema(description = "学习资源")
public class StudyResource {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "资源ID")
    private Integer id;
    
    /**
     * 资源名称
     */
    @Schema(description = "资源标题")
    private String title;
    
    /**
     * 资源描述
     */
    @Schema(description = "资源描述")
    private String description;
    
    /**
     * 文件URL
     */
    @Schema(description = "文件URL")
    private String fileUrl;
    
    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;
    
    /**
     * 文件大小(字节)
     */
    @Schema(description = "文件大小(字节)")
    private Long fileSize;
    
    /**
     * 文件格式
     */
    @Schema(description = "文件格式")
    private String format;
    
    /**
     * 资源分类ID
     * 1-思想理论，2-时政热点，3-团史学习，4-团章团规，5-入团教育，6-其他
     */
    @Schema(description = "资源分类ID")
    private Integer categoryId;
    
    /**
     * 资源分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
    
    /**
     * 所属组织ID
     */
    @Schema(description = "组织ID")
    private Integer organizationId;
    
    /**
     * 所属组织名称（非数据库字段）
     */
    @Schema(description = "组织名称")
    @TableField(exist = false)
    private String organizationName;
    
    /**
     * 创建人ID
     */
    @Schema(description = "创建者ID")
    private Integer creatorId;
    
    /**
     * 创建人姓名
     */
    @Schema(description = "创建者姓名")
    private String creatorName;
    
    /**
     * 下载次数
     */
    @Schema(description = "下载次数")
    private Integer downloads;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
} 