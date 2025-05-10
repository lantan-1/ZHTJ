import api from './index';

/**
 * 审批日志接口定义
 */
export interface TransferApprovalLog {
  id: number;
  transferId: number;
  approvalType: number;  // 1: 转出审批, 2: 转入审批
  approverId: number;
  approverName: string;
  approved: boolean;
  approvalRemark: string | null;
  operationIp: string;
  approvalTime: string;
  createTime: string;
}

/**
 * 获取转接申请的审批日志
 * @param transferId 转接申请ID
 * @returns 审批日志列表
 */
export const getApprovalLogs = async (transferId: number) => {
  try {
    return await api.get(`/api/transfers/${transferId}/logs`);
  } catch (error) {
    console.error('获取审批日志失败', error);
    throw error;
  }
};

/**
 * 获取用户的审批日志
 * @param userId 用户ID，不传则获取当前用户
 * @returns 审批日志列表
 */
export const getUserApprovalLogs = async (userId?: number) => {
  try {
    let url = '/api/users/approval-logs';
    if (userId) {
      url += `?userId=${userId}`;
    }
    return await api.get(url);
  } catch (error) {
    console.error('获取用户审批日志失败', error);
    throw error;
  }
};

/**
 * 根据审批类型获取转接申请的审批日志
 * @param transferId 转接申请ID
 * @param approvalType 审批类型 1: 转出审批, 2: 转入审批
 * @returns 审批日志
 */
export const getApprovalLogByType = async (transferId: number, approvalType: number) => {
  try {
    return await api.get(`/api/transfers/${transferId}/logs/${approvalType}`);
  } catch (error) {
    console.error('获取特定类型审批日志失败', error);
    throw error;
  }
}; 