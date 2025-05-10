<template>
  <div class="register-statistics-container">
    <div class="page-header">
      <h2>团籍注册统计</h2>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="注册年度">
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
          <span>总体注册情况</span>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-title">应注册人数</div>
            <div class="summary-value">{{ summaryData.totalCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-title">已注册人数</div>
            <div class="summary-value">{{ summaryData.registeredCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-title">注册率</div>
            <div class="summary-value">{{ summaryData.registrationRate || '0%' }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="summary-item">
            <div class="summary-title">团费缴纳率</div>
            <div class="summary-value">{{ summaryData.feesPaymentRate || '0%' }}</div>
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
              <span>注册状态分布</span>
            </div>
          </template>
          <div ref="statusPieChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>注册趋势分析</span>
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
              <span>各组织注册情况对比</span>
            </div>
          </template>
          <div ref="organizationBarChart" class="chart-container chart-bar"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 组织注册情况表格 -->
    <el-card class="table-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>组织注册详情</span>
        </div>
      </template>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="organizationName" label="组织名称" min-width="180" />
        <el-table-column prop="totalCount" label="应注册人数" width="120" />
        <el-table-column prop="registeredCount" label="已注册人数" width="120" />
        <el-table-column prop="registrationRate" label="注册率" width="100" />
        <el-table-column prop="feesPaymentRate" label="团费缴纳率" width="120" />
        <el-table-column label="注册进度" min-width="200">
          <template #default="scope">
            <el-progress 
              :percentage="parseFloat(scope.row.registrationRate)" 
              :status="getProgressStatus(parseFloat(scope.row.registrationRate))"
            />
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
import { ref, reactive, computed, onMounted, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import * as echarts from 'echarts';

const router = useRouter();

// DOM引用
const statusPieChart = ref(null);
const trendLineChart = ref(null);
const organizationBarChart = ref(null);

// echarts实例
let pieChart = null;
let lineChart = null;
let barChart = null;

// 数据定义
const loading = ref(false);
const organizationOptions = ref([]);

const filterForm = reactive({
  year: new Date().getFullYear().toString(),
  organizationId: null
});

const summaryData = reactive({
  totalCount: 0,
  registeredCount: 0,
  registrationRate: '0%',
  feesPaymentRate: '0%'
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
      organizationOptions.value = response.data || [];
    } else {
      ElMessage.error(response.data.message || '获取组织列表失败');
    }
  } catch (error) {
    console.error('加载组织列表失败:', error);
    ElMessage.error('加载组织列表失败');
  }
};

const loadStatisticsData = async () => {
  loading.value = true;
  try {
    const params = {
      year: filterForm.year,
      organizationId: filterForm.organizationId || undefined
    };

    const response = await axios.get('/api/register/statistics/data', { params });
    if (response.data.success) {
      // 更新概览数据
      Object.assign(summaryData, response.data.data.summary || {});
      
      // 更新表格数据
      tableData.value = response.data.data.organizations || [];
      
      // 初始化图表
      nextTick(() => {
        initPieChart(response.data.data.statusDistribution || []);
        initLineChart(response.data.data.trends || []);
        initBarChart(response.data.data.organizationComparison || []);
      });
    } else {
      ElMessage.error(response.data.message || '获取统计数据失败');
    }
  } catch (error) {
    console.error('加载统计数据失败:', error);
    ElMessage.error('加载统计数据失败');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  loadStatisticsData();
};

const resetFilter = () => {
  filterForm.year = new Date().getFullYear().toString();
  filterForm.organizationId = null;
  handleSearch();
};

const viewOrgDetail = (orgId) => {
  router.push({
    path: '/dashboard/register',
    query: { organizationId: orgId }
  });
};

const exportData = () => {
  const params = new URLSearchParams({
    year: filterForm.year,
    organizationId: filterForm.organizationId || ''
  }).toString();
  
  window.open(`/api/register/statistics/export?${params}`, '_blank');
};

const getProgressStatus = (rate) => {
  if (rate >= 90) return 'success';
  if (rate >= 70) return '';
  if (rate >= 50) return 'warning';
  return 'exception';
};

// 初始化饼图
const initPieChart = (data) => {
  if (!statusPieChart.value) return;
  
  // 销毁旧实例
  if (pieChart) {
    pieChart.dispose();
  }
  
  pieChart = echarts.init(statusPieChart.value);
  
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
        name: '注册状态',
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
      name: '注册人数',
      type: 'line',
      data: data.map(item => item.count),
      smooth: true
    },
    {
      name: '注册率',
      type: 'line',
      yAxisIndex: 1,
      data: data.map(item => item.rate),
      smooth: true
    }
  ];
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['注册人数', '注册率'],
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
    yAxis: [
      {
        type: 'value',
        name: '人数',
        position: 'left'
      },
      {
        type: 'value',
        name: '百分比',
        position: 'right',
        axisLabel: {
          formatter: '{value}%'
        },
        splitLine: {
          show: false
        }
      }
    ],
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
      data: ['应注册人数', '已注册人数', '注册率'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '3%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'value',
        name: '人数',
        position: 'bottom'
      }
    ],
    yAxis: {
      type: 'category',
      data: organizations
    },
    series: [
      {
        name: '应注册人数',
        type: 'bar',
        stack: '总量',
        emphasis: {
          focus: 'series'
        },
        data: data.map(item => item.totalCount)
      },
      {
        name: '已注册人数',
        type: 'bar',
        emphasis: {
          focus: 'series'
        },
        data: data.map(item => item.registeredCount)
      },
      {
        name: '注册率',
        type: 'line',
        yAxisIndex: 0,
        xAxisIndex: 0,
        emphasis: {
          focus: 'series'
        },
        data: data.map(item => ({
          value: item.totalCount,
          label: {
            show: true,
            formatter: item.registrationRate,
            color: '#409EFF'
          }
        }))
      }
    ]
  };
  
  barChart.setOption(option);
  
  // 响应式调整
  window.addEventListener('resize', () => barChart && barChart.resize());
};

// 生命周期钩子
onMounted(() => {
  loadOrganizations();
  loadStatisticsData();
});
</script>

<style scoped>
.register-statistics-container {
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
</style> 