package com.zhtj.interceptor;

import com.zhtj.config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT拦截器，用于验证请求中的JWT令牌
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);
    
    @Autowired
    private JwtConfig jwtConfig;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        
        // 打印请求路径和请求方法，便于调试
        log.debug("拦截请求: {} {}", request.getMethod(), requestURI);
        
        // 排除不需要token的路径
        if (isExcludedPath(requestURI)) {
            log.debug("排除路径，无需令牌验证: {}", requestURI);
            return true;
        }
        
        // 从请求头获取token
        String token = getTokenFromRequest(request);
        if (!StringUtils.hasText(token)) {
            log.warn("请求未携带token: {}", requestURI);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        
        log.debug("获取到token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
        
        // 验证token
        boolean isValid = jwtConfig.validateToken(token);
        if (!isValid) {
            log.warn("无效的token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        
        // 获取用户ID，放入请求属性中，便于后续使用
        Integer userId = jwtConfig.getUserIdFromToken(token);
        if (userId != null) {
            request.setAttribute("userId", userId);
            log.debug("Token验证通过: 用户ID={}", userId);
        } else {
            // 即使用户ID为null也不拒绝请求，但记录警告日志
            log.warn("Token有效但无法提取用户ID，请检查token内容或生成逻辑");
        }
        
        return true;
    }
    
    /**
     * 判断是否是排除路径
     */
    private boolean isExcludedPath(String path) {
        // 排除不需要验证token的路径
        return path.startsWith("/captcha") ||
               path.contains("/login") ||
               path.startsWith("/auth") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/static") ||  // 排除静态资源路径
               path.equals("/error") ||
               path.equals("/users/register") ||  // 注册接口
               path.startsWith("/users/reset-password/") ||  // 所有密码重置相关接口
               path.startsWith("/api/users/reset-password/");  // API前缀的密码重置相关接口
    }
    
    /**
     * 从请求中获取token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // 从Authorization头获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // 尝试从查询参数获取
        String paramToken = request.getParameter("token");
        if (StringUtils.hasText(paramToken)) {
            return paramToken;
        }
        
        return null;
    }
} 