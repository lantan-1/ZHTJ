package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.annotation.RequiresPermission;
import com.zhtj.annotation.RequiresRole;
import com.zhtj.common.api.Result;
import com.zhtj.domain.SystemRole;
import com.zhtj.domain.User;
import com.zhtj.domain.UserRole;
import com.zhtj.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/roles")
@Tag(name = "角色管理", description = "角色管理相关接口")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取角色列表
     */
    @GetMapping
    @Operation(summary = "获取角色列表", description = "分页获取角色列表")
    @RequiresPermission("role:list")
    public Result<Map<String, Object>> getRoles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName) {
        
        Page<SystemRole> pageParam = new Page<>(page, size);
        IPage<SystemRole> rolePage = roleService.getRolePage(pageParam, roleName);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", rolePage.getTotal());
        result.put("list", rolePage.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详情")
    @RequiresPermission("role:detail")
    public Result<SystemRole> getRoleDetail(@PathVariable Integer id) {
        SystemRole role = roleService.getRoleById(id);
        if (role == null) {
            return Result.failed("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色")
    @RequiresRole("ADMIN")
    @RequiresPermission("role:create")
    public Result<Boolean> createRole(@RequestBody SystemRole role) {
        boolean success = roleService.createRole(role);
        return success ? Result.success(true, "创建成功") : Result.failed("创建失败");
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新角色信息")
    @RequiresRole("ADMIN")
    @RequiresPermission("role:update")
    public Result<Boolean> updateRole(@PathVariable Integer id, @RequestBody SystemRole role) {
        role.setId(id);
        boolean success = roleService.updateRole(role);
        return success ? Result.success(true, "更新成功") : Result.failed("更新失败");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "删除角色信息")
    @RequiresRole("ADMIN")
    @RequiresPermission("role:delete")
    public Result<Boolean> deleteRole(@PathVariable Integer id) {
        boolean success = roleService.deleteRole(id);
        return success ? Result.success(true, "删除成功") : Result.failed("删除失败，该角色可能已被分配给用户");
    }

    /**
     * 获取所有可用角色
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有可用角色", description = "获取所有启用状态的角色")
    @RequiresPermission("role:list")
    public Result<List<SystemRole>> getAllRoles() {
        List<SystemRole> roles = roleService.getAllEnabledRoles();
        return Result.success(roles);
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户角色列表", description = "获取指定用户的角色列表")
    @RequiresPermission("role:list")
    public Result<Map<String, Object>> getUserRoles(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<UserRole> pageParam = new Page<>(page, size);
        IPage<UserRole> rolePage = roleService.getUserRolePage(pageParam, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", rolePage.getTotal());
        result.put("list", rolePage.getRecords());
        
        return Result.success(result);
    }

    /**
     * 分配用户角色
     */
    @PostMapping("/user")
    @Operation(summary = "分配用户角色", description = "为用户分配角色")
    @RequiresRole("ADMIN")
    @RequiresPermission("role:assign")
    public Result<Boolean> assignUserRole(@RequestBody UserRole userRole) {
        boolean success = roleService.assignUserRole(userRole);
        return success ? Result.success(true, "分配成功") : Result.failed("分配失败");
    }

    /**
     * 移除用户角色
     */
    @DeleteMapping("/user/{userId}/role/{roleId}")
    @Operation(summary = "移除用户角色", description = "移除用户的指定角色")
    @RequiresRole("ADMIN")
    @RequiresPermission("role:remove")
    public Result<Boolean> removeUserRole(@PathVariable Integer userId, @PathVariable Integer roleId) {
        boolean success = roleService.removeUserRole(userId, roleId);
        return success ? Result.success(true, "移除成功") : Result.failed("移除失败");
    }

    /**
     * 检查用户是否有指定角色
     */
    @GetMapping("/check/{userId}/has/{roleCode}")
    @Operation(summary = "检查用户角色", description = "检查用户是否拥有指定角色")
    public Result<Boolean> checkUserRole(@PathVariable Integer userId, @PathVariable String roleCode) {
        boolean hasRole = roleService.hasRole(userId, roleCode);
        return Result.success(hasRole);
    }

    /**
     * 获取当前用户的角色集合
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户角色", description = "获取当前登录用户的角色集合")
    public Result<Set<String>> getCurrentUserRoles(@ModelAttribute("currentUserId") Integer currentUserId) {
        Set<String> roles = roleService.getUserRoleCodes(currentUserId);
        return Result.success(roles);
    }
} 