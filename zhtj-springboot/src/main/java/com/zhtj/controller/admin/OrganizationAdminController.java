package com.zhtj.controller.admin;

import com.zhtj.common.api.Result;
import com.zhtj.domain.Organization;
import com.zhtj.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 组织管理控制器 - 管理员功能
 */
@RestController
@RequestMapping("/admin/organizations")
@Tag(name = "组织管理-管理员", description = "组织管理员维护接口")
public class OrganizationAdminController {

    private static final Logger log = LoggerFactory.getLogger(OrganizationAdminController.class);
    
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 修复组织结构
     * 直接更新关键数据以修复组织树
     */
    @PostMapping("/repair")
    @Operation(summary = "修复组织结构", description = "修复组织结构中的层级关系和路径")
    public Result<Map<String, Object>> repairOrganizationStructure() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("开始修复组织结构");
            
            // 1. 修正根节点
            int rootFixed = jdbcTemplate.update(
                "UPDATE organization SET parent_id = NULL, level = 0, path = '3', is_leaf = 0 WHERE id = 3"
            );
            result.put("root_node_fixed", rootFixed > 0);
            
            // 2. 修正二级节点
            int secondLevel1Fixed = jdbcTemplate.update(
                "UPDATE organization SET parent_id = 3, level = 1, path = '3,14', is_leaf = 0 WHERE id = 14"
            );
            int secondLevel2Fixed = jdbcTemplate.update(
                "UPDATE organization SET parent_id = 3, level = 1, path = '3,16', is_leaf = 0 WHERE id = 16"
            );
            result.put("second_level_fixed", secondLevel1Fixed + secondLevel2Fixed);
            
            // 3. 修正三级节点
            int thirdLevelFixed = jdbcTemplate.update(
                "UPDATE organization SET parent_id = 14, level = 2, path = '3,14,15', is_leaf = 1 WHERE id = 15"
            );
            result.put("third_level_fixed", thirdLevelFixed);
            
            // 4. 清空并重建organization_hierarchy表
            jdbcTemplate.execute("TRUNCATE TABLE organization_hierarchy");
            
            // 5. 插入层级数据
            jdbcTemplate.update(
                "INSERT INTO organization_hierarchy " +
                "(organization_id, parent_organization_id, level, path, create_time, update_time) " +
                "VALUES (3, NULL, 0, '3', ?, ?)",
                LocalDateTime.now(), LocalDateTime.now()
            );
            
            jdbcTemplate.update(
                "INSERT INTO organization_hierarchy " +
                "(organization_id, parent_organization_id, level, path, create_time, update_time) " +
                "VALUES (14, 3, 1, '3,14', ?, ?)",
                LocalDateTime.now(), LocalDateTime.now()
            );
            
            jdbcTemplate.update(
                "INSERT INTO organization_hierarchy " +
                "(organization_id, parent_organization_id, level, path, create_time, update_time) " +
                "VALUES (15, 14, 2, '3,14,15', ?, ?)",
                LocalDateTime.now(), LocalDateTime.now()
            );
            
            jdbcTemplate.update(
                "INSERT INTO organization_hierarchy " +
                "(organization_id, parent_organization_id, level, path, create_time, update_time) " +
                "VALUES (16, 3, 1, '3,16', ?, ?)",
                LocalDateTime.now(), LocalDateTime.now()
            );
            
            result.put("hierarchy_rebuilt", true);
            
            log.info("组织结构修复完成");
            return Result.success(result, "组织结构修复成功");
            
        } catch (Exception e) {
            log.error("修复组织结构失败: {}", e.getMessage(), e);
            result.put("error", e.getMessage());
            return Result.failed("修复组织结构失败: " + e.getMessage());
        }
    }
} 