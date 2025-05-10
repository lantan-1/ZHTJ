package com.zhtj.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件工具类
 */
@Component
@Slf4j
public class FileUtils {
    
    /**
     * 上传文件
     * 
     * @param file 文件
     * @param uploadDir 上传目录
     * @param fileName 文件名
     * @return 文件路径
     * @throws IOException IO异常
     */
    public String uploadFile(MultipartFile file, String uploadDir, String fileName) throws IOException {
        // 创建目录
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 保存文件
        Path path = Paths.get(uploadDir, fileName);
        Files.write(path, file.getBytes());
        
        // 返回相对路径
        return "/api" + uploadDir.substring(uploadDir.indexOf("/")) + "/" + fileName;
    }
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否成功
     */
    public boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            }
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: {}", filePath, e);
            return false;
        }
    }
} 