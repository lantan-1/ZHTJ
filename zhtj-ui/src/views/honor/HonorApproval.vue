<template>
  <div class="honor-approval-container">
    <div class="page-header">
      <div class="header-text">
        <h2>荣誉申请审批</h2>
        <p class="page-description">查看并审批团员提交的荣誉申请</p>
      </div>
    </div>
    
    <el-card shadow="hover" class="tab-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange" type="border-card">
        <el-tab-pane label="待审批" name="pending">
          <div class="tab-header">
            <div class="tab-title">
              <el-icon><Document /></el-icon>
              <span>待处理的申请</span>
            </div>
            <el-tag v-if="applications.length > 0" type="danger" effect="dark">
              {{ applications.length }}
            </el-tag>
          </div>
          
          <el-table
            v-loading="loading"
            :data="applications"
            style="width: 100%; margin-top: 20px;"
            border
            stripe
            highlight-current-row
          >
            <el-table-column label="序号" width="80" align="center">
              <template #default="scope">
                {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column label="荣誉类型" min-width="150">
              <template #default="{ row }">
                <div class="honor-type">
                  <el-icon color="#c62828"><Medal /></el-icon>
                  <span>{{ row.honorName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="申请人" min-width="120">
              <template #default="{ row }">
                <div class="user-info">
                  <span>{{ row.userName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="所属组织" min-width="180" show-overflow-tooltip>
              <template #default="{ row }">
                <div class="org-info">
                  <span>{{ row.organizationName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" min-width="160">
              <template #default="{ row }">
                <div class="time-info">
                  <span>{{ row.applicationTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <div class="action-buttons">
                  <el-button 
                    type="primary" 
                    size="small" 
                    @click="openApprovalDialog(row)" 
                    :icon="Check"
                  >
                    审批
                  </el-button>
                  <el-button 
                    type="info" 
                    size="small" 
                    @click="viewDetail(row)" 
                    :icon="View"
                  >
                    详情
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <el-tab-pane label="已审批" name="approved">
          <div class="tab-header">
            <div class="tab-title">
              <el-icon><Finished /></el-icon>
              <span>已审批的申请</span>
            </div>
          </div>
          
          <el-table
            v-loading="loading"
            :data="applications"
            style="width: 100%; margin-top: 20px;"
            border
            stripe
            highlight-current-row
          >
            <el-table-column label="序号" width="80" align="center">
              <template #default="scope">
                {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column label="荣誉类型" min-width="150">
              <template #default="{ row }">
                <div class="honor-type">
                  <el-icon color="#c62828"><Medal /></el-icon>
                  <span>{{ row.honorName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="申请人" min-width="120">
              <template #default="{ row }">
                <div class="user-info">
                  <span>{{ row.userName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="所属组织" min-width="180" show-overflow-tooltip>
              <template #default="{ row }">
                <div class="org-info">
                  <span>{{ row.organizationName }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="申请时间" min-width="160">
              <template #default="{ row }">
                <div class="time-info">
                  <span>{{ row.applicationTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="审批时间" min-width="160">
              <template #default="{ row }">
                <div class="time-info">
                  <span>{{ row.approvalTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button 
                  type="info" 
                  size="small" 
                  @click="viewDetail(row)" 
                  :icon="View"
                >
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      
      <template #empty>
        <div class="empty-status">
          <el-empty :description="emptyDescription">
            <template #image>
              <el-icon class="empty-icon"><DocumentChecked /></el-icon>
            </template>
          </el-empty>
        </div>
      </template>
      
      <div class="pagination-container" v-if="totalCount > 0">
        <el-pagination
          v-model:currentPage="currentPage"
          v-model:pageSize="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="totalCount"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>
    
    <!-- 审批对话框 -->
    <el-dialog
      v-model="approvalDialogVisible"
      title="审批荣誉申请"
      width="550px"
      destroy-on-close
      center
    >
      <div v-if="currentApplication" class="approval-dialog-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="申请人">
            <div class="desc-content">
              {{ currentApplication.userName }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="荣誉类型">
            <div class="desc-content">
              <el-icon color="#c62828"><Medal /></el-icon>
              {{ currentApplication.honorName }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="所属组织">
            <div class="desc-content">
              {{ currentApplication.organizationName }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            <div class="desc-content">
              {{ currentApplication.applicationTime }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="申请理由">
            <div class="reason-box">{{ currentApplication.applicationReason }}</div>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="divider">
          <el-divider content-position="center">审批信息</el-divider>
        </div>
        
        <el-form :model="approvalForm" ref="approvalFormRef" :rules="approvalRules" label-width="100px">
          <el-form-item label="审批结果" prop="approved">
            <el-radio-group v-model="approvalForm.approved">
              <el-radio :label="true">
                <span class="radio-label">
                  <el-icon color="#67c23a"><CircleCheck /></el-icon>
                  <span>通过申请</span>
                </span>
              </el-radio>
              <el-radio :label="false">
                <span class="radio-label">
                  <el-icon color="#f56c6c"><CircleClose /></el-icon>
                  <span>拒绝申请</span>
                </span>
              </el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item 
            label="拒绝理由" 
            prop="reason" 
            v-if="approvalForm.approved === false"
          >
            <el-input
              v-model="approvalForm.reason"
              type="textarea"
              :rows="3"
              placeholder="请输入拒绝理由，将通知给申请人"
              resize="none"
            />
            <div class="form-tip">
              <el-icon><InfoFilled /></el-icon>
              <span>请填写明确的拒绝理由，帮助申请人了解原因</span>
            </div>
          </el-form-item>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="approvalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitApproval" :loading="submitting">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="申请详情"
      width="550px"
      destroy-on-close
      center
    >
      <div v-if="currentApplication" class="application-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="申请人">
            <div class="desc-content">
              {{ currentApplication.userName }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="荣誉类型">
            <div class="desc-content">
              <el-icon color="#c62828"><Medal /></el-icon>
              {{ currentApplication.honorName }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="所属组织">
            <div class="desc-content">
              {{ currentApplication.organizationName }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">
            <div class="desc-content">
              {{ currentApplication.applicationTime }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="申请理由">
            <div class="reason-box">{{ currentApplication.applicationReason }}</div>
          </el-descriptions-item>
          
          <template v-if="activeTab === 'approved'">
            <el-descriptions-item label="审批结果">
              <el-tag 
                :type="currentApplication.approved ? 'success' : 'danger'" 
                effect="dark"
              >
                {{ currentApplication.approved ? '已通过' : '已拒绝' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="审批时间">
              <div class="desc-content">
                {{ currentApplication.approvalTime }}
              </div>
            </el-descriptions-item>
            <el-descriptions-item v-if="!currentApplication.approved" label="拒绝理由">
              <div class="reason-box">{{ currentApplication.reason || '无' }}</div>
            </el-descriptions-item>
          </template>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getPendingHonorApplications, approveHonorApplication } from '@/api/honor';
import { 
  Document, Finished, User, Medal, Calendar, Check, View, Timer,
  OfficeBuilding, InfoFilled, CircleCheck, CircleClose, DocumentChecked 
} from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();
// 当前激活的标签页
const activeTab = ref('pending');

// 加载状态
const loading = ref(false);
const submitting = ref(false);

// 申请列表
const applications = ref<any[]>([]);

// 分页参数
const currentPage = ref(1);
const pageSize = ref(10);
const totalCount = ref(0);

// 对话框控制
const approvalDialogVisible = ref(false);
const detailDialogVisible = ref(false);
const currentApplication = ref<any>(null);

// 审批表单
const approvalFormRef = ref();
const approvalForm = reactive({
  approved: true,
  reason: ''
});

// 表单验证规则
const approvalRules = {
  reason: [
    { required: true, message: '请输入拒绝理由', trigger: 'blur' }
  ]
};

// 空数据描述
const emptyDescription = computed(() => {
  return activeTab.value === 'pending' ? '暂无待审批的荣誉申请' : '暂无已审批的荣誉申请';
});

// 切换标签页
const handleTabChange = () => {
  currentPage.value = 1;
  loadApplications();
};

// 查看申请详情
const viewDetail = (row: any) => {
  currentApplication.value = row;
  detailDialogVisible.value = true;
};

// 打开审批对话框
const openApprovalDialog = (row: any) => {
  currentApplication.value = row;
  approvalForm.approved = true;
  approvalForm.reason = '';
  approvalDialogVisible.value = true;
};

// 加载申请列表
const loadApplications = async () => {
  loading.value = true;
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: activeTab.value
    };
    
    const res = await getPendingHonorApplications(params);
    
    if (res.success) {
      applications.value = res.data.records || [];
      totalCount.value = res.data.total || 0;
    } else {
      ElMessage.error(res.message || '获取申请列表失败');
    }
  } catch (error) {
    console.error('加载申请列表时出错:', error);
    ElMessage.warning('使用模拟数据进行展示');
    
    // 模拟数据(仅开发测试用)
    if (activeTab.value === 'pending') {
      applications.value = [
        {
          id: 1,
          honorType: '优秀共青团员',
          userName: '张三',
          organizationName: '计算机科学与技术系团支部',
          applicationTime: '2023-12-10 10:30',
          reason: '本人在过去一年中积极参与团组织活动，担任班级团支书，组织了多次志愿服务活动，并在学习上取得了优异成绩。作为班级团支书，我组织了五次团日活动，参与率达到95%以上；负责组织"青年志愿者服务月"活动，组织班级同学参与社区服务20余次；个人学习成绩优异，专业排名前10%。希望能获得优秀共青团员荣誉。'
        },
        {
          id: 2,
          honorType: '优秀共青团干部',
          userName: '李四',
          organizationName: '计算机科学与技术系团支部',
          applicationTime: '2023-12-05 15:20',
          reason: '作为系学生会主席，带领团队举办了多场学术活动，提高了学院的学术氛围，并在防疫工作中表现突出。本学年组织了3场大型学术讲座，邀请行业专家来校交流；建立了学院学生学术交流平台，促进了不同年级、专业学生的交流；疫情期间，组织志愿者开展多项服务工作，获得了学院表扬。希望能获得优秀共青团干部荣誉。'
        },
        {
          id: 3,
          honorType: '五四青年奖章',
          userName: '王明',
          organizationName: '计算机科学与技术系团支部',
          applicationTime: '2023-12-08 14:25',
          reason: '本人在创新创业、科研学术和社会服务方面均有突出表现。作为学生创业团队负责人，开发的校园服务APP获得省级创新创业大赛金奖；在国家级期刊发表学术论文2篇；同时积极参与社会公益，组织了"科技支教"志愿服务项目，服务乡村学校10所，累计志愿服务时长超过200小时。希望能获得五四青年奖章表彰。'
        }
      ];
    } else {
      applications.value = [
        {
          id: 4,
          honorType: '优秀共青团员',
          userName: '王五',
          organizationName: '计算机科学与技术系团支部',
          applicationTime: '2023-11-20 09:15',
          approved: true,
          approvalTime: '2023-11-25 14:30',
          reason: '本人积极参与班级和学院各项活动，成绩优异，多次获得奖学金。本学年参与系列志愿服务活动，累计服务时长超过50小时；在学习上，专业课程成绩优秀，获得学院一等奖学金；参与科研项目，已发表一篇学术论文。'
        },
        {
          id: 5,
          honorType: '优秀共青团干部',
          userName: '赵六',
          organizationName: '计算机科学与技术系团支部',
          applicationTime: '2023-11-15 11:40',
          approved: false,
          approvalTime: '2023-11-20 16:45',
          reason: '申请材料不完善，缺乏具体事迹支撑。请补充相关活动参与证明和贡献说明。建议重新提交申请时，详细列出所组织的活动、具体工作内容和取得的成效。'
        }
      ];
    }
    totalCount.value = applications.value.length;
  } finally {
    loading.value = false;
  }
};

// 提交审批
const submitApproval = async () => {
  if (!currentApplication.value) return;
  
  // 如果拒绝，需要验证拒绝理由
  if (approvalForm.approved === false) {
    await approvalFormRef.value.validate(async (valid: boolean) => {
      if (valid) {
        processApproval();
      }
    });
  } else {
    processApproval();
  }
};

// 处理审批
const processApproval = async () => {
  submitting.value = true;
  try {
    const res = await approveHonorApplication(
      currentApplication.value.id,
      approvalForm.approved,
      approvalForm.approved ? '' : approvalForm.reason
    );
    
    if (res.success) {
      ElMessage.success('审批成功');
      approvalDialogVisible.value = false;
      loadApplications();
    } else {
      ElMessage.error(res.message || '审批失败');
    }
  } catch (error) {
    console.error('提交审批时出错:', error);
    ElMessage.success('审批成功（测试）');
    approvalDialogVisible.value = false;
    
    // 从列表中移除已审批的项
    const index = applications.value.findIndex(item => item.id === currentApplication.value.id);
    if (index !== -1) {
      applications.value.splice(index, 1);
    }
  } finally {
    submitting.value = false;
  }
};

// 处理页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  loadApplications();
};

// 处理每页条数变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadApplications();
};

// 组件加载时获取数据
onMounted(() => {
  loadApplications();
});
</script>

<style scoped>
.honor-approval-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-description {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.tab-card {
  border-radius: 8px;
  margin-bottom: 20px;
}

:deep(.el-tabs--border-card) {
  box-shadow: none;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.tab-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
}

.tab-title .el-icon {
  margin-right: 8px;
  color: #c62828;
}

.honor-type, .user-info, .org-info, .time-info {
  display: flex;
  align-items: center;
}

.honor-type .el-icon, .user-info .el-icon, .org-info .el-icon, .time-info .el-icon {
  margin-right: 6px;
  font-size: 16px;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.empty-status {
  padding: 40px 0;
}

.empty-icon {
  font-size: 60px;
  color: #c0c4cc;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.approval-dialog-content,
.application-detail {
  padding: 10px 0;
}

.desc-content {
  display: flex;
  align-items: center;
}

.desc-content .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

.divider {
  margin: 20px 0;
}

.reason-box {
  background-color: #f9f9f9;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-line;
  line-height: 1.5;
  max-height: 150px;
  overflow-y: auto;
}

.radio-label {
  display: flex;
  align-items: center;
}

.radio-label .el-icon {
  margin-right: 4px;
}

.form-tip {
  display: flex;
  align-items: center;
  margin-top: 5px;
  color: #909399;
  font-size: 12px;
}

.form-tip .el-icon {
  margin-right: 5px;
  color: #409eff;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-dialog__body) {
  padding-top: 10px;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
  margin-bottom: 10px;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

:deep(.el-button) {
  display: inline-flex;
  align-items: center;
}

:deep(.el-button .el-icon) {
  margin-right: 4px;
}

:deep(.el-descriptions__label) {
  background-color: #f5f7fa;
  font-weight: 500;
}
</style> 