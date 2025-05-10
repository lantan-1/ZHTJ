<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  getApprovalTransfers, 
  getTransferDetail, 
  outApprove, 
  inApprove, 
  getStatusText, 
  getStatusColor
} from '../../api/transfer';
// 使用type导入类型
import type { 
  TransferItem,
  TransferDetail as TransferDetailType,
  ApprovalParams
} from '../../api/transfer';
import { View, Check, Close, Refresh } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const approveLoading = ref(false);
const activeTab = ref('out');
const currentStatus = ref('');

// 当前审批类型：out-转出审批，in-转入审批
const approvalType = ref<'out' | 'in'>('out');

// 分页
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 转接申请列表
const transfers = ref<TransferItem[]>([]);

// 审批表单
const approvalForm = ref<ApprovalParams>({
  approval: true,
  approvalRemark: ''
});

// 审批对话框
const approvalDialogVisible = ref(false);
const currentTransferId = ref<number | null>(null);

// 状态选项
const statusOptions = [
  { value: '', label: '全部' },
  { value: '0', label: '申请中' },
  { value: '1', label: '转出审批中' },
  { value: '2', label: '转入审批中' },
  { value: '3', label: '已通过' },
  { value: '4', label: '已拒绝' }
];

// 检查当前用户是否有审批权限
const hasApprovalPermission = computed(() => {
  return userStore.isCommitteeSecretary || userStore.isBranchSecretary;
});

// 加载转接申请列表
const loadTransfers = async () => {
  loading.value = true;
  try {
    console.log('开始加载转接审批列表...', { 
      页码: currentPage.value, 
      每页条数: pageSize.value, 
      状态: currentStatus.value, 
      类型: approvalType.value 
    });
    
    const response = await getApprovalTransfers({
      page: currentPage.value,
      size: pageSize.value,
      status: currentStatus.value,
      type: approvalType.value
    });
    
    console.log('API返回数据:', JSON.stringify(response, null, 2));
    
    if (response && (response.code === 200 || response.success === true)) {
      // 确保data和list存在
      if (!response.data) {
        console.warn('API返回数据缺少data字段:', response);
        response.data = { list: [], total: 0 };
      }
      
      // 处理data非标准格式的情况 - 如果data不包含list属性但包含user属性
      if (!response.data.list && response.data.user) {
        console.warn('API返回数据格式非标准，尝试处理单个用户数据:', response.data);
        
        // 构造一个包含用户数据的list
        const userData = response.data.user;
        const transferItem: any = {
          id: userData.id,
          transferUserId: userData.id,
          transferUserName: userData.name || '未知用户名',
          statusCode: 0, // 默认状态为"申请中"
          status: '申请中',
          transferOutOrgName: response.data.org_name || '未知组织',
          transferInOrgName: '目标组织', // 默认值
          applicationTime: userData.createTime || new Date().toISOString()
        };
        
        response.data.list = [transferItem];
        response.data.total = 1;
        
        console.log('已将非标准用户数据转换为转接申请列表项:', transferItem);
      } else if (!response.data.list) {
        console.warn('API返回数据缺少list字段，尝试使用整个data作为列表:', response.data);
        
        // 检查data是否为数组
        if (Array.isArray(response.data)) {
          response.data = { list: response.data, total: response.data.length };
        } else {
          // data不是数组且不包含list，设置为空列表
          response.data.list = [];
          response.data.total = 0;
        }
      }
      
      // 格式化转接申请数据
      const formattedList = formatTransferList(response.data.list || []);
      transfers.value = formattedList;
      total.value = response.data.total || 0;
      
      console.log(`成功加载${approvalType.value}类型转接申请:`, transfers.value.length, '条数据');
      console.log('转接申请数据:', transfers.value);
    } else {
      console.error('API返回错误:', response);
      ElMessage.error(response?.message || '获取转接申请列表失败');
    }
  } catch (error) {
    console.error('获取转接申请列表失败', error);
    ElMessage.error('获取转接申请列表失败：' + (error instanceof Error ? error.message : String(error)));
  } finally {
    loading.value = false;
  }
};

// 格式化转接申请列表数据
const formatTransferList = (list: any[]): TransferItem[] => {
  return list.map(item => {
    // 确保statusCode是数字
    if (typeof item.statusCode === 'string') {
      item.statusCode = parseInt(item.statusCode, 10);
    }
    
    // 确保状态文本一致
    if (!item.status || item.status === '') {
      item.status = getStatusText(item.statusCode);
    }
    
    // 确保必要的组织名称字段存在
    if (!item.transferOutOrgName && item.transferOutOrg) {
      if (typeof item.transferOutOrg === 'string') {
        item.transferOutOrgName = item.transferOutOrg;
      } else if (item.transferOutOrg && item.transferOutOrg.name) {
        item.transferOutOrgName = item.transferOutOrg.name;
      }
    }
    
    if (!item.transferInOrgName && item.transferInOrg) {
      if (typeof item.transferInOrg === 'string') {
        item.transferInOrgName = item.transferInOrg;
      } else if (item.transferInOrg && item.transferInOrg.name) {
        item.transferInOrgName = item.transferInOrg.name;
      }
    }
    
    // 确保用户名称字段存在
    if (!item.transferUserName && item.transferUser) {
      if (typeof item.transferUser === 'string') {
        item.transferUserName = item.transferUser;
      } else if (item.transferUser && item.transferUser.name) {
        item.transferUserName = item.transferUser.name;
      }
    }
    
    // 调试用户名称字段
    console.log('转接申请项目的申请人信息:', {
      id: item.id,
      transferUserId: item.transferUserId,
      transferUserName: item.transferUserName,
      transferUser: item.transferUser,
      原始数据: item
    });
    
    // 如果仍然没有transferUserName，但有transferUserId
    if (!item.transferUserName && item.transferUserId) {
      console.warn(`申请ID ${item.id} 缺少申请人姓名但有ID: ${item.transferUserId}`);
      item.transferUserName = `用户${item.transferUserId}`;
    }
    
    return item as TransferItem;
  });
};

// 通过ID获取转接申请详情
const getTransferById = async (id: number) => {
  try {
    const response = await getTransferDetail(id);
    if (response && response.success) {
      return response.data;
    }
    return null;
  } catch (error) {
    console.error('获取转接申请详情失败', error);
    return null;
  }
};

// 处理审批操作
const handleApprove = async (id: number, approval: boolean) => {
  currentTransferId.value = id;
  approvalForm.value.approval = approval;
  approvalForm.value.approvalRemark = '';
  approvalDialogVisible.value = true;
};

// 提交审批
const submitApproval = async () => {
  if (!currentTransferId.value) {
    ElMessage.error('未找到转接申请ID');
    return;
  }
  
  try {
    approveLoading.value = true;
    
    const approvalApi = approvalType.value === 'out' ? outApprove : inApprove;
    console.log('提交审批请求，ID:', currentTransferId.value, '参数:', approvalForm.value, '审批类型:', approvalType.value);
    
    const response = await approvalApi(currentTransferId.value, approvalForm.value);
    console.log('审批API响应:', JSON.stringify(response, null, 2));
    
    // 修改判断条件，使用code状态码判断是否成功
    if (response && (response.code === 200 || response.success === true)) {
      console.log('审批操作成功，刷新列表');
      ElMessage.success(response.message || '审批操作成功');
      approvalDialogVisible.value = false;
      await loadTransfers();
      
      // 如果是转入审批成功（且审批为同意），则刷新用户信息
      if (approvalType.value === 'in' && approvalForm.value.approval === true) {
        console.log('转入审批成功，刷新用户信息');
        // 使用userStore刷新用户信息，确保获取最新的组织信息
        await userStore.fetchUserInfo();
      }
    } else {
      console.error('审批操作失败，服务端返回:', response);
      ElMessage.error(response?.message || '审批操作失败');
    }
  } catch (error) {
    console.error('审批操作失败', error);
    ElMessage.error('审批操作失败：' + (error instanceof Error ? error.message : String(error)));
  } finally {
    approveLoading.value = false;
  }
};

// 查看转接申请详情
const viewTransfer = (id: number) => {
  router.push(`/dashboard/transfers/detail/${id}`);
};

// 切换标签页
const handleTabChange = (tab: any) => {
  approvalType.value = tab.props?.name || 'out';
  currentPage.value = 1;
  currentStatus.value = '';
  console.log('切换到标签页:', approvalType.value);
  loadTransfers();
};

// 状态筛选变化
const handleStatusChange = () => {
  currentPage.value = 1;
  console.log('状态筛选变化:', currentStatus.value);
  loadTransfers();
};

// 获取状态类型
const getStatusType = (statusCode: number | string): 'primary' | 'success' | 'warning' | 'danger' | 'info' => {
  // 将字符串转换为数字
  const code = typeof statusCode === 'string' ? parseInt(statusCode, 10) : statusCode;
  
  switch (code) {
    case 0: return 'primary';
    case 1: return 'warning';
    case 2: return 'warning';
    case 3: return 'success';
    case 4: return 'danger';
    default: return 'info';
  }
};

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  loadTransfers();
};

// 处理页码变化
const handleCurrentChange = (val: number) => {
  currentPage.value = val;
  loadTransfers();
};

// 格式化日期时间
const formatDateTime = (dateStr: string) => {
  if (!dateStr) return '-';
  try {
    const date = new Date(dateStr);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    return dateStr;
  }
};

// 刷新列表
const refreshList = () => {
  loadTransfers();
};

// 组件挂载时加载数据
onMounted(() => {
  console.log('用户角色信息:', {
    isCommitteeSecretary: userStore.isCommitteeSecretary,
    isBranchSecretary: userStore.isBranchSecretary,
    leaguePosition: userStore.leaguePosition
  });
  
  if (!hasApprovalPermission.value) {
    ElMessage.warning('您没有审批权限，请联系管理员');
    router.push('/dashboard');
    return;
  }
  
  loadTransfers();
});
</script>

<template>
  <div class="transfer-approve-container">
    <el-card class="transfer-approve-card">
      <template #header>
        <div class="card-header">
          <h2>团员关系转接审批</h2>
          <el-button 
            type="primary"
            :icon="Refresh"
            @click="refreshList"
            circle
            title="刷新列表"
          />
        </div>
      </template>
      
      <el-tabs v-model="approvalType" @tab-click="handleTabChange">
        <el-tab-pane label="转出审批" name="out">
          <div class="filter-container">
            <div class="filter-item">
              <span class="filter-label">状态：</span>
              <el-select 
                v-model="currentStatus" 
                placeholder="选择状态" 
                @change="handleStatusChange"
              >
                <el-option 
                  v-for="option in statusOptions" 
                  :key="option.value" 
                  :label="option.label" 
                  :value="option.value"
                />
              </el-select>
            </div>
          </div>
          
          <el-table 
            :data="transfers" 
            v-loading="loading" 
            border 
            style="width: 100%"
            :empty-text="'暂无转出审批数据'"
          >
            <el-table-column type="index" label="序号" width="80" align="center" />
            
            <el-table-column prop="applicationTime" label="申请时间" min-width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.applicationTime) }}
              </template>
            </el-table-column>
            
            <el-table-column prop="transferUserName" label="申请人" min-width="120">
              <template #default="scope">
                <span v-if="scope.row.transferUserName">{{ scope.row.transferUserName }}</span>
                <span v-else-if="scope.row.transferUserId">用户ID: {{ scope.row.transferUserId }}</span>
                <span v-else>未知用户</span>
              </template>
            </el-table-column>
            
            <el-table-column prop="transferOutOrgName" label="转出组织" min-width="180" />
            
            <el-table-column prop="transferInOrgName" label="转入组织" min-width="180" />
            
            <el-table-column prop="status" label="状态" min-width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.statusCode)">
                  {{ typeof scope.row.statusCode === 'number' ? getStatusText(scope.row.statusCode) : scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="200" align="center">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small" 
                  :icon="View" 
                  @click="viewTransfer(scope.row.id)"
                  circle
                  title="查看详情"
                />
                <!-- 转出审批按钮 - 当为转出审批类型且状态为申请中或转出审批中时显示 -->
                <template v-if="approvalType === 'out' && (scope.row.statusCode === 0 || scope.row.statusCode === 1)">
                <el-button 
                  type="success" 
                  size="small" 
                  :icon="Check"
                  @click="handleApprove(scope.row.id, true)"
                  circle
                  title="同意"
                />
                <el-button 
                  type="danger" 
                  size="small" 
                  :icon="Close"
                  @click="handleApprove(scope.row.id, false)"
                  circle
                  title="拒绝"
                />
                </template>
                <!-- 已审批记录的状态标识 -->
                <template v-if="scope.row.statusCode > 2">
                  <el-tag 
                    :type="scope.row.statusCode === 3 ? 'success' : 'danger'" 
                    size="small"
                  >
                    {{ scope.row.statusCode === 3 ? '已通过' : '已拒绝' }}
                  </el-tag>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="转入审批" name="in">
          <div class="filter-container">
            <div class="filter-item">
              <span class="filter-label">状态：</span>
              <el-select 
                v-model="currentStatus" 
                placeholder="选择状态" 
                @change="handleStatusChange"
              >
                <el-option 
                  v-for="option in statusOptions" 
                  :key="option.value" 
                  :label="option.label" 
                  :value="option.value"
                />
              </el-select>
            </div>
          </div>
          
          <el-table 
            :data="transfers" 
            v-loading="loading" 
            border 
            style="width: 100%"
            :empty-text="'暂无转入审批数据'"
          >
            <el-table-column type="index" label="序号" width="80" align="center" />
            
            <el-table-column prop="applicationTime" label="申请时间" min-width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.applicationTime) }}
              </template>
            </el-table-column>
            
            <el-table-column prop="transferUserName" label="申请人" min-width="120">
              <template #default="scope">
                <span v-if="scope.row.transferUserName">{{ scope.row.transferUserName }}</span>
                <span v-else-if="scope.row.transferUserId">用户ID: {{ scope.row.transferUserId }}</span>
                <span v-else>未知用户</span>
              </template>
            </el-table-column>
            
            <el-table-column prop="transferOutOrgName" label="转出组织" min-width="180" />
            
            <el-table-column prop="transferInOrgName" label="转入组织" min-width="180" />
            
            <el-table-column prop="status" label="状态" min-width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.statusCode)">
                  {{ typeof scope.row.statusCode === 'number' ? getStatusText(scope.row.statusCode) : scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="200" align="center">
              <template #default="scope">
                <el-button 
                  type="primary" 
                  size="small" 
                  :icon="View" 
                  @click="viewTransfer(scope.row.id)"
                  circle
                  title="查看详情"
                />
                <!-- 转入审批按钮 - 当为转入审批类型且状态为转入审批中时显示 -->
                <template v-if="approvalType === 'in' && scope.row.statusCode === 2">
                <el-button 
                  type="success" 
                  size="small" 
                  :icon="Check"
                  @click="handleApprove(scope.row.id, true)"
                  circle
                  title="同意"
                />
                <el-button 
                  type="danger" 
                  size="small" 
                  :icon="Close"
                  @click="handleApprove(scope.row.id, false)"
                  circle
                  title="拒绝"
                />
                </template>
                <!-- 已审批记录的状态标识 -->
                <template v-if="scope.row.statusCode > 2">
                  <el-tag 
                    :type="scope.row.statusCode === 3 ? 'success' : 'danger'" 
                    size="small"
                  >
                    {{ scope.row.statusCode === 3 ? '已通过' : '已拒绝' }}
                  </el-tag>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      
      <div class="pagination-container" v-if="total > 0">
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
      
      <!-- 审批对话框 -->
      <el-dialog
        v-model="approvalDialogVisible"
        title="审批转接申请"
        width="500px"
      >
        <el-form :model="approvalForm" label-width="100px">
          <el-form-item label="审批结果">
            <el-radio-group v-model="approvalForm.approval">
              <el-radio :label="true">同意</el-radio>
              <el-radio :label="false">拒绝</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审批说明">
            <el-input
              v-model="approvalForm.approvalRemark"
              type="textarea"
              :rows="4"
              placeholder="请输入审批说明（可选）"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="approvalDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitApproval" :loading="approveLoading">
              确定
            </el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<style scoped>
.transfer-approve-container {
  padding: 20px;
}

.transfer-approve-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
}

.filter-item {
  margin-right: 15px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.filter-label {
  margin-right: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 