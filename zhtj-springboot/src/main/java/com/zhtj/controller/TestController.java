package com.zhtj.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.User;
import com.zhtj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserService userService;

    /**
     * 测试MyBatis Plus分页查询
     */
    @GetMapping("/page")
    public Map<String, Object> testPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        // 创建分页对象
        Page<User> page = new Page<>(pageNum, pageSize);
        
        // 创建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 执行分页查询
        Page<User> userPage = userService.page(page, queryWrapper);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("total", userPage.getTotal());
        result.put("pages", userPage.getPages());
        result.put("current", userPage.getCurrent());
        result.put("records", userPage.getRecords());
        
        return result;
    }
} 