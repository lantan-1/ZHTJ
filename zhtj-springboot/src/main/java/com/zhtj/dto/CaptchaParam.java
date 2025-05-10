package com.zhtj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 验证码请求参数
 */
@Data
@Schema(description = "验证码请求参数")
public class CaptchaParam {
    
    @Schema(description = "验证码类型", example = "login", required = true)
    @NotBlank(message = "验证码类型不能为空")
    private String codeType;
    
    @Schema(description = "设备标识(可选)", example = "web-123456")
    private String deviceId;
} 