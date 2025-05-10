package com.zhtj.controller.twosystem;

import com.zhtj.common.api.Result;
import com.zhtj.domain.User;
import com.zhtj.config.JwtConfig;
import com.zhtj.model.twosystem.MemberRegister;
import com.zhtj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 团籍注册控制器
 */
@RestController
@RequestMapping("/register")
public class MemberRegisterController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取当前团员的注册状态
     */
    @GetMapping("/status")
    public Result<Object> getStatus(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
        return Result.success(userService.getRegisterStatus(userId));
        } catch (Exception e) {
            return Result.failed("获取注册状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取团员注册历史记录
     */
    @GetMapping("/history")
    public Result<Object> getHistory(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size,
            HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
        return Result.success(userService.getRegisterHistory(userId, page, size));
        } catch (Exception e) {
            return Result.failed("获取注册历史失败：" + e.getMessage());
        }
    }

    /**
     * 申请团籍注册
     */
    @PostMapping("/apply")
    public Result<Void> apply(@RequestBody MemberRegister register, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
            register.setUserId(userId);
        register.setStatus("待审核");
        register.setApplyTime(LocalDateTime.now());
        register.setCreateTime(LocalDateTime.now());
        register.setUpdateTime(LocalDateTime.now());
        
        boolean result = userService.applyRegister(register);
        return result ? Result.success() : Result.failed("提交注册申请失败");
        } catch (Exception e) {
            return Result.failed("提交注册申请失败：" + e.getMessage());
        }
    }

    /**
     * 获取注册审批列表
     */
    @GetMapping("/approve")
    @PreAuthorize("@ss.hasPermi('twosystem:register:approve')")
    public Result<Object> getApprovalList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "batchId", required = false) Integer batchId,
            @RequestParam(value = "memberName", required = false) String memberName,
            @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
        User currentUser = userService.getById(userId);
        return Result.success(userService.getApprovalList(currentUser, page, size, batchId, memberName, status));
        } catch (Exception e) {
            return Result.failed("获取审批列表失败：" + e.getMessage());
        }
    }

    /**
     * 审批通过
     */
    @PostMapping("/approve/{id}/approve")
    @PreAuthorize("@ss.hasPermi('twosystem:register:approve')")
    public Result<Void> approve(@PathVariable Integer id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
        User currentUser = userService.getById(userId);
        String comments = params.get("comments");
        
        boolean result = userService.approveRegister(id, currentUser, comments);
        return result ? Result.success() : Result.failed("审批通过失败");
        } catch (Exception e) {
            return Result.failed("审批通过失败：" + e.getMessage());
        }
    }

    /**
     * 审批驳回
     */
    @PostMapping("/approve/{id}/reject")
    @PreAuthorize("@ss.hasPermi('twosystem:register:approve')")
    public Result<Void> reject(@PathVariable Integer id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
        User currentUser = userService.getById(userId);
        String comments = params.get("comments");
        
        boolean result = userService.rejectRegister(id, currentUser, comments);
        return result ? Result.success() : Result.failed("审批驳回失败");
        } catch (Exception e) {
            return Result.failed("审批驳回失败：" + e.getMessage());
        }
    }

    /**
     * 批量审批通过
     */
    @PostMapping("/approve/batch-approve")
    @PreAuthorize("@ss.hasPermi('twosystem:register:approve')")
    @SuppressWarnings("unchecked")
    public Result<Void> batchApprove(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
        User currentUser = userService.getById(userId);
        List<Integer> ids = (List<Integer>) params.get("ids");
        String comments = (String) params.get("comments");
        
        boolean result = userService.batchApproveRegister(ids, currentUser, comments);
        return result ? Result.success() : Result.failed("批量审批通过失败");
        } catch (Exception e) {
            return Result.failed("批量审批通过失败：" + e.getMessage());
        }
    }

    /**
     * 批量审批驳回
     */
    @PostMapping("/approve/batch-reject")
    @PreAuthorize("@ss.hasPermi('twosystem:register:approve')")
    @SuppressWarnings("unchecked")
    public Result<Void> batchReject(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
        User currentUser = userService.getById(userId);
        List<Integer> ids = (List<Integer>) params.get("ids");
        String comments = (String) params.get("comments");
        
        boolean result = userService.batchRejectRegister(ids, currentUser, comments);
        return result ? Result.success() : Result.failed("批量审批驳回失败");
        } catch (Exception e) {
            return Result.failed("批量审批驳回失败：" + e.getMessage());
        }
    }

    /**
     * 获取审批统计数据
     */
    @GetMapping("/approve/statistics")
    @PreAuthorize("@ss.hasPermi('twosystem:register:approve')")
    public Result<Object> getApprovalStatistics(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
        Integer userId = jwtConfig.getUserIdFromToken(token);
        User currentUser = userService.getById(userId);
        return Result.success(userService.getApprovalStatistics(currentUser));
        } catch (Exception e) {
            return Result.failed("获取审批统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取注册统计数据
     */
    @GetMapping("/statistics/data")
    @PreAuthorize("@ss.hasPermi('twosystem:register:statistics')")
    public Result<Object> getStatisticsData(
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {
        try {
        Map<String, Object> data = userService.getStatisticsData(year, organizationId);
        return Result.success(data);
        } catch (Exception e) {
            return Result.failed("获取注册统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 导出统计数据
     */
    @GetMapping("/statistics/export")
    @PreAuthorize("@ss.hasPermi('twosystem:register:statistics')")
    public Result<String> exportStatistics(
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {
        try {
        String url = userService.exportStatistics(year, organizationId);
        return Result.success(url);
        } catch (Exception e) {
            return Result.failed("导出统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 更新注册状态（通用方法）
     */
    @PostMapping("/approve/{id}/update-status")
    @PreAuthorize("@ss.hasPermi('twosystem:register:approve')")
    public Result<Void> updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> params, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
            Integer userId = jwtConfig.getUserIdFromToken(token);
            User currentUser = userService.getById(userId);
            String status = params.get("status");
            String comment = params.get("comment");
            
            // 根据状态使用不同的方法
            boolean result = false;
            if ("已通过".equals(status)) {
                result = userService.approveRegister(id, currentUser, comment);
            } else if ("已驳回".equals(status)) {
                result = userService.rejectRegister(id, currentUser, comment);
            } else {
                // 这里需要添加通用状态更新的方法
                // 暂时先不实现
                return Result.failed("暂不支持此状态更新");
            }
            
            return result ? Result.success() : Result.failed("更新状态失败");
        } catch (Exception e) {
            return Result.failed("更新状态失败：" + e.getMessage());
        }
    }
} 