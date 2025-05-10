import api from './index';
import type { VolunteerService, ServiceStats } from '@/types/volunteer';

// 模拟数据：志愿服务记录列表
const MOCK_VOLUNTEER_SERVICES: VolunteerService[] = [
  {
    id: 1,
    userId: 1001,
    userName: '张三',
    organizationId: 101,
    organizationName: '计算机学院团委',
    serviceYear: 2025,
    serviceDuration: 4.5,
    serviceDescription: '参与校园环境清洁活动，负责图书馆周边区域的垃圾清理和绿化养护',
    serviceLocation: '校园图书馆',
    serviceType: '环保活动',
    serviceStartTime: '2025-04-15T09:00:00',
    serviceEndTime: '2025-04-15T13:30:00',
    serviceProof: JSON.stringify(['https://example.com/proof1.jpg', 'https://example.com/proof2.jpg']),
    verificationStatus: 1,
    verificationRemark: '服务内容属实，证明材料完整',
    verifierUserId: 2001,
    verifierUserName: '李老师',
    verificationTime: '2025-04-16T10:20:00',
    createTime: '2025-04-15T14:30:00',
    updateTime: '2025-04-16T10:20:00'
  },
  {
    id: 2,
    userId: 1001,
    userName: '张三',
    organizationId: 101,
    organizationName: '计算机学院团委',
    serviceYear: 2025,
    serviceDuration: 3,
    serviceDescription: '参与社区敬老院志愿活动，为老人们表演节目并提供生活协助',
    serviceLocation: '阳光敬老院',
    serviceType: '关爱老人',
    serviceStartTime: '2025-03-20T14:00:00',
    serviceEndTime: '2025-03-20T17:00:00',
    serviceProof: JSON.stringify(['https://example.com/proof3.jpg']),
    verificationStatus: 1,
    verificationRemark: '表现良好',
    verifierUserId: 2001,
    verifierUserName: '李老师',
    verificationTime: '2025-03-21T09:15:00',
    createTime: '2025-03-20T18:00:00',
    updateTime: '2025-03-21T09:15:00'
  },
  {
    id: 3,
    userId: 1001,
    userName: '张三',
    organizationId: 101,
    organizationName: '计算机学院团委',
    serviceYear: 2025,
    serviceDuration: 2,
    serviceDescription: '校运动会志愿者，负责100米跑道的计时和记录工作',
    serviceLocation: '校体育场',
    serviceType: '赛事服务',
    serviceStartTime: '2025-05-01T08:00:00',
    serviceEndTime: '2025-05-01T10:00:00',
    serviceProof: JSON.stringify(['https://example.com/proof4.jpg']),
    verificationStatus: 0,
    createTime: '2025-05-01T10:30:00',
    updateTime: '2025-05-01T10:30:00'
  },
  {
    id: 4,
    userId: 1001,
    userName: '张三',
    organizationId: 101,
    organizationName: '计算机学院团委',
    serviceYear: 2024,
    serviceDuration: 6,
    serviceDescription: '寒假返乡社区防疫宣传志愿服务，负责发放宣传材料和解答居民问题',
    serviceLocation: '家乡社区',
    serviceType: '医疗卫生',
    serviceStartTime: '2024-12-20T09:00:00',
    serviceEndTime: '2024-12-20T15:00:00',
    serviceProof: JSON.stringify(['https://example.com/proof5.jpg', 'https://example.com/proof6.jpg']),
    verificationStatus: 1,
    verificationRemark: '服务时间充足，宣传效果好',
    verifierUserId: 2002,
    verifierUserName: '王主任',
    verificationTime: '2024-12-25T14:20:00',
    createTime: '2024-12-21T08:30:00',
    updateTime: '2024-12-25T14:20:00'
  },
  {
    id: 5,
    userId: 1001,
    userName: '张三',
    organizationId: 101,
    organizationName: '计算机学院团委',
    serviceYear: 2024,
    serviceDuration: 2.5,
    serviceDescription: '参与小学生科技知识普及活动，讲解编程基础知识',
    serviceLocation: '希望小学',
    serviceType: '支教帮扶',
    serviceStartTime: '2024-11-15T14:00:00',
    serviceEndTime: '2024-11-15T16:30:00',
    serviceProof: JSON.stringify(['https://example.com/proof7.jpg']),
    verificationStatus: 2,
    verificationRemark: '提交的服务证明材料不足，请补充班主任签字确认的服务证明',
    verifierUserId: 2001,
    verifierUserName: '李老师',
    verificationTime: '2024-11-16T10:00:00',
    createTime: '2024-11-15T17:00:00',
    updateTime: '2024-11-16T10:00:00'
  }
];

// 模拟数据：服务统计
const MOCK_SERVICE_STATS: ServiceStats[] = [
  { year: 2025, serviceCount: 3, totalHours: 9.5, volunteerCount: 45 },
  { year: 2024, serviceCount: 8, totalHours: 18.5, volunteerCount: 62 },
  { year: 2023, serviceCount: 5, totalHours: 12, volunteerCount: 38 },
  { year: 2022, serviceCount: 4, totalHours: 10.5, volunteerCount: 30 }
];

/**
 * 获取志愿服务分页列表（模拟数据）
 * @param params 查询参数
 */
export function getVolunteerServicePage(params: any) {
  console.log('模拟请求志愿服务列表数据', params);
  
  // 根据查询条件过滤数据
  let filtered = [...MOCK_VOLUNTEER_SERVICES];
  
  if (params.userId) {
    filtered = filtered.filter(item => item.userId === params.userId);
  }
  
  if (params.organizationId) {
    filtered = filtered.filter(item => item.organizationId === params.organizationId);
  }
  
  if (params.year) {
    filtered = filtered.filter(item => item.serviceYear === params.year);
  }
  
  if (params.verificationStatus !== undefined) {
    filtered = filtered.filter(item => item.verificationStatus === params.verificationStatus);
  }
  
  if (params.serviceType) {
    filtered = filtered.filter(item => item.serviceType === params.serviceType);
  }

  // 模拟分页
  const pageNum = params.pageNum || 1;
  const pageSize = params.pageSize || 10;
  const start = (pageNum - 1) * pageSize;
  const end = start + pageSize;
  const paginated = filtered.slice(start, end);

  // 返回模拟的响应格式
  return Promise.resolve({
    list: paginated,
    total: filtered.length,
    pageNum,
    pageSize
  });
  
  // 注释掉原始API调用
  // return api.get('/api/volunteer/service/page', { params });
}

/**
 * 获取用户的志愿服务总时长（模拟数据）
 * @param userId 用户ID
 * @param year 年份（可选）
 */
export function getUserTotalServiceHours(userId: number, year?: number) {
  console.log('模拟请求用户志愿服务总时长', { userId, year });
  
  let filtered = MOCK_VOLUNTEER_SERVICES.filter(item => item.userId === userId);
  
  if (year) {
    filtered = filtered.filter(item => item.serviceYear === year);
  }
  
  // 只计算已验证的服务时长
  const verifiedServices = filtered.filter(item => item.verificationStatus === 1);
  const totalHours = verifiedServices.reduce((sum, item) => sum + item.serviceDuration, 0);
  
  return Promise.resolve({ totalHours });
  
  // 注释掉原始API调用
  // const params = { userId };
  // if (year) {
  //   Object.assign(params, { year });
  // }
  // return api.get('/api/volunteer/service/user/total-hours', { params });
}

/**
 * 获取组织的志愿服务总时长（模拟数据）
 * @param organizationId 组织ID
 * @param year 年份（可选）
 */
export function getOrganizationTotalServiceHours(organizationId: number, year?: number) {
  console.log('模拟请求组织志愿服务总时长', { organizationId, year });
  
  let filtered = MOCK_VOLUNTEER_SERVICES.filter(item => item.organizationId === organizationId);
  
  if (year) {
    filtered = filtered.filter(item => item.serviceYear === year);
  }
  
  // 只计算已验证的服务时长
  const verifiedServices = filtered.filter(item => item.verificationStatus === 1);
  const totalHours = verifiedServices.reduce((sum, item) => sum + item.serviceDuration, 0);
  
  return Promise.resolve({ totalHours, volunteerCount: Math.floor(totalHours * 2.5) });
  
  // 注释掉原始API调用
  // const params = { organizationId };
  // if (year) {
  //   Object.assign(params, { year });
  // }
  // return api.get('/api/volunteer/service/organization/total-hours', { params });
}

/**
 * 提交志愿服务记录（模拟数据）
 * @param data 志愿服务数据
 */
export function submitVolunteerService(data: any) {
  console.log('模拟提交志愿服务记录', data);
  
  // 模拟创建新记录
  const newService = {
    id: MOCK_VOLUNTEER_SERVICES.length + 1,
    ...data,
    verificationStatus: 0,
    createTime: new Date().toISOString(),
    updateTime: new Date().toISOString()
  };
  
  // 将新记录添加到模拟数据中
  MOCK_VOLUNTEER_SERVICES.push(newService as VolunteerService);
  
  return Promise.resolve({ success: true, message: '提交成功', data: newService });
  
  // 注释掉原始API调用
  // return api.post('/api/volunteer/service', data);
}

/**
 * 验证志愿服务（模拟数据）
 * @param id 服务ID
 * @param status 验证状态
 * @param remark 验证备注
 */
export function verifyVolunteerService(id: number, status: number, remark: string) {
  console.log('模拟验证志愿服务', { id, status, remark });
  
  // 查找并更新记录
  const serviceIndex = MOCK_VOLUNTEER_SERVICES.findIndex(item => item.id === id);
  
  if (serviceIndex !== -1) {
    MOCK_VOLUNTEER_SERVICES[serviceIndex] = {
      ...MOCK_VOLUNTEER_SERVICES[serviceIndex],
      verificationStatus: status,
      verificationRemark: remark,
      verifierUserId: 2001,
      verifierUserName: '李老师',
      verificationTime: new Date().toISOString(),
      updateTime: new Date().toISOString()
    };
  }
  
  return Promise.resolve({ success: true, message: '验证成功' });
  
  // 注释掉原始API调用
  // return api.put(`/api/volunteer/service/verify/${id}`, { status, remark });
}

/**
 * 批量验证志愿服务（模拟数据）
 * @param serviceIds 服务ID数组
 * @param status 验证状态
 * @param remark 验证备注
 */
export function batchVerifyServices(serviceIds: number[], status: number, remark: string) {
  console.log('模拟批量验证志愿服务', { serviceIds, status, remark });
  
  // 批量更新记录
  serviceIds.forEach(id => {
    const serviceIndex = MOCK_VOLUNTEER_SERVICES.findIndex(item => item.id === id);
    
    if (serviceIndex !== -1) {
      MOCK_VOLUNTEER_SERVICES[serviceIndex] = {
        ...MOCK_VOLUNTEER_SERVICES[serviceIndex],
        verificationStatus: status,
        verificationRemark: remark,
        verifierUserId: 2001,
        verifierUserName: '李老师',
        verificationTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      };
    }
  });
  
  return Promise.resolve({ success: true, message: '批量验证成功' });
  
  // 注释掉原始API调用
  // return api.put('/api/volunteer/service/verify/batch', { serviceIds, status, remark });
}

/**
 * 获取用户按年份的服务统计（模拟数据）
 * @param userId 用户ID
 */
export function getUserServiceStatsByYear(userId: number) {
  console.log('模拟请求用户按年份服务统计', { userId });
  
  // 过滤获取该用户的服务记录
  const userServices = MOCK_VOLUNTEER_SERVICES.filter(item => item.userId === userId);
  
  // 按年份分组并统计
  const years = Array.from(new Set(userServices.map(item => item.serviceYear)));
  const stats = years.map(year => {
    const yearServices = userServices.filter(item => item.serviceYear === year);
    const verifiedServices = yearServices.filter(item => item.verificationStatus === 1);
    
    return {
      year,
      serviceCount: yearServices.length,
      totalHours: verifiedServices.reduce((sum, item) => sum + item.serviceDuration, 0)
    };
  });
  
  return Promise.resolve(stats);
  
  // 注释掉原始API调用
  // return api.get(`/api/volunteer/service/user/stats/${userId}`);
}

/**
 * 获取组织按年份的服务统计（模拟数据）
 * @param organizationId 组织ID
 */
export function getOrganizationServiceStatsByYear(organizationId: number) {
  console.log('模拟请求组织按年份服务统计', { organizationId });
  
  // 简单返回模拟的统计数据
  return Promise.resolve(MOCK_SERVICE_STATS);
  
  // 注释掉原始API调用
  // return api.get(`/api/volunteer/service/organization/stats/${organizationId}`);
} 