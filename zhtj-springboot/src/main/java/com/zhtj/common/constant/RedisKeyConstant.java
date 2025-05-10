package com.zhtj.common.constant;

/**
 * Redis键常量
 */
public class RedisKeyConstant {
    
    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha";
    
    /**
     * 登录验证码
     */
    public static final String CAPTCHA_LOGIN = CAPTCHA_PREFIX + ":login:%s";
    
    /**
     * 注册验证码
     */
    public static final String CAPTCHA_REGISTER = CAPTCHA_PREFIX + ":register:%s";
    
    /**
     * 重置密码验证码
     */
    public static final String CAPTCHA_RESET_PASSWORD = CAPTCHA_PREFIX + ":resetPassword:%s";
    
    /**
     * 短信验证码
     */
    public static final String SMS_CAPTCHA = "sms:captcha:%s";
    
    /**
     * 用户令牌
     */
    public static final String USER_TOKEN = "user:token:%s";
    
    /**
     * 用户信息
     */
    public static final String USER_INFO = "user:info:%s";
    
    /**
     * 组织信息
     */
    public static final String ORGANIZATION_INFO = "organization:info:%s";
    
    /**
     * 统计数据
     */
    public static final String STATISTICS = "statistics:%s";
    
    /**
     * 构建验证码Redis键
     * 
     * @param type 验证码类型
     * @param key 标识
     * @return Redis键
     */
    public static String buildCaptchaKey(String type, String key) {
        switch (type) {
            case "login":
                return String.format(CAPTCHA_LOGIN, key);
            case "register":
                return String.format(CAPTCHA_REGISTER, key);
            case "resetPassword":
                return String.format(CAPTCHA_RESET_PASSWORD, key);
            default:
                return CAPTCHA_PREFIX + ":" + type + ":" + key;
        }
    }
} 