import { createRouter, createWebHistory } from 'vue-router';
import { useUserStore } from '../stores/user';
import { ElMessage } from 'element-plus';

// 定义路由类型
type AppRouteRecordRaw = {
  path: string;
  name?: string;
  component?: any;
  redirect?: string;
  children?: AppRouteRecordRaw[];
  meta?: {
    title?: string;
    requiresAuth?: boolean;
    requiresAdmin?: boolean;
    roles?: string[];
  };
};

const routes: AppRouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPassword.vue'),
    meta: {
      title: '忘记密码',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: {
      title: '首页',
      requiresAuth: true
    },
    children: [
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: {
          title: '个人信息',
          requiresAuth: true
        }
      },
      {
        path: 'settings',
        name: 'Settings',
        component: () => import('../views/Settings.vue'),
        meta: {
          title: '系统设置',
          requiresAuth: true
        }
      },
      {
        path: 'activities',
        name: 'Activities',
        component: () => import('../views/Activities.vue'),
        meta: {
          title: '活动管理',
          requiresAuth: true
        }
      },
      {
        path: 'activities/:id',
        name: 'ActivityDetail',
        component: () => import('../views/ActivityDetail.vue'),
        meta: {
          title: '活动详情',
          requiresAuth: true
        }
      },
      {
        path: 'meetings',
        redirect: '/dashboard/activities',
        meta: {
          requiresAuth: true
        }
      },
      {
        path: 'branch-activities',
        name: 'BranchActivities',
        component: () => import('../views/Activities.vue'),
        meta: {
          title: '下级组织活动',
          requiresAuth: true
        }
      },
      {
        path: 'branch-meetings',
        redirect: '/dashboard/branch-activities',
        meta: {
          requiresAuth: true
        }
      },

      // 帮助中心模块
      {
        path: 'help',
        name: 'Help',
        component: () => import('../views/Help.vue'),
        meta: {
          title: '帮助中心',
          requiresAuth: true
        }
      },
      // 全局搜索模块
      {
        path: 'search',
        name: 'GlobalSearch',
        component: () => import('../views/GlobalSearch.vue'),
        meta: {
          title: '全局搜索',
          requiresAuth: true
        }
      },
      // 学习资源模块
      {
        path: 'course-resources',
        name: 'CourseResources',
        component: () => import('../views/study/CourseResources.vue'),
        meta: {
          title: '团课资源',
          requiresAuth: true
        }
      },
      {
        path: 'league-wiki',
        name: 'LeagueWiki',
        component: () => import('../views/study/LeagueWiki.vue'),
        meta: {
          title: '团务百科',
          requiresAuth: true
        }
      },
      {
        path: 'help/manual',
        name: 'Manual',
        component: () => import('../views/Manual.vue'),
        meta: {
          title: '使用手册',
          requiresAuth: true
        }
      },
      {
        path: 'contact',
        name: 'Contact',
        component: () => import('../views/Contact.vue'),
        meta: {
          title: '联系我们',
          requiresAuth: true
        }
      },
      {
        path: 'faq',
        name: 'FAQ',
        component: () => import('../views/FAQ.vue'),
        meta: {
          title: '常见问题',
          requiresAuth: true
        }
      },
      // 两制相关路由
      {
        path: 'evaluations',
        name: 'Evaluations',
        component: () => import('../views/twosystem/Evaluations.vue'),
        meta: {
          title: '团员教育评议',
          requiresAuth: true
        }
      },
      {
        path: 'evaluations/create',
        name: 'CreateEvaluation',
        component: () => import('../views/twosystem/CreateEvaluation.vue'),
        meta: {
          title: '创建评议活动',
          requiresAuth: true
        }
      },
      {
        path: 'evaluations/:id',
        name: 'EvaluationDetail',
        component: () => import('../views/twosystem/EvaluationDetail.vue'),
        meta: {
          title: '评议活动详情',
          requiresAuth: true
        }
      },
      {
        path: 'evaluations/statistics',
        name: 'EvaluationStatistics',
        component: () => import('../views/twosystem/EvaluationStatistics.vue'),
        meta: {
          title: '评议统计分析',
          requiresAuth: true
        }
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('../views/twosystem/Register.vue'),
        meta: {
          title: '年度团籍注册',
          requiresAuth: true
        }
      },
      {
        path: 'register/detail/:id',
        name: 'RegisterDetail',
        component: () => import('../views/twosystem/RegisterDetail.vue'),
        meta: {
          title: '注册详情',
          requiresAuth: true
        }
      },
      {
        path: 'register/batch',
        name: 'RegisterBatch',
        component: () => import('../views/twosystem/RegisterBatch.vue'),
        meta: {
          title: '注册批次管理',
          requiresAuth: true
        }
      },
      {
        path: 'register/batch/:id',
        name: 'RegisterBatchDetail',
        component: () => import('../views/twosystem/RegisterBatchDetail.vue'),
        meta: {
          title: '批次详情',
          requiresAuth: true
        }
      },
      {
        path: 'member-register/approve',
        name: 'RegisterApprove',
        component: () => import('../views/member/RegisterApprove.vue'),
        meta: {
          title: '用户注册审批',
          requiresAuth: true,
          roles: ['admin', 'committee_secretary', 'deputy_committee_secretary']
        }
      },
      {
        path: 'register/statistics',
        name: 'RegisterStatistics',
        component: () => import('../views/twosystem/RegisterStatistics.vue'),
        meta: {
          title: '注册统计',
          requiresAuth: true
        }
      },
      // 志愿服务模块
      {
        path: 'volunteer-services',
        name: 'VolunteerServices',
        component: () => import('../views/volunteer/VolunteerServices.vue'),
        meta: {
          title: '志愿服务记录',
          requiresAuth: true
        }
      },
      {
        path: 'volunteer-service/add',
        name: 'AddVolunteerService',
        component: () => import('../views/volunteer/AddVolunteerService.vue'),
        meta: {
          title: '添加志愿服务记录',
          requiresAuth: true
        }
      },
      {
        path: 'volunteer-service/verify',
        name: 'VerifyVolunteerService',
        component: () => import('../views/volunteer/VerifyVolunteerService.vue'),
        meta: {
          title: '审核志愿服务',
          requiresAuth: true
        }
      },
      {
        path: 'volunteer-statistics',
        name: 'VolunteerStatistics',
        component: () => import('../views/volunteer/VolunteerStatistics.vue'),
        meta: {
          title: '志愿服务统计',
          requiresAuth: true
        }
      },
      // 荣誉模块
      {
        path: 'honor-apply',
        name: 'HonorApply',
        component: () => import('../views/honor/HonorApply.vue'),
        meta: {
          title: '申请荣誉',
          requiresAuth: true
        }
      },
      {
        path: 'my-honors',
        name: 'MyHonors',
        component: () => import('../views/honor/MyHonors.vue'),
        meta: {
          title: '我的荣誉',
          requiresAuth: true
        }
      },
      {
        path: 'honor-approval',
        name: 'HonorApproval',
        component: () => import('../views/honor/HonorApproval.vue'),
        meta: {
          title: '荣誉审批',
          requiresAuth: true,
          roles: ['committee_secretary', 'deputy_committee_secretary']
        }
      },
      // 通知模块
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('../views/notification/Notifications.vue'),
        meta: {
          title: '消息通知',
          requiresAuth: true
        }
      },
      {
        path: 'notification/detail/:id',
        name: 'NotificationDetail',
        component: () => import('../views/notification/NotificationDetail.vue'),
        meta: {
          title: '通知详情',
          requiresAuth: true
        }
      },
      // 团员关系转接模块
      {
        path: 'transfers',
        name: 'Transfers',
        component: () => import('../views/transfer/Transfers.vue'),
        meta: {
          title: '关系转接申请',
          requiresAuth: true
        }
      },
      {
        path: 'transfers/create',
        name: 'CreateTransfer',
        component: () => import('../views/transfer/CreateTransfer.vue'),
        meta: {
          title: '发起关系转接',
          requiresAuth: true
        }
      },
      {
        path: 'transfers/detail/:id',
        name: 'TransferDetail',
        component: () => import('../views/transfer/TransferDetail.vue'),
        meta: {
          title: '转接申请详情',
          requiresAuth: true
        }
      },
      {
        path: 'transfers/logs/:id',
        name: 'TransferApprovalLog',
        component: () => import('../views/transfer/TransferApprovalLog.vue'),
        meta: {
          title: '转接申请审批日志',
          requiresAuth: true
        }
      },
      {
        path: 'transfers/approve',
        name: 'TransferApprove',
        component: () => import('../views/transfer/TransferApprove.vue'),
        meta: {
          title: '转接申请审批',
          requiresAuth: true
        }
      }
    ]
  },
  // 管理控制台模块
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/admin/AdminDashboard.vue'),
    meta: {
      title: '管理控制台',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/admin/users',
    name: 'UserManagement',
    component: () => import('../views/admin/UserManagement.vue'),
    meta: {
      title: '用户管理',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/admin/roles',
    name: 'RoleManagement',
    component: () => import('../views/admin/RoleManagement.vue'),
    meta: {
      title: '角色管理',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/admin/organizations',
    name: 'OrganizationManagement',
    component: () => import('../views/admin/OrganizationManagement.vue'),
    meta: {
      title: '组织管理',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/admin/organization-structure',
    name: 'OrganizationStructure',
    component: () => import('../views/admin/OrganizationStructure.vue'),
    meta: {
      title: '组织结构维护',
      requiresAuth: true,
      requiresAdmin: true
    }
  },

  {
    path: '/admin/honors',
    name: 'HonorManagement',
    component: () => import('../views/admin/HonorManagement.vue'),
    meta: {
      title: '荣誉管理',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/admin/notifications',
    name: 'NotificationManagement',
    component: () => import('../views/admin/NotificationManagement.vue'),
    meta: {
      title: '通知管理',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/admin/notifications/create',
    name: 'CreateNotification',
    component: () => import('../views/admin/CreateNotification.vue'),
    meta: {
      title: '创建系统通知',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/admin/monitor',
    name: 'SystemMonitor',
    component: () => import('../views/admin/SystemMonitor.vue'),
    meta: {
      title: '系统监控',
      requiresAuth: true,
      requiresAdmin: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: {
      title: '404',
      requiresAuth: false
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局前置守卫
router.beforeEach(async (to: any, from: any, next: any) => {
  // 设置页面标题
  document.title = `智慧团建系统 - ${to.meta?.title || ''}`;
  
  const userStore = useUserStore();
  
  // 从localStorage获取token，确保使用最新的登录状态
  const token = localStorage.getItem('token');
  const hasToken = !!token;
  
  // 简化日志输出
  console.log('路由守卫 - 当前路径:', to.path);
  console.log('路由守卫 - Token状态:', hasToken);
  console.log('路由守卫 - Store登录状态:', userStore.isLoggedIn);
  
  // 防止循环重定向标记
  const isRedirecting = sessionStorage.getItem('isRedirecting');
  if (isRedirecting === 'true') {
    console.log('检测到可能的循环重定向，强制继续当前导航');
    sessionStorage.removeItem('isRedirecting');
    next();
    return;
  }
  
  // 同步登录状态 - 简化逻辑
  if (hasToken && !userStore.isLoggedIn) {
    console.log('发现localStorage有token但Store未更新，正在同步状态...');
    userStore.setToken(token);
    if (userStore.initFromStorage) {
      userStore.initFromStorage();
    }
  } else if (!hasToken && userStore.isLoggedIn) {
    console.log('警告：localStorage中没有token但Store显示已登录，修正状态');
    userStore.clearUserInfo();
  }
  
  // 判断该路由是否需要管理员权限
  if (to.meta.requiresAdmin) {
    if (hasToken) {
      // 检查是否有管理员权限
      const isAdmin = userStore.userInfo?.roles?.includes('admin') || userStore.userInfo?.username === 'admin';
      
      if (isAdmin) {
        next();
      } else {
        ElMessage.error('您没有管理员权限');
        next({ name: 'Dashboard' });
      }
    } else {
      sessionStorage.setItem('isRedirecting', 'true');
      next({ 
        name: 'Login', 
        query: { redirect: to.fullPath } 
      });
    }
  }
  // 判断该路由是否需要登录权限
  else if (to.meta.requiresAuth) {
    // 重新检查token状态 - 可能在前面的步骤中已更新
    const currentToken = localStorage.getItem('token');
    const isLoggedIn = !!currentToken || userStore.isLoggedIn;
    
    if (isLoggedIn) {
      // 已登录，允许访问
      console.log('用户已登录，允许访问:', to.path);
        
      // 检查用户是否已激活
      if (userStore.userInfo && userStore.userInfo.isActivated === false) {
        console.log('用户未激活，重定向到登录页');
        ElMessage.warning('您的账号尚未激活，请联系组织管理员进行激活');
        userStore.logout();
        sessionStorage.setItem('isRedirecting', 'true');
        next({ 
          name: 'Login', 
          query: { needActivation: 'true' } 
        });
        return;
      }
        
      // 必要时异步加载用户信息，但不阻塞导航
      if (!userStore.name || !userStore.organization) {
        console.log('用户信息不完整，异步加载用户信息');
        userStore.fetchUserInfo().catch((error: Error) => {
          console.error('获取用户信息失败，但仍然允许访问', error);
        });
        }
      
      next();
    } else {
      // 未登录，重定向到登录页
      console.log('用户未登录，重定向到登录页');
      sessionStorage.setItem('isRedirecting', 'true');
      next({ 
        name: 'Login', 
        query: { redirect: to.fullPath } 
      });
    }
  } else {
    // 不需要登录权限的页面
    // 如果已登录且要前往登录页，则重定向到首页
    const isLoggedIn = hasToken || userStore.isLoggedIn;
    
    if (isLoggedIn && to.path === '/login') {
      console.log('用户已登录，尝试访问登录页，重定向到首页');
      sessionStorage.setItem('isRedirecting', 'true');
      next({ name: 'Dashboard' });
    } else {
      next();
    }
  }
});

export default router; 