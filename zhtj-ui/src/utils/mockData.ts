/**
 * 模拟数据服务
 * 提供所有模块的模拟数据，方便开发和测试
 */
import config from '../config';

// 模拟延迟函数
export function mockDelay<T>(data: T): Promise<T> {
  return new Promise(resolve => {
    setTimeout(() => {
      resolve(data);
    }, config.mock.delay);
  });
}

// 模拟标准API响应格式
export function mockResponse<T>(data: T, success = true, message = '请求成功') {
  return {
    success,
    data,
    message
  };
}

// 模拟分页数据
export function mockPageResponse<T>(list: T[], total: number, page = 1, size = 10) {
  return mockResponse({
    list,
    total,
    page,
    size,
    totalPages: Math.ceil(total / size)
  });
}

// 荣誉类型模拟数据
export const mockHonorTypes = [
  {
    id: 1,
    name: '优秀团员',
    level: '校级',
    score: 10,
    validPeriod: 12,
    createTime: '2023-01-15',
    applicationsCount: 25,
    status: 1
  },
  {
    id: 2,
    name: '优秀团干部',
    level: '校级',
    score: 15,
    validPeriod: 12,
    createTime: '2023-01-15',
    applicationsCount: 12,
    status: 1
  },
  {
    id: 3,
    name: '优秀团支部书记',
    level: '院级',
    score: 12,
    validPeriod: 12,
    createTime: '2023-02-20',
    applicationsCount: 8,
    status: 1
  },
  {
    id: 4,
    name: '社会实践先进个人',
    level: '院级',
    score: 8,
    validPeriod: 6,
    createTime: '2023-03-18',
    applicationsCount: 30,
    status: 1
  },
  {
    id: 5,
    name: '志愿服务先进个人',
    level: '社会级',
    score: 10,
    validPeriod: 12,
    createTime: '2023-04-05',
    applicationsCount: 15,
    status: 1
  }
];

// 荣誉申请模拟数据
export const mockHonorApplications = [
  {
    id: 1,
    honorName: '优秀共青团员',
    honorTypeId: 1,
    userName: '李红',
    organizationName: '计算机科学与技术系团支部',
    applicationYear: '2023',
    applicationTime: '2023-12-20 09:00:00',
    status: '已通过',
    branchApprovalStatus: '已通过',
    collegeApprovalStatus: '已通过'
  },
  {
    id: 2,
    honorName: '优秀共青团干部',
    honorTypeId: 2,
    userName: '张明',
    organizationName: '计算机科学与技术系团支部',
    applicationYear: '2023',
    applicationTime: '2023-12-15 10:30:00',
    status: '审批中',
    branchApprovalStatus: '已通过',
    collegeApprovalStatus: '待审批'
  },
  {
    id: 3,
    honorName: '先进团支部',
    honorTypeId: 3,
    userName: '计算机科学与技术系团支部',
    organizationName: '计算机学院团委',
    applicationYear: '2023',
    applicationTime: '2023-12-10 14:20:00',
    status: '申请中',
    branchApprovalStatus: '待审批',
    collegeApprovalStatus: '待审批'
  },
  {
    id: 4,
    honorName: '优秀共青团员',
    honorTypeId: 1,
    userName: '赵芳',
    organizationName: '计算机科学与技术系团支部',
    applicationYear: '2023',
    applicationTime: '2023-12-05 16:45:00',
    status: '已拒绝',
    branchApprovalStatus: '已拒绝',
    collegeApprovalStatus: '待审批'
  }
];

// 荣誉授予记录模拟数据
export const mockHonorAwards = [
  {
    id: 1,
    honorTypeName: '优秀共青团员',
    honorTypeId: 1,
    userName: '张明',
    organizationName: '计算机科学与技术系团支部',
    awardYear: '2023',
    awardTime: '2023-12-20 09:00:00',
    level: '校级',
    category: '先进个人',
    awardingDepartment: '校团委',
    awardingUserName: '李主任',
    score: 10
  },
  {
    id: 2,
    honorTypeName: '优秀共青团干部',
    honorTypeId: 2,
    userName: '李红',
    organizationName: '计算机科学与技术系团支部',
    awardYear: '2023',
    awardTime: '2023-12-15 10:30:00',
    level: '校级',
    category: '先进个人',
    awardingDepartment: '校团委',
    awardingUserName: '李主任',
    score: 15
  },
  {
    id: 3,
    honorTypeName: '先进团支部',
    honorTypeId: 3,
    userName: '计算机科学与技术系团支部',
    organizationName: '计算机学院团委',
    awardYear: '2023',
    awardTime: '2023-12-10 14:20:00',
    level: '校级',
    category: '先进集体',
    awardingDepartment: '校团委',
    awardingUserName: '王书记',
    score: 20
  },
  {
    id: 4,
    honorTypeName: '青年志愿服务奖',
    honorTypeId: 4,
    userName: '赵芳',
    organizationName: '软件工程系团支部',
    awardYear: '2022',
    awardTime: '2022-12-05 16:45:00',
    level: '院级',
    category: '特殊贡献',
    awardingDepartment: '计算机学院团委',
    awardingUserName: '张老师',
    score: 8
  }
];

// 用户模拟数据
export const mockUsers = [
  { id: 1001, name: '张明', role: 'student' },
  { id: 1002, name: '李红', role: 'student' },
  { id: 1003, name: '王刚', role: 'student' },
  { id: 1004, name: '赵芳', role: 'student' },
  { id: 1005, name: '刘宇', role: 'student' },
  { id: 2001, name: '李主任', role: 'teacher' },
  { id: 2002, name: '王书记', role: 'teacher' },
  { id: 2003, name: '张老师', role: 'teacher' },
  { id: 3001, name: '系统管理员', role: 'admin' }
];

// 组织模拟数据
export const mockOrganizations = [
  { id: 2001, name: '计算机科学与技术系团支部', parentId: 2004 },
  { id: 2002, name: '软件工程系团支部', parentId: 2004 },
  { id: 2003, name: '数据科学与大数据技术系团支部', parentId: 2004 },
  { id: 2004, name: '计算机学院团委', parentId: 0 }
];

// 模拟荣誉申请详情
export function mockApplicationDetail(id: number) {
  const app = mockHonorApplications.find(item => item.id === id);
  if (!app) return null;
  
  return {
    ...app,
    description: `这是ID为${id}的荣誉申请详情描述，包含了申请理由、申请人信息等内容。`,
    attachments: [
      { name: '成绩单.pdf', url: '#', size: '1.2MB' },
      { name: '志愿服务证明.docx', url: '#', size: '856KB' },
      { name: '团活动照片.zip', url: '#', size: '5.7MB' }
    ],
    approveLogs: [
      { 
        step: '申请提交', 
        time: app.applicationTime, 
        operator: app.userName, 
        status: 'success', 
        comments: '申请已提交，等待组织审批'
      },
      { 
        step: '组织审批', 
        time: app.branchApprovalStatus === '已通过' ? '2023-12-16 14:20:35' : '待处理', 
        operator: app.branchApprovalStatus === '已通过' ? '王主任' : '待处理', 
        status: app.branchApprovalStatus === '已通过' ? 'success' : 
               app.branchApprovalStatus === '已拒绝' ? 'error' : 'waiting', 
        comments: app.branchApprovalStatus === '已通过' ? '该同学表现优秀，符合条件，建议通过' : ''
      },
      { 
        step: '院级审批', 
        time: app.collegeApprovalStatus === '已通过' ? '2023-12-18 09:30:22' : '待处理', 
        operator: app.collegeApprovalStatus === '已通过' ? '张院长' : '待处理', 
        status: app.collegeApprovalStatus === '已通过' ? 'success' : 
               app.collegeApprovalStatus === '已拒绝' ? 'error' : 'waiting', 
        comments: app.collegeApprovalStatus === '已通过' ? '符合条件，同意授予荣誉' : ''
      }
    ],
    currentApprovalStep: app.status === '申请中' ? '组织审批' : 
                         app.status === '审批中' ? '院级审批' : '',
  };
}

// 模拟荣誉授予记录详情
export function mockAwardDetail(id: number) {
  const award = mockHonorAwards.find(item => item.id === id);
  if (!award) return null;
  
  return {
    ...award,
    description: `这是ID为${id}的荣誉授予记录详情，该荣誉于${award.awardTime}授予${award.userName}，以表彰其在学习和工作中的优秀表现。`,
    attachments: [
      { name: '荣誉证书.pdf', url: '#', size: '2.5MB' },
      { name: '颁奖照片.jpg', url: '#', size: '3.2MB' }
    ],
    applicationId: id // 假设申请ID与授予ID相同
  };
} 