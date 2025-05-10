<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { ElMessage } from 'element-plus';
import { 
  getTransferDetail, 
  getStatusText, 
  getStatusColor
} from '../../api/transfer';
import type { TransferDetail as TransferDetailType } from '../../api/transfer';
import { ArrowLeft } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);

// 转接申请ID
const transferId = computed(() => Number(route.params.id));

// 转接申请详情
const transferDetail = ref<TransferDetailType | null>(null);

// 加载转接申请详情
const loadTransferDetail = async () => {
  if (!transferId.value) {
    ElMessage.error('转接申请ID无效');
    return;
  }
  
  loading.value = true;
  try {
    console.log('开始加载转接申请详情, ID:', transferId.value);
    const response = await getTransferDetail(transferId.value);
    console.log('获取到转接申请详情响应:', response);
    
    // 处理后端返回结果，支持多种返回格式
    if (response && (response.code === 200 || response.success)) {
      // 确保后端返回的数据完整性
      transferDetail.value = response.data;
      console.log('处理后的转接申请详情:', transferDetail.value);
      
      // 额外检查：如果response.data为null或undefined，则说明找不到记录
      if (!transferDetail.value) {
        console.error('转接申请详情数据为空');
        ElMessage.error('未找到转接申请详情');
        return;
      }
      
      // 数据规范化处理：确保所有必要字段都有值
      // 1. 确保状态文本字段存在
      if (!transferDetail.value.status && transferDetail.value.statusCode !== undefined) {
        transferDetail.value.status = getStatusText(transferDetail.value.statusCode);
      }
      
      // 2. 处理组织名称
      if (!transferDetail.value.transferOutOrgName && transferDetail.value.transferOutOrg) {
        if (typeof transferDetail.value.transferOutOrg === 'string') {
          transferDetail.value.transferOutOrgName = transferDetail.value.transferOutOrg;
        } else if (transferDetail.value.transferOutOrg && transferDetail.value.transferOutOrg.name) {
          transferDetail.value.transferOutOrgName = transferDetail.value.transferOutOrg.name;
        }
      }
      
      if (!transferDetail.value.transferInOrgName && transferDetail.value.transferInOrg) {
        if (typeof transferDetail.value.transferInOrg === 'string') {
          transferDetail.value.transferInOrgName = transferDetail.value.transferInOrg;
        } else if (transferDetail.value.transferInOrg && transferDetail.value.transferInOrg.name) {
          transferDetail.value.transferInOrgName = transferDetail.value.transferInOrg.name;
        }
      }
    } else {
      console.error('获取转接申请详情失败:', response);
      ElMessage.error(response?.message || '获取转接申请详情失败');
    }
  } catch (error) {
    console.error('获取转接申请详情失败', error);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 返回申请列表
const goBack = () => {
  router.push('/dashboard/transfers');
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
      minute: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
};

// 获取审批结果文本
const getApprovalText = (approved: boolean | null) => {
  if (approved === null) return '未审批';
  return approved ? '已通过' : '已拒绝';
};

// 获取审批结果类型
const getApprovalType = (approved: boolean | null) => {
  if (approved === null) return '';
  return approved ? 'success' : 'danger';
};

// 转接申请状态文本颜色
const statusColor = computed(() => {
  if (!transferDetail.value) return '#909399';
  return getStatusColor(transferDetail.value.statusCode);
});

// 时间轴步骤激活状态
const isStepActive = (step: number) => {
  if (!transferDetail.value) return false;
  const { statusCode } = transferDetail.value;
  
  // 根据转接状态判断步骤是否激活
  switch (step) {
    case 1: // 申请提交
      return true;
    case 2: // 转出审批
      return statusCode >= 1;
    case 3: // 转入审批
      return statusCode >= 2;
    case 4: // 申请完成
      return statusCode >= 3;
    default:
      return false;
  }
};

// 时间轴步骤状态
const getStepStatus = (step: number) => {
  if (!transferDetail.value) return 'wait';
  const { statusCode, outApproved, inApproved } = transferDetail.value;
  
  // 判断各步骤的状态
  switch (step) {
    case 1: // 申请提交
      return statusCode === 4 ? 'error' : 'success';
    case 2: // 转出审批
      if (!isStepActive(2)) return 'wait';
      return outApproved === false ? 'error' : 'success';
    case 3: // 转入审批
      if (!isStepActive(3)) return 'wait';
      return inApproved === false ? 'error' : 'success';
    case 4: // 申请完成
      if (!isStepActive(4)) return 'wait';
      return statusCode === 3 ? 'success' : 'error';
    default:
      return 'wait';
  }
};

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

// 查看审批日志
const viewApprovalLogs = () => {
  router.push(`/dashboard/transfers/logs/${transferId.value}`);
};

// 获取步骤图标类名
const getStepIconClass = (step: number) => {
  if (!transferDetail.value) return 'wait';
  return getStepStatus(step);
};

// 获取步骤图标
const getStepIcon = (step: number) => {
  // 这个函数可以返回不同的图标名称，但我们使用自定义模板，所以返回空字符串
  return '';
};

// 组件加载时获取转接申请详情
onMounted(() => {
  loadTransferDetail();
});
</script>

<template>
  <div class="transfer-detail-container">
    <el-card class="transfer-detail-card" v-loading="loading">
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
            <h2>转接申请详情</h2>
          </div>
          <div class="header-right" v-if="transferDetail">
            <el-tag 
              :style="{ color: statusColor }"
              :type="getStatusType(transferDetail.statusCode)"
              size="large"
            >
              {{ transferDetail.status }}
            </el-tag>
          </div>
        </div>
      </template>
      
      <!-- 没有找到数据时显示提示 -->
      <div v-if="!transferDetail && !loading" class="no-data">
        <el-empty 
          description="未找到转接申请详情" 
          :image-size="200"
        >
          <el-button type="primary" @click="goBack">返回列表</el-button>
        </el-empty>
      </div>
      
      <div v-if="transferDetail" class="transfer-detail-content">
        <!-- 申请基本信息 -->
        <el-descriptions 
          class="info-section" 
          title="申请基本信息" 
          :column="2" 
          border
        >
          <template #extra>
            <el-button 
              type="primary" 
              size="small"
              @click="viewApprovalLogs" 
              v-if="transferDetail && transferDetail.statusCode >= 1"
            >
              查看审批日志
            </el-button>
          </template>
          <el-descriptions-item label="申请人">{{ transferDetail.transferUserName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="转出组织">{{ transferDetail.transferOutOrgName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="转入组织">{{ transferDetail.transferInOrgName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDateTime(transferDetail.applicationTime) }}</el-descriptions-item>
          <el-descriptions-item label="过期时间">{{ formatDateTime(transferDetail.expireTime) }}</el-descriptions-item>
          <el-descriptions-item label="转接理由">{{ transferDetail.transferReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注信息">{{ transferDetail.transferRemark || '-' }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 审批信息 -->
        <el-descriptions 
          class="info-section" 
          title="审批信息" 
          :column="2" 
          border
        >
          <el-descriptions-item label="转出审批人">{{ transferDetail.outApproverName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="转出审批时间">{{ formatDateTime(transferDetail.outApprovalTime) }}</el-descriptions-item>
          <el-descriptions-item label="转出审批结果">
            <el-tag 
              :type="getApprovalType(transferDetail.outApproved)"
              v-if="transferDetail.outApprovalTime"
            >
              {{ getApprovalText(transferDetail.outApproved) }}
            </el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="转出审批备注">{{ transferDetail.outApprovalRemark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="转入审批人">{{ transferDetail.inApproverName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="转入审批时间">{{ formatDateTime(transferDetail.inApprovalTime) }}</el-descriptions-item>
          <el-descriptions-item label="转入审批结果">
            <el-tag 
              :type="getApprovalType(transferDetail.inApproved)"
              v-if="transferDetail.inApprovalTime"
            >
              {{ getApprovalText(transferDetail.inApproved) }}
            </el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="转入审批备注">{{ transferDetail.inApprovalRemark || '-' }}</el-descriptions-item>
        </el-descriptions>
        
        <!-- 美化的进度时间轴 -->
        <div class="timeline-section">
          <h3>申请进度</h3>
          <div class="progress-container">
            <el-steps 
              :active="transferDetail.statusCode + 1" 
              finish-status="success"
              class="transfer-steps"
              process-status="process"
              align-center
            >
              <el-step 
                title="申请提交" 
                :status="getStepStatus(1)"
                :description="formatDateTime(transferDetail.applicationTime)"
                :icon="getStepIcon(1)"
              >
                <template #icon>
                  <div class="custom-step-icon" :class="getStepIconClass(1)">
                    1
                  </div>
                </template>
              </el-step>
              <el-step 
                title="转出审批" 
                :status="getStepStatus(2)"
                :description="transferDetail.outApprovalTime ? 
                  formatDateTime(transferDetail.outApprovalTime) : '等待审批'"
                :icon="getStepIcon(2)"
              >
                <template #icon>
                  <div class="custom-step-icon" :class="getStepIconClass(2)">
                    2
                  </div>
                </template>
              </el-step>
              <el-step 
                title="转入审批" 
                :status="getStepStatus(3)"
                :description="transferDetail.inApprovalTime ? 
                  formatDateTime(transferDetail.inApprovalTime) : '等待审批'"
                :icon="getStepIcon(3)"
              >
                <template #icon>
                  <div class="custom-step-icon" :class="getStepIconClass(3)">
                    3
                  </div>
                </template>
              </el-step>
              <el-step 
                title="申请完成" 
                :status="getStepStatus(4)"
                :description="transferDetail.statusCode >= 3 ? 
                  (transferDetail.statusCode === 3 ? '转接成功' : '转接失败') : '等待完成'"
                :icon="getStepIcon(4)"
              >
                <template #icon>
                  <div class="custom-step-icon" :class="getStepIconClass(4)">
                    4
                  </div>
                </template>
              </el-step>
            </el-steps>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.transfer-detail-container {
  padding: 20px;
}

.transfer-detail-card {
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

.transfer-detail-content {
  padding: 20px 0;
}

.info-section {
  margin-bottom: 30px;
}

.timeline-section {
  margin-top: 40px;
}

.timeline-section h3 {
  font-size: 16px;
  color: #303133;
  margin-bottom: 20px;
  font-weight: 500;
}

.transfer-steps {
  padding: 20px 0;
}

.no-data {
  padding: 40px 0;
  text-align: center;
  color: #909399;
  font-size: 14px;
}

/* 美化进度条样式 */
.progress-container {
  background: linear-gradient(135deg, #f8f9fa 0%, #eaedf1 100%);
  border-radius: 16px;
  padding: 35px 25px 25px;
  margin: 0 auto;
  box-shadow: 0 5px 30px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
  position: relative;
  overflow: hidden;
}

.progress-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c, #f56c6c);
  opacity: 0.8;
  animation: gradientShift 8s ease infinite;
  background-size: 300% 100%;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 完全重置和隐藏 Element Plus 的默认样式 */
:deep(.el-step__icon),
:deep(.el-step__icon-inner) {
  background-color: transparent !important;
  border: none !important;
  box-shadow: none !important;
}

:deep(.el-step__head) {
  border: none !important;
  background-color: transparent !important;
}

:deep(.el-step__head.is-text) {
  border: none !important;
  background-color: transparent !important;
}

:deep(.el-step__main) {
  background-color: transparent !important;
}

/* 使线条透明，然后用自定义样式来代替 */
.transfer-steps :deep(.el-step__line) {
  height: 2px !important;
  background-color: #e0e0e0 !important;
  margin: 0 !important;
  top: 11px !important; /* 微调线条位置 */
  z-index: 1;
  opacity: 1;
}

.transfer-steps :deep(.el-step__line-inner) {
  display: block !important;
  height: 100%;
  background: linear-gradient(90deg, #409eff, #67c23a);
  transition: all 0.3s;
}

/* 调整步骤组件整体样式 */
.transfer-steps :deep(.el-steps) {
  padding-top: 20px;
}

.transfer-steps :deep(.el-step) {
  padding-top: 0 !important;
  margin-top: 0 !important;
}

.transfer-steps :deep(.el-step__head) {
  padding-bottom: 8px;
}

/* 修改图标的父容器定位 */
.transfer-steps :deep(.el-step__icon-inner) {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 自定义步骤图标样式 */
.custom-step-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 36px; /* 微调图标尺寸 */
  height: 36px; /* 微调图标尺寸 */
  border-radius: 50%;
  font-weight: bold;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.4s ease;
  position: relative;
  border: 2px solid transparent;
  z-index: 3; /* 确保在线条上层 */
  margin: 0 auto;
  background-color: #fff; /* 确保图标有背景色遮挡线条 */
}

.custom-step-icon::after {
  content: '';
  position: absolute;
  inset: -6px;
  border-radius: 50%;
  background: transparent;
  z-index: -1;
  transition: all 0.4s ease;
}

.custom-step-icon.wait {
  background-color: #ffffff;
  color: #909399;
  border: 2px solid #e0e0e0;
}

.custom-step-icon.process {
  background-color: #409eff;
  color: #ffffff;
  transform: scale(1.15);
  box-shadow: 0 5px 15px rgba(64, 158, 255, 0.4);
}

.custom-step-icon.process::after {
  background: linear-gradient(45deg, rgba(64, 158, 255, 0.3), rgba(64, 158, 255, 0.1));
  animation: pulse 2s infinite;
}

.custom-step-icon.success {
  background-color: #67c23a;
  color: #ffffff;
  box-shadow: 0 5px 15px rgba(103, 194, 58, 0.4);
}

.custom-step-icon.success::after {
  background: linear-gradient(45deg, rgba(103, 194, 58, 0.3), rgba(103, 194, 58, 0.1));
}

.custom-step-icon.error {
  background-color: #f56c6c;
  color: #ffffff;
  box-shadow: 0 5px 15px rgba(245, 108, 108, 0.4);
}

.custom-step-icon.error::after {
  background: linear-gradient(45deg, rgba(245, 108, 108, 0.3), rgba(245, 108, 108, 0.1));
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
  100% { transform: scale(1); opacity: 1; }
}

/* 步骤标题样式改进 */
:deep(.el-step__title) {
  font-weight: 600;
  font-size: 15px;
  margin-top: 10px;
  transition: all 0.3s ease;
}

:deep(.el-step__title.is-process) {
  color: #409eff;
  transform: translateY(-2px);
  text-shadow: 0 0 1px rgba(64, 158, 255, 0.2);
}

:deep(.el-step__title.is-success) {
  color: #67c23a;
  text-shadow: 0 0 1px rgba(103, 194, 58, 0.2);
}

:deep(.el-step__title.is-error) {
  color: #f56c6c;
  text-shadow: 0 0 1px rgba(245, 108, 108, 0.2);
}

:deep(.el-step__title.is-wait) {
  color: #909399;
}

:deep(.el-step__description) {
  font-size: 13px;
  color: #606266;
  margin-top: 5px;
  transition: all 0.3s ease;
}

:deep(.el-step__description.is-process) {
  color: #409eff;
}

:deep(.el-step__description.is-success) {
  color: #67c23a;
}

:deep(.el-step__description.is-error) {
  color: #f56c6c;
}
</style> 