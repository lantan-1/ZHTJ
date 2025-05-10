package com.zhtj.service.impl;

import com.zhtj.common.exception.BusinessException;
import com.zhtj.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 邮件服务实现类
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired(required = false)
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public boolean sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            logger.info("简单邮件已发送至: {}", to);
            return true;
        } catch (Exception e) {
            logger.error("发送简单邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送邮件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendSimpleEmail(String to, String cc, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setCc(cc);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            logger.info("带抄送的简单邮件已发送至: {}, 抄送: {}", to, cc);
            return true;
        } catch (Exception e) {
            logger.error("发送带抄送的简单邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送邮件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            logger.info("HTML邮件已发送至: {}", to);
            return true;
        } catch (MessagingException e) {
            logger.error("发送HTML邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送HTML邮件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendHtmlEmail(String to, String cc, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setCc(cc);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
            logger.info("带抄送的HTML邮件已发送至: {}, 抄送: {}", to, cc);
            return true;
        } catch (MessagingException e) {
            logger.error("发送带抄送的HTML邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送HTML邮件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendAttachmentEmail(String to, String subject, String content, String filePath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = file.getFilename();
            helper.addAttachment(fileName, file);

            mailSender.send(message);
            logger.info("带附件的邮件已发送至: {}, 附件: {}", to, filePath);
            return true;
        } catch (MessagingException e) {
            logger.error("发送带附件的邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送带附件的邮件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendAttachmentEmail(String to, String subject, String content, List<String> filePaths) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            for (String filePath : filePaths) {
                FileSystemResource file = new FileSystemResource(new File(filePath));
                String fileName = file.getFilename();
                helper.addAttachment(fileName, file);
            }

            mailSender.send(message);
            logger.info("带多个附件的邮件已发送至: {}, 附件数量: {}", to, filePaths.size());
            return true;
        } catch (MessagingException e) {
            logger.error("发送带多个附件的邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送带多个附件的邮件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendInlineResourceEmail(String to, String subject, String content, Map<String, File> imageMap) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            for (Map.Entry<String, File> entry : imageMap.entrySet()) {
                String contentId = entry.getKey();
                File file = entry.getValue();
                helper.addInline(contentId, file);
            }

            mailSender.send(message);
            logger.info("带内联资源的邮件已发送至: {}, 内联资源数量: {}", to, imageMap.size());
            return true;
        } catch (MessagingException e) {
            logger.error("发送带内联资源的邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送带内联资源的邮件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendTemplateEmail(String to, String subject, String template, Map<String, Object> parameters) {
        if (templateEngine == null) {
            throw new BusinessException("模板引擎未配置，无法发送模板邮件");
        }

        try {
            Context context = new Context();
            if (parameters != null) {
                parameters.forEach(context::setVariable);
            }
            String emailContent = templateEngine.process(template, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailContent, true);

            mailSender.send(message);
            logger.info("模板邮件已发送至: {}, 使用模板: {}", to, template);
            return true;
        } catch (Exception e) {
            logger.error("发送模板邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送模板邮件失败: " + e.getMessage());
        }
    }

    @Override
    public boolean sendBatchSimpleEmail(List<String> tos, String subject, String content) {
        try {
            for (String to : tos) {
                sendSimpleEmail(to, subject, content);
            }
            logger.info("批量邮件已发送至 {} 个收件人", tos.size());
            return true;
        } catch (Exception e) {
            logger.error("发送批量邮件时发生错误: {}", e.getMessage());
            throw new BusinessException("发送批量邮件失败: " + e.getMessage());
        }
    }
} 