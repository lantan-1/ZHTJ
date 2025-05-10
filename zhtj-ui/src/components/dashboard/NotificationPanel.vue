<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { getWorkNotifications } from '../../api/dashboard';

const router = useRouter();
const userStore = useUserStore();

// 通知项接口
interface Notification {
  id: number;
  title: string;
  content?: string;
  link?: string;
  create_time?: string;
}

// 直接从store获取用户信息
const userName = computed(() => userStore.name || '');
const orgName = computed(() => userStore.organization || '');
const position = computed(() => userStore.leaguePosition || '');

// 通知列表
const notifications = ref<Notification[]>([]);
const loading = ref(false);

// 获取通知列表
const fetchNotifications = async () => {
  loading.value = true;
  try {
    const response = await getWorkNotifications({
      page: 1,
      size: 5
    });
    
    if (response && response.data && response.data.list && response.data.list.length > 0) {
      notifications.value = response.data.list;
      console.log('成功获取API通知数据:', notifications.value);
    } else {
      // API没有返回数据，使用默认数据
      console.log('API返回空数据，使用默认通知');
      useDefaultNotifications();
    }
  } catch (error) {
    console.error('获取通知列表失败', error);
    // 使用默认数据
    useDefaultNotifications();
  } finally {
    loading.value = false;
  }
};

// 使用默认通知数据
const useDefaultNotifications = () => {
  notifications.value = [
    {
      id: 1,
      title: '智慧团建系统项目立项情况说明',
      content: '智慧团建系统已正式立项，项目旨在构建一个现代化、智能化的团组织管理与服务平台。系统将包含团员管理、组织管理、学习资源、活动管理等核心功能模块，打造一站式团建服务解决方案。项目计划在2025年底前全面上线，欢迎各级团组织共同参与建设和试用。',
      create_time: '2025-03-01 09:30:00'
    },
    {
      id: 2,
      title: '智慧团建系统开发进度通报',
      content: '智慧团建系统已完成主体功能开发，当前版本v1.0.0正处于内测阶段。目前已完成用户管理、权限系统、组织管理、学习资源管理等核心功能模块。正在进行的是消息通知、统计报表和移动端适配等功能的开发。感谢各位团干部的宝贵意见和建议，我们将持续优化系统功能和用户体验。',
      create_time: '2025-05-04 14:15:00'
    },
    {
      id: 3,
      title: `欢迎${orgName.value}${position.value}${userName.value}`,
      content: `尊敬的${orgName.value}${position.value}${userName.value}，欢迎您登录智慧团建系统！系统将为您提供便捷的团组织管理和团员服务功能。您可以通过左侧导航栏访问各项功能模块，如有任何问题，请随时联系系统管理员或查看帮助文档。祝您使用愉快！`,
      create_time: new Date().toLocaleString('zh-CN', {hour12: false}).replace(/\//g, '-')
    }
  ];
  console.log('默认通知已设置:', notifications.value);
};

// 查看通知详情
const viewNotification = (notification: Notification) => {
  if (notification.link) {
    router.push(notification.link);
  } else {
    router.push(`/dashboard/notification/detail/${notification.id}`);
  }
};

// 组件加载时获取通知列表
onMounted(() => {
  console.log('NotificationPanel组件已加载，获取通知中...');
  console.log('当前用户信息:', {
    name: userName.value,
    organization: orgName.value,
    position: position.value
  });
  fetchNotifications();
});
</script>

<template>
  <el-card shadow="hover" class="notification-card">
    <template #header>
      <div class="card-header">
        <h3>工作通知</h3>
        <el-link type="danger" @click="router.push('/dashboard/notifications')" class="view-all">查看全部</el-link>
      </div>
    </template>
    
    <div v-loading="loading" class="notification-list">
      <el-empty 
        v-if="notifications.length === 0" 
        description="暂无通知" 
        :image-size="55"
        class="custom-empty"
      ></el-empty>
      
      <div 
        v-for="notification in notifications" 
        :key="notification.id" 
        class="notification-item"
        @click="viewNotification(notification)"
      >
        <div class="notification-bullet"></div>
        <div class="notification-title">{{ notification.title }}</div>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.notification-card {
  height: 100%;
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #c62828;
}

.view-all {
  font-size: 13px;
}

.notification-list {
  min-height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 8px 0;
  border-bottom: 1px dashed #eee;
  cursor: pointer;
  transition: all 0.2s;
}

.notification-item:hover {
  background-color: #f9f9f9;
  padding-left: 8px;
}

.notification-bullet {
  width: 6px;
  height: 6px;
  background-color: #c62828;
  border-radius: 50%;
  margin-top: 7px;
  margin-right: 8px;
  flex-shrink: 0;
}

.notification-title {
  font-size: 13px;
  color: #333;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

:deep(.el-card__header) {
  padding: 8px 15px;
}

:deep(.el-card__body) {
  padding: 12px 15px;
}

/* 自定义暂无通知样式 */
:deep(.custom-empty) {
  padding: 20px 0;
}

:deep(.custom-empty .el-empty__description) {
  font-size: 16px;
  color: #909399;
  margin-top: 10px;
}

:deep(.custom-empty .el-empty__image) {
  margin-bottom: 5px;
}
</style> 