package com.zhtj.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/**
 * IP地址工具类
 */
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * 获取客户端IP地址
     *
     * @param request HTTP请求
     * @return IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = null;
        
        // X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        
        if (!StringUtils.hasText(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
            // Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }
        
        if (!StringUtils.hasText(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
            // WL-Proxy-Client-IP：WebLogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }
        
        if (!StringUtils.hasText(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
            // HTTP_CLIENT_IP：某些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }
        
        if (!StringUtils.hasText(ipAddresses) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
            // X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }
        
        // 有些网络通过多层代理，那么获取到的IP就会有多个，一般都是通过逗号分割开来
        if (StringUtils.hasText(ipAddresses)) {
            ip = ipAddresses.split(",")[0].trim();
        }
        
        // 还是不能获取到，最后再通过request.getRemoteAddr()获取
        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理本地访问
        if (LOCALHOST_IPV6.equals(ip)) {
            ip = LOCALHOST_IPV4;
        }
        
        return ip;
    }
} 