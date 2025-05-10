<script setup lang="ts">
import { Headset, Phone, Clock, Link } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();

// 跳转到联系我们页面
const goToContact = () => {
  router.push('/dashboard/contact');
};

// 复制邮箱到剪贴板并显示提示
const copyEmail = (email) => {
  navigator.clipboard.writeText(email)
    .then(() => {
      ElMessage({
        message: '邮箱已复制到剪贴板',
        type: 'success',
        duration: 2000
      });
    })
    .catch(() => {
      ElMessage({
        message: '复制失败，请手动复制',
        type: 'error',
        duration: 2000
      });
    });
};
</script>

<template>
  <div class="footer-container">
    <div class="footer-section">
    <div class="consult-info">
        <el-icon><Phone /></el-icon>
        <span class="consult-title">咨询电话：<a class="detail-link" @click="goToContact">"查看详情"</a> </span>
        <el-icon class="time-icon"><Clock /></el-icon>
      <span class="consult-time">咨询时间: 工作日8:30-17:30</span>
    </div>
    
    <div class="consult-tips">
        <p><span class="tip-number">1.</span> 关于常见业务流程，请点方法咨询问题，可查看左侧"帮助中心"或直接咨询上级组织管理员;</p>
        <p><span class="tip-number">2.</span> 如有其他技术问题，可将问题和系统截图发送至邮箱: <a class="email-link" @click.prevent="copyEmail('lantank2024@163.com')" title="点击复制邮箱">lantank2024@163.com</a>。</p>
      </div>
    </div>
    
    <div class="footer-divider"></div>
    
    <div class="system-info">
      <div class="system-logo">
        <img src="../../assets/img/logo.png" alt="智慧团建系统" class="system-logo-img" />
        <div class="system-text">
          <div class="system-name">"智慧团建"系统</div>
          <div class="copyright">© {{ new Date().getFullYear() }} 共青团系统开发测试</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.footer-container {
  padding: 20px 30px;
  border-top: 1px solid #e6e6e6;
  background-color: #f9f9f9;
  font-family: "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial, sans-serif;
}

.footer-section {
  margin-bottom: 15px;
}

.consult-info {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  color: #555;
  font-size: 14px;
}

.consult-title {
  margin-left: 8px;
  font-weight: bold;
  margin-right: 20px;
}

.time-icon {
  margin-left: 5px;
}

.consult-time {
  margin-left: 8px;
}

.consult-tips {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}

.consult-tips p {
  margin: 8px 0;
  display: flex;
  align-items: flex-start;
}

.tip-number {
  font-weight: bold;
  margin-right: 5px;
  flex-shrink: 0;
}

.email-link {
  color: #c03639;
  text-decoration: none;
  transition: all 0.3s;
  cursor: pointer;
  padding: 2px 5px;
  border-radius: 4px;
  position: relative;
}

.email-link:hover {
  color: #e03e41;
  background-color: rgba(192, 54, 57, 0.1);
}

.email-link:active {
  color: #a02e30;
}

.footer-divider {
  height: 1px;
  background-color: #e6e6e6;
  margin: 15px 0;
}

.system-info {
  display: flex;
  justify-content: center;
}

.system-logo {
  display: flex;
  align-items: center;
}

.system-logo-img {
  width: 40px;
  height: 40px;
  margin-right: 10px;
  filter: drop-shadow(0 0 2px rgba(0, 0, 0, 0.1));
}

.system-text {
  font-size: 13px;
  color: #444;
  display: flex;
  flex-direction: column;
}

.system-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.copyright {
  font-size: 12px;
  color: #666;
}

.detail-link {
  color: #c03639;
  cursor: pointer;
  text-decoration: none;
  transition: color 0.3s;
}

.detail-link:hover {
  color: #e03e41;
  text-decoration: underline;
}
</style> 