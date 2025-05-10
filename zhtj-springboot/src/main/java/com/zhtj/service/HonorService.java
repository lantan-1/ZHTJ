package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhtj.domain.Honor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 荣誉激励服务接口
 */
public interface HonorService extends IService<Honor> {
    
    /**
     * 分页查询荣誉列表
     * 
     * @param page 分页参数
     * @param type 荣誉类型(个人/集体)
     * @param organizationId 组织ID
     * @param userId 用户ID
     * @param year 荣誉年度
     * @return 荣誉列表
     */
    IPage<Honor> getHonorPage(Page<Honor> page, String type, Integer organizationId, Integer userId, String year);
    
    /**
     * 根据ID查询荣誉详情
     * 
     * @param id 荣誉ID
     * @return 荣誉详情
     */
    Honor getHonorById(Integer id);
    
    /**
     * 创建荣誉
     * 
     * @param honor 荣誉信息
     * @return 是否成功
     */
    boolean createHonor(Honor honor);
    
    /**
     * 更新荣誉
     * 
     * @param honor 荣誉信息
     * @return 是否成功
     */
    boolean updateHonor(Honor honor);
    
    /**
     * 删除荣誉
     * 
     * @param id 荣誉ID
     * @return 是否成功
     */
    boolean deleteHonor(Integer id);
    
    /**
     * 上传荣誉证书
     * 
     * @param id 荣誉ID
     * @param file 证书文件
     * @return 证书URL
     */
    String uploadCertificate(Integer id, MultipartFile file);
} 