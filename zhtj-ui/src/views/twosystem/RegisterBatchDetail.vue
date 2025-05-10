<template>
  <div class="register-batch-detail-container">
    <div class="page-header">
      <div class="header-title">
        <el-page-header @back="goBack" title="注册批次详情" />
      </div>
      <div class="header-actions">
        <el-button 
          v-if="batchDetail.status === '进行中'" 
          type="warning" 
          @click="endBatch"
        >结束批次</el-button>
        <el-button 
          v-if="batchDetail.status === '未开始'" 
          type="primary" 
          @click="editBatch"
        >编辑批次</el-button>
        <el-button 
          v-if="batchDetail.status === '未开始'" 
          type="success" 
          @click="startBatch"
        >开始批次</el-button>
      </div>
    </div>

    <el-card class="batch-info-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>批次信息</span>
          <el-tag :type="getBatchStatusType(batchDetail.status)">{{ batchDetail.status }}</el-tag>
        </div>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="批次名称">{{ batchDetail.batchCode || '--' }}</el-descriptions-item>
        <el-descriptions-item label="注册年度">{{ batchDetail.year || '--' }}</el-descriptions-item>
        <el-descriptions-item label="批次状态">
          <el-tag :type="getBatchStatusType(batchDetail.status)">{{ batchDetail.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="批次标题" :span="3">{{ batchDetail.title || '--' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="3">
          {{ formatDate(batchDetail.startDate) || '--' }} 至 {{ formatDate(batchDetail.endDate) || '--' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(batchDetail.createTime) || '--' }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ batchDetail.creatorName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="最后更新时间">{{ formatDateTime(batchDetail.updateTime) || '--' }}</el-descriptions-item>
      </el-descriptions>

      <div class="batch-description" v-if="batchDetail.description">
        <h4>批次说明</h4>
        <div class="description-content">{{ batchDetail.description }}</div>
      </div>

      <div class="progress-section">
        <h4>注册进度</h4>
        <div class="progress-bar">
          <el-progress :percentage="batchDetail.progressPercentage || 0" />
          <div class="progress-text">
            {{ batchDetail.registeredCount || 0 }}/{{ batchDetail.totalCount || 0 }}
          </div>
        </div>
      </div>
    </el-card>

    <el-card class="members-card">
      <template #header>
        <div class="card-header">
          <span>团员注册情况</span>
          <div class="filter-actions">
            <el-input 
              v-model="memberFilter.keyword" 
              placeholder="搜索姓名/学号" 
              style="width: 200px; margin-right: 10px;" 
              clearable
              @keyup.enter="loadMembers"
            />
            <el-select 
              v-model="memberFilter.status" 
              placeholder="状态筛选" 
              style="width: 120px; margin-right: 10px;" 
              clearable
              @change="loadMembers"
            >
              <el-option label="未申请" value="未申请" />
              <el-option label="待审核" value="待审核" />
              <el-option label="已通过" value="已通过" />
              <el-option label="已驳回" value="已驳回" />
            </el-select>
            <el-button type="primary" @click="loadMembers">查询</el-button>
          </div>
        </div>
      </template>

      <el-table :data="memberList" border style="width: 100%" v-loading="membersLoading">
        <el-table-column prop="memberName" label="姓名" width="120" />
        <el-table-column prop="memberCode" label="学号/工号" width="120" />
        <el-table-column prop="organizationName" label="所属组织" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.applyTime) || '--' }}
          </template>
        </el-table-column>
        <el-table-column prop="finishTime" label="完成时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.finishTime) || '--' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button 
              link 
              type="primary" 
              @click="showStatusChangeDialog(scope.row)"
              :disabled="scope.row.status === '已通过'"
            >{{ getButtonText(scope.row.status) }}</el-button>
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

    <!-- 编辑批次对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑注册批次"
      width="650px"
    >
      <el-form 
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="批次标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入批次标题" />
        </el-form-item>
        <el-form-item label="注册年度" prop="year">
          <el-select v-model="form.year" placeholder="请选择注册年度" style="width: 100%">
            <el-option 
              v-for="year in yearOptions" 
              :key="year" 
              :label="year" 
              :value="year" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="注册时间" prop="dateRange">
          <el-date-picker
            v-model="form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="注册说明" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入注册说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改注册状态对话框 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="修改注册状态"
      width="500px"
    >
      <el-form 
        ref="statusFormRef"
        :model="statusForm"
        label-width="100px"
      >
        <el-form-item label="团员姓名">
          <span>{{ currentMember?.memberName || '' }}</span>
        </el-form-item>
        <el-form-item label="学号/工号">
          <span>{{ currentMember?.memberCode || '' }}</span>
        </el-form-item>
        <el-form-item label="所属组织">
          <span>{{ currentMember?.organizationName || '' }}</span>
        </el-form-item>
        <el-form-item label="当前状态">
          <el-tag :type="getStatusType(currentMember?.status)">
            {{ currentMember?.status || '' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="注册状态" prop="status">
          <el-select v-model="statusForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待审核" value="待审核" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已驳回" value="已驳回" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="comment">
          <el-input 
            v-model="statusForm.comment" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入备注说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatusChange">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getBatchDetail, startBatch as apiStartBatch, finishBatch as apiFinishBatch, getBatchMembers, approveRegister, rejectRegister, updateRegisterStatus } from '../../api/register';
import api from '../../api';

const route = useRoute();
const router = useRouter();
const formRef = ref(null);
const statusFormRef = ref(null);

// 数据定义
const loading = ref(false);
const membersLoading = ref(false);
const dialogVisible = ref(false);
const statusDialogVisible = ref(false);
const batchId = ref(route.params.id);
const batchDetail = ref({});
const memberList = ref([]);
const currentMember = ref(null);

// 年度选项
const currentYear = new Date().getFullYear();
const yearOptions = ref([
  (currentYear - 1).toString(),
  currentYear.toString(),
  (currentYear + 1).toString()
]);

// 分页数据
const page = reactive({
  current: 1,
  size: 10,
  total: 0
});

// 团员筛选
const memberFilter = reactive({
  keyword: '',
  status: ''
});

// 表单数据
const form = reactive({
  id: '',
  title: '',
  year: '',
  dateRange: [],
  description: ''
});

// 状态修改表单
const statusForm = reactive({
  id: null,
  status: '',
  comment: ''
});

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入批次标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  year: [
    { required: true, message: '请选择注册年度', trigger: 'change' }
  ],
  dateRange: [
    { required: true, message: '请选择注册时间', trigger: 'change' }
  ]
};

// 页面加载
onMounted(() => {
  loadBatchDetail();
  loadMembers();
});

// 加载批次详情
const loadBatchDetail = async () => {
  loading.value = true;
  try {
    const response = await getBatchDetail(batchId.value);
    if (response.code === 200) {
      batchDetail.value = response.data || {};
    } else {
      ElMessage.error(response.message || '获取批次详情失败');
    }
  } catch (error) {
    console.error('加载批次详情失败:', error);
    ElMessage.error('加载批次详情失败');
  } finally {
    loading.value = false;
  }
};

// 加载批次成员列表
const loadMembers = async () => {
  membersLoading.value = true;
  try {
    // 使用专用API获取当前批次的成员列表
    const response = await getBatchMembers(Number(batchId.value), {
      page: page.current,
      size: page.size,
      keyword: memberFilter.keyword,
      status: memberFilter.status
    });
    
    console.log('批次成员响应:', response);
    
    // 处理响应数据
    if (response.data && response.data.list && response.code === 200) {
      memberList.value = response.data.list;
      page.total = response.data.total || 0;
    } else {
      memberList.value = [];
      page.total = 0;
    }
  } catch (error) {
    console.error('加载批次成员出错:', error);
    ElMessage.error('加载批次成员列表失败');
    memberList.value = [];
  } finally {
    membersLoading.value = false;
  }
};

// 返回上一页
const goBack = () => {
  router.push('/dashboard/register/batch');
};

// 编辑批次
const editBatch = () => {
  // 设置表单初始值
  form.id = batchDetail.value.id;
  form.title = batchDetail.value.title;
  form.year = batchDetail.value.year;
  form.description = batchDetail.value.description;
  
  // 设置日期范围
  if (batchDetail.value.startDate && batchDetail.value.endDate) {
    form.dateRange = [
      new Date(batchDetail.value.startDate),
      new Date(batchDetail.value.endDate)
    ];
  }
  
  dialogVisible.value = true;
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const data = {
          id: form.id,
          title: form.title,
          year: form.year,
          startDate: form.dateRange[0].toISOString().split('T')[0],
          endDate: form.dateRange[1].toISOString().split('T')[0],
          description: form.description
        };
        
        const response = await api.put(`/api/register/batch/${form.id}`, data);
        if (response.code === 200) {
          ElMessage.success('批次更新成功');
          dialogVisible.value = false;
          loadBatchDetail();
        } else {
          ElMessage.error(response.message || '批次更新失败');
        }
      } catch (error) {
        console.error('批次更新失败:', error);
        ElMessage.error('批次更新失败');
      }
    }
  });
};

// 显示修改注册状态对话框
const showStatusChangeDialog = (member) => {
  currentMember.value = member;
  statusForm.id = member.id;
  statusForm.status = member.status;
  statusForm.comment = '';
  
  statusDialogVisible.value = true;
};

// 提交修改注册状态
const submitStatusChange = async () => {
  try {
    let response;
    
    // 根据选择的状态调用不同的API
    if (statusForm.status === '已通过') {
      response = await approveRegister(statusForm.id, statusForm.comment);
    } else if (statusForm.status === '已驳回') {
      response = await rejectRegister(statusForm.id, statusForm.comment);
    } else {
      // 其他状态使用通用更新API
      response = await updateRegisterStatus(statusForm.id, statusForm.status, statusForm.comment);
    }
    
    if (response.code === 200) {
      ElMessage.success('注册状态更新成功');
      statusDialogVisible.value = false;
      loadMembers(); // 刷新列表
    } else {
      ElMessage.error(response.message || '注册状态更新失败');
    }
  } catch (error) {
    console.error('注册状态更新失败:', error);
    ElMessage.error('注册状态更新失败');
  }
};

// 开始批次
const startBatch = async () => {
  ElMessageBox.confirm('确定要开始此注册批次吗？开始后批次信息将不可修改。', '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await apiStartBatch(batchId.value);
      if (response.code === 200) {
        ElMessage.success('批次已开始');
        loadBatchDetail();
      } else {
        ElMessage.error(response.message || '操作失败');
      }
    } catch (error) {
      console.error('开始批次失败:', error);
      ElMessage.error('开始批次失败');
    }
  }).catch(() => {});
};

// 结束批次
const endBatch = async () => {
  ElMessageBox.confirm('确定要结束此注册批次吗？结束后团员将不能再提交注册申请。', '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await apiFinishBatch(batchId.value);
      if (response.code === 200) {
        ElMessage.success('批次已结束');
        loadBatchDetail();
      } else {
        ElMessage.error(response.message || '操作失败');
      }
    } catch (error) {
      console.error('结束批次失败:', error);
      ElMessage.error('结束批次失败');
    }
  }).catch(() => {});
};

// 分页处理
const handleSizeChange = (size) => {
  page.size = size;
  loadMembers();
};

const handleCurrentChange = (current) => {
  page.current = current;
  loadMembers();
};

// 辅助方法
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString();
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

const getBatchStatusType = (status) => {
  switch (status) {
    case '未开始': return 'info';
    case '进行中': return 'success';
    case '已结束': return 'warning';
    default: return 'info';
  }
};

const getStatusType = (status) => {
  switch (status) {
    case '未申请': return 'info';
    case '待审核': return 'warning';
    case '已通过': return 'success';
    case '已驳回': return 'danger';
    default: return 'info';
  }
};

// 获取按钮文本
const getButtonText = (status) => {
  switch(status) {
    case '已通过': return '已注册';
    case '待审核': return '审核中';
    case '已驳回': return '重新注册';
    default: return '注册';
  }
};
</script>

<style scoped>
.register-batch-detail-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-info-card,
.members-card {
  margin-bottom: 20px;
}

.batch-description {
  margin-top: 20px;
}

.description-content {
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
  white-space: pre-line;
}

.progress-section {
  margin-top: 20px;
}

.progress-bar {
  margin-top: 10px;
}

.progress-text {
  text-align: center;
  margin-top: 5px;
  font-size: 14px;
  color: #606266;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.filter-actions {
  display: flex;
  align-items: center;
}
</style> 