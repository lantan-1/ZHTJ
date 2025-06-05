package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据身份证号和密码查询用户ID
     * @param card 身份证号
     * @param pwd 密码
     * @return 用户ID，如果不存在则返回null
     */
    @Select("SELECT id FROM user WHERE card = #{card} AND pwd = #{pwd}")
    Integer getIdByCardAndPwd(@Param("card") String card, @Param("pwd") String pwd);
    
    /**
     * 分页查询用户列表
     * 
     * @param page 分页参数
     * @param name 用户姓名
     * @param organizationId 组织ID
     * @return 用户列表
     */
    @Select("<script>" +
            "SELECT u.id, u.name, u.card, u.pwd, u.gender, u.ethnic, u.occupation, " +
            "u.education_status, u.education_level, u.political_status, u.join_league_date, " +
            "u.join_party_date, u.work_unit, u.address, u.qq, u.wechat, u.weibo, " +
            "u.league_position, u.phone, u.email, u.organization, u.previous_organization, " +
            "u.transfer_date, u.transfer_count, u.create_time, u.update_time, " +
            "o.name as organization_name " +
            "FROM user u " +
            "LEFT JOIN organization o ON u.organization = o.id " +
            "WHERE 1=1 " +
            "<if test='name != null and name != \"\"'> AND u.name LIKE CONCAT('%', #{name}, '%') </if> " +
            "<if test='organizationId != null'> AND u.organization = #{organizationId} </if> " +
            "ORDER BY u.id DESC" +
            "</script>")
    IPage<User> selectUserPage(Page<User> page, 
                             @Param("name") String name,
                             @Param("organizationId") Integer organizationId);
    
    /**
     * 根据身份证号查询用户
     * 
     * @param card 身份证号
     * @return 用户信息
     */
    @Select("SELECT u.id, u.name, u.card, u.pwd, u.gender, u.ethnic, u.occupation, " +
            "u.education_status, u.education_level, u.political_status, u.join_league_date, " +
            "u.join_party_date, u.work_unit, u.address, u.qq, u.wechat, u.weibo, " +
            "u.league_position, u.phone, u.email, u.organization, u.previous_organization, " +
            "u.transfer_date, u.transfer_count, u.create_time, u.update_time, u.is_activated," +
            "o.name as organization_name " +
            "FROM user u " +
            "LEFT JOIN organization o ON u.organization = o.id " +
            "WHERE u.card = #{card}")
    User selectUserByCard(@Param("card") String card);
    
    /**
     * 根据ID查询用户详情
     * 
     * @param id 用户ID
     * @return 用户详情
     */
    @Select("SELECT u.id, u.name, u.card, u.pwd, u.gender, u.ethnic, u.occupation, " +
            "u.education_status, u.education_level, u.political_status, u.join_league_date, " +
            "u.join_party_date, u.work_unit, u.address, u.qq, u.wechat, u.weibo, " +
            "u.league_position, u.phone, u.email, u.organization, u.previous_organization, " +
            "u.transfer_date, u.transfer_count, u.create_time, u.update_time, u.is_activated, " +
            "o.name as organization_name, u.face_image_path " +
            "FROM user u " +
            "LEFT JOIN organization o ON u.organization = o.id " +
            "WHERE u.id = #{id}")
    User selectUserDetail(@Param("id") Integer id);
    
    /**
     * 获取用户详情，包含组织信息
     * @param id 用户ID
     * @return 用户详情
     */
    @Select("SELECT u.id, u.name, u.card, u.pwd, u.gender, u.ethnic, u.occupation, " +
            "u.education_status, u.education_level, u.political_status, u.join_league_date, " +
            "u.join_party_date, u.work_unit, u.address, u.qq, u.wechat, u.weibo, " +
            "u.league_position, u.phone, u.email, u.organization, u.previous_organization, " +
            "u.transfer_date, u.transfer_count, u.create_time, u.update_time, " +
            "o.name as organization_name, u.face_image_path " +
            "FROM user u " +
            "LEFT JOIN organization o ON u.organization = o.id " +
            "WHERE u.id = #{id}")
    User getUserDetail(@Param("id") Integer id);
    
    /**
     * 更新用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 影响行数
     */
    @Update("UPDATE user SET pwd = #{newPassword} WHERE id = #{userId}")
    int updatePassword(@Param("userId") Integer userId, @Param("newPassword") String newPassword);
    
    /**
     * 根据组织ID获取用户列表
     * @param organizationId 组织ID
     * @return 用户列表
     */
    @Select("SELECT id, name, card, pwd, gender, ethnic, occupation, education_status, " +
            "education_level, political_status, join_league_date, join_party_date, work_unit, " +
            "address, qq, wechat, weibo, league_position, phone, email, organization, " +
            "previous_organization, transfer_date, transfer_count, create_time, update_time " +
            "FROM user WHERE organization = #{organizationId}")
    List<User> getUsersByOrganization(@Param("organizationId") Integer organizationId);
    
    /**
     * 更新用户组织，用于团组织关系转接
     * @param userId 用户ID
     * @param newOrganizationId 新组织ID
     * @param oldOrganizationId 旧组织ID，用于历史记录
     * @return 影响行数
     */
    @Update("UPDATE user SET organization = #{newOrganizationId}, previous_organization = #{oldOrganizationId}, " +
            "transfer_date = NOW(), transfer_count = transfer_count + 1 " +
            "WHERE id = #{userId}")
    int updateUserOrganization(@Param("userId") Integer userId,
                              @Param("newOrganizationId") Integer newOrganizationId,
                              @Param("oldOrganizationId") Integer oldOrganizationId);
    
    /**
     * 根据政治面貌统计人数
     * @return 政治面貌和人数的映射
     */
    @Select("SELECT political_status, COUNT(*) as count FROM user GROUP BY political_status")
    List<Map<String, Object>> countByPoliticalStatus();

    /**
     * 分页查询待激活用户列表，不返回密码字段
     * 
     * @param page 分页参数
     * @param orgIds 组织ID列表
     * @param name 用户名（可选）
     * @param isActivated 激活状态（可选）
     * @return 分页查询结果
     */
    IPage<User> selectPendingUsersPage(Page<User> page, @Param("orgIds") List<Integer> orgIds, 
                                      @Param("name") String name, @Param("isActivated") Boolean isActivated);
} 