package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.domain.SystemRole;
import com.zhtj.domain.UserRole;
import com.zhtj.mapper.SystemRoleMapper;
import com.zhtj.mapper.UserRoleMapper;
import com.zhtj.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<SystemRoleMapper, SystemRole> implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    
    @Autowired
    private SystemRoleMapper systemRoleMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;
    
    // 角色和权限的映射关系，实际项目中这可能来自数据库
    // 简化实现，在实际项目中应该使用专门的权限表和角色权限关联表
    private static final java.util.Map<String, Set<String>> ROLE_PERMISSIONS = new java.util.HashMap<>();
    
    static {
        // 超级管理员角色拥有所有权限
        Set<String> adminPermissions = new HashSet<>();
        adminPermissions.add("user:*");
        adminPermissions.add("role:*");
        adminPermissions.add("organization:*");
        adminPermissions.add("transfer:*");
        ROLE_PERMISSIONS.put("ADMIN", adminPermissions);
        
        // 组织管理员角色
        Set<String> orgAdminPermissions = new HashSet<>();
        orgAdminPermissions.add("user:list");
        orgAdminPermissions.add("user:detail");
        orgAdminPermissions.add("organization:list");
        orgAdminPermissions.add("organization:detail");
        orgAdminPermissions.add("transfer:list");
        orgAdminPermissions.add("transfer:detail");
        orgAdminPermissions.add("transfer:create");
        orgAdminPermissions.add("transfer:approve");
        orgAdminPermissions.add("transfer:approval:list");
        ROLE_PERMISSIONS.put("ORGANIZATION_ADMIN", orgAdminPermissions);
        
        // 普通用户角色
        Set<String> memberPermissions = new HashSet<>();
        memberPermissions.add("user:detail:self");
        memberPermissions.add("transfer:list");
        memberPermissions.add("transfer:detail:self");
        memberPermissions.add("transfer:create:self");
        memberPermissions.add("transfer:create");
        memberPermissions.add("transfer:view");
        ROLE_PERMISSIONS.put("MEMBER", memberPermissions);
        
        // 团支部管理员角色
        Set<String> branchAdminPermissions = new HashSet<>();
        branchAdminPermissions.add("user:list");
        branchAdminPermissions.add("user:detail");
        branchAdminPermissions.add("organization:list");
        branchAdminPermissions.add("organization:detail");
        branchAdminPermissions.add("transfer:list");
        branchAdminPermissions.add("transfer:detail");
        branchAdminPermissions.add("transfer:create");
        branchAdminPermissions.add("transfer:approve:out");
        branchAdminPermissions.add("transfer:approval:list");
        branchAdminPermissions.add("transfer:view");
        // 添加团籍注册审批权限
        branchAdminPermissions.add("twosystem:register:approve");
        ROLE_PERMISSIONS.put("BRANCH_ADMIN", branchAdminPermissions);
        
        // 院级团委管理员角色
        Set<String> collegeAdminPermissions = new HashSet<>();
        collegeAdminPermissions.add("user:list");
        collegeAdminPermissions.add("user:detail");
        collegeAdminPermissions.add("organization:list");
        collegeAdminPermissions.add("organization:detail");
        collegeAdminPermissions.add("transfer:list");
        collegeAdminPermissions.add("transfer:detail");
        collegeAdminPermissions.add("transfer:create");
        collegeAdminPermissions.add("transfer:approve:out");
        collegeAdminPermissions.add("transfer:approve:in");
        collegeAdminPermissions.add("transfer:approval:list");
        collegeAdminPermissions.add("transfer:view");
        // 添加转入审批相关权限
        collegeAdminPermissions.add("transfer:approve");
        ROLE_PERMISSIONS.put("COLLEGE_ADMIN", collegeAdminPermissions);
        
        // 副书记角色 - 与对应的正职拥有相同的权限
        // 团委副书记与团委书记拥有相同的权限
        Set<String> deputyCollegeSecretaryPermissions = new HashSet<>(collegeAdminPermissions);
        ROLE_PERMISSIONS.put("DEPUTY_COLLEGE_SECRETARY", deputyCollegeSecretaryPermissions);
        
        // 团支部副书记与团支部书记拥有相同的权限
        Set<String> deputyBranchSecretaryPermissions = new HashSet<>(branchAdminPermissions);
        ROLE_PERMISSIONS.put("DEPUTY_BRANCH_SECRETARY", deputyBranchSecretaryPermissions);
    }

    @Override
    public IPage<SystemRole> getRolePage(Page<SystemRole> page, String roleName) {
        return systemRoleMapper.selectRolePage(page, roleName);
    }

    @Override
    public SystemRole getRoleById(Integer id) {
        return systemRoleMapper.selectById(id);
    }

    @Override
    public SystemRole getRoleByCode(String roleCode) {
        return systemRoleMapper.selectByRoleCode(roleCode);
    }

    @Override
    @Transactional
    public boolean createRole(SystemRole role) {
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        role.setStatus(true); // 默认启用
        return save(role);
    }

    @Override
    @Transactional
    public boolean updateRole(SystemRole role) {
        role.setUpdateTime(LocalDateTime.now());
        return updateById(role);
    }

    @Override
    @Transactional
    public boolean deleteRole(Integer id) {
        // 检查是否有用户关联此角色
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getRoleId, id);
        Long countLong = userRoleMapper.selectCount(queryWrapper);
        int count = countLong != null ? countLong.intValue() : 0;
        if (count > 0) {
            log.warn("角色ID:{} 有用户关联，无法删除", id);
            return false;
        }
        return removeById(id);
    }

    @Override
    public List<SystemRole> getAllEnabledRoles() {
        return systemRoleMapper.selectAllEnabledRoles();
    }

    @Override
    public List<UserRole> getUserRoles(Integer userId) {
        return userRoleMapper.selectUserRolesByUserId(userId);
    }

    @Override
    public Set<String> getUserRoleCodes(Integer userId) {
        return userRoleMapper.selectUserRoleCodes(userId);
    }

    @Override
    public IPage<UserRole> getUserRolePage(Page<UserRole> page, Integer userId) {
        return userRoleMapper.selectUserRolePage(page, userId);
    }

    @Override
    @Transactional
    public boolean assignUserRole(UserRole userRole) {
        // 检查是否已经存在相同的用户角色关联
        LambdaQueryWrapper<UserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRole::getUserId, userRole.getUserId())
                   .eq(UserRole::getRoleId, userRole.getRoleId());
        if (userRole.getOrganizationId() != null) {
            queryWrapper.eq(UserRole::getOrganizationId, userRole.getOrganizationId());
        }
        
        Long countLong = userRoleMapper.selectCount(queryWrapper);
        int count = countLong != null ? countLong.intValue() : 0;
        if (count > 0) {
            log.info("用户ID:{} 已经拥有角色ID:{}", userRole.getUserId(), userRole.getRoleId());
            return true; // 已经存在，视为成功
        }
        
        // 设置时间
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setUpdateTime(LocalDateTime.now());
        
        return userRoleMapper.insert(userRole) > 0;
    }

    @Override
    @Transactional
    public boolean removeUserRole(Integer userId, Integer roleId) {
        return userRoleMapper.deleteUserRole(userId, roleId) > 0;
    }

    @Override
    public boolean hasRole(Integer userId, String roleCode) {
        if (userId == null || !StringUtils.hasText(roleCode)) {
            return false;
        }
        
        Set<String> userRoleCodes = getUserRoleCodes(userId);
        return userRoleCodes.contains(roleCode);
    }

    @Override
    public boolean hasPermission(Integer userId, String permission) {
        if (userId == null || !StringUtils.hasText(permission)) {
            return false;
        }
        
        // 获取用户的角色
        Set<String> userRoleCodes = getUserRoleCodes(userId);
        
        // 遍历用户的角色，检查是否有符合要求的权限
        for (String roleCode : userRoleCodes) {
            Set<String> permissions = ROLE_PERMISSIONS.get(roleCode);
            if (permissions == null) {
                continue;
            }
            
            // 直接匹配权限
            if (permissions.contains(permission)) {
                return true;
            }
            
            // 支持通配符匹配，例如 "user:*" 可以匹配 "user:list", "user:create" 等
            for (String rolePermission : permissions) {
                if (rolePermission.endsWith(":*")) {
                    String permPrefix = rolePermission.substring(0, rolePermission.lastIndexOf(":"));
                    if (permission.startsWith(permPrefix + ":")) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean hasPermission(Integer userId, String roleCode, String permission) {
        if (!StringUtils.hasText(roleCode) || !StringUtils.hasText(permission)) {
            return false;
        }
        
        // 获取角色的权限集合
        Set<String> permissions = ROLE_PERMISSIONS.get(roleCode);
        if (permissions == null) {
            log.warn("角色不存在或没有权限: {}", roleCode);
            return false;
        }
        
        // 直接匹配权限
        if (permissions.contains(permission)) {
            return true;
        }
        
        // 支持通配符匹配
        for (String rolePermission : permissions) {
            if (rolePermission.endsWith(":*")) {
                String permPrefix = rolePermission.substring(0, rolePermission.lastIndexOf(":"));
                if (permission.startsWith(permPrefix + ":")) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * 为角色添加权限
     * 
     * @param roleCode 角色编码
     * @param permissionCode 权限编码
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean addPermissionToRole(String roleCode, String permissionCode) {
        // 简化实现，因为我们已经使用了内存中的权限映射
        log.info("尝试为角色 {} 添加权限 {}", roleCode, permissionCode);
        
        // 获取角色的权限集合
        Set<String> permissions = ROLE_PERMISSIONS.get(roleCode);
        if (permissions == null) {
            // 角色不存在
            log.warn("角色不存在: {}", roleCode);
            return false;
        }
        
        // 添加权限
        if (permissions.contains(permissionCode)) {
            log.info("角色 {} 已经拥有权限 {}", roleCode, permissionCode);
            return false;
        }
        
        permissions.add(permissionCode);
        log.info("成功为角色 {} 添加权限 {}", roleCode, permissionCode);
        return true;
    }
} 