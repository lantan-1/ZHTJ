package com.zhtj.domain.dto;

import com.zhtj.domain.enums.ResourceCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 学习资源数据传输对象
 */
@Data
@Schema(description = "学习资源DTO")
public class StudyResourceDTO {
    
    @Schema(description = "资源ID")
    private Long id;
    
    @NotBlank(message = "资源标题不能为空")
    @Size(max = 100, message = "资源标题长度不能超过100个字符")
    @Schema(description = "资源标题", required = true)
    private String title;
    
    @Size(max = 500, message = "资源描述长度不能超过500个字符")
    @Schema(description = "资源描述")
    private String description;
    
    @NotNull(message = "资源分类不能为空")
    @Schema(description = "资源分类", required = true)
    private ResourceCategory category;
    
    @Schema(description = "资源文件", type = "string", format = "binary")
    private MultipartFile file;
    
    @Schema(description = "文件路径")
    private String filePath;
    
    @Schema(description = "访问权限级别(1:公开 2:组织内可见)")
    private Integer accessLevel = 1;
    
    @Schema(description = "组织ID")
    private Long orgId;
} 