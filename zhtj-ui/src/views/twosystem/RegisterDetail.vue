<template>
  <div class="register-detail-container">
    <div class="page-header">
      <el-page-header @back="goBack" title="注册详情" />
    </div>

    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>申请信息</span>
          <el-tag :type="getStatusType(registerDetail.status)">{{ registerDetail.status }}</el-tag>
        </div>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="批次名称">{{ registerDetail.batchCode || '--' }}</el-descriptions-item>
        <el-descriptions-item label="注册年度">{{ registerDetail.batchYear || '--' }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusType(registerDetail.status)">{{ registerDetail.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDateTime(registerDetail.applyTime) || '--' }}</el-descriptions-item>
        <el-descriptions-item label="团支部审核时间">{{ formatDateTime(registerDetail.branchApproveTime) || '--' }}</el-descriptions-item>
        <el-descriptions-item label="团委审核时间">{{ formatDateTime(registerDetail.committeeApproveTime) || '--' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" :span="3">{{ formatDateTime(registerDetail.finishTime) || '--' }}</el-descriptions-item>
      </el-descriptions>

      <div class="step-container">
        <el-steps :active="getRegisterStep()" finish-status="success" align-center>
          <el-step title="申请注册" :description="formatDateTime(registerDetail.applyTime) || '未申请'" />
          <el-step title="团支部审核" :description="formatDateTime(registerDetail.branchApproveTime) || '未审核'" />
          <el-step title="团委审核" :description="formatDateTime(registerDetail.committeeApproveTime) || '未审核'" />
          <el-step title="注册完成" :description="formatDateTime(registerDetail.finishTime) || '未完成'" />
        </el-steps>
      </div>
    </el-card>

    <el-card class="member-card">
      <template #header>
        <div class="card-header">
          <span>团员信息</span>
        </div>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="姓名">{{ registerDetail.memberName }}</el-descriptions-item>
        <el-descriptions-item label="学号/工号">{{ registerDetail.memberCode }}</el-descriptions-item>
        <el-descriptions-item label="所属组织">{{ registerDetail.organizationName }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="evaluation-card">
      <template #header>
        <div class="card-header">
          <span>自我评价</span>
        </div>
      </template>

      <el-row :gutter="20" class="performance-row">
        <el-col :span="6">
          <div class="performance-item">
            <div class="performance-title">政治表现</div>
            <div class="performance-value">
              <el-rate 
                v-model="registerDetail.politicalPerformance" 
                disabled
                show-score
              />
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="performance-item">
            <div class="performance-title">学习表现</div>
            <div class="performance-value">
              <el-rate 
                v-model="registerDetail.studyPerformance" 
                disabled
                show-score
              />
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="performance-item">
            <div class="performance-title">工作表现</div>
            <div class="performance-value">
              <el-rate 
                v-model="registerDetail.workPerformance" 
                disabled
                show-score
              />
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="performance-item">
            <div class="performance-title">团费缴纳</div>
            <div class="performance-value">
              <el-tag :type="registerDetail.paidFees ? 'success' : 'danger'">
                {{ registerDetail.paidFees ? '已缴纳' : '未缴纳' }}
              </el-tag>
            </div>
          </div>
        </el-col>
      </el-row>

      <div class="evaluation-details">
        <div class="evaluation-item">
          <h4>活动参与情况</h4>
          <p>{{ registerDetail.activityParticipation || '--' }}</p>
        </div>
        <div class="evaluation-item">
          <h4>自我评价</h4>
          <p>{{ registerDetail.selfEvaluation || '--' }}</p>
        </div>
        <div class="evaluation-item">
          <h4>备注说明</h4>
          <p>{{ registerDetail.remarks || '无' }}</p>
        </div>
        <div class="evaluation-item" v-if="registerDetail.status === '已驳回'">
          <h4>驳回原因</h4>
          <p class="rejection-reason">{{ registerDetail.rejectionReason || '--' }}</p>
        </div>
      </div>
    </el-card>

    <div class="action-buttons" v-if="showApprovalButtons">
      <el-button type="success" @click="approve">通过</el-button>
      <el-button type="danger" @click="reject">驳回</el-button>
    </div>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      :title="isReject ? '驳回申请' : '审批通过'"
      width="500px"
    >
      <el-form 
        ref="approveFormRef"
        :model="approveForm"
        :rules="approveRules"
        label-width="100px"
      >
        <el-form-item label="团员姓名">
          <el-input v-model="approveForm.memberName" disabled />
        </el-form-item>
        <el-form-item label="所属组织">
          <el-input v-model="approveForm.organizationName" disabled />
        </el-form-item>
        <el-form-item label="审批意见" prop="comments">
          <el-input 
            v-model="approveForm.comments" 
            type="textarea" 
            :rows="4" 
            :placeholder="isReject ? '请输入驳回原因' : '请输入审批意见（选填）'" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="approveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApprove">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../../stores/user';
import axios from 'axios';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const approveFormRef = ref(null);

// 数据定义
const loading = ref(false);
const approveDialogVisible = ref(false);
const isReject = ref(false);
const registerId = ref(route.params.id);
const registerDetail = ref({});

// 审批表单
const approveForm = reactive({
  memberId: '',
  memberName: '',
  organizationName: '',
  comments: ''
});

// 表单验证规则
const approveRules = {
  comments: [
    { required: (form) => isReject.value, message: '请输入驳回原因', trigger: 'blur' }
  ]
};

// 计算属性
const showApprovalButtons = computed(() => {
  // 判断是否可以审核
  if (registerDetail.value.status !== '待审核') {
    return false;
  }
  
  // 团支部书记可以审核团支部级别的申请
  if (userStore.isBranchSecretary && !registerDetail.value.branchApproveTime) {
    return registerDetail.value.branchId === userStore.branchId;
  }
  
  // 团委书记可以审核团委级别的申请
  if (userStore.isCommitteeSecretary && registerDetail.value.branchApproveTime && !registerDetail.value.committeeApproveTime) {
    return registerDetail.value.committeeId === userStore.committeeId;
  }
  
  return false;
});

// 方法
const loadRegisterDetail = async () => {
  loading.value = true;
  try {
    const response = await axios.get(`/api/register/detail/${registerId.value}`);
    if (response.data.success) {
      registerDetail.value = response.data.data || {};
    } else {
      ElMessage.error(response.data.message || '获取注册详情失败');
    }
  } catch (error) {
    console.error('加载注册详情失败:', error);
    ElMessage.error('加载注册详情失败');
  } finally {
    loading.value = false;
  }
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

const getStatusType = (status) => {
  switch (status) {
    case '未申请': return 'info';
    case '待审核': return 'warning';
    case '已通过': return 'success';
    case '已驳回': return 'danger';
    case '已完成': return 'success';
    default: return 'info';
  }
};

const getRegisterStep = () => {
  if (!registerDetail.value) return 0;
  
  switch (registerDetail.value.status) {
    case '未申请': return 0;
    case '待审核':
      if (registerDetail.value.branchApproveTime) {
        return 2; // 团支部已审核，等待团委审核
      }
      return 1; // 等待团支部审核
    case '已通过': return 3;
    case '已驳回': return 0;
    case '已完成': return 4;
    default: return 0;
  }
};

const goBack = () => {
  router.back();
};

const approve = () => {
  isReject.value = false;
  approveForm.memberId = registerDetail.value.memberId;
  approveForm.memberName = registerDetail.value.memberName;
  approveForm.organizationName = registerDetail.value.organizationName;
  approveForm.comments = '';
  approveDialogVisible.value = true;
};

const reject = () => {
  isReject.value = true;
  approveForm.memberId = registerDetail.value.memberId;
  approveForm.memberName = registerDetail.value.memberName;
  approveForm.organizationName = registerDetail.value.organizationName;
  approveForm.comments = '';
  approveDialogVisible.value = true;
};

const submitApprove = async () => {
  if (!approveFormRef.value) return;
  
  try {
    await approveFormRef.value.validate();
    
    const data = {
      comments: approveForm.comments
    };
    
    if (isReject.value) {
      const response = await axios.post(`/api/register/approve/${registerId.value}/reject`, data);
      if (response.data.success) {
        ElMessage.success('申请已驳回');
        approveDialogVisible.value = false;
        loadRegisterDetail();
      } else {
        ElMessage.error(response.data.message || '驳回申请失败');
      }
    } else {
      const response = await axios.post(`/api/register/approve/${registerId.value}/approve`, data);
      if (response.data.success) {
        ElMessage.success('申请已通过');
        approveDialogVisible.value = false;
        loadRegisterDetail();
      } else {
        ElMessage.error(response.data.message || '通过申请失败');
      }
    }
  } catch (error) {
    console.error('提交审批失败:', error);
    if (error.message) {
      ElMessage.error(error.message);
    } else {
      ElMessage.error('提交审批失败');
    }
  }
};

// 生命周期钩子
onMounted(() => {
  loadRegisterDetail();
});
</script>

<style scoped>
.register-detail-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.detail-card,
.member-card,
.evaluation-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.step-container {
  margin-top: 30px;
}

.performance-row {
  margin-top: 20px;
  margin-bottom: 20px;
}

.performance-item {
  text-align: center;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.performance-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.evaluation-details {
  background-color: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
}

.evaluation-item {
  margin-bottom: 15px;
}

.evaluation-item h4 {
  font-size: 16px;
  margin-bottom: 8px;
  font-weight: 500;
}

.evaluation-item p {
  margin: 0;
  line-height: 1.6;
}

.rejection-reason {
  color: #f56c6c;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style> 