package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorAward;
import com.zhtj.service.HonorAwardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 荣誉授予记录控制器
 */
@RestController
@RequestMapping("/honors/awards")
@Tag(name = "荣誉激励模块", description = "荣誉授予记录相关接口")
public class HonorAwardController {
    
    @Autowired
    private HonorAwardService honorAwardService;
    
    /**
     * 分页查询荣誉授予记录
     */
    @GetMapping
    @Operation(summary = "分页查询荣誉授予记录", description = "根据条件分页查询荣誉授予记录")
    public ResponseEntity<Map<String, Object>> getHonorAwardPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "用户ID") @RequestParam(required = false) Integer userId,
            @Parameter(description = "组织ID") @RequestParam(required = false) Integer organizationId,
            @Parameter(description = "授予年度") @RequestParam(required = false) String awardYear,
            @Parameter(description = "荣誉类型ID") @RequestParam(required = false) Integer honorTypeId) {
        
        Page<HonorAward> pageObj = new Page<>(page, size);
        IPage<HonorAward> pageResult = honorAwardService.getHonorAwardPage(
                pageObj, userId, organizationId, awardYear, honorTypeId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取荣誉授予记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取荣誉授予记录详情", description = "根据ID获取荣誉授予记录详情")
    public ResponseEntity<HonorAward> getHonorAward(
            @Parameter(description = "记录ID") @PathVariable Integer id) {
        
        HonorAward honorAward = honorAwardService.getById(id);
        if (honorAward == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(honorAward);
    }
    
    /**
     * 创建荣誉授予记录
     */
    @PostMapping
    @Operation(summary = "创建荣誉授予记录", description = "创建新的荣誉授予记录")
    public ResponseEntity<Map<String, Object>> createHonorAward(
            @Parameter(description = "荣誉授予记录对象") @RequestBody HonorAward honorAward) {
        
        Integer id = honorAwardService.create(honorAward);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新荣誉授予记录
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新荣誉授予记录", description = "更新指定的荣誉授予记录")
    public ResponseEntity<Boolean> updateHonorAward(
            @Parameter(description = "记录ID") @PathVariable Integer id,
            @Parameter(description = "荣誉授予记录对象") @RequestBody HonorAward honorAward) {
        
        honorAward.setId(id);
        boolean success = honorAwardService.update(honorAward);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 删除荣誉授予记录
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除荣誉授予记录", description = "删除指定的荣誉授予记录")
    public ResponseEntity<Boolean> deleteHonorAward(
            @Parameter(description = "记录ID") @PathVariable Integer id) {
        
        boolean success = honorAwardService.delete(id);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 根据荣誉申请ID获取授予记录
     */
    @GetMapping("/application/{applicationId}")
    @Operation(summary = "根据申请ID获取授予记录", description = "根据荣誉申请ID获取相应的荣誉授予记录")
    public ResponseEntity<HonorAward> getByApplicationId(
            @Parameter(description = "申请ID") @PathVariable Integer applicationId) {
        
        HonorAward honorAward = honorAwardService.getByApplicationId(applicationId);
        if (honorAward == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(honorAward);
    }
    
    /**
     * 获取用户的荣誉授予记录
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户荣誉授予记录", description = "获取指定用户的荣誉授予记录列表")
    public ResponseEntity<Map<String, Object>> getByUserId(
            @Parameter(description = "用户ID") @PathVariable Integer userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        Page<HonorAward> pageObj = new Page<>(page, size);
        IPage<HonorAward> pageResult = honorAwardService.getByUserId(pageObj, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取组织的荣誉授予记录
     */
    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "获取组织荣誉授予记录", description = "获取指定组织的荣誉授予记录列表")
    public ResponseEntity<Map<String, Object>> getByOrganizationId(
            @Parameter(description = "组织ID") @PathVariable Integer organizationId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        
        Page<HonorAward> pageObj = new Page<>(page, size);
        IPage<HonorAward> pageResult = honorAwardService.getByOrganizationId(pageObj, organizationId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return ResponseEntity.ok(result);
    }
} 