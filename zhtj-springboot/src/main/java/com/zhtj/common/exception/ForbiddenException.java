package com.zhtj.common.exception;

import com.zhtj.common.api.ResultCode;

/**
 * 权限不足异常
 */
public class ForbiddenException extends ApiException {
    
    public ForbiddenException(String message) {
        super(message);
    }
    
    public ForbiddenException() {
        super(ResultCode.FORBIDDEN);
    }
} 