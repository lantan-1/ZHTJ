import api from './index';

// 接口定义：学习资源类型
export interface StudyResource {
  id?: number;
  title: string;
  description?: string;
  fileUrl?: string;
  fileName?: string;
  fileSize?: number;
  format?: string;
  categoryId: number;
  categoryName?: string;
  organizationId: number;
  organizationName?: string;
  creatorId?: number;
  creatorName?: string;
  downloads?: number;
  createTime?: string;
  updateTime?: string;
}

// 查询参数接口
export interface ResourceQueryParams {
  title?: string;
  categoryId?: number;
  format?: string;
  organizationId?: number;
  keyword?: string;  // 添加关键词搜索
  startDate?: string; // 添加日期范围搜索
  endDate?: string;
  page: number;
  size: number;
}

/**
 * 获取资源预览信息
 * @param id 资源ID
 */
export function previewResource(id: number) {
  return api.get(`/api/study-resources/${id}/preview`);
}

/**
 * 获取用户可访问的组织列表
 */
export function getAccessibleOrganizations() {
  return api.get('/api/study-resources/accessible-organizations');
}

/**
 * 批量删除资源
 * @param ids 资源ID数组
 */
export function batchDeleteResources(ids: number[]) {
  return api.delete('/api/study-resources/batch', { data: { ids } });
}

/**
 * 检查用户对资源的操作权限
 * @param id 资源ID
 */
export function checkResourcePermission(id: number) {
  return api.get(`/api/study-resources/${id}/permissions`);
}

/**
 * 获取资源列表
 * @param params 查询参数
 */
export function getResourceList(params: ResourceQueryParams) {
  return api.get('/api/study-resources', { params });
}

/**
 * 获取资源详情
 * @param id 资源ID
 */
export function getResourceDetail(id: number) {
  return api.get(`/api/study-resources/${id}`);
}

/**
 * 上传资源
 * @param formData 含文件和资源信息的FormData
 */
export function uploadResource(formData: FormData) {
  return api.post('/api/study-resources/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

/**
 * 更新资源信息
 * @param id 资源ID
 * @param data 资源数据
 */
export function updateResource(id: number, data: Partial<StudyResource>) {
  return api.put(`/api/study-resources/${id}`, data);
}

/**
 * 删除资源
 * @param id 资源ID
 */
export function deleteResource(id: number) {
  return api.delete(`/api/study-resources/${id}`);
}

/**
 * 下载资源
 * @param id 资源ID
 */
export function downloadResource(id: number) {
  return api.get(`/api/study-resources/${id}/download`, {
    responseType: 'blob'
  });
}

/**
 * 获取所有资源分类
 */
export function getResourceCategories() {
  return api.get('/api/study-resources/categories');
}

/**
 * 获取文件类型列表
 */
export function getFileTypes() {
  return api.get('/api/study-resources/format-types');
} 