<template>
  <div class="organization-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h2>组织管理</h2>
          <el-button type="primary" @click="showAddDialog">添加组织</el-button>
        </div>
      </template>
      
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索组织名称"
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
        
        <el-select v-model="orgTypeFilter" placeholder="组织类型" clearable @change="handleFilterChange">
          <el-option v-for="item in orgTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </div>
      
      <!-- 组织表格 -->
      <el-table
        :data="filteredOrganizations"
        style="width: 100%"
        border
        v-loading="loading"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="组织名称" />
        <el-table-column prop="fullName" label="组织全称" />
        <el-table-column prop="orgType" label="组织类型">
          <template #default="scope">
            <el-tag :type="getOrgTypeTagType(scope.row.orgType)">{{ scope.row.orgType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="memberCount" label="成员数量" width="100" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-popconfirm
              title="确定删除该组织吗？"
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
    
    <!-- 添加/编辑组织对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑组织' : '添加组织'"
      width="50%"
      :before-close="handleDialogClose"
    >
      <el-form :model="orgForm" :rules="orgRules" ref="orgFormRef" label-width="100px">
        <el-form-item label="组织名称" prop="name">
          <el-input v-model="orgForm.name" placeholder="请输入组织名称" />
        </el-form-item>
        <el-form-item label="组织全称" prop="fullName">
          <el-input v-model="orgForm.fullName" placeholder="请输入组织全称" />
        </el-form-item>
        <el-form-item label="组织类型" prop="orgType">
          <el-select v-model="orgForm.orgType" placeholder="请选择组织类型" style="width: 100%">
            <el-option v-for="item in orgTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="上级组织" prop="parentId">
          <el-select v-model="orgForm.parentId" placeholder="请选择上级组织" clearable style="width: 100%">
            <el-option label="无上级组织" :value="0" />
            <el-option 
              v-for="org in parentOrgOptions" 
              :key="org.id" 
              :label="org.name" 
              :value="org.id" 
              :disabled="isEdit && org.id === orgForm.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="orgForm.description" type="textarea" :rows="3" placeholder="请输入组织描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 查看组织详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="组织详情"
      width="50%"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="组织ID">{{ currentOrg.id }}</el-descriptions-item>
        <el-descriptions-item label="组织名称">{{ currentOrg.name }}</el-descriptions-item>
        <el-descriptions-item label="组织全称">{{ currentOrg.fullName }}</el-descriptions-item>
        <el-descriptions-item label="组织类型">
          <el-tag :type="getOrgTypeTagType(currentOrg.orgType)">{{ currentOrg.orgType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="成员数量">{{ currentOrg.memberCount }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentOrg.createTime }}</el-descriptions-item>
        <el-descriptions-item label="上级组织" :span="2">
          {{ currentOrg.parentName || '无上级组织' }}
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">
          {{ currentOrg.description || '暂无描述' }}
        </el-descriptions-item>
      </el-descriptions>
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
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus';
import type { Organization } from '../../types/organization';

// 组织数据
const organizations = ref<Organization[]>([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const searchKeyword = ref('');
const orgTypeFilter = ref('');

// 对话框相关
const dialogVisible = ref(false);
const viewDialogVisible = ref(false);
const isEdit = ref(false);
const orgFormRef = ref<FormInstance>();
const currentOrg = ref<Organization>({} as Organization);

// 组织表单
const orgForm = ref({
  id: 0,
  name: '',
  fullName: '',
  orgType: '',
  parentId: 0,
  description: ''
});

// 表单校验规则
const orgRules = {
  name: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
  fullName: [{ required: true, message: '请输入组织全称', trigger: 'blur' }],
  orgType: [{ required: true, message: '请选择组织类型', trigger: 'change' }]
};

// 组织类型选项
const orgTypeOptions = [
  { value: '高校', label: '高校' },
  { value: '中学', label: '中学' },
  { value: '企业', label: '企业' },
  { value: '街道', label: '街道' },
  { value: '社区', label: '社区' },
  { value: '其他', label: '其他' }
];

// 上级组织选项
const parentOrgOptions = ref<Organization[]>([]);

// 计算过滤后的组织列表
const filteredOrganizations = computed(() => {
  let result = [...organizations.value];
  
  // 关键词搜索
  if (searchKeyword.value) {
    result = result.filter(org => 
      org.name.includes(searchKeyword.value) || 
      org.fullName.includes(searchKeyword.value)
    );
  }
  
  // 类型过滤
  if (orgTypeFilter.value) {
    result = result.filter(org => org.orgType === orgTypeFilter.value);
  }
  
  return result;
});

// 初始化页面
onMounted(() => {
  fetchOrganizations();
});

// 获取组织列表
const fetchOrganizations = async () => {
  loading.value = true;
  try {
    // 模拟API调用
    // 实际项目中应替换为真实API调用
    // const response = await api.get('/api/organizations');
    // organizations.value = response.data.list;
    // total.value = response.data.total;
    
    // 模拟数据
    setTimeout(() => {
      organizations.value = [
        {
          id: 1,
          name: '清华大学团委',
          fullName: '清华大学共青团委员会',
          orgType: '高校',
          memberCount: 120,
          createTime: '2023-01-01',
          parentId: 0,
          description: '清华大学学生团组织',
          parentName: ''
        },
        {
          id: 2,
          name: '计算机系团委',
          fullName: '清华大学计算机系团委',
          orgType: '高校',
          memberCount: 45,
          createTime: '2023-01-02',
          parentId: 1,
          description: '计算机系团组织',
          parentName: '清华大学团委'
        }
      ];
      total.value = 2;
      loading.value = false;
      
      // 更新上级组织选项
      parentOrgOptions.value = [...organizations.value];
    }, 500);
  } catch (error) {
    ElMessage.error('获取组织列表失败');
    loading.value = false;
  }
};

// 获取组织类型标签样式
const getOrgTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    '高校': 'success',
    '中学': 'info',
    '企业': 'danger',
    '街道': 'warning',
    '社区': 'primary',
    '其他': ''
  };
  return typeMap[type] || '';
};

// 分页处理
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  fetchOrganizations();
};

const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  fetchOrganizations();
};

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1;
  fetchOrganizations();
};

const handleSearchClear = () => {
  searchKeyword.value = '';
  handleSearch();
};

const handleFilterChange = () => {
  currentPage.value = 1;
  fetchOrganizations();
};

// 组织CRUD操作
const showAddDialog = () => {
  isEdit.value = false;
  orgForm.value = {
    id: 0,
    name: '',
    fullName: '',
    orgType: '',
    parentId: 0,
    description: ''
  };
  dialogVisible.value = true;
};

const handleView = (row: Organization) => {
  currentOrg.value = row;
  viewDialogVisible.value = true;
};

const handleEdit = (row: Organization) => {
  isEdit.value = true;
  orgForm.value = { ...row };
  dialogVisible.value = true;
};

const handleDelete = (row: Organization) => {
  ElMessage.success(`模拟删除组织: ${row.name}`);
  // 实际项目中应调用删除API
  // try {
  //   await api.delete(`/api/organizations/${row.id}`);
  //   ElMessage.success('删除成功');
  //   fetchOrganizations();
  // } catch (error) {
  //   ElMessage.error('删除失败');
  // }
  
  // 模拟删除
  organizations.value = organizations.value.filter(org => org.id !== row.id);
  total.value -= 1;
};

const handleDialogClose = () => {
  dialogVisible.value = false;
  orgFormRef.value?.resetFields();
};

const handleSubmit = async () => {
  if (!orgFormRef.value) return;
  
  await orgFormRef.value.validate(async (valid) => {
    if (valid) {
      // 实际项目中应调用API
      // const url = isEdit.value ? `/api/organizations/${orgForm.value.id}` : '/api/organizations';
      // const method = isEdit.value ? 'put' : 'post';
      // try {
      //   await api[method](url, orgForm.value);
      //   ElMessage.success(isEdit.value ? '更新成功' : '添加成功');
      //   dialogVisible.value = false;
      //   fetchOrganizations();
      // } catch (error) {
      //   ElMessage.error(isEdit.value ? '更新失败' : '添加失败');
      // }
      
      // 模拟操作
      if (isEdit.value) {
        const index = organizations.value.findIndex(org => org.id === orgForm.value.id);
        if (index !== -1) {
          organizations.value[index] = {
            ...organizations.value[index],
            ...orgForm.value
          };
        }
        ElMessage.success('更新成功');
      } else {
        const newOrg = {
          ...orgForm.value,
          id: Math.floor(Math.random() * 1000) + 10,
          memberCount: 0,
          createTime: new Date().toISOString().split('T')[0],
          parentName: orgForm.value.parentId ? 
            organizations.value.find(org => org.id === orgForm.value.parentId)?.name || '' : ''
        };
        organizations.value.push(newOrg);
        total.value += 1;
        ElMessage.success('添加成功');
      }
      
      dialogVisible.value = false;
      // 更新上级组织选项
      parentOrgOptions.value = [...organizations.value];
    }
  });
};
</script>

<script lang="ts">
// 需要在这里声明类型，以便在setup中使用
export interface Organization {
  id: number;
  name: string;
  fullName: string;
  orgType: string;
  memberCount: number;
  createTime: string;
  parentId: number;
  description?: string;
  parentName?: string;
}
</script>

<style scoped>
.organization-management {
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