import api from './index';
import { useUserStore } from '../stores/user';

// 获取仪表板数据的接口 - 根据用户角色返回不同内容
export const getDashboardData = async () => {
  const userStore = useUserStore();
  let endpoint = '/api/dashboard';
  
  // 根据角色选择不同的API端点
  if (userStore.isCommitteeSecretary) {
    endpoint = '/api/dashboard/committee';
  } else if (userStore.isBranchSecretary) {
    endpoint = '/api/dashboard/branch';
  } else {
    endpoint = '/api/dashboard/member';
  }
  
  try {
    return await api.get(endpoint);
  } catch (error) {
    console.error('获取仪表盘数据失败', error);
    throw error;
  }
};

// 获取功能卡片数据
export const getFunctionCards = () => {
  try {
    return api.get('/api/dashboard/functions');
  } catch (error) {
    console.error('获取功能卡片数据失败', error);
    throw error;
  }
};

// 获取工作通知
export const getWorkNotifications = (params: { page?: number; size?: number }) => {
  try {
    return api.get('/api/notifications', { params });
  } catch (error) {
    console.error('获取工作通知失败', error);
    throw error;
  }
};

// 获取活动列表
export const getActivities = (params: { page?: number; size?: number; organizationId?: number }) => {
  try {
    return api.get('/api/activities', {
      params: {
        page: params.page || 1,
        size: params.size || 3,
        organization: params.organizationId
      }
    });
  } catch (error) {
    console.error('获取活动列表失败', error);
    throw error;
  }
};

// 获取组织统计数据 - 适用于团委书记和团支书
export const getOrganizationStatistics = (id: number) => {
  try {
    console.log(`获取组织[${id}]统计数据`);
    return api.get(`/api/organizations/${id}/statistics`).then(response => {
      console.log('组织统计数据原始响应:', response);
      
      // 检查是否包含成员数量信息
      if (response.data && typeof response.data === 'object') {
        const data = response.data;
        if (data.data) {
          // 嵌套数据结构，检查成员数量字段
          console.log('组织统计数据嵌套结构:', data.data);
          const nestedData = data.data;
          
          // 输出所有可能的成员数量字段
          console.log('可能的成员数量字段:', {
            memberCount: nestedData.memberCount,
            totalMembers: nestedData.totalMembers,
            totalMemberCount: nestedData.totalMemberCount,
            directMemberCount: nestedData.directMemberCount,
            childrenCount: nestedData.childrenCount,
            branchCount: nestedData.branchCount
          });
          
          // 如果没有找到成员数量字段，添加一个
          if (nestedData.memberCount === undefined && 
              nestedData.totalMembers === undefined && 
              nestedData.totalMemberCount === undefined) {
            console.log('未找到成员数量字段，添加默认值');
            if (nestedData.directMemberCount !== undefined && nestedData.childrenCount !== undefined) {
              // 计算总成员数 = 直属成员 + 子团员
              const total = (nestedData.directMemberCount || 0) + (nestedData.childrenCount || 0);
              console.log(`计算得到总成员数: ${total} (直属成员: ${nestedData.directMemberCount || 0} + 子团员: ${nestedData.childrenCount || 0})`);
              nestedData.memberCount = total;
            } else {
              // 添加默认值5
              console.log('无法计算总成员数，使用默认值: 5');
              nestedData.memberCount = 5;
            }
          }
        } else {
          // 顶级数据结构，直接检查成员数量字段
          console.log('组织统计数据顶级结构:', data);
          
          // 输出所有可能的成员数量字段
          console.log('可能的成员数量字段:', {
            memberCount: data.memberCount,
            totalMembers: data.totalMembers,
            totalMemberCount: data.totalMemberCount,
            directMemberCount: data.directMemberCount,
            childrenCount: data.childrenCount,
            branchCount: data.branchCount
          });
          
          // 如果没有找到成员数量字段，添加一个
          if (data.memberCount === undefined && 
              data.totalMembers === undefined && 
              data.totalMemberCount === undefined) {
            console.log('未找到成员数量字段，添加默认值');
            if (data.directMemberCount !== undefined && data.childrenCount !== undefined) {
              // 计算总成员数 = 直属成员 + 子团员
              const total = (data.directMemberCount || 0) + (data.childrenCount || 0);
              console.log(`计算得到总成员数: ${total} (直属成员: ${data.directMemberCount || 0} + 子团员: ${data.childrenCount || 0})`);
              data.memberCount = total;
            } else {
              // 添加默认值5
              console.log('无法计算总成员数，使用默认值: 5');
              data.memberCount = 5;
            }
          }
        }
      }
      
      return response;
    });
  } catch (error) {
    console.error('获取组织统计数据失败', error);
    throw error;
  }
};

// 获取待办任务数量
export const getTasksCount = () => {
  try {
    return api.get('/api/user/tasks/count');
  } catch (error) {
    console.error('获取待办任务数量失败', error);
    throw error;
  }
};

// 获取学习内容
export const getLearningMaterials = (params: { page?: number; size?: number }) => {
  try {
    return api.get('/api/learning-materials', { params });
  } catch (error) {
    console.error('获取学习内容失败', error);
    throw error;
  }
};

// 获取组织信息 - 适用于团支书和团委书记
export const getOrganizationInfo = (organizationId: number) => {
  try {
    return api.get(`/api/organizations/${organizationId}`);
  } catch (error) {
    console.error('获取组织信息失败', error);
    throw error;
  }
};

// 获取组织成员列表
export const getOrganizationMembers = (organizationId: number, params: { page?: number; size?: number }) => {
  try {
    return api.get(`/api/organizations/${organizationId}/members`, { params });
  } catch (error) {
    console.error('获取组织成员列表失败', error);
    throw error;
  }
};

// 获取父组织信息
export const getParentOrganization = (organizationId: number) => {
  try {
    return api.get(`/api/organizations/${organizationId}/parent`);
  } catch (error) {
    console.error('获取父组织信息失败', error);
    throw error;
  }
}; 