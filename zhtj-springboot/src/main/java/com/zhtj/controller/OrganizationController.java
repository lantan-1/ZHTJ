package com.zhtj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.common.api.Result;
import com.zhtj.domain.Organization;
import com.zhtj.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织控制器
 */
@RestController
@RequestMapping("/organizations")
@Tag(name = "组织管理", description = "组织机构的增删改查接口")
public class OrganizationController {
    
    @Autowired
    private OrganizationService organizationService;
    
    /**
     * 获取组织列表
     * 
     * @param page 页码，默认1
     * @param size 每页数量，默认10
     * @param name 组织名称，可选
     * @param parentId 上级组织ID，可选
     * @return 组织列表和总数
     */
    @GetMapping
    @Operation(summary = "获取组织列表", description = "分页获取组织列表，可根据名称、上级组织筛选")
    public Result<Map<String, Object>> getOrganizations(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer parentId) {
        
        Page<Organization> pageParam = new Page<>(page, size);
        IPage<Organization> pageResult = organizationService.getOrganizationPage(pageParam, name, parentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", pageResult.getTotal());
        result.put("list", pageResult.getRecords());
        
        return Result.success(result);
    }
    
    /**
     * 获取组织树结构
     * 
     * @param rootId 根组织ID，默认为顶级组织
     * @return 组织树结构
     */
    @GetMapping("/tree")
    @Operation(summary = "获取组织树结构", description = "获取组织的树形结构，可指定根组织ID")
    public Result<List<Organization>> getOrganizationTree(
            @RequestParam(required = false) Integer rootId) {
        
        List<Organization> tree = organizationService.getOrganizationTree(rootId);
        return Result.success(tree);
    }
    
    /**
     * 获取组织详情
     * 
     * @param id 组织ID
     * @return 组织详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取组织详情", description = "根据ID获取组织详细信息")
    public Result<Organization> getOrganizationDetail(@PathVariable Integer id) {
        Organization organization = organizationService.getOrganizationById(id);
        return Result.success(organization);
    }
    
    /**
     * 创建组织
     * 
     * @param organization 组织信息
     * @return 组织ID
     */
    @PostMapping
    @Operation(summary = "创建组织", description = "创建新组织")
    public Result<Map<String, Object>> createOrganization(@RequestBody Organization organization) {
        // 参数验证
        if (!StringUtils.hasText(organization.getName())) {
            return Result.validateFailed("组织名称不能为空");
        }
        if (!StringUtils.hasText(organization.getOrgType())) {
            return Result.validateFailed("组织类型不能为空");
        }
        
        boolean success = organizationService.createOrganization(organization);
        
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", organization.getId());
            return Result.success(data, "创建组织成功");
        } else {
            return Result.failed("创建组织失败");
        }
    }
    
    /**
     * 更新组织
     * 
     * @param id 组织ID
     * @param organization 组织信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新组织", description = "更新组织信息")
    public Result<Boolean> updateOrganization(
            @PathVariable Integer id,
            @RequestBody Organization organization) {
        
        organization.setId(id);
        boolean success = organizationService.updateOrganization(organization);
        return Result.success(success, success ? "更新组织成功" : "更新组织失败");
    }
    
    /**
     * 删除组织
     * 
     * @param id 组织ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除组织", description = "删除指定组织")
    public Result<Boolean> deleteOrganization(@PathVariable Integer id) {
        boolean success = organizationService.deleteOrganization(id);
        return Result.success(success, success ? "删除组织成功" : "删除组织失败");
    }
    
    /**
     * 获取组织统计数据
     * 
     * @param id 组织ID
     * @return 组织统计数据
     */
    @GetMapping("/{id}/statistics")
    @Operation(summary = "获取组织统计数据", description = "获取组织的统计数据，包括成员数量、活动数量等")
    public Result<Map<String, Object>> getOrganizationStatistics(@PathVariable Integer id) {
        // 验证组织ID
        Organization organization = organizationService.getOrganizationById(id);
        if (organization == null) {
            return Result.validateFailed("组织不存在");
        }
        
        // 构建统计数据
        Map<String, Object> statistics = new HashMap<>();
        
        // 基本信息
        statistics.put("id", organization.getId());
        statistics.put("name", organization.getName());
        statistics.put("fullName", organization.getFullName());
        statistics.put("orgType", organization.getOrgType());
        
        // 获取子组织
        List<Organization> children = organizationService.getChildrenByParentId(id);
        int childrenCount = children != null ? children.size() : 0;
        statistics.put("childrenCount", childrenCount);
        
        // 1. 计算直属子组织数量 - 所有直接子组织的数量
        int branchCount = childrenCount; // 直接使用childrenCount，表示所有直接子组织数量
        statistics.put("branchCount", branchCount);
        
        // 2. 获取直属成员数量 - 直接隶属于当前组织的成员数量（从user表查询）
        int directMemberCount = organizationService.countDirectMembersByOrganizationId(id);
        statistics.put("directMemberCount", directMemberCount);
        
        // 3. 团员总人数 - 当前组织及其所有子组织的成员总数
        // 方法一：使用数据库中存储的值（如果数据库值不准确，可能会显示错误）
        // int memberCount = organization.getMemberCount() != null ? organization.getMemberCount() : 0;
        
        // 方法二：使用服务方法计算组织及其所有子组织的成员总数
        int memberCount = organizationService.calculateTotalMemberCount(id);
        statistics.put("memberCount", memberCount);
        
        // 可以在此处添加更多统计数据，如活动数量、评议数量等
        // 这需要注入其他服务来获取相关数据
        
        return Result.success(statistics);
    }
} 