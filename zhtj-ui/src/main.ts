import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import './style.css'
// 引入公共样式
import './styles/common.scss'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import { vPermission } from './utils/permission'
import { useUserStore } from './stores/user'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)

const userStore = useUserStore()

// 初始化用户状态 - 从本地存储加载
const initApp = () => {
  console.log('初始化应用...')
  const token = localStorage.getItem('token')
  
  if (token) {
    console.log('从localStorage中发现token:', token.substring(0, 10) + '...')
    
    try {
      // 1. 设置token到store
      userStore.setToken(token)
      
      // 2. 确保从localStorage加载用户基本信息
userStore.initFromStorage && userStore.initFromStorage()
      
      // 3. 验证登录状态是否正确设置
      console.log('应用初始化后的登录状态:', userStore.isLoggedIn)
      
      // 4. 延迟异步获取最新用户信息 (不阻塞应用启动)
      setTimeout(() => {
        if (userStore.isLoggedIn) {
          console.log('异步获取最新用户信息...')
          userStore.fetchUserInfo().catch(err => {
            console.error('获取用户信息失败，但不影响应用继续运行:', err)
          })
        }
      }, 500)
    } catch (error) {
      console.error('初始化用户状态时出错:', error)
    }
  } else {
    console.log('未找到token，保持未登录状态')
  }
}

// 执行初始化
initApp()

// 注册自定义指令
app.directive('permission', vPermission)

app.use(ElementPlus, {locale: zhCn}).use(router).mount('#app')
