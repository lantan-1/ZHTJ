package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 荣誉类型Mapper接口
 */
@Mapper
public interface HonorTypeMapper extends BaseMapper<HonorType> {
    
    /**
     * 分页查询荣誉类型
     *
     * @param page 分页对象
     * @param category 荣誉类别
     * @return 分页结果
     */
    IPage<HonorType> selectHonorTypePage(
        Page<HonorType> page,
        @Param("category") String category
    );
} 