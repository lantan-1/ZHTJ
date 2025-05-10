package com.zhtj.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhtj.common.api.ResultCode;
import com.zhtj.common.exception.ApiException;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.common.exception.DuplicateResourceException;
import com.zhtj.common.exception.ForbiddenException;
import com.zhtj.common.exception.ResourceNotFoundException;
import com.zhtj.controller.ExceptionDemoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 全局异常处理器测试
 */
@WebMvcTest(ExceptionDemoController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testBusinessException() throws Exception {
        mockMvc.perform(get("/demo/exception/business"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.FAILED.getCode()))
                .andExpect(jsonPath("$.message").value(containsString("这是一个业务异常示例")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testApiException() throws Exception {
        mockMvc.perform(get("/demo/exception/api"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.FAILED.getCode()))
                .andExpect(jsonPath("$.message").value(containsString(ResultCode.FAILED.getMessage())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/demo/exception/not-found/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ResultCode.FAILED.getCode()))
                .andExpect(jsonPath("$.message").value(containsString("用户不存在，id: 1")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDuplicateResourceException() throws Exception {
        mockMvc.perform(get("/demo/exception/duplicate").param("username", "admin"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.FAILED.getCode()))
                .andExpect(jsonPath("$.message").value(containsString("用户已存在，用户名: admin")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testForbiddenException() throws Exception {
        mockMvc.perform(get("/demo/exception/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(ResultCode.FORBIDDEN.getCode()))
                .andExpect(jsonPath("$.message").value(containsString("您没有权限执行此操作")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testValidationException() throws Exception {
        mockMvc.perform(get("/demo/exception/validation")
                        .param("id", "0")
                        .param("name", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATE_FAILED.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testValidationBodyException() throws Exception {
        ExceptionDemoController.DemoDTO dto = new ExceptionDemoController.DemoDTO();
        dto.setName("");
        dto.setAge(0);

        mockMvc.perform(post("/demo/exception/validation-body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATE_FAILED.getCode()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testArithmeticException() throws Exception {
        mockMvc.perform(get("/demo/exception/arithmetic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ResultCode.FAILED.getCode()))
                .andExpect(jsonPath("$.message").value(containsString("服务器内部错误")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testNullPointerException() throws Exception {
        mockMvc.perform(get("/demo/exception/null-pointer"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(ResultCode.FAILED.getCode()))
                .andExpect(jsonPath("$.message").value(containsString("服务器内部错误")))
                .andDo(MockMvcResultHandlers.print());
    }
} 