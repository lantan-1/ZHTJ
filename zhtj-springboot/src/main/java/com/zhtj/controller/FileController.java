package com.zhtj.controller;

import com.zhtj.common.api.Result;
import com.zhtj.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件管理控制器
 */
@RestController
@RequestMapping("/files")
@Tag(name = "文件管理", description = "文件上传、下载和管理相关接口")
@ConditionalOnProperty(name = "minio.enabled", havingValue = "false")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private HttpServletRequest request;

    @Value("${file.local-storage.base-path:${user.home}/zhtj-storage}")
    private String baseStoragePath;

    /**
     * 上传文件
     * 
     * @param file 文件内容
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @param description 文件描述
     * @return 文件信息
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "上传文件并关联到具体业务")
    public Result<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("business_type") String businessType,
            @RequestParam("business_id") String businessId,
            @RequestParam(value = "description", required = false) String description) {
        try {
            // 根据业务类型决定文件类型
            String fileType;
            if (businessType.contains("image") || businessType.equals("avatar")) {
                fileType = "images";
            } else if (businessType.contains("video")) {
                fileType = "videos";
            } else {
                fileType = "documents";
            }

            // 上传文件
            String fileUrl = fileService.uploadFile(file, fileType);

            // 构建返回结果
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("id", businessType + "_" + businessId + "_" + System.currentTimeMillis());
            fileInfo.put("file_name", file.getOriginalFilename());
            fileInfo.put("file_path", fileUrl);
            fileInfo.put("file_size", file.getSize());
            
            // TODO: 如果需要，可以将文件信息保存到数据库

            return Result.success(fileInfo, "上传成功");
        } catch (IOException e) {
            return Result.failed("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件访问URL
     * 
     * @param fileUrl 文件路径
     * @param expireInSeconds 过期时间（秒）
     * @return 可访问的URL
     */
    @GetMapping("/url")
    @Operation(summary = "获取文件访问URL", description = "获取文件的临时访问URL")
    public Result<String> getFileUrl(
            @RequestParam("file_url") String fileUrl,
            @RequestParam(value = "expire", defaultValue = "3600") long expireInSeconds) {
        String publicUrl = fileService.getPublicUrl(fileUrl, expireInSeconds);
        return Result.success(publicUrl, "获取成功");
    }

    /**
     * 获取业务关联的文件列表
     * 
     * @param type 业务类型
     * @param id 业务ID
     * @return 文件列表
     */
    @GetMapping("/business/{type}/{id}")
    @Operation(summary = "获取业务关联的文件列表", description = "根据业务类型和ID获取关联的文件列表")
    public Result<Map<String, Object>> getBusinessFiles(
            @PathVariable("type") String type,
            @PathVariable("id") String id) {
        // TODO: 从数据库查询业务关联的文件信息
        // 这里模拟返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0);
        result.put("list", List.of());
        
        return Result.success(result);
    }

    /**
     * 删除文件
     * 
     * @param fileUrl 文件路径
     * @return 操作结果
     */
    @DeleteMapping
    @Operation(summary = "删除文件", description = "删除指定的文件")
    public Result<Boolean> deleteFile(@RequestParam("file_url") String fileUrl) {
        boolean deleted = fileService.deleteFile(fileUrl);
        return Result.success(deleted, deleted ? "删除成功" : "文件不存在");
    }

    /**
     * 获取文件信息
     * 
     * @param fileUrl 文件路径
     * @return 文件信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取文件信息", description = "获取文件的详细信息")
    public Result<Map<String, Object>> getFileInfo(@RequestParam("file_url") String fileUrl) {
        Map<String, Object> fileInfo = fileService.getFileInfo(fileUrl);
        return Result.success(fileInfo);
    }

    /**
     * 生成图片缩略图
     * 
     * @param fileUrl 图片文件路径
     * @param width 缩略图宽度
     * @param height 缩略图高度
     * @return 缩略图路径
     */
    @PostMapping("/thumbnail")
    @Operation(summary = "生成图片缩略图", description = "生成指定宽高的图片缩略图")
    public Result<String> generateThumbnail(
            @RequestParam("file_url") String fileUrl,
            @RequestParam(value = "width", defaultValue = "200") int width,
            @RequestParam(value = "height", defaultValue = "200") int height) {
        try {
            String thumbnailUrl = fileService.generateThumbnail(fileUrl, width, height);
            return Result.success(thumbnailUrl, "生成缩略图成功");
        } catch (IOException e) {
            return Result.failed("生成缩略图失败: " + e.getMessage());
        }
    }

    /**
     * 检查文件是否存在
     * 
     * @param fileUrl 文件路径
     * @return 是否存在
     */
    @GetMapping("/exists")
    @Operation(summary = "检查文件是否存在", description = "检查指定路径的文件是否存在")
    public Result<Boolean> fileExists(@RequestParam("file_url") String fileUrl) {
        boolean exists = fileService.fileExists(fileUrl);
        return Result.success(exists);
    }

    /**
     * 文件下载/预览接口
     *
     * @param download 是否作为下载文件处理
     * @return 文件内容
     */
    @GetMapping("/**")
    public ResponseEntity<Resource> getFile(
            @RequestParam(value = "download", required = false, defaultValue = "false") boolean download) {
        
        try {
            // 通过RequestURI获取完整路径并截取/files/后面的部分作为文件路径
            String requestUri = request.getRequestURI();
            String contextPath = request.getContextPath();
            String filePath = requestUri.substring(contextPath.length() + "/files/".length());
            
            if (!StringUtils.hasText(filePath)) {
                return ResponseEntity.badRequest().build();
            }
            
            // 构建文件系统路径
            Path path = Paths.get(baseStoragePath).resolve(filePath.replace('/', Paths.get("").getFileSystem().getSeparator().charAt(0)));
            
            // 检查文件是否存在
            if (!Files.exists(path) || Files.isDirectory(path)) {
                return ResponseEntity.notFound().build();
            }
            
            // 创建资源
            Resource resource = new UrlResource(path.toUri());
            
            // 确定内容类型
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            // 创建响应
            if (download) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + path.getFileName().toString() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
} 