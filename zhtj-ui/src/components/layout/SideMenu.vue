<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { 
  HomeFilled, Reading, Star, Trophy, Bell, 
  UserFilled, Notebook, SetUp, Link, Comment,
  Tickets, Help, CollectionTag, School, Menu, ArrowLeft, ArrowRight,
  Checked, Operation
} from '@element-plus/icons-vue';

// 定义props
const props = defineProps({
  // 当前激活的菜单项
  activeIndex: {
    type: String,
    default: 'profile'
  },
  // 是否折叠菜单
  isCollapse: {
    type: Boolean,
    default: false
  }
});

// 发出事件
const emit = defineEmits(['select', 'collapse-change']);

const router = useRouter();
const userStore = useUserStore();

// 确保头像正常显示，尝试从localStorage恢复
onMounted(() => {
  console.log('SideMenu - 组件挂载，当前头像状态:', userStore.avatar);
  if (!userStore.avatar) {
    console.log('SideMenu - 尝试恢复用户头像');
    userStore.initFromStorage && userStore.initFromStorage();
    
    // 如果仍然没有头像，强制刷新用户信息
    if (!userStore.avatar && userStore.isLoggedIn) {
      console.log('SideMenu - 本地存储中无头像，尝试重新获取用户信息');
      userStore.fetchUserInfo().catch((err: Error) => {
        console.error('SideMenu - 重新获取用户信息失败:', err);
      });
    }
  }
});

// 计算属性：用户头像 - 直接使用store中的计算属性
const userAvatar = computed(() => {
  console.log('SideMenu - 使用头像:', userStore.userAvatar);
  return userStore.userAvatar;
});

// 菜单项点击处理
const handleSelect = (key: string) => {
  emit('select', key);
  
  // 特殊处理团员团组管理菜单项
  if (key === 'member-register/approve') {
    // 使用router.push并添加查询参数
    router.push({
      path: '/dashboard/member-register/approve',
      query: { status: 'activated' }
    });
  }
};

// 切换菜单折叠状态
const toggleCollapse = () => {
  emit('collapse-change', !props.isCollapse);
};

// 基于用户角色计算菜单项
const menuItems = computed(() => {
  const items = [
    {
      index: 'profile',
      title: '个人首页',
      icon: HomeFilled,
      visible: true
    },
    {
      index: 'study',
      title: '学习资源',
      icon: Reading,
      visible: true,
      children: [
        { index: 'course-resources', title: '团课资源' },
        { index: 'league-wiki', title: '团务百科' }
      ]
    },
    {
      index: 'volunteer',
      title: '志愿服务',
      icon: Star,
      visible: true,
      children: [
        { index: 'volunteer-services', title: '志愿服务记录' },
        { index: 'volunteer-service/add', title: '添加服务记录' }
      ]
    },
    {
      index: 'honor',
      title: '荣誉管理',
      icon: Trophy,
      visible: true,
      children: [
        { index: 'honor-apply', title: '申请荣誉' },
        { index: 'my-honors', title: '我的荣誉' },
        { index: 'honor-approval', title: '荣誉审批', visible: userStore.isCommitteeSecretary || userStore.isCommitteeDeputySecretary }
      ]
    },
    {
      index: 'notifications',
      title: '消息通知',
      icon: Bell,
      visible: true
    },
    {
      index: 'activities',
      title: '活动管理',
      icon: Tickets,
      visible: true
    },
    {
      index: 'member-register/approve',
      title: '团员团组管理',
      icon: Notebook,
      visible: userStore.isCommitteeSecretary
    },
    {
      index: 'transfers',
      title: '组织关系转接',
      icon: Link,
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary
    },
    // 两制菜单
    {
      index: 'twosystem',
      title: '两制管理',
      icon: Operation,
      visible: true,
      children: [
        { index: 'evaluations', title: '团员教育评议' },
        { index: 'register', title: '年度团籍注册' }
      ]
    },
    {
      index: 'help',
      title: '帮助中心',
      icon: Help,
      visible: true
    }
  ];
  
  // 根据角色过滤菜单项
  return items.filter(item => item.visible);
});
</script>

<template>
  <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
    <div class="sidebar-user">
      <el-avatar :size="64" :src="userAvatar" class="user-avatar"></el-avatar>
      <div class="user-info" v-if="!isCollapse">
        <div class="user-name">{{ userStore.name }}</div>
        <div class="user-role">{{ userStore.leaguePosition }}</div>
      </div>
    </div>
    
    <el-menu
      :default-active="activeIndex"
      class="sidebar-menu"
      :collapse="isCollapse"
      @select="handleSelect"
      router
      background-color="#c62828"
      text-color="#f5f5f5"
      active-text-color="#ffd04b"
    >
      <template v-for="(item, index) in menuItems" :key="index">
        <!-- 带子菜单的项目 -->
        <el-sub-menu v-if="item.children && item.children.length > 0" :index="item.index">
          <template #title>
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </template>
          
          <el-menu-item 
            v-for="(child, childIndex) in item.children" 
            :key="childIndex"
            :index="child.index"
          >
            <span class="menu-item-indent"></span>
            {{ child.title }}
          </el-menu-item>
        </el-sub-menu>
        
        <!-- 无子菜单的项目 -->
        <el-menu-item v-else :index="item.index">
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>{{ item.title }}</template>
        </el-menu-item>
      </template>
    </el-menu>
    
    <div class="sidebar-footer">
      <el-button 
        type="primary" 
        circle
        size="small"
        class="collapse-btn"
        @click="toggleCollapse"
      >
        <el-icon>
          <component :is="isCollapse ? ArrowRight : ArrowLeft" />
        </el-icon>
      </el-button>
    </div>
  </el-aside>
</template>

<style scoped>
.sidebar {
  background-color: #c62828;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  height: 100%;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.sidebar-user {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  background-color: rgba(0, 0, 0, 0.15);
  margin-bottom: 10px;
}

.user-avatar {
  margin-bottom: 10px;
  border: 2px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

:deep(.user-avatar img) {
  transform: scale(1.3);/* 将头像放大到130% */
  transform-origin: center center;
  transition: transform 0.3s;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.user-name {
  color: white;
  font-size: 16px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 4px;
}

.user-role {
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

.sidebar-menu :deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
}

.menu-item-indent {
  display: inline-block;
  width: 12px;
}

.sidebar-footer {
  padding: 16px 0;
  display: flex;
  justify-content: center;
  background-color: rgba(0, 0, 0, 0.15);
}

.collapse-btn {
  background-color: rgba(255, 255, 255, 0.9);
  color: #c62828;
  border: none;
}

.collapse-btn:hover {
  background-color: white;
  color: #c62828;
  transform: scale(1.1);
}
</style> 