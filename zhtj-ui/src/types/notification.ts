/**
 * 消息通知类型定义
 */
export interface Notification {
  id?: number;
  title: string;
  content: string;
  senderName: string;
  senderId: number;
  recipientId: number;
  notificationType: string;
  referenceId?: number;
  referenceType?: string;
  isRead: number;
  readTime?: string;
  priority: number;
  expireTime?: string;
  actionUrl?: string;
  createTime?: string;
  updateTime?: string;
}

/**
 * 通知查询参数
 */
export interface NotificationQuery {
  userId: number;
  isRead?: number;
  notificationType?: string;
  pageNum?: number;
  pageSize?: number;
  orderBy?: string;
}

/**
 * 通知类型选项
 */
export const NOTIFICATION_TYPES = [
  { value: '志愿服务', label: '志愿服务' },
  { value: '荣誉通知', label: '荣誉通知' },
  { value: '审批通知', label: '审批通知' },
  { value: '其他', label: '其他' }
];

/**
 * 通知优先级选项
 */
export const NOTIFICATION_PRIORITIES = [
  { value: 0, label: '普通' },
  { value: 1, label: '重要' },
  { value: 2, label: '紧急' }
];

/**
 * 通知已读状态选项
 */
export const NOTIFICATION_READ_STATUS = [
  { value: 0, label: '未读' },
  { value: 1, label: '已读' }
]; 