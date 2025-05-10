import api from './index';
import axios from 'axios';

interface LoginParams {
  card: string;
  pwd: string;
}

interface CaptchaParams {
  codeType: string;
  deviceId?: string; 
}

// 获取验证码 - 直接使用POST请求
export async function getCaptcha(params: CaptchaParams): Promise<any> {
  try {
    // 尝试使用不会触发CORS预检的简化请求头
    const response = await api.post('/api/captcha', params, {
      headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
        // 移除触发CORS预检的Cache-Control和Pragma头
      }
    });
    
    // 兼容处理响应数据结构
    if (response && response.data) {
      return {
        success: true,
        data: {
          captchaImg: response.data.captchaImg || response.data,
          key: response.data.key || params.deviceId || 'default-key'
        }
      };
    } else {
      return {
        success: false,
        message: '获取验证码失败'
      };
    }
  } catch (error: unknown) {
    console.error('获取验证码失败，尝试使用图片URL方式:', error);
    
    // 如果AJAX方式失败，尝试返回验证码URL，让前端直接加载图片
    try {
      const imageUrl = getCaptchaUrl(params);
      console.log('使用图片URL方式:', imageUrl);
      
      return {
        success: true,
        data: {
          captchaImg: '', // 不使用Base64图片
          key: params.deviceId || 'default',
          imageUrl: imageUrl // 添加图片URL属性
        }
      };
    } catch (e) {
      const errorMessage = error instanceof Error ? error.message : '获取验证码失败，请重试';
      return {
        success: false,
        message: errorMessage
      };
    }
  }
}

// 获取验证码URL - 修改为使用数据URL方式的静态实现
export function getCaptchaUrl(params?: CaptchaParams): string {
  // 生成随机参数确保每次请求不被缓存
  const timestamp = new Date().getTime();
  const codeType = params?.codeType || 'login';
  const deviceId = params?.deviceId || 'default';
  
  // 使用基础URL和相对路径组合
  const baseUrl = import.meta.env.VITE_API_BASE_URL || '';
  return `${baseUrl}/api/captcha/image?codeType=${codeType}&deviceId=${deviceId}&t=${timestamp}&direct=true`;
}

// 用户登录
export async function login(userParams: LoginParams, captcha: string, captchaKey?: string): Promise<any> {
  try {
    // 构建登录请求数据
    const loginData: any = {
      user: {
        card: userParams.card,
        pwd: userParams.pwd
      },
      captcha: captcha
    };
    
    // 如果提供了验证码标识，加入请求
    if (captchaKey) {
      loginData.captchaKey = captchaKey;
    }
    
    console.log('发送登录请求，用户:', userParams.card);
    
    // 发送登录请求
    const response = await api.post('/api/login', loginData);
    
    console.log('登录响应:', response);
    
    // 如果登录成功，保存token到localStorage
    if (response && response.success) {
      // 确保token存在
      if (!response.data.token) {
        console.error('登录成功但响应中没有token:', response.data);
        return { 
          success: false, 
          message: '服务器返回的响应缺少token信息' 
        };
      }
      
      console.log('登录成功，保存token到localStorage');
      localStorage.setItem('token', response.data.token);
      
      // 保存token过期时间
      const now = new Date().getTime();
      let expiryTime = now + 24 * 60 * 60 * 1000; // 默认24小时过期
      
      if (response.data.expiry_time) {
        expiryTime = response.data.expiry_time;
      }
      
      localStorage.setItem('token_expiry', expiryTime.toString());
      console.log('设置token过期时间:', new Date(expiryTime).toLocaleString());
      
      // 保存登录响应到sessionStorage，供后续使用
      try {
        sessionStorage.setItem('loginResponse', JSON.stringify(response));
        console.log('登录响应已保存到sessionStorage');
      } catch (e) {
        console.error('保存登录响应到sessionStorage失败:', e);
      }
    } else {
      console.warn('登录请求返回非成功状态:', response);
    }
    
    return response;
  } catch (error: unknown) {
    console.error('登录请求失败', error);
    const errorMessage = error instanceof Error ? error.message : '登录失败';
    return { success: false, message: errorMessage };
  }
}

// 退出登录
export async function logout(): Promise<boolean> {
  try {
    // 在发送请求前确认token是否存在
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('退出登录时没有找到token');
    }
    
    // 发送登出请求到后端 - 使用正确的URL：/api/logout
    await api.post('/api/logout', {}, {
      headers: {
        'Authorization': `Bearer ${token || ''}` // 确保即使token为null也不会报错
      }
    });
    
    // 清除本地存储的token相关信息
    localStorage.removeItem('token');
    localStorage.removeItem('token_expiry');
    sessionStorage.removeItem('user_info');
    
    console.log('本地状态已清除');
    return true;
  } catch (error) {
    console.error('退出登录失败', error);
    
    // 即使后端请求失败，也清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('token_expiry');
    sessionStorage.removeItem('user_info');
    
    // 对于403错误也视为成功，因为我们已经清除了本地状态
    const axiosError = error as any;
    if (axios.isAxiosError(error) && axiosError.response?.status === 403) {
      console.log('尽管收到403错误，本地状态已清除，视为退出成功');
      return true;
    }
    
    return false;
  }
}

// 刷新JWT令牌
export async function refreshToken(): Promise<any> {
  try {
    const response = await api.post('/api/auth/refresh');
    
    // 如果刷新成功，更新localStorage中的token
    if (response && response.success) {
      localStorage.setItem('token', response.data.token);
      
      if (response.data.expiry_time) {
        localStorage.setItem('token_expiry', response.data.expiry_time.toString());
      }
    }
    
    return response;
  } catch (error: unknown) {
    console.error('刷新令牌失败', error);
    const errorMessage = error instanceof Error ? error.message : '刷新令牌失败';
    return { success: false, message: errorMessage };
  }
}

// 检查token是否即将过期 (默认阈值: 10分钟)
export function isTokenExpiringSoon(thresholdMinutes: number = 10): boolean {
  const expiryTime = localStorage.getItem('token_expiry');
  // 如果没有过期时间，不要认为即将过期
  if (!expiryTime) {
    console.log('未找到Token过期时间，不触发刷新');
    return false;
  }
  
  const expiryDate = parseInt(expiryTime);
  // 验证expiryDate是否是有效的时间戳
  if (isNaN(expiryDate)) {
    console.log('Token过期时间格式无效，不触发刷新');
    return false;
  }
  
  const now = new Date().getTime();
  const thresholdMs = thresholdMinutes * 60 * 1000;
  const timeLeft = expiryDate - now;
  
  // 只有当剩余时间小于阈值且大于0时才需要刷新
  const shouldRefresh = timeLeft < thresholdMs && timeLeft > 0;
  if (shouldRefresh) {
    console.log(`Token将在 ${Math.floor(timeLeft / 1000)} 秒后过期，需要刷新`);
  }
  
  return shouldRefresh;
}

// 检查token是否已过期
export function isTokenExpired(): boolean {
  const expiryTime = localStorage.getItem('token_expiry');
  if (!expiryTime) {
    console.log('未找到Token过期时间，视为已过期');
    return true;
  }
  
  const expiryDate = parseInt(expiryTime);
  if (isNaN(expiryDate)) {
    console.log('Token过期时间格式无效，视为已过期');
    return true;
  }
  
  const now = new Date().getTime();
  const isExpired = now >= expiryDate;
  
  if (isExpired) {
    console.log('Token已过期，过期时间:', new Date(expiryDate).toLocaleString());
  }
  
  return isExpired;
} 

// 忘记密码 - 发送验证码
export async function sendResetPasswordCode(cardNumber: string, type: 'email' | 'sms'): Promise<any> {
  try {
    const response = await api.post('/api/users/reset-password/send-code', {
      card: cardNumber,
      type: type
    });
    
    return response;
  } catch (error: unknown) {
    console.error('发送重置密码验证码失败', error);
    const errorMessage = error instanceof Error ? error.message : '发送验证码失败';
    return { success: false, message: errorMessage };
  }
}

// 忘记密码 - 验证码验证并重置密码
export async function verifyResetPasswordCode(cardNumber: string, code: string, type: 'email' | 'sms', newPassword: string): Promise<any> {
  try {
    const response = await api.post('/api/users/reset-password/verify', {
      card: cardNumber,
      code: code,
      type: type,
      newPassword: newPassword
    });
    
    return response;
  } catch (error: unknown) {
    console.error('验证重置密码失败', error);
    const errorMessage = error instanceof Error ? error.message : '重置密码失败';
    return { success: false, message: errorMessage };
  }
} 