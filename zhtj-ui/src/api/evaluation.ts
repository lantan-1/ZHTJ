import api from './index';

// 定义API参数和返回类型
interface EvaluationParams {
  page?: number;
  size?: number;
  year?: string;
  status?: string;
  organizationId?: number;
  memberName?: string;
  result?: string;
}

interface EvaluationResult {
  id?: number;
  memberId?: number;
  result: string;
  comment?: string;
}

interface BatchEvaluationData {
  memberIds?: number[];
  recordIds?: number[];  // 添加评议记录ID数组字段
  result: string;
  comment?: string;
}

interface ResetEvaluationData {
  memberId: number;
}

interface ApiResponse<T = any> {
  data: {
    code: number;
    message: string;
    data: T;
  };
}

/**
 * 获取评议活动列表
 */
export const getEvaluationList = (params: EvaluationParams) => {
  return api.get('/api/member-evaluations', { params }).then((response: any) => {
    // 添加调试日志
    console.log('原始API返回数据:', response);
    
    // 检查响应数据结构并适配
    // 首先检查是否存在response.data.code形式的结构
    if (response && response.data && response.data.code === 200) {
      // 标准API封装格式 { code: 200, message: '', data: {} }
      const responseData = response.data.data;
      
      console.log('标准API封装格式,提取data:', responseData);
      
      // 构造统一返回格式
      return {
        success: true,
        items: Array.isArray(responseData) ? responseData : 
              (responseData && Array.isArray(responseData.items)) ? responseData.items : [],
        total: responseData && responseData.total ? responseData.total : 
              (Array.isArray(responseData) ? responseData.length : 0)
      };
    } 
    // 检查是否直接返回了数据数组或对象
    else if (response && (Array.isArray(response) || (response.items && Array.isArray(response.items)))) {
      console.log('直接数据格式，无封装:', response);
      
      // 直接返回了数据
      return {
        success: true,
        items: Array.isArray(response) ? response : response.items,
        total: Array.isArray(response) ? response.length : (response.total || response.items.length)
      };
    }
    // 处理其他可能的返回格式
    else {
      console.log('其他格式的返回数据:', response);
      
      // 尝试最宽松的解析方式，寻找任何可能的items数组
      let items = [];
      let total = 0;
      
      if (response && response.data) {
        if (Array.isArray(response.data)) {
          items = response.data;
          total = items.length;
        } else if (response.data.items && Array.isArray(response.data.items)) {
          items = response.data.items;
          total = response.data.total || items.length;
        }
      }
      
      return {
        success: true,
        items,
        total
      };
    }
  });
};

/**
 * 获取评议活动统计数据
 */
export const getEvaluationStatistics = (params: EvaluationParams & { timeRange?: string }) => {
  const organizationId = params.organizationId || '';
  // 移除organizationId从params中，因为它现在会在URL路径中
  const { organizationId: _, ...otherParams } = params;
  return api.get(`/api/member-evaluations/organization/${organizationId}/statistics`, { params: otherParams }).then((response: ApiResponse) => {
    // 检查响应数据结构并适配
    if (response.data && response.data.code === 200) {
      // 确保返回正确的数据结构
      const result = {
        success: true,
        data: response.data.data || { data: [] }
      };
      return result;
    }
    return response;
  });
};

/**
 * 获取单个评议活动详情
 */
export const getEvaluationDetail = (id: number) => {
  return api.get(`/api/member-evaluations/${id}`);
};

/**
 * 获取评议活动结果列表
 */
export const getEvaluationResults = (id: number, params: EvaluationParams) => {
  return api.get(`/api/member-evaluations/${id}/results`, { params }).then((response: any) => {
    // 添加调试日志
    console.log('评议结果原始API返回数据:', response);
    
    // 检查响应数据结构并适配
    if (response && response.data && response.data.code === 200) {
      // 标准API封装格式 { code: 200, message: '', data: {} }
      const responseData = response.data.data;
      console.log('标准API封装格式,提取评议结果:', responseData);
      
      // 检查responseData是否为null或undefined
      if (!responseData) {
        console.warn('API返回的data为null或undefined');
        return {
          success: true,
          data: {
            list: [],
            total: 0
          }
        };
      }
      
      return {
        success: true,
        data: {
          list: Array.isArray(responseData) ? responseData : 
                (responseData && Array.isArray(responseData.items)) ? responseData.items : 
                (responseData && responseData.list && Array.isArray(responseData.list)) ? responseData.list : [],
          total: responseData && responseData.total ? responseData.total : 
                (Array.isArray(responseData) ? responseData.length : 
                (responseData.items ? responseData.items.length : 
                (responseData.list ? responseData.list.length : 0)))
        }
      };
    } 
    // 检查截图中看到的格式：直接返回 {items: Array(2), total: 2}
    else if (response && response.items && Array.isArray(response.items)) {
      console.log('返回格式具有items属性:', response);
      
      return {
        success: true,
        data: {
          list: response.items,
          total: response.total || response.items.length
        }
      };
    }
    // 检查是否直接返回了数据数组
    else if (response && Array.isArray(response)) {
      console.log('直接返回数组格式:', response);
      
      return {
        success: true,
        data: {
          list: response,
          total: response.length
        }
      };
    }
    // 处理其他可能的返回格式
    else {
      console.log('其他格式的返回数据:', response);
      
      // 尝试最宽松的解析方式，寻找任何可能的items或list数组
      let items = [];
      let total = 0;
      
      if (response) {
        if (Array.isArray(response)) {
          items = response;
          total = items.length;
        } else if (response.data) {
          if (Array.isArray(response.data)) {
            items = response.data;
            total = items.length;
          } else if (response.data.items && Array.isArray(response.data.items)) {
            items = response.data.items;
            total = response.data.total || items.length;
          } else if (response.data.list && Array.isArray(response.data.list)) {
            items = response.data.list;
            total = response.data.total || items.length;
          }
        } else if (response.items && Array.isArray(response.items)) {
          items = response.items;
          total = response.total || items.length;
        } else if (response.list && Array.isArray(response.list)) {
          items = response.list;
          total = response.total || items.length;
        }
      }
      
      return {
        success: true,
        data: {
          list: items,
          total
        }
      };
    }
  });
};

/**
 * 获取评议活动结果统计
 */
export const getEvaluationResultStatistics = (id: number) => {
  return api.get(`/api/member-evaluations/${id}/statistics`);
};

/**
 * 提交评议（支持单个或批量）
 * 统一接口：无论是单个评议还是批量评议，都使用同一个接口
 * @param id 评议活动ID
 * @param data 单个评议结果或多个评议结果
 * @returns
 */
export const submitEvaluation = (id: number, data: EvaluationResult | EvaluationResult[] | BatchEvaluationData) => {
  // 检查数据类型，确保始终以数组形式发送
  let dataToSubmit: any[];
  
  // 如果是批量评议数据格式（包含新格式的recordIds或旧格式的memberIds数组）
  if (
    ('memberIds' in data && Array.isArray(data.memberIds)) || 
    ('recordIds' in data && Array.isArray(data.recordIds))
  ) {
    // 直接发送原始数据格式，后端会处理
    console.log('提交批量评议数据:', data);
    return api.post(`/api/member-evaluations/${id}/batch-results`, data);
  } 
  // 如果已经是数组形式的评议结果，直接使用
  else if (Array.isArray(data)) {
    // 确保每个元素都有用于识别的id或memberId字段
    dataToSubmit = data.map(item => {
      if (!item.id && !item.memberId) {
        console.warn('评议项缺少id和memberId字段:', item);
      }
      return item;
    });
  } 
  // 如果是单个评议，转换为数组
  else {
    dataToSubmit = [data];
  }
  
  // 对于非批量格式使用标准API
  console.log('提交评议数据:', dataToSubmit);
  return api.post(`/api/member-evaluations/${id}/results`, dataToSubmit);
};

/**
 * 重置评议结果
 */
export const resetEvaluation = (id: number, data: ResetEvaluationData) => {
  return api.post(`/api/member-evaluations/${id}/reset`, data);
};

/**
 * 发送评议提醒
 */
export const sendEvaluationReminder = (id: number) => {
  return api.post(`/api/member-evaluations/${id}/reminder`);
};

/**
 * 开始评议活动
 */
export const startEvaluation = (id: number) => {
  return api.post(`/api/member-evaluations/${id}/start`);
};

/**
 * 完成评议活动
 */
export const completeEvaluation = (id: number) => {
  return api.post(`/api/member-evaluations/${id}/end`);
};

/**
 * 删除评议活动
 */
export const deleteEvaluation = (id: number) => {
  return api.delete(`/api/member-evaluations/${id}`);
};

/**
 * 创建评议活动
 */
export const createEvaluation = (data: any) => {
  return api.post('/api/member-evaluations', data);
};

/**
 * 更新评议活动
 */
export const updateEvaluation = (id: number, data: any) => {
  return api.put(`/api/member-evaluations/${id}`, data);
};

/**
 * 导出评议结果
 */
export const exportEvaluationResults = (id: number) => {
  return `${api.defaults.baseURL}/api/member-evaluations/${id}/export`;
}; 