<template>
  <div class="notification-management-container">
    <div class="page-header">
      <h1>通知管理</h1>
      <p>管理系统通知、公告和消息发布</p>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm" class="form-inline">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入通知标题" clearable></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择通知类型" clearable>
            <el-option v-for="item in notificationTypeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchNotifications">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="table-operations">
        <el-button type="primary" @click="createNotification">创建通知</el-button>
        <el-button type="warning" @click="batchPublish">批量发布</el-button>
        <el-button type="danger" @click="batchDelete">批量删除</el-button>
      </div>

      <el-table 
        :data="tableData" 
        border 
        style="width: 100%" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="通知标题" min-width="180"></el-table-column>
        <el-table-column prop="type" label="通知类型" width="120"></el-table-column>
        <el-table-column prop="publisher" label="发布人" width="120"></el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180"></el-table-column>
        <el-table-column prop="readCount" label="阅读量" width="90"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.status === '草稿'" size="small" type="success" @click="handlePublish(scope.row)">发布</el-button>
            <el-button v-if="scope.row.status === '已发布'" size="small" type="warning" @click="handleUnpublish(scope.row)">撤回</el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="info" @click="handleView(scope.row)">查看</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useRouter } from 'vue-router';

const router = useRouter();

// 表格数据
const tableData = ref([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const selectedRows = ref([]);

// 搜索表单
const searchForm = reactive({
  title: '',
  type: '',
  status: '',
  dateRange: []
});

// 通知类型选项
const notificationTypeOptions = [
  { value: '系统通知', label: '系统通知' },
  { value: '活动公告', label: '活动公告' },
  { value: '重要通知', label: '重要通知' },
  { value: '普通消息', label: '普通消息' }
];

// 状态选项
const statusOptions = [
  { value: '草稿', label: '草稿' },
  { value: '已发布', label: '已发布' },
  { value: '已撤回', label: '已撤回' },
  { value: '已过期', label: '已过期' }
];

// 生命周期钩子
onMounted(() => {
  fetchNotificationList();
});

// 获取通知列表数据
const fetchNotificationList = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 模拟数据
    tableData.value = [
      {
        id: 1,
        title: '关于2023年五四青年节表彰活动的通知',
        type: '重要通知',
        publisher: '团委办公室',
        publishTime: '2023-04-20 14:30:00',
        readCount: 256,
        status: '已发布'
      },
      {
        id: 2,
        title: '五四评优推荐名单公示',
        type: '活动公告',
        publisher: '团委办公室',
        publishTime: '2023-04-25 10:15:00',
        readCount: 198,
        status: '已发布'
      },
      {
        id: 3,
        title: '青年志愿者服务活动招募',
        type: '普通消息',
        publisher: '志愿者协会',
        publishTime: null,
        readCount: 0,
        status: '草稿'
      },
      {
        id: 4,
        title: '系统维护通知',
        type: '系统通知',
        publisher: '系统管理员',
        publishTime: '2023-05-10 23:30:00',
        readCount: 89,
        status: '已过期'
      },
      {
        id: 5,
        title: '关于开展"青春心向党"主题团日活动的通知',
        type: '重要通知',
        publisher: '团委办公室',
        publishTime: '2023-05-15 09:00:00',
        readCount: 145,
        status: '已发布'
      }
    ];
    
    total.value = tableData.value.length;
  } catch (error) {
    console.error('获取通知列表失败:', error);
    ElMessage.error('获取通知列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索通知
const searchNotifications = () => {
  currentPage.value = 1;
  fetchNotificationList();
};

// 重置搜索条件
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = key === 'dateRange' ? [] : '';
  });
  currentPage.value = 1;
  fetchNotificationList();
};

// 创建通知
const createNotification = () => {
  router.push('/admin/create-notification');
};

// 编辑通知
const handleEdit = (row) => {
  router.push(`/admin/edit-notification/${row.id}`);
};

// 查看通知
const handleView = (row) => {
  router.push(`/admin/view-notification/${row.id}`);
};

// 发布通知
const handlePublish = (row) => {
  ElMessageBox.confirm(`确定要发布通知"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    // 模拟发布操作
    const index = tableData.value.findIndex(item => item.id === row.id);
    if (index !== -1) {
      tableData.value[index].status = '已发布';
      tableData.value[index].publishTime = new Date().toLocaleString();
      ElMessage.success('发布成功');
    }
  }).catch(() => {
    // 取消发布
  });
};

// 撤回通知
const handleUnpublish = (row) => {
  ElMessageBox.confirm(`确定要撤回通知"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟撤回操作
    const index = tableData.value.findIndex(item => item.id === row.id);
    if (index !== -1) {
      tableData.value[index].status = '已撤回';
      ElMessage.success('撤回成功');
    }
  }).catch(() => {
    // 取消撤回
  });
};

// 删除通知
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除通知"${row.title}"吗？`, '提示', {
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

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection;
};

// 批量发布
const batchPublish = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条通知');
    return;
  }
  
  const draftRows = selectedRows.value.filter(row => row.status === '草稿');
  
  if (draftRows.length === 0) {
    ElMessage.warning('所选通知中没有可发布的草稿');
    return;
  }
  
  ElMessageBox.confirm(`确定要批量发布${draftRows.length}条通知吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    // 模拟批量发布操作
    const now = new Date().toLocaleString();
    draftRows.forEach(row => {
      const index = tableData.value.findIndex(item => item.id === row.id);
      if (index !== -1) {
        tableData.value[index].status = '已发布';
        tableData.value[index].publishTime = now;
      }
    });
    ElMessage.success(`成功发布${draftRows.length}条通知`);
  }).catch(() => {
    // 取消批量发布
  });
};

// 批量删除
const batchDelete = () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条通知');
    return;
  }
  
  ElMessageBox.confirm(`确定要批量删除${selectedRows.value.length}条通知吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟批量删除操作
    const ids = selectedRows.value.map(row => row.id);
    tableData.value = tableData.value.filter(item => !ids.includes(item.id));
    total.value = tableData.value.length;
    ElMessage.success(`成功删除${ids.length}条通知`);
  }).catch(() => {
    // 取消批量删除
  });
};

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val;
  fetchNotificationList();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchNotificationList();
};

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    '草稿': 'info',
    '已发布': 'success',
    '已撤回': 'warning',
    '已过期': 'danger'
  };
  return typeMap[status] || 'info';
};
</script>

<style scoped>
.notification-management-container {
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