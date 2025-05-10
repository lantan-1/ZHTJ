package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.common.exception.BusinessException;
import com.zhtj.common.exception.ResourceNotFoundException;
import com.zhtj.domain.Organization;
import com.zhtj.mapper.OrganizationMapper;
import com.zhtj.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 组织服务实现类
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    private static final Logger log = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Autowired
    private OrganizationMapper organizationMapper;

    // 辅助方法，通过名称获取组织ID，不是接口方法
    @Override
    public Integer getByName(String name) {
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Organization::getName, name);
        queryWrapper.select(Organization::getId);
        Organization org = this.getOne(queryWrapper);
        return org != null ? org.getId() : null;
    }

    // 辅助方法，根据ID获取组织名称，不是接口方法
    public String getOrganizationName(Integer id) {
        Organization org = this.getById(id);
        return org != null ? org.getName() : null;
    }
    
    @Override
    public Organization getOrganizationById(Integer id) {
        if (id == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        Organization organization = organizationMapper.selectOrganizationDetail(id);
        System.out.println("organization===="+organization);
        if (organization == null) {
            throw new ResourceNotFoundException("组织", "id", id);
        }
        
        return organization;
    }
    
    // 辅助方法，获取根组织列表，不是接口方法
    public List<Organization> getRootOrganizations() {
        return organizationMapper.getRootOrganizations();
    }
    
    @Override
    public List<Organization> getChildrenByParentId(Integer parentId) {
        if (parentId == null) {
            throw new BusinessException("父组织ID不能为空");
        }
        return organizationMapper.getChildrenByParentId(parentId);
    }
    
    // 辅助方法，更新组织成员数量，不是接口方法
    public boolean updateMemberCountWithValue(Integer organizationId, Integer count) {
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        if (count == null || count < 0) {
            throw new BusinessException("团员数量必须大于等于0");
        }
        return organizationMapper.updateMemberCount(organizationId, count) > 0;
    }
    
    @Override
    public IPage<Organization> getOrganizationPage(Page<Organization> page, String name, Integer parentId) {
        return organizationMapper.selectOrganizationPage(page, name, parentId);
    }
    
    // 辅助方法，根据路径获取子组织列表，不是接口方法
    public List<Organization> getAllChildrenByPath(String path) {
        if (!StringUtils.hasText(path)) {
            throw new BusinessException("组织路径不能为空");
        }
        return organizationMapper.getAllChildrenByPath(path);
    }
    
    // 辅助方法，保存组织信息，不是接口方法
    public boolean saveOrganization(Organization organization) {
        if (organization == null) {
            throw new BusinessException("组织信息不能为空");
        }
        
        // 检查组织名称是否已存在
        if (StringUtils.hasText(organization.getName()) && getByName(organization.getName()) != null) {
            throw new BusinessException("组织名称已存在");
        }
        
        // 设置创建时间
        organization.setCreateTime(LocalDateTime.now());
        
        // 设置默认值
        if (organization.getStatus() == null) {
            organization.setStatus(true); // 默认启用
        }
        
        if (organization.getIsLeaf() == null) {
            organization.setIsLeaf(true); // 默认为叶子节点
        }
        
        // 如果有父组织，设置path和level
        if (organization.getParentId() != null) {
            Organization parent = super.getById(organization.getParentId());
            if (parent == null) {
                throw new ResourceNotFoundException("父组织", "id", organization.getParentId());
            }
            
            // 将父组织设置为非叶子节点
            if (parent.getIsLeaf()) {
                parent.setIsLeaf(false);
                this.updateById(parent);
            }
            
            // 设置path
            if (StringUtils.hasText(parent.getPath())) {
                organization.setPath(parent.getPath() + "," + parent.getId());
            } else {
                organization.setPath(parent.getId().toString());
            }
            
            // 设置level
            organization.setLevel(parent.getLevel() != null ? parent.getLevel() + 1 : 1);
        } else {
            // 根组织
            organization.setLevel(0);
            // 保存后再设置path
        }
        
        boolean result = this.save(organization);
        
        // 对于根组织，需要在保存后设置path（因为需要用到自己的ID）
        if (result && organization.getParentId() == null) {
            organization.setPath(organization.getId().toString());
            this.updateById(organization);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean updateOrganization(Organization organization) {
        if (organization == null || organization.getId() == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        Organization existingOrg = this.getById(organization.getId());
        if (existingOrg == null) {
            throw new ResourceNotFoundException("组织", "id", organization.getId());
        }
        
        // 检查组织名称是否已被其他组织使用
        if (StringUtils.hasText(organization.getName()) && !organization.getName().equals(existingOrg.getName())) {
            Integer existingId = getByName(organization.getName());
            if (existingId != null && !existingId.equals(organization.getId())) {
                throw new BusinessException("组织名称已被其他组织使用");
            }
        }
        
        // 设置更新时间
        organization.setUpdateTime(LocalDateTime.now());
        
        // 不允许修改path、level这类由系统维护的字段
        if (existingOrg.getPath() != null) {
            organization.setPath(existingOrg.getPath());
        }
        organization.setLevel(existingOrg.getLevel());
        
        return this.updateById(organization);
    }
    
    @Override
    @Transactional
    public boolean deleteOrganization(Integer id) {
        if (id == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        Organization org = this.getById(id);
        if (org == null) {
            throw new ResourceNotFoundException("组织", "id", id);
        }
        
        // 检查是否有子组织
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Organization::getParentId, id);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException("该组织下还有子组织，无法删除");
        }
        
        // 检查是否有关联用户
        // TODO: 检查是否有关联用户，如果有则不允许删除
        
        // 更新父组织状态
        if (org.getParentId() != null) {
            // 检查父组织下是否还有其他子组织
            LambdaQueryWrapper<Organization> parentQueryWrapper = new LambdaQueryWrapper<>();
            parentQueryWrapper.eq(Organization::getParentId, org.getParentId());
            parentQueryWrapper.ne(Organization::getId, id);
            long siblingCount = this.count(parentQueryWrapper);
            
            // 如果没有其他子组织，将父组织设置为叶子节点
            if (siblingCount == 0) {
                Organization parent = this.getById(org.getParentId());
                if (parent != null) {
                    parent.setIsLeaf(true);
                    this.updateById(parent);
                }
            }
        }
        
        return this.removeById(id);
    }
    
    // 辅助方法，构建组织树，不是接口方法
    public List<Organization> buildOrganizationTree() {
        // 获取所有组织
        List<Organization> allOrgs = this.list();
        
        // 获取根组织
        List<Organization> rootOrgs = allOrgs.stream()
                .filter(org -> org.getParentId() == null)
                .collect(Collectors.toList());
        
        // 组织按ID映射，方便查找
        Map<Integer, Organization> orgMap = allOrgs.stream()
                .collect(Collectors.toMap(Organization::getId, Function.identity()));
        
        // 构建树形结构
        for (Organization org : allOrgs) {
            if (org.getParentId() != null) {
                Organization parent = orgMap.get(org.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(org);
                }
            }
        }
        
        return rootOrgs;
    }
    
    @Override
    public List<Organization> getOrganizationTree(Integer rootId) {
        List<Organization> result = new ArrayList<>();
        
        try {
            if (rootId == null) {
                // 如果不指定根节点，获取所有顶级组织
                List<Organization> roots = this.getRootOrganizations();
                
                // 检查是否有根组织
                if (roots == null || roots.isEmpty()) {
                    log.warn("未找到任何根组织，尝试查找任何组织作为根");
                    
                    // 尝试获取任何组织作为根
                    LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.last("LIMIT 1");
                    Organization anyOrg = this.getOne(queryWrapper);
                    
                    if (anyOrg != null) {
                        log.info("将组织ID={}设置为根组织", anyOrg.getId());
                        // 可以选择修复数据，但这里为了安全只在内存中处理
                        anyOrg.setParentId(null);
                        roots = new ArrayList<>();
                        roots.add(anyOrg);
                    }
                }
                
                // 构建每一个根的树结构
                for (Organization root : roots) {
                    try {
                        buildOrganizationTree(root);
                        result.add(root);
                    } catch (Exception e) {
                        log.error("构建组织树出错，根ID={}: {}", root.getId(), e.getMessage());
                        // 至少添加根节点
                        if (root.getChildren() == null) {
                            root.setChildren(new ArrayList<>());
                        }
                        result.add(root);
                    }
                }
            } else {
                // 指定根节点，只构建该节点及其子节点的树
                Organization root = organizationMapper.selectOrganizationDetail(rootId);
                if (root == null) {
                    log.warn("未找到ID={}的组织", rootId);
                    throw new ResourceNotFoundException("组织", "id", rootId);
                }
                
                try {
                    buildOrganizationTree(root);
                    result.add(root);
                } catch (Exception e) {
                    log.error("构建组织树出错，根ID={}: {}", rootId, e.getMessage());
                    // 至少添加根节点
                    if (root.getChildren() == null) {
                        root.setChildren(new ArrayList<>());
                    }
                    result.add(root);
                }
            }
        } catch (Exception e) {
            log.error("获取组织树失败: {}", e.getMessage());
            
            // 创建一个错误信息节点
            Organization errorNode = new Organization();
            errorNode.setId(-1);
            errorNode.setName("组织树加载失败");
            errorNode.setChildren(new ArrayList<>());
            result.add(errorNode);
        }
        
        return result;
    }
    
    /**
     * 递归构建组织树 - 增强错误处理
     * 
     * @param organization 当前组织节点
     */
    private void buildOrganizationTree(Organization organization) {
        if (organization == null) {
            log.warn("无法为null组织构建树");
            return;
        }
        
        try {
            // 检查当前节点是否有path，如果没有尝试修复
            if (organization.getPath() == null || organization.getPath().isEmpty()) {
                if (organization.getParentId() == null) {
                    // 根节点的path就是自己的ID
                    organization.setPath(organization.getId().toString());
                } else {
                    // 需要查询父节点获取path
                    Organization parent = this.getById(organization.getParentId());
                    if (parent != null && parent.getPath() != null) {
                        organization.setPath(parent.getPath() + "," + organization.getId());
                    } else {
                        // 父节点不可用，使用自己的ID作为path
                        organization.setPath(organization.getId().toString());
                    }
                }
            }
            
            // 获取子节点
            List<Organization> children = organizationMapper.selectChildrenByParentId(organization.getId());
            
            // 确保children不为null
            if (children == null) {
                children = new ArrayList<>();
            }
            
            if (!children.isEmpty()) {
                // 如果有子节点，设置非叶子状态
                if (organization.getIsLeaf() == null || organization.getIsLeaf()) {
                    organization.setIsLeaf(false);
                }
                
                organization.setChildren(children);
                
                // 递归处理子节点
                for (Organization child : children) {
                    try {
                        buildOrganizationTree(child);
                    } catch (Exception e) {
                        log.error("构建子组织树出错，ID={}: {}", child.getId(), e.getMessage());
                        // 为出错的子节点设置空子列表
                        child.setChildren(new ArrayList<>());
                    }
                }
            } else {
                // 没有子节点，设置为叶子节点
                organization.setChildren(new ArrayList<>());
                if (organization.getIsLeaf() == null || !organization.getIsLeaf()) {
                    organization.setIsLeaf(true);
                }
            }
        } catch (Exception e) {
            log.error("构建组织树过程中出错，组织ID={}: {}", organization.getId(), e.getMessage());
            // 确保children不为null
            organization.setChildren(new ArrayList<>());
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrganization(Organization organization) {
        // 参数校验
        if (organization == null) {
            throw new BusinessException("组织信息不能为空");
        }
        
        if (!StringUtils.hasText(organization.getName())) {
            throw new BusinessException("组织名称不能为空");
        }
        
        if (!StringUtils.hasText(organization.getOrgType())) {
            throw new BusinessException("组织类型不能为空");
        }
        
        // 计算组织层级
        if (organization.getParentId() != null) {
            Organization parent = this.getById(organization.getParentId());
            if (parent == null) {
                throw new ResourceNotFoundException("上级组织", "id", organization.getParentId());
            }
            organization.setLevel(parent.getLevel() + 1);
        } else {
            organization.setLevel(1); // 顶级组织层级为1
        }
        
        // 设置成员数量初始值
        organization.setMemberCount(0);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        organization.setCreateTime(now);
        organization.setUpdateTime(now);
        
        return this.save(organization);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMemberCount(Integer organizationId) {
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        Organization organization = this.getById(organizationId);
        if (organization == null) {
            throw new ResourceNotFoundException("组织", "id", organizationId);
        }
        
        int count = organizationMapper.countUsersByOrganizationId(organizationId);
        organization.setMemberCount(count);
        organization.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(organization);
    }

    @Override
    public boolean isSubOrganization(Integer parentId, Integer childId) {
        if (parentId == null || childId == null || parentId.equals(childId)) {
            return false;
        }
        
        Organization child = this.getById(childId);
        if (child == null || !StringUtils.hasText(child.getPath())) {
            return false;
        }
        
        // 检查path字段中是否包含parentId
        String[] pathIds = child.getPath().split(",");
        for (String id : pathIds) {
            if (parentId.toString().equals(id)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public Organization getById(Integer id) {
        if (id == null) {
            return null;
        }
        return super.getById(id);
    }

    @Override
    public int countDirectMembersByOrganizationId(Integer organizationId) {
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        // 从Mapper中获取直接隶属于当前组织的用户数量
        return organizationMapper.countUsersByOrganizationId(organizationId);
    }
    
    @Override
    public int calculateTotalMemberCount(Integer organizationId) {
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        
        // 先计算直属成员数量
        int totalCount = countDirectMembersByOrganizationId(organizationId);
        
        // 获取所有子组织
        List<Organization> children = getChildrenByParentId(organizationId);
        
        // 递归统计所有子组织的成员数量
        if (children != null && !children.isEmpty()) {
            for (Organization child : children) {
                totalCount += calculateTotalMemberCount(child.getId());
            }
        }
        
        return totalCount;
    }

    /**
     * 获取指定组织及其所有子组织的ID列表
     * 
     * @param organizationId 组织ID
     * @return 组织ID列表，包含自身和所有子组织
     */
    @Override
    public List<Integer> getSelfAndChildrenOrganizationIds(Integer organizationId) {
        List<Integer> result = new ArrayList<>();
        
        // 添加自身
        if (organizationId != null) {
            result.add(organizationId);
        } else {
            return result; // 如果ID为空，返回空列表
        }
        
        // 递归收集所有子组织ID
        collectChildOrganizationIds(organizationId, result);
        
        return result;
    }

    /**
     * 递归收集所有子组织ID
     * 
     * @param parentId 父组织ID
     * @param resultList 结果列表，方法将向此列表添加元素
     */
    private void collectChildOrganizationIds(Integer parentId, List<Integer> resultList) {
        // 获取直接子组织
        List<Organization> children = getChildrenByParentId(parentId);
        
        if (children != null && !children.isEmpty()) {
            for (Organization child : children) {
                Integer childId = child.getId();
                
                // 添加子组织ID
                resultList.add(childId);
                
                // 递归收集子组织的子组织
                collectChildOrganizationIds(childId, resultList);
            }
        }
    }
} 