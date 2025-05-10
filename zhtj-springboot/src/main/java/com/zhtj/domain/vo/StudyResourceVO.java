package com.zhtj.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 学习资源视图对象
 */
@Data
@Schema(description = "学习资源视图对象")
public class StudyResourceVO {
    
    @Schema(description = "资源ID")
    private Integer id;
    
    @Schema(description = "资源标题")
    private String title;
    
    @Schema(description = "资源描述")
    private String description;
    
    @Schema(description = "分类ID")
    private Integer categoryId;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "文件URL")
    private String fileUrl;
    
    @Schema(description = "文件名")
    private String fileName;
    
    @Schema(description = "文件大小(字节)")
    private Long fileSize;
    
    @Schema(description = "文件格式")
    private String format;
    
    @Schema(description = "创建者ID")
    private Integer creatorId;
    
    @Schema(description = "创建者姓名")
    private String creatorName;
    
    @Schema(description = "组织ID")
    private Integer organizationId;
    
    @Schema(description = "组织名称")
    private String organizationName;
    
    @Schema(description = "下载次数")
    private Integer downloads;
    
    @Schema(description = "创建时间")
    private Date createTime;
    
    @Schema(description = "更新时间")
    private Date updateTime;
    
    @Schema(description = "权限信息")
    private PermissionVO permissions;
    
    @Schema(description = "预览URL")
    private String previewUrl;
    
    @Schema(description = "下载URL")
    private String downloadUrl;
    
    /**
     * 权限信息内部类
     */
    @Data
    @Schema(description = "权限信息")
    public static class PermissionVO {
        
        @Schema(description = "是否可查看")
        private Boolean canView;
        
        @Schema(description = "是否可下载")
        private Boolean canDownload;
        
        @Schema(description = "是否可编辑")
        private Boolean canEdit;
        
        @Schema(description = "是否可删除")
        private Boolean canDelete;
    }
} 