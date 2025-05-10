package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.Transfer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 团员关系转接Mapper接口
 */
@Mapper
public interface TransferMapper extends BaseMapper<Transfer> {
    
    /**
     * 分页查询转接申请列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param statusCode 状态码
     * @param organizationId 组织ID
     * @return 转接申请列表
     */
    @Select("<script>" +
            "SELECT t.*, " +
            "u.name as transferUserName, " +
            "outOrg.name as transferOutOrgName, " +
            "outOrg.full_name as transferOutOrgFullName, " +
            "inOrg.name as transferInOrgName, " +
            "inOrg.full_name as transferInOrgFullName, " +
            "outApprover.name as outApproverName, " +
            "inApprover.name as inApproverName " +
            "FROM transfer t " +
            "LEFT JOIN user u ON t.transfer_user_id = u.id " +
            "LEFT JOIN organization outOrg ON t.transfer_out_org_id = outOrg.id " +
            "LEFT JOIN organization inOrg ON t.transfer_in_org_id = inOrg.id " +
            "LEFT JOIN user outApprover ON t.out_approver_id = outApprover.id " +
            "LEFT JOIN user inApprover ON t.in_approver_id = inApprover.id " +
            "WHERE 1=1 " +
            "<if test='userId != null'> AND t.transfer_user_id = #{userId} </if> " +
            "<if test='statusCode != null'> AND t.status_code = #{statusCode} </if> " +
            "<if test='organizationId != null'> AND (t.transfer_out_org_id = #{organizationId} OR t.transfer_in_org_id = #{organizationId}) </if> " +
            "ORDER BY t.application_time DESC" +
            "</script>")
    IPage<Transfer> selectTransferPage(Page<Transfer> page, 
                                      @Param("userId") Integer userId,
                                      @Param("statusCode") Integer statusCode,
                                      @Param("organizationId") Integer organizationId);
    
    /**
     * 查询用户的转接申请
     * 
     * @param userId 用户ID
     * @return 转接申请列表
     */
    @Select("SELECT t.*, " +
            "u.name as transferUserName, " +
            "outOrg.name as transferOutOrgName, " +
            "outOrg.full_name as transferOutOrgFullName, " +
            "inOrg.name as transferInOrgName, " +
            "inOrg.full_name as transferInOrgFullName, " +
            "outApprover.name as outApproverName, " +
            "inApprover.name as inApproverName " +
            "FROM transfer t " +
            "LEFT JOIN user u ON t.transfer_user_id = u.id " +
            "LEFT JOIN organization outOrg ON t.transfer_out_org_id = outOrg.id " +
            "LEFT JOIN organization inOrg ON t.transfer_in_org_id = inOrg.id " +
            "LEFT JOIN user outApprover ON t.out_approver_id = outApprover.id " +
            "LEFT JOIN user inApprover ON t.in_approver_id = inApprover.id " +
            "WHERE t.transfer_user_id = #{userId} " +
            "ORDER BY t.application_time DESC")
    List<Transfer> selectTransfersByUserId(@Param("userId") Integer userId);
    
    /**
     * 查询待转出组织审批的转接申请
     * 
     * @param organizationId 组织ID
     * @return 转接申请列表
     */
    @Select("SELECT t.*, " +
            "u.name as transferUserName, " +
            "outOrg.name as transferOutOrgName, " +
            "outOrg.full_name as transferOutOrgFullName, " +
            "inOrg.name as transferInOrgName, " +
            "inOrg.full_name as transferInOrgFullName, " +
            "outApprover.name as outApproverName, " +
            "inApprover.name as inApproverName " +
            "FROM transfer t " +
            "LEFT JOIN user u ON t.transfer_user_id = u.id " +
            "LEFT JOIN organization outOrg ON t.transfer_out_org_id = outOrg.id " +
            "LEFT JOIN organization inOrg ON t.transfer_in_org_id = inOrg.id " +
            "LEFT JOIN user outApprover ON t.out_approver_id = outApprover.id " +
            "LEFT JOIN user inApprover ON t.in_approver_id = inApprover.id " +
            "WHERE t.transfer_out_org_id = #{organizationId} " +
            "AND t.status_code = 0 " +
            "ORDER BY t.application_time ASC")
    List<Transfer> selectOutgoingTransfersByOrgId(@Param("organizationId") Integer organizationId);
    
    /**
     * 查询待转入组织审批的转接申请
     * 
     * @param organizationId 组织ID
     * @return 转接申请列表
     */
    @Select("SELECT t.*, " +
            "u.name as transferUserName, " +
            "outOrg.name as transferOutOrgName, " +
            "outOrg.full_name as transferOutOrgFullName, " +
            "inOrg.name as transferInOrgName, " +
            "inOrg.full_name as transferInOrgFullName, " +
            "outApprover.name as outApproverName, " +
            "inApprover.name as inApproverName " +
            "FROM transfer t " +
            "LEFT JOIN user u ON t.transfer_user_id = u.id " +
            "LEFT JOIN organization outOrg ON t.transfer_out_org_id = outOrg.id " +
            "LEFT JOIN organization inOrg ON t.transfer_in_org_id = inOrg.id " +
            "LEFT JOIN user outApprover ON t.out_approver_id = outApprover.id " +
            "LEFT JOIN user inApprover ON t.in_approver_id = inApprover.id " +
            "WHERE t.transfer_in_org_id = #{organizationId} " +
            "AND t.status_code = 2 " +
            "ORDER BY t.application_time ASC")
    List<Transfer> selectIncomingTransfersByOrgId(@Param("organizationId") Integer organizationId);
    
    /**
     * 查询转接详情
     * 
     * @param id 转接ID
     * @return 转接详情
     */
    @Select("SELECT t.*, " +
            "u.name as transferUserName, " +
            "outOrg.name as transferOutOrgName, " +
            "outOrg.full_name as transferOutOrgFullName, " +
            "inOrg.name as transferInOrgName, " +
            "inOrg.full_name as transferInOrgFullName, " +
            "outApprover.name as outApproverName, " +
            "inApprover.name as inApproverName " +
            "FROM transfer t " +
            "LEFT JOIN user u ON t.transfer_user_id = u.id " +
            "LEFT JOIN organization outOrg ON t.transfer_out_org_id = outOrg.id " +
            "LEFT JOIN organization inOrg ON t.transfer_in_org_id = inOrg.id " +
            "LEFT JOIN user outApprover ON t.out_approver_id = outApprover.id " +
            "LEFT JOIN user inApprover ON t.in_approver_id = inApprover.id " +
            "WHERE t.id = #{id}")
    Transfer selectTransferDetail(@Param("id") Integer id);
} 