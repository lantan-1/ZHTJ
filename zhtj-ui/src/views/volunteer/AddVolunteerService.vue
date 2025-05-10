<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { Plus, Delete, View, Picture as PictureIcon } from '@element-plus/icons-vue';
import { submitVolunteerService } from '../../api/volunteer';

const router = useRouter();

// 服务类型选项
const serviceTypeOptions = [
  { label: '社区服务', value: '社区服务' },
  { label: '环境保护', value: '环境保护' },
  { label: '助学支教', value: '助学支教' },
  { label: '扶贫帮困', value: '扶贫帮困' },
  { label: '医疗健康', value: '医疗健康' },
  { label: '文化宣传', value: '文化宣传' },
  { label: '应急救援', value: '应急救援' },
  { label: '其他', value: '其他' }
];

// 表单数据
const serviceForm = reactive({
  title: '',
  description: '',
  service_time: '',
  service_place: '',
  hours: 0,
  serviceType: '',
  certificate: [] as Array<any>,
  photos: [] as Array<any>
});

// 表单规则
const rules = reactive({
  title: [
    { required: true, message: '请输入服务标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入服务描述', trigger: 'blur' },
    { min: 10, max: 500, message: '长度在 10 到 500 个字符', trigger: 'blur' }
  ],
  service_time: [
    { required: true, message: '请选择服务时间', trigger: 'change' }
  ],
  service_place: [
    { required: true, message: '请输入服务地点', trigger: 'blur' }
  ],
  hours: [
    { required: true, message: '请输入服务时长', trigger: 'blur' },
    { type: 'number', min: 0.5, message: '时长必须大于0.5小时', trigger: 'blur' }
  ],
  serviceType: [
    { required: true, message: '请选择服务类型', trigger: 'change' }
  ]
});

// 引用表单
const serviceFormRef = ref<any>(null);
const certificateUploadRef = ref<any>(null);
const photosUploadRef = ref<any>(null);

// 提交表单
const submitForm = async () => {
  if (!serviceFormRef.value) return;
  
  await serviceFormRef.value.validate((valid: boolean) => {
    if (valid) {
      // 显示加载中
      const loading = ElMessage({
        message: '提交中...',
        type: 'info',
        duration: 0
      });
      
      // 模拟API调用
      setTimeout(() => {
        loading.close();
        ElMessage({
          message: '服务记录提交成功，等待审核',
          type: 'success'
        });
        
        // 提交成功后跳转到列表页
        router.push('/dashboard/volunteer-services');
      }, 1500);
    } else {
      ElMessage({
        message: '请完善表单信息',
        type: 'error'
      });
      return false;
    }
  });
};

// 返回列表页
const goBack = () => {
  router.push('/dashboard/volunteer-services');
};

// 重置表单
const resetForm = () => {
  if (serviceFormRef.value) {
    serviceFormRef.value.resetFields();
  }
  serviceForm.certificate = [];
  serviceForm.photos = [];
};

// 上传前验证文件
const beforeUpload = (file: any) => {
  // 验证文件类型
  const isImage = file.type.startsWith('image/');
  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  
  // 验证文件大小
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!');
    return false;
  }
  
  return true;
};

// 上传成功回调
const handleSuccess = (response: any, uploadFile: any) => {
  ElMessage.success('上传成功');
};

// 上传失败回调
const handleError = (error: any) => {
  ElMessage.error('上传失败，请重试');
};

// 图片预览
const handlePreview = (file: any) => {
  if (file.url) {
    window.open(file.url);
  }
};

// 删除图片
const handleRemove = (file: any, fileList: any[]) => {
  ElMessage.success('删除成功');
};
</script>

<template>
  <div class="add-service-container">
    <div class="page-header">
      <h2>添加志愿服务记录</h2>
      <p>请填写您的志愿服务详情，提交后将由系统审核</p>
    </div>
    
    <el-form
      ref="serviceFormRef"
      :model="serviceForm"
      :rules="rules"
      label-width="100px"
      class="service-form"
    >
      <el-form-item label="服务标题" prop="title">
        <el-input v-model="serviceForm.title" placeholder="请输入服务标题，例如：社区环境清洁活动"></el-input>
      </el-form-item>
      
      <el-form-item label="服务类型" prop="serviceType">
        <el-select 
          v-model="serviceForm.serviceType" 
          placeholder="请选择服务类型"
          style="width: 100%"
        >
          <el-option 
            v-for="item in serviceTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      
      <el-form-item label="服务时间" prop="service_time">
        <el-date-picker
          v-model="serviceForm.service_time"
          type="date"
          placeholder="选择服务日期"
          style="width: 100%"
        ></el-date-picker>
      </el-form-item>
      
      <el-form-item label="服务地点" prop="service_place">
        <el-input v-model="serviceForm.service_place" placeholder="请输入服务地点"></el-input>
      </el-form-item>
      
      <el-form-item label="服务时长" prop="hours">
        <el-input-number 
          v-model="serviceForm.hours" 
          :min="0.5" 
          :step="0.5" 
          :precision="1"
          placeholder="请输入服务时长(小时)"
          style="width: 100%"
        ></el-input-number>
      </el-form-item>
      
      <el-form-item label="服务描述" prop="description">
        <el-input 
          v-model="serviceForm.description" 
          type="textarea" 
          :rows="4"
          placeholder="请详细描述您的志愿服务内容、过程和成果"
        ></el-input>
      </el-form-item>
      
      <el-form-item label="服务证明">
        <el-upload
          ref="certificateUploadRef"
          action="#"
          list-type="picture-card"
          :auto-upload="false"
          :before-upload="beforeUpload"
          :on-success="handleSuccess"
          :on-error="handleError"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :file-list="serviceForm.certificate"
        >
          <el-icon><Plus /></el-icon>
          <template #tip>
            <div class="el-upload__tip">
              请上传盖章的服务证明文件，仅支持jpg/png格式，单个文件不超过2MB
            </div>
          </template>
        </el-upload>
      </el-form-item>
      
      <el-form-item label="服务照片">
        <el-upload
          ref="photosUploadRef"
          action="#"
          list-type="picture-card"
          :auto-upload="false"
          :before-upload="beforeUpload"
          :on-success="handleSuccess"
          :on-error="handleError"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :file-list="serviceForm.photos"
        >
          <el-icon><Plus /></el-icon>
          <template #tip>
            <div class="el-upload__tip">
              请上传服务过程的照片，帮助审核，仅支持jpg/png格式，单个文件不超过2MB
            </div>
          </template>
        </el-upload>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitForm">提交申请</el-button>
        <el-button @click="resetForm">重置</el-button>
        <el-button @click="goBack">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.add-service-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 30px;
  border-bottom: 1px solid #ebeef5;
  padding-bottom: 15px;
}

.page-header h2 {
  font-size: 22px;
  color: #303133;
  margin-bottom: 10px;
}

.page-header p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.service-form {
  max-width: 800px;
}

.el-form-item {
  margin-bottom: 22px;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .service-form {
    width: 100%;
  }
  
  .el-form-item {
    margin-bottom: 15px;
  }
}
</style> 
