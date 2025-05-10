<template>
  <div class="org-structure-container">
    <el-card class="org-structure-card">
      <template #header>
        <div class="card-header">
          <h2>组织结构维护</h2>
        </div>
      </template>
      
      <div class="repair-section">
        <p class="description">
          此工具用于修复组织结构中的数据问题。点击"修复组织结构"按钮将重建组织树关系。
        </p>
        
        <el-button 
          type="primary" 
          :loading="repairing" 
          @click="repairStructure">
          修复组织结构
        </el-button>
        
        <el-alert
          v-if="repairResult"
          :title="repairResult.success ? '修复成功' : '修复失败'"
          :type="repairResult.success ? 'success' : 'error'"
          :description="repairResult.message"
          show-icon
          :closable="true"
          class="result-alert"
        />
      </div>
      
      <div class="tree-section" v-loading="loading">
        <div class="section-header">
          <h3>当前组织树结构</h3>
          <el-button size="small" @click="loadOrganizationTree">刷新</el-button>
        </div>
        
        <el-tree
          :data="organizationTree"
          :props="defaultProps"
          default-expand-all
          node-key="id"
          :expand-on-click-node="false"
        >
          <template #default="{ node, data }">
            <div class="custom-tree-node">
              <span class="node-title">{{ data.name || '未命名节点' }}</span>
              <span class="node-info">
                ID: {{ data.id }} | 层级: {{ data.level || '?' }} | Path: {{ data.path || '?' }}
              </span>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { getOrganizationTree, repairOrganizationStructure } from '../../api/organization';
import { ElMessage } from 'element-plus';

// 状态
const loading = ref(false);
const repairing = ref(false);
const organizationTree = ref<any[]>([]);
const repairResult = ref<{ success: boolean; message: string } | null>(null);

// 配置
const defaultProps = {
  children: 'children',
  label: 'name'
};

// 加载组织树
const loadOrganizationTree = async () => {
  loading.value = true;
  try {
    console.log('开始加载组织树');
    const response = await getOrganizationTree();
    
    if (response && response.success && response.data) {
      organizationTree.value = response.data;
      console.log('成功加载组织树:', organizationTree.value);
    } else {
      console.error('加载组织树失败:', response?.message || '未知错误');
      ElMessage.error('加载组织树失败');
    }
  } catch (error) {
    console.error('加载组织树异常:', error);
    ElMessage.error('加载组织树时发生错误');
  } finally {
    loading.value = false;
  }
};

// 修复组织结构
const repairStructure = async () => {
  repairing.value = true;
  repairResult.value = null;
  
  try {
    console.log('开始修复组织结构');
    const response = await repairOrganizationStructure();
    
    if (response && response.success) {
      console.log('修复组织结构成功:', response.data);
      repairResult.value = {
        success: true,
        message: '组织结构修复成功'
      };
      
      // 重新加载组织树以显示最新结构
      await loadOrganizationTree();
      
      ElMessage.success('组织结构修复成功');
    } else {
      console.error('修复组织结构失败:', response?.message);
      repairResult.value = {
        success: false,
        message: response?.message || '修复失败，未知错误'
      };
      ElMessage.error(response?.message || '修复组织结构失败');
    }
  } catch (error: any) {
    console.error('修复组织结构异常:', error);
    repairResult.value = {
      success: false,
      message: error?.message || '修复过程中发生异常'
    };
    ElMessage.error('修复组织结构时发生错误');
  } finally {
    repairing.value = false;
  }
};

// 组件挂载时加载组织树
onMounted(() => {
  loadOrganizationTree();
});
</script>

<style scoped>
.org-structure-container {
  padding: 20px;
}

.org-structure-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-header h2 {
  font-size: 18px;
  margin: 0;
  color: #333;
}

.repair-section {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.description {
  margin-bottom: 20px;
  color: #606266;
}

.result-alert {
  margin-top: 20px;
}

.tree-section {
  margin-top: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
}

.custom-tree-node {
  display: flex;
  flex-direction: column;
  padding: 4px 0;
}

.node-title {
  font-weight: bold;
}

.node-info {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style> 