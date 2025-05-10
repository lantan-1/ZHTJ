package com.zhtj.controller.twosystem;

import com.zhtj.common.api.Result;
import com.zhtj.domain.User;
import com.zhtj.config.JwtConfig;
import com.zhtj.model.twosystem.EvaluationOption;
import com.zhtj.model.twosystem.EvaluationResult;
import com.zhtj.model.twosystem.MemberEvaluation;
import com.zhtj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 团员教育评议控制器
 */
@RestController
@RequestMapping("/member-evaluations")
public class MemberEvaluationController {
    
    private static final Logger log = LoggerFactory.getLogger(MemberEvaluationController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取评议活动列表
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:list')")
    public Result<Map<String, Object>> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "organizationId", required = false) Integer organizationId) {
        return Result.success(userService.getEvaluationList(page, size, year, status, organizationId));
    }

    /**
     * 获取评议活动详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:list')")
    public Result<MemberEvaluation> getDetail(@PathVariable Integer id) {
        MemberEvaluation evaluation = userService.getEvaluationDetail(id);
        
        // 直接返回evaluation对象，不需要手动映射initiator_id/initiator_name到creatorId/creatorName
        // MyBatis-Plus的@TableField注解会自动处理字段映射
        return Result.success(evaluation);
    }

    /**
     * 创建评议活动
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:add')")
    public Result<Void> create(@RequestBody MemberEvaluation evaluation, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer userId = jwtConfig.getUserIdFromToken(token);
        
        // 获取当前用户信息
        User currentUser = userService.getById(userId);
        
        // 只设置initiatorId和initiatorName字段
        evaluation.setInitiatorId(currentUser.getId());
        evaluation.setInitiatorName(currentUser.getName());
        
        evaluation.setStatus("草稿");
        evaluation.setCreateTime(LocalDateTime.now());
        evaluation.setUpdateTime(LocalDateTime.now());
        
        boolean result = userService.createEvaluation(evaluation);
        return result ? Result.success() : Result.failed("创建评议活动失败");
    }

    /**
     * 更新评议活动
     */
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:edit')")
    public Result<Void> update(@PathVariable Integer id, @RequestBody MemberEvaluation evaluation) {
        evaluation.setId(id);
        evaluation.setUpdateTime(LocalDateTime.now());
        
        boolean result = userService.updateEvaluation(evaluation);
        return result ? Result.success() : Result.failed("更新评议活动失败");
    }

    /**
     * 删除评议活动
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:remove')")
    public Result<Void> delete(@PathVariable Integer id) {
        boolean result = userService.deleteEvaluation(id);
        return result ? Result.success() : Result.failed("删除评议活动失败");
    }

    /**
     * 开始评议活动
     */
    @PostMapping("/{id}/start")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:edit')")
    public Result<Void> startEvaluation(@PathVariable Integer id) {
        boolean result = userService.startEvaluation(id);
        return result ? Result.success() : Result.failed("开始评议活动失败");
    }

    /**
     * 结束评议活动
     */
    @PostMapping("/{id}/end")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:edit')")
    public Result<Void> endEvaluation(@PathVariable Integer id) {
        boolean result = userService.endEvaluation(id);
        return result ? Result.success() : Result.failed("结束评议活动失败");
    }

    /**
     * 取消评议活动
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:edit')")
    public Result<Void> cancelEvaluation(@PathVariable Integer id) {
        boolean result = userService.cancelEvaluation(id);
        return result ? Result.success() : Result.failed("取消评议活动失败");
    }

    /**
     * 获取评议结果列表
     */
    @GetMapping("/{evaluationId}/results")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:list')")
    public Result<Map<String, Object>> getResultList(
            @PathVariable Integer evaluationId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "memberName", required = false) String memberName,
            @RequestParam(value = "result", required = false) String result) {
        Map<String, Object> resultData = userService.getResultList(evaluationId, page, size, status, memberName, result);
        
        // 添加日志，记录返回的数据
        if (log.isDebugEnabled()) {
            log.debug("评议结果数据: total={}, listSize={}", 
                    resultData.get("total"), 
                    resultData.containsKey("list") ? ((List<?>) resultData.get("list")).size() : "无数据");
        }
        
        return Result.success(resultData);
    }

    /**
     * 提交评议结果
     */
    @PostMapping("/{evaluationId}/results")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:edit')")
    public Result<Void> submitResult(@PathVariable Integer evaluationId, @RequestBody List<EvaluationResult> results, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Integer userId = jwtConfig.getUserIdFromToken(token);
        
        // 获取当前用户信息
        User currentUser = userService.getById(userId);
        
        for (EvaluationResult result : results) {
            // 如果有ID字段，则认为是更新操作，不强制检查memberId
            if(result.getId() != null) {
                log.debug("更新评议结果，ID: {}", result.getId());
            }
            // 如果没有ID字段，且没有设置memberId，则视为错误
            else if (result.getMemberId() == null) {
                log.warn("评议结果缺少memberId字段，可能导致插入失败");
                return Result.failed("评议结果缺少必要的团员ID字段");
            }
            
            result.setEvaluationId(evaluationId);
            result.setEvaluatorId(currentUser.getId());
            result.setEvaluatorName(currentUser.getName());
            result.setEvaluationTime(LocalDateTime.now());
            result.setStatus("已评议");
            result.setCreateTime(LocalDateTime.now());
            result.setUpdateTime(LocalDateTime.now());
        }
        
        boolean success = userService.submitResults(results);
        return success ? Result.success() : Result.failed("提交评议结果失败");
    }

    /**
     * 批量提交评议结果
     */
    @PostMapping("/{evaluationId}/batch-results")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:edit')")
    public Result<Void> submitBatchResult(@PathVariable Integer evaluationId, @RequestBody Map<String, Object> batchData, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").replace("Bearer ", "");
            Integer userId = jwtConfig.getUserIdFromToken(token);
            
            // 获取当前用户信息
            User currentUser = userService.getById(userId);
            
            @SuppressWarnings("unchecked")
            List<Integer> memberIds = (List<Integer>) batchData.get("memberIds");
            String result = (String) batchData.get("result");
            String comment = (String) batchData.get("comment");
            
            // 如果提供了评议记录ID列表，使用ID更新
            @SuppressWarnings("unchecked")
            List<Integer> recordIds = (List<Integer>) batchData.get("recordIds");
            
            // 如果既没有memberIds也没有recordIds，返回错误
            if ((memberIds == null || memberIds.isEmpty()) && (recordIds == null || recordIds.isEmpty())) {
                return Result.validateFailed("缺少必要参数(memberIds或recordIds)");
            }
            
            if (result == null) {
                return Result.validateFailed("缺少必要参数(result)");
            }
            
            List<EvaluationResult> results = new ArrayList<>();
            
            // 如果有记录ID，基于ID构建评议结果
            if (recordIds != null && !recordIds.isEmpty()) {
                for (Integer recordId : recordIds) {
                    EvaluationResult evalResult = new EvaluationResult();
                    evalResult.setId(recordId);
                    evalResult.setEvaluationId(evaluationId);
                    evalResult.setResult(result);
                    evalResult.setComment(comment);
                    evalResult.setEvaluatorId(currentUser.getId());
                    evalResult.setEvaluatorName(currentUser.getName());
                    evalResult.setEvaluationTime(LocalDateTime.now());
                    evalResult.setStatus("已评议");
                    evalResult.setUpdateTime(LocalDateTime.now());
                    results.add(evalResult);
                }
            } 
            // 否则基于memberIds构建评议结果
            else if (memberIds != null && !memberIds.isEmpty()) {
                results = memberIds.stream().map(memberId -> {
                EvaluationResult evalResult = new EvaluationResult();
                evalResult.setEvaluationId(evaluationId);
                evalResult.setMemberId(memberId);
                evalResult.setResult(result);
                evalResult.setComment(comment);
                evalResult.setEvaluatorId(currentUser.getId());
                evalResult.setEvaluatorName(currentUser.getName());
                evalResult.setEvaluationTime(LocalDateTime.now());
                evalResult.setStatus("已评议");
                evalResult.setCreateTime(LocalDateTime.now());
                evalResult.setUpdateTime(LocalDateTime.now());
                return evalResult;
                }).collect(Collectors.toList());
            }
            
            boolean success = userService.submitResults(results);
            return success ? Result.success() : Result.failed("批量提交评议结果失败");
        } catch (Exception e) {
            return Result.failed("批量提交评议结果失败: " + e.getMessage());
        }
    }

    /**
     * 重置评议结果
     */
    @PostMapping("/{evaluationId}/reset")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:edit')")
    public Result<Void> resetEvaluation(@PathVariable Integer evaluationId, @RequestBody Map<String, Object> data) {
        Integer memberId = (Integer) data.get("memberId");
        if (memberId == null) {
            return Result.validateFailed("缺少必要参数");
        }
        
        boolean success = userService.resetEvaluation(evaluationId, memberId);
        return success ? Result.success() : Result.failed("重置评议结果失败");
    }

    /**
     * 发送评议提醒
     */
    @PostMapping("/{evaluationId}/reminder")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:edit')")
    public Result<Void> sendReminder(@PathVariable Integer evaluationId) {
        boolean success = userService.sendEvaluationReminder(evaluationId);
        return success ? Result.success() : Result.failed("发送评议提醒失败");
    }

    /**
     * 导出评议结果
     */
    @GetMapping("/{evaluationId}/export")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:export')")
    public Result<String> exportResults(@PathVariable Integer evaluationId) {
        String url = userService.exportEvaluationResults(evaluationId);
        return Result.success(url);
    }

    /**
     * 获取指定评议活动的统计数据
     */
    @GetMapping("/{id}/statistics")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:list')")
    public Result<Map<String, Object>> getEvaluationStatistics(@PathVariable("id") Integer id) {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalCount", 0);
        statistics.put("completedCount", 0);
        statistics.put("overallProgress", 0);
        statistics.put("excellentCount", 0);
        statistics.put("excellentRate", 0);
        statistics.put("qualifiedCount", 0);
        statistics.put("qualifiedRate", 0);
        statistics.put("basicQualifiedCount", 0);
        statistics.put("basicQualifiedRate", 0);
        statistics.put("unqualifiedCount", 0);
        statistics.put("unqualifiedRate", 0);
        
        try {
            // 这里调用userService获取评议结果列表
            Map<String, Object> results = userService.getResultList(id, 1, 1000, null, null, null);
            if (results != null) {
                log.debug("获取到评议结果: {}", results);
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) results.get("items");
                
                if (items != null && !items.isEmpty()) {
                    int total = items.size();
                    int completed = 0;
                    int excellent = 0;
                    int qualified = 0;
                    int basicQualified = 0;
                    int unqualified = 0;
                    
                    for (Map<String, Object> item : items) {
                        if ("已评议".equals(item.get("status"))) {
                            completed++;
                            String result = (String) item.get("result");
                            if ("优秀".equals(result)) {
                                excellent++;
                            } else if ("合格".equals(result)) {
                                qualified++;
                            } else if ("基本合格".equals(result)) {
                                basicQualified++;
                            } else if ("不合格".equals(result)) {
                                unqualified++;
                            }
                        }
                    }
                    
                    statistics.put("totalCount", total);
                    statistics.put("completedCount", completed);
                    statistics.put("overallProgress", total > 0 ? (completed * 100) / total : 0);
                    statistics.put("excellentCount", excellent);
                    statistics.put("excellentRate", completed > 0 ? (excellent * 100) / completed : 0);
                    statistics.put("qualifiedCount", qualified + excellent);
                    statistics.put("qualifiedRate", completed > 0 ? ((qualified + excellent) * 100) / completed : 0);
                    statistics.put("basicQualifiedCount", basicQualified);
                    statistics.put("basicQualifiedRate", completed > 0 ? (basicQualified * 100) / completed : 0);
                    statistics.put("unqualifiedCount", unqualified);
                    statistics.put("unqualifiedRate", completed > 0 ? (unqualified * 100) / completed : 0);
                } else {
                    log.warn("评议结果列表为空");
                    
                    // 如果没有从userService获取到数据，尝试从数据库直接查询
                    Map<String, Object> dbResults = userService.getEvaluationResultsFromDb(id);
                    if (dbResults != null && !dbResults.isEmpty()) {
                        statistics = dbResults;
                    }
                }
            } else {
                log.warn("获取评议结果失败，结果为null");
            }
        } catch (Exception e) {
            log.error("获取评议统计数据失败: {}", e.getMessage(), e);
        }
        
        return Result.success(statistics);
    }

    /**
     * 获取指定组织的评议统计数据
     */
    @GetMapping("/organization/{organizationId}/statistics")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:list')")
    public Result<Map<String, Object>> getOrganizationStatistics(@PathVariable("organizationId") Integer organizationId,
                                                      @RequestParam(value = "year", required = false) String year,
                                                      @RequestParam(value = "timeRange", defaultValue = "year") String timeRange) {
        Map<String, Object> data = userService.getStatisticsData(year, organizationId, timeRange);
        return Result.success(data);
    }

    /**
     * 导出统计数据
     */
    @GetMapping("/statistics/export")
    @PreAuthorize("@ss.hasPermi('twosystem:evaluation:export')")
    public Result<String> exportStatistics(
            @RequestParam(value = "year", required = false) String year,
            @RequestParam(value = "organizationId", required = false) Integer organizationId,
            @RequestParam(value = "timeRange", defaultValue = "year") String timeRange) {
        String url = userService.exportStatistics(year, organizationId, timeRange);
        return Result.success(url);
    }
} 