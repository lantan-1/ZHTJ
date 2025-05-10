package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorAward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 荣誉授予记录Mapper接口
 */
@Mapper
public interface HonorAwardMapper extends BaseMapper<HonorAward> {
    
    /**
     * 分页查询荣誉授予记录
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @param awardYear 授予年度
     * @param honorTypeId 荣誉类型ID
     * @return 分页结果
     */
    IPage<HonorAward> selectHonorAwardPage(
        Page<HonorAward> page,
        @Param("userId") Integer userId,
        @Param("organizationId") Integer organizationId,
        @Param("awardYear") String awardYear,
        @Param("honorTypeId") Integer honorTypeId
    );
} 