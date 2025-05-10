package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorApplication;
import com.zhtj.service.HonorApplicationService;
import com.zhtj.service.UserService;
import com.zhtj.common.api.Result;
import com.zhtj.domain.User;
import com.zhtj.service.OrganizationService;
import com.zhtj.config.JwtConfig;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 荣誉管理控制器
 */
@RestController
@RequestMapping("/honors")
public class HonorController {
    
    private static final Logger logger = LoggerFactory.getLogger(HonorController.class);
    
    @Autowired
    private HonorApplicationService honorApplicationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrganizationService organizationService;
    
    @Autowired
    private JwtConfig jwtConfig;
    
    /**
     * 检查用户是否为团委书记
     */
    private boolean isCommitteeSecretary(Integer userId) {
        if (userId == null) return false;
        Set<String> roles = userService.getUserRoles(userId);

        return roles != null && (roles.contains("COLLEGE_ADMIN")||roles.contains("ADMIN")||roles.contains("SCHOOL_ADMIN"));
    }
    
    /**
     * 检查用户是否为团委副书记
     */
    private boolean isDeputyCommitteeSecretary(Integer userId) {
        if (userId == null) return false;
        Set<String> roles = userService.getUserRoles(userId);
        return roles != null && roles.contains("DEPUTY_COLLEGE_SECRETARY");
    }
    
    /**
     * 检查用户是否有审批权限
     */
    private boolean hasApprovalPermission(Integer userId) {
        return isCommitteeSecretary(userId) || isDeputyCommitteeSecretary(userId);
    }
    
    /**
     * 获取用户有权限审批的组织ID列表
     */
    private List<Integer> getAuthorizedOrganizationIds(Integer userId) {
        if (!hasApprovalPermission(userId)) {
            return List.of();
        }
        
        // 获取用户所在组织
        User user = userService.getById(userId);
        if (user == null || user.getOrganization() == null) {
            return List.of();
        }
        
        // 获取用户所在组织及其所有子组织
        return organizationService.getSelfAndChildrenOrganizationIds(user.getOrganization());
    }
    
    /**
     * 检查用户申请资格
     */
    @GetMapping("/check-eligibility")
    public Result checkEligibility(HttpServletRequest request) {
        // 从JWT令牌获取用户ID
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer userId = jwtConfig.getUserIdFromToken(token);
        
        if (userId == null) {
            logger.warn("检查用户申请资格时未获取到有效的userId，返回无资格");
            return Result.success(false);
        }
        
        logger.info("检查用户[{}]的荣誉申请资格", userId);
        String currentYear = String.valueOf(java.time.Year.now().getValue());
        boolean eligible = honorApplicationService.checkUserEligibility(userId, currentYear);
        logger.info("用户[{}]的荣誉申请资格检查结果: {}", userId, eligible);
        return Result.success(eligible);
    }
    
    /**
     * 获取我的荣誉申请
     */
    @GetMapping("/my-applications")
    public Result getMyApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        
        // 从JWT令牌获取用户ID
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer userId = jwtConfig.getUserIdFromToken(token);
        
        if (userId == null) {
            logger.warn("查询个人荣誉申请列表时未获取到有效的userId");
            return Result.failed("无法获取用户信息");
        }
        
        logger.info("用户[{}]查询个人荣誉申请列表, 页码: {}, 每页数量: {}", userId, page, size);
        Page<HonorApplication> pageObj = new Page<>(page, size);
        IPage<HonorApplication> pageResult = honorApplicationService.getByUserId(pageObj, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        logger.info("用户[{}]的荣誉申请总数: {}", userId, pageResult.getTotal());
        return Result.success(result);
    }
    
    /**
     * 创建荣誉申请
     */
    @PostMapping("/applications")
    public Result createApplication(@RequestBody Map<String, Object> requestData, HttpServletRequest request) {
        try {
            // 日志记录接收到的数据
            logger.info("接收到荣誉申请数据: {}", requestData);
            
            // 从JWT令牌获取用户信息
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            Integer userId = jwtConfig.getUserIdFromToken(token);
            
            if (userId == null) {
                logger.warn("创建荣誉申请时未获取到有效的userId");
                return Result.failed("无法获取用户信息");
            }
            
            // 获取用户信息
            User currentUser = userService.getById(userId);
            if (currentUser == null) {
                logger.warn("未找到用户信息: {}", userId);
                return Result.failed("未找到用户信息");
            }
            
            // 创建申请对象并映射字段
            HonorApplication application = new HonorApplication();
            
            // 字段映射和类型转换
            application.setHonorName((String) requestData.get("honorType"));
            application.setApplicationReason((String) requestData.get("reason"));
            
            // 使用从令牌中获取的用户ID
            application.setUserId(userId);
            application.setUserName(currentUser.getName());
            application.setOrganizationId((Integer) requestData.get("organizationId"));
            application.setOrganizationName((String) requestData.get("organizationName"));
            
            // 设置当前年度
            application.setApplicationYear(String.valueOf(java.time.Year.now().getValue()));
            
            // 设置申请时间
            application.setApplicationTime(java.time.LocalDateTime.now());
            
            // 设置初始状态
            application.setStatus("申请中");
            
            // 检查申请资格
            String currentYear = String.valueOf(java.time.Year.now().getValue());
            boolean eligible = honorApplicationService.checkUserEligibility(
                    application.getUserId(), currentYear);
            
            if (!eligible) {
                logger.warn("用户[{}]没有荣誉申请资格，当年评议结果不符合要求", application.getUserId());
                return Result.failed("您没有申请资格，需要在当年的教育评议中获得'优秀'评价");
            }
            
            Integer id = honorApplicationService.create(application);
            logger.info("用户[{}]荣誉申请创建成功，ID: {}", application.getUserId(), id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", id);
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("创建荣誉申请失败", e);
            return Result.failed("创建荣誉申请失败: " + e.getMessage());
        }
    }
    
    /**
     * 审批荣誉申请
     */
    @PostMapping("/applications/{id}/approve")
    public Result approveApplication(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> requestData,
            HttpServletRequest request) {
        
        Boolean approved = (Boolean) requestData.get("approved");
        String reason = (String) requestData.get("reason");
        
        // 从JWT令牌获取用户ID
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer userId = jwtConfig.getUserIdFromToken(token);
        
        if (userId == null) {
            logger.warn("审批荣誉申请时未获取到有效的userId");
            return Result.failed("无法获取用户信息");
        }
        
        if (approved == null) {
            return Result.failed("请提供审批结果");
        }
        
        String status = approved ? "已通过" : "已拒绝";
        logger.info("用户[{}]尝试审批荣誉申请[{}]，状态: {}", userId, id, status);
        
        // 检查审批权限（团委书记或副书记）
        if (!hasApprovalPermission(userId)) {
            logger.warn("用户[{}]没有荣誉审批权限", userId);
            return Result.failed("您没有审批权限");
        }
        
        // 检查是否有权限审批该申请
        HonorApplication application = honorApplicationService.getById(id);
        if (application == null) {
            logger.warn("荣誉申请[{}]不存在", id);
            return Result.failed("申请不存在");
        }
        
        // 获取审批人有权限的组织ID列表
        List<Integer> authorizedOrgIds = getAuthorizedOrganizationIds(userId);
        
        // 检查申请的组织ID是否在审批人权限范围内
        if (!authorizedOrgIds.contains(application.getOrganizationId())) {
            logger.warn("用户[{}]无权审批组织[{}]的荣誉申请", userId, application.getOrganizationId());
            return Result.failed("您无权审批该组织的荣誉申请");
        }
        
        boolean success = honorApplicationService.approve(id, status, reason, userId);
        
        if (success) {
            logger.info("用户[{}]成功审批荣誉申请[{}]，状态: {}", userId, id, status);
        } else {
            logger.error("用户[{}]审批荣誉申请[{}]失败", userId, id);
        }
        
        return Result.success(success);
    }
    
    /**
     * 获取申请详情
     */
    @GetMapping("/applications/{id}")
    public Result getApplication(@PathVariable Integer id) {
        logger.info("查询荣誉申请详情, ID: {}", id);
        
        HonorApplication application = honorApplicationService.getById(id);
        if (application == null) {
            logger.warn("荣誉申请[{}]不存在", id);
            return Result.failed("申请不存在");
        }
        
        logger.info("成功获取荣誉申请[{}]详情", id);
        return Result.success(application);
    }
    
    /**
     * 查询组织的申请列表（包括待审批和已审批）
     */
    @GetMapping("/pending-applications")
    public Result getPendingApplications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        
        // 从JWT令牌获取用户ID
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer userId = jwtConfig.getUserIdFromToken(token);
        
        if (userId == null) {
            logger.warn("查询申请列表时未获取到有效的userId");
            return Result.failed("无法获取用户信息");
        }
        
        // 获取用户信息及所属组织
        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            logger.warn("未找到用户信息: {}", userId);
            return Result.failed("未找到用户信息");
        }
        
        Integer organizationId = currentUser.getOrganization();
        
        // 记录请求
        logger.info("用户[{}]查询组织[{}]的申请列表，状态: {}", userId, organizationId, status);
        
        // 检查权限
        if (!hasApprovalPermission(userId)) {
            logger.warn("用户[{}]没有审批权限，无法查看申请列表", userId);
            return Result.unauthorized("没有审批权限");
        }
        
        try {
            // 将status参数转换为接口需要的格式
            if ("pending".equals(status)) {
                status = "申请中";
            } else if ("approved".equals(status)) {
                status = "已通过";
            } else if ("rejected".equals(status)) {
                status = "已拒绝";
            }
            
            // 查询申请
            Page<HonorApplication> pageObj = new Page<>(page, size);
            IPage<HonorApplication> applications = honorApplicationService.getHonorApplicationPage(
                    pageObj, null, status, null, organizationId);
            
            return Result.success(applications);
        } catch (Exception e) {
            logger.error("查询申请列表失败", e);
            return Result.failed("查询申请列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量审批荣誉申请
     */
    @PostMapping("/applications/batch-approve")
    public Result batchApproveApplications(
            @RequestBody List<Integer> ids,
            @RequestParam String status,
            @RequestParam(required = false) String comments,
            HttpServletRequest request) {
        
        // 从JWT令牌获取用户ID
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer userId = jwtConfig.getUserIdFromToken(token);
        
        if (userId == null) {
            logger.warn("批量审批荣誉申请时未获取到有效的userId");
            return Result.failed("无法获取用户信息");
        }
        
        logger.info("用户[{}]尝试批量审批荣誉申请，状态: {}, 申请数量: {}", userId, status, ids.size());
        
        // 检查审批权限
        if (!hasApprovalPermission(userId)) {
            logger.warn("用户[{}]没有荣誉审批权限", userId);
            return Result.failed("您没有审批权限");
        }
        
        // 获取审批人有权限的组织ID列表
        List<Integer> authorizedOrgIds = getAuthorizedOrganizationIds(userId);
        
        // 批量审批
        int successCount = 0;
        int failCount = 0;
        
        for (Integer id : ids) {
            HonorApplication application = honorApplicationService.getById(id);
            if (application == null) {
                logger.warn("荣誉申请[{}]不存在", id);
                failCount++;
                continue;
            }
            
            // 检查是否有权限审批该申请
            if (!authorizedOrgIds.contains(application.getOrganizationId())) {
                logger.warn("用户[{}]无权审批组织[{}]的荣誉申请[{}]", 
                    userId, application.getOrganizationId(), id);
                failCount++;
                continue;
            }
            
            boolean success = honorApplicationService.approve(id, status, comments, userId);
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        logger.info("用户[{}]批量审批完成，成功: {}, 失败: {}", userId, successCount, failCount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        
        return Result.success(result);
    }
}