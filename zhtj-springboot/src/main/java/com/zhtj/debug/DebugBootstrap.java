package com.zhtj.debug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 调试引导类，用于隔离问题
 * 仅扫描调试包，排除其他配置类
 */
@SpringBootApplication
@ComponentScan(
    basePackages = "com.zhtj.debug",
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.zhtj\\.config\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.zhtj\\.service\\..*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.zhtj\\.controller\\..*")
    }
)
public class DebugBootstrap {
    
    public static void main(String[] args) {
        try {
            SpringApplication app = new SpringApplication(DebugBootstrap.class);
            // 禁用所有自动配置
            app.setAdditionalProfiles("debug");
            app.run(args);
            System.out.println("调试引导程序启动成功");
        } catch (Exception e) {
            System.err.println("调试引导程序启动失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
} 