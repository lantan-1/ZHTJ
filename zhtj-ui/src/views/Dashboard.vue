<script setup lang="ts">
import { ref, onMounted, watch, onBeforeMount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '../stores/user';
import { ElMessageBox, ElMessage } from 'element-plus';
import { Location } from '@element-plus/icons-vue';

// 导入共用布局组件
import AppHeader from '../components/layout/AppHeader.vue';
import SideMenu from '../components/layout/SideMenu.vue';
import AppFooter from '../components/layout/AppFooter.vue';

// 导入角色特定组件
import MemberInfoCard from '../components/dashboard/MemberInfoCard.vue';
import BranchInfoCard from '../components/dashboard/BranchInfoCard.vue';
import CommitteeInfoCard from '../components/dashboard/CommitteeInfoCard.vue';
import FunctionCard from '../components/dashboard/FunctionCard.vue';
import NotificationPanel from '../components/dashboard/NotificationPanel.vue';
import ActivityList from '../components/dashboard/ActivityList.vue';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const activeIndex = ref('profile');
const isCollapse = ref(false);

// 处理菜单项选择
const handleSelect = (key: string) => {
  activeIndex.value = key;
  router.push(`/dashboard/${key}`);
};

// 处理菜单折叠状态变化
const handleCollapseChange = (collapsed: boolean) => {
  isCollapse.value = collapsed;
};

// 从路由路径中提取激活的菜单项
const getActiveIndexFromPath = (path: string) => {
  // 移除"/dashboard/"前缀
  const segments = path.replace('/dashboard/', '').split('/');
  return segments[0] || 'profile';
};

// 监听路由变化，自动更新activeIndex
watch(
  () => route.path,
  (newPath) => {
    if (newPath.startsWith('/dashboard/')) {
      const routeKey = getActiveIndexFromPath(newPath);
      console.log('路由变化，更新activeIndex:', routeKey);
      activeIndex.value = routeKey;
      
      // 添加调试信息
      if (routeKey === 'transfers') {
        console.log('当前路由:', newPath);
        console.log('Token状态:', !!localStorage.getItem('token'));
        console.log('Store登录状态:', userStore.isLoggedIn);
      }
    }
  }
);

// 组件挂载前检查token状态
onBeforeMount(() => {
  console.log('Dashboard组件挂载前，检查令牌状态');
  
  // 从localStorage获取token
  const token = localStorage.getItem('token');
  console.log('当前令牌状态:', !!token);
  
  // 如果没有token但用户已登录状态，重置用户状态
  if (!token && userStore.isLoggedIn) {
    console.warn('令牌丢失但用户状态为已登录，重置状态');
    userStore.clearUserInfo();
    router.push('/login');
    return;
  }
  
  // 验证token是否过期
  if (token) {
    const expiryTime = localStorage.getItem('token_expiry');
    if (expiryTime) {
      const now = new Date().getTime();
      const expiry = parseInt(expiryTime);
      
      if (now > expiry) {
        console.warn('令牌已过期，需要重新登录');
        userStore.clearUserInfo();
        router.push('/login');
        return;
      }
    }
  }
});

// 组件挂载时获取用户信息和设置激活菜单
onMounted(async () => {
  console.log('Dashboard组件挂载，初始化用户状态');
  
  // 从localStorage重新初始化用户状态
  userStore.initFromStorage();
  
  // 如果未登录但有token，尝试获取用户信息
  if (!userStore.isLoggedIn && localStorage.getItem('token')) {
    console.log('发现令牌但用户未登录，尝试获取用户信息');
    try {
      await userStore.fetchUserInfo();
    } catch (error) {
      console.error('获取用户信息失败，可能需要重新登录', error);
      ElMessage.error('登录状态失效，请重新登录');
      router.push('/login');
      return;
    }
  } else if (!userStore.isLoggedIn) {
    // 如果既没有登录状态也没有token，直接跳转登录
    console.warn('未登录且无令牌，重定向到登录页');
    router.push('/login');
    return;
  }
  
  // 如果已登录但用户信息不完整，重新获取
  if (userStore.isLoggedIn && (!userStore.name || !userStore.organizationId)) {
    console.log('用户已登录但信息不完整，重新获取用户信息');
    await userStore.fetchUserInfo();
  }
  
  // 根据当前路径设置activeIndex
  const currentPath = router.currentRoute.value.path;
  if (currentPath === '/dashboard') {
    router.push('/dashboard/profile');
  } else if (currentPath.startsWith('/dashboard/')) {
    activeIndex.value = getActiveIndexFromPath(currentPath);
    console.log('初始化activeIndex:', activeIndex.value);
  }
});
</script>

<template>
  <el-container class="dashboard-container">
    <!-- 顶部导航栏 -->
    <AppHeader />
    
    <el-container class="main-container">
      <!-- 左侧菜单 -->
      <SideMenu 
        :active-index="activeIndex"
        :is-collapse="isCollapse"
        @select="handleSelect"
        @collapse-change="handleCollapseChange"
      />
      
      <el-container class="content-container">
        <el-main class="main">
          <!-- 位置信息 -->
          <div class="location-bar">
            <el-icon><Location /></el-icon>
            当前位置: 
            <template v-if="route.path === '/dashboard/profile'">个人首页</template>
            <template v-else-if="route.path.includes('/transfers')">团员关系转接</template>
            <template v-else-if="route.path.includes('/activities')">活动管理</template>
            <template v-else-if="route.path.includes('/members')">成员管理</template>
            <template v-else-if="route.path.includes('/help')">帮助中心</template>
            <template v-else-if="route.path.includes('/contact')">联系我们</template>
            <template v-else-if="route.path.includes('/faq')">常见问题</template>
            <template v-else-if="route.path.includes('/volunteer')">志愿服务</template>
            <template v-else-if="route.path.includes('/honor-apply')">申请荣誉</template>
            <template v-else-if="route.path.includes('/my-honors')">我的荣誉</template>
            <template v-else-if="route.path.includes('/honor-approval')">荣誉审批</template>
            <template v-else-if="route.path.includes('/course-resources')">团课资源</template>
            <template v-else-if="route.path.includes('/league-wiki')">团务百科</template>
            <template v-else-if="route.path.includes('/notifications')">消息通知</template>
            <template v-else-if="route.path.includes('/evaluations')">团员教育评议</template>
            <template v-else-if="route.path.includes('/register')">年度团籍注册</template>
            <template v-else-if="route.path.includes('/notification')">消息通知详情</template>
            <template v-else-if="route.path.includes('/settings')">系统设置</template>
            <template v-else-if="route.path.includes('/member-register')">团员/团组织管理</template>
            <template v-else>{{ activeIndex }}</template>
          </div>
          
          <!-- 个人首页内容 -->
          <div v-if="route.path === '/dashboard/profile'" class="dashboard-content">
            <el-row :gutter="20" class="info-row">
              <!-- 个人信息卡片 (根据角色渲染不同卡片) -->
              <el-col :md="24" :lg="16">
                <MemberInfoCard v-if="userStore.isMember" />
                <BranchInfoCard v-else-if="userStore.isBranchSecretary" />
                <CommitteeInfoCard v-else-if="userStore.isCommitteeSecretary" />
              </el-col>
              
              <!-- 工作通知 -->
              <el-col :md="24" :lg="8">
                <NotificationPanel />
              </el-col>
            </el-row>
            
            <!-- 功能卡片 -->
            <FunctionCard />
            
            <!-- 活动列表 -->
            <el-row :gutter="20" class="activity-row">
              <el-col :span="24">
                <ActivityList />
              </el-col>
            </el-row>
          </div>
          
          <!-- 默认路由视图 -->
          <router-view v-else />
        </el-main>
        
        <!-- 底部咨询信息 -->
        <AppFooter />
      </el-container>
    </el-container>
  </el-container>
</template>

<style scoped>
.dashboard-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  flex: 1;
  display: flex;
}

.content-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.location-bar {
  display: flex;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
  gap: 5px;
}

.dashboard-content {
  margin-top: 15px;
}

.info-row {
  margin-bottom: 20px;
  min-height: 300px;
}

.activity-row {
  margin-top: 5px;
}

.main {
  background-color: #f5f7fa;
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}
</style> 