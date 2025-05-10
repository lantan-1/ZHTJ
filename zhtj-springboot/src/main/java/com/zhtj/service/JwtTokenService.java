package com.zhtj.service;

import com.zhtj.domain.User;

import java.util.Map;

/**
 * JWT令牌服务接口
 */
public interface JwtTokenService {
    
    /**
     * 根据用户信息生成JWT令牌
     * @param user 用户信息
     * @return JWT令牌
     */
    String generateToken(User user);
    
    /**
     * 根据用户ID和额外信息生成JWT令牌
     * @param userId 用户ID
     * @param claims 额外信息
     * @return JWT令牌
     */
    String generateToken(Integer userId, Map<String, Object> claims);
    
    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 从JWT令牌中获取用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    Integer getUserIdFromToken(String token);
    
    /**
     * 从JWT令牌中获取指定信息
     * @param token JWT令牌
     * @param key 信息键
     * @return 信息值
     */
    Object getClaimFromToken(String token, String key);
    
    /**
     * 吊销JWT令牌
     * @param token JWT令牌
     * @return 是否成功
     */
    boolean revokeToken(String token);
    
    /**
     * 刷新JWT令牌
     * @param token 旧令牌
     * @return 新令牌
     */
    String refreshToken(String token);
} 