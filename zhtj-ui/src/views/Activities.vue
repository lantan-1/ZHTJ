<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useActivityStore } from '../stores/activity';
import { useUserStore } from '../stores/user';
import type { Activity } from '../api/activity';
import { Search, Plus, Edit, Delete, Download } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import * as XLSX from 'xlsx';

const activityStore = useActivityStore();
const userStore = useUserStore();
const router = useRouter();

// 添加角色权限控制计算属性
const canManageActivities = computed(() => {
  return userStore.isCommitteeSecretary || userStore.isBranchSecretary;
});

// 表单对象
const activityForm = reactive<Activity>({
  category: '',
  requiredTopic: '',
  optionalTopics: '',
  participants: '',
  date: '',
  place: '',
  content: '',
  host: '',
  organization: 0
});

// 活动类别选项
const categoryOptions = [
  { label: '支部大会', value: '支部大会' },
  { label: '团课', value: '团课' },
  { label: '主题团日', value: '主题团日' },
  { label: '入团仪式', value: '入团仪式' },
  { label: '组织生活会', value: '组织生活会' },
  { label: '其他', value: '其他' }
];

// 必学专题选项
const requiredTopicOptions = [
  { label: '学习贯彻党的二十大精神', value: '学习贯彻党的二十大精神' },
  { label: '学习贯彻全国两会精神', value: '学习贯彻全国两会精神' },
  { label: '弘扬新时代雷锋精神', value: '弘扬新时代雷锋精神' },
  { label: '实践活动', value: '实践活动' },
  { label: '特色活动', value: '特色活动' },
  { label: '青年大学习', value: '青年大学习' },
  { label: '其他', value: '其他' }
];

// 自学专题选项
const optionalTopicOptions = [
  { label: '传承五四精神', value: '传承五四精神' },
  { label: '团员素质拓展', value: '团员素质拓展' },
  { label: '其他', value: '其他' }
];

// 表单验证规则
const rules = {
  category: [{ required: true, message: '请选择活动类别', trigger: 'change' }],
  date: [{ required: true, message: '请选择活动日期', trigger: 'change' }],
  place: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  content: [{ required: true, message: '请输入活动内容', trigger: 'blur' }],
  host: [{ required: true, message: '请输入活动主持人', trigger: 'blur' }]
};

// 所选专题标签
const selectedOptionalTopics = ref<string[]>([]);

// 当前组织的团员列表
const membersList = ref<{ id: number; name: string }[]>([]);
// 所选参与人员ID数组
const selectedParticipants = ref<number[]>([]);

// 监听selectedOptionalTopics变化，更新表单中的optionalTopics字段
watch(selectedOptionalTopics, (newVal) => {
  activityForm.optionalTopics = newVal.join(',');
});

// 监听selectedParticipants变化，更新表单中的participants字段
watch(selectedParticipants, (newVal) => {
  activityForm.participants = newVal.join(',');
});

// 表单对话框属性
const dialogVisible = ref(false);
const dialogTitle = ref('新增活动');
const formRef = ref();
const formLoading = ref(false);
const isEdit = ref(false);

// 表格加载状态
const tableLoading = computed(() => activityStore.loading);

// 列表分页参数
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
});

// 查询参数
const queryParams = reactive({
  category: '',
  startDate: '',
  endDate: ''
});

// 监听分页参数变化，重新获取数据
watch(() => pagination.page, () => {
  fetchActivityList();
});

// 获取活动列表
const fetchActivityList = async () => {
  await activityStore.fetchActivities({
    page: pagination.page,
    size: pagination.size,
    category: queryParams.category || undefined,
    startDate: queryParams.startDate || undefined,
    endDate: queryParams.endDate || undefined
  });
  
  pagination.total = activityStore.totalCount;
};

// 打开添加活动对话框
const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = '新增活动';
  resetForm();
  
  // 设置当前用户的组织
  activityForm.organization = userStore.organizationId || 0;
  
  dialogVisible.value = true;
  
  // 获取本组织的成员列表（实际项目中应从API获取）
  fetchOrganizationMembers();
};

// 获取组织成员列表（示例函数，实际项目中应从API获取）
const fetchOrganizationMembers = async () => {
  // 这里应该调用API获取成员列表
  // 示例数据
  membersList.value = [
    { id: 1, name: '张明' },
    { id: 2, name: '李红' },
    { id: 4, name: '赵芳' }
  ];
};

// 打开编辑活动对话框
const handleEdit = (row: Activity) => {
  isEdit.value = true;
  dialogTitle.value = '编辑活动';
  resetForm();
  
  // 填充表单数据
  Object.assign(activityForm, row);
  
  // 转换字符串为数组
  if (activityForm.optionalTopics) {
    selectedOptionalTopics.value = activityForm.optionalTopics.split(',');
  }
  
  if (activityForm.participants) {
    selectedParticipants.value = activityForm.participants.split(',').map(Number);
  }
  
  // 获取组织成员列表
  fetchOrganizationMembers();
  
  dialogVisible.value = true;
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  
  Object.assign(activityForm, {
    id: undefined,
    category: '',
    requiredTopic: '',
    optionalTopics: '',
    participants: '',
    date: '',
    place: '',
    content: '',
    host: '',
    organization: 0
  });
  
  selectedOptionalTopics.value = [];
  selectedParticipants.value = [];
};

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      formLoading.value = true;
      try {
        let result: boolean;
        
        if (isEdit.value) {
          // 更新活动
          result = await activityStore.modifyActivity(activityForm);
          
          if (result) {
            ElMessage.success('活动更新成功');
            dialogVisible.value = false;
            fetchActivityList();
          } else {
            ElMessage.error('活动更新失败');
          }
        } else {
          // 添加活动
          result = await activityStore.createActivity(activityForm);
          
          if (result) {
            ElMessage.success('活动添加成功');
            dialogVisible.value = false;
            fetchActivityList();
          } else {
            ElMessage.error('活动添加失败');
          }
        }
      } catch (error) {
        console.error('提交活动表单出错', error);
        ElMessage.error('操作过程出现错误');
      } finally {
        formLoading.value = false;
      }
    } else {
      ElMessage.warning('请完成表单填写');
      return false;
    }
  });
};

// 删除活动
const handleDelete = async (row: Activity) => {
  if (!row.id) return;
  
  try {
    await ElMessageBox.confirm(
      `确定要删除${row.category} "${row.requiredTopic || ''}"吗？`, 
      '警告', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    const result = await activityStore.removeActivity(row.id);
    
    if (result) {
      ElMessage.success('活动已成功删除');
      fetchActivityList();
    } else {
      ElMessage.error('活动删除失败');
    }
  } catch {
    // 用户取消操作
  }
};

// 获取活动类别对应的标签类型
const getCategoryTagType = (category: string) => {
  switch (category) {
    case '支部大会': return 'danger';
    case '团课': return 'primary';
    case '主题团日': return 'success';
    case '入团仪式': return 'warning';
    case '组织生活会': return 'info';
    default: return '';
  }
};

// 处理搜索查询
const handleSearch = () => {
  pagination.page = 1; // 重置为第一页
  fetchActivityList();
};

// 重置搜索条件
const resetSearch = () => {
  queryParams.category = '';
  queryParams.startDate = '';
  queryParams.endDate = '';
  pagination.page = 1;
  fetchActivityList();
};

// 查看活动详情
const handleViewDetail = (row: Activity) => {
  if (!row.id) return;
  router.push(`/dashboard/activities/${row.id}`);
};

// 导出活动列表
const handleExport = () => {
  try {
    // 获取当前组织名称
    const orgName = userStore.organization || '智慧团建';
    
    // 构建导出文件名
    const fileName = `${orgName}会议活动.xlsx`;
    
    // 准备导出数据 - 处理表格数据
    const exportData = activityStore.activities.map((item: Activity, index: number) => {
      // 转换日期格式
      const createTime = item.createTime ? new Date(item.createTime).toLocaleString() : '-';
      
      // 返回处理后的行数据
      return {
        '序号': index + 1,
        '活动类别': item.category,
        '活动主题': item.requiredTopic || '-',
        '开展时间': item.date || '-',
        '活动地点': item.place || '-',
        '主持人': item.host || '-',
        '活动内容': item.content || '-',
        '创建时间': createTime
      };
    });
    
    // 没有数据时提示
    if (exportData.length === 0) {
      ElMessage.warning('当前没有数据可导出');
      return;
    }
    
    // 创建工作簿和工作表
    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, '会议活动列表');
    
    // 设置列宽
    const colWidths = [
      { wch: 6 },  // 序号
      { wch: 12 }, // 活动类别
      { wch: 30 }, // 活动主题
      { wch: 15 }, // 开展时间
      { wch: 20 }, // 活动地点
      { wch: 10 }, // 主持人
      { wch: 40 }, // 活动内容
      { wch: 20 }  // 创建时间
    ];
    worksheet['!cols'] = colWidths;
    
    // 导出文件
    XLSX.writeFile(workbook, fileName);
    
    ElMessage.success(`成功导出"${fileName}"`);
  } catch (error) {
    console.error('导出活动列表失败:', error);
    ElMessage.error('导出失败，请稍后重试');
  }
};

// 组件挂载时加载数据
onMounted(async () => {
  await fetchActivityList();
  
  // 获取活动类别统计
  await activityStore.fetchCategoryStatistics();
});
</script>

<template>
  <div class="activities-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <h2>会议活动管理</h2>
          <el-button v-if="canManageActivities" type="primary" :icon="Plus" @click="handleAdd">添加活动</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="queryParams" class="demo-form-inline">
          <el-form-item label="活动类别" >
            <el-select 
              v-model="queryParams.category" 
              placeholder="请选择活动类别" 
              clearable 
              style="width: 180px;"
              class="custom-select"
            >
              <el-option 
                v-for="item in categoryOptions" 
                :key="item.value" 
                :label="item.label" 
                :value="item.value"
              >
                <div class="category-option">
                  <el-tag 
                    :type="getCategoryTagType(item.value)" 
                    size="small" 
                    class="category-tag"
                  >
                    {{ item.label }}
                  </el-tag>
                </div>
              </el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="开始日期">
            <el-date-picker
              v-model="queryParams.startDate"
              type="date"
              placeholder="选择开始日期"
              style="width: 180px;"
              format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item label="结束日期">
            <el-date-picker
              v-model="queryParams.endDate"
              type="date"
              placeholder="选择结束日期"
              style="width: 180px;"
              format="YYYY-MM-DD"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch" class="search-btn">搜索</el-button>
            <el-button @click="resetSearch" class="reset-btn">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 操作按钮区域 -->
      <div class="action-bar">
        <el-button v-if="canManageActivities" type="success" plain :icon="Download" @click="handleExport">导出活动</el-button>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        :data="activityStore.activities"
        v-loading="tableLoading"
        border
        style="width: 100%"
        stripe
        highlight-current-row
      >
        <el-table-column type="index" label="序号" width="60" />
        
        <el-table-column prop="category" label="活动类别" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.category === '支部大会'" type="danger">支部大会</el-tag>
            <el-tag v-else-if="row.category === '团课'" type="primary">团课</el-tag>
            <el-tag v-else-if="row.category === '主题团日'" type="success">主题团日</el-tag>
            <el-tag v-else-if="row.category === '入团仪式'" type="warning">入团仪式</el-tag>
            <el-tag v-else-if="row.category === '组织生活会'" type="info">组织生活会</el-tag>
            <el-tag v-else>其他</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="requiredTopic" label="活动主题" min-width="180" show-overflow-tooltip />
        
        <el-table-column prop="date" label="开展时间" width="120" />
        
        <el-table-column prop="place" label="活动地点" min-width="150" show-overflow-tooltip />
        
        <el-table-column prop="host" label="主持人" width="100" />
        
        <el-table-column prop="content" label="活动内容" min-width="180" show-overflow-tooltip />
        
        <el-table-column prop="createTime" label="创建时间" width="160" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.createTime ? new Date(row.createTime).toLocaleString() : '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary" 
              text 
              size="small" 
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button 
              v-if="canManageActivities" 
              type="success" 
              text 
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="canManageActivities" 
              type="danger" 
              text 
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50, 100]"
        />
      </div>
      
      <!-- 添加/编辑活动对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="680px"
        append-to-body
        destroy-on-close
      >
        <el-form
          ref="formRef"
          :model="activityForm"
          :rules="rules"
          label-width="100px"
        >
          <el-form-item label="活动类别" prop="category">
            <el-select v-model="activityForm.category" placeholder="请选择活动类别" style="width: 100%">
              <el-option 
                v-for="item in categoryOptions" 
                :key="item.value" 
                :label="item.label" 
                :value="item.value" 
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="必学专题" prop="requiredTopic">
            <el-select v-model="activityForm.requiredTopic" placeholder="请选择必学专题" style="width: 100%">
              <el-option 
                v-for="item in requiredTopicOptions" 
                :key="item.value" 
                :label="item.label" 
                :value="item.value" 
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="自学专题">
            <el-select
              v-model="selectedOptionalTopics"
              multiple
              collapse-tags
              placeholder="可多选自学专题"
              style="width: 100%"
            >
              <el-option 
                v-for="item in optionalTopicOptions" 
                :key="item.value" 
                :label="item.label" 
                :value="item.value" 
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="参与人员">
            <el-select
              v-model="selectedParticipants"
              multiple
              collapse-tags
              placeholder="请选择参与人员"
              style="width: 100%"
            >
              <el-option 
                v-for="member in membersList" 
                :key="member.id" 
                :label="member.name" 
                :value="member.id" 
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="活动日期" prop="date">
            <el-date-picker
              v-model="activityForm.date"
              type="date"
              placeholder="选择活动日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="活动地点" prop="place">
            <el-input v-model="activityForm.place" placeholder="请输入活动地点" />
          </el-form-item>
          
          <el-form-item label="主持人" prop="host">
            <el-input v-model="activityForm.host" placeholder="请输入活动主持人" />
          </el-form-item>
          
          <el-form-item label="活动内容" prop="content">
            <el-input
              v-model="activityForm.content"
              type="textarea"
              rows="4" 
              placeholder="请输入活动内容"
            />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="submitForm" :loading="formLoading">确 定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<style scoped>
.activities-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.search-area {
  margin-bottom: 20px;
  background-color: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.action-bar {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.custom-select {
  width: 180px;
}

.category-option {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.category-tag {
  width: 100%;
  text-align: center;
  margin-right: 8px;
  font-weight: bold;
}

.search-btn {
  margin-left: 10px;
  background-color: #409EFF;
  border-color: #409EFF;
  font-weight: bold;
  transition: all 0.3s;
}

.search-btn:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.reset-btn {
  margin-left: 10px;
  transition: all 0.3s;
}

.reset-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 详情按钮样式 */
.el-button--text {
  padding: 4px 12px;
  margin: 0 4px;
  border-radius: 4px;
  transition: all 0.3s;
  font-weight: bold;
}

.el-button--text:hover {
  background-color: rgba(64, 158, 255, 0.1);
  transform: translateY(-2px);
}

.el-button--text.el-button--danger:hover {
  background-color: rgba(245, 108, 108, 0.1);
}

.el-button--text.el-button--success:hover {
  background-color: rgba(103, 194, 58, 0.1);
}
</style> 