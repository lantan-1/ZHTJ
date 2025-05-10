package com.zhtj.common.exception;

/**
 * 授权异常
 * 当用户没有权限执行操作时抛出
 */
public class AuthorizationException extends RuntimeException {
    
    /**
     * 构造方法
     * 
     * @param message 错误消息
     */
    public AuthorizationException(String message) {
        super(message);
    }
    
    /**
     * 构造方法
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
} 