<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Bell, Setting, User, InfoFilled, DocumentCopy, SwitchButton, QuestionFilled, Link } from '@element-plus/icons-vue';

// 引入用户状态管理
const userStore = useUserStore();
const router = useRouter();

// 确保头像正常显示，尝试从localStorage恢复
onMounted(() => {
  console.log('AppHeader - 组件挂载，当前头像状态:', userStore.avatar);
  if (!userStore.avatar) {
    console.log('AppHeader - 尝试恢复用户头像');
    userStore.initFromStorage && userStore.initFromStorage();
    
    // 如果仍然没有头像，强制刷新用户信息
    if (!userStore.avatar && userStore.isLoggedIn) {
      console.log('AppHeader - 本地存储中无头像，尝试重新获取用户信息');
      userStore.fetchUserInfo().catch((err: Error) => {
        console.error('AppHeader - 重新获取用户信息失败:', err);
      });
    }
  }
});

// 计算属性：用户头像 - 确保始终使用userStore.userAvatar计算属性
const userAvatar = computed(() => {
  console.log('AppHeader - 使用头像:', userStore.userAvatar);
  return userStore.userAvatar;
});

// 获取未完成任务数量
const uncompletedTasks = computed(() => userStore.uncompletedTasksCount || 0);

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    try {
      console.log('用户确认退出，开始调用登出方法');
      await userStore.doLogout();
      
      // 无论如何，都跳转到登录页
      console.log('退出完成，准备跳转到登录页');
      router.push('/login');
    } catch (error) {
      console.error('退出登录过程中出错:', error);
      
      // 即使出错，我们仍然清除本地状态并跳转
      userStore.clearUserInfo();
      router.push('/login');
    }
  } catch {
    // 用户取消操作
    console.log('用户取消了退出操作');
  }
};

// 前往个人资料页面
const goToProfile = () => {
  router.push('/dashboard/profile');
};

// 前往通知页面
const goToNotifications = () => {
  router.push('/dashboard/notifications');
};

// 前往帮助中心
const goToHelp = () => {
  router.push('/dashboard/help');
};

// 前往常见问题页面
const goToFaq = () => {
  router.push('/dashboard/faq');
};

// 添加前往系统设置页面的方法
const goToSettings = () => {
  router.push('/dashboard/settings');
};

// 打开中国共青团网
const openGqtWebsite = () => {
  window.open('https://www.gqt.org.cn/', '_blank');
};
</script>

<template>
  <el-header class="main-header">
    <div class="header-logo">
      <img src="../../assets/img/logo.png" alt="Logo" class="logo-img" />
      <h1 class="system-title">网上共青团 · 智慧团建</h1>
    </div>
    <div class="header-user-info">
      <!-- 通知下拉菜单 -->
      <el-dropdown trigger="click" class="header-dropdown">
        <span class="dropdown-trigger notification-trigger">
          <el-badge :value="uncompletedTasks" :hidden="uncompletedTasks === 0" :max="99">
            <el-icon><Bell /></el-icon>
          </el-badge>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToNotifications">
              <el-icon><Bell /></el-icon>
              <span>查看通知</span>
            </el-dropdown-item>
            <el-dropdown-item v-if="uncompletedTasks > 0">
              <el-icon><DocumentCopy /></el-icon>
              <span>您有 {{ uncompletedTasks }} 条未读消息</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      
      <!-- 设置下拉菜单 -->
      <el-dropdown trigger="click" class="header-dropdown">
        <span class="dropdown-trigger">
          <el-icon><Setting /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToSettings">
              <el-icon><User /></el-icon>
              <span>系统设置</span>
            </el-dropdown-item>
            <el-dropdown-item @click="openGqtWebsite">
              <el-icon><Link /></el-icon>
              <span>共青团</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      
      <!-- 帮助中心下拉菜单 -->
      <el-dropdown trigger="click" class="header-dropdown">
        <span class="dropdown-trigger help-trigger">
          <span>帮助中心</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToHelp">
              <el-icon><QuestionFilled /></el-icon>
              <span>使用手册</span>
            </el-dropdown-item>
            <el-dropdown-item @click="goToFaq">
              <el-icon><InfoFilled /></el-icon>
              <span>常见问题</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      
      <!-- 用户头像和下拉菜单 -->
      <el-dropdown trigger="click" class="header-dropdown user-dropdown-menu">
        <span class="dropdown-trigger avatar-trigger">
          <el-avatar :size="32" :src="userAvatar" class="user-avatar"></el-avatar>
          <span class="user-name">{{ userStore.name }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goToProfile">
              <el-icon><User /></el-icon>
              <span>个人资料</span>
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<style scoped>
.main-header {
  background-color: #c62828;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 10;
}

.header-logo {
  display: flex;
  align-items: center;
}

.logo-img {
  height: 40px;
  margin-right: 10px;
}

.system-title {
  font-size: 20px;
  margin: 0;
  font-weight: 500;
  letter-spacing: 1px;
}

.header-user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-dropdown {
  display: flex;
  align-items: center;
}

.dropdown-trigger {
  cursor: pointer;
  color: white;
  font-size: 20px;
  height: 32px;
  width: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  border-radius: 4px;
}

.dropdown-trigger:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.help-trigger {
  font-size: 14px;
  width: auto;
  padding: 0 8px;
}

.avatar-trigger {
  width: auto;
  display: flex;
  gap: 8px;
  padding: 0 8px;
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.7);
  overflow: hidden;
}

:deep(.user-avatar img) {
  transform: scale(1.3);/* 将头像放大到130% */
  transform-origin: center center;
  transition: transform 0.3s;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

.notification-trigger {
  font-size: 20px;
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
}

:deep(.el-dropdown-menu__item i) {
  margin-right: 0;
}
</style> 