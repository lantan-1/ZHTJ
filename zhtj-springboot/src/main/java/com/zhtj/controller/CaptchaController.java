package com.zhtj.controller;

import com.zhtj.common.api.Result;
import com.zhtj.dto.CaptchaParam;
import com.zhtj.service.CaptchaService;
import com.zhtj.util.IpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 验证码控制器
 */
@Slf4j
@RestController
@RequestMapping("/captcha")
@Tag(name = "验证码管理", description = "验证码生成和验证接口")
public class CaptchaController {
    
    @Autowired
    private CaptchaService captchaService;
    
    /**
     * 获取验证码
     *
     * @param param 验证码参数
     * @param request HTTP请求
     * @return Base64编码的验证码图片
     */
    @PostMapping
    @Operation(summary = "获取验证码", description = "获取Base64编码的验证码图片")
    public Result<Map<String, String>> getCaptcha(@Valid @RequestBody CaptchaParam param, HttpServletRequest request) {
        // 获取验证码类型，默认为login
        log.info("收到验证码请求: codeType={}, deviceId={}", param.getCodeType(), param.getDeviceId());
        String codeType = param.getCodeType();
        
        // 获取客户端IP作为标识
        String ip = IpUtil.getClientIp(request);
        
        // 设备ID可选，如果有则组合IP和设备ID作为标识
        String key = StringUtils.hasText(param.getDeviceId()) 
                ? ip + ":" + param.getDeviceId() 
                : ip;
        
        // 生成验证码，有效期5分钟(300秒)
        String base64Image = captchaService.generateCaptcha(codeType, key, 300);
        
        // 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("captchaImg", base64Image);
        result.put("key", key); // 可以选择是否返回key
        
        return Result.success(result, "验证码获取成功");
    }
    

    /**
     * 获取重置密码的验证码
     *
     * @param param 验证码参数
     * @param request HTTP请求
     * @return 验证结果
     */
    @PostMapping("/reset-password")
    @Operation(summary = "获取重置密码验证码", description = "获取用于重置密码的验证码")
    public Result<Map<String, String>> getResetPasswordCaptcha(@Valid @RequestBody CaptchaParam param, HttpServletRequest request) {
        // 获取客户端IP作为标识
        String ip = IpUtil.getClientIp(request);
        
        // 设备ID可选，如果有则组合IP和设备ID作为标识
        String key = StringUtils.hasText(param.getDeviceId()) 
                ? ip + ":" + param.getDeviceId() 
                : ip;
        
        // 生成验证码，有效期10分钟(600秒)
        String base64Image = captchaService.generateCaptcha("resetPassword", key, 600);
        
        // 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("captchaImg", base64Image);
        result.put("key", key);
        
        return Result.success(result, "验证码获取成功");
    }
} 