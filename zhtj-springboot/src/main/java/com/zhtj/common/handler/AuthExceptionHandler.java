package com.zhtj.common.handler;

import com.zhtj.common.api.Result;
import com.zhtj.common.exception.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 认证授权异常处理器
 * 专门处理权限验证相关的异常
 */
@RestControllerAdvice
@Order(1) // 高优先级，确保在全局异常处理器之前处理
public class AuthExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(AuthExceptionHandler.class);
    
    /**
     * 处理授权异常
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAuthorizationException(AuthorizationException e) {
        log.warn("授权验证失败: {}", e.getMessage());
        return Result.forbidden(e.getMessage());
    }
    
    /**
     * 处理其他未捕获的认证授权相关异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.failed("服务器内部错误");
    }
} 