package com.zhtj.controller;

import com.zhtj.common.api.Result;
import com.zhtj.common.api.ResultCode;
import com.zhtj.common.exception.ApiException;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.common.exception.DuplicateResourceException;
import com.zhtj.common.exception.ForbiddenException;
import com.zhtj.common.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常处理演示控制器
 */
@RestController
@RequestMapping("/demo/exception")
@Validated
@Tag(name = "异常演示", description = "用于演示不同类型的异常处理")
public class ExceptionDemoController {

    @GetMapping("/business")
    @Operation(summary = "业务异常演示")
    public Result<Object> businessException() {
        throw new BusinessException("这是一个业务异常示例");
    }

    @GetMapping("/api")
    @Operation(summary = "API异常演示")
    public Result<Object> apiException() {
        throw new ApiException(ResultCode.FAILED);
    }

    @GetMapping("/not-found/{id}")
    @Operation(summary = "资源不存在异常演示")
    public Result<Object> resourceNotFoundException(
            @Parameter(description = "资源ID") @PathVariable("id") Integer id) {
        throw new ResourceNotFoundException("用户", "id", id);
    }

    @GetMapping("/duplicate")
    @Operation(summary = "数据重复异常演示")
    public Result<Object> duplicateResourceException(
            @Parameter(description = "用户名") @RequestParam String username) {
        throw new DuplicateResourceException("用户", "用户名", username);
    }

    @GetMapping("/forbidden")
    @Operation(summary = "权限不足异常演示")
    public Result<Object> forbiddenException() {
        throw new ForbiddenException("您没有权限执行此操作");
    }

    @GetMapping("/validation")
    @Operation(summary = "参数验证异常演示")
    public Result<Object> validationException(
            @Parameter(description = "用户ID") @RequestParam @Min(1) Integer id,
            @Parameter(description = "用户名") @RequestParam @NotBlank String name) {
        return Result.success("验证通过");
    }

    @PostMapping("/validation-body")
    @Operation(summary = "请求体验证异常演示")
    public Result<DemoDTO> validationBodyException(@Valid @RequestBody DemoDTO demoDTO) {
        return Result.success(demoDTO);
    }

    @GetMapping("/arithmetic")
    @Operation(summary = "算术异常演示")
    public Result<Integer> arithmeticException() {
        int result = 1 / 0;
        return Result.success(result);
    }

    @GetMapping("/null-pointer")
    @Operation(summary = "空指针异常演示")
    public Result<String> nullPointerException() {
        String str = null;
        return Result.success(str.toLowerCase());
    }

    /**
     * 演示DTO
     */
    @Data
    public static class DemoDTO {
        @NotBlank(message = "名称不能为空")
        private String name;

        @Min(value = 1, message = "年龄必须大于0")
        private Integer age;
    }
} 