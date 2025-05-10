<template>
  <div class="my-honors-container">
    
    <el-card shadow="hover" class="data-card">
      <template #header>
        <div class="card-header">
          <div class="header-title">
            <el-icon><Trophy /></el-icon>
            <span>申请记录</span>
          </div>
          <el-button type="primary" @click="goToApply" :icon="Plus">申请新荣誉</el-button>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="honorApplications"
        style="width: 100%"
        row-key="id"
        border
        stripe
        highlight-current-row
      >
        <el-table-column prop="honorType" label="荣誉类型" min-width="150">
          <template #default="{ row }">
            <div class="honor-type">
              <el-icon color="#c62828"><Medal /></el-icon>
              <span>{{ row.honorName }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="applicationTime" label="申请时间" min-width="160">
          <template #default="{ row }">
            <div class="time-info">
              <span>{{ row.applicationTime }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="applicationTime" label="申请理由" min-width="450">
          <template #default="{ row }">
            <div class="time-info">
              <span>{{ row.applicationReason }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="申请状态" min-width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" effect="dark" round>
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        
        <template #empty>
          <div class="empty-status">
            <el-empty description="暂无荣誉申请记录">
              <el-button type="primary" @click="goToApply">申请荣誉</el-button>
            </el-empty>
          </div>
        </template>
      </el-table>
      
      <div class="pagination-container" v-if="totalCount > 0">
        <el-pagination
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="totalCount"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  InfoFilled, Trophy, Medal, Calendar, CircleCheck, 
  CircleClose, Loading, Plus 
} from '@element-plus/icons-vue';
import { getMyHonors } from '@/api/honor';
import { useUserStore } from '@/stores/user';
const router = useRouter();
const userStore = useUserStore();
// 数据加载状态
const loading = ref(false);

// 荣誉申请列表
const honorApplications = ref<any[]>([]);

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);

// 跳转到申请页面
const goToApply = () => {
  router.push('/dashboard/honor-apply');
};

// 获取状态标签类型
const getStatusType = (status: string): string => {
  const statusMap: Record<string, string> = {
    '待审批': 'info',
    '审批中': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  };
  return statusMap[status] || 'info';
};

// 加载我的荣誉申请列表
const loadHonorApplications = async () => {
  loading.value = true;
  try {
    const res = await getMyHonors({
      page: currentPage.value,
      size: pageSize.value
    });
    
    if (res.success) {
      honorApplications.value = res.data.list || [];
      totalCount.value = res.data.total || 0;
    } else {
      ElMessage.error(res.message || '获取荣誉申请列表失败');
    }
  } catch (error) {
    console.error('加载荣誉申请列表时出错:', error);
    ElMessage.warning('使用模拟数据进行展示');
    
    // 模拟数据(仅开发测试用)
    honorApplications.value = [
      {
        id: 1,
        honorType: '优秀共青团员',
        applicationTime: '2023-12-10 10:30',
        status: '已通过',
        approvalTime: '2023-12-15 14:20'
      },
      {
        id: 2,
        honorType: '优秀共青团干部',
        applicationTime: '2023-11-25 09:45',
        status: '待审批'
      },
      {
        id: 3,
        honorType: '五四青年奖章',
        applicationTime: '2023-12-01 11:30',
        status: '审批中',
        approvalTime: ''
      },
      {
        id: 4,
        honorType: '优秀共青团员',
        applicationTime: '2023-10-05 16:30',
        status: '已拒绝',
        approvalTime: '2023-10-10 11:15',
        rejectReason: '申请理由不充分，请补充更多相关事迹和贡献。'
      }
    ];
    totalCount.value = honorApplications.value.length;
  } finally {
    loading.value = false;
  }
};

// 处理页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  loadHonorApplications();
};

// 处理每页条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadHonorApplications();
};

// 组件加载时获取数据
onMounted(() => {
  loadHonorApplications();
});
</script>

<style scoped>
.my-honors-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-text h2 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-description {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.data-card {
  border-radius: 8px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
}

.header-title .el-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #c62828;
}

.honor-type, .time-info {
  display: flex;
  align-items: center;
}

.honor-type .el-icon, .time-info .el-icon {
  margin-right: 5px;
  font-size: 16px;
}

.approval-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.approval-info .el-icon {
  font-size: 16px;
}

.time {
  font-size: 12px;
  color: #909399;
}

.success {
  color: #67c23a;
}

.success .el-icon {
  color: #67c23a;
}

.rejected {
  color: #f56c6c;
}

.rejected .el-icon {
  color: #f56c6c;
}

.pending {
  color: #909399;
}

.reason-icon {
  cursor: pointer;
  font-size: 16px !important;
  color: #e6a23c !important;
}

.empty-status {
  padding: 30px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    margin-top: 16px;
    width: 100%;
  }
  
  .header-actions .el-button {
    width: 100%;
  }
}
</style> 