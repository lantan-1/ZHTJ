<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { User, Setting, Monitor, Document, School, Trophy, Bell } from '@element-plus/icons-vue';

const router = useRouter();

const title = ref('系统管理控制台');
const description = ref('管理员可以在此管理系统配置、用户权限、组织结构等');
const loading = ref(false);

// 系统统计数据
const systemStats = reactive({
  totalUsers: 0,
  activeUsers: 0,
  organizations: 0,
  pendingApprovals: 0,
  honorsAwarded: 0,
  systemNotices: 0,
  lastUpdateTime: ''
});

// 权限验证
const checkAdminPermission = () => {
  // 这里应该调用API验证当前用户是否有管理员权限
  // 临时使用模拟数据
  const hasPermission = true;
  
  if (!hasPermission) {
    ElMessageBox.alert('您没有访问管理控制台的权限', '权限错误', {
      confirmButtonText: '返回首页',
      callback: () => {
        router.push('/dashboard');
      }
    });
  }
  
  return hasPermission;
};

// 加载系统统计数据
const loadSystemStats = async () => {
  loading.value = true;
  try {
    // 这里应该调用API获取系统统计数据
    // 临时使用模拟数据
    await new Promise(resolve => setTimeout(resolve, 500));
    
    systemStats.totalUsers = 1286;
    systemStats.activeUsers = 872;
    systemStats.organizations = 58;
    systemStats.pendingApprovals = 23;
    systemStats.honorsAwarded = 145;
    systemStats.systemNotices = 7;
    systemStats.lastUpdateTime = new Date().toLocaleString();
    
    ElMessage.success('系统数据加载成功');
  } catch (error) {
    console.error('加载系统数据失败:', error);
    ElMessage.warning('系统数据加载失败，显示模拟数据');
  } finally {
    loading.value = false;
  }
};

// 跳转到对应管理页面
const navigateTo = (path) => {
  router.push(path);
};

onMounted(() => {
  if (checkAdminPermission()) {
    loadSystemStats();
  }
});
</script>

<template>
  <div class="admin-dashboard">
    <div class="page-header">
      <h1>{{ title }}</h1>
      <p>{{ description }}</p>
    </div>
    
    <el-card class="stats-card" v-loading="loading">
      <div class="stats-header">
        <h2>系统概览</h2>
        <span>最后更新: {{ systemStats.lastUpdateTime }}</span>
      </div>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ systemStats.totalUsers }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-icon org-icon">
              <el-icon><School /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ systemStats.organizations }}</div>
              <div class="stat-label">组织数量</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="stat-item">
            <div class="stat-icon approval-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ systemStats.pendingApprovals }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <div class="admin-functions">
      <h2>管理功能</h2>
      <el-row :gutter="20">
        <!-- 用户管理 -->
        <el-col :span="8">
          <el-card class="function-card" @click="navigateTo('/admin/users')">
            <div class="function-content">
              <el-icon><User /></el-icon>
              <h3>用户管理</h3>
              <p>管理用户账号、权限和角色分配</p>
            </div>
          </el-card>
        </el-col>
        
        <!-- 组织管理 -->
        <el-col :span="8">
          <el-card class="function-card" @click="navigateTo('/admin/organizations')">
            <div class="function-content">
              <el-icon><School /></el-icon>
              <h3>组织管理</h3>
              <p>管理组织结构、团支部和委员会</p>
            </div>
          </el-card>
        </el-col>
        
        <!-- 系统设置 -->
        <el-col :span="8">
          <el-card class="function-card" @click="navigateTo('/admin/settings')">
            <div class="function-content">
              <el-icon><Setting /></el-icon>
              <h3>系统设置</h3>
              <p>配置系统参数、功能开关和安全设置</p>
            </div>
          </el-card>
        </el-col>
        
        <!-- 荣誉管理 -->
        <el-col :span="8">
          <el-card class="function-card" @click="navigateTo('/admin/honors')">
            <div class="function-content">
              <el-icon><Trophy /></el-icon>
              <h3>荣誉管理</h3>
              <p>管理荣誉类型、审批流程和证书模板</p>
            </div>
          </el-card>
        </el-col>
        
        <!-- 系统通知 -->
        <el-col :span="8">
          <el-card class="function-card" @click="navigateTo('/admin/notifications')">
            <div class="function-content">
              <el-icon><Bell /></el-icon>
              <h3>系统通知</h3>
              <p>管理系统公告、消息推送和提醒设置</p>
            </div>
          </el-card>
        </el-col>
        
        <!-- 系统监控 -->
        <el-col :span="8">
          <el-card class="function-card" @click="navigateTo('/admin/monitor')">
            <div class="function-content">
              <el-icon><Monitor /></el-icon>
              <h3>系统监控</h3>
              <p>监控系统性能、日志和操作记录</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <div class="quick-actions">
      <h2>快捷操作</h2>
      <el-button type="primary" @click="loadSystemStats">刷新系统数据</el-button>
      <el-button type="success" @click="navigateTo('/admin/notifications/create')">发布系统公告</el-button>
      <el-button type="warning" @click="navigateTo('/admin/users/roles')">管理角色权限</el-button>
      <el-button @click="navigateTo('/dashboard')">返回用户首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.admin-dashboard {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 12px;
}

.page-header h1 {
  font-size: 24px;
  margin-bottom: 8px;
  color: #303133;
}

.page-header p {
  color: #606266;
  font-size: 14px;
}

.stats-card {
  margin-bottom: 24px;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.stats-header h2 {
  font-size: 18px;
  margin: 0;
}

.stats-header span {
  font-size: 13px;
  color: #909399;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  background-color: #f6f8fa;
  height: 100px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon .el-icon {
  font-size: 28px;
  color: white;
}

.user-icon {
  background-color: #409eff;
}

.org-icon {
  background-color: #67c23a;
}

.approval-icon {
  background-color: #e6a23c;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 4px;
}

.admin-functions {
  margin-bottom: 24px;
}

.admin-functions h2, .quick-actions h2 {
  font-size: 18px;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.function-card {
  height: 180px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.function-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.function-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  padding: 20px;
}

.function-content .el-icon {
  font-size: 40px;
  margin-bottom: 12px;
  color: #409eff;
}

.function-content h3 {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: #303133;
}

.function-content p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.quick-actions {
  margin-top: 32px;
}

.quick-actions .el-button {
  margin-right: 12px;
  margin-bottom: 12px;
}
</style> 