<script setup lang="ts">
// App.vue是根组件
import { onMounted, ref } from 'vue';
import { useUserStore } from './stores/user';
import { useRouter } from 'vue-router';

const router = useRouter();
const userStore = useUserStore();
const isInitialized = ref(false);

// 组件挂载时检查登录状态
onMounted(() => {
  console.log('App.vue 组件挂载');
  
  // 防止重复初始化
  if (isInitialized.value) {
    console.log('App.vue - 已经初始化过，跳过');
    return;
  }
  
  // 标记为已初始化
  isInitialized.value = true;
  
  // 检查localStorage中的token
  const token = localStorage.getItem('token');
  const tokenExpiry = localStorage.getItem('token_expiry');
  
  console.log('App.vue - localStorage中是否有token:', !!token);
  
  // 检查token是否过期
  if (token && tokenExpiry) {
    const expiryTime = parseInt(tokenExpiry);
    const now = new Date().getTime();
    
    if (!isNaN(expiryTime) && now >= expiryTime) {
      console.log('App.vue - token已过期，清除登录状态');
      userStore.clearUserInfo();
        router.push('/login');
      return;
    }
  }
  
  // 如果token有效，确保store状态同步
  if (token) {
    console.log('App.vue - 发现有效token，确保登录状态同步');
    
    // 设置token到store
    userStore.setToken(token);
    
    // 从localStorage加载基本用户信息
    if (userStore.initFromStorage) {
      userStore.initFromStorage();
    }
    
    // 确保登录状态正确设置
    console.log('App.vue - 同步后的登录状态:', userStore.isLoggedIn);
  }
});
</script>

<template>
  <router-view />
</template>

<style>
body {
  width: 100%;
  margin: auto;
  padding: 0;
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 全局样式 */
.page-container {
  padding: 20px;
}

.form-container {
  max-width: 100vh;
  margin: 0;
}
</style>
