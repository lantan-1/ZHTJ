<template>
  <div class="register-approve-container">
    <div class="page-header">
      <div class="header-title">
        <h2>团籍注册审批</h2>
      </div>
    </div>

    <!-- 过滤器 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="批次名称">
          <el-select v-model="filterForm.batchId" placeholder="选择批次" clearable>
            <el-option 
              v-for="batch in batchOptions" 
              :key="batch.id" 
              :label="`${batch.year}年-${batch.batchCode}`" 
              :value="batch.id" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="团员姓名">
          <el-input v-model="filterForm.memberName" placeholder="团员姓名" clearable />
        </el-form-item>
        <el-form-item label="审批状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable>
            <el-option label="待审核" value="待审核" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已驳回" value="已驳回" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 审批统计 -->
    <el-row :gutter="20" class="statistics-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-title">待审批</div>
          <div class="stat-value">{{ statistics.pendingCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-title">已通过</div>
          <div class="stat-value">{{ statistics.approvedCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-title">已驳回</div>
          <div class="stat-value">{{ statistics.rejectedCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-title">审批进度</div>
          <div class="stat-progress">
            <el-progress :percentage="statistics.approvalProgress || 0" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 审批列表 -->
    <el-card class="approval-list-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>审批列表</span>
          <div>
            <el-button 
              type="success" 
              plain
              size="small"
              :disabled="!canBatchApprove"
              @click="batchApprove"
            >批量通过</el-button>
            <el-button 
              type="danger" 
              plain
              size="small"
              :disabled="!canBatchApprove"
              @click="batchReject"
            >批量驳回</el-button>
          </div>
        </div>
      </template>

      <el-table 
        :data="approvalList" 
        border 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="批次信息" width="150">
          <template #default="scope">
            {{ scope.row.batchYear }}年 - {{ scope.row.batchCode }}
          </template>
        </el-table-column>
        <el-table-column prop="memberName" label="团员姓名" width="120" />
        <el-table-column prop="memberCode" label="学号/工号" width="120" />
        <el-table-column prop="organizationName" label="所属组织" min-width="150" />
        <el-table-column prop="applyTime" label="申请时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="自我评价" min-width="250">
          <template #default="scope">
            <el-popover
              placement="top-start"
              width="300"
              trigger="hover"
            >
              <template #reference>
                <div class="evaluation-summary">
                  {{ scope.row.selfEvaluation ? scope.row.selfEvaluation.substring(0, 20) + '...' : '无' }}
                </div>
              </template>
              <div class="evaluation-detail">
                <p><strong>政治表现：</strong>{{ scope.row.politicalPerformance }}分</p>
                <p><strong>学习表现：</strong>{{ scope.row.studyPerformance }}分</p>
                <p><strong>工作表现：</strong>{{ scope.row.workPerformance }}分</p>
                <p><strong>活动参与：</strong>{{ scope.row.activityParticipation }}</p>
                <p><strong>团费缴纳：</strong>{{ scope.row.paidFees ? '已缴纳' : '未缴纳' }}</p>
                <p><strong>自我评价：</strong>{{ scope.row.selfEvaluation }}</p>
                <p v-if="scope.row.remarks"><strong>备注：</strong>{{ scope.row.remarks }}</p>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审批状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="approveLevel" label="当前环节" width="120">
          <template #default="scope">
            <el-tag type="info" v-if="scope.row.status === '待审核' && scope.row.branchApproveTime">团委审核</el-tag>
            <el-tag type="info" v-else-if="scope.row.status === '待审核'">团支部审核</el-tag>
            <el-tag type="success" v-else-if="scope.row.status === '已通过'">审核通过</el-tag>
            <el-tag type="danger" v-else-if="scope.row.status === '已驳回'">已驳回</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === '待审核' && canApprove(scope.row)"
              link 
              type="success" 
              @click="approve(scope.row)"
            >通过</el-button>
            <el-button 
              v-if="scope.row.status === '待审核' && canApprove(scope.row)"
              link 
              type="danger" 
              @click="reject(scope.row)"
            >驳回</el-button>
            <el-button 
              link 
              type="primary" 
              @click="viewDetail(scope.row)"
            >详情</el-button>
          </template>
        </el-table-column>
      </el-table>

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
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="approveDialogVisible"
      :title="approveForm.isReject ? '驳回申请' : '审批通过'"
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
            :placeholder="approveForm.isReject ? '请输入驳回原因' : '请输入审批意见（选填）'" 
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

    <!-- 批量审批对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      :title="batchForm.isReject ? '批量驳回' : '批量通过'"
      width="500px"
    >
      <div class="batch-info">已选择 {{ selectedRegisters.length }} 条申请</div>
      <el-form 
        ref="batchFormRef"
        :model="batchForm"
        :rules="batchRules"
        label-width="100px"
      >
        <el-form-item label="审批意见" prop="comments">
          <el-input 
            v-model="batchForm.comments" 
            type="textarea" 
            :rows="4" 
            :placeholder="batchForm.isReject ? '请输入驳回原因' : '请输入审批意见（选填）'" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBatchApprove">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../../stores/user';
import axios from 'axios';

const router = useRouter();
const userStore = useUserStore();
const approveFormRef = ref(null);
const batchFormRef = ref(null);

// 数据定义
const loading = ref(false);
const approveDialogVisible = ref(false);
const batchDialogVisible = ref(false);
const approvalList = ref([]);
const batchOptions = ref([]);
const selectedRegisters = ref([]);

const statistics = reactive({
  pendingCount: 0,
  approvedCount: 0,
  rejectedCount: 0,
  approvalProgress: 0
});

const page = reactive({
  current: 1,
  size: 10,
  total: 0
});

const filterForm = reactive({
  batchId: '',
  memberName: '',
  status: ''
});

// 审批表单
const approveForm = reactive({
  id: '',
  memberId: '',
  memberName: '',
  organizationName: '',
  comments: '',
  isReject: false
});

// 批量审批表单
const batchForm = reactive({
  ids: [],
  comments: '',
  isReject: false
});

// 表单验证规则
const approveRules = {
  comments: [
    { required: (form) => form.isReject, message: '请输入驳回原因', trigger: 'blur' }
  ]
};

const batchRules = {
  comments: [
    { required: (form) => form.isReject, message: '请输入驳回原因', trigger: 'blur' }
  ]
};

// 计算属性
const canBatchApprove = computed(() => {
  if (selectedRegisters.value.length === 0) return false;
  
  // 检查所有选中的记录是否都可以审批
  return selectedRegisters.value.every(register => canApprove(register));
});

// 方法
const loadApprovalList = async () => {
  loading.value = true;
  try {
    const params = {
      page: page.current,
      size: page.size,
      batchId: filterForm.batchId || undefined,
      memberName: filterForm.memberName || undefined,
      status: filterForm.status || undefined
    };

    const response = await axios.get('/api/register/approve', { params });
    if (response.data.success) {
      approvalList.value = response.data.data.list || [];
      page.total = response.data.data.total || 0;
    } else {
      ElMessage.error(response.data.message || '获取审批列表失败');
    }
  } catch (error) {
    console.error('加载审批列表失败:', error);
    ElMessage.error('加载审批列表失败');
  } finally {
    loading.value = false;
  }
};

const loadBatchOptions = async () => {
  try {
    const response = await axios.get('/api/register/batch/options');
    if (response.data.success) {
      batchOptions.value = response.data.data || [];
    } else {
      ElMessage.error(response.data.message || '获取批次选项失败');
    }
  } catch (error) {
    console.error('加载批次选项失败:', error);
    ElMessage.error('加载批次选项失败');
  }
};

const loadStatistics = async () => {
  try {
    const response = await axios.get('/api/register/approve/statistics');
    if (response.data.success) {
      Object.assign(statistics, response.data.data);
    } else {
      ElMessage.error(response.data.message || '获取审批统计数据失败');
    }
  } catch (error) {
    console.error('加载审批统计数据失败:', error);
    ElMessage.error('加载审批统计数据失败');
  }
};

const handleSearch = () => {
  page.current = 1;
  loadApprovalList();
};

const resetFilter = () => {
  filterForm.batchId = '';
  filterForm.memberName = '';
  filterForm.status = '';
  handleSearch();
};

const handleSizeChange = (val) => {
  page.size = val;
  loadApprovalList();
};

const handleCurrentChange = (val) => {
  page.current = val;
  loadApprovalList();
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

const getStatusType = (status) => {
  switch (status) {
    case '待审核': return 'warning';
    case '已通过': return 'success';
    case '已驳回': return 'danger';
    default: return 'info';
  }
};

// 判断是否可以审核
const canApprove = (register) => {
  // 团支部书记可以审核团支部级别的申请
  if (userStore.isBranchSecretary && !register.branchApproveTime) {
    return register.branchId === userStore.branchId;
  }
  
  // 团委书记可以审核团委级别的申请
  if (userStore.isCommitteeSecretary && register.branchApproveTime && !register.committeeApproveTime) {
    return register.committeeId === userStore.committeeId;
  }
  
  return false;
};

const approve = (register) => {
  approveForm.id = register.id;
  approveForm.memberId = register.memberId;
  approveForm.memberName = register.memberName;
  approveForm.organizationName = register.organizationName;
  approveForm.comments = '';
  approveForm.isReject = false;
  approveDialogVisible.value = true;
};

const reject = (register) => {
  approveForm.id = register.id;
  approveForm.memberId = register.memberId;
  approveForm.memberName = register.memberName;
  approveForm.organizationName = register.organizationName;
  approveForm.comments = '';
  approveForm.isReject = true;
  approveDialogVisible.value = true;
};

const submitApprove = async () => {
  if (!approveFormRef.value) return;
  
  try {
    await approveFormRef.value.validate();
    
    const data = {
      comments: approveForm.comments
    };
    
    if (approveForm.isReject) {
      const response = await axios.post(`/api/register/approve/${approveForm.id}/reject`, data);
      if (response.data.success) {
        ElMessage.success('申请已驳回');
        approveDialogVisible.value = false;
        loadApprovalList();
        loadStatistics();
      } else {
        ElMessage.error(response.data.message || '驳回申请失败');
      }
    } else {
      const response = await axios.post(`/api/register/approve/${approveForm.id}/approve`, data);
      if (response.data.success) {
        ElMessage.success('申请已通过');
        approveDialogVisible.value = false;
        loadApprovalList();
        loadStatistics();
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

const handleSelectionChange = (selection) => {
  selectedRegisters.value = selection;
};

const batchApprove = () => {
  if (selectedRegisters.value.length === 0) {
    ElMessage.warning('请选择需要审批的申请');
    return;
  }
  
  batchForm.ids = selectedRegisters.value.map(item => item.id);
  batchForm.comments = '';
  batchForm.isReject = false;
  batchDialogVisible.value = true;
};

const batchReject = () => {
  if (selectedRegisters.value.length === 0) {
    ElMessage.warning('请选择需要驳回的申请');
    return;
  }
  
  batchForm.ids = selectedRegisters.value.map(item => item.id);
  batchForm.comments = '';
  batchForm.isReject = true;
  batchDialogVisible.value = true;
};

const submitBatchApprove = async () => {
  if (!batchFormRef.value) return;
  
  try {
    await batchFormRef.value.validate();
    
    const data = {
      ids: batchForm.ids,
      comments: batchForm.comments
    };
    
    if (batchForm.isReject) {
      const response = await axios.post('/api/register/approve/batch-reject', data);
      if (response.data.success) {
        ElMessage.success('批量驳回成功');
        batchDialogVisible.value = false;
        loadApprovalList();
        loadStatistics();
      } else {
        ElMessage.error(response.data.message || '批量驳回失败');
      }
    } else {
      const response = await axios.post('/api/register/approve/batch-approve', data);
      if (response.data.success) {
        ElMessage.success('批量通过成功');
        batchDialogVisible.value = false;
        loadApprovalList();
        loadStatistics();
      } else {
        ElMessage.error(response.data.message || '批量通过失败');
      }
    }
  } catch (error) {
    console.error('提交批量审批失败:', error);
    if (error.message) {
      ElMessage.error(error.message);
    } else {
      ElMessage.error('提交批量审批失败');
    }
  }
};

const viewDetail = (register) => {
  router.push({
    path: `/dashboard/register/detail/${register.id}`
  });
};

// 生命周期钩子
onMounted(() => {
  loadApprovalList();
  loadBatchOptions();
  loadStatistics();
});
</script>

<style scoped>
.register-approve-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.statistics-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px 0;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
}

.stat-progress {
  padding: 0 20px;
  margin-top: 5px;
}

.approval-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.evaluation-summary {
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #409eff;
}

.evaluation-detail p {
  margin: 5px 0;
}

.batch-info {
  text-align: center;
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 