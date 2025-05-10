package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.annotation.RequiresPermission;
import com.zhtj.annotation.RequiresRole;
import com.zhtj.common.api.Result;
import com.zhtj.domain.Transfer;
import com.zhtj.domain.User;
import com.zhtj.domain.Organization;
import com.zhtj.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * 团组织关系转接控制器
 */
@RestController
@RequestMapping("/transfers")
@Tag(name = "团组织关系转接", description = "团组织关系转接申请和审批相关接口")
public class TransferController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);
    
    @Autowired
    private TransferService transferService;
    
    /**
     * 为所有控制器方法提供当前用户ID
     * 
     * @return 当前登录用户ID
     */
    @ModelAttribute("currentUserId")
    public Integer getCurrentUserIdModel() {
        Integer userId = getCurrentUserIdFromRequest();
        return userId;
    }
    
    /**
     * 获取转接申请列表
     * 
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param status 状态，可选
     * @param transferUserId 转接用户ID，可选
     * @param mine 是否为"我的转接申请"，可选
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @param currentUser 当前用户对象（由@ModelAttribute自动提供）
     * @return 转接申请列表和总数
     */
    @GetMapping
    @Operation(summary = "获取转接申请列表", description = "分页获取转接申请列表，可根据状态、用户进行筛选")
    @RequiresPermission("transfer:list")
    public Result<Map<String, Object>> getTransfers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer transferUserId,
            @RequestParam(required = false) Boolean mine,
            @ModelAttribute("currentUserId") Integer currentUserId,
            @ModelAttribute("currentUser") User currentUser) {
        
        // 如果是请求"我的转接申请"
        if (Boolean.TRUE.equals(mine)) {
            transferUserId = currentUserId;
        }
        
        Page<Transfer> pageParam = new Page<>(page, size);
        // 将status转换为Integer类型的statusCode
        Integer statusCode = null;
        if (StringUtils.hasText(status)) {
            try {
                statusCode = Integer.parseInt(status);
            } catch (NumberFormatException e) {
                logger.warn("无法将状态{}转换为整数", status);
            }
        }
        
        IPage<Transfer> pageResult = transferService.getTransferPage(pageParam, transferUserId, statusCode, null);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return Result.success(result);
    }
    
    /**
     * 获取转接申请详情
     * 
     * @param id 转接申请ID
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @return 转接申请详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取转接申请详情", description = "根据ID获取转接申请详细信息")
    public Result<Transfer> getTransferDetail(
            @PathVariable Integer id,
            @ModelAttribute("currentUserId") Integer currentUserId) {
        
        // 先获取转接申请信息
        Transfer transfer = transferService.getTransferDetail(id);
        
        // 检查是否有权限查看此详情
        // 1. 如果是自己的申请，可以查看
        // 2. 如果是自己所属组织的申请（作为转出或转入组织），可以查看
        boolean hasPermission = false;
        
        // 检查是否是自己的申请
        if (currentUserId.equals(transfer.getTransferUserId())) {
            hasPermission = true;
        }
        
        // 如果仍然没有通过，检查是否是所属组织的申请
        if (!hasPermission && (belongsToOrganization(transfer.getTransferOutOrgId()) || 
                               belongsToOrganization(transfer.getTransferInOrgId()))) {
            hasPermission = true;
        }
        
        // 没有权限查看
        if (!hasPermission) {
            return Result.forbidden("没有权限查看此转接申请");
        }
        
        return Result.success(transfer);
    }
    
    /**
     * 创建转接申请
     * 
     * @param transfer 转接申请信息
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @param currentUserOrgId 当前用户所属组织ID（由@ModelAttribute自动提供）
     * @return 转接申请ID
     */
    @PostMapping
    @Operation(summary = "创建转接申请", description = "创建新的团组织关系转接申请")
    @RequiresPermission("transfer:create")
    public Result<Map<String, Object>> createTransfer(
            @RequestBody Transfer transfer,
            @ModelAttribute("currentUserId") Integer currentUserId,
            @ModelAttribute("currentUserOrgId") Integer currentUserOrgId) {
        
        // 设置申请人ID为当前登录用户
        transfer.setTransferUserId(currentUserId);
        
        // 设置转出组织为当前用户所属组织
        if (transfer.getTransferOutOrgId() == null) {
            transfer.setTransferOutOrgId(currentUserOrgId);
        } else if (!currentUserOrgId.equals(transfer.getTransferOutOrgId())) {
            // 检查是否试图为其他组织创建申请
            return Result.validateFailed("只能创建自己所属组织的转接申请");
        }
        
        // 参数验证
        if (transfer.getTransferInOrgId() == null) {
            return Result.validateFailed("转入组织ID不能为空");
        }
        
        // 转出和转入组织不能相同
        if (transfer.getTransferOutOrgId().equals(transfer.getTransferInOrgId())) {
            return Result.validateFailed("转出和转入组织不能相同");
        }
        
        if (!StringUtils.hasText(transfer.getTransferReason())) {
            return Result.validateFailed("转接原因不能为空");
        }
        
        boolean success = transferService.createTransfer(transfer);
        
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", transfer.getId());
            return Result.success(data, "创建转接申请成功");
        } else {
            return Result.failed("创建转接申请失败");
        }
    }
    
    /**
     * 转出组织审批
     * 
     * @param id 转接申请ID
     * @param approval 是否批准
     * @param approvalRemark 备注
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @param currentUserRoles 当前用户角色（由@ModelAttribute自动提供）
     * @return 操作结果
     */
    @PostMapping("/{id}/outapprove")
    @Operation(summary = "转出组织审批", description = "转出组织对转接申请进行审批")
    @RequiresRole({"ADMIN", "ORGANIZATION_ADMIN", "BRANCH_ADMIN", "COLLEGE_ADMIN", "DEPUTY_COLLEGE_SECRETARY", "DEPUTY_BRANCH_SECRETARY"})
    public Result<Boolean> outApprove(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "false") Boolean approval,
            @RequestParam(required = false) String approvalRemark,
            @ModelAttribute("currentUserId") Integer currentUserId,
            @ModelAttribute("currentUserRoles") Set<String> currentUserRoles) {
        
        // 获取转接申请信息
        Transfer transfer = transferService.getTransferDetail(id);
        if (transfer == null) {
            return Result.validateFailed("转接申请不存在");
        }
        
        // 检查申请状态
        if (transfer.getStatusCode() != 0 && transfer.getStatusCode() != 1) { // 申请中或转出审批中
            return Result.validateFailed("当前状态无法进行转出审批");
        }
        
        // 检查是否有权限审批（只有转出组织管理员可以审批）
        if (!hasRole("ADMIN") && !belongsToOrganization(transfer.getTransferOutOrgId())) {
            return Result.forbidden("没有权限审批此转接申请");
        }
        
        // 修改参数顺序以匹配service接口
        boolean success = transferService.outApprove(id, approval, currentUserId, approvalRemark);
        return Result.success(success, success ? "转出组织审批成功" : "转出组织审批失败");
    }
    
    /**
     * 转入组织审批
     * 
     * @param id 转接申请ID
     * @param approval 是否批准
     * @param approvalRemark 备注
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @param currentUserRoles 当前用户角色（由@ModelAttribute自动提供）
     * @return 操作结果
     */
    @PostMapping("/{id}/inapprove")
    @Operation(summary = "转入组织审批", description = "转入组织对转接申请进行审批")
    @RequiresRole({"ADMIN", "ORGANIZATION_ADMIN", "BRANCH_ADMIN", "COLLEGE_ADMIN", "DEPUTY_COLLEGE_SECRETARY", "DEPUTY_BRANCH_SECRETARY"})
    public Result<Boolean> inApprove(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "false") Boolean approval,
            @RequestParam(required = false) String approvalRemark,
            @ModelAttribute("currentUserId") Integer currentUserId,
            @ModelAttribute("currentUserRoles") Set<String> currentUserRoles) {
        
        // 获取转接申请信息
        Transfer transfer = transferService.getTransferDetail(id);
        if (transfer == null) {
            return Result.validateFailed("转接申请不存在");
        }
        
        // 检查申请状态
        if (transfer.getStatusCode() != 2) { // 转入审批中
            return Result.validateFailed("当前状态无法进行转入审批");
        }
        
        // 检查是否有权限审批（只有转入组织管理员可以审批）
        if (!hasRole("ADMIN") && !belongsToOrganization(transfer.getTransferInOrgId())) {
            return Result.forbidden("没有权限审批此转接申请");
        }
        
        // 修改参数顺序以匹配service接口
        boolean success = transferService.inApprove(id, approval, currentUserId, approvalRemark);
        return Result.success(success, success ? "转入组织审批成功" : "转入组织审批失败");
    }

    /**
     * 获取待审批的转接申请列表
     * 
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param status 状态，可选
     * @param type 审批类型（out:转出审批，in:转入审批）
     * @param currentUserId 当前用户ID（由@ModelAttribute自动提供）
     * @param currentUser 当前用户对象（由@ModelAttribute自动提供）
     * @return 待审批的转接申请列表和总数
     */
    @GetMapping("/approval")
    @Operation(summary = "获取待审批的转接申请列表", description = "分页获取需要当前用户审批的转接申请列表")
    @RequiresRole({"ADMIN", "ORGANIZATION_ADMIN", "BRANCH_ADMIN", "COLLEGE_ADMIN", "DEPUTY_COLLEGE_SECRETARY", "DEPUTY_BRANCH_SECRETARY"})
    @RequiresPermission("transfer:approval:list")
    public Result<Map<String, Object>> getApprovalList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @ModelAttribute("currentUserId") Integer currentUserId,
            @ModelAttribute("currentUser") User currentUser) {
        
        logger.debug("获取待审批列表，用户: {}, 类型: {}, 状态: {}", currentUserId, type, status);
        
        // 获取当前用户所属组织ID
        Integer orgId = currentUser != null ? currentUser.getOrganization() : null;
        if (orgId == null) {
            return Result.validateFailed("无法获取用户所属组织信息");
        }
        
        // 将status转换为Integer类型的statusCode
        Integer statusCode = null;
        if (StringUtils.hasText(status)) {
            try {
                statusCode = Integer.parseInt(status);
            } catch (NumberFormatException e) {
                logger.warn("无法将状态{}转换为整数", status);
            }
        }
        
        Page<Transfer> pageParam = new Page<>(page, size);
        IPage<Transfer> pageResult;
        
        logger.info("开始处理转接审批列表查询，用户ID: {}, 组织ID: {}, 类型: {}, 状态代码: {}", 
            currentUserId, orgId, type, statusCode);
        
        // 根据审批类型获取不同的审批列表
        if ("out".equalsIgnoreCase(type)) {
            // 获取需要转出审批的申请列表（组织ID为转出组织ID且状态为申请中）
            logger.debug("获取转出审批列表，组织ID: {}", orgId);
            List<Transfer> outgoingTransfers = transferService.getOutgoingTransfersByOrgId(orgId);
            logger.debug("获取到{}条转出审批申请", outgoingTransfers.size());
            // 创建只包含转出审批列表的分页对象
            pageResult = new Page<>(page, size, outgoingTransfers.size());
            // 手动分页
            int start = (page - 1) * size;
            int end = Math.min(start + size, outgoingTransfers.size());
            if (start < outgoingTransfers.size()) {
                pageResult.setRecords(outgoingTransfers.subList(start, end));
            }
        } else if ("in".equalsIgnoreCase(type)) {
            // 获取需要转入审批的申请列表（组织ID为转入组织ID且状态为转出审批已通过）
            logger.debug("获取转入审批列表，组织ID: {}", orgId);
            List<Transfer> incomingTransfers = transferService.getIncomingTransfersByOrgId(orgId);
            logger.debug("获取到{}条转入审批申请", incomingTransfers.size());
            // 创建只包含转入审批列表的分页对象
            pageResult = new Page<>(page, size, incomingTransfers.size());
            // 手动分页
            int start = (page - 1) * size;
            int end = Math.min(start + size, incomingTransfers.size());
            if (start < incomingTransfers.size()) {
                pageResult.setRecords(incomingTransfers.subList(start, end));
            }
        } else {
            // 获取所有需要该组织审批的申请
            logger.debug("获取所有审批列表，组织ID: {}", orgId);
            List<Transfer> outgoingTransfers = transferService.getOutgoingTransfersByOrgId(orgId);
            List<Transfer> incomingTransfers = transferService.getIncomingTransfersByOrgId(orgId);
            logger.debug("获取到{}条转出审批申请，{}条转入审批申请", outgoingTransfers.size(), incomingTransfers.size());
            
            List<Transfer> allApprovalTransfers = new java.util.ArrayList<>(outgoingTransfers);
            allApprovalTransfers.addAll(incomingTransfers);
            // 创建包含所有审批列表的分页对象
            pageResult = new Page<>(page, size, allApprovalTransfers.size());
            // 手动分页
            int start = (page - 1) * size;
            int end = Math.min(start + size, allApprovalTransfers.size());
            if (start < allApprovalTransfers.size()) {
                pageResult.setRecords(allApprovalTransfers.subList(start, end));
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        logger.info("成功获取转接审批列表，返回{}条记录", pageResult.getRecords().size());
        return Result.success(result);
    }

    /**
     * 从请求中获取当前用户ID
     */
    protected Integer getCurrentUserIdFromRequest() {
        try {
            // 从请求属性中获取用户ID
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Object userIdAttr = request.getAttribute("userId");
                
                if (userIdAttr != null) {
                    logger.debug("从请求属性中获取用户ID: {}", userIdAttr);
                    if (userIdAttr instanceof Integer) {
                        return (Integer) userIdAttr;
                    } else {
                        try {
                            return Integer.parseInt(userIdAttr.toString());
                        } catch (NumberFormatException e) {
                            logger.error("无法将userId属性转换为Integer: {}", userIdAttr);
                        }
                    }
                } else {
                    logger.debug("请求属性中没有userId");
                }
            } else {
                logger.debug("无法获取请求属性");
            }
            
            logger.warn("未能从请求中获取用户ID，返回null");
            return null;
        } catch (Exception e) {
            logger.error("获取用户ID时发生异常: {}", e.getMessage(), e);
            return null;
        }
    }
} 