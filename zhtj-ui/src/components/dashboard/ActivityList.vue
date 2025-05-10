<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { getActivities } from '../../api/dashboard';

const router = useRouter();
const userStore = useUserStore();

// 活动项接口
interface Activity {
  id: number;
  title: string;
  date: string;
  image?: string;
  content?: string;
  place?: string;
  category?: string;
}

// 活动列表
const activities = ref<Activity[]>([]);
const loading = ref(false);

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true;
  try {
    // 使用统一的API服务
    const response = await getActivities({ 
      page: 1, 
      size: 3, 
      organizationId: userStore.organizationId || undefined 
    });
    
    if (response.data && response.data.list) {
      activities.value = response.data.list.map((item: any) => ({
        id: item.id,
        title: item.required_topic || item.category,
        date: item.date,
        place: item.place,
        content: item.content,
        category: item.category || '其他',
        image: item.image || getActivityImage(item.category || '其他')
      }));
      
      console.log('获取到的活动数据:', activities.value);
    }
  } catch (error) {
    console.error('获取活动列表失败', error);
    // 使用默认数据
    activities.value = [
      {
        id: 1,
        title: '团史学习——重温党的光辉历程',
        date: '2023-12-22 00:14:00',
        place: '计算机学院报告厅',
        category: '团课'
      },
      {
        id: 2,
        title: '新团员入团宣誓仪式',
        date: '2023-12-21 10:10:00',
        place: '电子工程学院报告厅',
        category: '入团仪式'
      },
      {
        id: 3,
        title: '学习二十大精神主题团日活动',
        date: '2023-12-21 00:11:00',
        place: '计算机学院活动室',
        category: '主题团日'
      }
    ];
  } finally {
    loading.value = false;
  }
};

// 根据活动类别获取对应图片
const getActivityImage = (category: string): string => {
  switch (category) {
    case '支部大会':
      return '/src/assets/activity/支部大会.jpg';
    case '团课':
      return '/src/assets/activity/团课.jpg';
    case '主题团日':
      return '/src/assets/activity/主题团日.jpg';
    case '入团仪式':
      return '/src/assets/activity/入团仪式.png';
    case '组织生活会':
      return '/src/assets/activity/组织生活会.jpg';
    default:
      return '/src/assets/activity/其他.png'; // 其他类别使用默认图片
  }
};

// 生成默认图片URL - 使用新的getActivityImage替代此函数
const getDefaultImage = (id: number) => {
  return getActivityImage('其他');
};

// 查看活动详情
const viewActivity = (activity: Activity) => {
  router.push(`/dashboard/activities/${activity.id}`);
};

// 格式化时间
const formatDate = (dateStr: string) => {
  try {
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
};

// 组件加载时获取活动列表
onMounted(fetchActivities);
</script>

<template>
  <el-card shadow="hover" class="activity-card">
    <template #header>
      <div class="card-header">
        <h3>组织会议活动</h3>
        <el-link type="danger" @click="router.push('/dashboard/activities')">查看全部</el-link>
      </div>
    </template>
    
    <div v-loading="loading" class="activity-list">
      <el-empty v-if="activities.length === 0" description="暂无活动"></el-empty>
      
      <el-row :gutter="20">
        <el-col 
          v-for="activity in activities" 
          :key="activity.id" 
          :span="8"
        >
          <div class="activity-item" @click="viewActivity(activity)">
            <div class="activity-image-container">
              <img :src="activity.image" alt="活动图片" class="activity-image">
            </div>
            <div class="activity-info">
              <div class="activity-title">{{ activity.title }}</div>
              <div class="activity-date">{{ formatDate(activity.date) }}</div>
              <div v-if="activity.place" class="activity-place">{{ activity.place }}</div>
              <el-link type="danger" class="view-more">
                阅读全文
              </el-link>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-card>
</template>

<style scoped>
.activity-card {
  height: 100%;
  margin-top: 5px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #c62828;
}

.activity-list {
  min-height: 200px;
}

.activity-item {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.activity-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.activity-image-container {
  width: 100%;
  height: 150px;
  overflow: hidden;
  background-color: #f5f5f5;
  display: flex;
  justify-content: center;
  align-items: center;
}

.activity-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  transition: transform 0.3s;
}

.activity-item:hover .activity-image {
  transform: scale(1.05); /* 悬停时轻微放大效果 */
}

.activity-info {
  padding: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.activity-title {
  font-weight: bold;
  margin-bottom: 8px;
  font-size: 16px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.activity-date, .activity-place {
  color: #999;
  font-size: 12px;
  margin-bottom: 4px;
}

.view-more {
  margin-top: auto;
  align-self: flex-end;
  margin-top: 8px;
}
</style> 