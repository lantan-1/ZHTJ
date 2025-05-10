import api from './index';

/**
 * 获取用户的未读通知数量
 * @param userId 用户ID
 * @param notificationType 通知类型（可选）
 */
export function getUnreadCount(userId: number, notificationType?: string) {
  const params = { userId };
  if (notificationType) {
    Object.assign(params, { notificationType });
  }
  return api.get('/api/notification/unread-count', { params });
}

/**
 * 获取用户通知分页列表
 * @param userId 用户ID
 * @param params 其他查询参数
 */
export function getNotificationPage(userId: number, params: any = {}) {
  return api.get('/api/notification/page', { params: { ...params, userId } });
}

/**
 * 标记通知为已读
 * @param notificationId 通知ID
 * @param userId 用户ID
 */
export function markAsRead(notificationId: number, userId: number) {
  return api.put(`/api/notification/read/${notificationId}`, { userId });
}

/**
 * 批量更新通知已读状态
 * @param notificationIds 通知ID数组
 * @param userId 用户ID
 */
export function batchUpdateReadStatus(notificationIds: number[], userId: number) {
  return api.put('/api/notification/read/batch', { notificationIds, userId });
}

/**
 * 更新用户所有通知为已读
 * @param userId 用户ID
 */
export function markAllAsRead(userId: number) {
  return api.put('/api/notification/read/all', { userId });
}

/**
 * 发送通知
 * @param data 通知数据
 */
export function sendNotification(data: any) {
  return api.post('/api/notification', data);
}

/**
 * 根据引用业务ID和类型查询通知
 * @param referenceId 引用业务ID
 * @param referenceType 引用业务类型
 * @param recipientId 接收者ID（可选）
 */
export function getByReferenceInfo(referenceId: number, referenceType: string, recipientId?: number) {
  const params = { referenceId, referenceType };
  if (recipientId) {
    Object.assign(params, { recipientId });
  }
  return api.get('/api/notification/reference', { params });
}

/**
 * 删除通知
 * @param id 通知ID
 */
export function deleteNotification(id: number) {
  return api.delete(`/api/notification/${id}`);
}

/**
 * 删除过期通知
 */
export function deleteExpiredNotifications() {
  return api.delete('/api/notification/expired');
} 