package com.zhtj.service;

/**
 * 验证码服务接口
 */
public interface CaptchaService {
    
    /**
     * 生成验证码并存储到Redis
     * 
     * @param codeType 验证码类型（如：login, register, resetPassword等）
     * @param key 用于标识验证码所属的对象（如：IP地址，用户ID等）
     * @param expireTime 过期时间（秒）
     * @return Base64编码的验证码图片
     */
    String generateCaptcha(String codeType, String key, long expireTime);
    
    /**
     * 验证验证码
     * 
     * @param codeType 验证码类型
     * @param key 用于标识验证码所属的对象
     * @param captcha 用户输入的验证码
     * @return 验证结果
     */
    boolean validateCaptcha(String codeType, String key, String captcha);
    
    /**
     * 清除验证码
     * 
     * @param codeType 验证码类型
     * @param key 用于标识验证码所属的对象
     */
    void clearCaptcha(String codeType, String key);
} 