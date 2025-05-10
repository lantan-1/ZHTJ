package com.zhtj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.Notification;

/**
 * 消息通知业务接口
 */
public interface NotificationService {
    
    /**
     * 分页查询通知列表
     *
     * @param page 分页对象
     * @param recipientId 接收者ID
     * @param isRead 是否已读
     * @return 分页结果
     */
    IPage<Notification> getNotificationPage(
        Page<Notification> page, 
        Integer recipientId, 
        Integer isRead
    );
    
    /**
     * 根据ID获取通知详情
     *
     * @param id 通知ID
     * @return 通知详情
     */
    Notification getById(Integer id);
    
    /**
     * 发送通知
     *
     * @param notification 通知对象
     * @return 新创建的通知ID
     */
    Integer send(Notification notification);
    
    /**
     * 标记通知为已读
     *
     * @param id 通知ID
     * @return 操作是否成功
     */
    boolean markAsRead(Integer id);
    
    /**
     * 标记用户所有通知为已读
     *
     * @param recipientId 接收者ID
     * @return 操作是否成功
     */
    boolean markAllAsRead(Integer recipientId);
    
    /**
     * 删除通知
     *
     * @param id 通知ID
     * @return 操作是否成功
     */
    boolean delete(Integer id);
    
    /**
     * 获取用户未读通知数量
     *
     * @param recipientId 接收者ID
     * @return 未读数量
     */
    int countUnreadNotifications(Integer recipientId);
    
    /**
     * 发送系统通知
     *
     * @param title 标题
     * @param content 内容
     * @param recipientId 接收者ID
     * @param notificationType 通知类型
     * @param referenceId 关联业务ID
     * @param referenceType 关联业务类型
     * @param actionUrl 操作链接
     * @return 新创建的通知ID
     */
    Integer sendSystemNotification(
        String title, 
        String content, 
        Integer recipientId, 
        String notificationType, 
        Integer referenceId, 
        String referenceType, 
        String actionUrl
    );
    
    /**
     * 发送转接申请通知
     * 
     * @param recipientId 接收者ID
     * @param transferId 转接申请ID
     * @param title 通知标题
     * @param content 通知内容
     * @return 新创建的通知ID
     */
    Integer sendTransferNotification(
        Integer recipientId,
        Integer transferId,
        String title,
        String content
    );
} 