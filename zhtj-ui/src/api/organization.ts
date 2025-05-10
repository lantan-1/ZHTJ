import api from './index';

// 默认空组织信息对象
export const emptyOrganizationInfo = {
  id: 0,
  name: '',
  fullName: '',
  type: '高校' // 提供一个默认类型值
};

// 获取组织详情
export const getOrganizationDetail = async (organizationId: number) => {
  try {
    console.log(`获取组织详情, ID: ${organizationId}`);
    
    // 确保organizationId是有效的
    if (!organizationId || organizationId <= 0) {
      console.error('无效的组织ID:', organizationId);
      return {
        success: false,
        data: null,
        message: '无效的组织ID'
      };
    }
    
    // 尝试不同的API路径格式
    let response;
    try {
      // 先尝试 /api/organizations/{id} 格式
      response = await api.get(`/api/organizations/${organizationId}`);
      console.log('组织详情API响应(路径1):', response);
    } catch (error) {
      console.error('路径1获取组织详情失败，尝试路径2', error);
      // 如果失败，尝试 /api/organization/{id} 格式
      try {
        response = await api.get(`/api/organization/${organizationId}`);
        console.log('组织详情API响应(路径2):', response);
      } catch (error) {
        console.error('路径2获取组织详情失败，尝试路径3', error);
        // 如果还是失败，尝试 /api/org/{id} 格式
        response = await api.get(`/api/org/${organizationId}`);
        console.log('组织详情API响应(路径3):', response);
      }
    }
    
    if (response && (response.success || response.code === 200)) {
      // 尝试提取组织数据
      const orgData = response.data || {};
      
      // 记录完整的响应数据用于调试
      console.log('获取到的组织数据:', orgData);
      
      return {
        success: true,
        data: {
          id: orgData.id || organizationId,
          name: orgData.name || orgData.org_name || '',
          fullName: orgData.fullName || orgData.full_name || orgData["full name"] || '',
          type: orgData.orgType || orgData.org_type || ''
        },
        message: '获取组织详情成功'
      };
    } else {
      console.error('API响应无效:', response);
      return {
        success: false,
        data: null,
        message: response?.message || '获取组织详情失败'
      };
    }
  } catch (error) {
    console.error('获取组织详情失败', error);
    return {
      success: false,
      data: null,
      message: '获取组织详情请求失败'
    };
  }
};

// 获取组织列表
export const getOrganizations = async () => {
  try {
    const response = await api.get('/api/organizations');
    return response;
  } catch (error) {
    console.error('获取组织列表失败', error);
    throw error;
  }
};

// 获取组织树结构
export const getOrganizationTree = async (rootId?: number) => {
  try {
    console.log('开始获取组织树结构，根ID:', rootId);
    
    const url = '/api/organizations/tree';
    const params = rootId ? { rootId } : {};
    
    console.log('请求组织树参数:', params);
    
    const response = await api.get(url, { params });
    console.log('组织树API响应:', response);
    
    // 验证和修正数据格式
    if (response?.data) {
      // 检查是否为数组
      if (!Array.isArray(response.data)) {
        console.warn('API返回的组织树不是数组格式，尝试修正');
        
        // 可能嵌套在data字段中
        if (response.data.data && Array.isArray(response.data.data)) {
          console.log('从data字段中提取到组织树数组');
          response.data = response.data.data;
        } else {
          // 将单个对象包装为数组
          console.log('将单个组织对象包装为数组');
          response.data = [response.data];
        }
      }
      
      // 确保有children字段，并且补充缺失的名称
      response.data = response.data.map((org: any) => {
        if (!org) return null;
        
        // 确保children是数组
        if (!org.children) org.children = [];
        
        // 确保有name字段
        if (!org.name && org.fullName) org.name = org.fullName;
        if (!org.name) org.name = '未命名组织';
        
        // 确保有label字段(用于树组件)
        if (!org.label) org.label = org.name;
        
        return org;
      }).filter((org: any) => org !== null);
      
      // 如果结果是空数组，使用默认数据
      if (response.data.length === 0) {
        console.warn('API返回的有效组织数据为空，使用默认数据');
        if (rootId) {
          // 如果指定了根节点ID但没有找到数据，可能是权限问题
          console.error('指定了组织ID但未找到数据，可能是权限问题:', rootId);
        }
        response.data = getDefaultOrganizationTree();
      }
    }
    
    return response;
  } catch (error) {
    console.error('获取组织树结构失败', error);
    // 返回一个带错误信息的响应，而不是抛出异常
    return {
      success: false,
      code: 500,
      message: '获取组织树结构失败',
      data: getDefaultOrganizationTree()
    };
  }
};

/**
 * 获取默认组织树结构(当API失败时使用)
 */
export const getDefaultOrganizationTree = () => {
  return [
    {
      id: 3,
      name: '清华大学团委',
      fullName: '清华大学共青团委员会',
      level: 0,
      path: '3',
      children: [
        {
          id: 14,
          name: '计算机学院团委',
          fullName: '清华大学计算机学院团委',
          level: 1,
          path: '3,14',
          children: [
            {
              id: 15,
              name: '计算机科学与技术系团支部',
              fullName: '清华大学计算机学院计算机科学与技术系团支部',
              level: 2,
              path: '3,14,15',
              children: []
            }
          ]
        },
        {
          id: 16,
          name: '电子工程学院团委',
          fullName: '清华大学电子工程学院团委',
          level: 1,
          path: '3,16',
          children: []
        }
      ]
    }
  ];
};

/**
 * 管理员工具 - 修复组织结构
 */
export const repairOrganizationStructure = async () => {
  try {
    console.log('开始请求修复组织结构');
    const response = await api.post('/admin/organizations/repair');
    console.log('修复组织结构响应:', response);
    return response;
  } catch (error) {
    console.error('修复组织结构失败:', error);
    throw error;
  }
}; 