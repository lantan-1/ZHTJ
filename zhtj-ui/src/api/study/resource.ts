import request from '@/api/index';

// 学习资源列表查询
export function listStudyResources(query: {
  pageNum: number;
  pageSize: number;
  title?: string;
  categoryId?: number;
  format?: string;
  organizationId?: number;
  keyword?: string;
  startDate?: string;
  endDate?: string;
}) {
  return request({
    url: '/study/resource/list',
    method: 'get',
    params: query
  });
}

// 获取学习资源详情
export function getStudyResource(id: number) {
  return request({
    url: `/study/resource/${id}`,
    method: 'get'
  });
}

// 获取学习资源下载链接
export function getDownloadUrl(id: number) {
  return request({
    url: `/study/resource/download/${id}`,
    method: 'get'
  });
}

// 增加下载计数
export function incrementDownloadCount(id: number) {
  return request({
    url: `/study/resource/record-download/${id}`,
    method: 'put'
  });
}

// 上传学习资源
export function uploadStudyResource(data: FormData) {
  return request({
    url: '/study/resource',
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  });
}

// 更新学习资源
export function updateStudyResource(data: FormData) {
  return request({
    url: '/study/resource',
    method: 'put',
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    data: data
  });
}

// 删除学习资源
export function deleteStudyResource(id: number) {
  return request({
    url: `/study/resource/${id}`,
    method: 'delete'
  });
}

// 批量删除学习资源
export function batchDeleteStudyResources(ids: number[]) {
  return request({
    url: '/study/resource/batch-delete',
    method: 'delete',
    data: ids
  });
}

// 获取所有资源分类
export function getAllCategories() {
  return request({
    url: '/study/resource/categories',
    method: 'get'
  });
}

// 获取用户可访问的组织列表
export function getAccessibleOrganizations() {
  return request({
    url: '/study/resource/organizations',
    method: 'get'
  });
}

// 获取资源预览信息
export function getResourcePreviewInfo(id: number) {
  return request({
    url: `/study/resource/preview/${id}`,
    method: 'get'
  });
}

// 检查资源权限
export function checkResourcePermissions(id: number) {
  return request({
    url: `/study/resource/permissions/${id}`,
    method: 'get'
  });
}

// 获取资源统计数据
export function getResourceStatistics(organizationId?: number) {
  return request({
    url: '/study/resource/statistics',
    method: 'get',
    params: { organizationId }
  });
}

// 资源格式类型定义
export interface ResourceFormat {
  key: string;
  label: string;
  icon: string;
}

// 常见文件格式分类
export const resourceFormats: ResourceFormat[] = [
  { key: 'DOC', label: 'Word文档', icon: 'document' },
  { key: 'DOCX', label: 'Word文档', icon: 'document' },
  { key: 'XLS', label: 'Excel表格', icon: 'excel' },
  { key: 'XLSX', label: 'Excel表格', icon: 'excel' },
  { key: 'PPT', label: 'PPT演示文稿', icon: 'ppt' },
  { key: 'PPTX', label: 'PPT演示文稿', icon: 'ppt' },
  { key: 'PDF', label: 'PDF文档', icon: 'pdf' },
  { key: 'TXT', label: '文本文件', icon: 'document' },
  { key: 'JPG', label: '图片', icon: 'image' },
  { key: 'JPEG', label: '图片', icon: 'image' },
  { key: 'PNG', label: '图片', icon: 'image' },
  { key: 'GIF', label: '图片', icon: 'image' },
  { key: 'MP4', label: '视频', icon: 'video' },
  { key: 'MP3', label: '音频', icon: 'audio' },
  { key: 'ZIP', label: '压缩包', icon: 'zip' },
  { key: 'RAR', label: '压缩包', icon: 'zip' },
  { key: 'OTHER', label: '其他文件', icon: 'file' }
]; 