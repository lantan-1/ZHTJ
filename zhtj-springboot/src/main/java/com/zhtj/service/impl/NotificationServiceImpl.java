package com.zhtj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.Notification;
import com.zhtj.mapper.NotificationMapper;
import com.zhtj.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 消息通知业务实现类
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationMapper notificationMapper;
    
    @Override
    public IPage<Notification> getNotificationPage(Page<Notification> page, Integer recipientId, Integer isRead) {
        return notificationMapper.selectNotificationPage(page, recipientId, isRead);
    }
    
    @Override
    public Notification getById(Integer id) {
        Notification notification = notificationMapper.selectById(id);
        // 记录查看行为，但不标记为已读
        return notification;
    }
    
    @Override
    @Transactional
    public Integer send(Notification notification) {
        // 设置默认值
        notification.setIsRead(0); // 未读
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        
        notificationMapper.insert(notification);
        return notification.getId();
    }
    
    @Override
    @Transactional
    public boolean markAsRead(Integer id) {
        return notificationMapper.markAsRead(id) > 0;
    }
    
    @Override
    @Transactional
    public boolean markAllAsRead(Integer recipientId) {
        return notificationMapper.markAllAsRead(recipientId) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(Integer id) {
        return notificationMapper.deleteById(id) > 0;
    }
    
    @Override
    public int countUnreadNotifications(Integer recipientId) {
        return notificationMapper.countUnreadNotifications(recipientId);
    }
    
    @Override
    @Transactional
    public Integer sendSystemNotification(
            String title, 
            String content, 
            Integer recipientId, 
            String notificationType, 
            Integer referenceId, 
            String referenceType, 
            String actionUrl) {
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSenderName("系统");
        notification.setSenderId(0); // 0表示系统
        notification.setRecipientId(recipientId);
        notification.setNotificationType(notificationType);
        notification.setReferenceId(referenceId);
        notification.setReferenceType(referenceType);
        notification.setIsRead(0); // 未读
        notification.setPriority(0); // 普通优先级
        notification.setActionUrl(actionUrl);
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        
        notificationMapper.insert(notification);
        return notification.getId();
    }

    @Override
    @Transactional
    public Integer sendTransferNotification(
            Integer recipientId,
            Integer transferId,
            String title,
            String content) {
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSenderName("系统");
        notification.setSenderId(0); // 0表示系统
        notification.setRecipientId(recipientId);
        notification.setNotificationType("转接申请");
        notification.setReferenceId(transferId);
        notification.setReferenceType("transfer");
        notification.setIsRead(0); // 未读
        notification.setPriority(1); // 重要优先级
        notification.setActionUrl("/dashboard/transfers/detail/" + transferId);
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        
        return send(notification);
    }
} 