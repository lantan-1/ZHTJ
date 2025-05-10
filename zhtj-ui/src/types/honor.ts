/**
 * 荣誉类型定义
 */
export interface HonorType {
  id?: number;
  name: string;
  description?: string;
  category: string;
  level: string;
  score: number;
  applicationCondition?: string;
  reviewProcess?: string;
  materialRequirement?: string;
  status: number;
  issueDepartment?: string;
  validPeriod?: number;
  createTime?: string;
  updateTime?: string;
}

/**
 * 荣誉申请定义
 */
export interface HonorApplication {
  id?: number;
  honorTypeId: number;
  honorType?: HonorType;
  applicantId: number;
  applicantName: string;
  organizationId?: number;
  organizationName?: string;
  applicationYear: number;
  applicationTime?: string;
  applicationReason: string;
  supportingMaterials?: string | string[];
  status: number;
  branchApproverId?: number;
  branchApprovalTime?: string;
  branchApprovalComment?: string;
  higherApproverId?: number;
  higherApprovalTime?: string;
  higherApprovalComment?: string;
  createTime?: string;
  updateTime?: string;
}

/**
 * 荣誉授予记录定义
 */
export interface HonorAward {
  id?: number;
  honorTypeId: number;
  honorTypeName: string;
  applicationId?: number;
  userId?: number;
  userName?: string;
  organizationId?: number;
  organizationName?: string;
  awardYear: number;
  awardTime: string;
  certificateNo?: string;
  certificateImage?: string;
  awardReason?: string;
  awardDescription?: string;
  awardingDepartment: string;
  awardingUserId: number;
  awardingUserName: string;
  score: number;
  createTime?: string;
  updateTime?: string;
}

/**
 * 荣誉类型查询参数
 */
export interface HonorTypeQuery {
  category?: string;
  level?: string;
  status?: number;
  keyword?: string;
  pageNum?: number;
  pageSize?: number;
  orderBy?: string;
}

/**
 * 荣誉申请查询参数
 */
export interface HonorApplicationQuery {
  applicantId?: number;
  organizationId?: number;
  year?: number;
  status?: number;
  honorTypeId?: number;
  category?: string;
  level?: string;
  pageNum?: number;
  pageSize?: number;
  orderBy?: string;
}

/**
 * 荣誉授予记录查询参数
 */
export interface HonorAwardQuery {
  userId?: number;
  organizationId?: number;
  year?: number;
  honorTypeId?: number;
  applicationId?: number;
  startTime?: string;
  endTime?: string;
  pageNum?: number;
  pageSize?: number;
  orderBy?: string;
}

/**
 * 荣誉类别选项
 */
export const HONOR_CATEGORIES = [
  { value: '个人荣誉', label: '个人荣誉' },
  { value: '组织荣誉', label: '组织荣誉' },
  { value: '集体荣誉', label: '集体荣誉' }
];

/**
 * 荣誉级别选项
 */
export const HONOR_LEVELS = [
  { value: '院级', label: '院级' },
  { value: '校级', label: '校级' },
  { value: '市级', label: '市级' },
  { value: '省级', label: '省级' },
  { value: '国家级', label: '国家级' }
];

/**
 * 荣誉类型状态选项
 */
export const HONOR_TYPE_STATUS_OPTIONS = [
  { value: 0, label: '禁用' },
  { value: 1, label: '启用' }
];

/**
 * 荣誉申请状态选项
 */
export const HONOR_APPLICATION_STATUS_OPTIONS = [
  { value: 0, label: '待审核' },
  { value: 1, label: '院级审核通过' },
  { value: 2, label: '院级审核不通过' },
  { value: 3, label: '校级审核通过' },
  { value: 4, label: '校级审核不通过' }
]; 