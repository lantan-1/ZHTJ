package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.HonorApplication;
import com.zhtj.mapper.HonorApplicationMapper;
import com.zhtj.service.HonorApplicationService;
import com.zhtj.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

/**
 * 荣誉申请业务实现类
 */
@Service
public class HonorApplicationServiceImpl implements HonorApplicationService {
    
    @Autowired
    private HonorApplicationMapper honorApplicationMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    public IPage<HonorApplication> getHonorApplicationPage(
            Page<HonorApplication> page, 
            String year, 
            String status, 
            Integer honorTypeId, 
            Integer organizationId) {
        // 传递null作为orderBy参数，使用默认排序
        return honorApplicationMapper.selectHonorApplicationPage(page, year, status, honorTypeId, organizationId, null);
    }
    
    @Override
    public HonorApplication getById(Integer id) {
        return honorApplicationMapper.selectById(id);
    }
    
    /**
     * 根据用户ID获取荣誉申请列表
     */
    @Override
    public IPage<HonorApplication> getByUserId(Page<HonorApplication> page, Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        try {
            return honorApplicationMapper.selectByUserId(page, userId);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("获取用户荣誉申请列表出错: " + e.getMessage());
            // 返回空页面
            return page.setRecords(Collections.emptyList());
        }
    }
    
    /**
     * 检查用户是否有资格申请荣誉（评议为优秀）
     */
    @Override
    public boolean checkUserEligibility(Integer userId, String year) {
        if (userId == null) {
            return false;
        }
        
        if (year == null || year.isEmpty()) {
            year = String.valueOf(java.time.Year.now().getValue());
        }
        
        try {
            // 查询用户在当年的评议结果
            return honorApplicationMapper.checkUserEligibility(userId, year);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("检查用户申请资格出错: " + e.getMessage());
            // 默认返回无资格
            return false;
        }
    }
    
    @Override
    @Transactional
    public Integer create(HonorApplication honorApplication) {
        // 设置默认值
        honorApplication.setStatus("申请中");
        if (honorApplication.getApplicationTime() == null) {
            honorApplication.setApplicationTime(LocalDateTime.now());
        }
        honorApplication.setCreateTime(LocalDateTime.now());
        honorApplication.setUpdateTime(LocalDateTime.now());
        
        honorApplicationMapper.insert(honorApplication);
        
        // 发送通知给支部负责人
//        sendNotificationToBranchLeader(honorApplication);
        
        return honorApplication.getId();
    }
    
    @Override
    @Transactional
    public boolean update(HonorApplication honorApplication) {
        honorApplication.setUpdateTime(LocalDateTime.now());
        return honorApplicationMapper.updateById(honorApplication) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(Integer id) {
        return honorApplicationMapper.deleteById(id) > 0;
    }
    
    /**
     * 通用审批方法，根据申请状态进行相应的审批操作
     * 
     * @param id 申请ID
     * @param status 审批状态（已通过/已拒绝）
     * @param comments 审批意见
     * @param approverId 审批人ID
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean approve(Integer id, String status, String comments, Integer approverId) {
        // 获取申请信息
        HonorApplication honorApplication = honorApplicationMapper.selectById(id);
        if (honorApplication == null) {
            return false;
        }
        
        // 直接设置申请状态为已通过或已拒绝，不进行多层审批
        honorApplication.setApproverId(approverId);
        honorApplication.setApprovalComments(comments);
        honorApplication.setApprovalTime(LocalDateTime.now());
        honorApplication.setStatus(status);
        honorApplication.setUpdateTime(LocalDateTime.now());
        
        return honorApplicationMapper.updateById(honorApplication) > 0;
    }
    
    @Override
    @Transactional
    public boolean branchApprove(Integer id, String approvalStatus, String approvalComments, Integer approverId) {
        HonorApplication honorApplication = honorApplicationMapper.selectById(id);
        if (honorApplication == null) {
            return false;
        }
        
        // 更新审批信息
        honorApplication.setApproverId(approverId);
        honorApplication.setApprovalComments(approvalComments);
        honorApplication.setApprovalTime(LocalDateTime.now());
        
        // 更新申请状态 - 直接设置为已通过或已拒绝
        honorApplication.setStatus(approvalStatus);
        honorApplication.setUpdateTime(LocalDateTime.now());
        
        // 不发送通知
        // sendNotificationToHigherLeader(honorApplication);
        // sendNotificationToApplicant(honorApplication, true);
        
        return honorApplicationMapper.updateById(honorApplication) > 0;
    }
    
    @Override
    @Transactional
    public boolean higherApprove(Integer id, String approvalStatus, String approvalComments, Integer approverId) {
        HonorApplication honorApplication = honorApplicationMapper.selectById(id);
        if (honorApplication == null) {
            return false;
        }
        
        // 更新审批信息
        honorApplication.setApproverId(approverId);
        honorApplication.setApprovalComments(approvalComments);
        honorApplication.setApprovalTime(LocalDateTime.now());
        
        // 更新申请状态 - 直接设置为已通过或已拒绝
        honorApplication.setStatus(approvalStatus);
        honorApplication.setUpdateTime(LocalDateTime.now());
        
        // 不发送通知
        // sendNotificationToApplicant(honorApplication, false);
        
        return honorApplicationMapper.updateById(honorApplication) > 0;
    }
    
    /**
     * 向支部负责人发送通知
     */
    private void sendNotificationToBranchLeader(HonorApplication honorApplication) {
        // 此处需要查询支部负责人ID，简化处理，假设organizationId就是负责人ID
        Integer leaderId = honorApplication.getOrganizationId();
        
        notificationService.sendSystemNotification(
                "新的荣誉申请等待审批", 
                "用户ID为" + honorApplication.getUserId() + "的成员申请了荣誉，请及时审批。", 
                leaderId, 
                "荣誉通知", 
                honorApplication.getId(), 
                "honor_application", 
                "/honor/application/" + honorApplication.getId()
        );
    }
    
    /**
     * 向上级组织负责人发送通知
     */
    private void sendNotificationToHigherLeader(HonorApplication honorApplication) {
        // 此处需要查询上级组织负责人ID，简化处理，假设是固定ID
        Integer higherLeaderId = 1; // 假设ID为1是上级负责人
        
        notificationService.sendSystemNotification(
                "新的荣誉推荐等待审批", 
                "组织ID为" + honorApplication.getOrganizationId() + "的团支部推荐了一个荣誉申请，请及时审批。", 
                higherLeaderId, 
                "荣誉通知", 
                honorApplication.getId(), 
                "honor_application", 
                "/honor/application/" + honorApplication.getId()
        );
    }
    
    /**
     * 向申请人发送通知
     */
    private void sendNotificationToApplicant(HonorApplication honorApplication, boolean isBranchApproval) {
        String title = isBranchApproval ? "您的荣誉申请已被支部审核" : "您的荣誉申请最终审核结果通知";
        String content;
        
        if (isBranchApproval) {
            if ("已推荐".equals(honorApplication.getStatus())) {
                content = "您申请的荣誉'" + honorApplication.getHonorName() + "'已通过支部审核，并推荐至上级审批。\n" +
                         "审批意见：" + honorApplication.getApprovalComments();
            } else {
                content = "您申请的荣誉'" + honorApplication.getHonorName() + "'未通过支部审核。\n" +
                         "审批意见：" + honorApplication.getApprovalComments();
            }
        } else {
            if ("已通过".equals(honorApplication.getStatus())) {
                content = "恭喜您！您申请的荣誉'" + honorApplication.getHonorName() + "'已通过最终审核。\n" +
                         "审批意见：" + honorApplication.getApprovalComments();
            } else {
                content = "很遗憾，您申请的荣誉'" + honorApplication.getHonorName() + "'未通过最终审核。\n" +
                         "审批意见：" + honorApplication.getApprovalComments();
            }
        }
        
        notificationService.sendSystemNotification(
                title,
                content,
                honorApplication.getUserId(),
                "荣誉通知",
                honorApplication.getId(),
                "honor_application",
                "/honor/application/" + honorApplication.getId()
        );
    }
} 