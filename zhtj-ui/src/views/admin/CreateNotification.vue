<template>
  <div class="create-notification-container">
    <div class="page-header">
      <h1>创建系统通知</h1>
      <p>创建新的系统通知、公告或消息</p>
    </div>

    <el-card class="form-card">
      <el-form 
        :model="notificationForm" 
        :rules="rules" 
        ref="formRef" 
        label-width="100px"
        class="notification-form"
      >
        <el-form-item label="通知标题" prop="title">
          <el-input 
            v-model="notificationForm.title" 
            placeholder="请输入通知标题" 
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item label="通知类型" prop="type">
          <el-select 
            v-model="notificationForm.type" 
            placeholder="请选择通知类型" 
            style="width: 100%"
          >
            <el-option 
              v-for="item in notificationTypeOptions" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="通知内容" prop="content">
          <el-input 
            type="textarea" 
            v-model="notificationForm.content" 
            placeholder="请输入通知内容"
            :rows="6"
          ></el-input>
        </el-form-item>

        <el-form-item label="富文本内容" prop="richContent">
          <!-- 此处可集成富文本编辑器，如TinyMCE、CKEditor等 -->
          <div class="rich-editor-placeholder">
            <p>富文本编辑器将在此集成</p>
            <p>实际应用中可使用TinyMCE、CKEditor等</p>
          </div>
        </el-form-item>

        <el-form-item label="附件">
          <el-upload
            action="#"
            :http-request="uploadFile"
            :file-list="fileList"
            :on-remove="handleRemove"
            :before-upload="beforeUpload"
            multiple
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                文件大小不超过10MB，支持常见文档格式
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="发送对象" prop="recipients">
          <el-select
            v-model="notificationForm.recipients"
            multiple
            collapse-tags
            placeholder="请选择发送对象"
            style="width: 100%"
          >
            <el-option-group
              v-for="group in recipientOptions"
              :key="group.label"
              :label="group.label"
            >
              <el-option
                v-for="item in group.options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              ></el-option>
            </el-option-group>
          </el-select>
        </el-form-item>

        <el-form-item label="优先级" prop="priority">
          <el-radio-group v-model="notificationForm.priority">
            <el-radio label="low">低</el-radio>
            <el-radio label="normal">普通</el-radio>
            <el-radio label="high">高</el-radio>
            <el-radio label="urgent">紧急</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="过期时间" prop="expireDate">
          <el-date-picker
            v-model="notificationForm.expireDate"
            type="datetime"
            placeholder="选择过期时间（可选）"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          ></el-date-picker>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm('publish')">发布通知</el-button>
          <el-button @click="submitForm('draft')">保存为草稿</el-button>
          <el-button @click="preview">预览</el-button>
          <el-button @click="cancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog
      title="通知预览"
      v-model="previewDialogVisible"
      width="70%"
    >
      <div class="notification-preview">
        <h2 class="preview-title">{{ notificationForm.title || '(无标题)' }}</h2>
        <div class="preview-meta">
          <span>类型：{{ getTypeLabel(notificationForm.type) }}</span>
          <span>发布人：{{ currentUser }}</span>
          <span>发布时间：{{ getCurrentTime() }}</span>
          <span>优先级：{{ getPriorityLabel(notificationForm.priority) }}</span>
        </div>
        <div class="preview-content">
          <p v-if="notificationForm.content" v-html="formatContent(notificationForm.content)"></p>
          <p v-else class="empty-content">(无内容)</p>
        </div>
        <div class="preview-attachments" v-if="fileList.length > 0">
          <h3>附件列表：</h3>
          <ul>
            <li v-for="(file, index) in fileList" :key="index">
              {{ file.name }} ({{ formatFileSize(file.size) }})
            </li>
          </ul>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="previewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const formRef = ref(null);
const currentUser = '系统管理员'; // 实际应从用户状态获取
const previewDialogVisible = ref(false);
const fileList = ref([]);

// 通知表单数据
const notificationForm = reactive({
  title: '',
  type: '',
  content: '',
  richContent: '',
  recipients: [],
  priority: 'normal',
  expireDate: '',
  attachments: []
});

// 通知类型选项
const notificationTypeOptions = [
  { value: 'system', label: '系统通知' },
  { value: 'activity', label: '活动公告' },
  { value: 'important', label: '重要通知' },
  { value: 'normal', label: '普通消息' }
];

// 发送对象选项
const recipientOptions = [
  {
    label: '系统角色',
    options: [
      { value: 'all', label: '所有用户' },
      { value: 'admin', label: '管理员' },
      { value: 'staff', label: '教师/辅导员' },
      { value: 'student', label: '学生' }
    ]
  },
  {
    label: '组织架构',
    options: [
      { value: 'org_1', label: '信息工程学院' },
      { value: 'org_2', label: '计算机科学学院' },
      { value: 'org_3', label: '机械工程学院' }
    ]
  },
  {
    label: '团组织',
    options: [
      { value: 'team_1', label: '校团委' },
      { value: 'team_2', label: '信息工程学院团委' },
      { value: 'team_3', label: '计算机科学学院团委' }
    ]
  }
];

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2到100个字符之间', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择通知类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' }
  ],
  recipients: [
    { required: true, message: '请选择至少一个发送对象', trigger: 'change' }
  ]
};

// 页面初始化
onMounted(() => {
  // 可以在这里获取表单初始数据，例如从草稿加载
});

// 获取当前时间
const getCurrentTime = () => {
  return new Date().toLocaleString();
};

// 获取类型标签
const getTypeLabel = (type) => {
  const found = notificationTypeOptions.find(item => item.value === type);
  return found ? found.label : type;
};

// 获取优先级标签
const getPriorityLabel = (priority) => {
  const priorityMap = {
    'low': '低',
    'normal': '普通',
    'high': '高',
    'urgent': '紧急'
  };
  return priorityMap[priority] || priority;
};

// 格式化内容（简单的换行处理）
const formatContent = (content) => {
  return content.replace(/\n/g, '<br>');
};

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + 'B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(1) + 'KB';
  } else {
    return (size / 1024 / 1024).toFixed(1) + 'MB';
  }
};

// 上传文件
const uploadFile = (options) => {
  // 实际情况应调用API上传文件
  // 这里仅做模拟
  const file = options.file;
  fileList.value.push({
    name: file.name,
    size: file.size,
    url: URL.createObjectURL(file)
  });
  ElMessage.success(`文件 ${file.name} 上传成功`);
};

// 移除文件
const handleRemove = (file, fileList) => {
  ElMessage.info(`文件 ${file.name} 已移除`);
};

// 上传前验证
const beforeUpload = (file) => {
  const maxSize = 10 * 1024 * 1024; // 10MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过10MB!');
    return false;
  }
  return true;
};

// 提交表单
const submitForm = async (action) => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    // 确认发布或保存
    const actionText = action === 'publish' ? '发布' : '保存为草稿';
    const confirmMessage = `确定要${actionText}该通知吗？`;
    
    ElMessageBox.confirm(confirmMessage, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }).then(() => {
      // 模拟API调用
      setTimeout(() => {
        ElMessage.success(`通知已${actionText}`);
        // 跳转回通知列表
        router.push('/admin/notifications');
      }, 1000);
    }).catch(() => {
      // 取消操作
    });
  } catch (error) {
    ElMessage.error('表单验证失败，请检查填写内容');
  }
};

// 预览通知
const preview = () => {
  if (!notificationForm.title && !notificationForm.content) {
    ElMessage.warning('请至少填写标题或内容再预览');
    return;
  }
  previewDialogVisible.value = true;
};

// 取消创建
const cancel = () => {
  ElMessageBox.confirm('是否放弃当前编辑的内容？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push('/admin/notifications');
  }).catch(() => {
    // 取消操作
  });
};
</script>

<style scoped>
.create-notification-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 14px;
}

.form-card {
  margin-bottom: 20px;
}

.notification-form {
  max-width: 900px;
  margin: 0 auto;
}

.rich-editor-placeholder {
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  padding: 20px;
  min-height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
}

.notification-preview {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  background-color: #fff;
}

.preview-title {
  font-size: 22px;
  margin-bottom: 16px;
  text-align: center;
}

.preview-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #dcdfe6;
}

.preview-content {
  padding: 10px 0;
  min-height: 100px;
  line-height: 1.6;
}

.empty-content {
  color: #999;
  font-style: italic;
}

.preview-attachments {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px dashed #dcdfe6;
}

.preview-attachments h3 {
  font-size: 16px;
  margin-bottom: 10px;
}

.preview-attachments ul {
  padding-left: 20px;
}

.preview-attachments li {
  line-height: 1.6;
}
</style> 