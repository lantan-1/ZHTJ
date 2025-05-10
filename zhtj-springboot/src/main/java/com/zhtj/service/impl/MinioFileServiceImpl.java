package com.zhtj.service.impl;

import com.zhtj.common.exception.BusinessException;
import com.zhtj.config.FileProperties;
import com.zhtj.config.MinioConfig;
import com.zhtj.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Primary
@ConditionalOnProperty(name = "minio.enabled", havingValue = "true")
public class MinioFileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private FileProperties fileProperties;

    private String getBucketName() {
        return minioConfig.getBucketName();
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
     * 获取对象存储路径
     */
    private String getObjectPath(String fileType, String fileName) {
        String subDir = fileType.toLowerCase() + "/" 
                      + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return subDir + "/" + fileName;
    }

    /**
     * 初始化存储桶
     */
    private void initializeBucket() {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(getBucketName())
                    .build());

            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(getBucketName())
                        .build());
            }
        } catch (Exception e) {
            throw new BusinessException("初始化存储桶失败: " + e.getMessage());
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

        // 确保存储桶存在
        initializeBucket();

        // 构建对象路径
        String objectPath = getObjectPath(fileType, uniqueFileName);

        try {
            // 上传文件到MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(objectPath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            // 返回可访问的URL
            return objectPath;
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
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(fileUrl)
                    .build());
            return true;
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
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(fileUrl)
                    .build());

            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("url", fileUrl);
            fileInfo.put("name", fileUrl.substring(fileUrl.lastIndexOf("/") + 1));
            fileInfo.put("size", stat.size());
            fileInfo.put("lastModified", stat.lastModified().toInstant().toEpochMilli());
            fileInfo.put("type", stat.contentType());
            fileInfo.put("extension", getFileExtension(fileUrl));

            return fileInfo;
        } catch (Exception e) {
            throw new BusinessException("获取文件信息失败: " + e.getMessage());
        }
    }

    @Override
    public boolean fileExists(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return false;
        }

        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(fileUrl)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String generateThumbnail(String fileUrl, int width, int height) throws IOException {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件路径不能为空");
        }

        // 验证是否为图片
        String extension = getFileExtension(fileUrl);
        if (!Arrays.asList(getAllowedImageTypes().split(",")).contains(extension.toLowerCase())) {
            throw new BusinessException("不支持的图片类型: ." + extension);
        }

        try {
            // 读取原图
            GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(fileUrl)
                    .build());

            BufferedImage originalImage = ImageIO.read(response);

            // 创建缩略图
            BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = thumbnail.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();

            // 创建缩略图文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            String thumbnailFileName = "thumbnail_" + width + "x" + height + "_" + fileName;
            
            // 提取目录路径
            String directoryPath = fileUrl.substring(0, fileUrl.lastIndexOf("/") + 1);
            String thumbnailPath = directoryPath + thumbnailFileName;

            // 保存缩略图到临时文件
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, extension, baos);
            byte[] thumbnailBytes = baos.toByteArray();

            // 上传到MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(getBucketName())
                    .object(thumbnailPath)
                    .stream(new ByteArrayInputStream(thumbnailBytes), thumbnailBytes.length, -1)
                    .contentType("image/" + extension.toLowerCase())
                    .build());

            return thumbnailPath;
        } catch (Exception e) {
            throw new BusinessException("生成缩略图失败: " + e.getMessage());
        }
    }

    @Override
    public String getPublicUrl(String fileUrl, long expireInSeconds) {
        if (!StringUtils.hasText(fileUrl)) {
            throw new BusinessException("文件路径不能为空");
        }

        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(getBucketName())
                    .object(fileUrl)
                    .method(Method.GET)
                    .expiry((int) expireInSeconds, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            throw new BusinessException("获取公共URL失败: " + e.getMessage());
        }
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
        
        String extension = getFileExtension(fileName).toLowerCase();
        
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