package com.zhtj.common.exception;

import com.zhtj.common.api.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义API异常
     */
    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleApiException(ApiException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "ApiException", e, request);
        
        if (e.getErrorCode() != null) {
            return Result.failed(e.getErrorCode(), e.getMessage() + " (请求ID: " + requestId + ")");
        }
        return Result.failed(e.getMessage() + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理资源不存在异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Object> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "ResourceNotFoundException", e, request);
        return Result.failed(e.getMessage() + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理数据重复异常
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleDuplicateResourceException(DuplicateResourceException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "DuplicateResourceException", e, request);
        return Result.failed(e.getMessage() + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Object> handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "ForbiddenException", e, request);
        return Result.forbidden(e.getMessage() + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "参数验证失败", e, request);
        
        BindingResult bindingResult = e.getBindingResult();
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessages.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
        return Result.validateFailed(String.join(", ", errorMessages) + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleBindException(BindException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "参数绑定失败", e, request);
        
        BindingResult bindingResult = e.getBindingResult();
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessages.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }
        return Result.validateFailed(String.join(", ", errorMessages) + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理约束验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "约束验证失败", e, request);
        
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> errorMessages = new ArrayList<>();
        for (ConstraintViolation<?> violation : violations) {
            errorMessages.add(violation.getPropertyPath() + ": " + violation.getMessage());
        }
        return Result.validateFailed(String.join(", ", errorMessages) + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理请求参数缺失异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "请求参数缺失", e, request);
        return Result.validateFailed("请求参数 " + e.getParameterName() + " 不能为空 (请求ID: " + requestId + ")");
    }

    /**
     * 处理请求参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "请求参数类型不匹配", e, request);
        return Result.validateFailed("参数 " + e.getName() + " 类型不匹配，应为 " + 
                e.getRequiredType().getSimpleName() + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Object> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "请求方法不支持", e, request);
        
        StringBuilder sb = new StringBuilder();
        sb.append("不支持 '").append(e.getMethod()).append("' 请求方法");
        if (e.getSupportedHttpMethods() != null && !e.getSupportedHttpMethods().isEmpty()) {
            sb.append("，支持的方法包括：").append(String.join(", ", e.getSupportedHttpMethods().stream().map(method -> method.toString()).toList()));
        }
        return Result.failed(sb.toString() + " (请求ID: " + requestId + ")");
    }

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Object> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "资源未找到", e, request);
        return Result.failed("路径 [" + e.getRequestURL() + "] 不存在 (请求ID: " + requestId + ")");
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e, HttpServletRequest request) {
        String requestId = generateRequestId();
        logError(requestId, "服务器内部错误", e, request);
        return Result.failed("服务器内部错误，请联系管理员 (请求ID: " + requestId + ")");
    }
    
    /**
     * 生成请求ID
     */
    private String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
    
    /**
     * 记录错误日志
     */
    private void logError(String requestId, String errorType, Exception e, HttpServletRequest request) {
        log.error("[{}] {} 异常: {}, URI: {}, 方法: {}, 客户端IP: {}, 用户代理: {}", 
                requestId, 
                errorType,
                e.getMessage(), 
                request.getRequestURI(), 
                request.getMethod(), 
                getClientIp(request),
                request.getHeader("User-Agent"),
                e);
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
} 