/**
 * 学习资源模块类型定义
 */

// 学习资源对象
export interface StudyResource {
  id: number;
  title: string;
  description: string;
  categoryId: number;
  categoryName: string;
  fileUrl: string;
  fileName: string;
  fileSize: number;
  format: string;
  creatorId: number;
  creatorName: string;
  organizationId: number;
  organizationName: string;
  downloads: number;
  createTime: string;
  updateTime: string;
  previewUrl?: string;
  downloadUrl?: string;
  permissions?: ResourcePermissions;
}

// 资源权限
export interface ResourcePermissions {
  canView: boolean;
  canDownload: boolean;
  canEdit: boolean;
  canDelete: boolean;
}

// 学习资源查询参数
export interface StudyResourceQuery {
  pageNum: number;
  pageSize: number;
  title?: string;
  categoryId?: number;
  format?: string;
  organizationId?: number;
  keyword?: string;
  startDate?: string;
  endDate?: string;
}

// 资源分类
export interface ResourceCategory {
  id: number;
  name: string;
}

// 团务百科分类
export interface WikiCategory {
  id: string;
  name: string;
}

// 团务百科文章
export interface WikiArticle {
  title: string;
  updateTime: string;
  content: string;
} 