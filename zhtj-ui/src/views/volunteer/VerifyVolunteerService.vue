<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, View, Check, Close } from '@element-plus/icons-vue';

// 服务审核状态
type VerifyStatus = 'pending' | 'approved' | 'rejected';

// 志愿服务项目接口
interface VolunteerService {
  id: number;
  title: string;
  member_name: string;
  organization: string;
  hours: number;
  service_time: string;
  service_place: string;
  description: string;
  status: VerifyStatus;
  certificate_url?: string;
  photos?: string[];
  submitted_at: string;
}

// 表单搜索条件
const searchForm = reactive({
  keyword: '',
  status: 'pending',
  date_range: [] as string[]
});

// 服务列表数据
const serviceList = ref<VolunteerService[]>([]);
const loading = ref(false);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);

// 当前查看的服务详情
const currentService = ref<VolunteerService | null>(null);
const detailsVisible = ref(false);

// 获取志愿服务列表
const fetchServices = async () => {
  loading.value = true;
  
  try {
    // 这里应该调用实际的API
    // const response = await api.get('/volunteer-services', { params: { ...searchForm, page: currentPage.value, size: pageSize.value } });
    
    // 模拟API返回数据
    await new Promise(resolve => setTimeout(resolve, 600));
    
    // 模拟数据
    serviceList.value = [
      {
        id: 1,
        title: '社区环保志愿服务',
        member_name: '张三',
        organization: '计算机科学与技术专业团支部',
        hours: 4,
        service_time: '2023-10-15',
        service_place: '市中心公园',
        description: '参与社区公园清洁活动，整理绿化带，清理垃圾。',
        status: 'pending',
        certificate_url: '/src/assets/img/volunteer_cert.jpg',
        photos: ['/src/assets/img/img_1.png', '/src/assets/img/img_2.png'],
        submitted_at: '2023-10-16 14:30:00'
      },
      {
        id: 2,
        title: '敬老院慰问活动',
        member_name: '李四',
        organization: '计算机科学与技术专业团支部',
        hours: 6,
        service_time: '2023-09-28',
        service_place: '阳光敬老院',
        description: '为敬老院老人表演节目，打扫卫生，陪老人聊天。',
        status: 'pending',
        certificate_url: '/src/assets/img/volunteer_cert.jpg',
        photos: ['/src/assets/img/img_3.png'],
        submitted_at: '2023-09-29 09:15:00'
      },
      {
        id: 3,
        title: '图书馆志愿服务',
        member_name: '王五',
        organization: '信息工程学院团支部',
        hours: 3,
        service_time: '2023-10-10',
        service_place: '市图书馆',
        description: '协助图书馆整理图书，指导读者使用图书馆设施。',
        status: 'pending',
        certificate_url: '/src/assets/img/volunteer_cert.jpg',
        photos: ['/src/assets/img/img_1.png'],
        submitted_at: '2023-10-11 16:45:00'
      }
    ];
    
    total.value = 23; // 模拟总记录数
  } catch (error) {
    console.error('获取志愿服务记录失败', error);
    ElMessage.error('获取志愿服务记录失败');
  } finally {
    loading.value = false;
  }
};

// 搜索方法
const handleSearch = () => {
  currentPage.value = 1;
  fetchServices();
};

// 重置搜索条件
const resetSearch = () => {
  searchForm.keyword = '';
  searchForm.status = 'pending';
  searchForm.date_range = [];
  handleSearch();
};

// 处理分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchServices();
};

// 处理每页显示数量变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  fetchServices();
};

// 查看服务详情
const viewServiceDetails = (service: VolunteerService) => {
  currentService.value = service;
  detailsVisible.value = true;
};

// 审核通过服务
const approveService = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认通过该志愿服务记录审核吗？', '审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 在实际应用中，这里应该调用API
    // await api.post(`/volunteer-services/${id}/approve`);
    
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 更新本地数据
    const serviceIndex = serviceList.value.findIndex(s => s.id === id);
    if (serviceIndex !== -1) {
      serviceList.value[serviceIndex].status = 'approved';
    }
    
    ElMessage.success('审核通过成功');
    
    // 如果正在查看详情，关闭详情弹窗
    if (detailsVisible.value && currentService.value?.id === id) {
      detailsVisible.value = false;
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核操作失败', error);
      ElMessage.error('审核操作失败');
    }
  }
};

// 驳回服务
const rejectService = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认驳回该志愿服务记录吗？', '审核确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    // 在实际应用中，这里应该调用API
    // await api.post(`/volunteer-services/${id}/reject`);
    
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 500));
    
    // 更新本地数据
    const serviceIndex = serviceList.value.findIndex(s => s.id === id);
    if (serviceIndex !== -1) {
      serviceList.value[serviceIndex].status = 'rejected';
    }
    
    ElMessage.success('已驳回该志愿服务记录');
    
    // 如果正在查看详情，关闭详情弹窗
    if (detailsVisible.value && currentService.value?.id === id) {
      detailsVisible.value = false;
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('驳回操作失败', error);
      ElMessage.error('驳回操作失败');
    }
  }
};

// 关闭详情
const closeDetails = () => {
  detailsVisible.value = false;
  currentService.value = null;
};

// 格式化状态
const formatStatus = (status: VerifyStatus) => {
  const statusMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已驳回'
  };
  return statusMap[status] || status;
};

// 获取状态标签类型
const getStatusType = (status: VerifyStatus) => {
  const typeMap = {
    pending: '',
    approved: 'success',
    rejected: 'danger'
  };
  return typeMap[status] || '';
};

// 组件挂载时加载数据
onMounted(() => {
  fetchServices();
});
</script>

<template>
  <div class="verify-container">
    <el-card shadow="hover" class="search-card">
      <template #header>
        <div class="card-header">
          <h3>志愿服务审核</h3>
        </div>
      </template>
      
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="关键词">
          <el-input 
            v-model="searchForm.keyword" 
            placeholder="服务名称/提交人" 
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        
        <el-form-item label="审核状态">
          <el-select v-model="searchForm.status" placeholder="选择状态" clearable>
            <el-option label="全部" value="" />
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已驳回" value="rejected" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="提交日期">
          <el-date-picker
            v-model="searchForm.date_range"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card shadow="hover" class="table-card">
      <el-table 
        v-loading="loading" 
        :data="serviceList" 
        border 
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="服务名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="member_name" label="提交人" width="100" />
        <el-table-column prop="organization" label="所属组织" min-width="180" show-overflow-tooltip />
        <el-table-column prop="hours" label="服务时长" width="100">
          <template #default="scope">
            {{ scope.row.hours }} 小时
          </template>
        </el-table-column>
        <el-table-column prop="service_time" label="服务日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="light">
              {{ formatStatus(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitted_at" label="提交时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button 
              type="primary" 
              :icon="View" 
              size="small"
              @click="viewServiceDetails(scope.row)"
              text
            >
              查看
            </el-button>
            
            <el-button 
              v-if="scope.row.status === 'pending'" 
              type="success" 
              :icon="Check" 
              size="small"
              @click="approveService(scope.row.id)"
              text
            >
              通过
            </el-button>
            
            <el-button 
              v-if="scope.row.status === 'pending'" 
              type="danger" 
              :icon="Close" 
              size="small"
              @click="rejectService(scope.row.id)"
              text
            >
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
          background
        />
      </div>
    </el-card>
    
    <!-- 服务详情对话框 -->
    <el-dialog
      v-model="detailsVisible"
      title="志愿服务详情"
      width="700px"
      destroy-on-close
    >
      <el-descriptions v-if="currentService" :column="2" border>
        <el-descriptions-item label="服务名称" :span="2">{{ currentService.title }}</el-descriptions-item>
        <el-descriptions-item label="提交人">{{ currentService.member_name }}</el-descriptions-item>
        <el-descriptions-item label="所属组织">{{ currentService.organization }}</el-descriptions-item>
        <el-descriptions-item label="服务时长">{{ currentService.hours }} 小时</el-descriptions-item>
        <el-descriptions-item label="服务日期">{{ currentService.service_time }}</el-descriptions-item>
        <el-descriptions-item label="服务地点" :span="2">{{ currentService.service_place }}</el-descriptions-item>
        <el-descriptions-item label="服务描述" :span="2">
          {{ currentService.description }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentService.status)" effect="light">
            {{ formatStatus(currentService.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentService.submitted_at }}</el-descriptions-item>
        
        <el-descriptions-item label="证明材料" :span="2" v-if="currentService.certificate_url">
          <el-image 
            :src="currentService.certificate_url" 
            fit="contain"
            style="max-width: 100%; max-height: 200px;"
          />
        </el-descriptions-item>
        
        <el-descriptions-item label="活动照片" :span="2" v-if="currentService.photos && currentService.photos.length > 0">
          <el-carousel 
            height="200px" 
            indicator-position="outside"
            arrow="always"
            :autoplay="false"
          >
            <el-carousel-item v-for="(photo, index) in currentService.photos" :key="index">
              <el-image 
                :src="photo" 
                fit="contain"
                style="width: 100%; height: 100%;"
              />
            </el-carousel-item>
          </el-carousel>
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeDetails">关闭</el-button>
          <el-button
            v-if="currentService && currentService.status === 'pending'"
            type="success"
            @click="approveService(currentService.id)"
          >
            通过审核
          </el-button>
          <el-button
            v-if="currentService && currentService.status === 'pending'"
            type="danger"
            @click="rejectService(currentService.id)"
          >
            驳回申请
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.verify-container {
  padding: 20px 0;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .search-form .el-form-item {
    margin-right: 0;
    width: 100%;
  }
  
  .el-form-item__content {
    width: 100%;
  }
  
  .el-input, .el-select, .el-date-picker {
    width: 100%;
  }
}
</style> 