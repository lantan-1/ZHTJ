<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ArrowLeft, Timer, Document } from '@element-plus/icons-vue';
import { useUserStore } from '../../stores/user';
import { markAsRead } from '../../api/notification';

const router = useRouter();
const route = useRoute();
const notificationId = route.params.id;
const userStore = useUserStore();

// 通知数据
const notification = reactive({
  id: '',
  title: '',
  content: '',
  sender: '',
  createTime: '',
  readStatus: false,
  type: '',
  priority: '',
  attachments: [] as string[]
});

// 加载状态
const loading = ref(true);

// 获取通知详情
onMounted(async () => {
  try {
    // 确保用户已登录
    if (!userStore.isLoggedIn) {
      const token = localStorage.getItem('token');
      if (token) {
        userStore.setToken(token);
        await userStore.fetchUserInfo();
      } else {
        ElMessage.warning('未登录状态，请先登录');
        router.push('/login');
        return;
      }
    }
    
    // 在实际项目中，这里应该调用API获取通知详情
    // const response = await getNotificationDetail(notificationId);
    
    // 由于当前API可能尚未实现，使用模拟数据
    let mockData = {};
    
    if (notificationId === '1') {
      mockData = {
        id: notificationId,
        title: '智慧团建系统项目立项情况说明',
        content: `尊敬的各位团干部、团员：

智慧团建系统已于2025年3月1日正式立项，现将项目基本情况说明如下：

一、项目背景
为贯彻落实共青团中央关于加强基层团建工作的指示精神，充分利用信息技术手段提升团组织工作效率，我校决定自主研发智慧团建系统，打造集团员管理、组织管理、学习资源、活动管理于一体的智能化团建平台。

二、项目目标
1. 构建一个现代化、智能化的团组织管理与服务平台
2. 实现团员信息的精细化、数字化管理
3. 提供丰富的线上团课学习资源
4. 支持团组织活动的全流程管理
5. 实现各级团组织工作的数据化、可视化呈现

三、项目计划
1. 需求调研与分析阶段：2025年3月 - 2025年4月
2. 系统设计与开发阶段：2025年4月 - 2025年7月
3. 系统测试与优化阶段：2025年7月 - 2025年9月
4. 试运行阶段：2025年9月 - 2025年11月
5. 正式上线：2025年12月

四、各方责任
1. 系统开发：信息技术中心
2. 业务指导：校团委
3. 试点单位：各二级学院团委

五、联系方式
项目负责人：蓝探
联系电话：123-4567-8900
邮箱地址：lantan@example.com

欢迎各级团组织共同参与建设和试用，提供宝贵意见！`,
        sender: '系统管理员',
        createTime: '2025-03-01 09:30:00',
        readStatus: false,
        type: '系统通知',
        priority: '重要',
        attachments: ['智慧团建系统立项方案.pdf', '智慧团建系统功能设计图.xlsx']
      };
    } else if (notificationId === '2') {
      mockData = {
        id: notificationId,
        title: '智慧团建系统开发进度通报',
        content: `尊敬的各位团干部、团员：

智慧团建系统开发工作自今年3月启动以来，各项工作稳步推进。现将系统开发进度通报如下：

一、已完成功能模块
1. 用户管理：支持多角色权限控制，实现团干部、团员、管理员等不同身份用户的功能访问权限管理
2. 组织管理：完成团支部、团总支、团委等多级团组织结构设计与管理功能
3. 学习资源：实现团课资源的上传、分类、搜索和在线预览功能
4. 活动管理：完成活动发布、报名、签到和评价等全流程功能设计

二、当前版本情况
当前版本：v1.0.0 beta
状态：内测阶段
发布日期：2025年5月4日

三、正在开发功能
1. 消息通知系统：支持系统通知、活动提醒、审批结果通知等多种消息类型
2. 统计报表：团建工作数据可视化展示
3. 团员发展管理：入团申请、团员发展等全流程管理
4. 移动端适配：支持手机、平板等多终端访问

四、下阶段工作计划
1. 5月中旬：完成消息通知系统开发
2. 5月下旬：完成统计报表模块开发
3. 6月上旬：移动端适配测试
4. 6月中旬：系统封版测试
5. 6月底：正式版上线

特此通报，感谢各位团干部的宝贵意见和建议，我们将持续优化系统功能和用户体验，为基层团建工作提供有力支持。`,
        sender: '系统管理员',
        createTime: '2025-05-04 14:15:00',
        readStatus: false,
        type: '系统通知',
        priority: '普通',
        attachments: ['智慧团建系统开发进度甘特图.pdf', '智慧团建系统测试报告.docx']
      };
    } else if (notificationId === '3') {
      mockData = {
        id: notificationId,
        title: `欢迎${userStore.userInfo?.organization_name || ''}${userStore.userInfo?.league_position || ''}${userStore.userInfo?.name || ''}`,
        content: `尊敬的${userStore.userInfo?.organization_name || ''}${userStore.userInfo?.league_position || ''}${userStore.userInfo?.name || ''}：

欢迎您登录智慧团建系统！

作为${userStore.userInfo?.organization_name || ''}的${userStore.userInfo?.league_position || ''}，您在系统中拥有以下权限：

一、系统功能
1. 团员管理：查看和管理辖区内团员基本信息
2. 组织管理：维护和更新团组织结构信息
3. 学习资源：上传、管理团课学习资料
4. 活动管理：发起活动、审批活动申请、查看活动数据
5. 通知发布：向辖区内团员发布通知公告

二、使用指南
1. 通过左侧导航栏可访问各项功能模块
2. 点击右上角头像可修改个人信息和密码
3. 如需帮助，可点击下方"帮助中心"查看详细使用说明
4. 系统支持数据导入导出功能，便于批量处理

如有任何问题，请随时联系系统管理员或查看帮助文档。

祝您使用愉快！`,
        sender: '智慧团建',
        createTime: new Date().toLocaleString('zh-CN', {hour12: false}).replace(/\//g, '-'),
        readStatus: false,
        type: '系统通知',
        priority: '普通',
        attachments: ['智慧团建系统用户手册.pdf']
      };
    } else {
      mockData = {
        id: notificationId,
        title: '通知详情',
        content: '通知内容不存在或已被删除',
        sender: '系统',
        createTime: new Date().toLocaleString('zh-CN', {hour12: false}).replace(/\//g, '-'),
        readStatus: false,
        type: '系统通知',
        priority: '普通',
        attachments: []
      };
    }
    
    // 填充数据
    Object.assign(notification, mockData);
    
    // 如果通知未读，标记为已读
    if (!notification.readStatus && userStore.userId) {
      try {
        const userId = parseInt(userStore.id);
        await markAsRead(parseInt(notification.id as string), userId);
        notification.readStatus = true;
      } catch (error) {
        console.error('标记通知为已读失败', error);
      }
    }
  } catch (error) {
    console.error('获取通知详情失败', error);
    ElMessage.error('获取通知详情失败，请稍后重试');
  } finally {
    loading.value = false;
  }
});

// 返回通知列表
const goBack = () => {
  router.push('/dashboard/notifications');
};

// 格式化时间
const formatDate = (dateStr: string) => {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
};

// 下载附件
const downloadAttachment = (filename: string) => {
  ElMessage({
    message: `正在下载${filename}...`,
    type: 'info'
  });
  
  // 实际项目中，这里应该调用API下载文件
};

// 格式化通知内容 - 将纯文本转换为HTML
const formattedContent = computed(() => {
  if (!notification.content) return '';
  
  // 首先将内容按行分割
  const lines = notification.content.split('\n');
  let html = '';
  let inList = false;
  
  // 处理每一行
  lines.forEach((line, index) => {
    line = line.trim();
    
    // 空行处理
    if (line === '') {
      if (inList) {
        html += '</ol>';
        inList = false;
      }
      return; // 跳过空行
    }
    
    // 标题识别：汉字数字（一、二、三等）+、
    if (/^[一二三四五六七八九十]+、/.test(line)) {
      if (inList) {
        html += '</ol>';
        inList = false;
      }
      html += `<h3>${line}</h3>`;
      return;
    }
    
    // 数字列表识别：1. 2. 3. 等
    if (/^\d+\.\s/.test(line)) {
      // 如果这是列表中的第一项
      if (!inList) {
        html += '<ol>';
        inList = true;
      }
      
      // 提取列表项内容，去掉序号
      const content = line.replace(/^\d+\.\s/, '');
      html += `<li>${content}</li>`;
      return;
    }
    
    // 如果当前在列表中但遇到非列表项，结束列表
    if (inList && !/^\d+\.\s/.test(line)) {
      html += '</ol>';
      inList = false;
    }
    
    // 其他内容当作段落处理
    if (!inList) {
      html += `<p>${line}</p>`;
    }
  });
  
  // 确保列表正确闭合
  if (inList) {
    html += '</ol>';
  }
  
  return html;
});
</script>

<template>
  <div class="notification-detail-container">
    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <el-button link @click="goBack" :icon="ArrowLeft">返回</el-button>
          </div>
          <div class="header-right">
            <span class="date-info">
              <el-icon><Timer /></el-icon>
              {{ formatDate(notification.createTime) }}
            </span>
          </div>
        </div>
      </template>
      
      <div class="notification-content" v-if="!loading">
        <h2 class="notification-title">{{ notification.title }}</h2>
        
        <div class="notification-meta">
          <el-tag size="small" type="info" class="meta-tag">{{ notification.type }}</el-tag>
          <el-tag 
            size="small" 
            :type="notification.priority === '重要' ? 'danger' : 'warning'" 
            class="meta-tag"
          >
            {{ notification.priority }}
          </el-tag>
          <span class="meta-sender">发布人: {{ notification.sender }}</span>
        </div>
        
        <div class="notification-body">
          <div class="formatted-content" v-html="formattedContent"></div>
        </div>
        
        <div class="notification-attachments" v-if="notification.attachments && notification.attachments.length > 0">
          <h3>附件:</h3>
          <ul class="attachment-list">
            <li 
              v-for="(file, index) in notification.attachments" 
              :key="index"
              @click="downloadAttachment(file)"
              class="attachment-item"
            >
              <span class="file-icon">
                <el-icon><Document /></el-icon>
              </span>
              <span class="file-name">{{ file }}</span>
            </li>
          </ul>
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.notification-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left, .header-right {
  display: flex;
  align-items: center;
}

.date-info {
  font-size: 14px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
}

.notification-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
  text-align: center;
  font-weight: 600;
}

.notification-meta {
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-tag {
  margin-right: 5px;
}

.meta-sender {
  font-size: 14px;
  color: #606266;
}

.notification-body {
  margin-bottom: 30px;
  line-height: 1.8;
  color: #303133;
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 使用新的:deep()语法替换::v-deep */
.formatted-content {
  font-family: inherit;
  font-size: 15px;
  line-height: 1.8;
}

.formatted-content :deep(h1),
.formatted-content :deep(h2),
.formatted-content :deep(h3),
.formatted-content :deep(h4),
.formatted-content :deep(h5),
.formatted-content :deep(h6) {
  font-weight: 600;
  margin-top: 24px;
  margin-bottom: 16px;
  line-height: 1.25;
  color: #303133;
}

.formatted-content :deep(h3) {
  font-size: 18px;
  color: #409EFF;
  border-bottom: 1px solid #EBEEF5;
  padding-bottom: 8px;
  margin-top: 30px;
}

.formatted-content :deep(p) {
  margin-bottom: 16px;
  text-indent: 0;
}

.formatted-content :deep(ol),
.formatted-content :deep(ul) {
  padding-left: 24px;
  margin-bottom: 16px;
  margin-top: 16px;
}

.formatted-content :deep(li) {
  margin-bottom: 8px;
}

.formatted-content :deep(li:last-child) {
  margin-bottom: 0;
}

.formatted-content :deep(ol) {
  list-style-type: decimal;
}

.formatted-content :deep(ul) {
  list-style-type: disc;
}

.notification-attachments {
  margin-top: 30px;
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
}

.notification-attachments h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #303133;
  font-weight: 600;
  border-left: 3px solid #409EFF;
  padding-left: 10px;
}

.attachment-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fff;
}

.attachment-item:hover {
  background-color: #e6f1fe;
  border-color: #b3d8ff;
}

.file-icon {
  margin-right: 12px;
  color: #409eff;
  font-size: 18px;
}

.file-name {
  color: #606266;
  font-size: 14px;
}
</style> 