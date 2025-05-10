<template>
  <div class="system-monitor-container">
    <div class="page-header">
      <h1>系统监控</h1>
      <p>监控系统运行状态、性能指标和访问数据</p>
    </div>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>系统资源使用率</span>
              <el-radio-group v-model="resourceTimeRange" size="small">
                <el-radio-button label="hour">1小时</el-radio-button>
                <el-radio-button label="day">24小时</el-radio-button>
                <el-radio-button label="week">7天</el-radio-button>
                <el-radio-button label="month">30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="resource-charts">
            <div class="chart-container" v-loading="loading">
              <div class="chart-title">CPU使用率 <span class="chart-value">{{ resourceData.cpu }}%</span></div>
              <div ref="cpuChartRef" class="chart"></div>
            </div>
            <div class="chart-container" v-loading="loading">
              <div class="chart-title">内存使用率 <span class="chart-value">{{ resourceData.memory }}%</span></div>
              <div ref="memoryChartRef" class="chart"></div>
            </div>
            <div class="chart-container" v-loading="loading">
              <div class="chart-title">磁盘使用率 <span class="chart-value">{{ resourceData.disk }}%</span></div>
              <div ref="diskChartRef" class="chart"></div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :xs="24" :sm="12" :md="6" v-for="(card, index) in statCards" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :class="card.iconClass">
            <i :class="card.icon"></i>
          </div>
          <div class="stat-content">
            <div class="stat-title">{{ card.title }}</div>
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-footer">
              <span :class="card.trend === 'up' ? 'trend-up' : 'trend-down'">
                <i :class="card.trend === 'up' ? 'el-icon-top' : 'el-icon-bottom'"></i>
                {{ card.percentage }}%
              </span>
              <span class="trend-period">较上周</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>系统访问量</span>
              <el-radio-group v-model="visitTimeRange" size="small">
                <el-radio-button label="today">今日</el-radio-button>
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="visitChartRef" class="full-chart" v-loading="loading"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>接口调用次数</span>
              <el-radio-group v-model="apiTimeRange" size="small">
                <el-radio-button label="today">今日</el-radio-button>
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="apiChartRef" class="full-chart" v-loading="loading"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>活跃用户</span>
              <el-button type="text" @click="refreshData">刷新</el-button>
            </div>
          </template>
          <el-table :data="activeUsers" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="username" label="用户名" min-width="100"></el-table-column>
            <el-table-column prop="role" label="角色" width="120"></el-table-column>
            <el-table-column prop="lastActiveTime" label="最近活跃时间" width="180"></el-table-column>
            <el-table-column prop="ip" label="IP地址" width="140"></el-table-column>
            <el-table-column prop="device" label="设备" width="120"></el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统日志</span>
              <el-button type="text" @click="refreshData">刷新</el-button>
            </div>
          </template>
          <el-table :data="systemLogs" stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="time" label="时间" width="180"></el-table-column>
            <el-table-column prop="level" label="级别" width="100">
              <template #default="scope">
                <el-tag :type="getLogLevelType(scope.row.level)">{{ scope.row.level }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="message" label="内容" min-width="240"></el-table-column>
            <el-table-column label="操作" width="60">
              <template #default="scope">
                <el-button type="text" @click="viewLogDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 日志详情对话框 -->
    <el-dialog title="日志详情" v-model="logDetailVisible" width="60%">
      <div v-if="selectedLog">
        <el-descriptions border column="1">
          <el-descriptions-item label="时间">{{ selectedLog.time }}</el-descriptions-item>
          <el-descriptions-item label="级别">
            <el-tag :type="getLogLevelType(selectedLog.level)">{{ selectedLog.level }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ selectedLog.ip }}</el-descriptions-item>
          <el-descriptions-item label="用户">{{ selectedLog.username }}</el-descriptions-item>
          <el-descriptions-item label="操作">{{ selectedLog.operation }}</el-descriptions-item>
          <el-descriptions-item label="内容">{{ selectedLog.message }}</el-descriptions-item>
          <el-descriptions-item label="参数">
            <pre>{{ selectedLog.params }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="堆栈信息" v-if="selectedLog.stackTrace">
            <pre>{{ selectedLog.stackTrace }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue';
import { ElMessage } from 'element-plus';
// 在实际应用中需要导入echarts
// import * as echarts from 'echarts';

// 图表时间范围选择
const resourceTimeRange = ref('day');
const visitTimeRange = ref('today');
const apiTimeRange = ref('today');

// 图表引用
const cpuChartRef = ref(null);
const memoryChartRef = ref(null);
const diskChartRef = ref(null);
const visitChartRef = ref(null);
const apiChartRef = ref(null);

// 图表实例对象
const charts = reactive({
  cpuChart: null,
  memoryChart: null,
  diskChart: null,
  visitChart: null,
  apiChart: null
});

// 系统资源数据
const resourceData = reactive({
  cpu: 32,
  memory: 64,
  disk: 45
});

// 数据加载状态
const loading = ref(false);

// 统计卡片数据
const statCards = [
  {
    title: '今日访问量',
    value: '1,234',
    icon: 'el-icon-view',
    iconClass: 'bg-blue',
    trend: 'up',
    percentage: 12.3
  },
  {
    title: '新增用户',
    value: '56',
    icon: 'el-icon-user',
    iconClass: 'bg-green',
    trend: 'up',
    percentage: 5.7
  },
  {
    title: '系统警告',
    value: '3',
    icon: 'el-icon-warning',
    iconClass: 'bg-yellow',
    trend: 'down',
    percentage: 8.5
  },
  {
    title: '服务器负载',
    value: '68%',
    icon: 'el-icon-s-data',
    iconClass: 'bg-red',
    trend: 'up',
    percentage: 4.2
  }
];

// 活跃用户数据
const activeUsers = ref([
  {
    username: 'admin',
    role: '系统管理员',
    lastActiveTime: '2023-06-01 09:32:15',
    ip: '192.168.1.100',
    device: 'Windows/Chrome'
  },
  {
    username: 'zhangsan',
    role: '教师',
    lastActiveTime: '2023-06-01 09:28:42',
    ip: '192.168.1.105',
    device: 'iOS/Safari'
  },
  {
    username: 'lisi',
    role: '学生',
    lastActiveTime: '2023-06-01 09:25:10',
    ip: '192.168.1.110',
    device: 'Android/Chrome'
  },
  {
    username: 'wangwu',
    role: '学生',
    lastActiveTime: '2023-06-01 09:20:35',
    ip: '192.168.1.115',
    device: 'Windows/Edge'
  },
  {
    username: 'zhaoliu',
    role: '辅导员',
    lastActiveTime: '2023-06-01 09:15:22',
    ip: '192.168.1.120',
    device: 'macOS/Chrome'
  }
]);

// 系统日志数据
const systemLogs = ref([
  {
    time: '2023-06-01 09:32:15',
    level: 'INFO',
    message: '用户admin登录系统',
    username: 'admin',
    ip: '192.168.1.100',
    operation: '用户登录',
    params: '{"username": "admin", "loginType": "password"}'
  },
  {
    time: '2023-06-01 09:28:30',
    level: 'WARNING',
    message: '用户尝试访问未授权资源',
    username: 'zhangsan',
    ip: '192.168.1.105',
    operation: '权限验证',
    params: '{"url": "/admin/settings", "method": "GET"}'
  },
  {
    time: '2023-06-01 09:25:10',
    level: 'ERROR',
    message: '数据库连接失败',
    username: 'system',
    ip: '127.0.0.1',
    operation: '系统运行',
    params: '{"database": "zhtj", "error": "Connection timed out"}',
    stackTrace: 'java.sql.SQLException: Connection timed out\n  at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:129)\n  at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:97)\n  at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)'
  },
  {
    time: '2023-06-01 09:20:35',
    level: 'INFO',
    message: '用户更新个人信息',
    username: 'lisi',
    ip: '192.168.1.110',
    operation: '用户操作',
    params: '{"userId": 1003, "fields": ["phone", "email"]}'
  },
  {
    time: '2023-06-01 09:15:22',
    level: 'INFO',
    message: '系统定时任务执行完成',
    username: 'system',
    ip: '127.0.0.1',
    operation: '定时任务',
    params: '{"task": "cleanTempFiles", "result": "success", "filesDeleted": 23}'
  }
]);

// 日志详情对话框
const logDetailVisible = ref(false);
const selectedLog = ref(null);

// 页面初始化
onMounted(() => {
  fetchSystemData();
  initCharts();
  // 设置定时刷新
  const timer = setInterval(fetchSystemData, 60000); // 每分钟刷新一次
  
  // 组件销毁前清除定时器
  onBeforeUnmount(() => {
    clearInterval(timer);
    disposeCharts();
  });
});

// 获取系统数据
const fetchSystemData = async () => {
  loading.value = true;
  try {
    // 实际应用中应调用API获取数据
    await new Promise(resolve => setTimeout(resolve, 1000)); // 模拟API调用延时
    // 更新图表数据
    updateCharts();
  } catch (error) {
    console.error('获取系统数据失败:', error);
    ElMessage.error('获取系统数据失败');
  } finally {
    loading.value = false;
  }
};

// 初始化图表
const initCharts = () => {
  // 注释掉echarts相关代码，避免运行时错误
  // 在实际应用中，应该使用以下代码
  /*
  if (cpuChartRef.value) {
    charts.cpuChart = echarts.init(cpuChartRef.value);
    // 配置CPU图表
  }
  if (memoryChartRef.value) {
    charts.memoryChart = echarts.init(memoryChartRef.value);
    // 配置内存图表
  }
  if (diskChartRef.value) {
    charts.diskChart = echarts.init(diskChartRef.value);
    // 配置磁盘图表
  }
  if (visitChartRef.value) {
    charts.visitChart = echarts.init(visitChartRef.value);
    // 配置访问量图表
  }
  if (apiChartRef.value) {
    charts.apiChart = echarts.init(apiChartRef.value);
    // 配置API调用图表
  }
  
  // 设置窗口大小变化时自动调整图表大小
  window.addEventListener('resize', () => {
    Object.values(charts).forEach(chart => {
      if (chart) chart.resize();
    });
  });
  
  // 初始更新图表数据
  updateCharts();
  */
};

// 销毁图表实例
const disposeCharts = () => {
  // 注释掉echarts相关代码，避免运行时错误
  // 在实际应用中，应该使用以下代码
  /*
  Object.values(charts).forEach(chart => {
    if (chart) chart.dispose();
  });
  */
};

// 更新图表数据
const updateCharts = () => {
  // 注释掉echarts相关代码，避免运行时错误
  // 在实际应用中，应该使用以下代码
  /*
  // 模拟生成图表数据
  const generateLineData = (count, baseValue, fluctuation) => {
    const data = [];
    for (let i = 0; i < count; i++) {
      data.push(baseValue + Math.random() * fluctuation - fluctuation / 2);
    }
    return data;
  };
  
  // 生成时间轴数据
  const generateTimeAxis = (count, type) => {
    const now = new Date();
    const axis = [];
    let step = 0;
    
    if (type === 'hour') {
      step = 60 * 1000; // 1分钟
    } else if (type === 'day') {
      step = 60 * 60 * 1000; // 1小时
    } else if (type === 'week') {
      step = 24 * 60 * 60 * 1000; // 1天
    } else if (type === 'month') {
      step = 24 * 60 * 60 * 1000; // 1天
    }
    
    for (let i = count - 1; i >= 0; i--) {
      const time = new Date(now.getTime() - i * step);
      axis.push(time.toLocaleTimeString());
    }
    
    return axis;
  };
  
  // CPU图表
  if (charts.cpuChart) {
    const xAxis = generateTimeAxis(24, resourceTimeRange.value);
    const cpuData = generateLineData(24, 30, 20);
    resourceData.cpu = cpuData[cpuData.length - 1].toFixed(0);
    
    charts.cpuChart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: xAxis,
        axisLabel: {
          fontSize: 10
        }
      },
      yAxis: {
        type: 'value',
        min: 0,
        max: 100,
        axisLabel: {
          formatter: '{value}%'
        }
      },
      series: [{
        data: cpuData,
        type: 'line',
        smooth: true,
        areaStyle: {},
        itemStyle: {
          color: '#409EFF'
        }
      }]
    });
  }
  
  // 其他图表类似配置...
  */
};

// 手动刷新数据
const refreshData = async () => {
  await fetchSystemData();
  ElMessage.success('数据已刷新');
};

// 查看日志详情
const viewLogDetail = (log) => {
  selectedLog.value = log;
  logDetailVisible.value = true;
};

// 获取日志级别对应的标签类型
const getLogLevelType = (level) => {
  const typeMap = {
    'INFO': 'info',
    'WARNING': 'warning',
    'ERROR': 'danger',
    'DEBUG': 'success'
  };
  return typeMap[level] || 'info';
};
</script>

<style scoped>
.system-monitor-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-card {
  margin-bottom: 20px;
}

.resource-charts {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
}

.chart-container {
  flex: 1;
  min-width: 200px;
}

.chart-title {
  font-size: 16px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-value {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.chart {
  height: 200px;
}

.full-chart {
  height: 350px;
}

.stat-card {
  margin-bottom: 20px;
  height: 120px;
  display: flex;
  overflow: hidden;
}

.stat-icon {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 36px;
  color: #fff;
  margin-right: 15px;
}

.bg-blue {
  background-color: #409EFF;
}

.bg-green {
  background-color: #67C23A;
}

.bg-yellow {
  background-color: #E6A23C;
}

.bg-red {
  background-color: #F56C6C;
}

.stat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.stat-footer {
  font-size: 12px;
}

.trend-up {
  color: #67C23A;
}

.trend-down {
  color: #F56C6C;
}

.trend-period {
  color: #909399;
  margin-left: 5px;
}

.mt-20 {
  margin-top: 20px;
}

pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
  font-family: monospace;
  font-size: 12px;
  max-height: 300px;
  overflow-y: auto;
}
</style> 