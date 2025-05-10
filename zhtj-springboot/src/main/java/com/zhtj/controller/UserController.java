package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.common.api.Result;
import com.zhtj.domain.Organization;
import com.zhtj.domain.User;
import com.zhtj.service.OrganizationService;
import com.zhtj.service.UserService;
import com.zhtj.service.CaptchaService;
import com.zhtj.service.EmailService;
import com.zhtj.service.SmsService;
import com.zhtj.service.RedisService;
import com.zhtj.util.IpUtil;
import jakarta.servlet.http.HttpSession;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zhtj.service.PasswordResetService;
import com.zhtj.config.JwtConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
@Tag(name = "用户管理", description = "用户的增删改查接口")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private CaptchaService captchaService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private PasswordResetService passwordResetService;
    @Autowired
    private JwtConfig jwtConfig;


    /**
     * 用户注册
     * 
     * @param userData 用户数据
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户账号")
    public Result<Map<String, Object>> register(@RequestBody Map<String, Object> requestData, HttpServletRequest request) {
        // 获取用户数据
        User userData = null;
        String captcha = null;
        String captchaKey = null;
        
        try {
            userData = (User) requestData.get("user");
            captcha = (String) requestData.get("captcha");
            captchaKey = (String) requestData.get("captchaKey");
        } catch (Exception e) {
            return Result.validateFailed("请求数据格式错误");
        }
        
        if (userData == null) {
            return Result.validateFailed("用户数据不能为空");
        }
        
        // 参数验证
        if (!StringUtils.hasText(userData.getName())) {
            return Result.validateFailed("用户姓名不能为空");
        }
        if (!StringUtils.hasText(userData.getCard())) {
            return Result.validateFailed("身份证号不能为空");
        }
        if (!StringUtils.hasText(userData.getPwd())) {
            return Result.validateFailed("密码不能为空");
        }
        if (userData.getOrganization() == null) {
            return Result.validateFailed("组织ID不能为空");
        }
        
        // 验证码校验
        if (!StringUtils.hasText(captcha)) {
            return Result.validateFailed("验证码不能为空");
        }
        
        // 如果没有提供captchaKey，则使用客户端IP作为key
        if (!StringUtils.hasText(captchaKey)) {
            captchaKey = IpUtil.getClientIp(request);
        }
        
        // 验证验证码
        boolean captchaValid = captchaService.validateCaptcha("register", captchaKey, captcha);
        if (!captchaValid) {
            return Result.validateFailed("验证码错误或已过期");
        }
        
        // 检查组织是否存在
        Organization organization = organizationService.getById(userData.getOrganization());
        if (organization == null) {
            return Result.validateFailed("组织不存在");
        }
        
        try {
            // 创建用户
            boolean success = userService.createUser(userData);
            
            if (success) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", userData.getId());
                return Result.success(data, "注册成功");
            } else {
                return Result.failed("注册失败");
            }
        } catch (Exception e) {
            return Result.failed("注册失败: " + e.getMessage());
        }
    }



    // 更新用户密码
    @PutMapping("/pwd")
    @Operation(summary = "更新用户密码", description = "更新当前登录用户的密码")
    public Result<Boolean> updatePassword(@RequestHeader(value = "Authorization", required = false) String authorization, 
                                         @RequestBody Map<String, Object> passwordData) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        Integer userId = null;
        try {
            // 此处假设有一个方法可以从token获取用户ID
            userId = userService.getUserIdFromToken(token);
        } catch (Exception e) {
            return Result.unauthorized("无效的令牌: " + e.getMessage());
        }
        
        if (userId == null) {
            return Result.unauthorized("无法识别用户身份");
        }
        
        String oldPassword = (String) passwordData.get("old_password");
        String newPassword = (String) passwordData.get("new_password");
        
        if (!StringUtils.hasText(oldPassword)) {
            return Result.validateFailed("原密码不能为空");
        }
        
        if (!StringUtils.hasText(newPassword)) {
            return Result.validateFailed("新密码不能为空");
        }
        
        try {
            boolean result = userService.updatePassword(userId, oldPassword, newPassword);
            if (result) {
                // 密码更新成功后，应当使当前token失效，要求用户重新登录
                userService.logout(token);
                return Result.success(true, "密码更新成功，请重新登录");
            } else {
                return Result.failed("密码更新失败");
            }
        } catch (Exception e) {
            return Result.failed("密码更新失败: " + e.getMessage());
        }
    }

    // 删除用户
    @DeleteMapping("/current")
    @Operation(summary = "删除当前用户", description = "删除当前登录用户的账号")
    public Result<Boolean> deleteCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        Integer userId = null;
        try {
            userId = userService.getUserIdFromToken(token);
        } catch (Exception e) {
            return Result.unauthorized("无效的令牌: " + e.getMessage());
        }
        
        if (userId == null) {
            return Result.unauthorized("无法识别用户身份");
        }
        
        try {
            boolean result = userService.deleteUser(userId);
            if (result) {
                // 删除成功后注销登录
                userService.logout(token);
                return Result.success(true, "账号删除成功");
            } else {
                return Result.failed("账号删除失败");
            }
        } catch (Exception e) {
            return Result.failed("账号删除失败: " + e.getMessage());
        }
    }


    // 更新个人信息
    @PutMapping("/profile")
    @Operation(summary = "更新个人信息", description = "更新当前登录用户的个人信息")
    public Result<Boolean> updateProfile(@RequestHeader(value = "Authorization", required = false) String authorization,
                                        @RequestBody User userData) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        Integer userId = null;
        try {
            userId = userService.getUserIdFromToken(token);
        } catch (Exception e) {
            return Result.unauthorized("无效的令牌: " + e.getMessage());
        }
        
        if (userId == null) {
            return Result.unauthorized("无法识别用户身份");
        }
        
        // 获取当前用户信息，确保只更新允许的字段
        User existingUser = userService.getById(userId);
        if (existingUser == null) {
            return Result.failed("未找到用户信息");
        }
        
        // 设置用户ID，确保只能修改自己的信息
        userData.setId(userId);
        
        // 保留不允许修改的敏感字段
        userData.setName(existingUser.getName());        // 保留姓名
        userData.setGender(existingUser.getGender());    // 保留性别
        userData.setCard(null);                          // 身份证号不允许修改
        userData.setPwd(null);                           // 密码应通过专门的接口修改
        userData.setStatus(null);                        // 状态不允许用户自己修改
        
        try {
            boolean result = userService.updateUser(userData);
            return Result.success(result, result ? "个人信息更新成功" : "个人信息更新失败");
        } catch (Exception e) {
            return Result.failed("个人信息更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取组织成员列表
     * 
     * @param authorization 包含令牌的请求头
     * @param organizationId 组织ID
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param name 用户姓名，可选
     * @return 组织成员列表
     */
    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "获取组织成员", description = "获取指定组织的所有成员")
    public Result<Map<String, Object>> getOrganizationMembers(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Integer organizationId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        if (!userService.validateToken(token)) {
            return Result.unauthorized("无效的令牌");
        }
        
        try {
            // 创建分页参数
            Page<User> pageParam = new Page<>(page, size);
            // 调用服务层方法获取分页数据
            IPage<User> pageResult = userService.getUserPage(pageParam, name, organizationId);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("total", pageResult.getTotal());
            result.put("list", pageResult.getRecords());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.failed("获取组织成员失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     * 
     * @param authorization Bearer token
     * @return 当前用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<Map<String, Object>> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        log.debug("获取用户信息，token前10位: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
        
        Integer userId = null;
        try {
            // 尝试从token获取用户ID
            userId = userService.getUserIdFromToken(token);
            log.debug("从token中解析出的用户ID: {}", userId);
            
            // 如果userId为null，返回错误信息
            if (userId == null) {
                log.warn("从token中解析用户ID失败，无法获取用户信息");
                return Result.unauthorized("无法识别用户身份，请重新登录");
            }
            
            // 获取用户信息
            log.debug("开始查询用户ID为{}的用户信息", userId);
            User user = userService.getUserById(userId);
            if (user == null) {
                log.warn("未找到ID为{}的用户信息", userId);
                return Result.failed("未找到用户信息");
            }
            
            log.debug("成功获取到用户信息: {}", user.getName());
            
            // 出于安全考虑，不返回密码
            user.setPwd(null);
            
            // 获取用户所属组织信息
            Organization organization = null;
            if (user.getOrganization() != null) {
                organization = organizationService.getById(user.getOrganization());
            }
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("user", user);
            
            // 添加组织信息
            if (organization != null) {
                result.put("org_name", organization.getName());
                result.put("full_name", organization.getFullName());
                result.put("org_type", organization.getOrgType());
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.failed("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 用户登录
     * 
     * @param loginInfo 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用身份证号和密码登录系统")
    public Result<Map<String, Object>> login(@RequestBody Map<String, Object> loginInfo) {
        // 参数验证
        Map<String, Object> user = (Map<String, Object>) loginInfo.get("user");
        if (user == null) {
            return Result.validateFailed("用户信息不能为空");
        }
        
        String card = (String) user.get("card");
        String pwd = (String) user.get("pwd");
        String captcha = (String) loginInfo.get("captcha");
        
        if (!StringUtils.hasText(card)) {
            return Result.validateFailed("身份证号不能为空");
        }
        if (!StringUtils.hasText(pwd)) {
            return Result.validateFailed("密码不能为空");
        }
        
        try {
            Map<String, Object> loginResult = userService.login(card, pwd, captcha);
            return Result.success(loginResult, "登录成功");
        } catch (Exception e) {
            return Result.failed("登录失败：" + e.getMessage());
        }
    }
    
    /**
     * 刷新令牌
     * 
     * @param request 请求信息
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
     * 获取用户列表
     * 
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param name 用户姓名，可选
     * @param organization 组织ID，可选
     * @return 用户列表和总数
     */
    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，可根据姓名、组织筛选")
    public Result<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer organization) {
        
        Page<User> pageParam = new Page<>(page, size);
        IPage<User> pageResult = userService.getUserPage(pageParam, name, organization);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return Result.success(result);
    }
    
    /**
     * 获取用户详情
     * 
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "根据ID获取用户详细信息")
    public Result<User> getUserDetail(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        // 出于安全考虑，不返回密码
        user.setPwd(null);
        return Result.success(user);
    }
    
    /**
     * 创建用户
     * 
     * @param user 用户信息
     * @return 用户ID
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public Result<Map<String, Object>> createUser(@RequestBody User user) {
        // 参数验证
        if (!StringUtils.hasText(user.getName())) {
            return Result.validateFailed("用户姓名不能为空");
        }
        if (!StringUtils.hasText(user.getCard())) {
            return Result.validateFailed("身份证号不能为空");
        }
        if (!StringUtils.hasText(user.getPwd())) {
            return Result.validateFailed("密码不能为空");
        }
        if (user.getOrganization() == null) {
            return Result.validateFailed("组织ID不能为空");
        }
        
        boolean success = userService.createUser(user);
        
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            return Result.success(data, "创建用户成功");
        } else {
            return Result.failed("创建用户失败");
        }
    }
    
    /**
     * 更新用户
     * 
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    public Result<Boolean> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.updateUser(user);
        return Result.success(success, success ? "更新用户成功" : "更新用户失败");
    }
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户")
    public Result<Boolean> deleteUser(@PathVariable Integer id) {
        boolean success = userService.deleteUser(id);
        return Result.success(success, success ? "删除用户成功" : "删除用户失败");
    }

    /**
     * 用户退出登录
     * 
     * @param authorization 包含令牌的请求头
     * @return 退出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "退出当前登录状态")
    public Result<Boolean> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        
        try {
            boolean result = userService.logout(token);
            return Result.success(result, "退出登录成功");
        } catch (Exception e) {
            return Result.failed("退出登录失败: " + e.getMessage());
        }
    }

    // 添加新的API端点，专门用于获取用户个人设置信息
    /**
     * 获取用户个人设置信息
     * 
     * @param authorization 包含令牌的请求头
     * @return 用户个人设置相关信息
     */
    @GetMapping("/settings")
    @Operation(summary = "获取用户个人设置信息", description = "获取当前登录用户的个人设置相关信息")
    public Result<Map<String, Object>> getUserSettings(@RequestHeader(value = "Authorization", required = false) String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        Integer userId = null;
        try {
            userId = userService.getUserIdFromToken(token);
        } catch (Exception e) {
            return Result.unauthorized("无效的令牌: " + e.getMessage());
        }
        
        if (userId == null) {
            return Result.unauthorized("无法识别用户身份");
        }
        
        try {
            User user = userService.getById(userId);
            if (user == null) {
                return Result.failed("用户不存在");
            }
            
            // 构建只包含设置页面需要的字段的响应
            Map<String, Object> data = new HashMap<>();
            
            // 用户基本信息
            data.put("id", user.getId());
            data.put("name", user.getName());
            
            // 联系信息
            data.put("phone", user.getPhone());
            data.put("email", user.getEmail());
            
            // 教育和工作信息
            data.put("education_level", user.getEducationLevel());
            data.put("education_status", user.getEducationStatus());
            data.put("political_status", user.getPoliticalStatus());
            data.put("occupation", user.getOccupation());
            
            // 团组织相关信息
            data.put("join_league_date", user.getJoinLeagueDate());
            data.put("league_position", user.getLeaguePosition());
            
            // 添加其他可能在数据库中存在的字段
            data.put("ethnic", user.getEthnic());
            data.put("gender", user.getGender());
            
            // 添加缺少的字段
            data.put("join_party_date", user.getJoinPartyDate());
            data.put("work_unit", user.getWorkUnit()); 
            data.put("address", user.getAddress());
            data.put("qq", user.getQq());
            data.put("wechat", user.getWechat());
            data.put("weibo", user.getWeibo());
            
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取用户设置信息失败", e);
            return Result.failed("获取用户设置信息失败: " + e.getMessage());
        }
    }

    /**
     * 发送重置密码验证码
     * @param request 请求
     * @param requestData 请求数据
     * @return 结果
     */
    @PostMapping("/reset-password/send-code")
    @Operation(summary = "发送重置密码验证码", description = "通过邮箱或手机号发送重置密码验证码，支持防暴力破解和冷却期")
    public Result<Map<String, Object>> sendResetPasswordCode(HttpServletRequest request, @RequestBody Map<String, Object> requestData) {
        String card = (String) requestData.get("card");
        String type = (String) requestData.get("type"); // email或sms
        
        if (!StringUtils.hasText(card)) {
            return Result.validateFailed("身份证号不能为空");
        }
        
        if (!StringUtils.hasText(type) || (!type.equals("email") && !type.equals("sms"))) {
            return Result.validateFailed("验证类型必须为email或sms");
        }
        
        try {
            // 获取客户端IP
            String clientIp = IpUtil.getClientIp(request);
            
            // 调用密码重置服务发送验证码
            Map<String, Object> data = passwordResetService.sendResetPasswordCode(card, type, clientIp);
            
            return Result.success(data, "email".equals(type) ? "验证码已发送至邮箱" : "验证码已发送至手机");
        } catch (IllegalArgumentException e) {
            return Result.validateFailed(e.getMessage());
        } catch (Exception e) {
            log.error("发送重置密码验证码失败: {}", e.getMessage(), e);
            return Result.failed("发送验证码失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证重置密码验证码并重置密码
     * @param requestData 请求数据
     * @return 结果
     */
    @PostMapping("/reset-password/verify")
    @Operation(summary = "验证重置密码验证码并重置密码", description = "验证重置密码验证码并重置密码，支持密码强度校验，成功后发送通知")
    public Result<Map<String, Object>> verifyResetPasswordCode(@RequestBody Map<String, Object> requestData) {
        String card = (String) requestData.get("card");
        String type = (String) requestData.get("type"); // email或sms
        String code = (String) requestData.get("code");
        String newPassword = (String) requestData.get("newPassword");
        
        if (!StringUtils.hasText(card)) {
            return Result.validateFailed("身份证号不能为空");
        }
        
        if (!StringUtils.hasText(type) || (!type.equals("email") && !type.equals("sms"))) {
            return Result.validateFailed("验证类型必须为email或sms");
        }
        
        if (!StringUtils.hasText(code)) {
            return Result.validateFailed("验证码不能为空");
        }
        
        if (!StringUtils.hasText(newPassword)) {
            return Result.validateFailed("新密码不能为空");
        }
        
        if (newPassword.length() < 6) {
            return Result.validateFailed("密码长度不能小于6位");
        }
        
        try {
            // 调用密码重置服务验证验证码并重置密码
            Map<String, Object> data = passwordResetService.verifyCodeAndResetPassword(card, type, code, newPassword);
            
            return Result.success(data, "密码重置成功");
        } catch (IllegalArgumentException e) {
            return Result.validateFailed(e.getMessage());
        } catch (Exception e) {
            log.error("重置密码失败: {}", e.getMessage(), e);
            return Result.failed("重置密码失败: " + e.getMessage());
        }
    }

    /**
     * 激活用户
     * 
     * @param userId 需要激活的用户ID
     * @return 激活结果
     */
    @PostMapping("/activate/{userId}")
    @Operation(summary = "激活用户", description = "激活指定用户账号（仅管理员可用）")
    public Result<Boolean> activateUser(
            @PathVariable Integer userId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未授权操作");
        }
        
        try {
            String token = authorization.substring(7);
            Integer adminId = jwtConfig.getUserIdFromToken(token);
            
            if (adminId == null) {
                return Result.unauthorized("无效的用户令牌");
            }
            
            boolean success = userService.activateUser(userId, adminId);
            if (success) {
                return Result.success(true, "用户激活成功");
            } else {
                return Result.failed("用户激活失败");
            }
        } catch (Exception e) {
            log.error("用户激活失败: {}", e.getMessage(), e);
            return Result.failed("用户激活失败: " + e.getMessage());
        }
    }

    /**
     * 获取待激活用户列表
     * 
     * @param authorization 授权信息
     * @param page 页码
     * @param size 每页大小
     * @param name 用户名（可选）
     * @param status 状态（可选，默认为未激活）
     * @return 待激活用户列表
     */
    @GetMapping("/pending")
    @Operation(summary = "获取待激活用户列表", description = "获取当前组织及其子组织中未激活的用户列表，支持分页和搜索")
    public Result<Map<String, Object>> getPendingUsers(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status) {

        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            return Result.unauthorized("未提供有效令牌");
        }
        
        String token = authorization.substring(7);
        Integer userId = null;
        try {
            userId = userService.getUserIdFromToken(token);
        } catch (Exception e) {
            return Result.unauthorized("无效的令牌: " + e.getMessage());
        }
        
        if (userId == null) {
            return Result.unauthorized("无法识别用户身份");
        }
        
        try {
            // 获取当前用户
            User currentUser = userService.getById(userId);
            if (currentUser == null) {
                return Result.failed("用户不存在");
            }
            
            // 获取组织ID
            Integer organizationId = currentUser.getOrganization();
            if (organizationId == null) {
                return Result.failed("用户没有所属组织");
            }
            
            // 获取未激活用户列表
            Page<User> pageParam = new Page<>(page, size);
            IPage<User> userPage = userService.getPendingUsers(pageParam, organizationId, name, status);
            
            // 构建响应结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", userPage.getRecords());
            result.put("total", userPage.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取待激活用户列表失败", e);
            return Result.failed("获取待激活用户列表失败: " + e.getMessage());
        }
    }
} 