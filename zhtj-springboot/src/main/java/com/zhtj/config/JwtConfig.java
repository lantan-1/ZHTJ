package com.zhtj.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Set;

/**
 * JWT配置类，用于处理JWT令牌的创建、验证和解析
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    private static final Logger log = LoggerFactory.getLogger(JwtConfig.class);

    /**
     * 密钥
     */
    @Value("${jwt.secret-key}")
    private String secret;

    /**
     * 过期时间（秒）
     */
    @Value("${jwt.token-expiration}")
    private long expiration;

    @Value("${jwt.issuer}")
    private String issuer;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 从令牌中获取用户ID
     */
    public Integer getUserIdFromToken(String token) {
        try {
            log.debug("开始从token中解析用户ID，token前10位: {}", token.substring(0, Math.min(token.length(), 10)));
            
            // 尝试从subject中获取
            String subject = getClaimFromToken(token, Claims::getSubject);
            log.debug("从token subject中获取的值为: {}", subject);
            
            if (subject != null && !subject.isEmpty()) {
                try {
                    return Integer.valueOf(subject);
                } catch (NumberFormatException e) {
                    log.warn("subject不是有效的数字: {}", subject);
                }
            }
            
            // 如果subject解析失败，尝试从user_id字段获取
            Object userIdClaim = getClaimFromToken(token, claims -> claims.get("user_id"));
            log.debug("从token user_id字段中获取的值为: {}", userIdClaim);
            
            if (userIdClaim != null) {
                if (userIdClaim instanceof Integer) {
                    return (Integer) userIdClaim;
                } else if (userIdClaim instanceof String) {
                    return Integer.valueOf((String) userIdClaim);
                } else if (userIdClaim instanceof Number) {
                    return ((Number) userIdClaim).intValue();
                }
            }
            
            log.warn("无法从token中解析出用户ID");
            return null;
        } catch (Exception e) {
            log.error("解析token中的用户ID出错: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从令牌中获取过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取指定声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (Exception e) {
            log.error("获取token声明失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从令牌中获取所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("解析token的Claims失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 检查令牌是否过期
     */
    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            log.error("检查token是否过期时出错: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 检查令牌是否在黑名单中
     */
    private Boolean isTokenInBlacklist(String token) {
        try {
            // 直接检查特定令牌是否在黑名单中
            String blacklistKeyPattern = "jwt:blacklist:*";
            Set<String> keys = redisTemplate.keys(blacklistKeyPattern);
            if (keys == null || keys.isEmpty()) {
                return false;
            }
            
            // 逐个检查键对应的值
            for (String key : keys) {
                Object value = redisTemplate.opsForValue().get(key);
                if (value != null && token.equals(value.toString())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("检查token是否在黑名单中时出错: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 生成令牌 (用于包含所有用户信息)
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成令牌
     */
    public String generateToken(Integer userId, String username, Integer orgId, String[] roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("username", username);
        claims.put("org_id", orgId);
        claims.put("roles", roles);
        return doGenerateToken(claims, userId.toString());
    }

    /**
     * 执行令牌生成
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        log.debug("生成Token，subject={}, claims={}", subject, claims);

        // 注意：Jwts.builder()先setClaims再setSubject时，subject会被claims覆盖
        // 所以我们需要确保subject单独设置，且在setClaims之后
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)  // 确保在setClaims后设置subject
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .setIssuer(issuer)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证令牌
     */
    public Boolean validateToken(String token) {
        try {
            boolean notExpired = !isTokenExpired(token);
            boolean notInBlacklist = !isTokenInBlacklist(token);
            log.debug("Token验证 - 未过期: {}, 不在黑名单: {}", notExpired, notInBlacklist);
            return notExpired && notInBlacklist;
        } catch (Exception e) {
            log.error("验证token时发生错误: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
} 