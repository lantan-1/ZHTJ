package com.zhtj.controller;

import com.zhtj.annotation.RequiresPermission;
import com.zhtj.common.api.Result;
import com.zhtj.domain.TransferApprovalLog;
import com.zhtj.service.TransferApprovalLogService;
import com.zhtj.service.TransferService;
import com.zhtj.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 转接申请审批日志控制器
 */
@RestController
@Tag(name = "审批日志管理", description = "转接申请审批日志相关接口")
public class TransferApprovalLogController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TransferApprovalLogController.class);
    
    @Autowired
    private TransferApprovalLogService transferApprovalLogService;
    
    @Autowired
    private TransferService transferService;
    
    /**
     * 获取转接申请的审批日志
     * 
     * @param transferId 转接申请ID
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @return 审批日志列表
     */
    @GetMapping("/transfers/{transferId}/logs")
    @Operation(summary = "获取转接申请审批日志", description = "获取指定转接申请的所有审批日志")
    @RequiresPermission("transfer:view")
    public Result<List<TransferApprovalLog>> getTransferLogs(
            @PathVariable Integer transferId,
            @ModelAttribute("currentUserId") Integer currentUserId) {
        
        // 验证转接申请是否存在
        if (!transferService.exists(transferId)) {
            return Result.validateFailed("转接申请不存在");
        }
        
        // 验证当前用户是否有权限查看（申请人、转入组织管理员、转出组织管理员、团支部管理员或系统管理员）
        boolean hasAccess = transferService.checkAccessPermission(transferId, currentUserId);
        if (!hasAccess && !hasRole("ADMIN") && !hasRole("BRANCH_ADMIN")) {
            return Result.forbidden("没有权限查看该转接申请的审批日志");
        }
        
        // 获取审批日志
        List<TransferApprovalLog> logs = transferApprovalLogService.getLogsByTransferId(transferId);
        return Result.success(logs);
    }
    
    /**
     * 根据审批类型获取转接申请的审批日志
     * 
     * @param transferId 转接申请ID
     * @param approvalType 审批类型（1：转出审批，2：转入审批）
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @return 审批日志
     */
    @GetMapping("/transfers/{transferId}/logs/{approvalType}")
    @Operation(summary = "获取特定类型的审批日志", description = "根据审批类型获取转接申请的审批日志")
    @RequiresPermission("transfer:view")
    public Result<TransferApprovalLog> getTransferLogByType(
            @PathVariable Integer transferId,
            @PathVariable Integer approvalType,
            @ModelAttribute("currentUserId") Integer currentUserId) {
        
        // 验证转接申请是否存在
        if (!transferService.exists(transferId)) {
            return Result.validateFailed("转接申请不存在");
        }
        
        // 验证审批类型是否合法
        if (approvalType != 1 && approvalType != 2) {
            return Result.validateFailed("审批类型无效");
        }
        
        // 验证当前用户是否有权限查看
        boolean hasAccess = transferService.checkAccessPermission(transferId, currentUserId);
        if (!hasAccess && !hasRole("ADMIN") && !hasRole("BRANCH_ADMIN")) {
            return Result.forbidden("没有权限查看该转接申请的审批日志");
        }
        
        // 获取审批日志
        TransferApprovalLog log = transferApprovalLogService.getLogByTransferIdAndType(transferId, approvalType);
        return Result.success(log);
    }
    
    /**
     * 获取用户的审批日志
     * 
     * @param userId 用户ID（可选，不传则获取当前用户）
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @return 审批日志列表
     */
    @GetMapping("/users/approval-logs")
    @Operation(summary = "获取用户审批日志", description = "获取用户参与审批的所有日志")
    @RequiresPermission("transfer:view")
    public Result<List<TransferApprovalLog>> getUserApprovalLogs(
            @RequestParam(required = false) Integer userId,
            @ModelAttribute("currentUserId") Integer currentUserId) {
        
        // 如果没有指定用户ID，则获取当前用户的审批日志
        Integer targetUserId = userId != null ? userId : currentUserId;
        
        // 如果查询的不是当前用户且当前用户不是管理员，则拒绝请求
        if (!targetUserId.equals(currentUserId) && !hasRole("ADMIN")) {
            return Result.forbidden("没有权限查看其他用户的审批日志");
        }
        
        // 获取用户审批日志
        List<TransferApprovalLog> logs = transferApprovalLogService.getLogsByApproverId(targetUserId);
        return Result.success(logs);
    }
} 