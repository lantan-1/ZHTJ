package com.zhtj.service;

import java.util.Map;

/**
 * 密码重置服务接口
 * 处理忘记密码相关的业务逻辑
 */
public interface PasswordResetService {
    
    /**
     * 发送重置密码验证码
     * @param card 身份证号
     * @param type 验证类型 (email或sms)
     * @param clientIp 客户端IP地址
     * @return 结果信息，包含掩码处理后的邮箱或手机号
     * @throws Exception 发送失败时抛出异常
     */
    Map<String, Object> sendResetPasswordCode(String card, String type, String clientIp) throws Exception;
    
    /**
     * 验证重置密码验证码并重置密码
     * @param card 身份证号
     * @param type 验证类型 (email或sms)
     * @param code 验证码
     * @param newPassword 新密码
     * @return 结果信息
     * @throws Exception 验证失败或重置失败时抛出异常
     */
    Map<String, Object> verifyCodeAndResetPassword(String card, String type, String code, String newPassword) throws Exception;
} 