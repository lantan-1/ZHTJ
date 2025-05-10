<template>
  <div class="notifications-container">
    
    
    <el-card shadow="hover" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="关键词" style="width: 200px;">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="搜索通知标题"
            clearable
            @keyup.enter="handleSearch"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="通知类型">
          <el-select v-model="searchForm.type" placeholder="选择类型" clearable style="width: 100px;">
            <el-option 
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="阅读状态">
          <el-select v-model="searchForm.readStatus" placeholder="选择状态" clearable style="width: 100px;">
            <el-option 
              v-for="item in readStatusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch" :icon="RefreshRight">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card shadow="hover" class="table-card">
      <el-table 
        :data="filterNotifications" 
        style="width: 100%" 
        v-loading="loading"
        border
      >
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="类型" width="120"></el-table-column>
        <el-table-column label="优先级" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.priority === '重要' ? 'danger' : 'info'" size="small">
              {{ scope.row.priority }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sender" label="发送人" width="120"></el-table-column>
        <el-table-column label="时间" width="160" align="center">
          <template #default="scope">
            {{ scope.row.createTime }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.readStatus ? 'info' : 'warning'" size="small">
              {{ scope.row.readStatus ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              link 
              @click="viewNotification(scope.row)" 
              :icon="View"
            >
              查看
            </el-button>
            <el-button 
              v-if="!scope.row.readStatus"
              type="success" 
              link 
              @click="markAsRead(scope.row)"
            >
              标为已读
            </el-button>
            <el-button 
              type="danger" 
              link 
              @click="deleteNotification(scope.row)" 
              :icon="Delete"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { Search, RefreshRight, Delete, View, Bell } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '@/stores/user';

const router = useRouter();
const userStore = useUserStore();

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: '',
  readStatus: ''
});

// 通知类型选项
const typeOptions = [
  { label: '全部', value: '' },
  { label: '工作通知', value: '工作通知' },
  { label: '活动通知', value: '活动通知' },
  { label: '学习资料', value: '学习资料' },
  { label: '系统通知', value: '系统通知' }
];

// 阅读状态选项
const readStatusOptions = [
  { label: '全部', value: '' },
  { label: '已读', value: 'read' },
  { label: '未读', value: 'unread' }
];

// 通知数据类型
interface Notification {
  id: string;
  title: string;
  type: string;
  sender: string;
  createTime: string;
  readStatus: boolean;
  priority: string;
  content: string;
}

// 表格数据
const tableData = ref<Notification[]>([
  {
    id: '1',
    title: '智慧团建系统项目立项情况说明',
    type: '系统通知',
    sender: '系统管理员',
    createTime: '2025-03-01 09:30:00',
    readStatus: false,
    priority: '重要',
    content: '智慧团建系统已正式立项，项目旨在构建一个现代化、智能化的团组织管理与服务平台。系统将包含团员管理、组织管理、学习资源、活动管理等核心功能模块，打造一站式团建服务解决方案。项目计划在2025年底前全面上线，欢迎各级团组织共同参与建设和试用。'
  },
  {
    id: '2',
    title: '智慧团建系统开发进度通报',
    type: '系统通知',
    sender: '系统管理员',
    createTime: '2025-05-04 14:15:00',
    readStatus: false,
    priority: '普通',
    content: '智慧团建系统已完成主体功能开发，当前版本v1.0.0正处于内测阶段。目前已完成用户管理、权限系统、组织管理、学习资源管理等核心功能模块。正在进行的是消息通知、统计报表和移动端适配等功能的开发。感谢各位团干部的宝贵意见和建议，我们将持续优化系统功能和用户体验。'
  },
  {
    id: '3',
    title: `欢迎${userStore.userInfo.organization_name || ''}${userStore.userInfo.league_position || ''}${userStore.userInfo.name || ''}`,
    type: '系统通知',
    sender: '智慧团建',
    createTime: new Date().toLocaleString('zh-CN', {hour12: false}).replace(/\//g, '-'),
    readStatus: false,
    priority: '普通',
    content: `尊敬的${userStore.userInfo.organization_name || ''}${userStore.userInfo.league_position || ''}${userStore.userInfo.name || ''}，欢迎您登录智慧团建系统！系统将为您提供便捷的团组织管理和团员服务功能。您可以通过左侧导航栏访问各项功能模块，如有任何问题，请随时联系系统管理员或查看帮助文档。祝您使用愉快！`
  }
]);

// 加载状态和分页
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = computed(() => filterNotifications.value.length);

// 根据搜索条件过滤通知
const filterNotifications = computed(() => {
  return tableData.value.filter(item => {
    // 关键词筛选
    if (searchForm.keyword && !item.title.includes(searchForm.keyword)) {
      return false;
    }
    
    // 类型筛选
    if (searchForm.type && item.type !== searchForm.type) {
      return false;
    }
    
    // 状态筛选
    if (searchForm.readStatus === 'read' && !item.readStatus) {
      return false;
    }
    if (searchForm.readStatus === 'unread' && item.readStatus) {
      return false;
    }
    
    return true;
  });
});

// 初始化
onMounted(() => {
  // 确保第三条通知标题始终显示最新的用户信息
  tableData.value[2].title = `欢迎${userStore.userInfo.organization_name || ''}${userStore.userInfo.league_position || ''}${userStore.userInfo.name || ''}`;
  tableData.value[2].content = `尊敬的${userStore.userInfo.organization_name || ''}${userStore.userInfo.league_position || ''}${userStore.userInfo.name || ''}，欢迎您登录智慧团建系统！系统将为您提供便捷的团组织管理和团员服务功能。您可以通过左侧导航栏访问各项功能模块，如有任何问题，请随时联系系统管理员或查看帮助文档。祝您使用愉快！`;
});

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
};

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = '';
  searchForm.type = '';
  searchForm.readStatus = '';
  currentPage.value = 1;
};

// 页面变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val;
};

// 每页条数变化
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  currentPage.value = 1;
};

// 查看通知详情
const viewNotification = (row: any) => {
  router.push(`/dashboard/notification/detail/${row.id}`);
};

// 删除通知
const deleteNotification = (row: any) => {
  ElMessageBox.confirm(`确定要删除通知"${row.title}"吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 从表格中移除
    tableData.value = tableData.value.filter((item: any) => item.id !== row.id);
    ElMessage({
      type: 'success',
      message: '删除成功'
    });
  }).catch(() => {
    // 取消删除
  });
};

// 标记为已读
const markAsRead = (row: any) => {
  row.readStatus = true;
  ElMessage({
    type: 'success',
    message: '已标记为已读'
  });
};
</script>

<style scoped>
.notifications-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 22px;
  margin-bottom: 10px;
}

.page-header p {
  color: #666;
  margin-bottom: 15px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>