package com.zhtj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security Web安全配置
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 完全禁用CSRF保护
        http.csrf(csrf -> csrf.disable());
        
        // 其他配置
        http
            // 启用CORS支持
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 配置HTTP请求授权规则
            .authorizeHttpRequests(auth -> auth
                // 公开端点，无需认证
                .requestMatchers(
                    "/captcha",
                    "/captcha/**",
                    "/auth/**",
                    "/login",
                    "/api/logout",  // 显式允许/api/logout路径通过
                    "/users/login"
                ).permitAll()
                // 用户信息接口，允许认证访问
                .requestMatchers(
                    "/users/current",
                    "/api/users/current"
                ).permitAll() // 临时修改为permitAll()以便调试
                // 其它所有请求需要认证
                .anyRequest().permitAll() // 临时修改为permitAll()以便调试
            )
            // 禁用Spring Security的默认logout功能
            .logout(logout -> logout.disable());
        
        return http.build();
    }
    
    /**
     * CORS配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有源
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 允许特定源
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173", 
            "http://localhost:8080",
            "http://127.0.0.1:5173",
            "http://127.0.0.1:8080"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Auth-Token", 
            "X-Requested-With", 
            "Accept", 
            "Origin", 
            "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept", 
            "Access-Control-Allow-Origin", 
            "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}