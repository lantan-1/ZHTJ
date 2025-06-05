<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue';
import { useUserStore } from '../stores/user';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUserSettings, updateUserProfile, updatePassword } from '../api/user';
import { getCaptcha } from '../api/login';
import { saveUserFaceImage } from '../api/face';
import { Loading } from '@element-plus/icons-vue';
import { useRoute } from 'vue-router';

// 正确导入类型
interface FormInstance {
  validate: (callback: (valid: boolean) => void) => Promise<void>;
  resetFields: () => void;
}

interface FormRules {
  [key: string]: Array<{
    required?: boolean;
    message?: string;
    trigger?: string;
    min?: number;
    type?: string;
    pattern?: RegExp;
    validator?: (rule: any, value: any, callback: any) => void;
  }>;
}

const userStore = useUserStore();
const formRef = ref<FormInstance | null>(null);
const passwordFormRef = ref<FormInstance | null>(null);

// 用户信息表单
const userForm = reactive({
  name: '',
  phone: '',
  email: '',
  avatar: '',
  educationLevel: '',
  educationStatus: '',
  politicalStatus: '',
  occupation: '',
  gender: '',
  joinLeagueDate: '',
  joinPartyDate: '',
  workUnit: '',
  address: '',
  qq: '',
  wechat: '',
  weibo: ''
});

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  captcha: ''
});

// 验证码相关
const captchaImg = ref('');
const captchaImgUrl = ref('');
const captchaKey = ref('');
const captchaLoading = ref(false);

// 人脸信息相关
const videoElement = ref<HTMLVideoElement | null>(null);
const canvasElement = ref<HTMLCanvasElement | null>(null);
const stream = ref<MediaStream | null>(null);
const capturedImage = ref<string | null>(null);
const cameraReady = ref(false);
const capturing = ref(false);
const savingFace = ref(false);

// 激活的tab名称，默认为个人信息
const activeTab = ref('个人信息');
const route = useRoute();

// 获取userStore中的hasFaceInfo状态
const hasFaceInfo = computed(() => userStore.hasFaceInfo);

// 表单校验规则
const userRules = reactive<FormRules>({
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  educationLevel: [
    { required: true, message: '请选择学历', trigger: 'change' }
  ],
  educationStatus: [
    { required: true, message: '请选择在读状况', trigger: 'change' }
  ],
  politicalStatus: [
    { required: true, message: '请选择政治面貌', trigger: 'change' }
  ],
  occupation: [
    { required: true, message: '请输入职业', trigger: 'blur' }
  ],
  joinLeagueDate: [
    { required: true, message: '请选择入团时间', trigger: 'change' }
  ],
  joinPartyDate: [
    { required: true, message: '请选择入党时间', trigger: 'change' }
  ],
  workUnit: [
    { required: true, message: '请输入工作单位', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入户籍地址', trigger: 'blur' }
  ],
  qq: [
    { required: true, message: '请输入QQ号码', trigger: 'blur' }
  ],
  wechat: [
    { required: true, message: '请输入微信号', trigger: 'blur' }
  ],
  weibo: [
    { required: true, message: '请输入微博账号', trigger: 'blur' }
  ]
});

const passwordRules = reactive<FormRules>({
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
});

// 加载用户数据
onMounted(async () => {
  try {
    // 直接通过API获取用户信息
    console.log('开始从API获取用户信息...');
    const response = await getUserSettings();
    
    if (!response || !response.data) {
      ElMessage.error('获取用户信息失败');
      return;
    }
    
    console.log('API返回的用户数据:', response);
    
    // 提取用户数据对象
    const responseData = response.data;
    
    // 使用any类型避免TypeScript类型错误
    let userData: any = {};
    
    // 如果用户数据在user字段中
    if (responseData.user) {
      userData = responseData.user;
      console.log('从user字段提取用户数据');
    } 
    // 如果用户数据在顶层
    else if (responseData.id) {
      userData = responseData;
      console.log('从顶层提取用户数据');
    }
    
    console.log('处理后的用户数据:', userData);
    
    // 支持后端返回的snake_case字段名
    const snakeToCamel = (str: string) => str.replace(/_([a-z])/g, (_, letter) => letter.toUpperCase());
    
    // 填充表单 - 确保所有字段正确映射
    userForm.phone = userData.phone || '';
    userForm.email = userData.email || '';
    // 优先使用camelCase字段，如果没有则尝试转换snake_case字段
    userForm.educationLevel = userData.educationLevel || userData.education_level || '';
    userForm.educationStatus = userData.educationStatus || userData.education_status || '';
    userForm.politicalStatus = userData.politicalStatus || userData.political_status || '';
    userForm.occupation = userData.occupation || '';
    userForm.joinLeagueDate = userData.joinLeagueDate || userData.join_league_date || '';
    userForm.joinPartyDate = userData.joinPartyDate || userData.join_party_date || '';
    userForm.workUnit = userData.workUnit || userData.work_unit || '';
    userForm.address = userData.address || '';
    userForm.qq = userData.qq || '';
    userForm.wechat = userData.wechat || '';
    userForm.weibo = userData.weibo || '';
    
    // 表单填充完毕后输出一次，检查是否正确赋值
    console.log('表单填充数据:', userForm);
  } catch (error) {
    ElMessage.error('获取用户信息失败');
    console.error('获取用户信息失败:', error);
  }
  
  // 加载验证码
  refreshCaptcha();

  // 检查路由query参数，如果存在tab=face，则激活人脸信息tab
  if (route.query.tab === 'face') {
    activeTab.value = '人脸信息';
  }
});

onBeforeUnmount(() => {
  // 在组件卸载前停止摄像头
  stopCamera();
});

// 提交用户信息表单
const submitForm = async (formEl: FormInstance | null) => {
  if (!formEl) return;
  
  await formEl.validate(async (valid: boolean) => {
    if (valid) {
      try {
        // 创建表单数据对象，确保使用驼峰命名
        const formData = {
          phone: userForm.phone,
          email: userForm.email,
          educationLevel: userForm.educationLevel,
          educationStatus: userForm.educationStatus,
          politicalStatus: userForm.politicalStatus,
          occupation: userForm.occupation,
          joinLeagueDate: userForm.joinLeagueDate,
          joinPartyDate: userForm.joinPartyDate,
          workUnit: userForm.workUnit,
          address: userForm.address,
          qq: userForm.qq,
          wechat: userForm.wechat,
          weibo: userForm.weibo
        };
        
        console.log('正在提交用户表单数据:', formData);
        const response = await updateUserProfile(formData);
        
        if (response.code === 200) {
          ElMessage.success('个人信息更新成功');
          // 不再通过store更新用户数据
          // 为确保用户导航栏显示正确，仍然通知userStore
          if (userStore && userStore.updateLocalUserInfo) {
            userStore.updateLocalUserInfo(formData);
          }
        } else {
          ElMessage.error(response.message || '更新失败');
        }
      } catch (error) {
        ElMessage.error('更新个人信息失败');
        console.error('更新个人信息失败:', error);
      }
    }
  });
};

// 刷新验证码
const refreshCaptcha = async () => {
  try {
    captchaLoading.value = true;
    captchaImg.value = '';
    captchaImgUrl.value = '';
    
    // 生成设备标识
    const deviceId = 'web_' + Math.random().toString(36).substring(2, 10);
    
    // 调用API获取验证码
    const response = await getCaptcha({
      codeType: 'resetPassword',
      deviceId: deviceId
    });
    
    if (response && response.success) {
      // 如果有base64图片数据，优先使用
      if (response.data.captchaImg) {
        captchaImg.value = response.data.captchaImg;
      } 
      // 如果有图片URL，则作为备选方案
      else if (response.data.imageUrl) {
        captchaImgUrl.value = response.data.imageUrl;
      }
      
      captchaKey.value = response.data.key;
    } else {
      ElMessage.warning('获取验证码失败，请重试');
    }
  } catch (error) {
    console.error('获取验证码出错', error);
    ElMessage.error('获取验证码出错');
  } finally {
    captchaLoading.value = false;
  }
};

// 验证码加载错误处理
const handleCaptchaError = () => {
  console.error('验证码图片加载失败');
  ElMessage.error('验证码加载失败，请刷新重试');
  captchaImgUrl.value = '';
};

// 提交密码表单
const submitPasswordForm = async (formEl: FormInstance | null) => {
  if (!formEl) return;
  
  await formEl.validate(async (valid: boolean) => {
    if (valid) {
      // 验证码为空时提示
      if (!passwordForm.captcha) {
        ElMessage.warning('请输入验证码');
        return;
      }
      
      try {
        const response = await updatePassword({
          old_password: passwordForm.oldPassword,
          new_password: passwordForm.newPassword,
          captcha: passwordForm.captcha,
          captchaKey: captchaKey.value
        });
        
        if (response.code === 200) {
          ElMessage.success('密码修改成功，请重新登录');
          // 立即清除所有用户信息并退出
          userStore.doLogout();
          // 立即跳转到登录页面
          window.location.href = '/login';
        } else {
          ElMessage.error(response.message || '密码修改失败');
          // 刷新验证码
          refreshCaptcha();
        }
      } catch (error) {
        ElMessage.error('修改密码失败');
        console.error('修改密码失败:', error);
        // 刷新验证码
        refreshCaptcha();
      }
    }
  });
};

// --- 人脸信息相关方法 --- 

// 开启摄像头
const openCamera = async () => {
  // 先停止可能正在运行的摄像头
  stopCamera();

  try {
    const mediaStream = await navigator.mediaDevices.getUserMedia({ video: true });
    stream.value = mediaStream; // 将获取到的MediaStream赋值给stream ref
    videoElement.value!.srcObject = mediaStream;
    videoElement.value!.play();
    cameraReady.value = true;
    capturedImage.value = null; // 清除之前的照片
  } catch (error) {
    console.error('访问摄像头失败:', error);
    ElMessage.error('无法访问摄像头，请检查权限设置');
    cameraReady.value = false;
  }
};

// 停止摄像头
const stopCamera = () => {
  if (stream.value) {
    stream.value.getTracks().forEach(track => track.stop());
    stream.value = null;
    cameraReady.value = false;
    // 停止后将 capturedImage 清空，恢复到未拍照状态
    capturedImage.value = null;
    videoElement.value!.srcObject = null;
    console.log('摄像头已停止');
  }
};

// 拍照
const capturePhoto = () => {
  if (!cameraReady.value || !videoElement.value || !canvasElement.value) {
    ElMessage.warning('摄像头未准备好或元素未加载');
    return;
  }

  capturing.value = true;
  const context = canvasElement.value.getContext('2d');
  if (!context) {
    ElMessage.error('无法获取canvas context');
    capturing.value = false;
    return;
  }

  // 设置canvas尺寸与视频尺寸一致，确保捕捉完整帧
  canvasElement.value.width = videoElement.value.videoWidth;
  canvasElement.value.height = videoElement.value.videoHeight;

  // 在canvas上绘制视频帧
  context.drawImage(videoElement.value, 0, 0, canvasElement.value.width, canvasElement.value.height);

  // 从canvas获取图片数据 (这里获取的是base64格式)
  capturedImage.value = canvasElement.value.toDataURL('image/png');
  capturing.value = false;

  // 拍照后停止摄像头预览，但不完全关闭，以便后续保存或重置
  if (videoElement.value) {
    videoElement.value.pause();
    // videoElement.value.srcObject = null; // 不需要清空srcObject，v-show会控制显示
  }
};

// 重置照片，回到摄像头预览状态
const resetPhoto = () => {
  capturedImage.value = null; // 清空照片
  if (videoElement.value) {
     videoElement.value.play(); // 继续播放视频流
  }
};

// 保存人脸信息
const saveFaceInfo = async () => {
  if (!capturedImage.value) {
    ElMessage.warning('请先拍照');
    return;
  }

  savingFace.value = true;
  try {
    // 将base64图片数据转换为File对象
    const byteString = atob(capturedImage.value.split(',')[1]);
    const mimeString = capturedImage.value.split(',')[0].split(':')[1].split(';')[0];
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for (let i = 0; i < byteString.length; i++) {
      ia[i] = byteString.charCodeAt(i);
    }
    const blob = new Blob([ab], { type: mimeString });
    const file = new File([blob], 'face_image.png', { type: mimeString });

    // 调用API保存人脸信息
    const response = await saveUserFaceImage(file);

    if (response.code === 200) {
      ElMessage.success('人脸信息保存成功');
      // 保存成功后，更新用户store中的hasFaceInfo状态
      userStore.hasFaceInfo = true; 
      // 可选：停止摄像头并清空照片
      // stopCamera();
      // capturedImage.value = null;
    } else {
      ElMessage.error(response.message || '人脸信息保存失败');
    }
  } catch (error) {
    ElMessage.error('保存人脸信息失败');
    console.error('保存人脸信息失败:', error);
  } finally {
    savingFace.value = false;
  }
};
</script>

<template>
  <div class="settings-container">
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <h3>系统设置</h3>
        </div>
      </template>
      
      <el-tabs v-model="activeTab">
        <!-- 个人信息设置 -->
        <el-tab-pane label="个人信息" name="个人信息">
          <el-form 
            ref="formRef"
            :model="userForm"
            :rules="userRules"
            label-width="100px"
            class="user-form"
          >
            <el-form-item label="手&nbsp;&nbsp;机&nbsp;&nbsp;号" prop="phone">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            
            <el-form-item label="邮&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;箱" prop="email">
              <el-input v-model="userForm.email" />
            </el-form-item>
            
            <el-form-item label="学&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;历" prop="educationLevel">
              <el-select v-model="userForm.educationLevel" placeholder="请选择学历">
                <el-option label="高中" value="高中" />
                <el-option label="专科" value="专科" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="在读状况" prop="educationStatus">
              <el-select v-model="userForm.educationStatus" placeholder="请选择在读状况">
                <el-option label="在读" value="在读" />
                <el-option label="毕业" value="毕业" />
                <el-option label="休学" value="休学" />
                <el-option label="退学" value="退学" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="政治面貌" prop="politicalStatus">
              <el-select v-model="userForm.politicalStatus" placeholder="请选择政治面貌">
                <el-option label="共青团员" value="共青团员" />
                <el-option label="中共党员" value="中共党员" />
                <el-option label="中共预备党员" value="中共预备党员" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="职&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;业" prop="occupation">
              <el-input v-model="userForm.occupation" />
            </el-form-item>
            
            <el-form-item label="入团时间" prop="joinLeagueDate">
              <el-date-picker
                v-model="userForm.joinLeagueDate"
                type="date"
                placeholder="选择入团日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            
            <el-form-item label="入党时间" prop="joinPartyDate" v-if="userForm.politicalStatus !== '共青团员'">
              <el-date-picker
                v-model="userForm.joinPartyDate"
                type="date"
                placeholder="选择入党日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            
            <el-form-item label="工作单位" prop="workUnit">
              <el-input v-model="userForm.workUnit" />
            </el-form-item>
            
            <el-form-item label="户籍地址" prop="address">
              <el-input v-model="userForm.address" />
            </el-form-item>
            
            <el-form-item label="QQ&nbsp;号码" prop="qq">
              <el-input v-model="userForm.qq" />
            </el-form-item>
            
            <el-form-item label="微&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;信" prop="wechat">
              <el-input v-model="userForm.wechat" />
            </el-form-item>
            
            <el-form-item label="微&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;博" prop="weibo">
              <el-input v-model="userForm.weibo" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitForm(formRef)">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 密码设置 -->
        <el-tab-pane label="修改密码" name="修改密码">
          <el-form 
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            class="password-form"
          >
            <el-form-item label="原&nbsp;&nbsp;密&nbsp;&nbsp;码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password />
            </el-form-item>
            
            <el-form-item label="新&nbsp;&nbsp;密&nbsp;&nbsp;码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" show-password />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
            </el-form-item>
            
            <el-form-item label="验证码" prop="captcha">
              <div class="captcha-container">
                <el-input 
                  v-model="passwordForm.captcha" 
                  placeholder="验证码" 
                  maxlength="4"
                  class="captcha-input"
                />
                <div 
                  v-if="captchaImg || captchaImgUrl" 
                  class="captcha-img" 
                  title="点击刷新验证码" 
                  @click="refreshCaptcha"
                >
                  <!-- 使用Base64编码的图片数据 -->
                  <img v-if="captchaImg" :src="'data:image/png;base64,' + captchaImg" alt="验证码" />
                  <!-- 使用图片URL方式 -->
                  <img 
                    v-else-if="captchaImgUrl" 
                    :src="captchaImgUrl" 
                    alt="验证码" 
                    @error="handleCaptchaError"
                  />
                </div>
                <div v-else class="captcha-loading" @click="refreshCaptcha">
                  <el-icon class="is-loading"><Loading /></el-icon>
                </div>
              </div>
            </el-form-item>
            
            <el-form-item class="button-center">
              <el-button type="primary" @click="submitPasswordForm(passwordFormRef)">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 人脸信息设置 -->
        <el-tab-pane label="人脸信息" name="人脸信息">
          <div class="face-recognition-container">

            <!-- 人脸信息录入状态展示 -->
            <div class="face-status-display">
              <p v-if="hasFaceInfo === true" class="status-success">您已录入人脸信息。</p>
              <p v-else-if="hasFaceInfo === false" class="status-warning">您还没有录入人脸信息。</p>
              <p v-else class="status-checking">正在检查人脸信息状态...</p>
            </div>
            
            <div class="video-canvas-container">
              <!-- 根据是否有 capturedImage 来切换显示视频流或 capturedImage -->
              <video v-show="cameraReady && !capturedImage" ref="videoElement" width="400" height="300" autoplay></video>
              <canvas ref="canvasElement" width="400" height="300" style="display: none;"></canvas>
              <img v-if="capturedImage" :src="capturedImage" alt="Captured Face" class="captured-image" />
            </div>
            
            <p>请确保光线充足，正对摄像头，面部无遮挡。</p>            

            <div class="action-buttons">
              <el-button @click="openCamera" :disabled="cameraReady">{{ cameraReady ? '摄像头已开启' : '开启摄像头' }}</el-button>
              <el-button @click="stopCamera" :disabled="!cameraReady">{{ cameraReady ? '关闭摄像头' : '摄像头已关闭' }}</el-button>
              
              <!-- 拍照/重置按钮 -->
              <el-button 
                @click="capturedImage ? resetPhoto() : capturePhoto()" 
                :disabled="!cameraReady && !capturedImage || capturing || savingFace" 
                :loading="capturing"
              >
                {{ capturedImage ? '重置' : '拍照' }}
              </el-button>

              <el-button type="primary" @click="saveFaceInfo" :disabled="!capturedImage || savingFace" :loading="savingFace">保存人脸信息</el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped>
.settings-container {
  padding: 20px;
}

.settings-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-form, .password-form {
  max-width: 500px;
  margin-top: 20px;
}

/* 添加以下样式来统一表单项宽度 */
:deep(.el-input),
:deep(.el-select),
:deep(.el-date-editor) {
  width: 100%;
}

:deep(.el-form-item__content) {
  width: 100%;
}

/* 按钮水平居中样式 */
:deep(.el-form-item:last-child) {
  margin-top: 30px;
  text-align: center;
}

:deep(.el-form-item:last-child .el-form-item__content) {
  justify-content: center;
  margin-left: 0 !important;
}

/* 验证码样式 */
.captcha-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-input {
  flex: 1;
}

.captcha-img {
  width: 100px;
  height: 38px;
  cursor: pointer;
  overflow: hidden;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.captcha-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.captcha-loading {
  width: 100px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f5f7fa;
}

/* 人脸信息相关样式 */
.face-recognition-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.video-canvas-container {
  position: relative;
  width: 400px; /* 与video/canvas宽度一致 */
  height: 300px; /* 与video/canvas高度一致 */
  border: 1px solid #dcdfe6;
  background-color: #000;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden; /* 确保图片不会超出容器 */
}

/* 让视频和照片都尝试填满容器 */
.video-canvas-container video,
.video-canvas-container .captured-image {
  display: block; /* 避免底部空白 */
  width: 100%; /* 填满容器宽度 */
  height: 100%; /* 填满容器高度 */
  object-fit: contain; /* 保持图片/视频比例并适应容器 */
}

/* 隐藏canvas */
.video-canvas-container canvas {
  display: none; 
}

.captured-image {
  border: 1px solid #dcdfe6; /* 给照片添加边框 */
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.face-status-display {
  text-align: center;
  min-height: 30px;
  width: 100%;
}

.face-status-display p {
  margin: 5px 0;
  padding: 8px;
  border-radius: 4px;
}

.status-success {
  color: #67c23a; /* 绿色 */
  background-color: #e1f3d8;
  border: 1px solid #c2e7b0;
}

.status-warning {
  color: #e6a23c; /* 黄色 */
  background-color: #fdf6ec;
  border: 1px solid #faecd8;
}

.status-checking {
  color: #909399; /* 灰色 */
}
</style> 