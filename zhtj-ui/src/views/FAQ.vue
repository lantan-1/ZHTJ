<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Search, Plus, Minus, QuestionFilled } from '@element-plus/icons-vue';

const router = useRouter();
const searchQuery = ref('');
const isLoading = ref(false);

// 常见问题分类
const categories = ref([
  { id: 'all', name: '全部问题', icon: 'Document' },
  { id: 'account', name: '账号相关', icon: 'User' },
  { id: 'function', name: '功能使用', icon: 'Tools' },
  { id: 'org', name: '组织管理', icon: 'OfficeBuilding' },
  { id: 'activity', name: '活动管理', icon: 'Calendar' },
  { id: 'system', name: '系统设置', icon: 'Setting' },
]);

// 当前选中的分类
const activeCategory = ref('all');

// 常见问题列表
const faqList = ref([
  { 
    id: 1, 
    category: 'account', 
    question: '如何修改我的密码？', 
    answer: '您可以在"系统设置"中点击"修改密码"，填写原密码和新密码后保存即可修改密码。修改密码后，系统会要求您重新登录。',
    expanded: false
  },
  { 
    id: 2, 
    category: 'account', 
    question: '忘记密码怎么办？', 
    answer: '如果您忘记了密码，可以在登录页面点击"忘记密码"，通过身份证号和手机号验证后重置密码。',
    expanded: false
  },
  { 
    id: 3, 
    category: 'function', 
    question: '如何查看我的组织信息？', 
    answer: '在系统首页，您可以看到您所属组织的基本信息卡片。点击卡片可以查看更详细的组织信息。',
    expanded: false
  },
  { 
    id: 4, 
    category: 'function', 
    question: '如何添加新的团建活动？', 
    answer: '在"活动管理"页面，点击右上角的"新建活动"按钮，填写活动信息后点击保存即可创建新的团建活动。',
    expanded: false
  },
  { 
    id: 5, 
    category: 'org', 
    question: '如何管理团员信息？', 
    answer: '在"成员管理"页面，您可以查看、编辑和管理您组织内的团员信息。支持批量导入和导出功能。',
    expanded: false
  },
  { 
    id: 6, 
    category: 'org', 
    question: '如何进行组织关系转接？', 
    answer: '在"组织关系转接"页面，您可以发起或处理组织关系转接申请。填写相关信息后提交审核即可。',
    expanded: false
  },
  { 
    id: 7, 
    category: 'activity', 
    question: '如何查看活动参与情况？', 
    answer: '在"活动管理"页面，点击具体活动后可以查看该活动的参与情况，包括签到记录和评价信息。',
    expanded: false
  },
  { 
    id: 8, 
    category: 'system', 
    question: '如何设置默认组织？', 
    answer: '在"系统设置"页面，选择"设置默认组织"选项，从下拉菜单中选择您希望设为默认的组织，然后点击保存即可。',
    expanded: false
  },
  { 
    id: 9, 
    category: 'system', 
    question: '系统支持哪些浏览器？', 
    answer: '智慧团建系统支持Chrome、Firefox、Edge等现代浏览器，建议使用最新版本以获得最佳体验。',
    expanded: false
  },
  { 
    id: 10, 
    category: 'account', 
    question: '如何修改我的个人信息？', 
    answer: '您可以在"系统设置"中选择"基本信息"选项卡，修改您的个人信息后点击保存即可。注意，某些基本信息（如身份证号）是无法修改的。',
    expanded: false
  },
  { 
    id: 11, 
    category: 'function', 
    question: '如何导出我的数据？', 
    answer: '在各个功能模块（如活动管理、成员管理等）中，通常会有导出按钮，点击后可以将当前数据导出为Excel格式。',
    expanded: false
  },
  { 
    id: 12, 
    category: 'activity', 
    question: '活动删除后能恢复吗？', 
    answer: '一般情况下，删除的活动无法直接恢复。因此，在删除活动前请确认这是您真正想要的操作。如果您误删了重要活动，请联系管理员寻求帮助。',
    expanded: false
  },
]);

// 根据分类筛选FAQ
const filteredFaq = computed(() => {
  let result = faqList.value;
  
  // 按分类筛选
  if (activeCategory.value !== 'all') {
    result = result.filter(item => item.category === activeCategory.value);
  }
  
  // 按搜索关键词筛选
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase().trim();
    result = result.filter(
      item => item.question.toLowerCase().includes(query) || 
              item.answer.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// 搜索FAQ
const searchFaq = () => {
  isLoading.value = true;
  
  // 模拟搜索延迟
  setTimeout(() => {
    isLoading.value = false;
  }, 300);
};

// 切换FAQ分类
const changeCategory = (categoryId) => {
  activeCategory.value = categoryId;
  // 重置展开状态
  faqList.value.forEach(item => item.expanded = false);
};

// 展开/折叠FAQ
const toggleFaq = (faqId) => {
  const faq = faqList.value.find(item => item.id === faqId);
  if (faq) {
    faq.expanded = !faq.expanded;
  }
};

// 前往帮助中心
const goToHelpCenter = () => {
  router.push('/dashboard/help');
};

// 前往联系我们
const goToContact = () => {
  router.push('/dashboard/contact');
};

onMounted(() => {
  // 可以在这里加载初始数据
});
</script>

<template>
  <div class="faq-container">
    <div class="page-header">
      <h1>常见问题</h1>
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索问题..."
          class="search-input"
          clearable
          @keyup.enter="searchFaq"
          @clear="searchFaq"
          @input="searchFaq"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>
    
    <div class="faq-content">
      <div class="faq-sidebar">
        <div class="category-header">问题分类</div>
        <ul class="category-list">
          <li 
            v-for="category in categories" 
            :key="category.id"
            :class="{ active: category.id === activeCategory }"
            @click="changeCategory(category.id)"
          >
            <el-icon><component :is="category.icon" /></el-icon>
            <span>{{ category.name }}</span>
            <span class="category-count" v-if="category.id === 'all'">
              {{ faqList.length }}
            </span>
            <span class="category-count" v-else>
              {{ faqList.filter(item => item.category === category.id).length }}
            </span>
          </li>
        </ul>
        
        <div class="sidebar-actions">
          <div class="action-title">需要更多帮助？</div>
          <div class="action-buttons">
            <el-button type="primary" @click="goToHelpCenter">
              查看使用手册
            </el-button>
            <el-button plain @click="goToContact">
              联系客服
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="faq-main">
        <div class="main-header">
          <h2>
            {{ activeCategory === 'all' ? '全部问题' : 
               categories.find(c => c.id === activeCategory)?.name || '常见问题' }}
          </h2>
          <div class="result-count" v-if="filteredFaq.length > 0">
            找到 {{ filteredFaq.length }} 个问题
          </div>
        </div>
        
        <div v-loading="isLoading" class="faq-list">
          <div 
            v-for="faq in filteredFaq" 
            :key="faq.id"
            class="faq-item"
            :class="{ expanded: faq.expanded }"
          >
            <div class="faq-question" @click="toggleFaq(faq.id)">
              <div class="question-icon">
                <el-icon><QuestionFilled /></el-icon>
              </div>
              <div class="question-text">{{ faq.question }}</div>
              <div class="question-toggle">
                <el-icon v-if="!faq.expanded"><Plus /></el-icon>
                <el-icon v-else><Minus /></el-icon>
              </div>
            </div>
            <div class="faq-answer" v-show="faq.expanded">
              {{ faq.answer }}
            </div>
          </div>
          
          <el-empty 
            v-if="filteredFaq.length === 0" 
            description="未找到相关问题"
          >
            <template #extra>
              <el-button type="primary" @click="goToContact">联系客服</el-button>
            </template>
          </el-empty>
        </div>
        
        <div class="faq-footer">
          <div class="footer-text">
            没有找到您需要的答案？
          </div>
          <div class="footer-actions">
            <el-button type="primary" @click="goToContact">
              联系客服
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.faq-container {
  padding: 20px;
}

.page-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 20px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 500;
  margin: 0;
  color: #303133;
}

.search-bar {
  width: 300px;
}

.search-input {
  width: 100%;
}

.faq-content {
  display: flex;
  gap: 20px;
  min-height: 600px;
}

.faq-sidebar {
  width: 250px;
  flex-shrink: 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.category-header {
  padding: 16px 20px;
  font-size: 16px;
  font-weight: 500;
  border-bottom: 1px solid #e4e7ed;
  color: #303133;
}

.category-list {
  list-style: none;
  padding: 10px 0;
  margin: 0;
}

.category-list li {
  padding: 12px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.category-list li:hover {
  background-color: #f5f7fa;
}

.category-list li.active {
  background-color: #f0f6ff;
  color: #c62828;
  font-weight: 500;
}

.category-list li .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

.category-count {
  position: absolute;
  right: 20px;
  background-color: #f0f0f0;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: #606266;
}

.category-list li.active .category-count {
  background-color: #c62828;
  color: white;
}

.sidebar-actions {
  margin-top: auto;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}

.action-title {
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.faq-main {
  flex: 1;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.main-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.main-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.result-count {
  font-size: 14px;
  color: #909399;
}

.faq-list {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.faq-item {
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.faq-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.faq-item.expanded {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.faq-question {
  padding: 16px 20px;
  background-color: #f8f8f8;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

.faq-item.expanded .faq-question {
  background-color: #f0f6ff;
}

.faq-question:hover {
  background-color: #f0f0f0;
}

.question-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #c62828;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.question-text {
  flex: 1;
  font-weight: 500;
}

.question-toggle {
  margin-left: 10px;
}

.faq-answer {
  padding: 15px 20px;
  background-color: #f9f9f9;
  color: #606266;
  line-height: 1.6;
  border-top: 1px solid #ebeef5;
  transition: all 0.3s;
  overflow: hidden;
  text-align: left;
  text-indent: 2em;
}

.faq-footer {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  background-color: #f8f8f8;
}

.footer-text {
  font-size: 14px;
  color: #606266;
}

@media (max-width: 768px) {
  .faq-content {
    flex-direction: column;
  }
  
  .faq-sidebar {
    width: 100%;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .search-bar {
    width: 100%;
  }
}
</style> 