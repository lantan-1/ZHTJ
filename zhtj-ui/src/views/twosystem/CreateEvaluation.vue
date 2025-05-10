<template>
  <div class="create-evaluation-container">
    <div class="page-header">
      <el-page-header @back="goBack" title="创建评议活动" />
    </div>

    <el-card class="form-card">
      <el-form 
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        status-icon
      >
        <el-form-item label="评议标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入评议标题" />
        </el-form-item>
        
        <el-form-item label="评议年度" prop="evaluationYear">
          <el-select v-model="form.evaluationYear" placeholder="请选择评议年度" style="width: 100%">
            <el-option 
              v-for="year in yearOptions" 
              :key="year" 
              :label="year" 
              :value="year" 
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="评议组织" prop="organizationId">
          <el-cascader
            v-model="form.organizationId"
            :options="organizationOptions"
            :props="{
              value: 'id',
              label: 'name',
              children: 'children',
              emitPath: false,
              checkStrictly: true
            }"
            placeholder="请选择评议组织"
            style="width: 100%"
            clearable
            filterable
          />
        </el-form-item>
        
        <el-form-item label="评议时间" prop="evaluationTime">
          <el-date-picker
            v-model="form.evaluationTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="评议说明" prop="description">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入评议说明" 
          />
        </el-form-item>
        
        <el-form-item label="评议选项" prop="evaluationOptions">
          <div class="options-header">
            <span>设置可选的评议结果</span>
            <el-button 
              type="primary" 
              link
              @click="form.evaluationOptions = defaultOptions"
            >恢复默认</el-button>
          </div>
          <div v-for="(option, index) in form.evaluationOptions" :key="index" class="option-item">
            <el-input 
              v-model="option.label" 
              placeholder="选项名称" 
              class="option-input"
            />
            <el-input 
              v-model="option.description" 
              placeholder="选项描述（可选）" 
              class="option-input"
            />
            <el-button 
              type="danger" 
              link
              :disabled="form.evaluationOptions.length <= 2"
              @click="removeOption(index)"
            >删除</el-button>
          </div>
          <el-button 
            type="primary" 
            link
            @click="addOption"
            class="add-option-btn"
          >添加选项</el-button>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm">创建评议活动</el-button>
          <el-button @click="resetForm">重置</el-button>
          <el-button @click="saveAsDraft">保存为草稿</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../../stores/user';
import { getOrganizationTree, getOrganizationDetail } from '../../api/organization';
import api from '../../api';

const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);

// 默认评议选项
const defaultOptions = [
  { label: '优秀', description: '表现优异，超额完成任务' },
  { label: '合格', description: '完成基本任务要求' },
  { label: '基本合格', description: '基本完成任务，但有待提高' },
  { label: '不合格', description: '未能完成基本任务要求' }
];

// 表单数据
const form = reactive({
  title: '',
  evaluationYear: new Date().getFullYear().toString(),
  organizationId: null,
  evaluationTime: [],
  description: '',
  evaluationOptions: JSON.parse(JSON.stringify(defaultOptions))
});

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入评议标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  evaluationYear: [
    { required: true, message: '请选择评议年度', trigger: 'change' }
  ],
  organizationId: [
    { required: true, message: '请选择评议组织', trigger: 'change' }
  ],
  evaluationTime: [
    { required: true, message: '请选择评议时间', trigger: 'change' }
  ]
};

// 组织数据
const organizationOptions = ref([]);

// 年份选项
const yearOptions = computed(() => {
  const currentYear = new Date().getFullYear();
  return Array.from({length: 5}, (_, i) => (currentYear - i).toString());
});

// 方法
const loadOrganizations = async () => {
  try {
    console.log('开始加载组织树结构...');
    
    // 获取当前用户的组织ID
    const userOrgId = userStore.organizationId;
    console.log('当前用户组织ID:', userOrgId);
    
    // 如果用户有组织ID，则只加载该组织及其子组织
    // 如果用户是超级管理员(没有组织限制)，则加载完整的组织树
    const response = await getOrganizationTree(userOrgId);
    console.log('组织树结构响应:', response);
    
    if (response && (response.success || response.code === 200)) {
      // 提取数据，兼容不同的响应格式
      let orgData = response.data || [];
      if (!Array.isArray(orgData) && orgData.data) {
        orgData = orgData.data;
      }
      
      // 确保组织数据有正确的children结构
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
      
      // 如果是团支部书记或团委书记，自动设置为当前组织
      if (userStore.isBranchSecretary || userStore.isCommitteeSecretary) {
        if (userStore.organizationId) {
          console.log('设置默认组织ID:', userStore.organizationId);
          form.organizationId = userStore.organizationId;
          
          // 检查是否在选项中
          const checkOrgExists = (orgs, orgId) => {
            for (const org of orgs) {
              if (org.id === orgId) return true;
              if (org.children && org.children.length > 0) {
                if (checkOrgExists(org.children, orgId)) return true;
              }
            }
            return false;
          };
          
          // 如果默认组织不在选项中，添加一个特殊选项
          if (!checkOrgExists(organizationOptions.value, userStore.organizationId)) {
            console.warn('未在组织树中找到当前用户组织，手动添加选项');
            // 获取组织详情
            try {
              const orgDetail = await getOrganizationDetail(userStore.organizationId);
              if (orgDetail && orgDetail.success && orgDetail.data) {
                organizationOptions.value.push({
                  id: orgDetail.data.id,
                  name: orgDetail.data.name || '当前组织',
                  children: []
                });
              }
            } catch (err) {
              console.error('获取当前组织详情失败:', err);
              // 添加一个临时选项
              organizationOptions.value.push({
                id: userStore.organizationId,
                name: '当前组织',
                children: []
              });
            }
          }
        }
      }
    } else {
      console.error('组织树数据格式不正确:', response);
      ElMessage.error(response?.message || '获取组织列表失败');
    }
  } catch (error) {
    console.error('加载组织列表失败:', error);
    ElMessage.error(`加载组织列表失败: ${error.message || '未知错误'}`);
  }
};

const goBack = () => {
  router.push('/dashboard/evaluations');
};

// 提交表单前验证组织权限
const checkOrganizationPermission = () => {
  // 如果用户是超级管理员，不受组织限制
  if (userStore.isAdmin) return true;
  
  // 获取当前用户组织ID
  const userOrgId = userStore.organizationId;
  if (!userOrgId) {
    ElMessage.error('您没有关联任何组织，无法创建评议活动');
    return false;
  }
  
  // 检查选择的组织是否是当前用户组织或其子组织
  // 这里假设API已经只返回了用户有权限的组织树，不需要再次检查
  return true;
};

// 增加一个获取选中组织名称的方法
const getSelectedOrganizationName = (orgId) => {
  if (!orgId) return '';
  
  // 递归查找组织
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

const submitForm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    // 检查组织权限
    if (!checkOrganizationPermission()) {
      return;
    }
    
    // 格式化提交数据 - 修改为后端可接受的格式 (不带时区信息)
    const startTime = formatDateTimeForBackend(form.evaluationTime[0]);
    const endTime = formatDateTimeForBackend(form.evaluationTime[1]);
    
    // 获取组织名称
    const organizationName = getSelectedOrganizationName(form.organizationId);
    console.log('选中组织ID:', form.organizationId, '组织名称:', organizationName);
    
    const data = {
      title: form.title,
      evaluationYear: form.evaluationYear,
      organizationId: form.organizationId,
      organizationName: organizationName, // 添加组织名称
      startTime,
      endTime,
      description: form.description,
      evaluationOptions: form.evaluationOptions,
      status: '进行中' // 直接创建为进行中状态
    };
    
    console.log('提交评议活动数据:', data);
    
    const response = await api.post('/api/member-evaluations', data);
    if (response.success || response.code === 200) {
      ElMessage.success('评议活动创建成功');
      router.push('/dashboard/evaluations');
    } else {
      ElMessage.error(response.message || '创建评议活动失败');
    }
  } catch (error) {
    console.error('创建评议活动失败:', error);
    if (error.message) {
      ElMessage.error(`创建评议活动失败: ${error.message}`);
    } else {
      ElMessage.error('创建评议活动失败');
    }
  }
};

const saveAsDraft = async () => {
  try {
    // 不进行完整验证，只检查必要字段
    if (!form.title) {
      ElMessage.warning('请至少填写评议标题');
      return;
    }
    
    // 检查组织权限
    if (!checkOrganizationPermission()) {
      return;
    }
    
    // 获取组织名称
    const organizationName = getSelectedOrganizationName(form.organizationId);
    
    // 准备数据
    const data = {
      title: form.title,
      evaluationYear: form.evaluationYear,
      organizationId: form.organizationId,
      organizationName: organizationName, // 添加组织名称
      startTime: form.evaluationTime && form.evaluationTime.length > 0 
        ? formatDateTimeForBackend(form.evaluationTime[0]) 
        : null,
      endTime: form.evaluationTime && form.evaluationTime.length > 1 
        ? formatDateTimeForBackend(form.evaluationTime[1]) 
        : null,
      description: form.description,
      evaluationOptions: form.evaluationOptions,
      status: '草稿' // 保存为草稿状态
    };
    
    console.log('保存草稿数据:', data);
    
    const response = await api.post('/api/member-evaluations', data);
    if (response.success || response.code === 200) {
      ElMessage.success('草稿保存成功');
      router.push('/dashboard/evaluations');
    } else {
      ElMessage.error(response.message || '保存草稿失败');
    }
  } catch (error) {
    console.error('保存草稿失败:', error);
    ElMessage.error('保存草稿失败');
  }
};

const resetForm = () => {
  if (!formRef.value) return;
  formRef.value.resetFields();
  form.evaluationOptions = JSON.parse(JSON.stringify(defaultOptions));
};

const addOption = () => {
  form.evaluationOptions.push({ label: '', description: '' });
};

const removeOption = (index) => {
  if (form.evaluationOptions.length <= 2) {
    ElMessage.warning('至少需要保留两个评议选项');
    return;
  }
  form.evaluationOptions.splice(index, 1);
};

// 日期时间格式化函数 - 将JS日期转换为后端可接受的格式
const formatDateTimeForBackend = (date) => {
  if (!date) return null;
  
  // 获取本地日期的年、月、日，确保是当地时间而非UTC时间
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  // 组合成 YYYY-MM-DD 00:00:00 格式，确保日期准确
  return `${year}-${month}-${day} 00:00:00`;
};

// 生命周期钩子
onMounted(() => {
  loadOrganizations();
});
</script>

<style scoped>
.create-evaluation-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.form-card {
  max-width: 800px;
  margin: 0 auto;
}

.options-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.option-input {
  margin-right: 10px;
}

.add-option-btn {
  margin-top: 10px;
}
</style> 