package com.zhtj.service.impl;

import com.zhtj.domain.User;
import com.zhtj.service.EmailService;
import com.zhtj.service.PasswordResetService;
import com.zhtj.service.RedisService;
import com.zhtj.service.SmsService;
import com.zhtj.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 密码重置服务实现类
 */
@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    
    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    @Autowired
    private RedisService redisService;
    
    @Value("${reset.password.code.expire}")
    private long codeExpireSeconds;
    
    @Value("${reset.password.max-attempts}")
    private int maxAttempts;
    
    @Value("${reset.password.cooldown}")
    private long cooldownSeconds;
    
    @Value("${reset.password.strength.enabled}")
    private boolean passwordStrengthEnabled;
    
    @Value("${reset.password.strength.min-length}")
    private int minPasswordLength;
    
    @Value("${reset.password.strength.require-mixed}")
    private boolean requireMixedPassword;
    
    // 邮箱验证正则表达式
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
    
    // 手机号验证正则表达式（中国大陆手机号）
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^1[3-9]\\d{9}$");
    
    // 强密码正则表达式（至少包含一个大写字母、一个小写字母和一个数字）
    private static final Pattern STRONG_PASSWORD_PATTERN = 
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    
    @Override
    public Map<String, Object> sendResetPasswordCode(String card, String type, String clientIp) throws Exception {
        // 参数验证
        if (!StringUtils.hasText(card)) {
            throw new IllegalArgumentException("身份证号不能为空");
        }
        
        if (!StringUtils.hasText(type) || (!type.equals("email") && !type.equals("sms") && !type.equals("face"))) {
            throw new IllegalArgumentException("验证类型必须为email、sms或face");
        }
        
        // 防暴力破解 - 检查IP请求频率
        String ipAttemptsKey = "reset_password_ip_attempts_" + clientIp;
        Object ipAttempts = redisService.get(ipAttemptsKey);
        int ipAttemptCount = ipAttempts == null ? 0 : Integer.parseInt(ipAttempts.toString());
        
        if (ipAttemptCount >= maxAttempts) {
            throw new IllegalStateException("请求次数过多，请1小时后再试");
        }
        
        // 冷却期检查 - 防止频繁发送验证码
        String cooldownKey = "reset_password_cooldown_" + type + "_" + card;
        if (redisService.hasKey(cooldownKey)) {
            long remainingTime = redisService.getExpire(cooldownKey);
            throw new IllegalStateException("请求过于频繁，请" + remainingTime + "秒后再试");
        }
        
        // 查询用户信息
        User user = userService.getByCard(card);
        if (user == null) {
            // 即使用户不存在也增加尝试次数，防止枚举攻击
            redisService.set(ipAttemptsKey, String.valueOf(ipAttemptCount + 1), 3600); // 1小时内限制
            throw new IllegalArgumentException("该身份证号未注册");
        }
        
        // 生成6位随机验证码
        String code = generateVerificationCode();
        String redisKey = "reset_password_" + type + "_" + card;
        
        Map<String, Object> result = new HashMap<>();
        
        // 根据验证类型发送验证码
        if ("email".equals(type)) {
            // 检查用户是否有邮箱
            if (!StringUtils.hasText(user.getEmail())) {
                redisService.set(ipAttemptsKey, String.valueOf(ipAttemptCount + 1), 3600);
                throw new IllegalArgumentException("用户未设置邮箱");
            }
            
            // 验证邮箱格式
            if (!isValidEmail(user.getEmail())) {
                log.warn("用户邮箱格式不正确: {}", user.getEmail());
                redisService.set(ipAttemptsKey, String.valueOf(ipAttemptCount + 1), 3600);
                throw new IllegalArgumentException("邮箱格式不正确，请联系管理员更新您的邮箱");
            }
            
            // 发送邮件
            String emailContent = "尊敬的用户：您好！\n\n"
                    + "您正在申请重置密码，验证码为：" + code + "，有效期5分钟。\n\n"
                    + "如非本人操作，请忽略此邮件并立即修改密码。\n\n"
                    + "智慧团建系统";
            emailService.sendSimpleEmail(user.getEmail(), "智慧团建系统 - 重置密码验证码", emailContent);
            
            result.put("maskedEmail", maskEmail(user.getEmail()));
            log.info("向用户{}的邮箱{}发送重置密码验证码成功", user.getName(), maskEmail(user.getEmail()));
        } else {
            // 检查用户是否有手机号
            if (!StringUtils.hasText(user.getPhone())) {
                redisService.set(ipAttemptsKey, String.valueOf(ipAttemptCount + 1), 3600);
                throw new IllegalArgumentException("用户未设置手机号");
            }
            
            // 验证手机号格式
            if (!isValidPhone(user.getPhone())) {
                log.warn("用户手机号格式不正确: {}", user.getPhone());
                redisService.set(ipAttemptsKey, String.valueOf(ipAttemptCount + 1), 3600);
                throw new IllegalArgumentException("手机号格式不正确，请联系管理员更新您的手机号");
            }
            
            // 发送短信
            smsService.sendVerificationCode(user.getPhone(), code);
            
            result.put("maskedPhone", maskPhone(user.getPhone()));
            log.info("向用户{}的手机号{}发送重置密码验证码成功", user.getName(), maskPhone(user.getPhone()));
        }
        
        // 保存验证码到Redis，有效期5分钟
        redisService.set(redisKey, code, codeExpireSeconds);
        
        // 设置冷却期，防止频繁请求
        redisService.set(cooldownKey, "1", cooldownSeconds);
        
        // 增加IP尝试次数
        redisService.set(ipAttemptsKey, String.valueOf(ipAttemptCount + 1), 3600);
        
        return result;
    }
    
    @Override
    @Transactional
    public Map<String, Object> verifyCodeAndResetPassword(String card, String type, String code, String newPassword) throws Exception {
        // 验证参数
        if (!StringUtils.hasText(card)) {
            throw new IllegalArgumentException("身份证号不能为空");
        }
        
        if (!StringUtils.hasText(type) || (!type.equals("email") && !type.equals("sms") && !type.equals("face"))) {
            throw new IllegalArgumentException("验证类型必须为email、sms或face");
        }
        
        if (!"face".equals(type) && !StringUtils.hasText(code)) {
            throw new IllegalArgumentException("验证码不能为空");
        }
        
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        
        // 检查密码强度
        if (passwordStrengthEnabled) {
            if (newPassword.length() < minPasswordLength) {
                throw new IllegalArgumentException("密码长度不能小于" + minPasswordLength + "位");
            }
            
            if (requireMixedPassword && !isStrongPassword(newPassword)) {
                throw new IllegalArgumentException("密码必须包含大小写字母和数字");
            }
        } else {
            // 基本密码长度检查
            if (newPassword.length() < 6) {
                throw new IllegalArgumentException("密码长度不能小于6位");
            }
        }
        
        // 查询用户信息
        User user = userService.getByCard(card);
        if (user == null) {
            throw new IllegalArgumentException("该身份证号未注册");
        }
        
        // 验证验证码
        if (!"face".equals(type)) {
            String redisKey = "reset_password_" + type + "_" + card;
            Object storedCode = redisService.get(redisKey);
            if (storedCode == null) {
                throw new IllegalArgumentException("验证码已过期或不存在");
            }
            if (!code.equals(storedCode.toString())) {
                throw new IllegalArgumentException("验证码错误");
            }
            // 删除验证码
            redisService.del(redisKey);
        }
        // 重置密码
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setPwd(newPassword);
        boolean success = userService.updateUser(updatedUser);
        if (!success) {
            throw new RuntimeException("密码重置失败");
        }
        // 发送密码重置成功通知
        try {
            sendPasswordResetNotification(user, type);
        } catch (Exception e) {
            // 通知发送失败不影响主流程
            log.warn("发送密码重置通知失败: {}", e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        log.info("用户{}({})重置密码成功", user.getName(), card);
        return result;
    }
    
    /**
     * 发送密码重置成功通知
     * @param user 用户信息
     * @param type 通知类型(email或sms)
     */
    private void sendPasswordResetNotification(User user, String type) {
        if (("email".equals(type) || "face".equals(type)) && StringUtils.hasText(user.getEmail())) {
            String emailContent = "尊敬的用户：您好！\n\n"
                    + "您的智慧团建系统密码已成功重置。\n\n"
                    + "如非本人操作，请立即联系管理员。\n\n"
                    + "智慧团建系统";
            emailService.sendSimpleEmail(user.getEmail(), "智慧团建系统 - 密码重置成功", emailContent);
        } else if ("sms".equals(type) && StringUtils.hasText(user.getPhone())) {
            Map<String, String> templateParams = new HashMap<>();
            templateParams.put("name", user.getName());
            templateParams.put("system", "智慧团建系统");
            smsService.sendNotification(user.getPhone(), "SMS_298075331", templateParams);
        }
    }
    
    /**
     * 生成6位随机验证码
     * @return 验证码
     */
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 掩码邮箱
     * @param email 邮箱
     * @return 掩码后的邮箱
     */
    private String maskEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return "";
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        
        String prefix = email.substring(0, 3);
        String suffix = email.substring(atIndex);
        return prefix + "****" + suffix;
    }
    
    /**
     * 掩码手机号
     * @param phone 手机号
     * @return 掩码后的手机号
     */
    private String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() < 7) {
            return phone;
        }
        
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
    
    /**
     * 验证邮箱格式
     * @param email 邮箱
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 验证手机号格式
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhone(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 验证密码强度
     * @param password 密码
     * @return 是否是强密码
     */
    private boolean isStrongPassword(String password) {
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }
} 