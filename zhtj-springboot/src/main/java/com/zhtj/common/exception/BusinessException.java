package com.zhtj.common.exception;

import com.zhtj.common.api.IErrorCode;
import com.zhtj.common.api.ResultCode;

/**
 * 业务异常
 */
public class BusinessException extends ApiException {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(IErrorCode errorCode) {
        super(errorCode);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public BusinessException() {
        super(ResultCode.FAILED);
    }
} 