package com.zhtj.common.exception;

import com.zhtj.common.api.ResultCode;

/**
 * 数据重复异常
 */
public class DuplicateResourceException extends ApiException {
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s已存在，%s: %s", resourceName, fieldName, fieldValue));
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
    
    public DuplicateResourceException() {
        super(ResultCode.FAILED);
    }
} 