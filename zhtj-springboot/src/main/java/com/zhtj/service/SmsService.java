package com.zhtj.service;

import java.util.Map;

/**
 * 短信服务接口
 */
public interface SmsService {
    
    /**
     * 发送短信验证码
     *
     * @param phoneNumber 手机号码
     * @param code 验证码
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String phoneNumber, String code);
    
    /**
     * 发送通知短信
     *
     * @param phoneNumber 手机号码
     * @param templateCode 模板代码
     * @param templateParams 模板参数
     * @return 是否发送成功
     */
    boolean sendNotification(String phoneNumber, String templateCode, Map<String, String> templateParams);
    
    /**
     * 批量发送短信
     *
     * @param phoneNumbers 手机号码列表
     * @param templateCode 模板代码
     * @param templateParams 模板参数
     * @return 是否发送成功
     */
    boolean sendBatchSms(String[] phoneNumbers, String templateCode, Map<String, String> templateParams);
    
    /**
     * 查询短信发送状态
     *
     * @param bizId 业务ID
     * @param phoneNumber 手机号码
     * @return 短信发送状态
     */
    Map<String, Object> querySendStatus(String bizId, String phoneNumber);
    
    /**
     * 获取短信剩余额度
     *
     * @return 剩余额度信息
     */
    Map<String, Object> queryBalance();
} 