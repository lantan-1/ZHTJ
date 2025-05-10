package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhtj.domain.Honor;
import com.zhtj.mapper.HonorMapper;
import com.zhtj.service.HonorService;
import com.zhtj.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 荣誉激励服务实现类
 */
@Service
@Slf4j
public class HonorServiceImpl extends ServiceImpl<HonorMapper, Honor> implements HonorService {

    @Value("${file.upload.path:/upload}")
    private String uploadPath;
    
    @Value("${file.url.prefix:http://localhost:8080}")
    private String urlPrefix;
    
    @Autowired
    private FileUtils fileUtils;
    
    @Override
    public IPage<Honor> getHonorPage(Page<Honor> page, String type, Integer organizationId, Integer userId, String year) {
        LambdaQueryWrapper<Honor> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(Honor::getType, type);
        }
        
        if (organizationId != null) {
            queryWrapper.eq(Honor::getOrganizationId, organizationId);
        }
        
        if (userId != null) {
            queryWrapper.eq(Honor::getUserId, userId);
        }
        
        if (StringUtils.hasText(year)) {
            queryWrapper.eq(Honor::getHonorYear, year);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Honor::getCreateTime);
        
        return this.page(page, queryWrapper);
    }
    
    @Override
    public Honor getHonorById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("荣誉ID不能为空");
        }
        
        return this.getById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createHonor(Honor honor) {
        // 参数校验
        if (honor == null) {
            throw new IllegalArgumentException("荣誉信息不能为空");
        }
        
        if (!StringUtils.hasText(honor.getTitle())) {
            throw new IllegalArgumentException("荣誉标题不能为空");
        }
        
        if (!StringUtils.hasText(honor.getType())) {
            throw new IllegalArgumentException("荣誉类型不能为空");
        }
        
        if (!StringUtils.hasText(honor.getHonorYear())) {
            throw new IllegalArgumentException("荣誉年度不能为空");
        }
        
        if ("个人".equals(honor.getType()) && honor.getUserId() == null) {
            throw new IllegalArgumentException("个人荣誉必须关联用户ID");
        }
        
        if ("集体".equals(honor.getType()) && honor.getOrganizationId() == null) {
            throw new IllegalArgumentException("集体荣誉必须关联组织ID");
        }
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        honor.setCreateTime(now);
        honor.setUpdateTime(now);
        
        return this.save(honor);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHonor(Honor honor) {
        // 参数校验
        if (honor == null || honor.getId() == null) {
            throw new IllegalArgumentException("荣誉ID不能为空");
        }
        
        // 检查荣誉是否存在
        Honor existingHonor = this.getById(honor.getId());
        if (existingHonor == null) {
            throw new IllegalArgumentException("荣誉不存在");
        }
        
        // 设置更新时间
        honor.setUpdateTime(LocalDateTime.now());
        
        return this.updateById(honor);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteHonor(Integer id) {
        // 参数校验
        if (id == null) {
            throw new IllegalArgumentException("荣誉ID不能为空");
        }
        
        // 检查荣誉是否存在
        Honor existingHonor = this.getById(id);
        if (existingHonor == null) {
            throw new IllegalArgumentException("荣誉不存在");
        }
        
        return this.removeById(id);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadCertificate(Integer id, MultipartFile file) {
        // 参数校验
        if (id == null) {
            throw new IllegalArgumentException("荣誉ID不能为空");
        }
        
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("证书文件不能为空");
        }
        
        // 检查荣誉是否存在
        Honor existingHonor = this.getById(id);
        if (existingHonor == null) {
            throw new IllegalArgumentException("荣誉不存在");
        }
        
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            
            // 上传文件
            String filePath = fileUtils.uploadFile(file, uploadPath + "/certificate", newFilename);
            
            // 更新荣誉的证书URL
            String certificateUrl = urlPrefix + filePath;
            existingHonor.setCertificateUrl(certificateUrl);
            existingHonor.setUpdateTime(LocalDateTime.now());
            this.updateById(existingHonor);
            
            return certificateUrl;
        } catch (IOException e) {
            log.error("上传荣誉证书失败", e);
            throw new RuntimeException("上传证书文件失败", e);
        }
    }
} 