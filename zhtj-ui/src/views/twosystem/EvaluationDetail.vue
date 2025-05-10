<template>
  <div class="evaluation-detail-container">
    <div class="page-header">
      <el-page-header @back="goBack" :title="evaluation.title || '评议详情'">
        <template #content>
          <div class="flex items-center">
            <el-tag 
              :type="getStatusType(evaluation.status)" 
              effect="dark"
              class="ml-2"
            >{{ evaluation.status }}</el-tag>
          </div>
        </template>
      </el-page-header>
    </div>

    <div v-loading="loading">
      <!-- 评议活动信息卡片 -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <span>评议活动信息</span>
            <el-button 
              v-if="evaluation.status === '草稿' && canEdit"
              type="primary" 
              plain
              size="small"
              @click="editEvaluation"
            >编辑</el-button>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="评议标题">{{ evaluation.title }}</el-descriptions-item>
          <el-descriptions-item label="评议年度">{{ evaluation.evaluationYear }}</el-descriptions-item>
          <el-descriptions-item label="组织名称">{{ evaluation.organizationName }}</el-descriptions-item>
          <el-descriptions-item label="评议时间">
            {{ formatDateTime(evaluation.startTime) }} 至 {{ formatDateTime(evaluation.endTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="创建人">{{ evaluation.initiatorName }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(evaluation.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="评议说明" :span="2">
            {{ evaluation.description || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 进度统计 -->
      <el-card class="statistics-card">
        <template #header>
          <div class="card-header">
            <span>评议进度</span>
            <el-button
              v-if="evaluation.status === '进行中'"
              type="success"
              plain
              size="small"
              @click="sendReminderHandler"
            >发送提醒</el-button>
          </div>
        </template>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="progress-item">
              <h4>总体进度</h4>
              <el-progress
                type="dashboard"
                :percentage="statistics.overallProgress || 0"
                :color="customColors"
              />
              <div class="progress-text">已完成 {{ statistics.completedCount || 0 }}/{{ statistics.totalCount || 0 }}</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="progress-item">
              <h4>优秀率</h4>
              <el-progress
                type="dashboard"
                :percentage="statistics.excellentRate || 0"
                :color="customColors"
              />
              <div class="progress-text">{{ statistics.excellentCount || 0 }}人</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="progress-item">
              <h4>合格率</h4>
              <el-progress
                type="dashboard"
                :percentage="computedQualifiedRate"
                :color="customColors"
              />
              <div class="progress-text">{{ computedQualifiedCount }}人(含优秀)</div>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 评议结果列表 -->
      <el-card class="result-card">
        <template #header>
          <div class="card-header">
            <span>评议结果列表</span>
            <div>
              <el-button 
                type="primary"
                plain
                size="small"
                :disabled="!canBatchEvaluate"
                @click="batchEvaluate"
              >批量评议</el-button>
              <el-button 
                type="success"
                plain
                size="small"
                @click="exportResultsHandler"
              >导出结果</el-button>
            </div>
          </div>
        </template>

        <!-- 筛选区域 -->
        <div class="filter-section">
          <el-form :inline="true" :model="filterForm" class="filter-form">
            <el-form-item label="评议状态">
              <el-select v-model="filterForm.resultStatus" placeholder="评议状态" clearable style="width: 120px;">
                <el-option label="未评议" value="未评议" />
                <el-option label="已评议" value="已评议" />
              </el-select>
            </el-form-item>
            <el-form-item label="姓名">
              <el-input 
                v-model="filterForm.memberName" 
                placeholder="团员姓名" 
                clearable
                style="width: 120px;"
                @keyup.enter="handleSearch"
              />
            </el-form-item>
            <el-form-item label="评议结果">
              <el-select v-model="filterForm.result" placeholder="评议结果" clearable style="width: 120px;">
                <el-option label="优秀" value="优秀" />
                <el-option label="合格" value="合格" />
                <el-option label="基本合格" value="基本合格" />
                <el-option label="不合格" value="不合格" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="resetFilter">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 结果表格 -->
        <el-table
          v-loading="resultLoading"
          :data="resultList"
          border
          style="width: 100%"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" v-if="evaluation.status === '进行中'" />
          <el-table-column prop="memberName" label="团员姓名" width="120">
            <template #default="scope">
              {{ scope.row.memberName }}
            </template>
          </el-table-column>
          <el-table-column prop="position" label="职务" width="150">
            <template #default="scope">
              {{ scope.row.position }}
            </template>
          </el-table-column>
          <el-table-column prop="organizationName" label="所属团支部" min-width="150">
            <template #default="scope">
              {{ scope.row.organizationName }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="评议状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已评议' ? 'success' : 'info'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="result" label="评议结果" width="100">
            <template #default="scope">
              <span v-if="scope.row.status === '已评议'">{{ scope.row.result }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="evaluationTime" label="评议时间" width="180">
            <template #default="scope">
              <span v-if="scope.row.status === '已评议'">
                {{ formatDateTime(scope.row.evaluationTime) }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="comment" label="评议意见" min-width="200">
            <template #default="scope">
              <span v-if="scope.row.status === '已评议'">
                {{ scope.row.comment || '无' }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button 
                v-if="evaluation.status === '进行中' && (scope.row.status !== '已评议' || canEdit)"
                link 
                type="primary" 
                @click="evaluateMember(scope.row)"
              >评议</el-button>
              <el-button 
                v-if="scope.row.status === '已评议' && canEdit"
                link 
                type="danger" 
                @click="resetMemberEvaluationHandler(scope.row)"
              >重置</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="resultPage.current"
            v-model:page-size="resultPage.size"
            :page-sizes="[10, 20, 50, 100]"
            :total="resultPage.total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleResultSizeChange"
            @current-change="handleResultCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 评议对话框 -->
    <el-dialog
      v-model="evaluationDialogVisible"
      title="团员评议"
      width="600px"
    >
      <el-form :model="evaluationForm" label-width="100px">
        <el-form-item label="团员姓名">
          <el-input v-model="evaluationForm.memberName" disabled />
        </el-form-item>
        <el-form-item label="所属团支部">
          <el-input v-model="evaluationForm.branchName" disabled />
        </el-form-item>
        <el-form-item label="评议结果" required>
          <el-radio-group v-model="evaluationForm.result">
            <el-radio label="优秀">优秀</el-radio>
            <el-radio label="合格">合格</el-radio>
            <el-radio label="基本合格">基本合格</el-radio>
            <el-radio label="不合格">不合格</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="评议意见">
          <el-input v-model="evaluationForm.comment" type="textarea" rows="4" placeholder="请输入评议意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="evaluationDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEvaluationHandler">提交</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 批量评议对话框 -->
    <el-dialog
      v-model="batchDialogVisible"
      title="批量评议"
      width="600px"
    >
      <div class="batch-info">已选择 {{ selectedMembers.length }} 名团员</div>
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="评议结果" required>
          <el-radio-group v-model="batchForm.result">
            <el-radio label="优秀">优秀</el-radio>
            <el-radio label="合格">合格</el-radio>
            <el-radio label="基本合格">基本合格</el-radio>
            <el-radio label="不合格">不合格</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="评议意见">
          <el-input v-model="batchForm.comment" type="textarea" rows="4" placeholder="请输入评议意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="batchDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBatchEvaluationHandler">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../../stores/user';
import { 
  getEvaluationDetail, 
  getEvaluationResults, 
  getEvaluationResultStatistics,
  submitEvaluation,
  resetEvaluation,
  sendEvaluationReminder,
  exportEvaluationResults
} from '../../api/evaluation';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 数据定义
const loading = ref(false);
const resultLoading = ref(false);
const evaluation = ref({});
const statistics = reactive({
  totalCount: 0,
  completedCount: 0,
  overallProgress: 0,
  excellentCount: 0,
  excellentRate: 0,
  qualifiedCount: 0,
  qualifiedRate: 0
});

const resultList = ref([]);
const resultPage = reactive({
  current: 1,
  size: 10,
  total: 0
});

const filterForm = reactive({
  resultStatus: '',
  memberName: '',
  result: ''
});

// 评议表单
const evaluationDialogVisible = ref(false);
const evaluationForm = reactive({
  id: '',
  memberId: '',
  memberName: '',
  branchName: '',
  result: '合格',
  comment: ''
});

// 批量评议
const selectedMembers = ref([]);
const batchDialogVisible = ref(false);
const batchForm = reactive({
  result: '合格',
  comment: ''
});

// 自定义颜色
const customColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 }
];

// 计算属性
const canEdit = computed(() => {
  // 组织管理员或创建者可以编辑
  return userStore.isBranchSecretary || userStore.isCommitteeSecretary;
});

const canBatchEvaluate = computed(() => {
  return evaluation.value.status === '进行中' && selectedMembers.value.length > 0;
});

// 计算正确的合格率（包含优秀人数）
const computedQualifiedCount = computed(() => {
  // 合格人数 + 优秀人数
  return (statistics.qualifiedCount || 0) + (statistics.excellentCount || 0);
});

// 计算正确的合格率百分比
const computedQualifiedRate = computed(() => {
  const totalCount = statistics.totalCount || 0;
  if (totalCount === 0) return 0;
  
  // (合格人数 + 优秀人数) / 总人数 * 100
  return Math.round(((statistics.qualifiedCount || 0) + (statistics.excellentCount || 0)) / totalCount * 100);
});

// 方法
const loadEvaluation = async () => {
  loading.value = true;
  try {
    const evaluationId = route.params.id;
    const response = await getEvaluationDetail(evaluationId);
    if (response.success) {
      evaluation.value = response.data || {};
    } else {
      ElMessage.error(response.message || '获取评议活动详情失败');
    }
  } catch (error) {
    console.error('加载评议活动详情失败:', error);
    ElMessage.error('加载评议活动详情失败');
  } finally {
    loading.value = false;
  }
};

const loadStatistics = async () => {
  try {
    const evaluationId = route.params.id;
    const response = await getEvaluationResultStatistics(evaluationId);
    if (response.success) {
      Object.assign(statistics, response.data);
    }
  } catch (error) {
    console.error('加载评议统计数据失败:', error);
  }
};

const loadResults = async () => {
  resultLoading.value = true;
  try {
    const evaluationId = route.params.id;
    const params = {
      page: resultPage.current,
      size: resultPage.size,
      status: filterForm.resultStatus || undefined,
      memberName: filterForm.memberName || undefined,
      result: filterForm.result || undefined
    };

    const response = await getEvaluationResults(evaluationId, params);
    console.log('评议结果列表响应:', response);
    
    if (response && response.success) {
      // 现在API层已经标准化了返回格式，所以这里直接使用标准格式
      if (response.data) {
      resultList.value = response.data.list || [];
      resultPage.total = response.data.total || 0;
        console.log(`成功加载评议结果, ${resultList.value.length}条记录, 总计${resultPage.total}条`);
        
        // 检查返回的数据中是否有需要的字段，如果没有进行适配
        if (resultList.value.length > 0) {
          resultList.value = resultList.value.map(item => {
            return {
              ...item,
              // 确保关键字段的存在，使用别名兼容不同的API返回格式
              memberName: item.memberName || item.user_name || item.userName || '',
              organizationName: item.organizationName || item.organization_name || item.branchName || '',
              position: item.position || item.memberPosition || '团员',
              status: item.status || (item.result ? '已评议' : '未评议'),
              evaluationTime: item.evaluationTime || item.evaluation_time || item.updateTime || '',
              comment: item.comment || '' // 使用数据库中正确的字段名
            };
          });
        }
      } else {
        console.warn('API返回成功但没有data字段:', response);
        resultList.value = [];
        resultPage.total = 0;
      }
    } else {
      console.error('API请求成功但返回非成功状态:', response);
      ElMessage.error(response?.message || '获取评议结果列表失败');
      resultList.value = [];
      resultPage.total = 0;
    }
  } catch (error) {
    console.error('加载评议结果列表失败:', error);
    ElMessage.error('加载评议结果列表失败: ' + (error.message || '未知错误'));
    resultList.value = [];
    resultPage.total = 0;
  } finally {
    resultLoading.value = false;
  }
};

const handleSearch = () => {
  console.log('执行搜索，姓名:', filterForm.memberName);
  
  // 确保姓名不为空白字符
  if (filterForm.memberName) {
    filterForm.memberName = filterForm.memberName.trim();
  }
  
  resultPage.current = 1;
  loadResults();
};

const resetFilter = () => {
  filterForm.resultStatus = '';
  filterForm.memberName = '';
  filterForm.result = '';
  handleSearch();
};

const handleResultSizeChange = (val) => {
  resultPage.size = val;
  loadResults();
};

const handleResultCurrentChange = (val) => {
  resultPage.current = val;
  loadResults();
};

const formatDateTime = (dateStr) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleString();
};

const getStatusType = (status) => {
  switch (status) {
    case '草稿': return 'info';
    case '进行中': return 'warning';
    case '已完成': return 'success';
    case '已取消': return 'danger';
    default: return 'info';
  }
};

const goBack = () => {
  router.push('/dashboard/evaluations');
};

const editEvaluation = () => {
  // 跳转到编辑页面
  router.push(`/dashboard/evaluations/edit/${route.params.id}`);
};

const evaluateMember = (member) => {
  // 初始化评议表单
  Object.assign(evaluationForm, {
    id: member.id,
    memberId: member.memberId,
    memberName: member.memberName,
    branchName: member.organizationName,
    result: member.result || '合格',
    comment: member.comment || ''
  });
  evaluationDialogVisible.value = true;
};

const submitEvaluationHandler = async () => {
  try {
    const evaluationId = route.params.id;
    const data = {
      id: evaluationForm.id,
      memberId: evaluationForm.memberId,
      result: evaluationForm.result,
      comment: evaluationForm.comment
    };

    const response = await submitEvaluation(evaluationId, data);
    if (response.success) {
      ElMessage.success('评议提交成功');
      evaluationDialogVisible.value = false;
      loadResults();
      loadStatistics();
    } else {
      ElMessage.error(response.message || '评议提交失败');
    }
  } catch (error) {
    console.error('评议提交失败:', error);
    ElMessage.error('评议提交失败');
  }
};

const resetMemberEvaluationHandler = async (member) => {
  try {
    ElMessageBox.confirm(`确定要重置${member.memberName}的评议结果吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      const evaluationId = route.params.id;
      const response = await resetEvaluation(evaluationId, {
        memberId: member.memberId
      });
      
      if (response.success) {
        ElMessage.success('评议重置成功');
        loadResults();
        loadStatistics();
      } else {
        ElMessage.error(response.message || '评议重置失败');
      }
    }).catch(() => {});
  } catch (error) {
    console.error('评议重置失败:', error);
    ElMessage.error('评议重置失败');
  }
};

const handleSelectionChange = (selection) => {
  selectedMembers.value = selection;
};

const batchEvaluate = () => {
  if (selectedMembers.value.length === 0) {
    ElMessage.warning('请先选择团员');
    return;
  }
  batchDialogVisible.value = true;
};

const submitBatchEvaluationHandler = async () => {
  try {
    const evaluationId = route.params.id;
    
    // 收集所有选中记录的ID和团员ID
    const recordIds = selectedMembers.value
      .filter(member => member.id) // 过滤有评议记录ID的项
      .map(member => member.id);
    
    const memberIds = selectedMembers.value
      .filter(member => !member.id && (member.userId || member.memberId)) // 过滤没有评议记录ID但有团员ID的项
      .map(member => member.userId || member.memberId);
    
    // 构建请求数据
    const data = {
      recordIds,      // 评议记录ID数组，用于更新已有记录
      memberIds,      // 团员ID数组，用于创建新记录
      result: batchForm.result,
      comment: batchForm.comment
    };
    
    console.log('批量评议数据:', data);
    
    const response = await submitEvaluation(evaluationId, data);
    if (response.success) {
      ElMessage.success('批量评议提交成功');
      batchDialogVisible.value = false;
      loadResults();
      loadStatistics();
    } else {
      ElMessage.error(response.message || '批量评议提交失败');
    }
  } catch (error) {
    console.error('批量评议提交失败:', error);
    ElMessage.error('批量评议提交失败');
  }
};

const sendReminderHandler = async () => {
  try {
    const evaluationId = route.params.id;
    const response = await sendEvaluationReminder(evaluationId);
    if (response.success) {
      ElMessage.success('评议提醒发送成功');
    } else {
      ElMessage.error(response.message || '评议提醒发送失败');
    }
  } catch (error) {
    console.error('评议提醒发送失败:', error);
    ElMessage.error('评议提醒发送失败');
  }
};

const exportResultsHandler = () => {
  const evaluationId = route.params.id;
  const url = exportEvaluationResults(evaluationId);
  window.open(url, '_blank');
};

// 生命周期钩子
onMounted(() => {
  loadEvaluation();
  loadResults();
  loadStatistics();
});
</script>

<style scoped>
.evaluation-detail-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.info-card {
  margin-bottom: 20px;
}

.statistics-card {
  margin-bottom: 20px;
}

.result-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 0;
}

.progress-item h4 {
  margin-bottom: 15px;
  color: #606266;
}

.progress-text {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

.filter-section {
  margin-bottom: 20px;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.batch-info {
  margin-bottom: 15px;
  font-size: 14px;
  color: #606266;
  text-align: center;
}
</style> 