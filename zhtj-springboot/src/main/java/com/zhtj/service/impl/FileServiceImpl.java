package com.zhtj.service.impl;

import com.zhtj.common.exception.BusinessException;
import com.zhtj.config.FileProperties;
import com.zhtj.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * 文件服务实现类
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.service.default-impl", havingValue = "true", matchIfMissing = false)
public class FileServiceImpl implements FileService {
    
    @Autowired
    private FileProperties fileProperties;
    
    @Value("${file.upload.base-path:E:/Hunter/Project/zhtj/uploads}")
    private String baseUploadPath;
    
    @Value("${file.upload.study-resource.allowed-types:pdf,doc,docx,ppt,pptx,txt,mp4,mp3,zip,rar,jpg,jpeg,png,gif}")
    private String allowedFileTypes;
    
    // 从fileProperties中获取配置值
    private String getUploadDir() {
        return fileProperties.getUploadDir();
    }
    
    private String getAllowedImageTypes() {
        return fileProperties.getAllowedFileTypes().get("images");
    }
    
    private String getAllowedDocumentTypes() {
        return fileProperties.getAllowedFileTypes().get("documents");
    }
    
    private String getAllowedVideoTypes() {
        return fileProperties.getAllowedFileTypes().get("videos");
    }
    
    // 默认允许的文件类型
    private static final Set<String> DEFAULT_ALLOWED_TYPES = new HashSet<>(
            Arrays.asList("pdf", "doc", "docx", "ppt", "pptx", "txt", "mp4", "mp3", "zip", "rar", "jpg", "jpeg", "png", "gif")
    );
    
    /**
     * 获取允许的文件类型列表
     */
    private List<String> getAllowedFileExtensions(String fileType) {
        switch (fileType.toLowerCase()) {
            case "images":
                return Arrays.asList(getAllowedImageTypes().split(","));
            case "documents":
                return Arrays.asList(getAllowedDocumentTypes().split(","));
            case "videos":
                return Arrays.asList(getAllowedVideoTypes().split(","));
            default:
                throw new BusinessException("不支持的文件类型: " + fileType);
        }
    }
    
    /**
     * 验证文件类型
     */
    private void validateFileType(String fileName, String fileType) {
        if (!StringUtils.hasText(fileName)) {
            throw new BusinessException("文件名不能为空");
        }
        
        String extension = getFileExtension(fileName);
        if (!StringUtils.hasText(extension)) {
            throw new BusinessException("文件没有扩展名");
        }
        
        List<String> allowedExtensions = getAllowedFileExtensions(fileType);
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的文件类型: ." + extension + "，允许的类型: " + String.join(", ", allowedExtensions));
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
    
    /**
     * 生成唯一文件名
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = UUID.randomUUID().toString().substring(0, 8);
        
        return timeStamp + "_" + randomStr + "." + extension;
    }
    
    /**
     * 获取上传目录
     */
    private Path getUploadPath(String fileType) {
        // 确保以/结尾
        String dir = getUploadDir();
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        
        // 根据文件类型创建子目录
        String subDir = fileType.toLowerCase() + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(dir + subDir);
        
        // 确保目录存在
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new BusinessException("创建上传目录失败: " + e.getMessage());
        }
        
        return uploadPath;
    }
    
    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isFileTypeAllowed(originalFilename)) {
            throw new IllegalArgumentException("不支持的文件类型");
        }
        
        // 生成日期目录，格式：yyyy/MM/dd
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 构建文件存储的完整路径：基础路径 + 文件类型目录 + 日期目录
        Path uploadDir = Paths.get(baseUploadPath, directory, dateDir);
        
        // 确保目录存在
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        // 生成唯一的文件名：UUID + 原始文件扩展名
        String fileExtension = getFileExtension(originalFilename);
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        
        // 保存文件
        Path filePath = uploadDir.resolve(newFileName);
        file.transferTo(filePath.toFile());
        
        // 返回相对路径，用于存储在数据库中
        // 格式：/directory/yyyy/MM/dd/filename.ext
        return "/" + directory + "/" + dateDir + "/" + newFileName;
    }
    
    @Override
    public List<String> uploadFiles(List<MultipartFile> files, String fileType) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new BusinessException("文件列表不能为空");
        }
        
        List<String> fileUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileUrl = uploadFile(file, fileType);
            fileUrls.add(fileUrl);
        }
        
        return fileUrls;
    }
    
    @Override
    public boolean deleteFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        }
        
        // 移除路径中的起始斜杠
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        
        // 构建完整的文件路径
        Path fullPath = Paths.get(baseUploadPath, filePath);
        
        try {
            return Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            log.error("删除文件失败：{}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getFileInfo(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件URL不能为空");
        }
        
        // 获取文件路径
        Path filePath = getPathFromUrl(fileUrl);
        
        if (!Files.exists(filePath)) {
            throw new BusinessException("文件不存在: " + fileUrl);
        }
        
        try {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("url", fileUrl);
            fileInfo.put("name", filePath.getFileName().toString());
            fileInfo.put("size", Files.size(filePath));
            fileInfo.put("lastModified", Files.getLastModifiedTime(filePath).toMillis());
            fileInfo.put("type", Files.probeContentType(filePath));
            fileInfo.put("extension", getFileExtension(filePath.getFileName().toString()));
            
            return fileInfo;
        } catch (IOException e) {
            throw new BusinessException("获取文件信息失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean fileExists(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return false;
        }
        
        // 获取文件路径
        Path filePath = getPathFromUrl(fileUrl);
        
        return Files.exists(filePath);
    }
    
    @Override
    public String generateThumbnail(String fileUrl, int width, int height) throws IOException {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件URL不能为空");
        }
        
        // 获取文件路径
        Path filePath = getPathFromUrl(fileUrl);
        
        if (!Files.exists(filePath)) {
            throw new BusinessException("文件不存在: " + fileUrl);
        }
        
        // 验证是否为图片
        String extension = getFileExtension(filePath.getFileName().toString());
        if (!Arrays.asList(getAllowedImageTypes().split(",")).contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的图片类型: ." + extension);
        }
        
        // 读取原图
        BufferedImage originalImage = ImageIO.read(filePath.toFile());
        
        // 创建缩略图
        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbnail.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        
        // 创建缩略图文件名
        String thumbnailFileName = "thumbnail_" + width + "x" + height + "_" + filePath.getFileName().toString();
        Path thumbnailPath = filePath.getParent().resolve(thumbnailFileName);
        
        // 保存缩略图
        ImageIO.write(thumbnail, extension, thumbnailPath.toFile());
        
        // 生成缩略图URL
        String fileType = fileUrl.substring(fileUrl.indexOf("/api/files/") + 11, fileUrl.indexOf("/", fileUrl.indexOf("/api/files/") + 11));
        return createFileUrl(fileType, thumbnailFileName);
    }
    
    @Override
    public String getPublicUrl(String filePath, long expirationSeconds) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        
        // 在实际项目中，这里可能需要生成带有访问令牌的URL
        // 例如，使用JWT生成一个包含文件路径和过期时间的令牌
        // 简化实现：直接返回文件访问URL
        if (!filePath.startsWith("/")) {
            filePath = "/" + filePath;
        }
        
        // 假设系统提供了一个/api/files端点用于文件访问
        return "/api/files" + filePath + "?t=" + System.currentTimeMillis();
    }
    
    /**
     * 从URL创建文件路径
     */
    private Path getPathFromUrl(String fileUrl) {
        // 假设URL格式为: http://domain/api/files/{fileType}/{yyyy/MM/dd}/{fileName}
        String path = fileUrl.substring(fileUrl.indexOf("/api/files/") + 11);
        
        // 确保上传目录以/结尾
        String dir = getUploadDir();
        if (!dir.endsWith("/")) {
            dir += "/";
        }
        
        return Paths.get(dir + path);
    }
    
    /**
     * 创建文件URL
     */
    private String createFileUrl(String fileType, String fileName) {
        String subDir = fileType.toLowerCase() + "/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/")
                .path(subDir + "/")
                .path(fileName)
                .toUriString();
    }
    
    @Override
    public boolean isFileTypeAllowed(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }
        
        String extension = getFileExtension(fileName).toLowerCase();
        
        // 获取配置的允许类型
        Set<String> allowedTypes = getAllowedFileTypes();
        
        return allowedTypes.contains(extension);
    }
    
    @Override
    public boolean isFileSizeAllowed(long fileSize, long maxSizeInMB) {
        // 转换MB为字节
        long maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        return fileSize <= maxSizeInBytes;
    }
    
    /**
     * 获取允许的文件类型集合
     */
    private Set<String> getAllowedFileTypes() {
        if (StringUtils.isEmpty(allowedFileTypes)) {
            return DEFAULT_ALLOWED_TYPES;
        }
        
        // 解析配置的允许类型
        String[] types = allowedFileTypes.split(",");
        Set<String> allowedTypes = new HashSet<>();
        for (String type : types) {
            String trimmedType = type.trim().toLowerCase();
            if (!StringUtils.isEmpty(trimmedType)) {
                allowedTypes.add(trimmedType);
            }
        }
        
        return allowedTypes.isEmpty() ? DEFAULT_ALLOWED_TYPES : allowedTypes;
    }
} 