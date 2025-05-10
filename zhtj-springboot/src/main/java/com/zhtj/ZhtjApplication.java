package com.zhtj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.zhtj.config.FileProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileProperties.class})
@EnableScheduling
public class ZhtjApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ZhtjApplication.class, args);
    }
} 