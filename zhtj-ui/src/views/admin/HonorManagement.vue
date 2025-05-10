<template>
  <div class="honor-management-container">
    <div class="page-header">
      <h1>荣誉管理</h1>
      <p>管理系统荣誉类型、荣誉申请和授予记录</p>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm" class="form-inline">
        <el-form-item label="荣誉名称">
          <el-input v-model="searchForm.name" placeholder="请输入荣誉名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="荣誉类型">
          <el-select v-model="searchForm.type" placeholder="请选择荣誉类型" clearable>
            <el-option v-for="item in honorTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchHonors">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-operations">
        <el-button type="primary" @click="showAddDialog">添加荣誉类型</el-button>
        <el-button type="success" @click="exportData">导出数据</el-button>
      </div>

      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="荣誉名称" width="180"></el-table-column>
        <el-table-column prop="type" label="荣誉类型" width="120"></el-table-column>
        <el-table-column prop="level" label="荣誉级别" width="120"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="success" @click="viewApplications(scope.row)">查看申请</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>

    <!-- 添加/编辑荣誉类型对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="honorForm" :rules="rules" ref="honorFormRef" label-width="100px">
        <el-form-item label="荣誉名称" prop="name">
          <el-input v-model="honorForm.name" placeholder="请输入荣誉名称"></el-input>
        </el-form-item>
        <el-form-item label="荣誉类型" prop="type">
          <el-select v-model="honorForm.type" placeholder="请选择荣誉类型" style="width: 100%">
            <el-option v-for="item in honorTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="荣誉级别" prop="level">
          <el-select v-model="honorForm.level" placeholder="请选择荣誉级别" style="width: 100%">
            <el-option v-for="item in honorLevelOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="荣誉描述" prop="description">
          <el-input v-model="honorForm.description" type="textarea" rows="4" placeholder="请输入荣誉描述"></el-input>
        </el-form-item>
        <el-form-item label="申请条件" prop="requirements">
          <el-input v-model="honorForm.requirements" type="textarea" rows="4" placeholder="请输入申请条件"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="honorForm.status">
            <el-radio label="启用">启用</el-radio>
            <el-radio label="禁用">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitHonorForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 搜索表单
const searchForm = reactive({
  name: '',
  type: '',
  status: ''
});

// 荣誉类型选项
const honorTypeOptions = [
  { value: '个人荣誉', label: '个人荣誉' },
  { value: '集体荣誉', label: '集体荣誉' },
  { value: '项目荣誉', label: '项目荣誉' }
];

// 荣誉级别选项
const honorLevelOptions = [
  { value: '校级', label: '校级' },
  { value: '市级', label: '市级' },
  { value: '省级', label: '省级' },
  { value: '国家级', label: '国家级' }
];

// 状态选项
const statusOptions = [
  { value: '启用', label: '启用' },
  { value: '禁用', label: '禁用' }
];

// 对话框相关
const dialogVisible = ref(false);
const dialogTitle = ref('添加荣誉类型');
const honorFormRef = ref(null);
const honorForm = reactive({
  id: '',
  name: '',
  type: '',
  level: '',
  description: '',
  requirements: '',
  status: '启用'
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入荣誉名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择荣誉类型', trigger: 'change' }],
  level: [{ required: true, message: '请选择荣誉级别', trigger: 'change' }],
  description: [{ required: true, message: '请输入荣誉描述', trigger: 'blur' }],
  requirements: [{ required: true, message: '请输入申请条件', trigger: 'blur' }]
};

// 生命周期钩子
onMounted(() => {
  fetchHonorList();
});

// 获取荣誉列表数据
const fetchHonorList = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模拟数据
    tableData.value = [
      {
        id: 1,
        name: '优秀共青团员',
        type: '个人荣誉',
        level: '校级',
        createTime: '2023-05-15 14:30:00',
        status: '启用'
      },
      {
        id: 2,
        name: '优秀团支部',
        type: '集体荣誉',
        level: '校级',
        createTime: '2023-05-16 09:15:00',
        status: '启用'
      },
      {
        id: 3,
        name: '青年志愿者先进个人',
        type: '个人荣誉',
        level: '市级',
        createTime: '2023-05-18 11:20:00',
        status: '启用'
      },
      {
        id: 4,
        name: '青年文明号',
        type: '集体荣誉',
        level: '省级',
        createTime: '2023-05-20 16:45:00',
        status: '禁用'
      }
    ];
    
    total.value = tableData.value.length;
  } catch (error) {
    console.error('获取荣誉列表失败:', error);
    ElMessage.error('获取荣誉列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索荣誉
const searchHonors = () => {
  currentPage.value = 1;
  fetchHonorList();
};

// 重置搜索条件
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = '';
  });
  currentPage.value = 1;
  fetchHonorList();
};

// 添加荣誉类型
const showAddDialog = () => {
  dialogTitle.value = '添加荣誉类型';
  Object.keys(honorForm).forEach(key => {
    honorForm[key] = key === 'status' ? '启用' : '';
  });
  dialogVisible.value = true;
};

// 编辑荣誉类型
const handleEdit = (row) => {
  dialogTitle.value = '编辑荣誉类型';
  Object.keys(honorForm).forEach(key => {
    honorForm[key] = row[key] || '';
  });
  dialogVisible.value = true;
};

// 查看申请
const viewApplications = (row) => {
  ElMessage.info(`查看荣誉"${row.name}"的申请记录`);
  // 跳转到申请列表页面，这里只做提示
};

// 删除荣誉类型
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除荣誉类型"${row.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除操作
    tableData.value = tableData.value.filter(item => item.id !== row.id);
    total.value = tableData.value.length;
    ElMessage.success('删除成功');
  }).catch(() => {
    // 取消删除
  });
};

// 提交荣誉表单
const submitHonorForm = () => {
  honorFormRef.value.validate((valid) => {
    if (valid) {
      if (honorForm.id) {
        // 更新荣誉类型
        const index = tableData.value.findIndex(item => item.id === honorForm.id);
        if (index !== -1) {
          tableData.value[index] = { ...honorForm, createTime: tableData.value[index].createTime };
          ElMessage.success('更新成功');
        }
      } else {
        // 添加新荣誉类型
        const newId = tableData.value.length > 0 ? Math.max(...tableData.value.map(item => item.id)) + 1 : 1;
        tableData.value.push({
          ...honorForm,
          id: newId,
          createTime: new Date().toLocaleString()
        });
        total.value = tableData.value.length;
        ElMessage.success('添加成功');
      }
      dialogVisible.value = false;
    } else {
      return false;
    }
  });
};

// 导出数据
const exportData = () => {
  ElMessage.info('导出数据功能待实现');
};

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchHonorList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchHonorList();
};

// 获取状态标签类型
const getStatusType = (status) => {
  return status === '启用' ? 'success' : 'danger';
};
</script>

<style scoped>
.honor-management-container {
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

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.table-operations {
  margin-bottom: 15px;
  display: flex;
  justify-content: flex-start;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}
</style> 