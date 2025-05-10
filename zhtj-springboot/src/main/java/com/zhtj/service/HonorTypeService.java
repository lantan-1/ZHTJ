package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorType;

import java.util.List;

/**
 * 荣誉类型业务接口
 */
public interface HonorTypeService {
    
    /**
     * 分页查询荣誉类型
     *
     * @param page 分页对象
     * @param category 荣誉类别
     * @return 分页结果
     */
    IPage<HonorType> getHonorTypePage(Page<HonorType> page, String category);
    
    /**
     * 根据ID获取荣誉类型
     *
     * @param id 荣誉类型ID
     * @return 荣誉类型
     */
    HonorType getById(Integer id);
    
    /**
     * 创建荣誉类型
     *
     * @param honorType 荣誉类型对象
     * @return 新创建的荣誉类型ID
     */
    Integer create(HonorType honorType);
    
    /**
     * 更新荣誉类型
     *
     * @param honorType 荣誉类型对象
     * @return 操作是否成功
     */
    boolean update(HonorType honorType);
    
    /**
     * 删除荣誉类型
     *
     * @param id 荣誉类型ID
     * @return 操作是否成功
     */
    boolean delete(Integer id);
    
    /**
     * 启用/禁用荣誉类型
     *
     * @param id 荣誉类型ID
     * @param status 状态(0-禁用，1-启用)
     * @return 操作是否成功
     */
    boolean updateStatus(Integer id, Integer status);
    
    /**
     * 获取所有启用的荣誉类型
     *
     * @return 荣誉类型列表
     */
    List<HonorType> getAllEnabledTypes();
    
    /**
     * 根据类别获取荣誉类型
     *
     * @param category 荣誉类别
     * @return 荣誉类型列表
     */
    List<HonorType> getTypesByCategory(String category);
} 