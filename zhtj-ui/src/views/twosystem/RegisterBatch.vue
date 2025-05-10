<template>
  <div class="register-batch-container">
    <div class="page-header">
      <div class="header-title">
        <el-page-header @back="goBack" title="团籍注册批次管理" />
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleCreateBatch">创建新批次</el-button>
      </div>
    </div>

    <!-- 过滤器 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="批次名称">
          <el-input v-model="filterForm.batchName" placeholder="批次名称" clearable class="filter-select"/>
        </el-form-item>
        <el-form-item label="注册年度">
          <el-select v-model="filterForm.registerYear" placeholder="选择年度" clearable class="filter-select">
            <el-option 
              v-for="year in yearOptions" 
              :key="year" 
              :label="year" 
              :value="year" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="批次状态">
          <el-select v-model="filterForm.status" placeholder="选择状态" clearable class="filter-select">
            <el-option label="未开始" value="未开始" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 批次列表 -->
    <el-card class="batch-list-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>注册批次列表</span>
        </div>
      </template>

      <el-table :data="batchList" border style="width: 100%">
        <el-table-column prop="batchCode" label="批次名称" width="120" />
        <el-table-column prop="registerYear" label="注册年度" width="100" />
        <el-table-column prop="batchName" label="批次标题" min-width="150" />
        <el-table-column label="批次时间" min-width="220">
          <template #default="scope">
            {{ formatDate(scope.row.startTime) }} 至 {{ formatDate(scope.row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getBatchStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="进度" width="150">
          <template #default="scope">
            <div class="progress-wrapper">
              <el-progress :percentage="scope.row.progressPercentage || 0" />
              <div class="progress-text">
                {{ scope.row.registeredCount || 0 }}/{{ scope.row.totalCount || 0 }}
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button 
              link 
              type="primary" 
              @click="viewDetail(scope.row.id)"
            >查看</el-button>
            <el-button 
              v-if="scope.row.status === '未开始'"
              link 
              type="success" 
              @click="startBatch(scope.row.id)"
            >开始</el-button>
            <el-button 
              v-if="scope.row.status === '进行中'"
              link 
              type="warning" 
              @click="endBatch(scope.row.id)"
            >结束</el-button>
            <el-button 
              v-if="scope.row.status === '未开始'"
              link 
              type="primary" 
              @click="editBatch(scope.row)"
            >编辑</el-button>
            <el-button 
              v-if="scope.row.status === '未开始'"
              link 
              type="danger" 
              @click="deleteBatch(scope.row.id)"
            >删除</el-button>
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

    <!-- 创建/编辑批次对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑注册批次' : '创建注册批次'"
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
        <el-form-item label="目标组织" prop="targetOrganizationId">
          <el-cascader
            v-model="form.targetOrganizationId"
            :options="organizationOptions"
            :props="{
              value: 'id',
              label: 'name',
              children: 'children',
              emitPath: false,
              checkStrictly: true
            }"
            placeholder="请选择目标组织"
            style="width: 100%"
            clearable
            filterable
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { InfoFilled } from '@element-plus/icons-vue';
import { getBatchList, createBatch as apiCreateBatch, updateBatch, deleteBatch as apiDeleteBatch, startBatch as apiStartBatch, finishBatch as apiFinishBatch, getBatchDetail } from '../../api/register.ts';
import { getOrganizationTree } from '@/api/organization';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const formRef = ref(null);
const userStore = useUserStore();

// 数据定义
const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const batchList = ref([]);
const organizationOptions = ref([]);

const page = reactive({
  current: 1,
  size: 10,
  total: 0
});

const filterForm = reactive({
  batchName: '',
  registerYear: '',
  status: ''
});

// 表单数据
const form = reactive({
  id: '',
  title: '',
  year: new Date().getFullYear().toString(),
  dateRange: [],
  targetOrganizationId: null,
  description: ''
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
  ],
  targetOrganizationId: [
    { required: true, message: '请选择目标组织', trigger: 'change' }
  ]
};

// 计算属性
const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear();
  return Array.from({length: 5}, (_, i) => (currentYear - i).toString());
});

// 方法
const loadBatchList = async () => {
  loading.value = true;
  try {
    const response = await getBatchList({
      page: page.current,
      size: page.size,
      batchName: filterForm.batchName || undefined,
      registerYear: filterForm.registerYear || undefined,
      status: filterForm.status || undefined
    });
    if (response.code === 200) {
      batchList.value = response.data.list || [];
      page.total = response.data.total || 0;
    } else {
      ElMessage.error(response.message || '获取注册批次列表失败');
    }
  } catch (error) {
    console.error('加载注册批次列表失败:', error);
    ElMessage.error('加载注册批次列表失败');
  } finally {
    loading.value = false;
  }
};

const loadOrganizations = async () => {
  try {
    const userOrgId = userStore.organizationId;
    
    const response = await getOrganizationTree(userOrgId);
    
    if (response && (response.success || response.code === 200)) {
      let orgData = response.data || [];
      if (!Array.isArray(orgData) && orgData.data) {
        orgData = orgData.data;
      }
      
      const processOrgData = (orgs) => {
        return orgs.map(org => {
          if (!org.children) org.children = [];
          if (org.children.length > 0) {
            org.children = processOrgData(org.children);
          }
          return org;
        });
      };
      
      organizationOptions.value = processOrgData(orgData);
      
      if (userStore.organizationId) {
        form.targetOrganizationId = userStore.organizationId;
      }
    } else {
      ElMessage.error(response?.message || '获取组织列表失败');
    }
  } catch (error) {
    console.error('加载组织列表失败:', error);
    ElMessage.error('加载组织列表失败');
  }
};

const handleSearch = () => {
  page.current = 1;
  loadBatchList();
};

const resetFilter = () => {
  filterForm.batchName = '';
  filterForm.registerYear = '';
  filterForm.status = '';
  handleSearch();
};

const handleSizeChange = (val) => {
  page.size = val;
  loadBatchList();
};

const handleCurrentChange = (val) => {
  page.current = val;
  loadBatchList();
};

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

const goBack = () => {
  router.push('/dashboard/register');
};

const handleCreateBatch = () => {
  isEdit.value = false;
  form.id = '';
  form.title = '';
  form.year = new Date().getFullYear().toString();
  form.dateRange = [];
  form.targetOrganizationId = null;
  form.description = '';
  dialogVisible.value = true;
};

const editBatch = async (batch) => {
  isEdit.value = true;
  form.id = batch.id;
  form.title = batch.batchName || batch.title || '';
  form.year = batch.registerYear || batch.year || '';
  form.dateRange = [
    new Date(batch.startTime || batch.startDate || ''), 
    new Date(batch.endTime || batch.endDate || '')
  ];
  form.targetOrganizationId = batch.targetOrganizationId || batch.organizationId;
  form.description = batch.description || '';
  dialogVisible.value = true;
};

const getSelectedOrganizationName = (orgId) => {
  if (!orgId) return '';
  
  const findOrgName = (options, id) => {
    for (const option of options) {
      if (option.id === id) {
        return option.name || '';
      }
      if (option.children && option.children.length > 0) {
        const name = findOrgName(option.children, id);
        if (name) return name;
      }
    }
    return '';
  };
  
  return findOrgName(organizationOptions.value, orgId);
};

// 格式化日期为后端接受的格式
const formatDateTimeForBackend = (date) => {
  if (!date) return null;
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  // 后端期望的格式为 yyyy-MM-dd HH:mm:ss
  return `${year}-${month}-${day} 00:00:00`;
};

const submitForm = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const formData = {
          title: form.title,
          year: form.year,
          startDate: formatDateTimeForBackend(form.dateRange[0]),
          endDate: formatDateTimeForBackend(form.dateRange[1]),
          targetOrganizationId: form.targetOrganizationId,
          description: form.description
        };

        if (isEdit.value) {
          formData.id = form.id;
          const response = await updateBatch(form.id, formData);
          if (response.code === 200) {
            ElMessage.success('注册批次更新成功');
            dialogVisible.value = false;
            loadBatchList();
          } else {
            ElMessage.error(response.message || '更新注册批次失败');
          }
        } else {
          console.log('调用createBatch API...');
          try {
            const response = await apiCreateBatch(formData);
            console.log('API响应:', response);
            if (response.code === 200) {
              ElMessage.success('注册批次创建成功');
              dialogVisible.value = false;
              loadBatchList();
            } else {
              ElMessage.error(response.message || '创建注册批次失败');
            }
          } catch (apiError) {
            console.error('API调用异常:', apiError);
            ElMessage.error(`API调用异常: ${apiError.message || '未知错误'}`);
          }
        }
      } catch (error) {
        console.error('提交注册批次失败:', error);
        if (error.message) {
          ElMessage.error(error.message);
        } else {
          ElMessage.error('提交注册批次失败');
        }
      }
    }
  });
};

const viewDetail = (id) => {
  router.push(`/dashboard/register/batch/${id}`);
};

const startBatch = async (id) => {
  try {
    ElMessageBox.confirm('确定要开始此注册批次吗？开始后将通知团员进行注册。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      const response = await apiStartBatch(id);
      if (response.code === 200) {
        ElMessage.success('注册批次已开始');
        loadBatchList();
      } else {
        ElMessage.error(response.message || '开始注册批次失败');
      }
    }).catch(() => {});
  } catch (error) {
    console.error('开始注册批次失败:', error);
    ElMessage.error('开始注册批次失败');
  }
};

const endBatch = async (id) => {
  try {
    ElMessageBox.confirm('确定要结束此注册批次吗？结束后团员将无法继续注册。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      const response = await apiFinishBatch(id);
      if (response.code === 200) {
        ElMessage.success('注册批次已结束');
        loadBatchList();
      } else {
        ElMessage.error(response.message || '结束注册批次失败');
      }
    }).catch(() => {});
  } catch (error) {
    console.error('结束注册批次失败:', error);
    ElMessage.error('结束注册批次失败');
  }
};

const deleteBatch = async (id) => {
  try {
    ElMessageBox.confirm('确定要删除此注册批次吗？此操作不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    }).then(async () => {
      const response = await apiDeleteBatch(id);
      if (response.code === 200) {
        ElMessage.success('注册批次已删除');
        loadBatchList();
      } else {
        ElMessage.error(response.message || '删除注册批次失败');
      }
    }).catch(() => {});
  } catch (error) {
    console.error('删除注册批次失败:', error);
    ElMessage.error('删除注册批次失败');
  }
};

// 生命周期钩子
onMounted(() => {
  loadBatchList();
  loadOrganizations();
});
</script>

<style scoped>
.register-batch-container {
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

.batch-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-wrapper {
  display: flex;
  flex-direction: column;
}

.progress-text {
  font-size: 12px;
  margin-top: 5px;
  color: #909399;
  text-align: center;
}

.filter-select {
  width: 120px;
  margin-right: 10px;
}

.filter-select :deep(.el-input__inner) {
  border-radius: 4px;
  transition: all 0.3s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.filter-select :deep(.el-input__inner:hover) {
  border-color: #409EFF;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 