<template>
  <div class="evaluations-container">
    <div class="page-header">
      <h2>团员教育评议</h2>
      <el-button type="primary" @click="createEvaluation" v-if="canCreateEvaluation">
        创建评议活动
      </el-button>
    </div>

    <!-- 进度统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">评议活动总数</div>
          <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">进行中活动</div>
          <div class="stat-value">{{ statistics.inProgressCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">已完成活动</div>
          <div class="stat-value">{{ statistics.completedCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-title">参与率</div>
          <div class="stat-value">{{ statistics.participationRate || '0%' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索筛选 -->
    <div class="filter-section">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="评议年度">
          <el-select v-model="filterForm.year" placeholder="选择年度" clearable class="filter-select">
            <el-option 
              v-for="year in yearOptions" 
              :key="year" 
              :label="year" 
              :value="year" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="评议状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable class="filter-select">
            <el-option label="草稿" value="草稿" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已取消" value="已取消" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" class="search-btn">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetFilter" class="reset-btn">
            <el-icon><RefreshRight /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 评议活动列表 -->
    <el-table 
      v-loading="loading" 
      :data="evaluationList" 
      border 
      style="width: 100%"
    >
      <el-table-column prop="title" label="评议标题" min-width="200" />
      <el-table-column label="评议年度" width="100">
        <template #default="scope">
          <span>{{ scope.row.evaluationYear || scope.row.year || (scope.row.title ? scope.row.title.substring(0, 4) : '') || '' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="organizationName" label="组织名称" min-width="150">
        <template #default="scope">
          {{ scope.row.organizationName || scope.row.organization_name || '未知组织' }}
        </template>
      </el-table-column>
      <el-table-column label="评议时间" min-width="200">
        <template #default="scope">
          {{ formatDate(scope.row.startTime) }} 至 {{ formatDate(scope.row.endTime) }}
        </template>
      </el-table-column>
      <el-table-column label="评议进度" width="200">
        <template #default="scope">
          <div class="progress-wrapper">
            <el-progress 
              :percentage="calculateProgress(scope.row)" 
              :status="getProgressStatus(scope.row.status)"
            />
            <span class="progress-text">{{ scope.row.status }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button 
            link 
            type="primary" 
            @click="viewDetail(scope.row.id)"
          >查看详情</el-button>
          <el-button 
            v-if="scope.row.status === '草稿'"
            link 
            type="success" 
            @click="startEvaluation(scope.row.id)"
          >开始评议</el-button>
          <el-button 
            v-if="scope.row.status === '进行中'"
            link 
            type="warning" 
            @click="completeEvaluation(scope.row.id)"
          >结束评议</el-button>
          <el-button 
            v-if="scope.row.status === '草稿'"
            link 
            type="danger" 
            @click="deleteEvaluation(scope.row.id)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="page.current"
        v-model:page-size="page.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="page.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../../stores/user';
import { Search, RefreshRight } from '@element-plus/icons-vue';
import { 
  getEvaluationList, 
  getEvaluationStatistics, 
  startEvaluation as startEvaluationApi,
  completeEvaluation as completeEvaluationApi,
  deleteEvaluation as deleteEvaluationApi 
} from '../../api/evaluation';

const router = useRouter();
const userStore = useUserStore();

// 数据定义
const loading = ref(false);
const evaluationList = ref([]);
const statistics = reactive({
  totalCount: 0,
  inProgressCount: 0,
  completedCount: 0,
  participationRate: '0%'
});

const page = reactive({
  current: 1,
  size: 10,
  total: 0
});

const filterForm = reactive({
  year: '',
  status: ''
});

// 计算属性
const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear();
  return Array.from({length: 5}, (_, i) => (currentYear - i).toString());
});

const canCreateEvaluation = computed(() => {
  return userStore.isBranchSecretary || userStore.isCommitteeSecretary;
});

// 方法
const loadEvaluations = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.current,
      size: page.size,
      year: filterForm.year || undefined,
      status: filterForm.status || undefined,
      organizationId: userStore.organizationId
    };

    // 使用封装好的API接口
    const response = await getEvaluationList(params);
    
    console.log('评议列表API响应:', response);
    
    if (response && response.success) {
      // 使用新的API返回格式，并标准化字段名
      const items = response.items || [];
      
      // 标准化数据字段，处理可能的字段名差异
      evaluationList.value = items.map(item => {
        // 提取评议年度，尝试多种方式
        let year = '';
        // 1. 直接从字段中获取 - 优先使用evaluation_year字段
        if (item.evaluation_year) {
          year = item.evaluation_year;
        } else if (item.evaluationYear) {
          year = item.evaluationYear;
        } else if (item.year) {
          year = item.year;
        } 
        // 2. 从标题中提取年份
        else if (item.title && /^\d{4}/.test(item.title)) {
          year = item.title.substring(0, 4);
        }
        // 3. 从时间中提取年份
        else if (item.startTime) {
          const startDate = new Date(item.startTime);
          if (!isNaN(startDate.getTime())) {
            year = startDate.getFullYear().toString();
          }
        }
        
        if (!year) {
          console.warn('无法搜索年度，原始数据:', item);
        }
        
        return {
          ...item,
          id: item.id || 0,
          title: item.title || '',
          evaluationYear: year, // 使用提取的年度
          organizationName: item.organizationName || item.organization_name || '',
          startTime: item.startTime || item.start_time || '',
          endTime: item.endTime || item.end_time || '',
          status: item.status || '草稿',
          initiatorId: item.initiatorId || item.initiator_id || 0,
          initiatorName: item.initiatorName || item.initiator_name || ''
        };
      });
      
      // 打印处理后的数据，检查年度字段
      console.log('处理后的评议数据，检查年度字段:');
      evaluationList.value.forEach((item, index) => {
        console.log(`[${index}] id=${item.id}, title=${item.title}, evaluationYear=${item.evaluationYear}`);
      });
      
      page.total = response.total || evaluationList.value.length;
      
      console.log('成功获取评议列表数据:', evaluationList.value.length, '条记录');
      console.log('处理后的评议列表数据:', evaluationList.value);
    } else {
      console.error('API请求成功但返回非成功状态:', response);
      ElMessage.error(response?.message || '获取评议活动列表失败');
      evaluationList.value = [];
    }
  } catch (error) {
    console.error('加载评议活动列表失败:', error);
    ElMessage.error('加载评议活动列表失败');
    evaluationList.value = [];
  } finally {
    loading.value = false;
  }
};

const loadStatistics = async () => {
  try {
    const params = {
      organizationId: userStore.organizationId,
      year: filterForm.year || undefined
    };

    // 使用封装好的API接口
    const response = await getEvaluationStatistics(params);
    
    console.log('评议统计API响应:', response);
    
    if (response.success) {
      // 数据已经正确返回，但需要汇总
      const evaluationData = response.data.data || [];
      console.log('评议数据数组:', evaluationData);
      
      // 计算总数
      statistics.totalCount = evaluationData.length;
      
      // 计算各状态数量
      statistics.inProgressCount = evaluationData.filter(item => item.status === '进行中').length;
      statistics.completedCount = evaluationData.filter(item => item.status === '已完成').length;
      
      // 计算总体参与率
      let totalMembers = 0;
      let totalCompleted = 0;
      
      evaluationData.forEach(item => {
        if (item.totalMembers > 0) {
          totalMembers += item.totalMembers;
          totalCompleted += item.completedCount || 0;
        }
      });
      
      const participationRate = totalMembers > 0 
        ? Math.round((totalCompleted / totalMembers) * 100) 
        : 0;
      
      statistics.participationRate = `${participationRate}%`;
      
      console.log('统计数据已更新:', statistics);
    }
  } catch (error) {
    console.error('加载评议统计数据失败:', error);
  }
};

const handleSearch = () => {
  page.current = 1;
  loadEvaluations();
};

const resetFilter = () => {
  filterForm.year = '';
  filterForm.status = '';
  handleSearch();
};

const handleSizeChange = (val) => {
  page.size = val;
  loadEvaluations();
};

const handleCurrentChange = (val) => {
  page.current = val;
  loadEvaluations();
};

const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString();
};

const calculateProgress = (row) => {
  if (row.status === '已完成') return 100;
  if (row.status === '草稿') return 0;
  if (row.status === '已取消') return 100;
  
  // 进行中状态，计算时间进度
  const now = new Date();
  const start = new Date(row.startTime);
  const end = new Date(row.endTime);
  
  if (now < start) return 0;
  if (now > end) return 100;
  
  const total = end - start;
  const current = now - start;
  return Math.floor((current / total) * 100);
};

const getProgressStatus = (status) => {
  if (status === '已完成') return 'success';
  if (status === '进行中') return '';
  if (status === '草稿') return 'info';
  if (status === '已取消') return 'exception';
  return '';
};

const createEvaluation = () => {
  router.push('/dashboard/evaluations/create');
};

const viewDetail = (id) => {
  router.push(`/dashboard/evaluations/${id}`);
};

const startEvaluation = async (id) => {
  try {
    const response = await startEvaluationApi(id);
    if (response.success) {
      ElMessage.success('评议活动已启动');
      loadEvaluations();
    } else {
      ElMessage.error(response.message || '启动评议活动失败');
    }
  } catch (error) {
    console.error('启动评议活动失败:', error);
    ElMessage.error('启动评议活动失败');
  }
};

const completeEvaluation = async (id) => {
  try {
    const response = await completeEvaluationApi(id);
    if (response.success) {
      ElMessage.success('评议活动已完成');
      loadEvaluations();
    } else {
      ElMessage.error(response.message || '完成评议活动失败');
    }
  } catch (error) {
    console.error('完成评议活动失败:', error);
    ElMessage.error('完成评议活动失败');
  }
};

const deleteEvaluation = async (id) => {
  ElMessageBox.confirm('确定要删除该评议活动吗？此操作不可恢复', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await deleteEvaluationApi(id);
      if (response.success) {
        ElMessage.success('评议活动已删除');
        loadEvaluations();
      } else {
        ElMessage.error(response.message || '删除评议活动失败');
      }
    } catch (error) {
      console.error('删除评议活动失败:', error);
      ElMessage.error('删除评议活动失败');
    }
  }).catch(() => {});
};

// 在组件挂载前检查身份验证状态
onBeforeMount(() => {
  // 检查是否已登录
  if (!userStore.isLoggedIn) {
    console.warn('用户未登录，尝试从localStorage初始化');
    userStore.initFromStorage();
    
    // 如果仍未登录，跳转到登录页
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录');
      router.push('/login');
    }
  }
  
  // 验证token是否存在
  const token = localStorage.getItem('token');
  if (!token) {
    console.error('令牌不存在，需要重新登录');
    ElMessage.error('身份验证失败，请重新登录');
    router.push('/login');
  }
});

// 组件加载时获取数据
onMounted(() => {
  console.log('Evaluations组件挂载，开始加载数据');
  console.log('当前用户状态:', userStore.isLoggedIn ? '已登录' : '未登录');
  console.log('Token状态:', !!localStorage.getItem('token'));
  console.log('组织ID:', userStore.organizationId);
  
  // 确保有组织ID
  if (!userStore.organizationId) {
    console.warn('缺少组织ID，尝试重新获取用户信息');
    userStore.fetchUserInfo().then(() => {
      loadEvaluations();
      loadStatistics();
    }).catch(error => {
      console.error('获取用户信息失败', error);
      ElMessage.error('获取用户信息失败，请重新登录');
      router.push('/login');
    });
  } else {
    // 添加一个标记，帮助在控制台中找到这次数据加载的日志
    console.log('=================== 开始加载评议列表数据 ===================');
    loadEvaluations();
    loadStatistics();
  }
});
</script>

<style scoped>
.evaluations-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.statistics-cards {
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-title {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.filter-section {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.filter-section:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.filter-form {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  width: 180px;
  margin-right: 8px;
}

.search-btn, .reset-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.search-btn:hover, .reset-btn:hover {
  transform: scale(1.05);
}

.search-btn .el-icon, .reset-btn .el-icon {
  margin-right: 5px;
}

.progress-wrapper {
  display: flex;
  flex-direction: column;
}

.progress-text {
  margin-top: 5px;
  font-size: 12px;
  text-align: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式样式 */
@media screen and (max-width: 768px) {
  .filter-form {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .el-form-item {
    margin-bottom: 15px;
    width: 100%;
  }
  
  .filter-select {
    width: 100%;
  }
}
</style> 