<template>
  <div class="forgot-password-container">
    <div class="forgot-password-box">
      <div class="header">
        <h2>找回密码</h2>
        <p class="desc">请选择验证方式找回密码</p>
      </div>
      
      <!-- 步骤指示器 -->
      <div class="custom-steps-container">
        <el-steps :active="currentStep" finish-status="success" align-center>
          <el-step title="身份验证">
            <template #icon>
              <div class="custom-step-icon" :class="{'step-active': currentStep >= 0, 'step-completed': currentStep > 0}">
                <span v-if="currentStep > 0" class="step-check">✓</span>
                <span v-else>1</span>
              </div>
            </template>
          </el-step>
          <el-step title="重置密码">
            <template #icon>
              <div class="custom-step-icon" :class="{'step-active': currentStep >= 1, 'step-completed': currentStep > 1}">
                <span v-if="currentStep > 1" class="step-check">✓</span>
                <span v-else>2</span>
              </div>
            </template>
          </el-step>
          <el-step title="完成">
            <template #icon>
              <div class="custom-step-icon" :class="{'step-active': currentStep >= 2, 'step-completed': currentStep > 2}">
                <span v-if="currentStep > 2" class="step-check">✓</span>
                <span v-else>3</span>
              </div>
            </template>
          </el-step>
        </el-steps>
        <div class="progress-bar-container">
          <div class="progress-bar" :style="{width: progressWidth}"></div>
        </div>
      </div>
      
      <!-- 步骤1：输入身份证号和选择验证方式 -->
      <div v-if="currentStep === 0" class="step-content">
        <el-form :model="formData" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="身份证号" prop="card">
            <el-input v-model="formData.card" placeholder="请输入您的身份证号" />
          </el-form-item>
          
          <el-form-item label="验证方式" prop="verifyType">
            <el-radio-group v-model="formData.verifyType">
              <el-radio label="email">邮箱验证</el-radio>
              <el-radio label="sms">手机验证</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-button type="primary" @click="submitStep1" :loading="loading" style="width: 100%">下一步</el-button>
        </el-form>
      </div>
      
      <!-- 步骤2：输入验证码和新密码 -->
      <div v-if="currentStep === 1" class="step-content">
        <el-alert
          v-if="contactInfo"
          :title="`验证码已发送至${formData.verifyType === 'email' ? '邮箱' : '手机'}: ${contactInfo}`"
          type="success"
          show-icon
          style="margin-bottom: 15px"
        />
        
        <el-form :model="formData" :rules="rules" ref="formRef" label-position="top">
          <el-form-item label="验证码" prop="code" class="verification-code-item">
            <div class="verification-code-container">
              <el-input v-model="formData.code" placeholder="请输入验证码" class="verification-input" />
              <el-button 
                :disabled="countdown > 0" 
                @click="sendCode" 
                class="verification-button">
                {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input 
              v-model="formData.newPassword" 
              placeholder="请输入新密码" 
              :type="showPassword ? 'text' : 'password'"
              show-password
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input 
              v-model="formData.confirmPassword" 
              placeholder="请再次输入新密码" 
              :type="showPassword ? 'text' : 'password'"
              show-password
            />
          </el-form-item>
          
          <div class="form-actions" style="display: flex; gap: 15px;">
            <el-button @click="currentStep = 0" style="flex: 1;">上一步</el-button>
            <el-button type="primary" @click="submitStep2" :loading="loading" style="flex: 1;">提交</el-button>
          </div>
        </el-form>
      </div>
      
      <!-- 步骤3：完成 -->
      <div v-if="currentStep === 2" class="step-content success-step">
        <el-result
          icon="success"
          title="密码重置成功"
          sub-title="您可以使用新密码登录系统"
        >
          <template #extra>
            <el-button type="primary" @click="goToLogin">返回登录</el-button>
          </template>
        </el-result>
      </div>
      
      <!-- 返回登录链接 -->
      <div class="footer-links" v-if="currentStep !== 2">
        <el-link type="primary" @click="goToLogin">返回登录</el-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { sendResetPasswordCode, verifyResetPasswordCode } from '../api/login';

type FormValidateCallback = (valid: boolean, fields?: unknown) => void;

interface FormInstance {
  validate: (callback: FormValidateCallback) => Promise<void>;
}

interface FormRule {
  required?: boolean;
  message?: string;
  trigger?: string | string[];
  min?: number;
  max?: number;
  pattern?: RegExp;
  validator?: (rule: unknown, value: string, callback: (error?: Error) => void) => void;
}

interface FormRules {
  [key: string]: FormRule[];
}

const router = useRouter();
const formRef = ref<FormInstance>();
const loading = ref(false);
const currentStep = ref(0);
const showPassword = ref(false);
const contactInfo = ref('');
const countdown = ref(0);
let countdownTimer: number | null = null;

// 计算进度条宽度
const progressWidth = computed(() => {
  // 总共3个步骤，每完成一步增加33.3%，最后一步完成时100%
  const stepPercentage = (currentStep.value / 2) * 100;
  return `${Math.min(stepPercentage, 100)}%`;
});

// 表单数据
const formData = reactive({
  card: '',
  verifyType: 'email',
  code: '',
  newPassword: '',
  confirmPassword: ''
});

// 表单验证规则
const rules = reactive<FormRules>({
  card: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  verifyType: [
    { required: true, message: '请选择验证方式', trigger: 'change' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (_: unknown, value: string, callback: (error?: Error) => void) => {
        if (value !== formData.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      }, 
      trigger: 'blur' 
    }
  ]
});

// 提交步骤1表单
const submitStep1 = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid: boolean, fields: unknown) => {
    if (!valid) {
      console.log('表单验证失败', fields);
      return;
    }
    
    loading.value = true;
    
    try {
      const response = await sendResetPasswordCode(formData.card, formData.verifyType as 'email' | 'sms');
      
      if (response && response.success) {
        // 提取联系信息并显示掩码版本
        if (formData.verifyType === 'email' && response.data.maskedEmail) {
          contactInfo.value = response.data.maskedEmail;
        } else if (formData.verifyType === 'sms' && response.data.maskedPhone) {
          contactInfo.value = response.data.maskedPhone;
        }
        
        ElMessage.success(response.message || '验证码已发送');
        currentStep.value = 1;
        
        // 启动倒计时
        startCountdown();
      } else {
        ElMessage.error(response?.message || '发送验证码失败');
      }
    } catch (error) {
      console.error('发送验证码出错', error);
      ElMessage.error('发送验证码出错');
    } finally {
      loading.value = false;
    }
  });
};

// 提交步骤2表单
const submitStep2 = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid: boolean, fields: unknown) => {
    if (!valid) {
      console.log('表单验证失败', fields);
      return;
    }
    
    loading.value = true;
    
    try {
      const response = await verifyResetPasswordCode(
        formData.card,
        formData.code,
        formData.verifyType as 'email' | 'sms',
        formData.newPassword
      );
      
      if (response && response.success) {
        ElMessage.success(response.message || '密码重置成功');
        currentStep.value = 2;
      } else {
        ElMessage.error(response?.message || '验证码验证失败');
      }
    } catch (error) {
      console.error('重置密码出错', error);
      ElMessage.error('重置密码出错');
    } finally {
      loading.value = false;
    }
  });
};

// 发送验证码
const sendCode = async () => {
  if (countdown.value > 0) return;
  
  loading.value = true;
  
  try {
    const response = await sendResetPasswordCode(formData.card, formData.verifyType as 'email' | 'sms');
    
    if (response && response.success) {
      // 更新联系信息
      if (formData.verifyType === 'email' && response.data.maskedEmail) {
        contactInfo.value = response.data.maskedEmail;
      } else if (formData.verifyType === 'sms' && response.data.maskedPhone) {
        contactInfo.value = response.data.maskedPhone;
      }
      
      ElMessage.success(response.message || '验证码已重新发送');
      
      // 重新启动倒计时
      startCountdown();
    } else {
      ElMessage.error(response?.message || '发送验证码失败');
    }
  } catch (error) {
    console.error('发送验证码出错', error);
    ElMessage.error('发送验证码出错');
  } finally {
    loading.value = false;
  }
};

// 启动倒计时
const startCountdown = () => {
  countdown.value = 60;
  
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
  
  countdownTimer = window.setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--;
    } else {
      if (countdownTimer) {
        clearInterval(countdownTimer);
        countdownTimer = null;
      }
    }
  }, 1000);
};

// 返回登录页
const goToLogin = () => {
  router.push('/login');
};

// 组件卸载前清除定时器
onBeforeUnmount(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});
</script>

<style scoped>
.forgot-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.forgot-password-box {
  width: 450px;
  padding: 30px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header {
  text-align: center;
  margin-bottom: 25px;
}

.header h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.header .desc {
  font-size: 14px;
  color: #606266;
}

.step-content {
  margin-top: 25px;
  margin-bottom: 25px;
}

.footer-links {
  text-align: center;
  margin-top: 15px;
}

.success-step {
  text-align: center;
  padding: 20px 0;
}

.custom-steps-container {
  margin: 30px 0;
  position: relative;
}

.progress-bar-container {
  height: 6px;
  background-color: #f0f0f0;
  border-radius: 5px;
  overflow: hidden;
  margin: 20px 40px 0;
  box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
  position: relative;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #67c23a);
  border-radius: 5px;
  transition: width 0.8s cubic-bezier(0.25, 0.1, 0.25, 1);
  box-shadow: 0 0 10px rgba(64,158,255,0.5);
  position: relative;
  overflow: hidden;
}

.progress-bar::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    rgba(255,255,255,0) 0%,
    rgba(255,255,255,0.4) 50%,
    rgba(255,255,255,0) 100%
  );
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.custom-step-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #dcdfe6;
  transition: all 0.3s;
  font-weight: bold;
  margin: 0 auto;
  position: relative;
  z-index: 2;
}

.step-active {
  background: linear-gradient(135deg, #409eff, #36d1dc);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 5px 15px rgba(64,158,255,0.4);
  transform: scale(1.1);
}

.step-completed {
  background: linear-gradient(135deg, #67c23a, #52c41a);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 5px 15px rgba(103,194,58,0.4);
}

.step-check {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 16px;
  font-weight: bold;
}

:deep(.el-step__title) {
  font-weight: 500;
  margin-top: 8px;
  transition: color 0.3s;
}

:deep(.el-step__title.is-process) {
  color: #409eff;
  font-weight: 600;
}

:deep(.el-step__line) {
  display: none !important;
}

:deep(.el-step__head) {
  padding: 0 !important;
}

:deep(.el-step) {
  flex-basis: auto !important;
  margin-right: 0 !important;
  padding-right: 0 !important;
}

:deep(.el-steps) {
  display: flex;
  justify-content: space-between;
  padding: 0 40px;
}

.verification-code-item {
  margin-bottom: 20px;
}

.verification-code-container {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.verification-input {
  flex: 1;
}

.verification-input :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.verification-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.verification-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #409eff inset;
}

.verification-button {
  min-width: 120px;
  height: 38px;
  font-size: 14px;
  border-radius: 4px;
  padding: 0 15px;
  transition: all 0.3s;
}

.verification-button:not(:disabled):hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
  color: #fff;
}

.verification-button:disabled {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #c0c4cc;
  cursor: not-allowed;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
}
</style> 