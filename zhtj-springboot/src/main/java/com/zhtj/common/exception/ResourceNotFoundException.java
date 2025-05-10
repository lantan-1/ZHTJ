package com.zhtj.common.exception;

import com.zhtj.common.api.ResultCode;

/**
 * 资源不存在异常
 */
public class ResourceNotFoundException extends ApiException {
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s不存在，%s: %s", resourceName, fieldName, fieldValue));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException() {
        super(ResultCode.NOT_FOUND);
    }
} 