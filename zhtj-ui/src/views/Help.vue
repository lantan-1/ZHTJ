<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { DocumentCopy, QuestionFilled, ChatLineRound } from '@element-plus/icons-vue';

const router = useRouter();

// 帮助指南项目
const helpGuides = [
  {
    title: '智慧团建系统使用手册',
    icon: DocumentCopy,
    color: '#409eff',
    description: '详细了解系统的各项功能和操作方法',
    link: '/dashboard/help/manual'
  },
  {
    title: '常见问题解答',
    icon: QuestionFilled,
    color: '#e6a23c',
    description: '查看常见问题及其解决方案',
    link: '/dashboard/faq'
  },
  {
    title: '联系我们',
    icon: ChatLineRound,
    color: '#67c23a',
    description: '有任何问题？联系我们获取帮助',
    link: '/dashboard/contact'
  }
];

// 热门问题
const hotQuestions = [
  { 
    question: '如何修改我的密码？', 
    answer: '您可以在"系统设置"中点击"修改密码"，填写原密码和新密码后保存即可修改密码。'
  },
  { 
    question: '忘记密码怎么办？', 
    answer: '如果您忘记了密码，可以在登录页面点击"忘记密码"，通过身份证号和手机号验证后重置密码。'
  },
  { 
    question: '如何查看我的组织信息？', 
    answer: '在系统首页，您可以看到您所属组织的基本信息卡片。点击卡片可以查看更详细的组织信息。'
  },
  { 
    question: '如何添加新的团建活动？', 
    answer: '在"活动管理"页面，点击右上角的"新建活动"按钮，填写活动信息后点击保存即可创建新的团建活动。'
  },
  { 
    question: '如何进行组织关系转接？', 
    answer: '在"组织关系转接"页面，您可以发起或处理组织关系转接申请。填写相关信息后提交审核即可。'
  }
];

// 打开指南链接
const openGuide = (link: string) => {
  router.push(link);
};
</script>

<template>
  <div class="help-center">
    <div class="welcome-section">
      <h2>帮助中心</h2>
      <p>在这里您可以找到关于使用智慧团建系统的所有帮助信息</p>
    </div>
    
    <el-row :gutter="20" class="guide-cards">
      <el-col 
        v-for="(guide, index) in helpGuides" 
        :key="index" 
        :xs="24" 
        :sm="24" 
        :md="8"
      >
        <el-card 
          class="guide-card" 
          shadow="hover" 
          @click="openGuide(guide.link)"
        >
          <div class="guide-icon" :style="{ backgroundColor: `${guide.color}20` }">
            <el-icon :color="guide.color" :size="36">
              <component :is="guide.icon" />
            </el-icon>
          </div>
          <h3>{{ guide.title }}</h3>
          <p>{{ guide.description }}</p>
          <el-button 
            type="primary" 
            text 
            class="guide-button"
          >
            查看详情
          </el-button>
        </el-card>
      </el-col>
    </el-row>
    
    <div class="hot-questions-section">
      <h3>热门问题</h3>
      <el-collapse>
        <el-collapse-item 
          v-for="(item, index) in hotQuestions" 
          :key="index" 
          :name="index"
        >
          <template #title>
            <span class="question">
              <el-icon color="#c62828" class="question-icon"><QuestionFilled /></el-icon>
              {{ item.question }}
            </span>
          </template>
          <div class="answer">{{ item.answer }}</div>
        </el-collapse-item>
      </el-collapse>
      
      <div class="more-questions">
        <el-button type="primary" plain @click="router.push('/dashboard/faq')">
          查看更多问题
        </el-button>
      </div>
    </div>
    
    <div class="contact-section">
      <h3>联系我们</h3>
      <p>如果您有其他问题，欢迎随时联系我们</p>
      <div class="contact-methods">
        <div class="contact-method">
          <el-icon :size="24" color="#c62828"><ChatLineRound /></el-icon>
          <div class="contact-details">
            <h4>在线客服</h4>
            <p>工作时间: 周一至周五 8:30-17:30</p>
          </div>
        </div>
        <div class="contact-method">
          <el-icon :size="24" color="#c62828"><i class="el-icon-phone"></i></el-icon>
          <div class="contact-details">
            <h4>电话咨询</h4>
            <p>123-4567-890</p>
          </div>
        </div>
      </div>
      <el-button type="primary" @click="router.push('/dashboard/contact')">
        联系我们
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.help-center {
  padding: 20px;
}

.welcome-section {
  text-align: center;
  margin-bottom: 30px;
}

.welcome-section h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.welcome-section p {
  color: #606266;
  font-size: 16px;
}

.guide-cards {
  margin-bottom: 40px;
}

.guide-card {
  height: 100%;
  cursor: pointer;
  transition: transform 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 20px;
}

.guide-card:hover {
  transform: translateY(-5px);
}

.guide-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  margin: 0 auto 20px;
}

.guide-card h3 {
  font-size: 18px;
  margin-bottom: 10px;
  color: #303133;
}

.guide-card p {
  color: #606266;
  flex-grow: 1;
  margin-bottom: 20px;
}

.guide-button {
  margin-top: auto;
}

.hot-questions-section {
  margin-bottom: 40px;
}

.hot-questions-section h3 {
  font-size: 22px;
  margin-bottom: 20px;
  color: #303133;
}

.question {
  display: flex;
  align-items: center;
}

.question-icon {
  margin-right: 8px;
}

.answer {
  padding: 10px 0 10px 28px;
  color: #606266;
  line-height: 1.6;
}

.more-questions {
  text-align: center;
  margin-top: 20px;
}

.more-questions .el-button,
.contact-section .el-button {
  color: white;
  background-color: #c62828;
  border-color: #c62828;
  padding: 12px 24px;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.more-questions .el-button:hover,
.contact-section .el-button:hover {
  background-color: #b71c1c;
  border-color: #b71c1c;
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(198, 40, 40, 0.2);
}

.contact-section {
  background-color: #f8f9fa;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
}

.contact-section h3 {
  font-size: 22px;
  margin-bottom: 10px;
  color: #303133;
}

.contact-section > p {
  color: #606266;
  margin-bottom: 20px;
}

.contact-methods {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.contact-method {
  display: flex;
  align-items: center;
  text-align: left;
}

.contact-details {
  margin-left: 12px;
}

.contact-details h4 {
  font-size: 16px;
  margin-bottom: 4px;
  color: #303133;
}

.contact-details p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

@media (max-width: 768px) {
  .guide-card {
    margin-bottom: 20px;
  }
  
  .contact-methods {
    flex-direction: column;
    gap: 20px;
  }
}
</style> 