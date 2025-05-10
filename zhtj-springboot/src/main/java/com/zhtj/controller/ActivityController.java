package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.common.api.Result;
import com.zhtj.domain.Activity;
import com.zhtj.domain.User;
import com.zhtj.service.ActivityService;
import com.zhtj.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 团日活动控制器
 */
@RestController
@RequestMapping("/activities")
@Tag(name = "团日活动", description = "团日活动的增删改查接口")
public class ActivityController {
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private UserService userService;

    /**
     * 获取活动列表
     * 
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param organization 组织ID，可选
     * @param category 活动类别，可选
     * @param startDate 开始日期，可选
     * @param endDate 结束日期，可选
     * @return 活动列表和总数
     */
    @GetMapping
    @Operation(summary = "获取活动列表", description = "分页获取活动列表，可根据组织、类别、日期范围筛选")
    public Result<Map<String, Object>> getActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer organization,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        Page<Activity> pageParam = new Page<>(page, size);
        IPage<Activity> pageResult = activityService.getActivityPage(pageParam, organization, category, startDate, endDate);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return Result.success(result);
    }

    /**
     * 获取活动详情
     * 
     * @param id 活动ID
     * @return 活动详细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取活动详情", description = "根据活动ID获取详细信息")
    public Result<Activity> getActivityDetail(@PathVariable Integer id) {
        Activity activity = activityService.getActivityDetail(id);
        return Result.success(activity);
    }

    /**
     * 创建活动
     * 
     * @param activity 活动信息
     * @return 活动ID
     */
    @PostMapping
    @Operation(summary = "创建活动", description = "创建新的团日活动")
    public Result<Map<String, Object>> createActivity(@RequestBody Activity activity) {
        // 参数验证
        if (activity.getOrganization() == null) {
            return Result.validateFailed("组织ID不能为空");
        }
        if (!StringUtils.hasText(activity.getCategory())) {
            return Result.validateFailed("活动类别不能为空");
        }
        if (!StringUtils.hasText(activity.getDate())) {
            return Result.validateFailed("活动日期不能为空");
        }
        
        boolean success = activityService.save(activity);
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", activity.getId());
            return Result.success(data, "创建活动成功");
        } else {
            return Result.failed("创建活动失败");
        }
    }

    /**
     * 更新活动
     * 
     * @param id 活动ID
     * @param activity 活动信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新活动", description = "更新已有的团日活动")
    public Result<Boolean> updateActivity(
            @PathVariable Integer id,
            @RequestBody Activity activity) {
        
        // 设置ID
        activity.setId(id);
        
        // 参数验证
        if (activity.getOrganization() == null) {
            return Result.validateFailed("组织ID不能为空");
        }
        if (!StringUtils.hasText(activity.getCategory())) {
            return Result.validateFailed("活动类别不能为空");
        }
        if (!StringUtils.hasText(activity.getDate())) {
            return Result.validateFailed("活动日期不能为空");
        }
        
        boolean success = activityService.update(activity);
        return Result.success(success, success ? "更新活动成功" : "更新活动失败");
    }

    /**
     * 删除活动
     * 
     * @param id 活动ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除活动", description = "删除指定的团日活动")
    public Result<Boolean> deleteActivity(@PathVariable Integer id) {
        boolean success = activityService.delete(id);
        return Result.success(success, success ? "删除活动成功" : "删除活动失败");
    }

    /**
     * 根据日期获取活动
     * 
     * @param organization 组织ID
     * @param date 日期
     * @return 活动列表
     */
    @GetMapping("/date")
    @Operation(summary = "根据日期获取活动", description = "根据组织ID和日期获取活动列表")
    public Result<List<Activity>> getActivitiesByDate(
            @RequestParam Integer organization,
            @RequestParam String date) {
        
        if (organization == null) {
            return Result.validateFailed("组织ID不能为空");
        }
        if (!StringUtils.hasText(date)) {
            return Result.validateFailed("日期不能为空");
        }
        
        List<Activity> activities = activityService.getAllByDate(organization, date);
        return Result.success(activities);
    }

    /**
     * 按类别统计活动数量
     * 
     * @param organization 组织ID
     * @return 各类别的活动数量
     */
    @GetMapping("/statistics/category")
    @Operation(summary = "按类别统计活动数量", description = "统计指定组织各类别的活动数量")
    public Result<Map<String, Object>> getActivityStatisticsByCategory(@RequestParam Integer organization) {
        if (organization == null) {
            return Result.validateFailed("组织ID不能为空");
        }
        
        Map<String, Integer> categoryCount = activityService.countByCategory(organization);
        
        // 转换为前端所需格式
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        return Result.success(result);
    }
} 