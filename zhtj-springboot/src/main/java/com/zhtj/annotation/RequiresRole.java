package com.zhtj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

/**
 * 角色验证注解
 * 用于标记需要特定角色才能访问的方法或类
 * 
 * <p>使用示例：
 * <pre>
 * // 需要ADMIN角色
 * {@literal @}RequiresRole("ADMIN")
 * public void adminMethod() { ... }
 * 
 * // 需要ADMIN或MANAGER角色之一
 * {@literal @}RequiresRole({"ADMIN", "MANAGER"})
 * public void managerMethod() { ... }
 * 
 * // 需要同时拥有ADMIN和AUDITOR角色
 * {@literal @}RequiresRole(value = {"ADMIN", "AUDITOR"}, allRoles = true)
 * public void auditMethod() { ... }
 * 
 * // 自定义错误消息
 * {@literal @}RequiresRole(value = "ADMIN", message = "需要管理员权限")
 * public void customMethod() { ... }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresRole {
    
    /**
     * 需要的角色名称
     * <p>可以指定单个角色名称或角色名称数组
     * <p>如果指定了多个角色，默认行为是用户只需拥有其中一个角色即可通过验证
     * <p>如果需要用户同时拥有所有指定角色，请将{@link #allRoles()}设置为true
     * 
     * @return 角色名称数组
     */
    String[] value();
    
    /**
     * 是否需要满足所有角色
     * <p>true: 需要用户拥有所有指定角色
     * <p>false: 用户只需拥有其中一个角色即可
     * 
     * @return 是否需要满足所有角色
     */
    boolean allRoles() default false;
    
    /**
     * 角色校验失败时的错误消息
     * <p>当用户角色验证失败时，将在异常和日志中使用此消息
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