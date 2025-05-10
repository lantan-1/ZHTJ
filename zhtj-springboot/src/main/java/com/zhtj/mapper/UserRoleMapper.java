package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;
import java.util.Set;

/**
 * 用户角色关联Mapper接口
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户ID获取用户角色列表
     * 
     * @param userId 用户ID
     * @return 用户角色列表
     */
    @Select("SELECT ur.*, sr.role_name, sr.role_code, org.name as organization_name " +
            "FROM user_role ur " +
            "LEFT JOIN system_role sr ON ur.role_id = sr.id " +
            "LEFT JOIN organization org ON ur.organization_id = org.id " +
            "WHERE ur.user_id = #{userId} AND sr.status = 1")
    List<UserRole> selectUserRolesByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据用户ID获取角色编码集合
     * 
     * @param userId 用户ID
     * @return 角色编码集合
     */
    @Select("SELECT sr.role_code " +
            "FROM user_role ur " +
            "LEFT JOIN system_role sr ON ur.role_id = sr.id " +
            "WHERE ur.user_id = #{userId} AND sr.status = 1")
    Set<String> selectUserRoleCodes(@Param("userId") Integer userId);
    
    /**
     * 分页查询用户的角色列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    @Select("SELECT ur.*, sr.role_name, sr.role_code, org.name as organization_name " +
            "FROM user_role ur " +
            "LEFT JOIN system_role sr ON ur.role_id = sr.id " +
            "LEFT JOIN organization org ON ur.organization_id = org.id " +
            "WHERE ur.user_id = #{userId} AND sr.status = 1 " +
            "ORDER BY ur.id ASC")
    IPage<UserRole> selectUserRolePage(Page<UserRole> page, @Param("userId") Integer userId);
    
    /**
     * 删除用户的角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int deleteUserRole(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
    
    /**
     * 根据组织ID和角色ID查询用户列表
     * 
     * @param organizationId 组织ID
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM user_role WHERE organization_id = #{organizationId} AND role_id = #{roleId}")
    List<Integer> selectUserIdsByOrgAndRole(@Param("organizationId") Integer organizationId, @Param("roleId") Integer roleId);
} 