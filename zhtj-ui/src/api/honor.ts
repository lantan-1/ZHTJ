import api from './index';

/**
 * 检查用户申请资格（基于教育评议结果）
 * @returns Promise<any>
 */
export async function checkEligibility() {
  return await api.get(`/api/honors/check-eligibility`);
}

/**
 * 获取我的荣誉申请列表
 * @param params 查询参数 (page, size)
 * @returns Promise<any>
 */
export async function getMyHonors(params: any) {
  return await api.get('/api/honors/my-applications', { params });
}

/**
 * 提交荣誉申请
 * @param data 申请数据
 * @returns Promise<any>
 */
export async function submitHonorApplication(data: any) {
  return await api.post('/api/honors/applications', data);
}

/**
 * 获取待审批的荣誉申请列表
 * @param params 查询参数 (page, size, status)
 * @returns Promise<any>
 */
export async function getPendingHonorApplications(params: any) {
  return await api.get('/api/honors/pending-applications', { params });
}

/**
 * 审批荣誉申请
 * @param id 申请ID
 * @param approved 是否通过
 * @param reason 审批理由
 * @returns Promise<any>
 */
export async function approveHonorApplication(id: number, approved: boolean, reason: string) {
  return await api.post(`/api/honors/applications/${id}/approve`, {
    approved,
    reason
  });
}