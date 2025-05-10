package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.SystemRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统角色Mapper接口
 */
@Mapper
public interface SystemRoleMapper extends BaseMapper<SystemRole> {

    /**
     * 分页查询角色列表
     * 
     * @param page 分页参数
     * @param roleName 角色名称（可选）
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM system_role " +
            "<where>" +
            "<if test='roleName != null and roleName != \"\"'>" +
            "  AND role_name LIKE CONCAT('%',#{roleName},'%') " +
            "</if>" +
            "  AND status = 1 " +
            "</where>" +
            "ORDER BY id ASC" +
            "</script>")
    IPage<SystemRole> selectRolePage(Page<SystemRole> page, @Param("roleName") String roleName);
    
    /**
     * 根据角色编码获取角色
     * 
     * @param roleCode 角色编码
     * @return 角色信息
     */
    @Select("SELECT * FROM system_role WHERE role_code = #{roleCode} AND status = 1")
    SystemRole selectByRoleCode(@Param("roleCode") String roleCode);
    
    /**
     * 获取所有启用的角色
     * 
     * @return 角色列表
     */
    @Select("SELECT * FROM system_role WHERE status = 1 ORDER BY id ASC")
    List<SystemRole> selectAllEnabledRoles();
} 