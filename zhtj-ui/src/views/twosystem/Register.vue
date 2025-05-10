<template>
  <div class="register-container">
    <div class="page-header">
      <h2>年度团籍注册</h2>
    </div>

    <!-- 注册批次卡片 -->
    <el-card class="batch-card" v-loading="batchLoading">
      <template #header>
        <div class="card-header">
          <span>当前注册批次</span>
          <el-button 
            type="primary" 
            size="small"
            @click="navigateToBatchManagement"
            v-if="isAdmin"
          >批次管理</el-button>
        </div>
      </template>

      <div v-if="currentBatch">
        <el-alert
          v-if="currentBatch && (currentBatch.status === '进行中' || currentBatch.status?.includes('进行'))"
          type="success"
          show-icon
          :title="`正在进行 ${currentBatch.registerYear || currentBatch.year || '--'}年度团籍注册`"
          :description="`请在截止日期 ${formatDate(currentBatch.endDate || currentBatch.endTime) || '--' } 前完成注册`"
        />
        <el-alert
          v-else-if="currentBatch && (currentBatch.status === '未开始' || currentBatch.status?.includes('未开始'))"
          type="info"
          show-icon
          :title="`${currentBatch.registerYear || currentBatch.year || '--'}年度团籍注册将于 ${formatDate(currentBatch.startDate || currentBatch.startTime) || '--' } 开始`"
          :description="'请在开始日期后进行注册'"
        />
        <el-alert
          v-else-if="currentBatch && (currentBatch.status === '已结束' || currentBatch.status?.includes('结束'))"
          type="warning"
          show-icon
          :title="`${currentBatch.registerYear || currentBatch.year || '--'}年度团籍注册已结束`"
          :description="'本批次注册已截止，请联系管理员'"
        />

        <div class="batch-info">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">批次名称</div>
                <div class="info-value">{{ currentBatch.batchCode || currentBatch.batchName || '--' }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">注册年度</div>
                <div class="info-value">{{ (currentBatch.registerYear || currentBatch.year || '--') + '年度' }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">批次状态</div>
                <div class="info-value">
                  <el-tag :type="getStatusType(currentBatch.status || '')">
                    {{ currentBatch.status || '未知' }}
                  </el-tag>
                </div>
              </div>
            </el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">开始日期</div>
                <div class="info-value">{{ formatDate(currentBatch.startDate || currentBatch.startTime) || '--' }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">截止日期</div>
                <div class="info-value">{{ formatDate(currentBatch.endDate || currentBatch.endTime) || '--' }}</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <div class="info-label">距离截止</div>
                <div class="info-value countdown">
                  <template v-if="currentBatch && (currentBatch.status === '进行中' || currentBatch.status?.includes('进行'))">
                    {{ countdownText }}
                  </template>
                  <template v-else>
                    --
                  </template>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="batch-description" v-if="currentBatch.description">
          <div class="description-title">注册说明</div>
          <div class="description-content">{{ currentBatch.description }}</div>
        </div>
      </div>
      
      <el-empty 
        v-else 
        description="当前没有正在进行的团籍注册批次" 
      />
    </el-card>

    <!-- 注册状态卡片
    <el-card class="status-card" v-loading="registerLoading">
      <template #header>
        <div class="card-header">
          <span>我的注册状态</span>
          <el-button 
            type="primary" 
            size="small"
            @click="register"
            :disabled="!canRegister"
          >立即注册</el-button>
        </div>
      </template>

      <div v-if="registerStatus">
        <el-steps :active="getRegisterStep(registerStatus?.status)" finish-status="success" align-center>
          <el-step title="申请注册" :description="formatDate(registerStatus?.registerTime || registerStatus?.applyTime)" />
          <el-step title="团支部审核" :description="formatDate(registerStatus?.branchApproveTime)" />
          <el-step title="团委审核" :description="formatDate(registerStatus?.committeeApproveTime)" />
          <el-step title="注册完成" :description="formatDate(registerStatus?.approvalTime || registerStatus?.finishTime)" />
        </el-steps>

        <div class="register-info">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="姓名">{{ registerStatus?.memberName || registerStatus?.userName || userStore.name || '--' }}</el-descriptions-item>
            <el-descriptions-item label="学号/工号">{{ registerStatus?.memberCode || registerStatus?.leagueNumber || '--' }}</el-descriptions-item>
            <el-descriptions-item label="所属组织">{{ registerStatus?.organizationName || userStore.organization_name || '--' }}</el-descriptions-item>
            <el-descriptions-item label="注册状态">
              <el-tag :type="getStatusType(registerStatus?.status || '')">{{ registerStatus?.status || '未知' }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ formatDate(registerStatus?.registerTime || registerStatus?.applyTime) }}</el-descriptions-item>
            <el-descriptions-item label="完成时间">{{ formatDate(registerStatus?.approvalTime || registerStatus?.finishTime) }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="3">{{ registerStatus?.remark || '无' }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
      
      <el-empty 
        v-else 
        description="您尚未申请注册" 
      />
    </el-card>
 -->
    <!-- 历史注册记录 -->
    <el-card class="history-card" v-loading="historyLoading">
      <template #header>
        <div class="card-header">
          <span>历史注册记录</span>
        </div>
      </template>

      <el-table :data="historyList" border style="width: 100%">
        <el-table-column label="注册年度" width="100">
          <template #default="scope">
            {{ scope.row?.registerYear || scope.row?.year || scope.row?.batch_year || '--' }}
          </template>
        </el-table-column>
        <el-table-column label="批次名称" width="120">
          <template #default="scope">
            {{ scope.row?.batchCode || scope.row?.batchName || scope.row?.title || '--' }}
          </template>
        </el-table-column>
        <el-table-column label="注册状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row?.status || '')">{{ scope.row?.status || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row?.registerTime || scope.row?.applyTime || scope.row?.create_time) }}
          </template>
        </el-table-column>
        <el-table-column label="完成时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row?.approvalTime || scope.row?.finishTime || scope.row?.update_time) }}
          </template>
        </el-table-column>
        <el-table-column label="备注" min-width="200">
          <template #default="scope">
            {{ scope.row?.remark || scope.row?.remarks || scope.row?.comment || '无' }}
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          :page-sizes="[5, 10, 20, 50]"
          :total="page.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 团员自我评价表单 -->
    <el-dialog
      v-model="dialogVisible"
      title="团员年度自我评价"
      width="650px"
    >
      <el-form 
        ref="formRef"
        :model="form" 
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="姓名">
          <el-input v-model="form.memberName" disabled />
        </el-form-item>
        <el-form-item label="组织归属">
          <el-input v-model="form.organizationName" disabled />
        </el-form-item>
        <el-form-item label="政治表现" prop="politicalPerformance">
          <el-rate 
            v-model="form.politicalPerformance" 
            :texts="rateTexts"
            show-text
          />
        </el-form-item>
        <el-form-item label="学习表现" prop="studyPerformance">
          <el-rate 
            v-model="form.studyPerformance" 
            :texts="rateTexts"
            show-text
          />
        </el-form-item>
        <el-form-item label="工作表现" prop="workPerformance">
          <el-rate 
            v-model="form.workPerformance" 
            :texts="rateTexts"
            show-text
          />
        </el-form-item>
        <el-form-item label="参与活动情况" prop="activityParticipation">
          <el-select v-model="form.activityParticipation" placeholder="请选择参与活动情况" style="width: 100%">
            <el-option label="积极参与" value="积极参与" />
            <el-option label="一般参与" value="一般参与" />
            <el-option label="较少参与" value="较少参与" />
            <el-option label="未参与" value="未参与" />
          </el-select>
        </el-form-item>
        <el-form-item label="缴纳团费" prop="paidFees">
          <el-radio-group v-model="form.paidFees">
            <el-radio :label="true">已缴纳</el-radio>
            <el-radio :label="false">未缴纳</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年度自我评价" prop="selfEvaluation">
          <el-input 
            v-model="form.selfEvaluation" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入年度自我评价" 
          />
        </el-form-item>
        <el-form-item label="备注说明">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="2" 
            placeholder="其他需要说明的事项（选填）" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { useUserStore } from '../../stores/user';
import { getRegisterStatus, getRegisterHistory, applyRegister, getCurrentBatch } from '../../api/register';
import { formatDate, getStatusType, getRegisterStep, getFieldValue } from '../../utils/dataMapping';

// 简化的接口定义
interface FormData {
  memberId: number;
  memberName: string;
  organizationName: string;
  batchId: number;
  politicalPerformance: number;
  studyPerformance: number;
  workPerformance: number;
  activityParticipation: string;
  paidFees: boolean;
  selfEvaluation: string;
  remark: string;
}

// 使用类型标注
const router = useRouter();
const userStore = useUserStore();
const formRef = ref(null);

// 数据定义和类型标注
const batchLoading = ref(false);
const registerLoading = ref(false);
const historyLoading = ref(false);
const currentBatch = ref<any | null>(null);
const registerStatus = ref<any | null>(null);
const historyList = ref<any[]>([]);
const countdownText = ref('');
const dialogVisible = ref(false);
let countdownTimer: number | null = null;

const page = reactive({
  current: 1,
  size: 5,
  total: 0
});

// 评分文本
const rateTexts = ['较差', '一般', '良好', '优秀', '极佳'];

// 表单数据
const form = reactive<FormData>({
  memberId: 0,
  memberName: '',
  organizationName: '',
  batchId: 0,
  politicalPerformance: 3,
  studyPerformance: 3,
  workPerformance: 3,
  activityParticipation: '一般参与',
  paidFees: true,
  selfEvaluation: '',
  remark: ''
});

// 表单验证规则
const rules = {
  politicalPerformance: [
    { required: true, message: '请选择政治表现', trigger: 'change' }
  ],
  studyPerformance: [
    { required: true, message: '请选择学习表现', trigger: 'change' }
  ],
  workPerformance: [
    { required: true, message: '请选择工作表现', trigger: 'change' }
  ],
  activityParticipation: [
    { required: true, message: '请选择参与活动情况', trigger: 'change' }
  ],
  selfEvaluation: [
    { required: true, message: '请输入年度自我评价', trigger: 'blur' },
    { min: 10, max: 500, message: '长度在 10 到 500 个字符', trigger: 'blur' }
  ]
};

// 计算属性
const isAdmin = computed(() => {
  return userStore.isBranchSecretary || userStore.isCommitteeSecretary;
});

const canRegister = computed(() => {
  // 有进行中的批次，且未注册或注册被驳回时可以注册
  if (!currentBatch.value) {
    return false;
  }
  
  // 确认status字段正确处理
  const batchStatus = currentBatch.value.status || '';
  if (!batchStatus.includes('进行') && batchStatus !== '进行中') {
    return false;
  }
  
  if (!registerStatus.value) {
    return true; // 无注册记录可以注册
  }
  
  const regStatus = registerStatus.value.status || '';
  return regStatus === '已驳回' || regStatus === '未申请' || regStatus.includes('驳回');
});

// 添加防抖标志
let historyLoadingInProgress = false;

// 修改loadHistoryList函数，添加防抖逻辑
const loadHistoryList = async () => {
  // 如果已经在加载中，则不重复加载
  if (historyLoadingInProgress) {
    console.log("历史记录正在加载中，跳过重复请求");
    return;
  }
  
  historyLoadingInProgress = true;
  historyLoading.value = true;
  
  try {
    const response = await getRegisterHistory({
      page: page.current,
      size: page.size
    });
    console.log("历史记录原始响应:", response.data);
    
    if (response.data) {
      const data = response.data;
      
      // 适应不同的返回格式，并处理空数组情况
      let records = [];
      
      if (Array.isArray(data) && data.length > 0) {
        records = data;
        page.total = data.length;
      } else if (data && data.list && Array.isArray(data.list) && data.list.length > 0) {
        records = data.list;
        page.total = data.total || data.list.length;
      } else if (data && !Array.isArray(data)) {
        records = [data];
        page.total = 1;
      }
      
      // 通过id去重
      const uniqueMap = new Map();
      records.forEach((item: any) => {
        if (item && item.id) {
          uniqueMap.set(item.id, item);
        }
      });
      
      historyList.value = Array.from(uniqueMap.values());
      console.log("处理后的历史记录:", historyList.value);
    } else {
      historyList.value = [];
      page.total = 0;
    }
  } catch (error) {
    console.error('加载历史记录失败:', error);
    historyList.value = [];
    page.total = 0;
  } finally {
    historyLoading.value = false;
    // 设置一个短延迟，避免同一事件循环中的多次调用
    setTimeout(() => {
      historyLoadingInProgress = false;
    }, 300);
  }
};

const startCountdown = (): void => {
  if (!currentBatch.value) {
    countdownText.value = '无截止日期';
    return;
  }
  
  // 适应不同的字段名
  const endDate = currentBatch.value.endDate || currentBatch.value.endTime;
  if (!endDate) {
    countdownText.value = '无截止日期';
    return;
  }
  
  const endTime = new Date(endDate).getTime();
  
  const updateCountdown = () => {
    const now = new Date().getTime();
    const distance = endTime - now;
    
    if (distance < 0) {
      if (countdownTimer) {
        clearInterval(countdownTimer);
        countdownTimer = null;
      }
      countdownText.value = '已截止';
      return;
    }
    
    const days = Math.floor(distance / (1000 * 60 * 60 * 24));
    const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
    
    countdownText.value = `${days}天 ${hours}小时 ${minutes}分钟`;
  };
  
  updateCountdown();
  countdownTimer = window.setInterval(updateCountdown, 60000);
};

const navigateToBatchManagement = () => {
  router.push('/dashboard/register/batch');
};

const register = () => {
  // 打开注册对话框
  dialogVisible.value = true;
};

const submitForm = async () => {
  if (!formRef.value) return;
  
  // 表单验证
  await (formRef.value as any).validate(async (valid: boolean) => {
    if (!valid) {
      ElMessage.warning('请完善表单信息');
      return;
    }
    
    // 确认提交
    try {
      // 组装提交数据
      const registerData = {
        userId: userStore.id,
        userName: form.memberName,
        organizationId: userStore.organizationId || (typeof userStore.organization === 'object' ? userStore.organization.id : null),
        organizationName: form.organizationName,
        batchId: currentBatch.value?.id || 0,
        registerYear: currentBatch.value?.registerYear || currentBatch.value?.year || '',
        politicalPerformance: form.politicalPerformance,
        studyPerformance: form.studyPerformance,
        workPerformance: form.workPerformance,
        activityParticipation: form.activityParticipation,
        paidFees: form.paidFees,
        selfEvaluation: form.selfEvaluation,
        remark: form.remark,
        memberId: userStore.id,
        status: '待审核' // 确保状态字段正确
      };
      
      console.log("提交注册数据:", registerData);
      
      const response = await applyRegister(registerData);
      console.log("注册响应:", response.data);
      
      // 检查各种可能的成功响应格式
      const isSuccess = 
        (response.data && response.data.code === 200) || 
        (response.data && response.data.status === 'success') || 
        (response.data && response.data.success === true) ||
        (response.data && response.data.code === 0);
      
      if (isSuccess) {
        ElMessage.success('注册申请提交成功');
        dialogVisible.value = false;
        
        // 重新加载注册状态
        await loadRegisterStatus();
        await loadHistoryList(); // 同时刷新历史记录
      } else {
        // 提取可能的错误信息
        const errorMsg = 
          response.data?.message || 
          response.data?.msg || 
          response.data?.error || 
          '注册申请提交失败';
        
        ElMessage.error(errorMsg);
      }
    } catch (error) {
      console.error('注册申请提交失败:', error);
      ElMessage.error('注册申请提交失败，请稍后重试');
    }
  });
};

const handleSizeChange = (val: number) => {
  page.size = val;
  loadHistoryList();
};

const handleCurrentChange = (val: number) => {
  page.current = val;
  loadHistoryList();
};

// 方法
const loadCurrentBatch = async () => {
  batchLoading.value = true;
  try {
    const response = await getCurrentBatch();
    
    if (response.data) {
      // 直接赋值，不做复杂处理
      currentBatch.value = response.data;
      console.log("批次数据:", currentBatch.value);
      
      // 检查批次数据是否有效
      if (currentBatch.value && typeof currentBatch.value === 'object') {
        // 确认status字段正确处理
        const status = currentBatch.value.status || '';
        if (status.includes('进行') || status === '进行中') {
          startCountdown();
        }
      } else {
        console.warn("批次数据无效或为空");
      }
    }
  } catch (error) {
    console.error('加载批次失败:', error);
  } finally {
    batchLoading.value = false;
  }
};

const loadRegisterStatus = async () => {
  registerLoading.value = true;
  try {
    const response = await getRegisterStatus();
    console.log("注册状态原始响应:", response.data);
    
    if (response.data) {
      const responseData = response.data || {};
      
      // 直接赋值原始数据，但确保处理正确
      if (responseData.register && !Array.isArray(responseData.register)) {
        // 如果register是对象，直接使用
        registerStatus.value = responseData.register;
      } else if (Array.isArray(responseData.register) && responseData.register.length > 0) {
        // 如果register是数组且不为空，使用第一项
        registerStatus.value = responseData.register[0];
      } else {
        // 否则设为null
        registerStatus.value = null;
      }
      
      console.log("处理后的注册状态:", registerStatus.value);
      
      // 尝试从responseData获取批次信息(如果currentBatch为空)
      if (!currentBatch.value && responseData.batch) {
        currentBatch.value = responseData.batch;
        console.log("从状态获取的批次:", currentBatch.value);
        
        // 确认status字段正确处理
        const status = currentBatch.value.status || '';
        if (status.includes('进行') || status === '进行中') {
          startCountdown();
        }
      }
      
      // 初始化表单数据
      if (registerStatus.value) {
        // 如果有注册记录，填充表单
        form.politicalPerformance = registerStatus.value.politicalPerformance || 3;
        form.studyPerformance = registerStatus.value.studyPerformance || 3;
        form.workPerformance = registerStatus.value.workPerformance || 3;
        form.activityParticipation = registerStatus.value.activityParticipation || '一般参与';
        form.paidFees = registerStatus.value.paidFees === false ? false : true;
        form.selfEvaluation = registerStatus.value.selfEvaluation || '';
        form.remark = registerStatus.value.remark || '';
      } else if (currentBatch.value) {
        // 没有注册记录，初始化表单
        form.batchId = currentBatch.value.id || 0;
        form.memberName = userStore.name;
        form.organizationName = userStore.organization_name || userStore.organization || '';
      }
    }
  } catch (error) {
    console.error('加载注册状态失败:', error);
  } finally {
    registerLoading.value = false;
  }
};

// 生命周期钩子
onMounted(async () => {
  console.log("Register组件已挂载，开始加载数据");
  try {
    // 先加载批次信息
    await loadCurrentBatch();
    
    // 然后加载注册状态
    await loadRegisterStatus();
    
    // 最后加载历史记录
    await loadHistoryList();
    
    console.log("所有数据加载完成");
  } catch (error) {
    console.error("数据加载过程中出错:", error);
  }
});

onBeforeUnmount(() => {
  // 清除倒计时定时器
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});
</script>

<style scoped>
.register-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.batch-card,
.status-card,
.history-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.batch-info {
  margin-top: 20px;
  margin-bottom: 20px;
}

.info-item {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  height: 100%;
}

.info-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.info-value {
  font-size: 16px;
  font-weight: bold;
}

.countdown {
  color: #f56c6c;
}

.batch-description {
  margin-top: 20px;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.description-title {
  font-weight: bold;
  margin-bottom: 10px;
}

.register-info {
  margin-top: 30px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style> 