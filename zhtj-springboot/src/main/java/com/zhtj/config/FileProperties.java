package com.zhtj.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    private String uploadDir;
    private String maxFileSize;
    private Map<String, String> allowedFileTypes;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Map<String, String> getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(Map<String, String> allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }
} 