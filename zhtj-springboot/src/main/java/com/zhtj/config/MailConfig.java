package com.zhtj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 邮件配置类
 * 用于解决邮件服务器连接超时问题
 */
@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    /**
     * 自定义JavaMailSender配置，添加更多的超时设置
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        
        // 设置邮件属性
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        
        // 关键超时设置
        props.put("mail.smtp.connectiontimeout", "5000"); // 连接超时时间5秒
        props.put("mail.smtp.timeout", "10000"); // 套接字超时时间10秒
        props.put("mail.smtp.writetimeout", "10000"); // 写入超时时间10秒
        
        // 信任所有主机
        props.put("mail.smtp.ssl.trust", host);
        
        return mailSender;
    }
} 