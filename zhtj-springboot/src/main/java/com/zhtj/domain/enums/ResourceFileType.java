package com.zhtj.domain.enums;

import lombok.Getter;
import java.util.Arrays;
import java.util.List;

/**
 * 学习资源文件类型枚举
 */
@Getter
public enum ResourceFileType {
    
    DOCUMENT("文档", Arrays.asList("DOC", "DOCX", "PDF", "PPT", "PPTX", "TXT", "XLS", "XLSX")),
    VIDEO("视频", Arrays.asList("MP4", "AVI", "MOV", "FLV", "MKV", "WMV")),
    AUDIO("音频", Arrays.asList("MP3", "WAV", "FLAC", "AAC", "OGG")),
    IMAGE("图片", Arrays.asList("JPG", "JPEG", "PNG", "GIF", "BMP", "WEBP")),
    ARCHIVE("压缩包", Arrays.asList("ZIP", "RAR", "7Z", "TAR", "GZ"));
    
    private final String name;
    private final List<String> formats;
    
    ResourceFileType(String name, List<String> formats) {
        this.name = name;
        this.formats = formats;
    }
    
    /**
     * 根据文件格式获取文件类型
     */
    public static ResourceFileType getByFormat(String format) {
        if (format == null) {
            return null;
        }
        
        String upperFormat = format.toUpperCase();
        
        for (ResourceFileType fileType : values()) {
            if (fileType.getFormats().contains(upperFormat)) {
                return fileType;
            }
        }
        return null;
    }
    
    /**
     * 获取文件类型的代码
     */
    public String getCode() {
        return name();
    }
    
    /**
     * 获取指定文件类型包含的文件格式列表
     */
    public static List<String> getFormatsByType(String typeCode) {
        try {
            ResourceFileType fileType = ResourceFileType.valueOf(typeCode);
            return fileType.getFormats();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
} 