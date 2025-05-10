package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.VolunteerService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 志愿服务记录Mapper接口
 */
@Mapper
public interface VolunteerServiceMapper extends BaseMapper<VolunteerService> {
    
    /**
     * 分页查询志愿服务记录，包含用户和组织信息
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @param serviceYear 服务年度
     * @return 分页结果
     */
    IPage<VolunteerService> selectVolunteerServicePage(
        Page<VolunteerService> page,
        @Param("userId") Integer userId,
        @Param("organizationId") Integer organizationId,
        @Param("serviceYear") String serviceYear
    );
    
    /**
     * 查询用户志愿服务总时长
     *
     * @param userId 用户ID
     * @return 总时长
     */
    Double selectTotalServiceHoursByUserId(@Param("userId") Integer userId);
    
    /**
     * 查询组织志愿服务总时长
     *
     * @param organizationId 组织ID
     * @return 总时长
     */
    Double selectTotalServiceHoursByOrganizationId(@Param("organizationId") Integer organizationId);
} 