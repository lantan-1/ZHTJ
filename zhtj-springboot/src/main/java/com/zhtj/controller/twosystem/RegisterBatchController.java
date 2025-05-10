package com.zhtj.controller.twosystem;

import com.zhtj.common.api.Result;
import com.zhtj.domain.User;
import com.zhtj.config.JwtConfig;
import com.zhtj.model.twosystem.RegisterBatch;
import com.zhtj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 团籍注册批次控制器
 */
@RestController
@RequestMapping("register/batch")
public class RegisterBatchController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取注册批次列表
     */
    @GetMapping
    @PreAuthorize("@ss.hasPermi('twosystem:register:batch')")
    public Result<Object> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "batchName", required = false) String batchName,
            @RequestParam(value = "registerYear", required = false) String registerYear,
            @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request) {
        try {
            return Result.success(userService.getBatchList(page, size, batchName, registerYear, status, request));
        } catch (Exception e) {
            return Result.failed("获取批次列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取批次详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('twosystem:register:batch')")
    public Result<Object> getDetail(@PathVariable Integer id) {
        try {
            return Result.success(userService.getBatchDetail(id));
        } catch (Exception e) {
            return Result.failed("获取批次详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建批次
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('twosystem:register:batch')")
    public Result<Void> create(@RequestBody RegisterBatch batch, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return Result.unauthorized("未授权访问");
            }
            String token = authHeader.substring(7);
            Integer userId = jwtConfig.getUserIdFromToken(token);
            
            // 获取当前用户信息
            User currentUser = userService.getById(userId);
            
            // 为前端兼容性，设置batchCode字段
            String year = batch.getRegisterYear();
            String batchCode = year + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            batch.setBatchCode(batchCode);
            
            batch.setCreatorId(currentUser.getId());
            batch.setCreatorName(currentUser.getName());
            batch.setStatus("未开始");
            batch.setCreateTime(LocalDateTime.now());
            batch.setUpdateTime(LocalDateTime.now());
            
            boolean result = userService.createBatch(batch);
            return result ? Result.success() : Result.failed("创建注册批次失败");
        } catch (Exception e) {
            return Result.failed("创建注册批次失败：" + e.getMessage());
        }
    }

    /**
     * 更新批次
     */
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('twosystem:register:batch')")
    public Result<Void> update(@PathVariable Integer id, @RequestBody RegisterBatch batch) {
        try {
            batch.setId(id);
            batch.setUpdateTime(LocalDateTime.now());
            
            boolean result = userService.updateBatch(batch);
            return result ? Result.success() : Result.failed("更新注册批次失败");
        } catch (Exception e) {
            return Result.failed("更新注册批次失败：" + e.getMessage());
        }
    }

    /**
     * 删除批次
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('twosystem:register:batch')")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            boolean result = userService.deleteBatch(id);
            return result ? Result.success() : Result.failed("删除注册批次失败");
        } catch (Exception e) {
            return Result.failed("删除注册批次失败：" + e.getMessage());
        }
    }

    /**
     * 开始批次
     */
    @PostMapping("/{id}/start")
    @PreAuthorize("@ss.hasPermi('twosystem:register:batch')")
    public Result<Void> startBatch(@PathVariable Integer id) {
        try {
            boolean result = userService.startBatch(id);
            return result ? Result.success() : Result.failed("开始注册批次失败");
        } catch (Exception e) {
            return Result.failed("开始注册批次失败：" + e.getMessage());
        }
    }

    /**
     * 结束批次
     */
    @PostMapping("/{id}/end")
    @PreAuthorize("@ss.hasPermi('twosystem:register:batch')")
    public Result<Void> endBatch(@PathVariable Integer id) {
        try {
            boolean result = userService.endBatch(id);
            return result ? Result.success() : Result.failed("结束注册批次失败");
        } catch (Exception e) {
            return Result.failed("结束注册批次失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前批次
     */
    @GetMapping("/current")
    public Result<Object> getCurrentBatch() {
        try {
            return Result.success(userService.getCurrentBatch());
        } catch (Exception e) {
            return Result.failed("获取当前批次失败：" + e.getMessage());
        }
    }

    /**
     * 获取批次选项
     */
    @GetMapping("/options")
    public Result<Object> getBatchOptions() {
        try {
            return Result.success(userService.getBatchOptions());
        } catch (Exception e) {
            return Result.failed("获取批次选项失败：" + e.getMessage());
        }
    }

    /**
     * 获取批次成员列表
     */
    @GetMapping("/members")
    @PreAuthorize("@ss.hasPermi('twosystem:register:batch')")
    public Result<Object> getBatchMembers(
            @RequestParam(value = "batchId", required = true) Integer batchId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status) {
        try {
            // 使用新的专用方法获取批次成员列表
            return Result.success(userService.getBatchMemberList(batchId, page, size, keyword, status));
        } catch (Exception e) {
            return Result.failed("获取批次成员列表失败：" + e.getMessage());
        }
    }
} 