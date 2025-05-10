package com.zhtj.service;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送简单文本邮件
     *
     * @param to      收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendSimpleEmail(String to, String subject, String content);
    
    /**
     * 发送带抄送的简单文本邮件
     *
     * @param to      收件人
     * @param cc      抄送人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendSimpleEmail(String to, String cc, String subject, String content);
    
    /**
     * 发送HTML格式邮件
     *
     * @param to      收件人
     * @param subject 邮件主题
     * @param content HTML内容
     * @return 是否发送成功
     */
    boolean sendHtmlEmail(String to, String subject, String content);
    
    /**
     * 发送带抄送的HTML格式邮件
     *
     * @param to      收件人
     * @param cc      抄送人
     * @param subject 邮件主题
     * @param content HTML内容
     * @return 是否发送成功
     */
    boolean sendHtmlEmail(String to, String cc, String subject, String content);
    
    /**
     * 发送带附件的邮件
     *
     * @param to       收件人
     * @param subject  邮件主题
     * @param content  邮件内容
     * @param filePath 附件路径
     * @return 是否发送成功
     */
    boolean sendAttachmentEmail(String to, String subject, String content, String filePath);
    
    /**
     * 发送带多个附件的邮件
     *
     * @param to        收件人
     * @param subject   邮件主题
     * @param content   邮件内容
     * @param filePaths 附件路径列表
     * @return 是否发送成功
     */
    boolean sendAttachmentEmail(String to, String subject, String content, List<String> filePaths);
    
    /**
     * 发送带图片内联的HTML邮件
     *
     * @param to       收件人
     * @param subject  邮件主题
     * @param content  HTML内容，需要包含 <img src="cid:imageId" />
     * @param imageMap 图片映射，key为图片ID，value为图片文件
     * @return 是否发送成功
     */
    boolean sendInlineResourceEmail(String to, String subject, String content, Map<String, File> imageMap);
    
    /**
     * 使用模板发送邮件
     *
     * @param to         收件人
     * @param subject    邮件主题
     * @param template   模板名称
     * @param parameters 模板参数
     * @return 是否发送成功
     */
    boolean sendTemplateEmail(String to, String subject, String template, Map<String, Object> parameters);
    
    /**
     * 批量发送简单邮件
     *
     * @param tos     收件人列表
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否全部发送成功
     */
    boolean sendBatchSimpleEmail(List<String> tos, String subject, String content);
} 