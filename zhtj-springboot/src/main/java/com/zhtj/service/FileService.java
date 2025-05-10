package com.zhtj.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件
     * 
     * @param file 文件对象
     * @param directory 存储目录类型（如images、documents、videos等）
     * @return 文件路径
     * @throws IOException 如果上传过程中发生IO异常
     */
    String uploadFile(MultipartFile file, String directory) throws IOException;
    
    /**
     * 上传多个文件
     * @param files 文件列表
     * @param fileType 文件类型（images/documents/videos）
     * @return 文件访问URL列表
     * @throws IOException 如果发生IO错误
     */
    List<String> uploadFiles(List<MultipartFile> files, String fileType) throws IOException;
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否成功
     */
    boolean deleteFile(String filePath);
    
    /**
     * 获取文件信息
     * @param fileUrl 文件URL
     * @return 文件信息
     */
    Map<String, Object> getFileInfo(String fileUrl);
    
    /**
     * 检查文件是否存在
     * @param fileUrl 文件URL
     * @return 文件是否存在
     */
    boolean fileExists(String fileUrl);
    
    /**
     * 生成文件的缩略图（仅支持图片）
     * @param fileUrl 文件URL
     * @param width 宽度
     * @param height 高度
     * @return 缩略图URL
     * @throws IOException 如果发生IO错误
     */
    String generateThumbnail(String fileUrl, int width, int height) throws IOException;
    
    /**
     * 获取文件的公共访问URL
     * 
     * @param filePath 文件路径
     * @param expirationSeconds 过期时间（秒）
     * @return 访问URL
     */
    String getPublicUrl(String filePath, long expirationSeconds);
    
    /**
     * 检查文件类型是否允许
     * 
     * @param fileName 文件名
     * @return 是否允许
     */
    boolean isFileTypeAllowed(String fileName);
    
    /**
     * 检查文件大小是否在限制范围内
     * 
     * @param fileSize 文件大小（字节）
     * @param maxSizeInMB 最大大小（MB）
     * @return 是否符合大小限制
     */
    boolean isFileSizeAllowed(long fileSize, long maxSizeInMB);
} 