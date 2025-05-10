package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.Organization;

import java.util.List;

/**
 * 组织服务接口
 */
public interface OrganizationService extends IService<Organization> {
    
    /**
     * 分页查询组织列表
     * 
     * @param page 分页参数
     * @param name 组织名称（可选）
     * @param parentId 父组织ID（可选）
     * @return 分页结果
     */
    IPage<Organization> getOrganizationPage(Page<Organization> page, String name, Integer parentId);
    
    /**
     * 获取组织树
     * 
     * @param rootId 根组织ID，如果为null则获取所有顶级组织及其子组织
     * @return 组织树列表
     */
    List<Organization> getOrganizationTree(Integer rootId);
    
    /**
     * 获取组织详情
     * 
     * @param id 组织ID
     * @return 组织详情
     */
    Organization getOrganizationById(Integer id);
    
    /**
     * 创建组织
     * 
     * @param organization 组织信息
     * @return 是否成功
     */
    boolean createOrganization(Organization organization);
    
    /**
     * 更新组织
     * 
     * @param organization 组织信息
     * @return 是否成功
     */
    boolean updateOrganization(Organization organization);
    
    /**
     * 删除组织
     * 
     * @param id 组织ID
     * @return 是否成功
     */
    boolean deleteOrganization(Integer id);
    
    /**
     * 获取父组织的直接子组织
     * 
     * @param parentId 父组织ID
     * @return 子组织列表
     */
    List<Organization> getChildrenByParentId(Integer parentId);
    
    /**
     * 更新组织成员数量
     * 
     * @param organizationId 组织ID
     * @return 是否成功
     */
    boolean updateMemberCount(Integer organizationId);
    
    /**
     * 根据组织名称获取组织ID
     * 
     * @param name 组织名称
     * @return 组织ID
     */
    Integer getByName(String name);
    
    /**
     * 根据ID获取组织信息
     * 
     * @param id 组织ID
     * @return 组织信息
     */
    Organization getById(Integer id);
    
    /**
     * 判断一个组织是否是另一个组织的下级组织
     * 
     * @param parentId 父组织ID
     * @param childId 子组织ID
     * @return 是否是下级组织
     */
    boolean isSubOrganization(Integer parentId, Integer childId);
    
    /**
     * 统计直接隶属于指定组织的成员数量
     * 
     * @param organizationId 组织ID
     * @return 直属成员数量
     */
    int countDirectMembersByOrganizationId(Integer organizationId);
    
    /**
     * 计算组织及其所有子组织的成员总数
     * 
     * @param organizationId 组织ID
     * @return 成员总数
     */
    int calculateTotalMemberCount(Integer organizationId);
    
    /**
     * 获取指定组织及其所有子组织的ID列表
     * 
     * @param organizationId 组织ID
     * @return 组织ID列表，包含自身和所有子组织
     */
    List<Integer> getSelfAndChildrenOrganizationIds(Integer organizationId);
} 