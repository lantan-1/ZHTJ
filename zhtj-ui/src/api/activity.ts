import api from './index';

export interface Activity {
  id?: number;
  organization?: number;
  category: string;
  requiredTopic?: string;
  optionalTopics?: string;
  participants?: string;
  date: string;
  place: string;
  content: string;
  host?: string;
  organizationName?: string;
  createTime?: string;
  updateTime?: string;
}

// 添加活动
export const addActivity = (data: Activity): Promise<any> => {
  return api.post('/api/activities', data);
};

// 删除活动
export const deleteActivity = (id: number): Promise<any> => {
  return api.delete(`/api/activities/${id}`);
};

// 修改活动
export const updateActivity = (data: Activity): Promise<any> => {
  if (!data.id) {
    return Promise.reject('活动ID不能为空');
  }
  return api.put(`/api/activities/${data.id}`, data);
};

// 获取组织的所有活动
export const getActivities = (params?: { 
  page?: number; 
  size?: number; 
  organization?: number;
  category?: string;
  startDate?: string;
  endDate?: string;
}): Promise<any> => {
  return api.get('/api/activities', { params });
};

// 获取活动详情
export const getActivityDetail = (id: number): Promise<any> => {
  return api.get(`/api/activities/${id}`);
};

// 根据日期查询活动
export const getActivitiesByDate = (params: { 
  organization: number; 
  date: string 
}): Promise<any> => {
  return api.get('/api/activities/date', { params });
};

// 获取活动类别统计
export const getActivityStatistics = (organization: number): Promise<any> => {
  return api.get('/api/activities/statistics/category', { 
    params: { organization } 
  });
}; 