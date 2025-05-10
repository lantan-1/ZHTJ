<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useActivityStore } from '../stores/activity';
import { useUserStore } from '../stores/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import { ArrowLeft, Edit, Delete, Download } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const activityStore = useActivityStore();
const userStore = useUserStore();

// 添加角色权限控制计算属性
const canManageActivities = computed(() => {
  return userStore.isCommitteeSecretary || userStore.isBranchSecretary;
});

const activityId = ref<number>(0);
const loading = ref(true);
const participants = ref<any[]>([]);

// 活动类别对应的标签类型
const getCategoryTag = (category: string) => {
  switch (category) {
    case '支部大会': return 'danger';
    case '团课': return 'primary';
    case '主题团日': return 'success';
    case '入团仪式': return 'warning';
    case '组织生活会': return 'info';
    default: return '';
  }
};

// 格式化时间显示
const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return '-';
  try {
    const date = new Date(dateStr);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
};

// 返回活动列表页面
const goBack = () => {
  router.push('/dashboard/activities');
};

// 编辑活动
const handleEdit = () => {
  router.push(`/dashboard/activities/edit/${activityId.value}`);
};

// 删除活动
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除此活动吗？此操作不可恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    if (activityId.value) {
      const result = await activityStore.removeActivity(activityId.value);
      if (result) {
        ElMessage.success('活动已成功删除');
        router.push('/dashboard/activities');
      } else {
        ElMessage.error('删除活动失败');
      }
    }
  } catch {
    // 用户取消操作
  }
};

// 打印活动详情
const handlePrint = () => {
  window.print();
};

// 获取参与人员信息
const fetchParticipants = async (participantIds: string) => {
  if (!participantIds) return;
  
  try {
    // 实际项目中应该通过API获取成员详细信息
    // 这里使用模拟数据
    const memberMap = {
      '1': { id: 1, name: '张明', avatarUrl: '/path/to/avatar1.jpg' },
      '2': { id: 2, name: '李红', avatarUrl: '/path/to/avatar2.jpg' },
      '4': { id: 4, name: '赵芳', avatarUrl: '/path/to/avatar4.jpg' }
    };
    
    const ids = participantIds.split(',');
    participants.value = ids.map(id => memberMap[id] || { id, name: `成员${id}` });
  } catch (error) {
    console.error('获取参与人员信息失败', error);
  }
};

// 组件挂载时加载活动详情
onMounted(async () => {
  const id = route.params.id;
  if (id && !isNaN(Number(id))) {
    activityId.value = Number(id);
    
    try {
      loading.value = true;
      await activityStore.fetchActivityDetail(activityId.value);
      
      if (activityStore.currentActivity?.participants) {
        await fetchParticipants(activityStore.currentActivity.participants);
      }
    } catch (error) {
      console.error('获取活动详情失败', error);
      ElMessage.error('获取活动详情失败');
    } finally {
      loading.value = false;
    }
  } else {
    ElMessage.error('无效的活动ID');
    router.push('/dashboard/activities');
  }
});
</script>

<template>
  <div class="activity-detail-container">
    <el-card v-loading="loading" class="box-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <el-button :icon="ArrowLeft" @click="goBack">返回</el-button>
            <h2>活动详情</h2>
          </div>
          <div class="right">
            <el-button type="primary" :icon="Edit" @click="handleEdit" v-if="canManageActivities">编辑</el-button>
            <el-button type="danger" :icon="Delete" @click="handleDelete" v-if="canManageActivities">删除</el-button>
            <el-button type="default" :icon="Download" @click="handlePrint">打印</el-button>
          </div>
        </div>
      </template>
      
      <div v-if="activityStore.currentActivity" class="activity-content">
        <div class="activity-header">
          <h1 class="activity-title">
            {{ activityStore.currentActivity.requiredTopic || '未设置活动主题' }}
          </h1>
          <div class="activity-meta">
            <el-tag 
              :type="getCategoryTag(activityStore.currentActivity.category)"
              size="large"
            >
              {{ activityStore.currentActivity.category }}
            </el-tag>
            <span class="meta-item">
              <i class="el-icon-date"></i>
              开展时间: {{ formatDate(activityStore.currentActivity.date) }}
            </span>
            <span class="meta-item">
              <i class="el-icon-location"></i>
              活动地点: {{ activityStore.currentActivity.place }}
            </span>
            <span class="meta-item">
              <i class="el-icon-user"></i>
              主持人: {{ activityStore.currentActivity.host }}
            </span>
          </div>
        </div>
        
        <el-divider content-position="left">活动内容</el-divider>
        
        <div class="activity-description">
          <p>{{ activityStore.currentActivity.content }}</p>
        </div>
        
        <el-divider content-position="left">专题学习</el-divider>
        
        <div class="activity-topics">
          <el-descriptions title="学习专题" :column="1" border>
            <el-descriptions-item label="必学专题">
              {{ activityStore.currentActivity.requiredTopic || '无' }}
            </el-descriptions-item>
            <el-descriptions-item label="自学专题">
              <template v-if="activityStore.currentActivity.optionalTopics">
                <el-tag 
                  v-for="topic in activityStore.currentActivity.optionalTopics.split(',')"
                  :key="topic"
                  class="topic-tag"
                >
                  {{ topic }}
                </el-tag>
              </template>
              <template v-else>无</template>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <el-divider content-position="left">参与人员</el-divider>
        
        <div class="activity-participants">
          <el-empty v-if="!participants.length" description="暂无参与人员"></el-empty>
          
          <div v-else class="participants-list">
            <el-avatar
              v-for="participant in participants"
              :key="participant.id"
              :size="50"
              :src="participant.avatarUrl"
              class="participant-avatar"
            >
              {{ participant.name.substring(0, 1) }}
            </el-avatar>
          </div>
          
          <el-table v-if="participants.length" :data="participants" style="width: 100%">
            <el-table-column type="index" width="50" />
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column label="状态" width="100">
              <template #default>
                <el-tag type="success">已签到</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
        
        <el-divider content-position="left">活动信息</el-divider>
        
        <div class="activity-info">
          <el-descriptions border>
            <el-descriptions-item label="创建时间" :span="2">
              {{ activityStore.currentActivity.createTime ? new Date(activityStore.currentActivity.createTime).toLocaleString() : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="最后更新" :span="1">
              {{ activityStore.currentActivity.updateTime ? new Date(activityStore.currentActivity.updateTime).toLocaleString() : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="组织名称" :span="3">
              {{ activityStore.currentActivity.organizationName || '未知组织' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      
      <el-empty v-else-if="!loading" description="未找到活动信息"></el-empty>
    </el-card>
  </div>
</template>

<style scoped>
.activity-detail-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header .left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.card-header .right {
  display: flex;
  gap: 10px;
}

.activity-content {
  padding: 16px;
}

.activity-header {
  margin-bottom: 24px;
}

.activity-title {
  font-size: 24px;
  margin-bottom: 16px;
  color: #333;
  font-weight: bold;
}

.activity-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-top: 16px;
}

.meta-item {
  color: #606266;
  font-size: 14px;
}

.activity-description {
  margin-bottom: 24px;
  line-height: 1.6;
}

.activity-topics {
  margin-bottom: 24px;
}

.topic-tag {
  margin-right: 8px;
}

.activity-participants {
  margin-bottom: 24px;
}

.participants-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}

.participant-avatar {
  cursor: pointer;
}

.activity-info {
  margin-bottom: 24px;
}

@media print {
  .card-header .left button,
  .card-header .right {
    display: none;
  }
}
</style> 