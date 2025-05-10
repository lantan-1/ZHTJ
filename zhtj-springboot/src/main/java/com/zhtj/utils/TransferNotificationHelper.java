package com.zhtj.utils;

import com.zhtj.domain.Notification;
import com.zhtj.domain.Transfer;
import com.zhtj.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 转接通知辅助类
 * 用于处理团员关系转接相关的通知发送
 */
@Component
public class TransferNotificationHelper {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 发送转接申请已创建通知
     * 
     * @param transfer 转接申请对象
     */
    public void sendTransferCreatedNotification(Transfer transfer) {
        // 通知转出组织管理员
        Notification notification = new Notification();
        notification.setTitle("新的转接申请待审批");
        notification.setContent("您收到一个新的团员关系转接申请（ID:" + transfer.getId() + "），请及时审批。");
        notification.setSenderName("系统");
        notification.setSenderId(0);
        notification.setRecipientId(transfer.getOutOrganizationAdminId());
        notification.setNotificationType("转接申请");
        notification.setReferenceId(transfer.getId());
        notification.setReferenceType("transfer");
        notification.setPriority(1); // 重要
        notification.setActionUrl("/dashboard/transfers/approve");
        
        notificationService.send(notification);
        
        // 通知申请人
        notification = new Notification();
        notification.setTitle("转接申请已提交");
        notification.setContent("您的团员关系转接申请（ID:" + transfer.getId() + "）已成功提交，请等待审批。");
        notification.setSenderName("系统");
        notification.setSenderId(0);
        notification.setRecipientId(transfer.getTransferUserId());
        notification.setNotificationType("转接申请");
        notification.setReferenceId(transfer.getId());
        notification.setReferenceType("transfer");
        notification.setPriority(0); // 普通
        notification.setActionUrl("/dashboard/transfers/detail/" + transfer.getId());
        
        notificationService.send(notification);
    }
    
    /**
     * 发送转出审批通知
     * 
     * @param transfer 转接申请对象
     * @param approved 是否批准
     */
    public void sendOutApprovalNotification(Transfer transfer, boolean approved) {
        // 通知申请人
        String title = approved ? "转接申请转出审批已通过" : "转接申请已被转出组织拒绝";
        String content = approved ? 
                "您的团员关系转接申请（ID:" + transfer.getId() + "）已通过转出组织审批，正在等待转入组织审批。" :
                "您的团员关系转接申请（ID:" + transfer.getId() + "）已被转出组织拒绝，原因：" + transfer.getOutApprovalRemark();
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSenderName("系统");
        notification.setSenderId(0);
        notification.setRecipientId(transfer.getTransferUserId());
        notification.setNotificationType("转接申请");
        notification.setReferenceId(transfer.getId());
        notification.setReferenceType("transfer");
        notification.setPriority(1); // 重要
        notification.setActionUrl("/dashboard/transfers/detail/" + transfer.getId());
        
        notificationService.send(notification);
        
        // 如果批准，通知转入组织管理员
        if (approved) {
            notification = new Notification();
            notification.setTitle("新的转接申请待审批");
            notification.setContent("您收到一个新的团员关系转入申请（ID:" + transfer.getId() + "），已通过转出组织审批，请及时审批。");
            notification.setSenderName("系统");
            notification.setSenderId(0);
            notification.setRecipientId(transfer.getInOrganizationAdminId());
            notification.setNotificationType("转接申请");
            notification.setReferenceId(transfer.getId());
            notification.setReferenceType("transfer");
            notification.setPriority(1); // 重要
            notification.setActionUrl("/dashboard/transfers/approve");
            
            notificationService.send(notification);
        }
    }
    
    /**
     * 发送转入审批通知
     * 
     * @param transfer 转接申请对象
     * @param approved 是否批准
     */
    public void sendInApprovalNotification(Transfer transfer, boolean approved) {
        // 通知申请人
        String title = approved ? "转接申请已完成" : "转接申请已被转入组织拒绝";
        String content = approved ? 
                "您的团员关系转接申请（ID:" + transfer.getId() + "）已通过全部审批，转接成功。" :
                "您的团员关系转接申请（ID:" + transfer.getId() + "）已被转入组织拒绝，原因：" + transfer.getInApprovalRemark();
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSenderName("系统");
        notification.setSenderId(0);
        notification.setRecipientId(transfer.getTransferUserId());
        notification.setNotificationType("转接申请");
        notification.setReferenceId(transfer.getId());
        notification.setReferenceType("transfer");
        notification.setPriority(1); // 重要
        notification.setActionUrl("/dashboard/transfers/detail/" + transfer.getId());
        
        notificationService.send(notification);
        
        // 通知转出组织管理员
        title = approved ? "转接申请已完成" : "转接申请被转入组织拒绝";
        content = approved ? 
                "团员关系转接申请（ID:" + transfer.getId() + "）已完成全部审批流程，转接成功。" :
                "团员关系转接申请（ID:" + transfer.getId() + "）被转入组织拒绝，原因：" + transfer.getInApprovalRemark();
        
        notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSenderName("系统");
        notification.setSenderId(0);
        notification.setRecipientId(transfer.getOutOrganizationAdminId());
        notification.setNotificationType("转接申请");
        notification.setReferenceId(transfer.getId());
        notification.setReferenceType("transfer");
        notification.setPriority(0); // 普通
        notification.setActionUrl("/dashboard/transfers/detail/" + transfer.getId());
        
        notificationService.send(notification);
    }
    
    /**
     * 发送申请过期通知
     * 
     * @param transfer 转接申请对象
     * @param stage 过期的阶段（"转出组织"或"转入组织"）
     */
    public void sendExpiredNotification(Transfer transfer, String stage) {
        // 通知申请人
        Notification notification = new Notification();
        notification.setTitle("转接申请已过期");
        notification.setContent("您的团员关系转接申请(ID:" + transfer.getId() + ")已过期，" 
                + stage + "未及时处理，系统已自动拒绝。请重新提交申请或联系管理员。");
        notification.setSenderName("系统");
        notification.setSenderId(0);
        notification.setRecipientId(transfer.getTransferUserId());
        notification.setNotificationType("转接申请");
        notification.setReferenceId(transfer.getId());
        notification.setReferenceType("transfer");
        notification.setPriority(1); // 重要
        notification.setActionUrl("/dashboard/transfers/detail/" + transfer.getId());
        
        notificationService.send(notification);
    }
} 