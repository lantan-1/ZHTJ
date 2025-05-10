package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.SystemRole;
import com.zhtj.domain.UserRole;

import java.util.List;
import java.util.Set;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<SystemRole> {
    
    /**
     * 分页查询角色列表
     * 
     * @param page 分页参数
     * @param roleName 角色名称（可选）
     * @return 分页结果
     */
    IPage<SystemRole> getRolePage(Page<SystemRole> page, String roleName);
    
    /**
     * 根据ID获取角色
     * 
     * @param id 角色ID
     * @return 角色信息
     */
    SystemRole getRoleById(Integer id);
    
    /**
     * 根据角色编码获取角色
     * 
     * @param roleCode 角色编码
     * @return 角色信息
     */
    SystemRole getRoleByCode(String roleCode);
    
    /**
     * 创建角色
     * 
     * @param role 角色信息
     * @return 是否成功
     */
    boolean createRole(SystemRole role);
    
    /**
     * 更新角色
     * 
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(SystemRole role);
    
    /**
     * 删除角色
     * 
     * @param id 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Integer id);
    
    /**
     * 获取所有启用的角色
     * 
     * @return 角色列表
     */
    List<SystemRole> getAllEnabledRoles();
    
    /**
     * 获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 用户角色列表
     */
    List<UserRole> getUserRoles(Integer userId);
    
    /**
     * 获取用户角色编码集合
     * 
     * @param userId 用户ID
     * @return 角色编码集合
     */
    Set<String> getUserRoleCodes(Integer userId);
    
    /**
     * 分页查询用户的角色列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<UserRole> getUserRolePage(Page<UserRole> page, Integer userId);
    
    /**
     * 分配用户角色
     * 
     * @param userRole 用户角色信息
     * @return 是否成功
     */
    boolean assignUserRole(UserRole userRole);
    
    /**
     * 删除用户角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean removeUserRole(Integer userId, Integer roleId);
    
    /**
     * 检查用户是否有指定角色
     * 
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否有角色
     */
    boolean hasRole(Integer userId, String roleCode);
    
    /**
     * 检查用户是否拥有权限
     * 
     * @param userId 用户ID
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean hasPermission(Integer userId, String permission);
    
    /**
     * 检查角色是否拥有权限
     * 
     * @param userId 用户ID（可为null，仅用于日志记录）
     * @param roleCode 角色编码
     * @param permission 权限标识
     * @return 是否有权限
     */
    boolean hasPermission(Integer userId, String roleCode, String permission);
    
    /**
     * 为角色添加权限
     * 
     * @param roleCode 角色编码
     * @param permissionCode 权限编码
     * @return 是否成功
     */
    boolean addPermissionToRole(String roleCode, String permissionCode);
} 