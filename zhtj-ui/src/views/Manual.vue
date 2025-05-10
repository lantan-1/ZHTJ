<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Document, Notebook, Link, QuestionFilled, ArrowLeft } from '@element-plus/icons-vue';

const router = useRouter();

// 手册章节
const manualSections = [
  {
    title: '系统介绍',
    icon: Document,
    content: '智慧团建系统是为共青团组织提供的一站式数字化管理平台，帮助团组织高效开展各项工作。通过本系统，您可以进行团员管理、活动组织、数据统计、评议管理等操作。'
  },
  {
    title: '快速入门',
    icon: Notebook,
    content: '1. 首次登录后，请完善您的个人信息；\n2. 在主页查看您所在组织的基本信息和动态；\n3. 通过左侧菜单导航访问各功能模块；\n4. 如有疑问，可随时查看帮助中心或联系管理员。'
  },
  {
    title: '功能说明',
    icon: Link,
    subsections: [
      { title: '团员管理', desc: '管理团员信息、审核入团申请、处理组织关系转接等' },
      { title: '活动管理', desc: '创建和管理团建活动，包括活动申报、签到、评价等' },
      { title: '评议管理', desc: '开展团员评议工作，生成评议结果报告' },
      { title: '荣誉管理', desc: '管理团内各类荣誉评选和申报' },
      { title: '数据统计', desc: '查看团组织各类数据统计和分析图表' }
    ]
  },
  {
    title: '常见问题',
    icon: QuestionFilled,
    content: '本节列出了用户在使用过程中常见的问题和解决方法，如遇到未覆盖的问题，请查看"常见问题"专区或联系系统管理员。'
  }
];

// 返回帮助中心
const goBackToHelp = () => {
  router.push('/dashboard/help');
};
</script>

<template>
  <div class="manual-container">
    <div class="page-header">
      <div class="back-link" @click="goBackToHelp">
        <el-icon><ArrowLeft /></el-icon>
        <span>返回帮助中心</span>
      </div>
      <h1>智慧团建系统使用手册</h1>
      <p class="header-desc">详细了解系统的各项功能和操作方法</p>
    </div>
    
    <div class="manual-content">
      <div class="toc-section">
        <h3>目录</h3>
        <ul class="toc-list">
          <li v-for="(section, index) in manualSections" :key="index">
            <a :href="`#section-${index}`">{{ section.title }}</a>
          </li>
        </ul>
      </div>
      
      <div class="sections-container">
        <section 
          v-for="(section, index) in manualSections" 
          :key="index" 
          :id="`section-${index}`"
          class="manual-section"
        >
          <div class="section-header">
            <el-icon :size="24" class="section-icon">
              <component :is="section.icon" />
            </el-icon>
            <h2>{{ section.title }}</h2>
          </div>
          
          <div class="section-content">
            <p v-if="section.content" v-html="section.content.replace(/\n/g, '<br>')"></p>
            
            <div v-if="section.subsections" class="subsections">
              <div 
                v-for="(subsection, subIndex) in section.subsections" 
                :key="subIndex"
                class="subsection"
              >
                <h3>{{ subsection.title }}</h3>
                <p>{{ subsection.desc }}</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
    
    <div class="footer-actions">
      <p>如果您对使用手册有任何疑问，欢迎联系我们获取更多帮助</p>
      <el-button type="primary" @click="router.push('/dashboard/contact')">
        联系我们
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.manual-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
  position: relative;
}

.back-link {
  position: absolute;
  left: 0;
  top: 10px;
  display: flex;
  align-items: center;
  color: #c62828;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.3s;
}

.back-link:hover {
  color: #e03e41;
}

.back-link .el-icon {
  margin-right: 5px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 500;
  margin: 0 0 15px;
  color: #303133;
}

.header-desc {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.manual-content {
  display: flex;
  gap: 30px;
  margin-bottom: 40px;
}

.toc-section {
  width: 250px;
  flex-shrink: 0;
  position: sticky;
  top: 20px;
  align-self: flex-start;
  background-color: #f5f7fa;
  border-radius: 8px;
  padding: 20px;
}

.toc-section h3 {
  margin: 0 0 15px;
  font-size: 18px;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 10px;
}

.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.toc-list li {
  margin-bottom: 10px;
}

.toc-list a {
  color: #606266;
  text-decoration: none;
  display: block;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.toc-list a:hover {
  background-color: #ecf5ff;
  color: #c62828;
}

.sections-container {
  flex: 1;
  min-width: 0;
}

.manual-section {
  margin-bottom: 40px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.section-header {
  background-color: #f5f7fa;
  padding: 20px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
}

.section-icon {
  color: #c62828;
  margin-right: 10px;
}

.section-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 500;
  color: #303133;
}

.section-content {
  padding: 20px;
}

.section-content p {
  line-height: 1.8;
  color: #606266;
  margin-top: 0;
}

.subsections {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-top: 10px;
}

.subsection {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 6px;
  border-left: 3px solid #c62828;
}

.subsection h3 {
  margin: 0 0 10px;
  font-size: 16px;
  color: #303133;
}

.subsection p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.footer-actions {
  text-align: center;
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.footer-actions p {
  margin: 0 0 15px;
  color: #606266;
}

.footer-actions .el-button {
  background-color: #c62828;
  border-color: #c62828;
}

.footer-actions .el-button:hover {
  background-color: #b71c1c;
  border-color: #b71c1c;
}

@media (max-width: 768px) {
  .manual-content {
    flex-direction: column;
  }
  
  .toc-section {
    width: 100%;
    position: static;
  }
  
  .subsections {
    grid-template-columns: 1fr;
  }
  
  .back-link {
    position: static;
    margin-bottom: 15px;
    justify-content: center;
  }
  
  .page-header {
    margin-bottom: 20px;
  }
}
</style> 