/**
 * 志愿服务类型定义
 */
export interface VolunteerService {
  id?: number;
  userId: number;
  userName: string;
  organizationId: number;
  organizationName: string;
  serviceYear: number;
  serviceDuration: number;
  serviceDescription: string;
  serviceLocation: string;
  serviceType: string;
  serviceStartTime: string;
  serviceEndTime: string;
  serviceProof?: string | string[];
  verificationStatus: number;
  verificationRemark?: string;
  verifierUserId?: number;
  verifierUserName?: string;
  verificationTime?: string;
  createTime?: string;
  updateTime?: string;
}

/**
 * 志愿服务查询参数
 */
export interface VolunteerServiceQuery {
  userId?: number;
  organizationId?: number;
  year?: number;
  verificationStatus?: number;
  serviceType?: string;
  startTime?: string;
  endTime?: string;
  pageNum?: number;
  pageSize?: number;
  orderBy?: string;
}

/**
 * 志愿服务验证参数
 */
export interface VerifyServiceParams {
  id: number;
  status: number;
  remark: string;
}

/**
 * 批量验证服务参数
 */
export interface BatchVerifyParams {
  serviceIds: number[];
  status: number;
  remark: string;
  verifierUserId: number;
  verifierUserName: string;
}

/**
 * 服务统计数据
 */
export interface ServiceStats {
  year: number;
  serviceCount: number;
  totalHours: number;
  volunteerCount?: number;
}

/**
 * 服务类型选项
 */
export const SERVICE_TYPES = [
  { value: '社区服务', label: '社区服务' },
  { value: '环保活动', label: '环保活动' },
  { value: '关爱老人', label: '关爱老人' },
  { value: '支教帮扶', label: '支教帮扶' },
  { value: '校园服务', label: '校园服务' },
  { value: '赛事服务', label: '赛事服务' },
  { value: '医疗卫生', label: '医疗卫生' },
  { value: '文化宣传', label: '文化宣传' },
  { value: '其他', label: '其他' }
];

/**
 * 验证状态选项
 */
export const VERIFICATION_STATUS_OPTIONS = [
  { value: 0, label: '待验证' },
  { value: 1, label: '已验证' },
  { value: 2, label: '验证不通过' }
]; 