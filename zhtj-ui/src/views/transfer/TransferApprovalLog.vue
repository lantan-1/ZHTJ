<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { ElMessage } from 'element-plus';
import { getTransferDetail } from '../../api/transfer';
import { getApprovalLogs } from '../../api/approvalLog';
import type { TransferApprovalLog } from '../../api/approvalLog';
import { ArrowLeft } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);

// 转接申请ID
const transferId = computed(() => Number(route.params.id));

// 申请信息
const transferInfo = ref<any>(null);

// 审批日志列表
const approvalLogs = ref<TransferApprovalLog[]>([]);

// 加载审批日志
const loadApprovalLogs = async () => {
  if (!transferId.value) {
    ElMessage.error('转接申请ID无效');
    return;
  }
  
  loading.value = true;
  try {
    // 获取申请基本信息
    const detailResponse = await getTransferDetail(transferId.value);
    if (detailResponse && detailResponse.success) {
      transferInfo.value = detailResponse.data;
    }
    
    // 获取审批日志
    const logsResponse = await getApprovalLogs(transferId.value);
    if (logsResponse && logsResponse.success) {
      approvalLogs.value = logsResponse.data || [];
    } else {
      ElMessage.error(logsResponse?.message || '获取审批日志失败');
    }
  } catch (error) {
    console.error('获取审批日志失败', error);
    ElMessage.error('获取审批日志失败');
  } finally {
    loading.value = false;
  }
};

// 返回到详情页
const goBack = () => {
  router.push(`/dashboard/transfers/detail/${transferId.value}`);
};

// 格式化时间
const formatDateTime = (dateStr: string | null) => {
  if (!dateStr) return '-';
  try {
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
};

// 获取审批类型文本
const getApprovalTypeText = (type: number) => {
  switch (type) {
    case 1: return '转出审批';
    case 2: return '转入审批';
    default: return '未知类型';
  }
};

// 获取审批结果文本
const getApprovalText = (approved: boolean | null) => {
  if (approved === null) return '未审批';
  return approved ? '通过' : '拒绝';
};

// 获取审批结果样式类
const getApprovalClass = (approved: boolean | null) => {
  if (approved === null) return 'pending';
  return approved ? 'approved' : 'rejected';
};

// 组件加载时获取数据
onMounted(() => {
  loadApprovalLogs();
});
</script>

<template>
  <div class="approval-log-container">
    <el-card class="approval-log-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button 
              type="text" 
              :icon="ArrowLeft" 
              @click="goBack"
              class="back-button"
            >
              返回
            </el-button>
            <h2>审批日志</h2>
          </div>
        </div>
      </template>
      
      <div class="approval-log-content">
        <!-- 申请基本信息 -->
        <div class="info-section" v-if="transferInfo">
          <div class="info-header">
            <h3>申请基本信息</h3>
          </div>
          <div class="info-body">
            <div class="info-item">
              <span class="label">申请人:</span>
              <span class="value">{{ transferInfo.transferUserName }}</span>
            </div>
            <div class="info-item">
              <span class="label">转出组织:</span>
              <span class="value">{{ transferInfo.transferOutOrgName }}</span>
            </div>
            <div class="info-item">
              <span class="label">转入组织:</span>
              <span class="value">{{ transferInfo.transferInOrgName }}</span>
            </div>
            <div class="info-item">
              <span class="label">申请时间:</span>
              <span class="value">{{ formatDateTime(transferInfo.applicationTime) }}</span>
            </div>
            <div class="info-item">
              <span class="label">当前状态:</span>
              <span class="value">{{ transferInfo.status }}</span>
            </div>
          </div>
        </div>
        
        <!-- 审批日志列表 -->
        <div class="log-section">
          <div class="log-header">
            <h3>审批历史记录</h3>
          </div>
          
          <div class="log-body" v-if="approvalLogs.length > 0">
            <el-timeline>
              <el-timeline-item
                v-for="log in approvalLogs"
                :key="log.id"
                :timestamp="formatDateTime(log.approvalTime)"
                :type="log.approved ? 'success' : 'danger'"
              >
                <div class="log-item">
                  <div class="log-title">
                    <span class="log-type">{{ getApprovalTypeText(log.approvalType) }}</span>
                    <span class="log-result" :class="getApprovalClass(log.approved)">
                      {{ getApprovalText(log.approved) }}
                    </span>
                  </div>
                  <div class="log-info">
                    <span class="log-approver">审批人: {{ log.approverName }}</span>
                  </div>
                  <div class="log-remark" v-if="log.approvalRemark">
                    <span class="label">备注:</span>
                    <span class="value">{{ log.approvalRemark }}</span>
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
          
          <div class="empty-logs" v-else>
            <el-empty description="暂无审批记录" />
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.approval-log-container {
  padding: 20px;
}

.approval-log-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.back-button {
  margin-right: 15px;
}

.card-header h2 {
  font-size: 18px;
  color: #303133;
  margin: 0;
}

.info-section {
  margin-bottom: 30px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fff;
}

.info-header {
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #f5f7fa;
}

.info-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.info-body {
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: flex-start;
}

.info-item .label {
  color: #606266;
  font-weight: bold;
  margin-right: 8px;
  min-width: 80px;
}

.info-item .value {
  color: #303133;
  word-break: break-all;
}

.log-section {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fff;
}

.log-header {
  padding: 12px 20px;
  border-bottom: 1px solid #ebeef5;
  background-color: #f5f7fa;
}

.log-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.log-body {
  padding: 20px;
}

.log-item {
  margin-bottom: 8px;
}

.log-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.log-type {
  font-weight: bold;
  color: #303133;
}

.log-result {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.approved {
  background-color: #f0f9eb;
  color: #67c23a;
}

.rejected {
  background-color: #fef0f0;
  color: #f56c6c;
}

.pending {
  background-color: #f4f4f5;
  color: #909399;
}

.log-info {
  margin-bottom: 8px;
  color: #606266;
}

.log-remark {
  background-color: #f5f7fa;
  padding: 8px 12px;
  border-radius: 4px;
  color: #606266;
}

.log-remark .label {
  font-weight: bold;
  margin-right: 8px;
}

.empty-logs {
  padding: 30px 0;
}
</style> 