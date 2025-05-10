import api from './index'

// 定义接口类型
interface BatchParams {
  page?: number
  size?: number
  batchCode?: string
  year?: string
  status?: string
}

interface BatchData {
  id?: number
  title: string
  year: string
  startDate: string
  endDate: string
  targetOrganizationIds: number[]
  description?: string
}

interface RegisterParams {
  page?: number
  size?: number
  batchId?: number
  memberName?: string
  status?: string
}

interface RegisterData {
  id?: number
  batchId: number
  memberId: number
  status?: string
  politicalPerformance?: number
  studyPerformance?: number
  workPerformance?: number
  activityParticipation?: string
  paidFees?: boolean
  selfEvaluation?: string
  remarks?: string
  [key: string]: any
}

interface ApprovalData {
  ids?: number[]
  comments?: string
}

// 批次管理相关接口
export function getBatchList(params?: BatchParams) {
  return api.get('/api/register/batch', { params })
}

export function getBatchDetail(id: number) {
  return api({
    url: `/api/register/batch/${id}`,
    method: 'get'
  })
}

export function createBatch(data: BatchData) {
  return api.post('/api/register/batch', data)
}

export function updateBatch(id: number, data: BatchData) {
  return api.put(`/api/register/batch/${id}`, data)
}

export function deleteBatch(id: number) {
  return api.delete(`/api/register/batch/${id}`)
}

export function startBatch(id: number) {
  return api.post(`/api/register/batch/${id}/start`)
}

export function finishBatch(id: number) {
  return api.post(`/api/register/batch/${id}/end`)
}

export function getBatchOptions() {
  return api({
    url: '/api/register/batch/options',
    method: 'get'
  })
}

// 团员注册相关接口
export function getRegisterStatus() {
  return api.get('/api/register/status')
}

export function getRegisterHistory(params?: RegisterParams) {
  return api.get('/api/register/history', { params })
}

export function applyRegister(data: RegisterData) {
  return api.post('/api/register/apply', data)
}

// 审批相关接口
export function getApprovalList(params?: RegisterParams) {
  return api.get('/api/register/approval/list', { params })
}

export function approveRegister(id: number, comments?: string) {
  return api.post(`/api/register/approve/${id}/approve`, { comments })
}

export function rejectRegister(id: number, comments: string) {
  return api.post(`/api/register/approve/${id}/reject`, { comments })
}

export function batchApprove(data: ApprovalData) {
  return api.post('/api/register/approve/batch-approve', data)
}

export function batchReject(data: ApprovalData) {
  return api.post('/api/register/approve/batch-reject', data)
}

export function getApprovalStatistics() {
  return api.get('/api/register/approve/statistics')
}

// 更新注册状态
export function updateRegisterStatus(id: number, status: string, comment?: string) {
  // 根据状态调用不同的API
  if (status === '已通过') {
    return approveRegister(id, comment);
  } else if (status === '已驳回') {
    return rejectRegister(id, comment || '');
  } else {
    // 对于其他状态，创建一个新的自定义更新API请求
    return api.post(`/api/register/approve/${id}/update-status`, {
      status,
      comment
    });
  }
}

// 当前批次查询
export function getCurrentBatch() {
  return api.get('/api/register/batch/current')
}

// 获取批次成员列表
export function getBatchMembers(batchId: number, params?: { 
  page?: number, 
  size?: number, 
  keyword?: string, 
  status?: string
}) {
  // 确保batchId是Number类型
  const batchIdNum = typeof batchId === 'string' ? Number(batchId) : batchId;
  
  return api.get('/api/register/batch/members', { 
    params: {
      batchId: batchIdNum,
      ...params
    } 
  })
}

export function exportStatistics(params: { year?: string, organizationId?: number }) {
  return api({
    url: '/api/register/statistics/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
} 