package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.VolunteerService;
import com.zhtj.service.VolunteerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 志愿服务记录控制器
 */
@RestController
@RequestMapping("/api/volunteer-services")
@Tag(name = "志愿服务模块", description = "志愿服务记录相关接口")
public class VolunteerController {
    
    @Autowired
    private VolunteerServiceService volunteerServiceService;
    
    /**
     * 分页查询志愿服务记录
     */
    @GetMapping
    @Operation(summary = "分页查询志愿服务记录", description = "根据条件分页查询志愿服务记录")
    public ResponseEntity<Map<String, Object>> getVolunteerServicePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Integer userId,
            @Parameter(description = "组织ID") @RequestParam(required = false) Integer organizationId,
            @Parameter(description = "服务年度") @RequestParam(required = false) String serviceYear) {
        
        Page<VolunteerService> pageObj = new Page<>(page, size);
        IPage<VolunteerService> pageResult = volunteerServiceService.getVolunteerServicePage(
                pageObj, userId, organizationId, serviceYear);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取指定志愿服务记录
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取志愿服务记录详情", description = "根据ID获取志愿服务记录详情")
    public ResponseEntity<VolunteerService> getVolunteerService(
            @Parameter(description = "记录ID") @PathVariable Integer id) {
        
        VolunteerService volunteerService = volunteerServiceService.getById(id);
        if (volunteerService == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(volunteerService);
    }
    
    /**
     * 创建志愿服务记录
     */
    @PostMapping
    @Operation(summary = "创建志愿服务记录", description = "创建新的志愿服务记录")
    public ResponseEntity<Map<String, Object>> createVolunteerService(
            @Parameter(description = "志愿服务记录对象") @RequestBody VolunteerService volunteerService) {
        
        Integer id = volunteerServiceService.create(volunteerService);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新志愿服务记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新志愿服务记录", description = "更新指定的志愿服务记录")
    public ResponseEntity<Boolean> updateVolunteerService(
            @Parameter(description = "记录ID") @PathVariable Integer id,
            @Parameter(description = "志愿服务记录对象") @RequestBody VolunteerService volunteerService) {
        
        volunteerService.setId(id);
        boolean success = volunteerServiceService.update(volunteerService);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 删除志愿服务记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除志愿服务记录", description = "删除指定的志愿服务记录")
    public ResponseEntity<Boolean> deleteVolunteerService(
            @Parameter(description = "记录ID") @PathVariable Integer id) {
        
        boolean success = volunteerServiceService.delete(id);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 验证志愿服务记录
     */
    @PostMapping("/{id}/verify")
    @Operation(summary = "验证志愿服务记录", description = "验证指定的志愿服务记录")
    public ResponseEntity<Boolean> verifyVolunteerService(
            @Parameter(description = "记录ID") @PathVariable Integer id,
            @Parameter(description = "验证状态") @RequestParam Integer verificationStatus,
            @Parameter(description = "验证备注") @RequestParam(required = false) String verificationRemark,
            @Parameter(description = "验证人ID") @RequestParam Integer verifierUserId) {
        
        boolean success = volunteerServiceService.verify(id, verificationStatus, verificationRemark, verifierUserId);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 获取用户志愿服务总时长
     */
    @GetMapping("/user/{userId}/hours")
    @Operation(summary = "获取用户志愿服务总时长", description = "获取指定用户的志愿服务总时长")
    public ResponseEntity<Double> getUserTotalServiceHours(
            @Parameter(description = "用户ID") @PathVariable Integer userId) {
        
        Double totalHours = volunteerServiceService.getTotalServiceHoursByUserId(userId);
        
        return ResponseEntity.ok(totalHours);
    }
    
    /**
     * 获取组织志愿服务总时长
     */
    @GetMapping("/organization/{organizationId}/hours")
    @Operation(summary = "获取组织志愿服务总时长", description = "获取指定组织的志愿服务总时长")
    public ResponseEntity<Double> getOrganizationTotalServiceHours(
            @Parameter(description = "组织ID") @PathVariable Integer organizationId) {
        
        Double totalHours = volunteerServiceService.getTotalServiceHoursByOrganizationId(organizationId);
        
        return ResponseEntity.ok(totalHours);
    }
} 