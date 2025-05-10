<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { ElMessage } from 'element-plus';
import { getMyTransfers, getStatusText, getStatusColor } from '../../api/transfer';
import type { TransferItem } from '../../api/transfer';
import { Plus, View, Delete, Filter } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const currentStatus = ref('');

// 分页
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 数据列表
const transfers = ref<TransferItem[]>([]);

// 状态选项
const statusOptions = [
  { value: '', label: '全部' },
  { value: '0', label: '申请中' },
  { value: '1', label: '转出审批中' },
  { value: '2', label: '转入审批中' },
  { value: '3', label: '已通过' },
  { value: '4', label: '已拒绝' }
];

// 计算是否有过滤状态
const hasFilteredStatus = computed(() => {
  return currentStatus.value !== '';
});

// 获取状态类型
const getStatusType = (statusCode: number): 'primary' | 'success' | 'warning' | 'danger' | 'info' => {
  switch (statusCode) {
    case 0: return 'primary';
    case 1: return 'warning';
    case 2: return 'warning';
    case 3: return 'success';
    case 4: return 'danger';
    default: return 'info';
  }
};

// 加载转接列表
const loadTransfers = async () => {
  loading.value = true;
  try {
    console.log('开始加载转接申请列表...');
    
    // 构造请求参数
    const params: any = {
      page: currentPage.value,
      size: pageSize.value,
      status: currentStatus.value,
      mine: true
    };
    
    const response = await getMyTransfers(params);
    
    console.log('API返回数据:', response);
    
    if (response && (response.success || response.code === 200)) {
      if (response.data && Array.isArray(response.data.list)) {
        transfers.value = response.data.list;
        total.value = response.data.total || 0;
        
        // 确保关键字段有值
        transfers.value = transfers.value.map(item => {
          // 添加缺失字段的默认值
          if (!item.transferOutOrgName) {
            item.transferOutOrgName = '未知组织';
          }
          if (!item.transferInOrgName) {
            item.transferInOrgName = '未知组织';
          }
          
          // 处理日期格式
          if (item.applicationTime && item.applicationTime.includes('T')) {
            item.applicationTime = item.applicationTime.replace('T', ' ');
          }
          
          return item;
        });
      } else {
        transfers.value = [];
        total.value = 0;
        console.error('未找到有效的转接申请列表数据');
      }
      
      console.log('成功加载转接申请:', transfers.value.length, '条数据');
    } else {
      console.error('API返回错误:', response);
      transfers.value = [];
      total.value = 0;
      ElMessage.error(response?.message || '获取转接申请列表失败');
    }
  } catch (error) {
    console.error('获取转接申请列表失败', error);
    transfers.value = [];
    total.value = 0;
    ElMessage.error('获取转接申请列表失败：' + (error instanceof Error ? error.message : String(error)));
  } finally {
    loading.value = false;
  }
};

// 创建新申请
const createTransfer = () => {
  router.push('/dashboard/transfers/create');
};

// 查看详情
const viewDetail = (id: number | any) => {
  try {
    // 如果传入的是对象（如整行数据），则提取id属性
    const detailId = typeof id === 'object' && id !== null ? id.id : id;
    
    if (!detailId) {
      console.error('无效的转接申请ID:', id);
      ElMessage.warning('无法查看详情：申请ID无效');
      return;
    }
    
    console.log('查看转接申请详情，ID:', detailId);
    router.push(`/dashboard/transfers/detail/${detailId}`);
  } catch (error) {
    console.error('查看详情错误:', error);
    ElMessage.error('查看详情失败');
  }
};

// 格式化时间
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return '-';
  try {
    // 标准化日期格式，处理 ISO 格式日期
    let formattedDate = dateStr;
    if (dateStr.includes('T')) {
      formattedDate = dateStr.replace('T', ' ');
    }
    
    // 处理可能的时区信息
    if (formattedDate.includes('+') && formattedDate.includes('Z')) {
      formattedDate = formattedDate.split('+')[0].replace('Z', '');
    }
    
    const date = new Date(formattedDate);
    
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      console.warn('无效的日期格式:', dateStr);
      return dateStr; // 如果无法解析，则返回原始字符串
    }
    
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    });
  } catch (e) {
    console.error('格式化日期时间出错:', e, dateStr);
    return dateStr;
  }
};

// 处理分页变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  loadTransfers();
};

// 处理每页显示条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadTransfers();
};

// 处理状态筛选变化
const handleStatusChange = (value: string) => {
  currentStatus.value = value;
  currentPage.value = 1;
  loadTransfers();
};

// 是否有转接申请数据
const hasTransfers = computed(() => transfers.value.length > 0);

// 全局错误处理
const handleGlobalError = (error: any) => {
  console.error('全局错误捕获:', error);
  ElMessage.error('系统错误，请查看控制台获取详细信息');
};

// 组件加载时获取转接申请列表
onMounted(() => {
  try {
    console.log('Transfers.vue 组件已挂载');
    console.log('当前路由:', router.currentRoute.value.path);
    
    // 加载转接申请数据
    loadTransfers();
  } catch (error) {
    handleGlobalError(error);
  }
});
</script>

<template>
  <div class="transfers-container">
    <el-card class="transfers-card">
      <template #header>
        <div class="card-header">
          <h2>团员关系转接申请</h2>
          <div class="button-group">
            <el-button type="primary" @click="createTransfer" :icon="Plus">发起申请</el-button>
          </div>
        </div>
      </template>
      
      <!-- 删除调试信息 -->
      <div v-if="loading" class="loading-info">数据加载中...</div>
      
      <!-- 美化过的筛选栏 -->
      <div class="filter-container">
        <div class="filter-item">
          <div class="filter-wrapper">
            <span class="filter-icon"><el-icon><Filter /></el-icon></span>
          <span class="filter-label">状态：</span>
          <el-select 
            v-model="currentStatus" 
            placeholder="选择状态" 
            @change="handleStatusChange"
              class="status-select"
              popper-class="status-select-dropdown"
          >
            <el-option 
              v-for="option in statusOptions" 
              :key="option.value" 
              :label="option.label" 
              :value="option.value"
              >
                <template v-if="option.value !== ''">
                  <el-tag size="small" :type="getStatusType(Number(option.value))" class="status-tag">
                    {{ option.label }}
                  </el-tag>
                </template>
                <span v-else>{{ option.label }}</span>
              </el-option>
          </el-select>
            <el-button 
              v-if="currentStatus !== ''" 
              size="small" 
              type="primary" 
              text 
              class="clear-filter" 
              @click="currentStatus = ''; handleStatusChange('')"
            >
              清除筛选
            </el-button>
          </div>
        </div>
      </div>
      
      <el-table 
        :data="transfers" 
        v-loading="loading" 
        border 
        style="width: 100%"
        :empty-text="'暂无转接申请数据'"
        @row-click="viewDetail"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        
        <el-table-column prop="applicationTime" label="申请时间" min-width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.applicationTime || '') }}
          </template>
        </el-table-column>
        
        <el-table-column prop="transferOutOrgName" label="转出组织" min-width="180">
          <template #default="scope">
            {{ scope.row.transferOutOrgName || scope.row.transferOutOrg || '未知组织' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="transferInOrgName" label="转入组织" min-width="180">
          <template #default="scope">
            {{ scope.row.transferInOrgName || scope.row.transferInOrg || '未知组织' }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" min-width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.statusCode)">
              {{ scope.row.status || getStatusText(scope.row.statusCode) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="120" align="center">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              :icon="View" 
              @click.stop="viewDetail(scope.row.id)"
              circle
              title="查看详情"
            />
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      
      <div v-if="!loading && transfers.length === 0 && !hasFilteredStatus" class="empty-data">
        <div class="empty-text">暂无转接申请记录</div>
        <el-button type="primary" @click="createTransfer">发起申请</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.transfers-container {
  padding: 20px;
}

.transfers-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.button-group {
  display: flex;
  gap: 10px;
}

.card-header h2 {
  font-size: 18px;
  color: #303133;
  margin: 0;
}

/* 美化筛选容器 */
.filter-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  margin-bottom: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 12px 16px;
}

/* 美化筛选项 */
.filter-item {
  display: flex;
  align-items: center;
  margin-right: 20px;
  margin-bottom: 0;
}

/* 筛选包装器 */
.filter-wrapper {
  display: flex;
  align-items: center;
  position: relative;
}

/* 筛选图标 */
.filter-icon {
  margin-right: 8px;
  color: #409EFF;
}

/* 筛选标签 */
.filter-label {
  margin-right: 10px;
  color: #606266;
  font-weight: 500;
}

/* 状态选择器 */
.status-select {
  width: 140px;
}

/* 状态标签 */
.status-tag {
  width: 100%;
  text-align: center;
}

/* 清除筛选按钮 */
.clear-filter {
  margin-left: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.empty-data {
  margin: 40px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.empty-text {
  color: #909399;
  font-size: 14px;
  margin-bottom: 20px;
}

.loading-info {
  text-align: center;
  padding: 20px;
  color: #409EFF;
}

/* 删除所有调试相关样式 */
</style> 