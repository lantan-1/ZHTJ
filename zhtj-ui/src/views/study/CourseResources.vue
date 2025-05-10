<template>
  <div class="course-resources-container">

    <el-card class="resource-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item>
          <el-button type="primary" @click="handleAdd" v-if="userStore.isBranchSecretary || userStore.isCommitteeSecretary">
            <el-icon><Plus /></el-icon>添加资源
          </el-button>
        </el-form-item>
        <el-form-item label="资源名称">
          <el-input v-model="queryParams.title" placeholder="请输入资源名称" clearable />
        </el-form-item>
        <el-form-item label="资源分类">
          <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable>
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
      
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane v-for="type in fileTypeList" :key="type.code" :label="type.name" :name="type.code"></el-tab-pane>
      </el-tabs>
      
      <el-table
        v-loading="loading"
        :data="resourceList"
        style="width: 100%"
        border
      >
        <el-table-column prop="title" label="资源名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="format" label="格式" width="80">
          <template #default="{ row }">
            <el-tag :type="getFormatTagType(row.format)">{{ row.format }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="creatorName" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="success" link @click="handleDownload(row)">下载</el-button>
            <el-button type="danger" link @click="handleDelete(row)" 
              v-if="userStore.isBranchSecretary || userStore.isCommitteeSecretary || row.creatorId === userStore.userId">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-if="totalRecords > 0"
        class="pagination"
        :total="totalRecords"
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
      />
    </el-card>
    
    <!-- 添加/编辑对话框 -->
    <el-dialog 
      :title="dialogType === 'add' ? '添加团课资源' : '编辑团课资源'" 
      v-model="dialogVisible"
      width="600px"
      append-to-body
    >
      <el-form 
        ref="resourceFormRef"
        :model="resourceForm"
        :rules="resourceRules"
        label-width="100px"
      >
        <el-form-item label="资源名称" prop="title">
          <el-input v-model="resourceForm.title" placeholder="请输入资源名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="resourceForm.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in categoryOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="资源描述" prop="description">
          <el-input 
            v-model="resourceForm.description" 
            type="textarea"
            placeholder="请输入资源描述" 
            maxlength="500"
            :rows="4"
          />
        </el-form-item>
        <el-form-item label="资源文件">
          <el-upload
            class="resource-uploader"
            action="#"
            :auto-upload="false"
            :on-change="handleFileChange"
            :before-upload="beforeUpload"
            :on-remove="handleFileRemove"
            :limit="1"
            :file-list="fileList"
            ref="uploadRef"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持格式：DOC, DOCX, PPT, PPTX, PDF, MP4, MP3等，单个文件不超过100MB！
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确认</el-button>
      </template>
    </el-dialog>
    
    <!-- 资源预览对话框 -->
    <el-dialog
      title="资源预览"
      v-model="previewVisible"
      width="80%"
      append-to-body
      destroy-on-close
    >
      <template v-if="previewData.format === 'PDF'">
        <vue-office-pdf :src="previewUrl" style="height: 600px;" @rendered="onDocRendered" @error="onDocError" />
      </template>
      <template v-else-if="previewData.format === 'DOC' || previewData.format === 'DOCX'">
        <vue-office-docx :src="previewUrl" style="height: 600px;" @rendered="onDocRendered" @error="onDocError" />
      </template>
      <template v-else-if="previewData.format === 'XLS' || previewData.format === 'XLSX'">
        <vue-office-excel :src="previewUrl" style="height: 600px;" @rendered="onDocRendered" @error="onDocError" />
      </template>
      <template v-else-if="previewData.format === 'PPT' || previewData.format === 'PPTX'">
        <vue-office-docx :src="previewUrl" style="height: 600px;" @rendered="onDocRendered" @error="onDocError" />
      </template>
      <template v-else-if="previewData.format === 'MP4' || previewData.format === 'VIDEO'">
        <video :src="previewUrl" controls width="100%" height="auto" 
          style="max-height: 600px;" controlsList="nodownload">
          您的浏览器不支持视频播放，请<el-button type="primary" link @click="handleDownload(previewData)">下载文件</el-button>观看
        </video>
      </template>
      <template v-else-if="previewData.format === 'MP3' || previewData.format === 'AUDIO'">
        <audio :src="previewUrl" controls style="width: 100%"></audio>
      </template>
      <template v-else>
        <div class="preview-error">
          <el-icon><WarningFilled /></el-icon>
          <p>该文件格式不支持在线预览，请下载后查看</p>
          <el-button type="primary" @click="handleDownload(previewData)">下载文件</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { Plus, Search, Refresh, WarningFilled } from '@element-plus/icons-vue';
import PageHeader from '@/components/common/PageHeader.vue';
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus';
import type { FormInstance, FormRules, UploadProps } from 'element-plus';
import { useUserStore } from '@/stores/user';
import { formatDate, formatFileSize } from '@/utils/format';
import { 
  getResourceList as fetchResourceList, 
  uploadResource,
  deleteResource,
  getResourceCategories,
  downloadResource,
  previewResource as fetchPreviewResource,
  getFileTypes,
  type StudyResource,
  type ResourceQueryParams
} from '../../api/study';
// 导入vue-office相关组件
// @ts-ignore 忽略类型检查错误
import VueOfficePdf from '@vue-office/pdf';
// @ts-ignore 忽略类型检查错误
import VueOfficeDocx from '@vue-office/docx';
// @ts-ignore 忽略类型检查错误
import VueOfficeExcel from '@vue-office/excel';

// 用户信息
const userStore = useUserStore();

// 查询参数
const queryParams = reactive<ResourceQueryParams>({
  title: '',
  categoryId: undefined,
  format: '',
  page: 1,
  size: 10
});

// 分页参数
const pageNum = ref(1);
const pageSize = ref(10);
const totalRecords = ref(0);

// 资源列表
const resourceList = ref<StudyResource[]>([]);
const loading = ref(false);
const activeTab = ref('all');

// 资源分类选项
const categoryOptions = ref<{value: number, label: string}[]>([]);

// 对话框相关
const dialogVisible = ref(false);
const dialogType = ref<'add' | 'edit'>('add');
const resourceFormRef = ref<any>();
const submitLoading = ref(false);

// 资源表单
interface ResourceFormType extends Partial<StudyResource> {
  hasSelectedFile?: boolean;
}

const resourceForm = reactive<ResourceFormType>({
  id: undefined,
  title: '',
  categoryId: undefined,
  description: '',
  fileUrl: '',
  fileName: '',
  fileSize: 0,
  format: '',
  organizationId: userStore.organizationId,
  hasSelectedFile: false
});

// 表单验证规则
const resourceRules = {
  title: [
    { required: true, message: '请输入资源名称', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择资源分类', trigger: 'change' }
  ]
} as any;

// 文件上传相关
const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/api/study-resources/upload`;
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token') || ''}`
};
const fileList = ref<any[]>([]);
const uploadRef = ref<any>(null);

// 变量名冲突修复
const previewData = ref<StudyResource>({} as StudyResource);
const previewUrl = ref('');
const previewVisible = ref(false);

// 在script setup部分添加
const fileTypeList = ref<any[]>([]);

// 生命周期钩子
onMounted(() => {
  getResourceList();
  getCategoryOptions();
  getFileTypeList();
});

// 获取资源分类
const getCategoryOptions = async () => {
  try {
    const res = await getResourceCategories();
    categoryOptions.value = res.data.map((item: any) => ({
      value: item.id || item.value,
      label: item.name || item.label
    }));
  } catch (error) {
    console.error('获取资源分类失败:', error);
  }
};

// 获取资源列表
const getResourceList = async () => {
  loading.value = true;
  try {
    const params: ResourceQueryParams = {
      title: queryParams.title,
      categoryId: queryParams.categoryId,
      format: queryParams.format,
      page: pageNum.value,
      size: pageSize.value
    };

    const res = await fetchResourceList(params);
    resourceList.value = res.data.list || [];
    totalRecords.value = res.data.total || 0;
  } catch (error) {
    ElMessage.error('获取资源列表失败');
    console.error('获取资源列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 查询操作
const handleQuery = () => {
  pageNum.value = 1;
  getResourceList();
};

// 重置查询
const resetQuery = () => {
  queryParams.title = '';
  queryParams.categoryId = undefined;
  activeTab.value = 'all';
  pageNum.value = 1;
  getResourceList();
};

// 处理标签页切换
const handleTabClick = () => {
  if (activeTab.value === 'all') {
    // 全部标签
    queryParams.format = '';
  } else {
    // 如果是特定文件类型标签，直接使用标签名作为format参数
    queryParams.format = activeTab.value;
    console.log('选择文件类型:', activeTab.value);
  }
  pageNum.value = 1;
  getResourceList();
};

// 分页操作
const handleSizeChange = (val: number) => {
  pageSize.value = val;
  getResourceList();
};

const handleCurrentChange = (val: number) => {
  pageNum.value = val;
  getResourceList();
};

// 添加资源
const handleAdd = () => {
  resetResourceForm();
  dialogType.value = 'add';
  dialogVisible.value = true;
};

// 重置资源表单
const resetResourceForm = () => {
  if (resourceFormRef.value) {
    resourceFormRef.value.resetFields();
  }
  fileList.value = [];
  Object.assign(resourceForm, {
    id: undefined,
    title: '',
    categoryId: undefined,
    description: '',
    fileUrl: '',
    fileName: '',
    fileSize: 0,
    format: '',
    organizationId: userStore.organizationId
  });
};

// 文件变更处理
const handleFileChange = (uploadFile: any, uploadFiles: any[]) => {
  console.log('文件变更:', uploadFile.name, uploadFile);
  console.log('当前文件列表:', uploadFiles);
  
  // 保存文件列表
  fileList.value = uploadFiles;
  
  // 设置文件已选择标志
  resourceForm.hasSelectedFile = true;
};

// 文件移除处理
const handleFileRemove = (uploadFile: any, uploadFiles: any[]) => {
  console.log('文件移除:', uploadFile.name);
  console.log('当前文件列表:', uploadFiles);
  
  // 更新文件列表
  fileList.value = uploadFiles;
  
  // 如果没有文件了，重置标志
  if (uploadFiles.length === 0) {
    resourceForm.hasSelectedFile = false;
  }
};

// 提交表单
const submitForm = async () => {
  if (!resourceFormRef.value) return;
  
  console.log('提交表单，文件列表:', fileList.value);
  
  if (fileList.value.length === 0) {
    ElMessage.error('请上传资源文件');
    return;
  }
  
  await resourceFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitLoading.value = true;
      try {
        const formData = new FormData();
        formData.append('title', resourceForm.title || '');
        formData.append('description', resourceForm.description || '');
        formData.append('categoryId', String(resourceForm.categoryId || ''));
        formData.append('organizationId', String(resourceForm.organizationId || ''));
        
        // 获取文件对象
        const uploadFile = fileList.value[0];
        let fileToUpload: File | null = null;
        
        // 尝试从不同属性获取文件对象
        if (uploadFile.raw) {
          fileToUpload = uploadFile.raw;
          console.log('从raw属性获取文件:', uploadFile.raw);
        } else if ((uploadFile as any).originFileObj) {
          fileToUpload = (uploadFile as any).originFileObj;
          console.log('从originFileObj获取文件:', (uploadFile as any).originFileObj);
        }
        
        if (fileToUpload) {
          console.log('添加文件到FormData:', fileToUpload.name, fileToUpload.size, fileToUpload.type);
          formData.append('file', fileToUpload);
          
          // 输出FormData内容
          for (const pair of formData.entries()) {
            console.log(pair[0] + ': ' + (pair[1] instanceof File ? `${pair[1].name} (${pair[1].size} bytes)` : pair[1]));
          }
          
          const result = await uploadResource(formData);
          console.log('上传结果:', result);
          ElMessage.success('上传成功');
          dialogVisible.value = false;
          getResourceList();
        } else {
          throw new Error('无法获取文件对象，请重新选择文件');
        }
      } catch (error: any) {
        console.error('上传失败:', error);
        ElMessage.error(`上传失败: ${error.message || '未知错误'}`);
      } finally {
        submitLoading.value = false;
      }
    }
  });
};

// 文件上传前检查
const beforeUpload = (file: File) => {
  // 文件大小限制 (100MB)
  const isLt100M = file.size / 1024 / 1024 < 100;
  if (!isLt100M) {
    ElMessage.error('文件大小不能超过 100MB!');
    return false;
  }
  
  console.log('文件检查通过:', file.name, file.size, file.type);
  return true;
};

// 查看资源
const handleView = async (row: StudyResource) => {
  try {
    const res = await fetchPreviewResource(row.id as number);
    previewData.value = row;
    
    // 检查是否有完整的URL
    let previewUrlValue = res.data.previewUrl || row.fileUrl || '';
    
    // 如果URL不是以http开头，添加API基础URL
    if (previewUrlValue && !previewUrlValue.startsWith('http')) {
      previewUrlValue = `${import.meta.env.VITE_API_BASE_URL}${previewUrlValue}`;
    }
    
    previewUrl.value = previewUrlValue;
    console.log('预览URL:', previewUrl.value);
    console.log('资源格式:', row.format);
  previewVisible.value = true;
  } catch (error) {
    ElMessage.error('获取预览信息失败');
    console.error('获取预览信息失败:', error);
  }
};

// 文档渲染成功回调
const onDocRendered = () => {
  console.log('文档渲染成功');
};

// 文档渲染失败回调
const onDocError = (error: any) => {
  console.error('文档渲染失败:', error);
  ElMessage.error('文档渲染失败，请尝试下载后查看');
};

// 下载资源
const handleDownload = async (row: StudyResource) => {
  const loadingInstance = ElLoading.service({
    text: '下载中...',
    background: 'rgba(0, 0, 0, 0.7)'
  });
  
  try {
    // 获取下载数据 - 指定responseType为blob
    const response = await downloadResource(row.id as number);
    
    // 处理响应数据，确保能获取Blob
    let blob: Blob;
    
    // 如果响应是标准axios格式(有data, headers等属性)
    if (response && response.data && response.data instanceof Blob) {
      blob = response.data;
    } 
    // 如果响应本身就是Blob
    else if (response instanceof Blob) {
      blob = response;
    }
    // 其他情况
    else {
      console.error('响应格式错误，无法提取Blob数据:', response);
      throw new Error('下载失败: 服务器返回的数据格式不正确');
    }
    
    // 设置文件名（使用资源信息）
    const fileName = row.fileName || `${row.title}.${row.format?.toLowerCase()}`;
    
    // 使用 Element Plus 下载方式
    const url = URL.createObjectURL(blob);
    
    // 使用下载组件（通过模拟隐藏链接点击）
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    link.style.display = 'none';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    // 延迟释放URL
    setTimeout(() => {
      URL.revokeObjectURL(url);
      loadingInstance.close();
      ElMessage.success('下载成功');
    }, 100);
  } catch (error) {
    console.error('下载失败详情:', error);
    loadingInstance.close();
    ElMessage.error('下载失败，请稍后重试');
  }
};

// 删除资源
const handleDelete = (row: StudyResource) => {
  ElMessageBox.confirm(
    `确认删除资源 "${row.title}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteResource(row.id as number);
      ElMessage.success('删除成功');
      getResourceList();
    } catch (error) {
      ElMessage.error('删除失败');
      console.error('删除失败:', error);
    }
  }).catch(() => {
    // 取消删除
  });
};

// 获取格式标签的类型
const getFormatTagType = (format: string) => {
  if (!format) return '';
  const upperFormat = format.toUpperCase();
  
  // 根据文件格式返回不同的颜色
  if (['DOC', 'DOCX', 'TXT', 'PDF'].includes(upperFormat)) {
    return 'primary'; // 文档类型-蓝色
  } else if (['XLS', 'XLSX'].includes(upperFormat)) {
    return 'success'; // Excel类型-绿色
  } else if (['PPT', 'PPTX'].includes(upperFormat)) {
    return 'warning'; // PPT类型-黄色
  } else if (['MP4', 'AVI', 'MOV'].includes(upperFormat)) {
    return 'danger'; // 视频类型-红色
  } else if (['MP3', 'WAV', 'FLAC', 'AAC'].includes(upperFormat)) {
    return 'info'; // 音频类型-灰色
  } else if (['JPG', 'JPEG', 'PNG', 'GIF', 'BMP'].includes(upperFormat)) {
    return ''; // 图片类型-默认
  } else {
    return '';
  }
};

// 添加获取文件类型的方法
const getFileTypeList = async () => {
  try {
    const res = await getFileTypes();
    fileTypeList.value = res.data;
    console.log('文件类型列表:', fileTypeList.value);
  } catch (error) {
    console.error('获取文件类型列表失败:', error);
  }
};
</script>

<style scoped>
.course-resources-container {
  padding: 16px;
}

.resource-card {
  margin-top: 16px;
}

.search-form {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.preview-error {
  text-align: center;
  padding: 40px 0;
}

.preview-error .el-icon {
  font-size: 48px;
  color: #E6A23C;
  margin-bottom: 20px;
}

.resource-uploader {
  width: 100%;
}
</style> 