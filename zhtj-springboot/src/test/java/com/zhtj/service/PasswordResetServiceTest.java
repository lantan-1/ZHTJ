package com.zhtj.service;

import com.zhtj.domain.User;
import com.zhtj.service.impl.PasswordResetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PasswordResetServiceTest {

    @InjectMocks
    private PasswordResetServiceImpl passwordResetService;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @Mock
    private RedisService redisService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendResetPasswordCodeViaEmail() throws Exception {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("测试用户");
        mockUser.setCard("110101200001010101");
        mockUser.setEmail("test@example.com");

        // 模拟UserService返回用户
        when(userService.getByCard(anyString())).thenReturn(mockUser);
        
        // 模拟RedisService行为
//        when(redisService.set(anyString(), anyString(), anyLong())).thenReturn(true);

        // 模拟EmailService行为 - void方法使用doNothing()
        doNothing().when(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        
        // 调用测试方法
        Map<String, Object> result = passwordResetService.sendResetPasswordCode(
            "110101200001010101", "email", "127.0.0.1");
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("maskedEmail"));
        assertEquals("t****@example.com", result.get("maskedEmail"));
        
        // 验证调用
        verify(userService).getByCard("110101200001010101");
        verify(redisService).set(eq("reset_password_email_110101200001010101"), anyString(), eq(600L));
        verify(emailService).sendSimpleEmail(eq("test@example.com"), anyString(), anyString());
    }
    
    @Test
    public void testSendResetPasswordCodeViaSms() throws Exception {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("测试用户");
        mockUser.setCard("110101200001010101");
        mockUser.setPhone("13800138000");

        // 模拟UserService返回用户
        when(userService.getByCard(anyString())).thenReturn(mockUser);
        
        // 模拟RedisService行为
//        when(redisService.set(anyString(), anyString(), anyLong())).thenReturn(true);
        
        // 模拟SmsService行为 - void方法使用doNothing()
        doNothing().when(smsService).sendVerificationCode(anyString(), anyString());
        
        // 调用测试方法
        Map<String, Object> result = passwordResetService.sendResetPasswordCode(
            "110101200001010101", "sms", "127.0.0.1");
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("maskedPhone"));
        assertEquals("138****8000", result.get("maskedPhone"));
        
        // 验证调用
        verify(userService).getByCard("110101200001010101");
        verify(redisService).set(eq("reset_password_sms_110101200001010101"), anyString(), eq(600L));
        verify(smsService).sendVerificationCode(eq("13800138000"), anyString());
    }
    
    @Test
    public void testVerifyCodeAndResetPassword() throws Exception {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("测试用户");
        mockUser.setCard("110101200001010101");
        
        // 模拟UserService返回用户
        when(userService.getByCard(anyString())).thenReturn(mockUser);
        
        // 模拟RedisService返回验证码
        when(redisService.get(anyString())).thenReturn("123456");
        
        // 模拟更新用户成功
        when(userService.updateUser(any(User.class))).thenReturn(true);
        
        // 模拟删除Redis键
        doReturn(true).when(redisService).del(anyString());
        
        // 调用测试方法
        Map<String, Object> result = passwordResetService.verifyCodeAndResetPassword(
            "110101200001010101", "email", "123456", "newpassword");
        
        // 验证结果
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        
        // 验证调用
        verify(userService).getByCard("110101200001010101");
        verify(redisService).get("reset_password_email_110101200001010101");
        verify(userService).updateUser(any(User.class));
        verify(redisService).del("reset_password_email_110101200001010101");
    }
    
    @Test
    public void testVerifyCodeAndResetPasswordWithInvalidCode() {
        // 准备测试数据
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setName("测试用户");
        mockUser.setCard("110101200001010101");
        
        // 模拟UserService返回用户
        when(userService.getByCard(anyString())).thenReturn(mockUser);
        
        // 模拟RedisService返回验证码
        when(redisService.get(anyString())).thenReturn("123456");
        
        // 调用测试方法并验证异常
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordResetService.verifyCodeAndResetPassword(
                "110101200001010101", "email", "654321", "newpassword");
        });
        
        assertEquals("验证码错误", exception.getMessage());
    }
} 