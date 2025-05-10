<script setup lang="ts">
import { useRouter } from 'vue-router';

const router = useRouter();

// 返回首页
const goToHome = () => {
  router.push('/');
};
</script>

<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <div class="error-message">页面不存在</div>
      <div class="error-desc">很抱歉，您访问的页面不存在或已被移除</div>
      <el-button type="primary" @click="goToHome">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 40px;
}

.error-code {
  font-size: 120px;
  color: #409eff;
  line-height: 1.2;
  font-weight: bold;
}

.error-message {
  font-size: 30px;
  color: #303133;
  margin: 20px 0;
}

.error-desc {
  font-size: 16px;
  color: #909399;
  margin-bottom: 30px;
}
</style> 