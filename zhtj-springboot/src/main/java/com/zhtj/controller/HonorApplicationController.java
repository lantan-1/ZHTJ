package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorApplication;
import com.zhtj.service.HonorApplicationService;
import com.zhtj.config.JwtConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 荣誉申请控制器
 */
@RestController
@RequestMapping("/honors/application-detail")
@Tag(name = "荣誉激励模块", description = "荣誉申请相关接口")
public class HonorApplicationController {
    
    private static final Logger logger = LoggerFactory.getLogger(HonorApplicationController.class);
    
    @Autowired
    private HonorApplicationService honorApplicationService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    /**
     * 分页查询荣誉申请
     */
    @GetMapping
    @Operation(summary = "分页查询荣誉申请", description = "根据条件分页查询荣誉申请")
    public ResponseEntity<Map<String, Object>> getHonorApplicationPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "申请年度") @RequestParam(required = false) String year,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "荣誉类型ID") @RequestParam(required = false) Integer honorTypeId,
            @Parameter(description = "组织ID") @RequestParam(required = false) Integer organizationId) {
        
        Page<HonorApplication> pageObj = new Page<>(page, size);
        IPage<HonorApplication> pageResult = honorApplicationService.getHonorApplicationPage(
                pageObj, year, status, honorTypeId, organizationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取荣誉申请详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取荣誉申请详情", description = "根据ID获取荣誉申请详情")
    public ResponseEntity<HonorApplication> getHonorApplication(
            @Parameter(description = "申请ID") @PathVariable Integer id) {
        
        HonorApplication honorApplication = honorApplicationService.getById(id);
        if (honorApplication == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(honorApplication);
    }
    
    /**
     * 创建荣誉申请
     */
    @PostMapping
    @Operation(summary = "创建荣誉申请", description = "创建新的荣誉申请")
    public ResponseEntity<Map<String, Object>> createHonorApplication(
            @Parameter(description = "荣誉申请对象") @RequestBody HonorApplication honorApplication) {
        
        Integer id = honorApplicationService.create(honorApplication);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新荣誉申请
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新荣誉申请", description = "更新指定的荣誉申请")
    public ResponseEntity<Boolean> updateHonorApplication(
            @Parameter(description = "申请ID") @PathVariable Integer id,
            @Parameter(description = "荣誉申请对象") @RequestBody HonorApplication honorApplication) {
        
        honorApplication.setId(id);
        boolean success = honorApplicationService.update(honorApplication);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 删除荣誉申请
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除荣誉申请", description = "删除指定的荣誉申请")
    public ResponseEntity<Boolean> deleteHonorApplication(
            @Parameter(description = "申请ID") @PathVariable Integer id) {
        
        boolean success = honorApplicationService.delete(id);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 支部审批荣誉申请
     */
    @PostMapping("/{id}/branch-approve")
    @Operation(summary = "支部审批荣誉申请", description = "支部审批指定的荣誉申请")
    public ResponseEntity<Boolean> branchApproveHonorApplication(
            @Parameter(description = "申请ID") @PathVariable Integer id,
            @Parameter(description = "审批状态") @RequestParam String approvalStatus,
            @Parameter(description = "审批意见") @RequestParam(required = false) String approvalComments,
            HttpServletRequest request) {
        
        // 从JWT令牌获取用户ID
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer approverId = jwtConfig.getUserIdFromToken(token);
        
        if (approverId == null) {
            logger.warn("支部审批荣誉申请时未获取到有效的用户ID");
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("用户[{}]尝试支部审批荣誉申请[{}]，状态: {}", approverId, id, approvalStatus);
        boolean success = honorApplicationService.branchApprove(id, approvalStatus, approvalComments, approverId);
        
        if (success) {
            logger.info("用户[{}]成功支部审批荣誉申请[{}]", approverId, id);
        } else {
            logger.error("用户[{}]支部审批荣誉申请[{}]失败", approverId, id);
        }
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 上级审批荣誉申请
     */
    @PostMapping("/{id}/higher-approve")
    @Operation(summary = "上级审批荣誉申请", description = "上级审批指定的荣誉申请")
    public ResponseEntity<Boolean> higherApproveHonorApplication(
            @Parameter(description = "申请ID") @PathVariable Integer id,
            @Parameter(description = "审批状态") @RequestParam String approvalStatus,
            @Parameter(description = "审批意见") @RequestParam(required = false) String approvalComments,
            HttpServletRequest request) {
        
        // 从JWT令牌获取用户ID
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer approverId = jwtConfig.getUserIdFromToken(token);
        
        if (approverId == null) {
            logger.warn("上级审批荣誉申请时未获取到有效的用户ID");
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("用户[{}]尝试上级审批荣誉申请[{}]，状态: {}", approverId, id, approvalStatus);
        boolean success = honorApplicationService.higherApprove(id, approvalStatus, approvalComments, approverId);
        
        if (success) {
            logger.info("用户[{}]成功上级审批荣誉申请[{}]", approverId, id);
        } else {
            logger.error("用户[{}]上级审批荣誉申请[{}]失败", approverId, id);
        }
        
        return ResponseEntity.ok(success);
    }
} 