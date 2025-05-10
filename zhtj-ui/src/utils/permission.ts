/**
 * 权限控制工具
 */
import { useUserStore } from '../stores/user';
import config from '../config';

/**
 * 检查当前用户是否拥有指定权限
 * @param permissionCode 权限代码
 * @returns 是否拥有权限
 */
export function hasPermission(permissionCode: string): boolean {
  // 如果未启用权限功能，默认返回true
  if (!config.features.userPermission) {
    return true;
  }
  
  const userStore = useUserStore();
  
  // 超级管理员拥有所有权限
  if (userStore.user?.role === 'admin') {
    return true;
  }
  
  // 检查用户权限列表
  return Array.isArray(userStore.permissions) && 
         userStore.permissions.includes(permissionCode);
}

/**
 * 检查当前用户是否拥有任意一个指定权限
 * @param permissionCodes 权限代码数组
 * @returns 是否拥有权限
 */
export function hasAnyPermission(permissionCodes: string[]): boolean {
  return permissionCodes.some(code => hasPermission(code));
}

/**
 * 检查当前用户是否拥有所有指定权限
 * @param permissionCodes 权限代码数组
 * @returns 是否拥有权限
 */
export function hasAllPermissions(permissionCodes: string[]): boolean {
  return permissionCodes.every(code => hasPermission(code));
}

/**
 * 权限控制指令
 * 使用方式：v-permission="'honor:add'"
 */
export const vPermission = {
  mounted(el: HTMLElement, binding: { value: string | string[] }) {
    // 获取权限值
    const permission = binding.value;
    
    // 检查权限
    let hasAuth = false;
    if (typeof permission === 'string') {
      hasAuth = hasPermission(permission);
    } else if (Array.isArray(permission)) {
      hasAuth = hasAnyPermission(permission);
    }
    
    // 如果没有权限，则从DOM中移除元素
    if (!hasAuth) {
      el.parentNode && el.parentNode.removeChild(el);
    }
  }
};

/**
 * 权限角色定义
 */
export const PermissionRoles = {
  // 荣誉管理权限
  HONOR_VIEW: 'honor:view',      // 查看荣誉
  HONOR_ADD: 'honor:add',        // 添加荣誉
  HONOR_EDIT: 'honor:edit',      // 编辑荣誉
  HONOR_DELETE: 'honor:delete',  // 删除荣誉
  HONOR_APPROVE: 'honor:approve', // 荣誉审批
  
  // 用户管理权限
  USER_VIEW: 'user:view',        // 查看用户
  USER_ADD: 'user:add',          // 添加用户
  USER_EDIT: 'user:edit',        // 编辑用户
  USER_DELETE: 'user:delete',    // 删除用户
  
  // 系统管理权限
  SYSTEM_SETTING: 'system:setting', // 系统设置
  SYSTEM_LOG: 'system:log',         // 系统日志
}; 