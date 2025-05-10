package com.zhtj.controller;

import com.zhtj.domain.User;
import com.zhtj.domain.Organization;
import com.zhtj.service.UserService;
import com.zhtj.service.OrganizationService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * 控制器基类
 * 提供获取当前用户信息和权限验证的通用方法
 */
public abstract class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);
    
    @Autowired
    protected UserService userService;
    
    @Autowired
    protected OrganizationService organizationService;
    
    /**
     * 为所有控制器方法提供当前用户ID
     * 
     * @return 当前登录用户ID
     */
    @ModelAttribute("currentUserId")
    public Integer getCurrentUserIdModel() {
        Integer userId = getCurrentUserIdFromRequest();
        log.debug("当前用户ID: {}", userId);
        return userId;
    }
    
    /**
     * 为所有控制器方法提供当前用户对象
     * 
     * @return 当前登录用户对象
     */
    @ModelAttribute("currentUser")
    public User getCurrentUserModel() {
        Integer userId = getCurrentUserIdFromRequest();
        if (userId != null) {
            User user = userService.getById(userId);
            log.debug("当前用户: {}", user != null ? user.getName() : "未找到");
            return user;
        }
        return null;
    }
    
    /**
     * 为所有控制器方法提供当前用户所属组织ID
     * 
     * @return 当前用户所属组织ID
     */
    @ModelAttribute("currentUserOrgId")
    public Integer getCurrentUserOrgIdModel() {
        User user = getCurrentUserModel();
        if (user != null && user.getOrganization() != null) {
            log.debug("当前用户组织ID: {}", user.getOrganization());
            return user.getOrganization();
        }
        return null;
    }
    
    /**
     * 为所有控制器方法提供当前用户所属组织
     * 
     * @return 当前用户所属组织
     */
    @ModelAttribute("currentUserOrg")
    public Organization getCurrentUserOrgModel() {
        Integer orgId = getCurrentUserOrgIdModel();
        if (orgId != null) {
            Organization org = organizationService.getById(orgId);
            log.debug("当前用户组织: {}", org != null ? org.getName() : "未找到");
            return org;
        }
        return null;
    }
    
    /**
     * 为所有控制器方法提供当前用户角色列表
     * 
     * @return 当前用户角色列表
     */
    @ModelAttribute("currentUserRoles")
    public Set<String> getCurrentUserRolesModel() {
        User user = getCurrentUserModel();
        if (user != null) {
            // 获取用户角色，具体实现需要根据您的用户角色存储方式调整
            Set<String> roles = userService.getUserRoles(user.getId());
            log.debug("当前用户角色: {}", roles);
            return roles;
        }
        return Collections.emptySet();
    }
    
    /**
     * 判断当前用户是否拥有指定角色
     * 
     * @param role 角色名
     * @return 是否拥有该角色
     */
    protected boolean hasRole(String role) {
        Set<String> roles = getCurrentUserRolesModel();
        return roles.contains(role);
    }
    
    /**
     * 判断当前用户是否拥有指定权限
     * 
     * @param permission 权限名
     * @return 是否拥有该权限
     */
    protected boolean hasPermission(String permission) {
        // 这里可以实现更复杂的权限检查逻辑
        // 可以根据您的权限系统设计进行调整
        User user = getCurrentUserModel();
        if (user != null) {
            return userService.hasPermission(user.getId(), permission);
        }
        return false;
    }
    
    /**
     * 判断用户是否属于指定组织或其下级组织
     * 
     * @param organizationId 组织ID
     * @return 是否属于该组织或其下级
     */
    protected boolean belongsToOrganization(Integer organizationId) {
        Integer userOrgId = getCurrentUserOrgIdModel();
        if (userOrgId != null && organizationId != null) {
            // 检查是否是同一组织
            if (userOrgId.equals(organizationId)) {
                return true;
            }
            
            // 检查是否是下级组织
            return organizationService.isSubOrganization(userOrgId, organizationId);
        }
        return false;
    }
    
    /**
     * 检查当前用户是否有权限审批转接申请
     * 
     * @param transferId 转接申请ID
     * @param approvalType 审批类型（"out"或"in"）
     * @return 是否有权限
     */
    protected boolean canApproveTransfer(Integer transferId, String approvalType) {
        // 根据您的业务规则实现审批权限检查
        // 例如：只有组织管理员才能审批
        if (hasRole("ADMIN") || hasRole("ORGANIZATION_ADMIN")) {
            return true;
        }
        
        // 根据转出/转入组织判断权限
        // 这里需要根据您的具体业务逻辑完善
        
        return false;
    }
    
    /**
     * 从请求中获取当前登录用户ID
     * 
     * @return 当前用户ID
     */
    protected Integer getCurrentUserIdFromRequest() {
        try {
            // 从请求属性中获取用户ID
            // 这个属性是由JwtInterceptor在验证token时设置的
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Object userIdAttr = request.getAttribute("userId");
                
                if (userIdAttr != null) {
                    if (userIdAttr instanceof Integer) {
                        return (Integer) userIdAttr;
                    } else {
                        // 尝试转换为Integer
                        try {
                            return Integer.parseInt(userIdAttr.toString());
                        } catch (NumberFormatException e) {
                            log.error("无法将userId属性转换为Integer: {}", userIdAttr);
                        }
                    }
                } else {
                    log.warn("请求中没有userId属性，JWT拦截器可能未正确设置");
                }
            } else {
                log.warn("无法获取当前请求上下文");
            }
            
            // 如果无法获取，返回默认ID
            return 1; // 默认ID，您应该替换为实际的逻辑或抛出异常
        } catch (Exception e) {
            log.error("获取当前用户ID失败: {}", e.getMessage());
            return 1; // 异常情况下的默认值
        }
    }
} 