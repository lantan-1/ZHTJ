<template>
  <div class="evaluation-statistics-container">
    <div class="page-header">
      <h2>评议统计分析</h2>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="评议年度">
          <el-select v-model="filterForm.year" placeholder="选择年度" clearable>
            <el-option 
              v-for="year in yearOptions" 
              :key="year" 
              :label="year" 
              :value="year" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="组织层级">
          <el-cascader
            v-model="filterForm.organizationId"
            :options="organizationOptions"
            :props="{
              value: 'id',
              label: 'name',
              children: 'children',
              emitPath: false,
              checkStrictly: true
            }"
            placeholder="选择组织"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="success" @click="exportData">导出数据</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 总体统计卡片 -->
    <el-card class="summary-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>总体评议情况</span>
          <el-radio-group v-model="timeRange" size="small">
            <el-radio-button label="year">年度</el-radio-button>
            <el-radio-button label="quarter">季度</el-radio-button>
            <el-radio-button label="month">月度</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-title">评议活动总数</div>
            <div class="summary-value">{{ summaryData.totalCount || 0 }}</div>
            <div class="summary-compare" :class="getCompareClass(summaryData.totalCountCompare)">
              <i :class="getCompareIcon(summaryData.totalCountCompare)"></i>
              {{ formatCompare(summaryData.totalCountCompare) }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-title">完成率</div>
            <div class="summary-value">{{ summaryData.completionRate || '0%' }}</div>
            <div class="summary-compare" :class="getCompareClass(summaryData.completionRateCompare)">
              <i :class="getCompareIcon(summaryData.completionRateCompare)"></i>
              {{ formatCompare(summaryData.completionRateCompare) }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-title">优秀率</div>
            <div class="summary-value">{{ summaryData.excellentRate || '0%' }}</div>
            <div class="summary-compare" :class="getCompareClass(summaryData.excellentRateCompare)">
              <i :class="getCompareIcon(summaryData.excellentRateCompare)"></i>
              {{ formatCompare(summaryData.excellentRateCompare) }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-title">参与人数</div>
            <div class="summary-value">{{ summaryData.participantCount || 0 }}</div>
            <div class="summary-compare" :class="getCompareClass(summaryData.participantCountCompare)">
              <i :class="getCompareIcon(summaryData.participantCountCompare)"></i>
              {{ formatCompare(summaryData.participantCountCompare) }}
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>评议结果分布</span>
            </div>
          </template>
          <div ref="resultPieChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>评议趋势分析</span>
            </div>
          </template>
          <div ref="trendLineChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>各组织评议情况对比</span>
            </div>
          </template>
          <div ref="organizationBarChart" class="chart-container chart-bar"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 组织评议情况表格 -->
    <el-card class="table-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>组织评议详情</span>
        </div>
      </template>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="organizationName" label="组织名称" min-width="180" />
        <el-table-column prop="evaluationCount" label="评议活动数" width="100" />
        <el-table-column prop="participantCount" label="参与人数" width="100" />
        <el-table-column prop="completionRate" label="完成率" width="100" />
        <el-table-column label="评议结果" min-width="400">
          <template #default="scope">
            <div class="result-progress">
              <div class="progress-label">优秀：{{ scope.row.excellentRate }}</div>
              <el-progress 
                :percentage="parseFloat(scope.row.excellentRate)" 
                :show-text="false" 
                color="#67c23a" 
                :stroke-width="10"
              />
            </div>
            <div class="result-progress">
              <div class="progress-label">合格：{{ scope.row.qualifiedRate }}</div>
              <el-progress 
                :percentage="parseFloat(scope.row.qualifiedRate)" 
                :show-text="false" 
                color="#409eff" 
                :stroke-width="10"
              />
            </div>
            <div class="result-progress">
              <div class="progress-label">基本合格：{{ scope.row.basicQualifiedRate }}</div>
              <el-progress 
                :percentage="parseFloat(scope.row.basicQualifiedRate)" 
                :show-text="false" 
                color="#e6a23c" 
                :stroke-width="10"
              />
            </div>
            <div class="result-progress">
              <div class="progress-label">不合格：{{ scope.row.unqualifiedRate }}</div>
              <el-progress 
                :percentage="parseFloat(scope.row.unqualifiedRate)" 
                :show-text="false" 
                color="#f56c6c" 
                :stroke-width="10"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="scope">
            <el-button 
              link 
              type="primary" 
              @click="viewOrgDetail(scope.row.organizationId)"
            >详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick, onBeforeMount } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import * as echarts from 'echarts';
import api from '../../api/index';
import { useUserStore } from '../../stores/user';

const router = useRouter();
const userStore = useUserStore();

// DOM引用
const resultPieChart = ref(null);
const trendLineChart = ref(null);
const organizationBarChart = ref(null);

// echarts实例
let pieChart = null;
let lineChart = null;
let barChart = null;

// 数据定义
const loading = ref(false);
const timeRange = ref('year');
const organizationOptions = ref([]);

const filterForm = reactive({
  year: new Date().getFullYear().toString(),
  organizationId: null
});

const summaryData = reactive({
  totalCount: 0,
  totalCountCompare: 0,
  completionRate: '0%',
  completionRateCompare: 0,
  excellentRate: '0%',
  excellentRateCompare: 0,
  participantCount: 0,
  participantCountCompare: 0
});

const tableData = ref([]);

// 计算属性
const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear();
  return Array.from({length: 5}, (_, i) => (currentYear - i).toString());
});

// 方法
const loadOrganizations = async () => {
  try {
    const response = await axios.get('/api/organizations/tree');
    if (response.data.success) {
      organizationOptions.value = response.data.data || [];
    } else {
      ElMessage.error(response.data.message || '获取组织列表失败');
    }
  } catch (error) {
    console.error('加载组织列表失败:', error);
    ElMessage.error('加载组织列表失败');
  }
};

const loadStatistics = async () => {
  loading.value = true;
  try {
    // 请求前检查token
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('加载评议统计数据失败: 缺少令牌');
      ElMessage.error('身份验证失败，请重新登录');
      router.push('/login');
      return;
    }

    const params = {
      organizationId: userStore.organizationId,
      year: filterForm.year || undefined
    };
    
    // 使用api实例代替axios，确保请求拦截器生效
    const response = await api.get('/evaluations/statistics/data', { 
      params,
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.success) {
      statistics = response.data;
      // 处理数据用于图表展示
      prepareChartData();
    } else {
      ElMessage.error(response.message || '获取统计数据失败');
    }
  } catch (error) {
    console.error('加载统计数据失败:', error);
    ElMessage.error('加载统计数据失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  loadStatistics();
};

const resetFilter = () => {
  filterForm.year = new Date().getFullYear().toString();
  filterForm.organizationId = null;
  handleSearch();
};

const viewOrgDetail = (orgId) => {
  router.push({
    path: '/dashboard/evaluations',
    query: { organizationId: orgId }
  });
};

const exportData = () => {
  const params = new URLSearchParams({
    year: filterForm.year,
    organizationId: filterForm.organizationId || '',
    timeRange: timeRange.value
  }).toString();
  
  window.open(`/api/evaluations/statistics/export?${params}`, '_blank');
};

// 格式化比较值
const formatCompare = (value) => {
  if (!value && value !== 0) return '--';
  return value > 0 ? `+${value}%` : `${value}%`;
};

// 获取比较值的样式类
const getCompareClass = (value) => {
  if (!value && value !== 0) return '';
  return value > 0 ? 'up' : (value < 0 ? 'down' : '');
};

// 获取比较值的图标
const getCompareIcon = (value) => {
  if (!value && value !== 0) return '';
  return value > 0 ? 'el-icon-top' : (value < 0 ? 'el-icon-bottom' : '');
};

// 初始化饼图
const initPieChart = (data) => {
  if (!resultPieChart.value) return;
  
  // 销毁旧实例
  if (pieChart) {
    pieChart.dispose();
  }
  
  pieChart = echarts.init(resultPieChart.value);
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      data: data.map(item => item.name)
    },
    series: [
      {
        name: '评议结果',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '14',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data.map(item => ({
          value: item.value,
          name: item.name
        }))
      }
    ]
  };
  
  pieChart.setOption(option);
  
  // 响应式调整
  window.addEventListener('resize', () => pieChart && pieChart.resize());
};

// 初始化折线图
const initLineChart = (data) => {
  if (!trendLineChart.value) return;
  
  // 销毁旧实例
  if (lineChart) {
    lineChart.dispose();
  }
  
  lineChart = echarts.init(trendLineChart.value);
  
  const xAxisData = data.map(item => item.date);
  const series = [
    {
      name: '优秀',
      type: 'line',
      data: data.map(item => item.excellent),
      smooth: true
    },
    {
      name: '合格',
      type: 'line',
      data: data.map(item => item.qualified),
      smooth: true
    },
    {
      name: '基本合格',
      type: 'line',
      data: data.map(item => item.basicQualified),
      smooth: true
    },
    {
      name: '不合格',
      type: 'line',
      data: data.map(item => item.unqualified),
      smooth: true
    }
  ];
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['优秀', '合格', '基本合格', '不合格'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '12%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: series
  };
  
  lineChart.setOption(option);
  
  // 响应式调整
  window.addEventListener('resize', () => lineChart && lineChart.resize());
};

// 初始化柱状图
const initBarChart = (data) => {
  if (!organizationBarChart.value) return;
  
  // 销毁旧实例
  if (barChart) {
    barChart.dispose();
  }
  
  barChart = echarts.init(organizationBarChart.value);
  
  const organizations = data.map(item => item.organizationName);
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['优秀', '合格', '基本合格', '不合格'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    yAxis: {
      type: 'category',
      data: organizations
    },
    series: [
      {
        name: '优秀',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: data.map(item => parseFloat(item.excellentRate))
      },
      {
        name: '合格',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: data.map(item => parseFloat(item.qualifiedRate))
      },
      {
        name: '基本合格',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: data.map(item => parseFloat(item.basicQualifiedRate))
      },
      {
        name: '不合格',
        type: 'bar',
        stack: 'total',
        label: {
          show: true
        },
        emphasis: {
          focus: 'series'
        },
        data: data.map(item => parseFloat(item.unqualifiedRate))
      }
    ]
  };
  
  barChart.setOption(option);
  
  // 响应式调整
  window.addEventListener('resize', () => barChart && barChart.resize());
};

// 监听时间范围变化
watch(timeRange, (newValue) => {
  loadStatistics();
});

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

// 组件挂载时加载数据
onMounted(() => {
  console.log('EvaluationStatistics组件挂载，开始加载数据');
  console.log('当前用户状态:', userStore.isLoggedIn ? '已登录' : '未登录');
  console.log('Token状态:', !!localStorage.getItem('token'));
  console.log('组织ID:', userStore.organizationId);
  
  // 确保有组织ID
  if (!userStore.organizationId) {
    console.warn('缺少组织ID，尝试重新获取用户信息');
    userStore.fetchUserInfo().then(() => {
      loadStatistics();
    }).catch(error => {
      console.error('获取用户信息失败', error);
      ElMessage.error('获取用户信息失败，请重新登录');
      router.push('/login');
    });
  } else {
    loadStatistics();
  }
});
</script>

<style scoped>
.evaluation-statistics-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.filter-card,
.summary-card,
.chart-card,
.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-item {
  text-align: center;
  padding: 15px 0;
}

.summary-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.summary-compare {
  font-size: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.summary-compare.up {
  color: #67c23a;
}

.summary-compare.down {
  color: #f56c6c;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 350px;
  width: 100%;
}

.chart-bar {
  height: 400px;
}

.result-progress {
  margin-bottom: 8px;
}

.progress-label {
  font-size: 12px;
  margin-bottom: 4px;
}
</style> 