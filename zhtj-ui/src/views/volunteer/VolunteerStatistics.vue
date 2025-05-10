<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useUserStore } from '../../stores/user';
import * as echarts from 'echarts/core';
import { BarChart, PieChart, LineChart } from 'echarts/charts';
import {
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent
} from 'echarts/components';
import { CanvasRenderer } from 'echarts/renderers';

// 注册必要的组件
echarts.use([
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent,
  BarChart,
  PieChart,
  LineChart,
  CanvasRenderer
]);

// 图表实例
const serviceTypeChartRef = ref<HTMLElement | null>(null);
const timeDistributionChartRef = ref<HTMLElement | null>(null);
const monthlyTrendChartRef = ref<HTMLElement | null>(null);
let serviceTypeChart: echarts.ECharts | null = null;
let timeDistributionChart: echarts.ECharts | null = null;
let monthlyTrendChart: echarts.ECharts | null = null;

// 图表数据 (示例数据)
const serviceTypeData = [
  { value: 35, name: '社区服务' },
  { value: 25, name: '公益活动' },
  { value: 18, name: '青年志愿者' },
  { value: 15, name: '扶贫帮困' },
  { value: 12, name: '环保行动' },
  { value: 10, name: '助老助残' },
  { value: 8, name: '支教活动' },
  { value: 5, name: '其他' }
];

const timeDistributionData = [
  { value: 22, name: '5小时以下' },
  { value: 38, name: '5-10小时' },
  { value: 25, name: '10-20小时' },
  { value: 15, name: '20小时以上' }
];

const monthlyTrendData = {
  months: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
  values: [12, 15, 20, 25, 28, 35, 40, 38, 32, 28, 22, 18]
};

// 统计数据
const statisticsData = reactive({
  totalHours: 0,
  totalServices: 0,
  averageHours: 0,
  thisYearHours: 0,
  thisMonthHours: 0
});

// 计算统计数据
const calculateStatistics = () => {
  // 这里应该从API获取数据，现在使用模拟数据
  statisticsData.totalHours = 128;
  statisticsData.totalServices = 15;
  statisticsData.averageHours = 8.5;
  statisticsData.thisYearHours = 85;
  statisticsData.thisMonthHours = 12;
};

// 初始化服务类型图表
const initServiceTypeChart = () => {
  if (serviceTypeChartRef.value) {
    serviceTypeChart = echarts.init(serviceTypeChartRef.value);
    
    const option = {
      title: {
        text: '志愿服务类型分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: serviceTypeData.map(item => item.name)
      },
      series: [
        {
          name: '服务类型',
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '16',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: serviceTypeData
        }
      ],
      color: ['#f56c6c', '#e6a23c', '#409eff', '#67c23a', '#909399', '#9254DE', '#36CECE', '#FF9C6E']
    };
    
    serviceTypeChart.setOption(option);
  }
};

// 初始化时长分布图表
const initTimeDistributionChart = () => {
  if (timeDistributionChartRef.value) {
    timeDistributionChart = echarts.init(timeDistributionChartRef.value);
    
    const option = {
      title: {
        text: '服务时长分布',
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        data: timeDistributionData.map(item => item.name)
      },
      series: [
        {
          name: '时长分布',
          type: 'pie',
          radius: '70%',
          data: timeDistributionData,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ],
      color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c']
    };
    
    timeDistributionChart.setOption(option);
  }
};

// 初始化月度趋势图表
const initMonthlyTrendChart = () => {
  if (monthlyTrendChartRef.value) {
    monthlyTrendChart = echarts.init(monthlyTrendChartRef.value);
    
    const option = {
      title: {
        text: '月度服务时长趋势',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: monthlyTrendData.months
      },
      yAxis: {
        type: 'value',
        name: '服务时长(小时)'
      },
      series: [
        {
          name: '服务时长',
          type: 'line',
          data: monthlyTrendData.values,
          smooth: true,
          lineStyle: {
            width: 3,
            shadowColor: 'rgba(0,0,0,0.3)',
            shadowBlur: 10,
            shadowOffsetY: 10
          },
          areaStyle: {
            opacity: 0.3,
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#c62828' },
                { offset: 1, color: 'rgba(198,40,40,0.1)' }
              ]
            }
          },
          itemStyle: {
            color: '#c62828'
          }
        }
      ],
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      }
    };
    
    monthlyTrendChart.setOption(option);
  }
};

// 处理窗口大小变化
const handleResize = () => {
  serviceTypeChart?.resize();
  timeDistributionChart?.resize();
  monthlyTrendChart?.resize();
};

// 组件挂载
onMounted(() => {
  calculateStatistics();
  
  // 初始化图表
  initServiceTypeChart();
  initTimeDistributionChart();
  initMonthlyTrendChart();
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize);
});

// 组件卸载前清理
const onBeforeUnmount = () => {
  window.removeEventListener('resize', handleResize);
  serviceTypeChart?.dispose();
  timeDistributionChart?.dispose();
  monthlyTrendChart?.dispose();
};
</script>

<template>
  <div class="statistics-container">
    <el-card shadow="hover" class="summary-card">
      <template #header>
        <div class="card-header">
          <h3>志愿服务统计</h3>
        </div>
      </template>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="stat-item">
            <div class="stat-title">总服务时长</div>
            <div class="stat-value">{{ statisticsData.totalHours }} 小时</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="stat-item">
            <div class="stat-title">服务次数</div>
            <div class="stat-value">{{ statisticsData.totalServices }} 次</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="stat-item">
            <div class="stat-title">平均时长</div>
            <div class="stat-value">{{ statisticsData.averageHours }} 小时/次</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="stat-item">
            <div class="stat-title">本年累计</div>
            <div class="stat-value">{{ statisticsData.thisYearHours }} 小时</div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8" :lg="4">
          <div class="stat-item">
            <div class="stat-title">本月累计</div>
            <div class="stat-value">{{ statisticsData.thisMonthHours }} 小时</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="chart-card">
          <div ref="serviceTypeChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="hover" class="chart-card">
          <div ref="timeDistributionChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <el-card shadow="hover" class="chart-card">
          <div ref="monthlyTrendChartRef" class="chart trend-chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.statistics-container {
  padding: 20px 0;
}

.summary-card {
  margin-bottom: 20px;
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
  color: #303133;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;
}

.chart {
  height: 400px;
  width: 100%;
}

.trend-chart {
  height: 500px;
}

@media (max-width: 768px) {
  .chart {
    height: 300px;
  }
  
  .trend-chart {
    height: 350px;
  }
  
  .chart-row {
    margin-bottom: 0;
  }
  
  .chart-card {
    margin-bottom: 20px;
  }
}
</style> 