<template>
  <div class="volunteer-services">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>志愿服务记录</h3>
          <el-button type="primary" @click="$router.push('/dashboard/volunteer-service/add')">
            <el-icon><Plus /></el-icon>添加记录
          </el-button>
        </div>
      </template>

      <el-form :model="queryParams" ref="queryForm" :inline="true" class="search-form">
        <el-form-item label="服务年度">
          <el-select v-model="queryParams.year" placeholder="选择年度" clearable>
            <el-option 
              v-for="year in years" 
              :key="year" 
              :label="year" 
              :value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="服务类型">
          <el-select v-model="queryParams.serviceType" placeholder="选择服务类型" clearable>
            <el-option 
              v-for="type in SERVICE_TYPES" 
              :key="type.value" 
              :label="type.label" 
              :value="type.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="queryParams.verificationStatus" placeholder="选择状态" clearable>
            <el-option 
              v-for="status in VERIFICATION_STATUS_OPTIONS" 
              :key="status.value" 
              :label="status.label" 
              :value="status.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="serviceList"
        style="width: 100%"
        border
        stripe
        highlight-current-row
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="服务时间" align="center" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.serviceStartTime, 'YYYY-MM-DD HH:mm') }}
          </template>
        </el-table-column>
        <el-table-column prop="serviceDescription" label="服务描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="serviceLocation" label="服务地点" width="150" show-overflow-tooltip />
        <el-table-column prop="serviceType" label="服务类型" width="100" align="center" />
        <el-table-column prop="serviceDuration" label="服务时长(小时)" width="120" align="center" />
        <el-table-column label="审核状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.verificationStatus)">
              {{ getStatusName(scope.row.verificationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button
              link
              type="primary"
              @click="viewDetails(scope.row)"
              size="small"
            >
              详情
            </el-button>
            <el-button
              v-if="scope.row.verificationStatus === 0"
              link
              type="danger"
              @click="handleDelete(scope.row)"
              size="small"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailsVisible" title="志愿服务详情" width="650px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="服务年度">{{ selectedService.serviceYear }}</el-descriptions-item>
        <el-descriptions-item label="服务类型">{{ selectedService.serviceType }}</el-descriptions-item>
        <el-descriptions-item label="服务时长(小时)">{{ selectedService.serviceDuration }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(selectedService.verificationStatus)">
            {{ getStatusName(selectedService.verificationStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务地点" :span="2">{{ selectedService.serviceLocation }}</el-descriptions-item>
        <el-descriptions-item label="服务时间" :span="2">
          {{ formatDate(selectedService.serviceStartTime) }} 至 {{ formatDate(selectedService.serviceEndTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="服务描述" :span="2">{{ selectedService.serviceDescription }}</el-descriptions-item>
        <el-descriptions-item v-if="selectedService.verificationStatus !== 0" label="验证人" :span="2">
          {{ selectedService.verifierUserName }}
        </el-descriptions-item>
        <el-descriptions-item v-if="selectedService.verificationRemark" label="验证备注" :span="2">
          {{ selectedService.verificationRemark }}
        </el-descriptions-item>
      </el-descriptions>
      <div class="service-proof" v-if="selectedService.serviceProof">
        <h4>服务证明</h4>
        <div class="image-list">
          <el-image
            v-for="(url, index) in getProofImages(selectedService.serviceProof)"
            :key="index"
            :src="url"
            :preview-src-list="getProofImages(selectedService.serviceProof)"
            fit="cover"
            class="proof-image"
          >
            <template #error>
              <div class="image-error">
                <el-icon><Document /></el-icon>
                <div>非图片文件</div>
              </div>
            </template>
          </el-image>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Search, Refresh, Document } from '@element-plus/icons-vue';
import { getVolunteerServicePage } from '@/api/volunteer';
import { VolunteerService, VolunteerServiceQuery, SERVICE_TYPES, VERIFICATION_STATUS_OPTIONS } from '@/types/volunteer';
import { useUserStore } from '@/stores/user';
import { useRouter } from 'vue-router';

const router = useRouter();

// 用户信息
const userStore = useUserStore();
const userId = computed(() => userStore.user?.id);

// 查询参数
const queryParams = reactive<VolunteerServiceQuery>({
  userId: userId.value,
  pageNum: 1,
  pageSize: 10,
  orderBy: 'serviceStartTime DESC'
});

// 表格数据
const loading = ref(false);
const serviceList = ref<VolunteerService[]>([]);
const total = ref(0);
const selectedRows = ref<VolunteerService[]>([]);

// 详情弹窗
const detailsVisible = ref(false);
const selectedService = ref<VolunteerService>({} as VolunteerService);

// 获取年份选项（近5年）
const years = computed(() => {
  const currentYear = new Date().getFullYear();
  const years = [];
  for (let i = 0; i < 5; i++) {
    years.push(currentYear - i);
  }
  return years;
});

// 获取数据
const getList = async () => {
  loading.value = true;
  try {
    const res = await getVolunteerServicePage(queryParams);
    serviceList.value = res.list || [];
    total.value = res.total || 0;
  } catch (error) {
    ElMessage.error('获取志愿服务记录失败');
  } finally {
    loading.value = false;
  }
};

// 处理查询
const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};

// 重置查询条件
const resetQuery = () => {
  queryParams.year = undefined;
  queryParams.serviceType = undefined;
  queryParams.verificationStatus = undefined;
  handleQuery();
};

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val;
  getList();
};

// 处理页码变化
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val;
  getList();
};

// 复选框选择事件
const handleSelectionChange = (selection: VolunteerService[]) => {
  selectedRows.value = selection;
};

// 查看详情
const viewDetails = (row: VolunteerService) => {
  selectedService.value = row;
  detailsVisible.value = true;
};

// 删除记录
const handleDelete = (row: VolunteerService) => {
  ElMessageBox.confirm('确定要删除该志愿服务记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功');
    getList();
  }).catch(() => {});
};

// 格式化日期
const formatDate = (dateStr: string, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');
  const seconds = date.getSeconds().toString().padStart(2, '0');
  
  return format
    .replace('YYYY', year.toString())
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
};

// 获取状态对应的样式类型
const getStatusType = (status: number) => {
  switch (status) {
    case 0: return 'info';
    case 1: return 'success';
    case 2: return 'danger';
    default: return 'info';
  }
};

// 获取状态名称
const getStatusName = (status: number) => {
  const found = VERIFICATION_STATUS_OPTIONS.find(item => item.value === status);
  return found ? found.label : '未知状态';
};

// 解析服务证明图片
const getProofImages = (proof: string | string[] | undefined) => {
  if (!proof) return [];
  if (typeof proof === 'string') {
    try {
      return JSON.parse(proof);
    } catch {
      return [proof];
    }
  }
  return proof;
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.volunteer-services {
  padding: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.service-proof {
  margin-top: 16px;
  border-top: 1px solid #eee;
  padding-top: 16px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.proof-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 12px;
  width: 100%;
  height: 100%;
}
</style> 