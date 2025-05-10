package com.zhtj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * 权限验证注解
 * 用于标记需要特定权限才能访问的方法或类
 * 
 * <p>使用示例：
 * <pre>
 * // 需要user:view权限
 * {@literal @}RequiresPermission("user:view")
 * public void viewUserMethod() { ... }
 * 
 * // 自定义错误消息
 * {@literal @}RequiresPermission(value = "user:delete", message = "您没有删除用户的权限")
 * public void deleteUserMethod() { ... }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * 需要的权限名称
     * <p>权限通常使用冒号分隔的格式，如 "resource:action"
     * 
     * @return 权限名称
     */
    String value();
    
    /**
     * 权限校验失败时的错误消息
     * <p>当用户权限验证失败时，将在异常和日志中使用此消息
     * 
     * @return 错误消息
     */
    String message() default "没有操作权限";
    
    /**
     * 是否记录验证失败信息
     * <p>当设置为true时，验证失败会在日志中记录详细信息
     * <p>在生产环境中可以考虑关闭以提高性能
     * 
     * @return 是否记录验证失败信息
     */
    boolean logFailure() default true;
    
    /**
     * 是否抛出异常
     * <p>当设置为false时，验证失败不会抛出异常，而是由切面决定后续处理
     * <p>默认情况下会抛出AuthorizationException
     * 
     * @return 是否抛出异常
     */
    boolean throwException() default true;
} 