package com.zhtj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhtj.domain.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 消息通知Mapper接口
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 分页查询用户通知
     *
     * @param page 分页对象
     * @param recipientId 接收者ID
     * @param isRead 是否已读
     * @return 分页结果
     */
    IPage<Notification> selectNotificationPage(
        Page<Notification> page,
        @Param("recipientId") Integer recipientId,
        @Param("isRead") Integer isRead
    );
    
    /**
     * 获取用户未读通知数量
     *
     * @param recipientId 接收者ID
     * @return 未读数量
     */
    int countUnreadNotifications(@Param("recipientId") Integer recipientId);
    
    /**
     * 标记通知为已读
     *
     * @param id 通知ID
     * @return 更新结果
     */
    @Update("UPDATE notification SET is_read = 1, read_time = NOW() WHERE id = #{id}")
    int markAsRead(@Param("id") Integer id);
    
    /**
     * 标记用户所有通知为已读
     *
     * @param recipientId 接收者ID
     * @return 更新结果
     */
    @Update("UPDATE notification SET is_read = 1, read_time = NOW() WHERE recipient_id = #{recipientId} AND is_read = 0")
    int markAllAsRead(@Param("recipientId") Integer recipientId);
} 