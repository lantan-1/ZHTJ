package com.zhtj.config;

import com.zhtj.common.api.Result;
import com.zhtj.common.exception.ApiException;
import com.zhtj.common.exception.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

/**
 * 配置模块异常处理器
 * 已被com.zhtj.common.exception.GlobalExceptionHandler替代
 * @deprecated 请使用com.zhtj.common.exception.GlobalExceptionHandler
 */
@RestControllerAdvice("com.zhtj.config")
public class ConfigExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigExceptionHandler.class);

    /**
     * 处理自定义API异常
     */
    @ExceptionHandler(value = ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handle(ApiException e) {
        if (e.getErrorCode() != null) {
            LOGGER.error("发生API异常：{}", e.getMessage(), e);
            return Result.failed(e.getMessage());
        }
        return Result.failed(e.getMessage());
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handle(BusinessException e) {
        LOGGER.error("发生业务异常：{}", e.getMessage(), e);
        if (e.getErrorCode() != null) {
            return Result.failed(e.getMessage());
        }
        return Result.failed(e.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(value = {MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleValidException(Exception e) {
        LOGGER.error("参数验证失败：{}", e.getMessage(), e);
        BindingResult bindingResult = null;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else if (e instanceof BindException) {
            bindingResult = ((BindException) e).getBindingResult();
        }
        if (bindingResult != null && bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                return Result.validateFailed(fieldError.getDefaultMessage());
            }
        }
        return Result.validateFailed();
    }

    /**
     * 处理约束验证异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleConstraintViolationException(ConstraintViolationException e) {
        LOGGER.error("约束验证失败：{}", e.getMessage(), e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        if (!violations.isEmpty()) {
            ConstraintViolation<?> violation = violations.iterator().next();
            return Result.validateFailed(violation.getMessage());
        }
        return Result.validateFailed();
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> handleException(Exception e) {
        LOGGER.error("发生未知异常：{}", e.getMessage(), e);
        return Result.failed("系统内部错误，请联系管理员");
    }
} 