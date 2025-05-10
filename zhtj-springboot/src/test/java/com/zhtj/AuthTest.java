package com.zhtj;

import com.zhtj.domain.User;
import com.zhtj.mapper.UserMapper;
import com.zhtj.service.UserService;
import com.zhtj.service.impl.UserServiceImpl;
import com.zhtj.config.JwtConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证流程测试类
 */
@SpringBootTest
public class AuthTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    /**
     * 测试完整的登录流程
     */
    @Test
    public void testLoginFlow() {
        // 模拟登录请求
        String card = "123456789012345678"; // 测试用户证件号
        String password = "password123";     // 测试密码
        
        try {
            // 1. 验证登录功能
            Map<String, Object> loginResult = userService.login(card, password, null);
            
            // 2. 验证返回的令牌信息
            Assert.notNull(loginResult.get("token"), "登录响应中应包含token");
            Assert.notNull(loginResult.get("expires_in"), "登录响应中应包含expires_in");
            Assert.notNull(loginResult.get("user_info"), "登录响应中应包含user_info");
            
            String token = (String) loginResult.get("token");
            
            // 3. 验证令牌有效性
            Boolean isValid = jwtConfig.validateToken(token);
            Assert.isTrue(isValid, "生成的JWT令牌应该有效");
            
            // 4. 验证Redis中应存储令牌信息
            Integer userId = jwtConfig.getUserIdFromToken(token);
            Assert.notNull(userId, "应能从JWT令牌中提取用户ID");
            
            String redisKey = String.format("user:token:%d", userId);
            Object tokenInfo = redisTemplate.opsForValue().get(redisKey);
            Assert.notNull(tokenInfo, "Redis中应存储用户令牌信息");
            
            // 5. 测试令牌刷新
            Map<String, Object> refreshResult = userService.refreshToken(token);
            Assert.notNull(refreshResult.get("token"), "刷新响应中应包含token");
            
            String newToken = (String) refreshResult.get("token");
            Assert.isTrue(!token.equals(newToken), "刷新后的令牌应与原令牌不同");
            
            // 6. 测试登出功能
            Boolean logoutResult = userService.logout(newToken);
            Assert.isTrue(logoutResult, "登出应成功");
            
            // 7. 验证登出后Redis中的令牌信息已被删除
            Object tokenInfoAfterLogout = redisTemplate.opsForValue().get(redisKey);
            Assert.isNull(tokenInfoAfterLogout, "登出后Redis中的令牌信息应被删除");
            
            // 8. 验证登出后的令牌应失效
            Boolean isTokenStillValid = jwtConfig.validateToken(newToken);
            Assert.isTrue(!isTokenStillValid, "登出后令牌应失效");
            
            System.out.println("认证流程测试成功!");
        } catch (Exception e) {
            System.err.println("认证流程测试失败: " + e.getMessage());
            e.printStackTrace();
            Assert.isTrue(false, "认证流程测试应无异常");
        }
    }
    
    /**
     * 测试BCrypt密码验证
     */
    @Test
    public void testPasswordEncoding() {
        String rawPassword = "password123";
        
        // 生成加密密码
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("加密后密码: " + encodedPassword);
        
        // 验证密码
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        Assert.isTrue(matches, "原始密码应匹配加密密码");
        
        // 验证错误密码不匹配
        boolean wrongMatches = passwordEncoder.matches("wrongpassword", encodedPassword);
        Assert.isTrue(!wrongMatches, "错误密码不应匹配加密密码");
        
        System.out.println("密码加密测试成功!");
    }
} 