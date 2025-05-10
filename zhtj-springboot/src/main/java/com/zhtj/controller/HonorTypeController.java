package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorType;
import com.zhtj.service.HonorTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 荣誉类型控制器
 */
@RestController
@RequestMapping("/honors/types")
@Tag(name = "荣誉激励模块", description = "荣誉类型相关接口")
public class HonorTypeController {
    
    @Autowired
    private HonorTypeService honorTypeService;
    
    /**
     * 分页查询荣誉类型
     */
    @GetMapping
    @Operation(summary = "分页查询荣誉类型", description = "根据条件分页查询荣誉类型")
    public ResponseEntity<Map<String, Object>> getHonorTypePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "荣誉类别") @RequestParam(required = false) String category) {
        
        Page<HonorType> pageObj = new Page<>(page, size);
        IPage<HonorType> pageResult = honorTypeService.getHonorTypePage(pageObj, category);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取荣誉类型详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取荣誉类型详情", description = "根据ID获取荣誉类型详情")
    public ResponseEntity<HonorType> getHonorType(
            @Parameter(description = "荣誉类型ID") @PathVariable Integer id) {
        
        HonorType honorType = honorTypeService.getById(id);
        if (honorType == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(honorType);
    }
    
    /**
     * 创建荣誉类型
     */
    @PostMapping
    @Operation(summary = "创建荣誉类型", description = "创建新的荣誉类型")
    public ResponseEntity<Map<String, Object>> createHonorType(
            @Parameter(description = "荣誉类型对象") @RequestBody HonorType honorType) {
        
        Integer id = honorTypeService.create(honorType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新荣誉类型
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新荣誉类型", description = "更新指定的荣誉类型")
    public ResponseEntity<Boolean> updateHonorType(
            @Parameter(description = "荣誉类型ID") @PathVariable Integer id,
            @Parameter(description = "荣誉类型对象") @RequestBody HonorType honorType) {
        
        honorType.setId(id);
        boolean success = honorTypeService.update(honorType);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 删除荣誉类型
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除荣誉类型", description = "删除指定的荣誉类型")
    public ResponseEntity<Boolean> deleteHonorType(
            @Parameter(description = "荣誉类型ID") @PathVariable Integer id) {
        
        boolean success = honorTypeService.delete(id);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 启用/禁用荣誉类型
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新荣誉类型状态", description = "启用或禁用指定的荣誉类型")
    public ResponseEntity<Boolean> updateHonorTypeStatus(
            @Parameter(description = "荣誉类型ID") @PathVariable Integer id,
            @Parameter(description = "状态(0-禁用，1-启用)") @RequestParam Integer status) {
        
        boolean success = honorTypeService.updateStatus(id, status);
        
        return ResponseEntity.ok(success);
    }
    
    /**
     * 获取所有启用的荣誉类型
     */
    @GetMapping("/enabled")
    @Operation(summary = "获取所有启用的荣誉类型", description = "获取系统中所有启用状态的荣誉类型")
    public ResponseEntity<List<HonorType>> getAllEnabledTypes() {
        
        List<HonorType> types = honorTypeService.getAllEnabledTypes();
        
        return ResponseEntity.ok(types);
    }
    
    /**
     * 根据类别获取荣誉类型
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "根据类别获取荣誉类型", description = "获取指定类别的荣誉类型列表")
    public ResponseEntity<List<HonorType>> getTypesByCategory(
            @Parameter(description = "荣誉类别") @PathVariable String category) {
        
        List<HonorType> types = honorTypeService.getTypesByCategory(category);
        
        return ResponseEntity.ok(types);
    }
} 