package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.model.twosystem.MemberRegister;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 团员注册Mapper接口
 */
@Mapper
public interface MemberRegisterMapper extends BaseMapper<MemberRegister> {

    /**
     * 获取用户在指定批次中的注册状态
     * @param userId 用户ID
     * @param batchId 批次ID
     * @return 注册信息
     */
    MemberRegister getRegisterStatus(@Param("userId") Integer userId, @Param("batchId") Integer batchId);
    
    /**
     * 获取用户的注册历史
     * @param userId 用户ID
     * @param page 分页参数
     * @return 注册历史列表
     */
    IPage<MemberRegister> getRegisterHistory(Page<MemberRegister> page, @Param("userId") Integer userId);
    
    /**
     * 获取待审批的注册申请列表
     * @param page 分页参数
     * @param batchId 批次ID（可选）
     * @param organizationIds 组织ID列表
     * @param memberName 团员姓名（可选）
     * @param status 状态（可选）
     * @return 申请列表
     */
    IPage<MemberRegister> getApprovalList(Page<MemberRegister> page, 
                                        @Param("batchId") Integer batchId,
                                        @Param("organizationIds") List<Integer> organizationIds,
                                        @Param("memberName") String memberName,
                                        @Param("status") String status);
    
    /**
     * 获取指定批次的成员列表（专用于批次详情页）
     * @param page 分页参数
     * @param batchId 批次ID
     * @param keyword 搜索关键词（姓名或学号）
     * @param status 状态筛选
     * @return 成员列表
     */
    IPage<MemberRegister> getBatchMemberList(Page<MemberRegister> page, 
                                          @Param("batchId") Integer batchId,
                                          @Param("keyword") String keyword,
                                        @Param("status") String status);
    
    /**
     * 更新注册状态
     * @param id 注册ID
     * @param status 状态
     * @param comments 审批意见
     * @param approverId 审批人ID
     * @param approverName 审批人姓名
     * @return 影响行数
     */
    @Update("UPDATE member_register SET status = #{status}, remark = #{comments}, " +
            "approver_id = #{approverId}, approver_name = #{approverName}, " +
            "approval_time = NOW(), update_time = NOW() " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, 
                    @Param("status") String status, 
                    @Param("comments") String comments,
                    @Param("approverId") Integer approverId,
                    @Param("approverName") String approverName);
    
    /**
     * 获取组织的注册统计数据
     * @param organizationIds 组织ID列表
     * @param year 年度
     * @return 统计数据
     */
    @Select("<script>" +
            "SELECT " +
            "  COUNT(*) as total, " +
            "  SUM(CASE WHEN status = '已完成' THEN 1 ELSE 0 END) as approved, " +
            "  SUM(CASE WHEN status = '已驳回' THEN 1 ELSE 0 END) as rejected, " +
            "  SUM(CASE WHEN status = '待审核' THEN 1 ELSE 0 END) as pending " +
            "FROM member_register r " +
            "JOIN member_register_batch b ON r.register_year = b.register_year " +
            "WHERE b.register_year = #{year} " +
            "<if test='organizationIds != null and organizationIds.size() > 0'> " +
            "  AND (r.organization_id IN " +
            "    <foreach collection='organizationIds' item='id' open='(' separator=',' close=')'>" +
            "      #{id}" +
            "    </foreach>" +
            "  OR (SELECT parent_id FROM organization WHERE id = r.organization_id) IN " +
            "    <foreach collection='organizationIds' item='id' open='(' separator=',' close=')'>" +
            "      #{id}" +
            "    </foreach>" +
            "  )" +
            "</if> " +
            "</script>")
    Map<String, Object> getRegisterStatistics(@Param("organizationIds") List<Integer> organizationIds, 
                                           @Param("year") String year);
} 