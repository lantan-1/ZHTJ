package com.zhtj.common.exception;

import com.zhtj.common.api.ResultCode;

/**
 * 未授权异常（用户未登录或凭证无效）
 */
public class UnauthorizedException extends ApiException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public UnauthorizedException() {
        super(ResultCode.UNAUTHORIZED);
    }
} 