package com.zhtj.service.impl;

import com.zhtj.config.JwtConfig;
import com.zhtj.domain.User;
import com.zhtj.service.JwtTokenService;
import com.zhtj.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT令牌服务实现类
 */
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

    @Autowired
    private JwtConfig jwtConfig;
    
    @Autowired
    private RedisService redisService;
    
    // Redis中存储黑名单的前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "jwt:blacklist:";
    
    // Redis中存储令牌的过期时间（比JWT令牌本身多一些，确保过期的令牌也能被识别为黑名单）
    private static final long TOKEN_BLACKLIST_TTL = 24 * 60 * 60; // 24小时
    
    @Override
    public String generateToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }
        
        // 构建额外信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getName());
        claims.put("org_id", user.getOrganization());
        
        // 可以添加用户角色等信息
        // 简化处理，这里不添加角色信息
        String[] roles = new String[]{"USER"};
        
        // 调用JwtConfig生成令牌
        return jwtConfig.generateToken(user.getId(), user.getName(), user.getOrganization(), roles);
    }
    
    @Override
    public String generateToken(Integer userId, Map<String, Object> claims) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 从claims中提取必要信息
        String username = (String) claims.getOrDefault("username", "未知用户");
        Integer orgId = (Integer) claims.getOrDefault("org_id", 0);
        
        // 简化处理，这里使用固定角色
        String[] roles = new String[]{"USER"};
        
        // 调用JwtConfig生成令牌
        return jwtConfig.generateToken(userId, username, orgId, roles);
    }
    
    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        // 检查是否在黑名单中
        if (isTokenRevoked(token)) {
            return false;
        }
        
        // 调用JwtConfig验证令牌
        return jwtConfig.validateToken(token);
    }
    
    @Override
    public Integer getUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("令牌不能为空");
        }
        
        return jwtConfig.getUserIdFromToken(token);
    }
    
    @Override
    public Object getClaimFromToken(String token, String key) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("令牌不能为空");
        }
        
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("键名不能为空");
        }
        
        try {
            // 使用JwtConfig获取声明
            return jwtConfig.getClaimFromToken(token, claims -> claims.get(key));
        } catch (Exception e) {
            throw new IllegalArgumentException("无法从令牌中获取声明: " + e.getMessage());
        }
    }
    
    @Override
    public boolean revokeToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("令牌不能为空");
        }
        
        // 添加到Redis黑名单
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        redisService.set(blacklistKey, true, TOKEN_BLACKLIST_TTL);
        
        return true;
    }
    
    @Override
    public String refreshToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("令牌不能为空");
        }
        
        // 验证原令牌是否有效
        if (!validateToken(token)) {
            throw new IllegalArgumentException("无效的令牌");
        }
        
        // 吊销原令牌
        revokeToken(token);
        
        // 获取用户ID
        Integer userId = getUserIdFromToken(token);
        
        // 构建新令牌的声明
        Map<String, Object> newClaims = new HashMap<>();
        
        // 添加必要信息
        String username = (String) getClaimFromToken(token, "username");
        Integer orgId = (Integer) getClaimFromToken(token, "org_id");
        newClaims.put("username", username != null ? username : "未知用户");
        newClaims.put("org_id", orgId != null ? orgId : 0);
        
        // 生成新令牌
        return generateToken(userId, newClaims);
    }
    
    /**
     * 检查令牌是否已被吊销
     * @param token JWT令牌
     * @return 是否已被吊销
     */
    private boolean isTokenRevoked(String token) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisService.hasKey(blacklistKey));
    }
} 