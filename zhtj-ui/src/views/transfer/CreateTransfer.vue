<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createTransfer, getTransferReasons } from '../../api/transfer';
import { getOrganizationTree } from '../../api/organization';
import type { TransferRequest } from '../../api/transfer';

// 扩展TransferRequest类型，使转入组织ID可以为null
interface ExtendedTransferRequest extends Omit<TransferRequest, 'transferInOrgId'> {
  transferInOrgId: number | null;
}

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);

// 计算属性：当前组织名称
const currentOrgName = computed(() => {
  // 优先显示organizationFullName（对应API中的full_name）
  // 如果不存在，则显示organization（对应API中的org_name）
  return userStore.organizationFullName || userStore.organization || '未获取到组织信息';
});

// 转接理由选项
const reasonOptions = getTransferReasons();

// 转接申请表单
const transferForm = reactive<ExtendedTransferRequest>({
  transferInOrgId: null, // 使用null作为初始值
  transferReason: '',
  transferRemark: ''
});

// 组织树数据
const organizationTree = ref<any[]>([]);
const organizationLoading = ref(false);

// 加载组织树数据
const loadOrganizationTree = async () => {
  organizationLoading.value = true;
  try {
    console.log('开始加载组织树数据');
    // 获取组织树
    const response = await getOrganizationTree();
    
    console.log('组织树API响应:', response);
    
    if (response && response.success && response.data) {
      // 转换服务器返回的数据为树形结构
      let treeData = response.data;
      
      // 详细记录当前数据结构
      console.log('原始数据结构:', {
        isArray: Array.isArray(treeData),
        type: typeof treeData,
        keys: treeData && typeof treeData === 'object' ? Object.keys(treeData) : '无法获取keys'
      });
      
      // 检查是否需要进一步处理数据结构
      if (!Array.isArray(treeData) && treeData.data && Array.isArray(treeData.data)) {
        console.log('从嵌套data字段中提取数组数据');
        treeData = treeData.data;
      }
      
      // 如果仍不是数组，包装成数组
      if (!Array.isArray(treeData)) {
        console.log('数据不是数组，将其包装为数组');
        treeData = [treeData];
      }
      
      // 处理空数组情况
      if (Array.isArray(treeData) && treeData.length === 0) {
        console.log('组织树数据为空数组，使用默认数据');
        useDefaultOrgTree();
        return;
      }
      
      organizationTree.value = Array.isArray(treeData) 
        ? treeData.map((org: any) => formatOrgTreeNode(org))
        : [formatOrgTreeNode(treeData)];
      
      console.log('格式化后的组织树数据:', organizationTree.value);
    } else {
      console.warn('获取组织树失败或数据为空，使用默认数据');
      // 加载失败时使用静态数据（数据库中实际存在的组织）
      useDefaultOrgTree();
    }
  } catch (error) {
    console.error('加载组织树失败', error);
    ElMessage.error('加载组织树失败');
    
    // 加载失败时使用静态数据
    useDefaultOrgTree();
  } finally {
    organizationLoading.value = false;
  }
};

// 使用默认的组织树数据
const useDefaultOrgTree = () => {
  console.log('使用默认组织树数据');
    organizationTree.value = [
      {
        id: 3,
        label: '清华大学团委',
        children: [
          {
            id: 14,
            label: '计算机学院团委',
            children: [
              {
                id: 15,
                label: '计算机科学与技术系团支部'
            },
            {
              id: 151,
              label: '软件工程系团支部'
              }
            ]
          },
          {
            id: 16,
          label: '电子工程学院团委',
          children: [
            {
              id: 161,
              label: '电子工程系团支部'
            },
            {
              id: 162,
              label: '信息与通信工程系团支部'
            }
          ]
        },
        {
          id: 17,
          label: '机械工程学院团委',
          children: [
            {
              id: 171,
              label: '机械工程系团支部'
            }
          ]
        },
        {
          id: 18,
          label: '经济管理学院团委',
          children: [
            {
              id: 181,
              label: '经济系团支部'
            },
            {
              id: 182,
              label: '金融系团支部'
            }
          ]
          }
        ]
      }
    ];
};

// 格式化组织树节点
const formatOrgTreeNode = (org: any) => {
  if (!org) {
    console.warn('尝试格式化空组织节点');
    return {
      id: 0,
      label: '未知组织',
      children: []
    };
  }
  
  console.log('格式化组织节点:', org);
  
  const node = {
    id: org.id || 0,
    label: org.name || org.fullName || org.label || '未命名组织',
    children: []
  };
  
  if (org.children && Array.isArray(org.children) && org.children.length > 0) {
    node.children = org.children
      .filter((child: any) => child !== null && typeof child === 'object')
      .map((child: any) => formatOrgTreeNode(child));
  }
  
  return node;
};

// 表单校验规则
const rules = {
  transferInOrgId: [
    { required: true, message: '请选择转入组织', trigger: 'change' }
  ],
  transferReason: [
    { required: true, message: '请选择转接理由', trigger: 'change' }
  ]
};

// 表单提交
const submitForm = async () => {
  // 表单验证
  if (!transferForm.transferInOrgId) {
    ElMessage.warning('请选择转入组织');
    return;
  }
  if (!transferForm.transferReason) {
    ElMessage.warning('请选择转接理由');
    return;
  }
  
  // 确认提交
  try {
    await ElMessageBox.confirm('确定要提交转接申请吗？', '提交确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    loading.value = true;
    // 提交时确保transferInOrgId为数字类型
    const submitData: TransferRequest = {
      transferInOrgId: Number(transferForm.transferInOrgId),
      transferReason: transferForm.transferReason,
      transferRemark: transferForm.transferRemark
    };
    const response = await createTransfer(submitData);
    
    if (response && response.success) {
      ElMessage.success('转接申请提交成功');
      router.push('/dashboard/transfers');
    } else {
      // 处理特定错误信息
      if (response?.message?.includes('已有正在进行的转接申请')) {
        ElMessage.warning('您已有一个正在处理的转接申请，不能同时提交多个申请');
      } else {
        ElMessage.error(response?.message || '转接申请提交失败');
      }
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('提交转接申请失败', error);
      
      // 处理特定错误消息
      if (error.message?.includes('已有正在进行的转接申请')) {
        ElMessage.warning('您已有一个正在处理的转接申请，不能同时提交多个申请');
      } else {
        ElMessage.error('提交转接申请失败: ' + (error.message || '未知错误'));
      }
    }
  } finally {
    loading.value = false;
  }
};

// 取消提交
const cancelSubmit = () => {
  router.push('/dashboard/transfers');
};

// 组件加载时获取组织树和用户所属组织
onMounted(async () => {
  // 确保用户组织信息已加载
  if (!userStore.organizationFullName && !userStore.organization) {
    try {
      await userStore.fetchUserInfo();
    } catch (error) {
      console.error('获取用户信息失败', error);
    }
  }
  
  // 加载组织树
  loadOrganizationTree();
});
</script>

<template>
  <div class="create-transfer-container">
    <el-card class="create-transfer-card">
      <template #header>
        <div class="card-header">
          <h2>发起团员关系转接申请</h2>
        </div>
      </template>
      
      <el-form 
        :model="transferForm" 
        :rules="rules" 
        label-position="top" 
        class="transfer-form"
        v-loading="loading"
      >
        <el-form-item label="当前所属组织" required>
          <el-input 
            v-model="currentOrgName"
            disabled 
            placeholder="当前所属组织"
          />
        </el-form-item>
        
        <el-form-item label="转入组织" prop="transferInOrgId" required>
          <el-tree-select
            v-model="transferForm.transferInOrgId"
            :data="organizationTree"
            placeholder="请选择转入组织"
            check-strictly
            node-key="id"
            :props="{ label: 'label', children: 'children' }"
            :loading="organizationLoading"
            clearable
            value-key="id"
            :render-after-expand="false"
          />
        </el-form-item>
        
        <el-form-item label="转接理由" prop="transferReason" required>
          <el-select 
            v-model="transferForm.transferReason" 
            placeholder="请选择转接理由"
            style="width: 100%"
          >
            <el-option 
              v-for="option in reasonOptions" 
              :key="option.value" 
              :label="option.label" 
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="备注信息" prop="transferRemark">
          <el-input 
            v-model="transferForm.transferRemark" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入备注信息（可选）"
          />
        </el-form-item>
        
        <el-form-item class="action-buttons">
          <el-button type="primary" @click="submitForm" :loading="loading">提交申请</el-button>
          <el-button @click="cancelSubmit">取消</el-button>
        </el-form-item>
      </el-form>
      
      <div class="transfer-notes">
        <h3>注意事项</h3>
        <ol>
          <li>请确保您输入的组织信息准确无误。</li>
          <li>转接申请递交后需经过转出组织和转入组织双方审批。</li>
          <li>转接申请有效期为3个月，超过有效期未完成审批的申请将自动作废。</li>
          <li>关系转接完成后，您的组织关系将转到目标组织的档案，原组织关系将被取消。</li>
        </ol>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.create-transfer-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.create-transfer-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header h2 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.transfer-form {
  margin-top: 20px;
}

.action-buttons {
  margin-top: 30px;
  text-align: center;
}

.transfer-notes {
  margin-top: 30px;
  padding: 15px;
  background-color: #fafafa;
  border-left: 4px solid #faad14;
  text-align: left;
}

.transfer-notes h3 {
  font-size: 16px;
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
  text-align: center;
  width: 100%;
}

.transfer-notes ol {
  margin: 0;
  padding-left: 20px;
  text-align: left;
}

.transfer-notes li {
  margin-bottom: 8px;
  color: #666;
  line-height: 1.5;
  text-align: left;
  padding-left: 5px;
}
</style> 