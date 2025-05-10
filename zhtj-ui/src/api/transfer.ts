import api from './index';

/**
 * 转接申请接口
 */
export interface TransferRequest {
  transferInOrgId: number;      // 转入组织ID
  transferReason: string;       // 转接理由
  transferRemark?: string;      // 转接备注(可选)
}

/**
 * 转接申请详情接口
 */
export interface TransferDetail {
  id: number;
  transferUserId?: number;
  transferUserName: string;
  transferOutOrgId?: number;
  transferOutOrgName: string;
  transferInOrgId?: number;
  transferInOrgName: string;
  transferReason: string;
  transferRemark: string;
  applicationTime: string;
  status: string;
  statusCode: number;
  expireTime: string;
  outApprovalTime: string | null;
  outApproverName: string | null;
  outApproved: boolean | null;
  outApprovalRemark: string | null;
  inApprovalTime: string | null;
  inApproverName: string | null;
  inApproved: boolean | null;
  inApprovalRemark: string | null;
  transferUser?: any;
  transferOutOrg?: any;
  transferInOrg?: any;
  outApprover?: any;
  inApprover?: any;
}

/**
 * 转接申请列表项接口
 */
export interface TransferItem {
  id: number;
  transferUserId?: number;
  transferOutOrgId?: number;
  transferInOrgId?: number;
  transferReason?: string;
  transferRemark?: string;
  transferOutOrgName: string;
  transferInOrgName: string;
  applicationTime: string;
  status: string;
  statusCode: number;
  expireTime?: string;
  outApprovalTime?: string | null;
  outApproverId?: number | null;
  outApproved: boolean | null;
  outApprovalRemark?: string | null;
  inApprovalTime?: string | null;
  inApproverId?: number | null;
  inApproved: boolean | null;
  inApprovalRemark?: string | null;
  createTime?: string;
  updateTime?: string;
  transferUser?: any;
  transferOutOrg?: any;
  transferInOrg?: any;
  outApprover?: any;
  inApprover?: any;
  outOrganizationAdminId?: number | null;
  inOrganizationAdminId?: number | null;
}

/**
 * 查询参数接口
 */
export interface TransferQueryParams {
  page?: number;
  size?: number;
  status?: string;
  transferUserId?: number;
  mine?: boolean;
  type?: 'out' | 'in';  // 添加审批类型字段
}

/**
 * 审批参数接口
 */
export interface ApprovalParams {
  approval: boolean;
  approvalRemark?: string;
}

/**
 * 发起转接申请
 * @param data 申请数据
 * @returns 处理结果
 */
export const createTransfer = async (data: TransferRequest) => {
  try {
    return await api.post('/api/transfers', data);
  } catch (error) {
    console.error('发起转接申请失败', error);
    throw error;
  }
};

/**
 * 获取我的转接申请列表
 * @param params 查询参数
 * @returns 申请列表
 */
export const getMyTransfers = async (params: TransferQueryParams = {}) => {
  console.log('调用 getMyTransfers API，参数:', params);
  try {
    const response = await api.get('/api/transfers', { 
      params: {
        ...params,
        mine: params.mine === undefined ? true : params.mine
      } 
    });
    console.log('getMyTransfers API 返回原始数据:', response);
    
    // 处理响应数据
    if (response.code === 200 && response.data) {
      // 确保返回格式统一
      if (!response.success) {
        response.success = true;
      }
      
      if (response.data.list && Array.isArray(response.data.list)) {
        // 处理列表数据，确保字段名一致
        response.data.list = response.data.list.map((item: any) => {
          // 如果没有关键字段，尝试从其他可能的字段名获取
          if (!item.transferOutOrgName && item.transferOutOrg) {
            if (typeof item.transferOutOrg === 'string') {
              item.transferOutOrgName = item.transferOutOrg;
            } else if (item.transferOutOrg && item.transferOutOrg.name) {
              item.transferOutOrgName = item.transferOutOrg.name;
            }
          }
          
          if (!item.transferInOrgName && item.transferInOrg) {
            if (typeof item.transferInOrg === 'string') {
              item.transferInOrgName = item.transferInOrg;
            } else if (item.transferInOrg && item.transferInOrg.name) {
              item.transferInOrgName = item.transferInOrg.name;
            }
          }
          
          return item;
        });
      }
    }
    
    return response;
  } catch (error) {
    console.error('获取我的转接申请列表失败', error);
    throw error;
  }
};

/**
 * 获取待审批转接申请列表
 * @param params 查询参数
 * @returns 申请列表
 */
export const getApprovalTransfers = async (params: TransferQueryParams) => {
  try {
    const response = await api.get('/api/transfers/approval', { params });
    console.log('getApprovalTransfers API 返回原始数据:', response);
    
    // 处理响应数据
    if (response.code === 200 && response.data) {
      // 确保返回格式统一
      if (!response.success) {
        response.success = true;
      }
      
      // 处理非标准数据格式 - 检查是否直接返回用户信息而非列表
      if (!response.data.list && response.data.user) {
        console.log('API返回了用户信息而非列表格式，正在转换为标准格式');
        
        // 将用户信息转换为转接申请列表项
        const userData = response.data.user;
        const transferItem: any = {
          id: userData.id,
          transferUserId: userData.id,
          transferUserName: userData.name || '未知用户名',
          statusCode: 0, // 默认状态为"申请中"
          status: '申请中',
          transferOutOrgName: response.data.org_name || response.data.full_name || '未知组织',
          transferInOrgName: '目标组织', // 默认值
          applicationTime: userData.createTime || new Date().toISOString()
        };
        
        response.data = {
          list: [transferItem],
          total: 1
        };
        
        console.log('已将用户数据转换为标准列表格式:', response.data);
      } else if (Array.isArray(response.data)) {
        // 处理直接返回数组的情况
        console.log('API直接返回了数组格式，正在转换为标准格式');
        response.data = {
          list: response.data,
          total: response.data.length
        };
      }
      
      // 处理标准列表数据
      if (response.data.list && Array.isArray(response.data.list)) {
        // 处理列表数据，确保字段名一致
        response.data.list = response.data.list.map((item: any) => {
          // 如果没有关键字段，尝试从其他可能的字段名获取
          if (!item.transferOutOrgName && item.transferOutOrg) {
            if (typeof item.transferOutOrg === 'string') {
              item.transferOutOrgName = item.transferOutOrg;
            } else if (item.transferOutOrg && item.transferOutOrg.name) {
              item.transferOutOrgName = item.transferOutOrg.name;
            }
          }
          
          if (!item.transferInOrgName && item.transferInOrg) {
            if (typeof item.transferInOrg === 'string') {
              item.transferInOrgName = item.transferInOrg;
            } else if (item.transferInOrg && item.transferInOrg.name) {
              item.transferInOrgName = item.transferInOrg.name;
            }
          }
          
          // 确保有用户名称
          if (!item.transferUserName && item.transferUser) {
            if (typeof item.transferUser === 'string') {
              item.transferUserName = item.transferUser;
            } else if (item.transferUser && item.transferUser.name) {
              item.transferUserName = item.transferUser.name;
            }
          }
          
          // 增强：确保用户名称数据正确
          console.log('处理申请项用户数据:', {
            id: item.id,
            transferUserId: item.transferUserId,
            transferUserName: item.transferUserName,
            originalData: item
          });

          // 如果没有transferUserName但有transferUserId，尝试获取
          if (!item.transferUserName && item.transferUserId) {
            // 临时解决方案：使用ID作为名称
            item.transferUserName = `申请人(${item.transferUserId})`;
            console.warn(`申请ID ${item.id} 缺少申请人姓名，已使用ID ${item.transferUserId} 代替`);
          }
          
          return item;
        });
      } else if (!response.data.list) {
        // 如果仍然没有list字段，初始化为空数组
        console.warn('API返回数据格式异常，没有list字段且无法转换，设置为空列表');
        response.data.list = [];
        response.data.total = 0;
      }
    }
    
    return response;
  } catch (error) {
    console.error('获取待审批转接申请列表失败', error);
    throw error;
  }
};

/**
 * 获取转接申请详情
 * @param id 申请ID
 * @returns 申请详情
 */
export const getTransferDetail = async (id: number) => {
  try {
    return await api.get(`/api/transfers/${id}`);
  } catch (error) {
    console.error('获取转接申请详情失败', error);
    throw error;
  }
};

/**
 * 转出组织审批
 * @param id 申请ID
 * @param params 审批参数
 * @returns 处理结果
 */
export const outApprove = async (id: number, params: ApprovalParams) => {
  try {
    console.log(`调用转出审批API: /api/transfers/${id}/outapprove`, params);
    const response = await api.post(`/api/transfers/${id}/outapprove`, null, {
      params: params
    });
    
    console.log('转出审批API响应原始数据:', response);
    
    // 确保返回数据格式一致
    if (response.code === 200) {
      if (!response.success) {
        response.success = true;
      }
      
      // 如果没有消息，添加默认消息
      if (!response.message && response.data === true) {
        response.message = '转出审批成功';
      }
    }
    
    return response;
  } catch (error) {
    console.error('转出组织审批失败', error);
    throw error;
  }
};

/**
 * 转入组织审批
 * @param id 申请ID
 * @param params 审批参数
 * @returns 处理结果
 */
export const inApprove = async (id: number, params: ApprovalParams) => {
  try {
    console.log(`调用转入审批API: /api/transfers/${id}/inapprove`, params);
    const response = await api.post(`/api/transfers/${id}/inapprove`, null, {
      params: params
    });
    
    console.log('转入审批API响应原始数据:', response);
    
    // 确保返回数据格式一致
    if (response.code === 200) {
      if (!response.success) {
        response.success = true;
      }
      
      // 如果没有消息，添加默认消息
      if (!response.message && response.data === true) {
        response.message = '转入审批成功';
      }
    }
    
    return response;
  } catch (error) {
    console.error('转入组织审批失败', error);
    throw error;
  }
};

/**
 * 获取转接理由选项
 * @returns 理由选项列表
 */
export const getTransferReasons = () => {
  return [
    { value: '升学', label: '升学' },
    { value: '转学', label: '转学' },
    { value: '工作', label: '工作' },
    { value: '毕业', label: '毕业' },
    { value: '退学', label: '退学' },
    { value: '辞职', label: '辞职' },
    { value: '其他', label: '其他' }
  ];
};

/**
 * 获取申请状态文本
 * @param statusCode 状态码
 * @returns 状态文本
 */
export const getStatusText = (statusCode: number | string): string => {
  // 将字符串转换为数字
  const code = typeof statusCode === 'string' ? parseInt(statusCode, 10) : statusCode;
  
  switch (code) {
    case 0: return '申请中';
    case 1: return '转出审批中';
    case 2: return '转入审批中';
    case 3: return '已通过';
    case 4: return '已拒绝';
    default: return '未知状态';
  }
};

/**
 * 获取状态颜色
 * @param statusCode 状态码
 * @returns 状态颜色
 */
export const getStatusColor = (statusCode: number | string): string => {
  // 将字符串转换为数字
  const code = typeof statusCode === 'string' ? parseInt(statusCode, 10) : statusCode;
  
  switch (code) {
    case 0: return '#409eff'; // 蓝色
    case 1: return '#e6a23c'; // 橙色
    case 2: return '#67c23a'; // 绿色
    case 3: return '#67c23a'; // 绿色
    case 4: return '#f56c6c'; // 红色
    default: return '#909399'; // 灰色
  }
}; 