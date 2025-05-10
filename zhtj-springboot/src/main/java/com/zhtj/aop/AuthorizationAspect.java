package com.zhtj.aop;

import com.zhtj.annotation.RequiresPermission;
import com.zhtj.annotation.RequiresRole;
import com.zhtj.common.api.Result;
import com.zhtj.common.exception.AuthorizationException;
import com.zhtj.controller.BaseController;
import com.zhtj.domain.User;
import com.zhtj.service.UserService;
import com.zhtj.common.handler.AuthExceptionHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * 权限验证切面
 * 用于处理RequiresPermission和RequiresRole注解的权限验证
 */
@Aspect
@Component
public class AuthorizationAspect {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationAspect.class);
    
    @Autowired
    private UserService userService;
    
    /**
     * 定义切入点 - 匹配所有带RequiresPermission注解的方法
     */
    @Pointcut("@annotation(com.zhtj.annotation.RequiresPermission)")
    public void permissionPointcut() {}
    
    /**
     * 定义切入点 - 匹配所有带RequiresRole注解的方法
     */
    @Pointcut("@annotation(com.zhtj.annotation.RequiresRole)")
    public void roleMethodPointcut() {}
    
    /**
     * 定义切入点 - 匹配所有带RequiresRole注解的类中的所有方法
     */
    @Pointcut("@within(com.zhtj.annotation.RequiresRole)")
    public void roleClassPointcut() {}
    
    /**
     * 定义切点，匹配所有带有@PreAuthorize注解的方法
     */
    @Pointcut("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public void preAuthorize() {}
    
    /**
     * 处理RequiresPermission注解
     */
    @Around("permissionPointcut()")
    public Object handleRequiresPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequiresPermission annotation = method.getAnnotation(RequiresPermission.class);
        String requiredPermission = annotation.value();
        String errorMessage = annotation.message();
        
        // 获取当前用户ID
        Integer userId = getCurrentUserId();
        if (userId == null) {
            throw new AuthorizationException("未认证用户");
        }
        
        // 检查权限
        boolean hasPermission = userService.hasPermission(userId, requiredPermission);
        if (!hasPermission) {
            log.warn("用户(ID:{})没有所需权限: {}", userId, requiredPermission);
            throw new AuthorizationException(errorMessage);
        }
        
        // 有权限，继续执行原方法
        return joinPoint.proceed();
    }
    
    /**
     * 处理方法级别RequiresRole注解
     */
    @Around("roleMethodPointcut()")
    public Object handleMethodRequiresRole(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequiresRole annotation = method.getAnnotation(RequiresRole.class);
        return handleRoleVerification(joinPoint, annotation);
    }
    
    /**
     * 处理类级别RequiresRole注解
     * 注意：如果方法上也有RequiresRole注解，方法级注解优先
     */
    @Around("roleClassPointcut() && !roleMethodPointcut()")
    public Object handleClassRequiresRole(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        RequiresRole annotation = AnnotationUtils.findAnnotation(targetClass, RequiresRole.class);
        return handleRoleVerification(joinPoint, annotation);
    }
    
    /**
     * 通用角色验证处理逻辑
     */
    private Object handleRoleVerification(ProceedingJoinPoint joinPoint, RequiresRole annotation) throws Throwable {
        if (annotation == null) {
            return joinPoint.proceed(); // 没有注解，直接执行
        }
        
        String[] requiredRoles = annotation.value();
        boolean allRoles = annotation.allRoles();
        String errorMessage = annotation.message();
        boolean logFailure = annotation.logFailure();
        boolean throwException = annotation.throwException();
        
        // 获取当前用户ID
        Integer userId = getCurrentUserId();
        if (userId == null) {
            if (logFailure) {
                log.warn("未能获取认证用户");
            }
            if (throwException) {
                throw new AuthorizationException("未认证用户");
            }
            return Result.unauthorized("未认证用户");
        }
        
        // 获取用户角色
        Set<String> userRoles = userService.getUserRoles(userId);
        
        // 检查是否满足角色要求
        boolean hasRequiredRoles;
        if (allRoles) {
            // 需要拥有所有指定角色
            hasRequiredRoles = Arrays.stream(requiredRoles)
                    .allMatch(userRoles::contains);
        } else {
            // 只需拥有其中一个角色即可
            hasRequiredRoles = Arrays.stream(requiredRoles)
                    .anyMatch(userRoles::contains);
        }
        
        if (!hasRequiredRoles) {
            if (logFailure) {
                log.warn("用户(ID:{})没有所需角色: {}，当前角色: {}", 
                        userId, Arrays.toString(requiredRoles), userRoles);
            }
            if (throwException) {
                throw new AuthorizationException(errorMessage);
            }
            return Result.forbidden(errorMessage);
        }
        
        // 有角色，继续执行原方法
        return joinPoint.proceed();
    }
    
    /**
     * 获取当前用户ID
     */
    private Integer getCurrentUserId() {
        ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Object userIdAttr = request.getAttribute("userId");
            
            if (userIdAttr != null) {
                if (userIdAttr instanceof Integer) {
                    return (Integer) userIdAttr;
                } else {
                    try {
                        return Integer.parseInt(userIdAttr.toString());
                    } catch (NumberFormatException e) {
                        log.error("无法将userId属性转换为Integer: {}", userIdAttr);
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * 在执行带有@PreAuthorize注解的方法之前记录日志
     */
    @Before("preAuthorize()")
    public void logAuthorization(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.debug("执行权限校验: {}.{}", className, methodName);
    }
} 