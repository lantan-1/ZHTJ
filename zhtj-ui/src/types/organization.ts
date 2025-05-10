export interface Organization {
  id: number;
  name: string;
  fullName: string;
  orgType: string;
  memberCount: number;
  createTime: string;
  parentId: number;
  description?: string;
  parentName?: string;
} 