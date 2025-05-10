package com.zhtj.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.zhtj.common.constant.RedisKeyConstant;
import com.zhtj.service.CaptchaService;
import com.zhtj.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 验证码服务实现类
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
    
    @Autowired
    private RedisService redisService;
    
    @Override
    public String generateCaptcha(String codeType, String key, long expireTime) {
        // 生成验证码（4位数字字母组合，带10条干扰线）
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 80, 4, 10);
        String code = captcha.getCode();
        
        // 存储到Redis，设置过期时间
        String redisKey = RedisKeyConstant.buildCaptchaKey(codeType, key);
        redisService.set(redisKey, code, expireTime);
        
        // 返回Base64编码的图片
        return captcha.getImageBase64();
    }
    
    @Override
    public boolean validateCaptcha(String codeType, String key, String captcha) {
        if (!StringUtils.hasText(captcha)) {
            return false;
        }
        
        String redisKey = RedisKeyConstant.buildCaptchaKey(codeType, key);
        Object codeObj = redisService.get(redisKey);
        
        if (codeObj == null) {
            return false;
        }
        
        String correctCaptcha = codeObj.toString();
        
        // 验证成功后删除验证码，防止重复使用
        redisService.del(redisKey);
        
        // 忽略大小写比较
        return captcha.equalsIgnoreCase(correctCaptcha);
    }
    
    @Override
    public void clearCaptcha(String codeType, String key) {
        String redisKey = RedisKeyConstant.buildCaptchaKey(codeType, key);
        redisService.del(redisKey);
    }
} 