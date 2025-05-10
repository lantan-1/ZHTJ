import axios from 'axios';
import router from '../router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { isTokenExpired, isTokenExpiringSoon, refreshToken } from './login';
import { useUserStore } from '../stores/user';

// 获取环境变量中的API基础URL
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || '';

// 创建axios实例
const api = axios.create({
  baseURL: apiBaseUrl,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true  // 确保跨域请求时携带凭证（Cookie）
});

console.log('API基础URL:', apiBaseUrl);

// 排除自动令牌刷新的URL列表
const excludeRefreshTokenUrls = [
  '/api/login',
  '/api/logout',
  '/api/auth/refresh',
  '/api/captcha'
];

// 请求拦截器
api.interceptors.request.use(
  async (config: any) => {
    // 特殊处理验证码请求
    if (config.url && config.url.includes('/captcha')) {
      config.headers = {
        ...config.headers,
        'X-Requested-With': 'XMLHttpRequest',
        'Content-Type': 'application/json;charset=UTF-8'
      };
      
      // 对验证码请求，不需要后续处理了
      return config;
    }
    
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    
    // 增加调试日志
    console.log(`请求路径: ${config.url}, 是否有token: ${!!token}`);
    
    // 如果有token，添加到请求头
    if (token && config.headers) {
      config.headers['Authorization'] = `Bearer ${token}`;
      
      // 调试日志
      console.log(`已添加认证头: Bearer ${token.substring(0, 10)}...`);
      
      // 对非排除的URL检查token是否即将过期
      const currentUrl = config.url || '';
      const isExcluded = excludeRefreshTokenUrls.some(url => currentUrl.includes(url));
      
      // 如果token即将过期且当前请求不在排除列表中，尝试刷新token
      if (!isExcluded && isTokenExpiringSoon()) {
        try {
          console.log('Token即将过期，尝试刷新...');
          const response = await refreshToken();
          
          if (response.success) {
            // 使用新token更新请求头
            const newToken = localStorage.getItem('token');
            config.headers['Authorization'] = `Bearer ${newToken}`;
            console.log(`Token刷新成功，新token: ${newToken?.substring(0, 10)}...`);
          } else {
            console.warn('Token刷新失败');
          }
        } catch (error) {
          console.error('刷新Token出错', error);
        }
      }
    }
    
    // 添加标准的AJAX请求头
    if (config.headers) {
      config.headers['X-Requested-With'] = 'XMLHttpRequest';
    }
    
    return config;
  },
  (error: any) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response: any) => {
    // 对于二进制数据响应，直接返回不做处理
    if (response.config.responseType === 'blob' || 
        response.config.responseType === 'arraybuffer' || 
        response.headers['content-type']?.includes('application/octet-stream') ||
        response.headers['content-type']?.includes('application/pdf') ||
        response.headers['content-type']?.includes('video/') ||
        response.headers['content-type']?.includes('audio/')) {
      console.log('检测到二进制响应，直接返回response');
      return response;
    }
    
    const res = response.data;
    console.log(`API响应状态: ${response.status}, 响应数据:`, res);
    
    // 请求成功但业务状态检查
    if (res.code === 200) {
      console.log('响应状态码200，返回数据');
      // 确保成功响应有success标志
      if (res.success === undefined) {
        res.success = true;
      }
      
      // 处理标准响应格式 {code, message, data}
      console.log('响应数据结构:', Object.keys(res));
      if (res.data !== undefined) {
        console.log('标准格式响应，data字段存在:', res.data);
      }
      
      return res;
    } else if (res.success === true) {
      // 显式标记为成功的响应
      console.log('响应success=true，返回数据');
      if (res.code === undefined) {
        res.code = 200;
      }
      return res;
    }
    
    // 处理token过期的情况
    if (res.code === 401 || res.code === 'UNAUTHORIZED') {
      console.log('检测到Token过期，准备跳转登录');
      ElMessageBox.confirm(
        '您的登录已过期，请重新登录',
        '登录过期',
        {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // 清除用户信息并重定向到登录页
        const userStore = useUserStore();
        userStore.doLogout(); // 使用正确的方法名
        router.push('/login');
      });
      return Promise.reject(new Error('登录已过期'));
    }
    
    // 其他业务错误情况
    console.error('API业务错误:', res.code, res.message);
    ElMessage({
      message: res.message || '请求失败',
      type: 'error',
      duration: 5 * 1000
    });
    return Promise.reject(new Error(res.message || '请求失败'));
  },
  (error: any) => {
    // 请求异常处理
    console.error('API请求异常:', error.response || error);
    let message = '网络错误，请稍后再试';
    
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求错误，请检查请求参数';
          break;
        case 403:
          message = '权限不足，无法访问该资源';
          break;
        case 404:
          message = '请求的资源不存在';
          break;
        case 500:
          message = '服务器错误，请联系管理员';
          break;
        default:
          message = `请求失败(${error.response.status})`;
      }
    } else if (error.message && error.message.includes('timeout')) {
      message = '请求超时，请检查网络连接';
    }
    
    ElMessage({
      message,
      type: 'error',
      duration: 5 * 1000
    });
    
    return Promise.reject(error);
  }
);

export default api; 