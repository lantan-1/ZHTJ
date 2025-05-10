<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Edit, Delete, Key, Download } from '@element-plus/icons-vue';
import { activateUser } from '../../api/user';

const router = useRouter();

const title = ref('用户管理');
const description = ref('管理系统用户、分配角色和权限');
const loading = ref(false);
const dialogVisible = ref(false);
const currentUser = ref(null);
const dialogTitle = ref('编辑用户');
const dialogMode = ref('edit'); // 'edit' 或 'roles'

// 用户表格数据
const tableData = ref([]);
const total = ref(0);

// 搜索条件
const searchForm = reactive({
  keyword: '',
  status: '',
  role: '',
  dateRange: []
});

// 分页设置
const pagination = reactive({
  currentPage: 1,
  pageSize: 10
});

// 用户状态选项
const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '正常', value: 'active' },
  { label: '禁用', value: 'disabled' },
  { label: '待审核', value: 'pending' },
  { label: '已删除', value: 'deleted' }
];

// 角色选项
const roleOptions = [
  { label: '全部角色', value: '' },
  { label: '管理员', value: 'admin' },
  { label: '团委管理员', value: 'committee_admin' },
  { label: '团支书', value: 'branch_secretary' },
  { label: '普通用户', value: 'user' }
];

// 用户表单
const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  email: '',
  phone: '',
  status: 'active',
  roles: []
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
};

// 角色表格数据
const roleTableData = ref([
  { id: 1, name: '管理员', code: 'admin', description: '系统管理员，拥有所有权限' },
  { id: 2, name: '团委管理员', code: 'committee_admin', description: '团委管理员，管理团委相关功能' },
  { id: 3, name: '团支书', code: 'branch_secretary', description: '团支书，管理团支部相关功能' },
  { id: 4, name: '普通用户', code: 'user', description: '普通用户，基本功能访问权限' }
]);

// 模拟用户数据
const mockUsers = [
  { id: 1, username: 'admin', realName: '管理员', email: 'admin@example.com', phone: '13800138000', status: 'active', createTime: '2023-01-01', roles: ['admin'] },
  { id: 2, username: 'zhangsan', realName: '张三', email: 'zhangsan@example.com', phone: '13800138001', status: 'active', createTime: '2023-02-15', roles: ['committee_admin'] },
  { id: 3, username: 'lisi', realName: '李四', email: 'lisi@example.com', phone: '13800138002', status: 'active', createTime: '2023-03-10', roles: ['branch_secretary'] },
  { id: 4, username: 'wangwu', realName: '王五', email: 'wangwu@example.com', phone: '13800138003', status: 'disabled', createTime: '2023-04-05', roles: ['user'] },
  { id: 5, username: 'zhaoliu', realName: '赵六', email: 'zhaoliu@example.com', phone: '13800138004', status: 'pending', createTime: '2023-05-20', roles: ['user'] }
];

// 为了演示目的，生成更多模拟数据
const generateMockData = () => {
  const result = [...mockUsers];
  
  for (let i = 6; i <= 50; i++) {
    const roles = [];
    const roleRandom = Math.floor(Math.random() * 4);
    
    if (roleRandom === 0) roles.push('admin');
    else if (roleRandom === 1) roles.push('committee_admin');
    else if (roleRandom === 2) roles.push('branch_secretary');
    else roles.push('user');
    
    const statusRandom = Math.floor(Math.random() * 4);
    let status = 'active';
    if (statusRandom === 1) status = 'disabled';
    else if (statusRandom === 2) status = 'pending';
    else if (statusRandom === 3) status = 'deleted';
    
    result.push({
      id: i,
      username: `user${i}`,
      realName: `用户${i}`,
      email: `user${i}@example.com`,
      phone: `1380013${i.toString().padStart(4, '0')}`,
      status,
      createTime: `2023-${Math.floor(Math.random() * 12) + 1}-${Math.floor(Math.random() * 28) + 1}`,
      roles
    });
  }
  
  return result;
};

// 加载用户数据
const loadUsers = async () => {
  loading.value = true;
  try {
    // 这里应该调用API获取用户数据
    // 临时使用模拟数据
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const allUsers = generateMockData();
    
    // 筛选
    let filteredUsers = allUsers;
    
    if (searchForm.keyword) {
      const keyword = searchForm.keyword.toLowerCase();
      filteredUsers = filteredUsers.filter(user => 
        user.username.toLowerCase().includes(keyword) || 
        user.realName.toLowerCase().includes(keyword) || 
        user.email.toLowerCase().includes(keyword) || 
        user.phone.includes(keyword)
      );
    }
    
    if (searchForm.status) {
      filteredUsers = filteredUsers.filter(user => user.status === searchForm.status);
    }
    
    if (searchForm.role) {
      filteredUsers = filteredUsers.filter(user => user.roles.includes(searchForm.role));
    }
    
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      const startDate = new Date(searchForm.dateRange[0]).getTime();
      const endDate = new Date(searchForm.dateRange[1]).getTime();
      
      filteredUsers = filteredUsers.filter(user => {
        const createTime = new Date(user.createTime).getTime();
        return createTime >= startDate && createTime <= endDate;
      });
    }
    
    // 分页
    total.value = filteredUsers.length;
    const startIndex = (pagination.currentPage - 1) * pagination.pageSize;
    const endIndex = startIndex + pagination.pageSize;
    tableData.value = filteredUsers.slice(startIndex, endIndex);
    
    if (tableData.value.length === 0 && total.value > 0) {
      // 如果当前页没有数据但总数不为0，说明页码过大，回到第一页
      pagination.currentPage = 1;
      loadUsers();
    }
  } catch (error) {
    console.error('加载用户数据失败:', error);
    ElMessage.error('加载用户数据失败');
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  pagination.currentPage = 1;
  loadUsers();
};

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = '';
  searchForm.status = '';
  searchForm.role = '';
  searchForm.dateRange = [];
  pagination.currentPage = 1;
  loadUsers();
};

// 处理分页变化
const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  loadUsers();
};

const handleSizeChange = (val) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  loadUsers();
};

// 编辑用户
const handleEdit = (row) => {
  currentUser.value = row;
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    email: row.email,
    phone: row.phone,
    status: row.status,
    roles: [...row.roles]
  });
  dialogTitle.value = '编辑用户';
  dialogMode.value = 'edit';
  dialogVisible.value = true;
};

// 创建新用户
const handleCreate = () => {
  currentUser.value = null;
  Object.assign(userForm, {
    id: null,
    username: '',
    realName: '',
    email: '',
    phone: '',
    status: 'active',
    roles: ['user']
  });
  dialogTitle.value = '创建用户';
  dialogMode.value = 'edit';
  dialogVisible.value = true;
};

// 管理用户角色
const handleRoles = (row) => {
  currentUser.value = row;
  Object.assign(userForm, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    roles: [...row.roles]
  });
  dialogTitle.value = `管理用户角色 - ${row.realName}`;
  dialogMode.value = 'roles';
  dialogVisible.value = true;
};

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户 ${row.realName}(${row.username}) 吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 这里应该调用API删除用户
      // 临时使用模拟操作
      await new Promise(resolve => setTimeout(resolve, 500));
      
      ElMessage.success('用户删除成功');
      loadUsers();
    } catch (error) {
      console.error('删除用户失败:', error);
      ElMessage.error('删除用户失败');
    }
  }).catch(() => {
    // 取消删除
  });
};

// 改变用户状态
const handleStatusChange = (row) => {
  const action = row.status === 'active' ? '禁用' : '启用';
  const newStatus = row.status === 'active' ? 'disabled' : 'active';
  
  ElMessageBox.confirm(`确定要${action}用户 ${row.realName}(${row.username}) 吗？`, '状态变更', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 这里应该调用API更改用户状态
      // 临时使用模拟操作
      await new Promise(resolve => setTimeout(resolve, 500));
      
      row.status = newStatus;
      ElMessage.success(`用户${action}成功`);
    } catch (error) {
      console.error(`${action}用户失败:`, error);
      ElMessage.error(`${action}用户失败`);
    }
  }).catch(() => {
    // 取消操作
  });
};

// 保存用户信息
const userFormRef = ref(null);
const handleSaveUser = async () => {
  if (!userFormRef.value) return;
  
  try {
    await userFormRef.value.validate();
    
    // 这里应该调用API保存用户信息
    // 临时使用模拟操作
    await new Promise(resolve => setTimeout(resolve, 500));
    
    if (userForm.id) {
      // 更新现有用户
      const index = tableData.value.findIndex(u => u.id === userForm.id);
      if (index !== -1) {
        const updatedUser = {
          ...tableData.value[index],
          username: userForm.username,
          realName: userForm.realName,
          email: userForm.email,
          phone: userForm.phone,
          status: userForm.status,
          roles: [...userForm.roles]
        };
        tableData.value.splice(index, 1, updatedUser);
      }
      ElMessage.success('用户信息更新成功');
    } else {
      // 创建新用户
      ElMessage.success('用户创建成功');
      loadUsers();
    }
    
    dialogVisible.value = false;
  } catch (error) {
    console.error('保存用户信息失败:', error);
    ElMessage.error('表单验证失败，请检查输入');
  }
};

// 保存用户角色
const handleSaveRoles = async () => {
  try {
    // 这里应该调用API保存用户角色
    // 临时使用模拟操作
    await new Promise(resolve => setTimeout(resolve, 500));
    
    const index = tableData.value.findIndex(u => u.id === userForm.id);
    if (index !== -1) {
      tableData.value[index].roles = [...userForm.roles];
    }
    
    ElMessage.success('用户角色更新成功');
    dialogVisible.value = false;
  } catch (error) {
    console.error('保存用户角色失败:', error);
    ElMessage.error('保存用户角色失败');
  }
};

// 返回管理控制台
const goBack = () => {
  router.push('/admin');
};

// 导出用户数据
const handleExport = () => {
  ElMessage.success('开始导出用户数据，请稍候...');
  // 这里应该调用API导出用户数据
};

// 关闭对话框
const handleDialogClose = () => {
  userFormRef.value?.resetFields();
};

// 格式化用户角色
const formatRoles = (roles) => {
  if (!roles || roles.length === 0) return '无角色';
  
  return roles.map(role => {
    const foundRole = roleOptions.find(option => option.value === role);
    return foundRole ? foundRole.label : role;
  }).join(', ');
};

// 导航到角色管理页面
const goToRoleManagement = () => {
  router.push('/admin/roles');
};

// 激活用户
const handleActivate = async (row: any) => {
  try {
    loading.value = true;
    const response = await activateUser(row.id);
    if (response.data && (response.data.code === 200 || response.data.success)) {
      ElMessage.success('用户激活成功');
      // 刷新用户列表
      loadUsers();
    } else {
      ElMessage.error(response.data?.message || '用户激活失败');
    }
  } catch (error: any) {
    console.error('激活用户出错', error);
    ElMessage.error(`激活失败：${error.response?.data?.message || error.message || '未知错误'}`);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadUsers();
});
</script>

<template>
  <div class="user-management">
    <div class="page-header">
      <div class="header-left">
        <h1>{{ title }}</h1>
        <p>{{ description }}</p>
      </div>
      <div class="header-right">
        <el-button @click="goBack">返回管理控制台</el-button>
      </div>
    </div>
    
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" inline>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/姓名/邮箱/手机" clearable />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
            <el-option v-for="option in statusOptions" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="选择角色" clearable>
            <el-option v-for="option in roleOptions" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="注册时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 用户表格 -->
    <el-card class="table-card">
      <div class="table-header">
        <div class="table-title">
          <h2>用户列表</h2>
          <span>共 {{ total }} 条记录</span>
        </div>
        <div class="table-actions">
          <el-button type="primary" @click="handleCreate">新增用户</el-button>
          <el-button type="success" @click="goToRoleManagement" :icon="Key">角色管理</el-button>
          <el-button @click="handleExport" :icon="Download">导出数据</el-button>
        </div>
      </div>
      
      <el-table :data="tableData" style="width: 100%" v-loading="loading" border stripe>
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="用户名" prop="username" min-width="120" />
        <el-table-column label="姓名" prop="realName" min-width="120" />
        <el-table-column label="邮箱" prop="email" min-width="180" />
        <el-table-column label="手机" prop="phone" min-width="120" />
        <el-table-column label="角色" min-width="150">
          <template #default="scope">
            {{ formatRoles(scope.row.roles) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 'active'" type="success">正常</el-tag>
            <el-tag v-else-if="scope.row.status === 'disabled'" type="danger">禁用</el-tag>
            <el-tag v-else-if="scope.row.status === 'pending'" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 'deleted'" type="info">已删除</el-tag>
            <el-tag v-else>未知</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="激活状态" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isActivated ? 'success' : 'danger'">
              {{ scope.row.isActivated ? '已激活' : '未激活' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" prop="createTime" min-width="120" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="!scope.row.isActivated"
              type="success" 
              size="small" 
              @click="handleActivate(scope.row)"
            >
              激活
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(scope.row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="warning" @click="handleRoles(scope.row)" :icon="Key">角色</el-button>
            <el-button 
              size="small" 
              :type="scope.row.status === 'active' ? 'danger' : 'success'"
              @click="handleStatusChange(scope.row)"
            >
              {{ scope.row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 用户编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle" 
      width="600px"
      @closed="handleDialogClose"
    >
      <!-- 编辑用户表单 -->
      <template v-if="dialogMode === 'edit'">
        <el-form 
          ref="userFormRef" 
          :model="userForm" 
          :rules="rules" 
          label-width="100px"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" placeholder="请输入用户名" />
          </el-form-item>
          
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="userForm.email" placeholder="请输入邮箱地址" />
          </el-form-item>
          
          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="userForm.phone" placeholder="请输入手机号码" />
          </el-form-item>
          
          <el-form-item label="状态" prop="status">
            <el-select v-model="userForm.status" placeholder="请选择状态">
              <el-option label="正常" value="active" />
              <el-option label="禁用" value="disabled" />
              <el-option label="待审核" value="pending" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="角色" prop="roles">
            <el-select v-model="userForm.roles" placeholder="请选择角色" multiple>
              <el-option v-for="option in roleOptions.filter(r => r.value)" :key="option.value" :label="option.label" :value="option.value" />
            </el-select>
          </el-form-item>
        </el-form>
      </template>
      
      <!-- 角色管理表单 -->
      <template v-else>
        <div class="role-management">
          <p class="user-info">
            用户: <strong>{{ userForm.realName }}</strong> ({{ userForm.username }})
          </p>
          
          <el-divider>当前角色</el-divider>
          
          <el-checkbox-group v-model="userForm.roles">
            <el-checkbox v-for="role in roleTableData" :key="role.code" :label="role.code">
              {{ role.name }} - {{ role.description }}
            </el-checkbox>
          </el-checkbox-group>
        </div>
      </template>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="dialogMode === 'edit' ? handleSaveUser() : handleSaveRoles()">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.user-management {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 12px;
}

.header-left h1 {
  font-size: 24px;
  margin-bottom: 8px;
  color: #303133;
}

.header-left p {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-title {
  display: flex;
  align-items: baseline;
}

.table-title h2 {
  font-size: 18px;
  margin: 0;
  margin-right: 12px;
}

.table-title span {
  font-size: 14px;
  color: #909399;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.role-management {
  padding: 0 20px;
}

.user-info {
  font-size: 16px;
  margin-bottom: 20px;
}

.el-checkbox {
  display: block;
  margin-left: 0 !important;
  margin-bottom: 12px;
}
</style> 