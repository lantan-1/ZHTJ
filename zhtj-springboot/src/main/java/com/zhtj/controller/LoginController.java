package com.zhtj.controller;

import com.zhtj.common.api.Result;
import com.zhtj.service.CaptchaService;
import com.zhtj.service.UserService;
import com.zhtj.util.IpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 登录控制器
 */
@RestController
@RequestMapping
@Tag(name = "认证管理", description = "用户登录、注销和令牌刷新接口")
public class LoginController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private CaptchaService captchaService;
    
    /**
     * 用户登录
     * 
     * @param loginInfo 登录信息，包含用户证件号、密码和验证码
     * @return 登录结果(token和用户信息)
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用身份证号和密码登录系统")
    public Result<Map<String, Object>> login(@RequestBody Map<String, Object> loginInfo, HttpServletRequest request) {
        // 参数验证
        Map<String, Object> user = (Map<String, Object>) loginInfo.get("user");
        if (user == null) {
            return Result.validateFailed("用户信息不能为空");
        }
        
        String card = (String) user.get("card");
        String pwd = (String) user.get("pwd");
        String inputCaptcha = (String) loginInfo.get("captcha");
        String captchaKey = (String) loginInfo.get("captchaKey");
        
        if (!StringUtils.hasText(card)) {
            return Result.validateFailed("身份证号不能为空");
        }
        if (!StringUtils.hasText(pwd)) {
            return Result.validateFailed("密码不能为空");
        }
        
        // 验证码校验
        if (!StringUtils.hasText(inputCaptcha)) {
            return Result.validateFailed("验证码不能为空");
        }
        
        // 如果没有提供captchaKey，则使用客户端IP作为key
        if (!StringUtils.hasText(captchaKey)) {
            captchaKey = IpUtil.getClientIp(request);
        }
        
        // 验证验证码
        boolean captchaValid = captchaService.validateCaptcha("login", captchaKey, inputCaptcha);
        if (!captchaValid) {
            return Result.validateFailed("验证码错误或已过期");
        }
        
        try {
            Map<String, Object> loginResult = userService.login(card, pwd, null);
            return Result.success(loginResult, "登录成功");
        } catch (Exception e) {
            return Result.failed("登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 处理GET请求的登录页面访问
     * 主要用于处理退出登录后的重定向
     */
    @GetMapping("/login")
    @Operation(summary = "登录页面", description = "处理GET请求的登录页面访问")
    public Result<String> loginPage(@RequestParam(value = "logout", required = false) String logout) {
        if (logout != null) {
            return Result.success("您已成功退出登录");
        }
        return Result.success("请使用POST方法提交登录信息");
    }
    
    /**
     * 刷新令牌
     * 
     * @param authorization 包含旧令牌的请求头
     * @return 新令牌
     */
    @PostMapping("/auth/refresh")
    @Operation(summary = "刷新令牌", description = "使用旧令牌获取新令牌")
    public Result<Map<String, Object>> refreshToken(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        
        try {
            Map<String, Object> refreshResult = userService.refreshToken(token);
            return Result.success(refreshResult, "刷新成功");
        } catch (Exception e) {
            return Result.failed("刷新失败：" + e.getMessage());
        }
    }
    
    /**
     * 退出登录
     * 
     * @param authorization 包含令牌的请求头
     * @return 退出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "注销当前用户登录状态")
    public Result<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        // 如果提供了令牌，就使用令牌进行登出操作
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            boolean success = userService.logout(token);
            return Result.success(success, success ? "退出成功" : "退出失败");
        }

        // 如果没有提供令牌，直接返回成功（因为Spring Security已经处理了会话无效化）
        return Result.success(true, "退出成功");
    }
} 