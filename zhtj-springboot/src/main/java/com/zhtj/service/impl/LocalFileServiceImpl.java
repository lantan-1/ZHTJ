package com.zhtj.service.impl;

import com.zhtj.common.exception.BusinessException;
import com.zhtj.config.FileProperties;
import com.zhtj.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * 本地文件存储服务实现类
 * 仅在minio.enabled=false时激活
 */
@Service
@Primary
@ConditionalOnProperty(name = "minio.enabled", havingValue = "false", matchIfMissing = true)
public class LocalFileServiceImpl implements FileService {

    @Autowired
    private FileProperties fileProperties;
    
    @Value("${file.local-storage.base-path:${user.home}/zhtj-storage}")
    private String baseStoragePath;
    
    @Value("${file.local-storage.public-url-prefix:http://localhost:8080/api/files}")
    private String publicUrlPrefix;

    private String getAllowedImageTypes() {
        return fileProperties.getAllowedFileTypes().get("images");
    }

    private String getAllowedDocumentTypes() {
        return fileProperties.getAllowedFileTypes().get("documents");
    }

    private String getAllowedVideoTypes() {
        return fileProperties.getAllowedFileTypes().get("videos");
    }

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
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
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
     * 获取对象存储路径
     */
    private String getObjectPath(String fileType, String fileName) {
        // 不再创建子目录，直接返回文件名
        return fileName;
    }

    /**
     * 确保目录存在
     */
    private void ensureDirectoryExists(String directoryPath) {
        Path directory = Paths.get(directoryPath);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new BusinessException("创建目录失败: " + e.getMessage());
            }
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String fileType) throws IOException {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 验证文件类型
        String originalFileName = file.getOriginalFilename();
        validateFileType(originalFileName, fileType);

        // 生成唯一文件名
        String uniqueFileName = generateUniqueFileName(originalFileName);

        // 构建对象路径
        String relativePath = getObjectPath(fileType, uniqueFileName);
        
        // 检查是否存储到study-resources目录
        boolean isStudyResource = baseStoragePath.endsWith("study-resources");
        
        // 如果是study-resources目录，则直接使用根路径
        String absolutePath;
        if (isStudyResource) {
            absolutePath = baseStoragePath + File.separator + uniqueFileName;
            relativePath = uniqueFileName; // 更新相对路径为仅文件名
        } else {
            absolutePath = baseStoragePath + File.separator + relativePath.replace('/', File.separatorChar);
        }
        
        // 确保目录存在
        String directory = absolutePath.substring(0, absolutePath.lastIndexOf(File.separator));
        ensureDirectoryExists(directory);

        try {
            // 保存文件到本地
            Path targetLocation = Paths.get(absolutePath);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回相对路径作为URL
            return relativePath;
        } catch (Exception e) {
            throw new BusinessException("上传文件失败: " + e.getMessage());
        }
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
    public boolean deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件路径不能为空");
        }

        try {
            String absolutePath = baseStoragePath + File.separator + fileUrl.replace('/', File.separatorChar);
            Path targetPath = Paths.get(absolutePath);
            return Files.deleteIfExists(targetPath);
        } catch (Exception e) {
            throw new BusinessException("删除文件失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getFileInfo(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件路径不能为空");
        }

        try {
            String absolutePath = baseStoragePath + File.separator + fileUrl.replace('/', File.separatorChar);
            Path targetPath = Paths.get(absolutePath);
            
            if (!Files.exists(targetPath)) {
                throw new BusinessException("文件不存在");
            }
            
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("url", fileUrl);
            fileInfo.put("name", fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
            fileInfo.put("size", Files.size(targetPath));
            fileInfo.put("lastModified", Files.getLastModifiedTime(targetPath).toMillis());
            fileInfo.put("type", Files.probeContentType(targetPath));
            fileInfo.put("extension", getFileExtension(fileUrl));

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

        try {
            String absolutePath = baseStoragePath + File.separator + fileUrl.replace('/', File.separatorChar);
            Path targetPath = Paths.get(absolutePath);
            return Files.exists(targetPath);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String generateThumbnail(String fileUrl, int width, int height) throws IOException {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件路径不能为空");
        }
        
        String absolutePath = baseStoragePath + File.separator + fileUrl.replace('/', File.separatorChar);
        Path originalPath = Paths.get(absolutePath);
        
        if (!Files.exists(originalPath)) {
            throw new BusinessException("文件不存在");
        }
        
        // 检查文件是否为图片
        String extension = getFileExtension(fileUrl);
        if (!getAllowedFileExtensions("images").contains(extension)) {
            throw new BusinessException("不支持的文件类型，仅支持图片格式");
        }
        
        // 生成缩略图文件名和路径
        String thumbFileName = "thumb_" + width + "x" + height + "_" + fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        String thumbFileDir = fileUrl.substring(0, fileUrl.lastIndexOf("/"));
        String thumbRelativePath = thumbFileDir + "/thumbnails/" + thumbFileName;
        String thumbAbsolutePath = baseStoragePath + File.separator + thumbRelativePath.replace('/', File.separatorChar);
        
        // 确保缩略图目录存在
        String directory = thumbAbsolutePath.substring(0, thumbAbsolutePath.lastIndexOf(File.separator));
        ensureDirectoryExists(directory);
        
        // 生成缩略图
        try {
            BufferedImage originalImage = ImageIO.read(originalPath.toFile());
            BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            
            Graphics2D g = thumbnail.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();
            
            ImageIO.write(thumbnail, extension, new File(thumbAbsolutePath));
            
            return thumbRelativePath;
        } catch (Exception e) {
            throw new BusinessException("生成缩略图失败: " + e.getMessage());
        }
    }

    @Override
    public String getPublicUrl(String fileUrl, long expireInSeconds) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件路径不能为空");
        }
        
        // 本地文件系统实现不支持过期链接
        // 直接返回公共URL前缀 + 文件路径
        return publicUrlPrefix + "/" + fileUrl;
    }

    @Override
    public boolean isFileSizeAllowed(long fileSize, long maxSizeInMB) {
        // 转换MB为字节
        long maxSizeInBytes = maxSizeInMB * 1024 * 1024;
        return fileSize <= maxSizeInBytes;
    }

    @Override
    public boolean isFileTypeAllowed(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return false;
        }
        
        String extension = getFileExtension(fileName);
        
        // 合并所有允许的文件类型
        List<String> allowedImageTypes = Arrays.asList(getAllowedImageTypes().split(","));
        List<String> allowedDocumentTypes = Arrays.asList(getAllowedDocumentTypes().split(","));
        List<String> allowedVideoTypes = Arrays.asList(getAllowedVideoTypes().split(","));
        
        // 检查文件扩展名是否在允许列表中
        return allowedImageTypes.contains(extension) || 
               allowedDocumentTypes.contains(extension) || 
               allowedVideoTypes.contains(extension);
    }
} 