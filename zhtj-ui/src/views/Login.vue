<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { login, getCaptcha, getCaptchaUrl } from '../api/login';
import { useUserStore } from '../stores/user';
import { View, Hide, User, Lock, Message, Position, Key, ArrowRight } from '@element-plus/icons-vue';

// 注意：静态资源直接在模板中通过相对路径引用
// 背景图片在CSS中通过相对路径引用：url('../assets/img/beijing.jpg')

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 表单数据
const loginForm = reactive({
  card: '',
  pwd: '',
  captcha: ''
});

// 密码可见性
const showPassword = ref(false);

// 验证码相关
const captchaImg = ref(''); // 验证码图片Base64
const captchaImgUrl = ref(''); // 验证码图片URL，作为备选方案
const captchaKey = ref(''); // 验证码标识
const loading = ref(false);

// 登录方式
const loginType = ref('normal'); // normal, qrcode

// 刷新验证码
const refreshCaptcha = async () => {
  try {
    loading.value = true;
    captchaImg.value = ''; // 清空旧的验证码
    captchaImgUrl.value = ''; // 清空旧的图片URL

    // 调用API获取验证码
    const response = await getCaptcha({
      codeType: 'login',
      deviceId: generateDeviceId() // 生成设备标识
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
    loading.value = false;
  }
};

// 生成简单的设备标识
const generateDeviceId = () => {
  // 从sessionStorage获取已存在的设备ID，如果没有则创建新的
  let deviceId = sessionStorage.getItem('device_id');
  if (!deviceId) {
    deviceId = 'web_' + Math.random().toString(36).substring(2, 10);
    sessionStorage.setItem('device_id', deviceId);
  }
  return deviceId;
};

// 登录处理
const handleLogin = async () => {
  // 表单验证
  if (loginForm.card === '') {
    ElMessage.warning('请输入身份证号码');
    return;
  }
  if (loginForm.pwd === '') {
    ElMessage.warning('请输入密码');
    return;
  }
  if (loginForm.captcha === '') {
    ElMessage.warning('请输入验证码');
    return;
  }
  
  loading.value = true;
  
  try {
    console.log('开始登录请求...');
    
    // 先清除可能存在的旧用户数据
    localStorage.removeItem('userInfo');
    sessionStorage.removeItem('user_info');
    sessionStorage.removeItem('loginResponse');
    
    const result = await login(
      { card: loginForm.card, pwd: loginForm.pwd },
      loginForm.captcha,
      captchaKey.value
    );
    
    console.log('登录响应:', result);
    
    // 确保result不为undefined或null
    if (!result) {
      ElMessage.error('登录请求无响应');
      refreshCaptcha();
      loading.value = false;
      return;
    }
    
    if (result.code === 200 || result.success) {
      console.log('登录成功，准备设置token');
      
      // 检查用户是否需要激活
      if (result.data && result.data.needActivation) {
        ElMessage.warning('您的账号尚未激活，请联系组织管理员进行激活');
        refreshCaptcha();
        loading.value = false;
        return;
      }
      
      // 确保返回了token
      if (result.data && result.data.token) {
        // 重置用户存储，确保清除旧用户数据
        userStore.clearUserInfo();
        
        // 设置token
        userStore.setToken(result.data.token);
        console.log('Token已保存，loginStatus:', userStore.isLoggedIn);
        
        // 保存登录响应到sessionStorage供后续使用
        sessionStorage.setItem('loginResponse', JSON.stringify(result));
        
        // 保存用户名到localStorage，用于后续API fallback
        if (result.data.user && result.data.user.name) {
          localStorage.setItem('username', result.data.user.name);
        }
        
        try {
            // 如果用户对象中有组织ID，提前获取组织信息
            const orgId = result.data.user.organization;
            if (orgId) {
              try {
                // 动态导入组织API
                const { getOrganizationDetail } = await import('../api/organization');
                console.log('获取组织详情，ID:', orgId);
                
                // 调用API获取组织详情
                const orgResponse = await getOrganizationDetail(orgId);
                
                if (orgResponse.success && orgResponse.data) {
                  console.log('获取到组织信息:', orgResponse.data);
                  
                  // 创建一个新的副本以避免修改原始对象
                  const updatedResult = JSON.parse(JSON.stringify(result));
                  
                  // 将组织信息添加到用户响应
                  updatedResult.data.organizationInfo = {
                    id: orgResponse.data.id || orgId,
                    name: orgResponse.data.name || '',
                    fullName: orgResponse.data.fullName || '',
                    type: orgResponse.data.type || ''
                  };
                
                // 同时添加使用精确字段名的组织信息
                updatedResult.data.org_name = orgResponse.data.name || '';
                updatedResult.data.full_name = orgResponse.data.fullName || '';
                updatedResult.data.org_type = orgResponse.data.type || '';
                  
                  // 更新sessionStorage
                  sessionStorage.setItem('loginResponse', JSON.stringify(updatedResult));
                console.log('已更新登录响应，添加了组织信息:', {
                  org_name: updatedResult.data.org_name,
                  full_name: updatedResult.data.full_name,
                  org_type: updatedResult.data.org_type
                });
                } else {
                  console.error('获取组织详情失败:', orgResponse.message);
                }
              } catch (error) {
                console.error('获取组织信息时出错:', error);
              }
            }
        } catch (error) {
          console.error('处理组织信息时出错:', error);
          }
        
        // 显示成功消息
        ElMessage.success('登录成功');
          
          // 获取用户信息（包含组织信息）
          await userStore.fetchUserInfo();
          console.log('用户信息获取成功，用户名:', userStore.name);
          console.log('组织信息:', {
            name: userStore.organization, 
            fullName: userStore.organizationFullName,
            type: userStore.organizationType
          });
        
        // 5. 检查路由状态并延迟跳转，确保状态更新
        const redirectPath = route.query.redirect as string;
        console.log('登录成功，准备跳转到', redirectPath || '/dashboard');
        
        // 在状态更新后再执行跳转，使用setTimeout确保其他操作完成
        setTimeout(() => {
          // 再次确认token状态
          console.log('跳转前确认 - Token状态:', !!localStorage.getItem('token'));
          console.log('跳转前确认 - Store状态:', userStore.isLoggedIn);
          
          // 执行导航
          if (redirectPath) {
            router.replace(redirectPath);
          } else {
            router.replace('/dashboard');
          }
          
          // 最后再关闭loading
          loading.value = false;
        }, 300); // 添加足够的延迟确保状态更新
      } else {
        ElMessage.error('登录成功但未收到有效的认证令牌');
        refreshCaptcha();
        loading.value = false;
      }
    } else {
      ElMessage.error(result.message || '身份证号码、密码或验证码错误');
      refreshCaptcha();
      loading.value = false;
    }
  } catch (error) {
    console.error('登录过程出现错误', error);
    ElMessage.error('登录过程出现错误，请重试');
    refreshCaptcha();
    loading.value = false;
  }
};

// 切换密码可见性
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value;
};

// 切换登录方式
const toggleLoginType = () => {
  loginType.value = loginType.value === 'normal' ? 'qrcode' : 'normal';
};

// 前往注册页
const goToRegister = () => {
  router.push('/register');
};

// 前往重置密码页
const goToResetPwd = () => {
  router.push('/reset-password');
};

// 前往常见问题页
const goToFAQ = () => {
  router.push('/faq');
};

// 前往电话咨询页
const goToContact = () => {
  router.push('/contact');
};

// 验证码加载错误处理
const handleCaptchaError = () => {
  console.error('验证码图片加载失败');
  ElMessage.error('验证码加载失败，请刷新重试');
  captchaImgUrl.value = '';
};

// 组件挂载时刷新验证码
onMounted(() => {
  refreshCaptcha();
});
</script>

<template>
  <div class="login-container">
    <div class="login-bg-overlay"></div>
    <!-- 头部共青团标志 -->
    <div class="header">
      <div class="logo-wrapper">
        <img class="logo animate__animated animate__fadeIn" src="../assets/img/logo.png" alt="共青团团徽" />
      </div>
      <img class="title-img animate__animated animate__fadeIn animate__delay-1s" src="../assets/img/title.png" alt="网上共青团·智慧团建" />
    </div>
    
    <!-- 登录表单区域 -->
    <transition name="fade-slide" mode="out-in">
      <!-- 账号密码登录 -->
      <div v-if="loginType === 'normal'" key="normal-login" class="form-container animate__animated animate__fadeIn animate__delay-2s">
        <h2 class="login-title">用户登录</h2>
        
        <div class="input-item">
          <el-input 
            v-model="loginForm.card" 
            placeholder="身份证号码" 
            maxlength="18"
            class="custom-input"
          >
            <template #prefix>
              <el-icon class="input-icon"><User /></el-icon>
            </template>
          </el-input>
        </div>
        
        <div class="input-item">
          <el-input 
            v-model="loginForm.pwd" 
            :type="showPassword ? 'text' : 'password'" 
            placeholder="密码" 
            maxlength="18"
            class="custom-input"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <el-icon class="input-icon"><Lock /></el-icon>
            </template>
            <template #suffix>
              <el-icon 
                class="eye-icon" 
                @click="togglePasswordVisibility"
              >
                <component :is="showPassword ? View : Hide" />
              </el-icon>
            </template>
          </el-input>
        </div>
        
        <div class="captcha-container">
          <div class="captcha-label">验证码</div>
          <el-input 
            v-model="loginForm.captcha" 
            class="captcha-input"
            maxlength="4"
            @keyup.enter="handleLogin"
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
            <el-icon class="is-loading"><Position /></el-icon>
          </div>
        </div>
        
        <div class="btn-container">
          <el-button 
            type="primary" 
            class="login-btn" 
            :loading="loading"
            @click="handleLogin"
          >
            <span>登录</span>
            <el-icon class="btn-icon"><ArrowRight /></el-icon>
          </el-button>
        </div>
        
        <!-- 使用Element Plus组件优化底部链接区域 -->
        <div class="bottom-links">
          <el-row justify="space-between" class="link-row">
            <el-col :span="4" class="text-center">
              <el-link :underline="false" @click="router.push('/register')" class="custom-link">
                <el-icon><User /></el-icon>
                <span>注册</span>
              </el-link>
            </el-col>
            <el-col :span="1" class="divider-container">
              <div class="vertical-divider"></div>
            </el-col>
            <el-col :span="6" class="text-center">
              <el-link :underline="false" @click="router.push('/forgot-password')" class="custom-link">
                <el-icon><Key /></el-icon>
                <span>忘记密码</span>
              </el-link>
            </el-col>
            <el-col :span="1" class="divider-container">
              <div class="vertical-divider"></div>
            </el-col>
            <el-col :span="6" class="text-center">
              <el-link :underline="false" @click="goToFAQ" class="custom-link">
                <el-icon><Message /></el-icon>
                <span>常见问题</span>
              </el-link>
            </el-col>
            <el-col :span="1" class="divider-container">
              <div class="vertical-divider"></div>
            </el-col>
            <el-col :span="5" class="text-center">
              <el-link :underline="false" @click="goToContact" class="custom-link">
                <el-icon><Position /></el-icon>
                <span>咨询电话</span>
              </el-link>
            </el-col>
          </el-row>
        </div>
        
        <div class="switch-login-type">
          <el-button link @click="toggleLoginType" class="switch-btn">
            <img src="../assets/img/logo.png" class="qrcode-icon" alt="扫码登录" />
            <span>扫码登录</span>
          </el-button>
        </div>
      </div>
      
      <!-- 扫码登录 -->
      <div v-else key="qrcode-login" class="form-container qrcode-login-container animate__animated animate__fadeIn">
        <h2 class="login-title">扫码登录</h2>
        
        <div class="qrcode-box">
          <img class="qrcode-large" src="../assets/img/xiaochengxu.jpg" alt="扫码登录" />
          <div class="qrcode-border"></div>
          <div class="scan-line"></div>
        </div>
        
        <p class="qrcode-tip">请使用"网上共青团"小程序扫码登录</p>
        
        <div class="switch-login-type">
          <el-button link @click="toggleLoginType" class="switch-btn">
            <el-icon><User /></el-icon>
            <span>账号密码登录</span>
          </el-button>
        </div>
      </div>
    </transition>
    
    <!-- 底部版权信息 -->
    <div class="footer animate__animated animate__fadeInUp animate__delay-3s">
      <p>主办：2025 共青团系统开发测试 京ICP备123456789号-1</p>
    </div>
    
    <!-- 右下角小程序二维码 -->
    <div @click="toggleLoginType" class="qrcode-area animate__animated animate__fadeIn animate__delay-3s">
      <img class="qrcode" src="../assets/img/xiaochengxu.jpg" alt="小程序二维码" />
      <p>扫码登录小程序</p>
      <img class="halo" src="../assets/img/guanghuan.png" alt="光环" />
    </div>
  </div>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

@import 'animate.css';

.login-container {
  height: 100vh;
  width: 100vh;
  background: url('../assets/img/beijing.jpg') no-repeat center;
  background-size: cover;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
  font-family: "Microsoft YaHei", "微软雅黑", sans-serif;
  overflow: hidden;
}

.login-bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, rgba(0,0,0,0.2) 0%, rgba(0,0,0,0.5) 100%);
  z-index: 1;
}

/* 头部样式 */
.header {
  margin-top: 80px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 2;
}

.logo-wrapper {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: radial-gradient(circle at center, rgba(198, 40, 40, 0.8) 0%, rgba(198, 40, 40, 0.4) 70%, transparent 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  animation: pulse 3s infinite;
}

.logo {
  width: 80px;
  height: 80px;
  filter: drop-shadow(0 0 15px rgba(255, 255, 255, 0.7));
}

.title-img {
  height: 40px;
  filter: drop-shadow(0 0 12px rgba(255, 255, 255, 0.7));
}

/* 表单区域 */
.form-container {
  margin-top: 40px;
  width: 460px;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.3);
  padding: 35px;
  border-radius: 12px;
  backdrop-filter: blur(8px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3), 
              0 0 20px rgba(255, 255, 255, 0.1) inset;
  border: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 2;
  overflow: hidden;
}

.form-container::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.1) 0%, transparent 60%);
  transform: rotate(45deg);
  z-index: -1;
}

.login-title {
  font-size: 24px;
  font-weight: 500;
  margin-bottom: 30px;
  text-align: center;
  width: 100%;
  color: #fff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  position: relative;
}

.login-title::after {
  content: "";
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(to right, transparent, rgba(255, 255, 255, 0.7), transparent);
}

.input-item {
  width: 100%;
  margin-bottom: 20px;
}

/* 自定义Element Plus输入框样式 */
.custom-input :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.15) !important;
  border: none !important;
  box-shadow: none !important;
  color: #fff !important;
  border-radius: 6px;
  height: 46px;
  transition: all 0.3s;
}

.custom-input:hover :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.25) !important;
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.15) !important;
}

.custom-input :deep(.el-input__inner) {
  color: #fff !important;
  height: 44px;
  font-size: 15px;
}

.input-icon {
  color: rgba(255, 255, 255, 0.8);
  font-size: 20px;
}

.eye-icon {
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  font-size: 20px;
}

/* 验证码区域 */
.captcha-container {
  width: 100%;
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 6px;
  padding: 0 15px;
  height: 46px;
  transition: all 0.3s;
}

.captcha-container:hover {
  background-color: rgba(255, 255, 255, 0.25);
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.15);
}

.captcha-label {
  color: #fff;
  margin-right: 12px;
  white-space: nowrap;
  font-size: 15px;
}

.captcha-input {
  flex: 1;
}

.captcha-input :deep(.el-input__wrapper) {
  background-color: transparent !important;
  border: none !important;
  box-shadow: none !important;
  padding: 0;
}

.captcha-input :deep(.el-input__inner) {
  color: #fff !important;
  height: 44px;
  padding: 0;
  font-size: 15px;
  letter-spacing: 2px;
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

/* 登录按钮 */
.btn-container {
  width: 100%;
  margin-top: 10px;
  margin-bottom: 25px;
}

.login-btn {
  width: 100%;
  height: 48px;
  background-color: #c62828;
  border-color: #c62828;
  border-radius: 24px;
  font-size: 17px;
  font-weight: bold;
  letter-spacing: 3px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(198, 40, 40, 0.4);
}

.login-btn:hover {
  background-color: #d32f2f;
  border-color: #d32f2f;
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(198, 40, 40, 0.5);
}

.login-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 10px rgba(198, 40, 40, 0.4);
}

.btn-icon {
  margin-left: 8px;
  font-size: 16px;
}

/* 添加新的底部链接样式 */
.bottom-links {
  width: 100%;
  margin: 15px 0;
}

.link-row {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.text-center {
  text-align: center;
}

.custom-link {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.9) !important;
  transition: all 0.3s;
  font-size: 14px;
  padding: 6px 8px;
  border-radius: 4px;
  white-space: nowrap;
}

.custom-link:hover {
  background-color: rgba(255, 255, 255, 0.15);
  color: #fff !important;
  transform: translateY(-2px);
}

.custom-link .el-icon {
  margin-right: 4px;
  font-size: 14px;
}

.divider-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 28px;
}

.vertical-divider {
  width: 2px;
  height: 16px;
  background-color: rgba(255, 255, 255, 0.3);
}

/* 切换登录方式 */
.switch-login-type {
  margin-top: 20px;
  width: 100%;
  display: flex;
  justify-content: center;
}

.switch-btn {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 5px;
  padding: 6px 16px;
  border-radius: 20px;
  background-color: rgba(255, 255, 255, 0.1);
}

.switch-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
  color: #fff;
  transform: translateY(-2px);
}

.qrcode-icon {
  width: 16px;
  height: 16px;
}

/* 底部版权 */
.footer {
  position: absolute;
  bottom: 20px;
  text-align: center;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.8);
  z-index: 2;
}

/* 小程序二维码 */
.qrcode-area {
  position: absolute;
  bottom: 50px;
  right: 50px;
  text-align: center;
  z-index: 2;
}

.qrcode {
  width: 90px;
  height: 90px;
  border-radius: 10px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
  transition: all 0.3s;
}

.qrcode:hover {
  transform: scale(1.08);
  border-color: white;
  box-shadow: 0 12px 25px rgba(0, 0, 0, 0.4), 0 0 15px rgba(255, 255, 255, 0.3);
}

.qrcode-area p {
  margin-top: 10px;
  font-size: 13px;
  color: #fff;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
}

.halo {
  position: absolute;
  bottom: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 110px;
  z-index: -1;
  opacity: 0.7;
  animation: glow 3s infinite alternate;
}

/* 扫码登录样式 */
.qrcode-login-container {
  padding: 35px 30px;
}

.qrcode-box {
  width: 220px;
  height: 220px;
  position: relative;
  margin: 10px 0 30px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.qrcode-large {
  width: 180px;
  height: 180px;
  border-radius: 10px;
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.25);
  position: relative;
  z-index: 2;
  background: white;
}

.qrcode-border {
  position: absolute;
  width: 200px;
  height: 200px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-radius: 12px;
  animation: rotate 20s linear infinite;
}

.scan-line {
  position: absolute;
  width: 180px;
  height: 2px;
  background: linear-gradient(to right, transparent, #c62828, transparent);
  top: 0;
  z-index: 3;
  box-shadow: 0 0 8px rgba(198, 40, 40, 0.7);
  animation: scan 3s ease-in-out infinite;
}

.qrcode-tip {
  color: rgba(255, 255, 255, 0.9);
  font-size: 15px;
  margin: 0 0 20px;
  text-align: center;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

/* 动画效果 */
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(198, 40, 40, 0.7);
  }
  70% {
    box-shadow: 0 0 0 15px rgba(198, 40, 40, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(198, 40, 40, 0);
  }
}

@keyframes glow {
  from {
    opacity: 0.5;
    transform: translateX(-50%) scale(0.95);
  }
  to {
    opacity: 0.8;
    transform: translateX(-50%) scale(1.05);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes scan {
  0% {
    top: 10%;
    opacity: 1;
  }
  50% {
    top: 90%;
    opacity: 0.8;
  }
  100% {
    top: 10%;
    opacity: 1;
  }
}

/* 过渡动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

/* 自定义placeholder颜色 */
:deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.6) !important;
}

/* 响应式样式 */
@media (max-width: 576px) {
  .form-container {
    width: 95%;
    padding: 25px 20px;
  }
  
  .logo-wrapper {
    width: 100px;
    height: 100px;
  }
  
  .logo {
    width: 70px;
    height: 70px;
  }
  
  .title-img {
    height: 34px;
  }
  
  .qrcode-area {
    display: none;
  }
  
  .header {
    margin-top: 60px;
  }
  
  .custom-link {
    font-size: 12px;
    padding: 4px 4px;
  }
  
  .custom-link .el-icon {
    margin-right: 2px;
    font-size: 12px;
  }
}
</style> 