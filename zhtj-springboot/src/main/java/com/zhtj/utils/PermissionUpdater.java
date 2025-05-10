package com.zhtj.utils;

import com.zhtj.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

/**
 * 权限更新工具类
 * 在应用启动时自动添加缺失的权限到角色
 */
@Component
public class PermissionUpdater implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PermissionUpdater.class);

    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始检查并更新角色权限...");

        // 确保团支部管理员角色有查看审批日志的权限
        if (!roleService.hasPermission(null, "BRANCH_ADMIN", "transfer:view")) {
            boolean result = roleService.addPermissionToRole("BRANCH_ADMIN", "transfer:view");
            if (result) {
                log.info("已成功为团支部管理员(BRANCH_ADMIN)角色添加查看审批日志(transfer:view)权限");
            } else {
                log.warn("为团支部管理员角色添加查看审批日志权限失败，可能权限已存在或角色不存在");
            }
        } else {
            log.info("团支部管理员角色已拥有查看审批日志权限");
        }

        log.info("角色权限检查和更新完成");
    }
} 