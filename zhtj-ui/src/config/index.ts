/**
 * 系统配置文件
 */

// 判断当前环境
const isDev = process.env.NODE_ENV === 'development';

// 系统配置
const config = {
  // API 配置
  api: {
    baseUrl: isDev ? '/api' : 'https://api.example.com',
    timeout: 10000, // 请求超时时间 (ms)
    retryTimes: 1,  // 请求失败后重试次数
  },
  
  // 模拟数据配置
  mock: {
    enabled: isDev, // 开发环境下默认启用模拟数据
    delay: 300,     // 模拟延迟 (ms)
  },
  
  // 缓存配置
  cache: {
    enabled: true,
    expire: 30 * 60 * 1000, // 缓存过期时间 (ms) - 默认30分钟
  },
  
  // 系统功能开关
  features: {
    certificateGeneration: true, // 证书生成功能
    statistics: true,            // 统计分析功能
    userPermission: true,        // 用户权限控制
  }
};

export default config; 