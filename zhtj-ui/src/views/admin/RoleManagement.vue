<template>
  <div class="role-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h2>角色管理</h2>
          <el-button type="primary" @click="showAddDialog">添加角色</el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索角色名称"
          clearable
          style="width: 300px;"
          @clear="handleSearchClear"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
      
      <!-- 角色表格 -->
      <el-table
        :data="filteredRoles"
        style="width: 100%"
        border
        v-loading="loading"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="code" label="角色代码" />
        <el-table-column prop="description" label="角色描述" />
        <el-table-column prop="userCount" label="用户数量" width="100" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-popconfirm
              title="确定删除该角色吗？"
              @confirm="handleDelete(scope.row)"
              confirm-button-text="确定"
              cancel-button-text="取消"
            >
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑角色' : '添加角色'"
      width="50%"
      :before-close="handleDialogClose"
    >
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色代码" prop="code">
          <el-input v-model="roleForm.code" placeholder="请输入角色代码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="roleForm.status"
            :active-value="1"
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
        
        <el-divider>角色权限</el-divider>
        
        <el-form-item label="权限配置">
          <el-tree
            ref="permissionTreeRef"
            :data="permissionTree"
            show-checkbox
            node-key="id"
            default-expand-all
            :default-checked-keys="selectedPermissions"
            :props="{ label: 'name' }"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 查看角色详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="角色详情"
      width="50%"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="角色ID">{{ currentRole.id }}</el-descriptions-item>
        <el-descriptions-item label="角色名称">{{ currentRole.name }}</el-descriptions-item>
        <el-descriptions-item label="角色代码">{{ currentRole.code }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentRole.status === 1 ? 'success' : 'danger'">
            {{ currentRole.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户数量">{{ currentRole.userCount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentRole.createTime }}</el-descriptions-item>
        <el-descriptions-item label="角色描述" :span="2">
          {{ currentRole.description || '暂无描述' }}
        </el-descriptions-item>
      </el-descriptions>
      
      <el-divider>角色权限</el-divider>
      
      <el-tree
        :data="permissionTree"
        show-checkbox
        node-key="id"
        default-expand-all
        :default-checked-keys="viewRolePermissions"
        :props="{ label: 'name' }"
        disabled
      />
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox, FormInstance, ElTree } from 'element-plus';

// 角色数据
const roles = ref<Role[]>([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const searchKeyword = ref('');

// 对话框相关
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const isEdit = ref(false);
const roleFormRef = ref<FormInstance>();
const permissionTreeRef = ref<InstanceType<typeof ElTree>>();
const currentRole = ref<Role>({} as Role);
const selectedPermissions = ref<number[]>([]);
const viewRolePermissions = ref<number[]>([]);

// 角色表单
const roleForm = ref({
  id: 0,
  name: '',
  code: '',
  description: '',
  status: 1
});

// 表单校验规则
const roleRules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  code: [
    { required: true, message: '请输入角色代码', trigger: 'blur' },
    { pattern: /^[a-zA-Z_]+$/, message: '角色代码只能包含字母和下划线', trigger: 'blur' }
  ]
};

// 权限树
const permissionTree = ref([
  {
    id: 1,
    name: '系统管理',
    children: [
      { id: 11, name: '用户管理' },
      { id: 12, name: '角色管理' },
      { id: 13, name: '组织管理' },
      { id: 14, name: '权限管理' }
    ]
  },
  {
    id: 2,
    name: '内容管理',
    children: [
      { id: 21, name: '文章管理' },
      { id: 22, name: '栏目管理' },
      { id: 23, name: '标签管理' }
    ]
  },
  {
    id: 3,
    name: '活动管理',
    children: [
      { id: 31, name: '活动列表' },
      { id: 32, name: '报名管理' },
      { id: 33, name: '志愿服务' }
    ]
  },
  {
    id: 4,
    name: '系统工具',
    children: [
      { id: 41, name: '系统监控' },
      { id: 42, name: '日志管理' },
      { id: 43, name: '数据备份' }
    ]
  }
]);

// 计算过滤后的角色列表
const filteredRoles = computed(() => {
  if (!searchKeyword.value) {
    return roles.value;
  }
  return roles.value.filter(role => 
    role.name.includes(searchKeyword.value) || 
    role.code.includes(searchKeyword.value)
  );
});

// 初始化页面
onMounted(() => {
  fetchRoles();
});

// 获取角色列表
const fetchRoles = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    // 实际项目中应替换为真实API调用
    // const response = await api.get('/api/roles');
    // roles.value = response.data.list;
    // total.value = response.data.total;
    
    // 模拟数据
    setTimeout(() => {
      roles.value = [
        {
          id: 1,
          name: '超级管理员',
          code: 'admin',
          description: '系统超级管理员，拥有所有权限',
          status: 1,
          userCount: 1,
          createTime: '2023-01-01',
          permissions: [1, 11, 12, 13, 14, 2, 21, 22, 23, 3, 31, 32, 33, 4, 41, 42, 43]
        },
        {
          id: 2,
          name: '普通用户',
          code: 'user',
          description: '普通用户，拥有基本权限',
          status: 1,
          userCount: 55,
          createTime: '2023-01-02',
          permissions: [2, 21, 22, 3, 31, 32]
        },
        {
          id: 3,
          name: '访客',
          code: 'visitor',
          description: '访客，仅拥有查看权限',
          status: 0,
          userCount: 120,
          createTime: '2023-01-03',
          permissions: [2, 21, 3, 31]
        }
      ];
      total.value = 3;
      loading.value = false;
    }, 500);
  } catch (error) {
    ElMessage.error('获取角色列表失败');
    loading.value = false;
  }
};

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  fetchRoles();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchRoles();
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  fetchRoles();
};

const handleSearchClear = () => {
  searchKeyword.value = '';
  handleSearch();
};

// 角色CRUD操作
const showAddDialog = () => {
  isEdit.value = false;
  roleForm.value = {
    id: 0,
    name: '',
    code: '',
    description: '',
    status: 1
  };
  selectedPermissions.value = [];
  dialogVisible.value = true;
};

const handleView = (row: Role) => {
  currentRole.value = row;
  viewRolePermissions.value = [...row.permissions];
  viewDialogVisible.value = true;
};

const handleEdit = (row: Role) => {
  isEdit.value = true;
  roleForm.value = {
    id: row.id,
    name: row.name,
    code: row.code,
    description: row.description || '',
    status: row.status
  };
  selectedPermissions.value = [...row.permissions];
  dialogVisible.value = true;
};

const handleToggleStatus = (row: Role) => {
  // 实际项目中应调用API
  // try {
  //   await api.put(`/api/roles/${row.id}/status`, { status: row.status === 1 ? 0 : 1 });
  //   ElMessage.success(`${row.status === 1 ? '禁用' : '启用'}成功`);
  //   fetchRoles();
  // } catch (error) {
  //   ElMessage.error(`${row.status === 1 ? '禁用' : '启用'}失败`);
  // }
  
  // 模拟操作
  const status = row.status === 1 ? 0 : 1;
  const index = roles.value.findIndex(r => r.id === row.id);
  if (index !== -1) {
    roles.value[index].status = status;
  }
  ElMessage.success(`${status === 1 ? '启用' : '禁用'}成功`);
};

const handleDelete = (row: Role) => {
  // 实际项目中应调用API
  // try {
  //   await api.delete(`/api/roles/${row.id}`);
  //   ElMessage.success('删除成功');
  //   fetchRoles();
  // } catch (error) {
  //   ElMessage.error('删除失败');
  // }
  
  // 模拟删除
  roles.value = roles.value.filter(r => r.id !== row.id);
  total.value -= 1;
  ElMessage.success('删除成功');
};

const handleDialogClose = () => {
  dialogVisible.value = false;
  roleFormRef.value?.resetFields();
};

const handleSubmit = async () => {
  if (!roleFormRef.value) return;
  
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      // 获取选中的权限
      const permissions = permissionTreeRef.value?.getCheckedKeys() as number[];
      // 获取半选中的父节点权限
      const halfCheckedKeys = permissionTreeRef.value?.getHalfCheckedKeys() as number[];
      // 合并权限
      const allPermissions = [...permissions, ...halfCheckedKeys];
      
      // 实际项目中应调用API
      // const url = isEdit.value ? `/api/roles/${roleForm.value.id}` : '/api/roles';
      // const method = isEdit.value ? 'put' : 'post';
      // try {
      //   await api[method](url, { ...roleForm.value, permissions: allPermissions });
      //   ElMessage.success(isEdit.value ? '更新成功' : '添加成功');
      //   dialogVisible.value = false;
      //   fetchRoles();
      // } catch (error) {
      //   ElMessage.error(isEdit.value ? '更新失败' : '添加失败');
      // }
      
      // 模拟操作
      if (isEdit.value) {
        const index = roles.value.findIndex(r => r.id === roleForm.value.id);
        if (index !== -1) {
          roles.value[index] = {
            ...roles.value[index],
            ...roleForm.value,
            permissions: allPermissions
          };
        }
        ElMessage.success('更新成功');
      } else {
        const newRole = {
          ...roleForm.value,
          id: Math.floor(Math.random() * 1000) + 10,
          userCount: 0,
          createTime: new Date().toISOString().split('T')[0],
          permissions: allPermissions
        };
        roles.value.push(newRole);
        total.value += 1;
        ElMessage.success('添加成功');
      }
      
      dialogVisible.value = false;
    }
  });
};
</script>

<script lang="ts">
// 需要在这里声明类型，以便在setup中使用
export interface Role {
  id: number;
  name: string;
  code: string;
  description?: string;
  status: number;
  userCount: number;
  createTime: string;
  permissions: number[];
}
</script>

<style scoped>
.role-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  margin-top: 20px;
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style> 