package com.zhtj.controller;

import com.zhtj.common.api.Result;
import com.zhtj.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/permissions")
@Tag(name = "权限管理", description = "权限相关接口")
public class PermissionController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);
    
    @Autowired
    private RoleService roleService;
    
    /**
     * 刷新角色权限缓存
     * 
     * @param roleCode 角色编码
     * @param permissionCode 权限编码
     * @return 操作结果
     */
    @GetMapping("/refresh/{roleCode}/{permissionCode}")
    @Operation(summary = "刷新角色权限", description = "为指定角色添加指定权限，刷新内存缓存")
    public Result<String> refreshPermission(
            @PathVariable String roleCode, 
            @PathVariable String permissionCode) {
        
        log.info("收到手动刷新权限请求: 角色={}, 权限={}", roleCode, permissionCode);
        
        if (!hasRole("ADMIN")) {
            return Result.forbidden("只有系统管理员才能刷新权限缓存");
        }
        
        boolean result = roleService.addPermissionToRole(roleCode, permissionCode);
        if (result) {
            return Result.success("权限刷新成功");
        } else {
            return Result.failed("权限刷新失败，请检查角色和权限是否存在");
        }
    }
} 