package com.zhtj.config;

import com.zhtj.common.api.Result;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OpenAPI文档配置
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置OpenAPI基本信息
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智慧团建 API文档")
                        .description("智慧团建系统API接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("contact@example.com")
                                .url("https://www.example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt", Collections.emptyList()));
    }

    /**
     * 添加全局请求头参数
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // 全局添加响应状态码
            Components components = openApi.getComponents();
            components.addSchemas("ValidationError", createValidationErrorSchema());
            components.addSchemas("ErrorResponse", createErrorResponseSchema());
        };
    }

    /**
     * 为每个操作添加通用的响应定义
     */
    @Bean
    public OperationCustomizer operationCustomizer() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiResponses responses = operation.getResponses();
            
            // 添加通用的成功响应
            if (responses.get("200") == null) {
                responses.addApiResponse("200", new ApiResponse()
                        .description("操作成功")
                        .content(new Content().addMediaType("application/json", 
                                new MediaType().schema(new Schema<Object>().$ref("#/components/schemas/SuccessResponse")))));
            }
            
            // 添加通用的错误响应
            Map<String, String> commonErrorResponses = new HashMap<>();
            commonErrorResponses.put("400", "请求参数错误");
            commonErrorResponses.put("401", "未授权");
            commonErrorResponses.put("403", "权限不足");
            commonErrorResponses.put("404", "资源不存在");
            commonErrorResponses.put("500", "服务器内部错误");
            
            for (Map.Entry<String, String> entry : commonErrorResponses.entrySet()) {
                if (responses.get(entry.getKey()) == null) {
                    responses.addApiResponse(entry.getKey(), new ApiResponse()
                            .description(entry.getValue())
                            .content(new Content().addMediaType("application/json", 
                                    new MediaType().schema(new Schema<Object>().$ref("#/components/schemas/ErrorResponse")))));
                }
            }
            
            return operation;
        };
    }
    
    /**
     * 创建验证错误响应模型
     */
    private Schema<Object> createValidationErrorSchema() {
        Schema<Object> schema = new Schema<>();
        schema.setType("object");
        Schema<String> stringSchema = new Schema<>();
        stringSchema.setType("string");
        
        schema.addProperty("field", stringSchema.description("字段名"));
        schema.addProperty("message", stringSchema.description("错误消息"));
        return schema;
    }
    
    /**
     * 创建错误响应模型
     */
    private Schema<Object> createErrorResponseSchema() {
        Schema<Object> schema = new Schema<>();
        schema.setType("object");
        
        Schema<Integer> integerSchema = new Schema<>();
        integerSchema.setType("integer");
        
        Schema<String> stringSchema = new Schema<>();
        stringSchema.setType("string");
        
        Schema<Object> objectSchema = new Schema<>();
        objectSchema.setType("object");
        objectSchema.setNullable(true);
        
        schema.addProperty("code", integerSchema.description("错误代码"));
        schema.addProperty("message", stringSchema.description("错误消息"));
        schema.addProperty("data", objectSchema.description("数据"));
        
        return schema;
    }
} 