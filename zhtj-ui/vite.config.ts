import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd())
  const apiBaseUrl = env.VITE_API_BASE_URL || 'http://localhost:8080'
  
  console.log('API基础URL配置:', apiBaseUrl)
  
  return {
    plugins: [vue()],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url))
      }
    },
    server: {
      hmr: true, // 启用热模块替换
      watch: {
        usePolling: true, // 在某些系统上可能需要轮询来检测文件变化
      },
      proxy: {
        '/api': {
          target: apiBaseUrl, // 使用环境变量中的API基础URL
          changeOrigin: true,
          // 不再重写路径，保留/api前缀
          rewrite: (path) => path,
          configure: (proxy, _options) => {
            proxy.on('proxyReq', (proxyReq, req, _res) => {
              // 保留原始的Cookie头
              if (req.headers.cookie) {
                proxyReq.setHeader('Cookie', req.headers.cookie);
              }
              
              // 为验证码请求添加特殊处理
              const url = req.url || '';
              if (url.includes('/api/captcha')) {
                // 添加标记这是AJAX请求的头，可以帮助绕过某些CSRF保护
                proxyReq.setHeader('X-Requested-With', 'XMLHttpRequest');
                
                // 添加一些额外的头，帮助处理CSRF
                if (req.method === 'POST') {
                  proxyReq.setHeader('Content-Type', 'application/json;charset=UTF-8');
                }
              }
              
              // 调试日志
              console.log(`代理请求: ${req.method} ${url} -> ${apiBaseUrl}${url}`);
            });
            
            // 处理代理响应事件
            proxy.on('proxyRes', (_proxyRes, req, _res) => {
              console.log(`代理响应: ${req.method} ${req.url}`);
            });
            
            // 处理代理错误
            proxy.on('error', (err, _req, _res) => {
              console.error('代理错误:', err);
            });
          }
        },
        // 添加特殊处理，为所有REST风格的URL提供代理
        // 将/evaluations和/member-evaluations请求都代理到/api/member-evaluations
        '/evaluations': {
          target: apiBaseUrl,
          changeOrigin: true,
          rewrite: (path) => `/api/member-evaluations${path.replace('/evaluations', '')}`
        },
        '/member-evaluations': {
          target: apiBaseUrl,
          changeOrigin: true,
          rewrite: (path) => `/api${path}`
        },
        '/users': {
          target: apiBaseUrl,
          changeOrigin: true,
          rewrite: (path) => `/api${path}`
        },
        '/organizations': {
          target: apiBaseUrl,
          changeOrigin: true,
          rewrite: (path) => `/api${path}`
        },
        '/statistics': {
          target: apiBaseUrl,
          changeOrigin: true,
          rewrite: (path) => `/api${path}`
        }
      }
    }
  }
})
