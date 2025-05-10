package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 荣誉申请Mapper接口
 */
@Mapper
public interface HonorApplicationMapper extends BaseMapper<HonorApplication> {
    
    /**
     * 分页查询荣誉申请
     *
     * @param page 分页对象
     * @param year 申请年度
     * @param status 状态
     * @param honorTypeId 荣誉类型ID
     * @param organizationId 组织ID
     * @param orderBy 排序字段
     * @return 分页结果
     */
    IPage<HonorApplication> selectHonorApplicationPage(
        Page<HonorApplication> page,
        @Param("year") String year,
        @Param("status") String status,
        @Param("honorTypeId") Integer honorTypeId,
        @Param("organizationId") Integer organizationId,
        @Param("orderBy") String orderBy
    );
    
    /**
     * 根据用户ID查询荣誉申请
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 分页结果
     */
    @Select("SELECT * FROM honor_application WHERE user_id = #{userId} ORDER BY create_time DESC")
    IPage<HonorApplication> selectByUserId(
        Page<HonorApplication> page,
        @Param("userId") Integer userId
    );
    
    /**
     * 检查用户是否有资格申请荣誉（评议为优秀）
     *
     * @param userId 用户ID
     * @param year 年份
     * @return 是否有资格
     */
    @Select("SELECT COUNT(*) > 0 FROM member_evaluation_detail d " +
            "JOIN member_evaluation e ON d.evaluation_id = e.id " +
            "WHERE d.user_id = #{userId} AND e.evaluation_year = #{year} " +
            "AND d.result = '优秀' AND d.status = '已评议'")
    boolean checkUserEligibility(@Param("userId") Integer userId, @Param("year") String year);
} 