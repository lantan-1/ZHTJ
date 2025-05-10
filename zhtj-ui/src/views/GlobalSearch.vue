<template>
  <div class="global-search-container">
    <div class="page-header">
      <h1>全局搜索</h1>
      <p class="description">在系统内容中查找您需要的信息</p>
    </div>

    <el-card class="search-card">
      <div class="search-input-container">
        <el-input
          v-model="searchQuery"
          placeholder="请输入搜索关键词"
          clearable
          @keyup.enter="performSearch"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="performSearch" type="primary">搜索</el-button>
          </template>
        </el-input>
      </div>

      <div class="filter-options">
        <el-checkbox-group v-model="selectedCategories">
          <el-checkbox label="all">全部</el-checkbox>
          <el-checkbox label="activities">活动会议</el-checkbox>
          <el-checkbox label="evaluations">教育评议</el-checkbox>
          <el-checkbox label="members">团员信息</el-checkbox>
          <el-checkbox label="organizations">组织管理</el-checkbox>
          <el-checkbox label="honors">荣誉管理</el-checkbox>
          <el-checkbox label="notifications">消息通知</el-checkbox>
          <el-checkbox label="study">学习资料</el-checkbox>
          <el-checkbox label="settings">系统设置</el-checkbox>
          <el-checkbox label="help">帮助中心</el-checkbox>
        </el-checkbox-group>
      </div>
    </el-card>

    <el-empty v-if="searchResults.length === 0 && searched" description="没有找到匹配的结果" />

    <div v-if="searchResults.length > 0" class="search-results">
      <h2>搜索结果 ({{ searchResults.length }})</h2>
      
      <el-card v-for="(result, index) in searchResults" :key="index" class="result-card">
        <div class="result-header">
          <div class="result-category">{{ getCategoryLabel(result.category) }}</div>
          <div class="result-title" @click="navigateToResult(result)">{{ result.title }}</div>
        </div>
        <div class="result-content" v-html="highlightKeyword(result.content)"></div>
        <div class="result-footer">
          <span class="result-date">{{ result.date }}</span>
          <el-button type="primary" link @click="navigateToResult(result)">
            查看详情
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { Search, ArrowRight } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 搜索状态
const searchQuery = ref('');
const selectedCategories = ref(['all']);
const searchResults = ref<any[]>([]);
const searched = ref(false);

// 用于路由导航
const router = useRouter();

// 模拟的搜索数据（实际项目中可以扩展这个数据集）
const searchData = [
  // 个人信息
  {
    id: 1,
    title: '个人信息与组织资料',
    content: '查看和编辑您的基本信息、联系方式、组织关系、入团时间、团员状态等。',
    category: 'members',
    date: '2023-12-01',
    url: '/dashboard/profile'
  },
  // 团员管理
  {
    id: 2,
    title: '团员信息管理',
    content: '管理本组织所有团员的基本信息、团员状态、团员发展、团员注册等。',
    category: 'members',
    date: '2023-12-02',
    url: '/dashboard/member-register/approve?status=activated'
  },
  {
    id: 3,
    title: '团干部信息管理',
    content: '录入、查询和变更团干部信息，支持干部任职、离任、干部职务变更等操作。',
    category: 'members',
    date: '2023-12-03',
    url: '/dashboard/member-register/approve?status=activated'
  },
  // 活动管理
  {
    id: 4,
    title: '会议活动管理',
    content: '发布、管理和统计团组织的会议、活动、主题团日等，支持活动报名、签到、总结。',
    category: 'activities',
    date: '2023-12-04',
    url: '/dashboard/activities'
  },
  {
    id: 5,
    title: '活动详情与参与',
    content: '查看活动详情、参与活动、查看活动总结和参与名单。',
    category: 'activities',
    date: '2023-12-05',
    url: '/dashboard/activities'
  },
  // 荣誉管理
  {
    id: 6,
    title: '荣誉申请与审批',
    content: '申请个人荣誉、查看荣誉申请进度、审批团员荣誉、查看荣誉获奖名单。',
    category: 'honors',
    date: '2023-12-06',
    url: '/dashboard/honor-apply'
  },
  {
    id: 7,
    title: '我的荣誉',
    content: '查看自己获得的所有荣誉、荣誉证书下载、荣誉历史记录。',
    category: 'honors',
    date: '2023-12-07',
    url: '/dashboard/my-honors'
  },
  // 志愿服务
  {
    id: 8,
    title: '志愿服务记录',
    content: '添加、管理和统计个人及组织的志愿服务活动，支持服务时长统计和服务证明下载。',
    category: 'activities',
    date: '2023-12-08',
    url: '/dashboard/volunteer-services'
  },
  // 通知
  {
    id: 9,
    title: '消息通知',
    content: '系统消息、审批通知、活动提醒、荣誉获奖通知等，支持已读未读管理。',
    category: 'notifications',
    date: '2023-12-09',
    url: '/dashboard/notifications'
  },
  // 组织管理
  {
    id: 10,
    title: '组织信息与结构',
    content: '查看本级及下级团组织信息、组织架构、组织关系转接、组织成员管理。',
    category: 'organizations',
    date: '2023-12-10',
    url: '/dashboard/member-register/approve?status=activated'
  },
  {
    id: 11,
    title: '组织关系转接',
    content: '发起、审批和跟踪团员组织关系转接流程，支持转入转出、审批日志查询。',
    category: 'organizations',
    date: '2023-12-11',
    url: '/dashboard/transfers'
  },
  // 教育评议
  {
    id: 12,
    title: '团员教育评议',
    content: '参与年度团员教育评议，查看评议结果、评议统计、评议活动详情。',
    category: 'evaluations',
    date: '2023-12-12',
    url: '/dashboard/evaluations'
  },
  // 学习资源
  {
    id: 13,
    title: '团课资源',
    content: '学习团的历史、团章、团务知识、团课视频等，提升团员理论素养。',
    category: 'study',
    date: '2023-12-13',
    url: '/dashboard/course-resources'
  },
  {
    id: 14,
    title: '团务百科',
    content: '查阅团务相关政策、制度、操作流程、常见问题解答。',
    category: 'study',
    date: '2023-12-14',
    url: '/dashboard/league-wiki'
  },
  // 帮助中心
  {
    id: 15,
    title: '帮助中心',
    content: '系统使用手册、常见问题、联系方式、在线客服等，帮助用户快速上手。',
    category: 'help',
    date: '2023-12-15',
    url: '/dashboard/help'
  },
  // 其他
  {
    id: 16,
    title: '系统设置',
    content: '修改个人密码、设置通知偏好、管理账号安全等。',
    category: 'settings',
    date: '2023-12-16',
    url: '/dashboard/settings'
  },
  {
    id: 17,
    title: '全局搜索',
    content: '在系统所有模块和页面中进行关键词搜索，快速定位所需信息。',
    category: 'help',
    date: '2023-12-17',
    url: '/dashboard/search'
  },
  // 示例：活动、荣誉、通知等可继续扩展
  // ...
];

// 监听分类选择变化
watch(selectedCategories, (newVal, oldVal) => {
  // 如果点击了"全部"，只保留"全部"
  if (newVal.includes('all') && oldVal && !oldVal.includes('all')) {
    selectedCategories.value = ['all'];
  }
  // 如果没有任何选项，自动选"全部"
  else if (newVal.length === 0) {
    selectedCategories.value = ['all'];
  }
  // 只要选了其他分类，"全部"就取消
  else if (newVal.includes('all') && newVal.length > 1) {
    selectedCategories.value = newVal.filter(val => val !== 'all');
  }
});

// 执行搜索
const performSearch = () => {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入搜索关键词');
    return;
  }

  const keyword = searchQuery.value.toLowerCase().trim();
  let filteredResults;

  // 根据选择的分类过滤
  if (selectedCategories.value.includes('all')) {
    filteredResults = searchData;
  } else {
    filteredResults = searchData.filter(item => 
      selectedCategories.value.includes(item.category)
    );
  }

  // 根据关键词搜索
  searchResults.value = filteredResults.filter(item => 
    item.title.toLowerCase().includes(keyword) || 
    item.content.toLowerCase().includes(keyword)
  );

  searched.value = true;
};

// 获取分类标签
const getCategoryLabel = (category: string) => {
  const labels: Record<string, string> = {
    'activities': '活动会议',
    'evaluations': '教育评议',
    'members': '团员信息',
    'organizations': '组织管理',
    'honors': '荣誉管理',
    'notifications': '消息通知',
    'study': '学习资料',
    'settings': '系统设置',
    'help': '帮助中心'
  };
  return labels[category] || '其他';
};

// 高亮关键词
const highlightKeyword = (text: string) => {
  if (!searchQuery.value.trim()) return text;
  
  const keyword = searchQuery.value.trim();
  const regex = new RegExp(keyword, 'gi');
  return text.replace(regex, match => `<span class="highlight">${match}</span>`);
};

// 导航到结果页面
const navigateToResult = (result: any) => {
  router.push(result.url);
};
</script>

<style scoped>
.global-search-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}

.description {
  color: #666;
  font-size: 14px;
}

.search-card {
  margin-bottom: 30px;
}

.search-input-container {
  margin-bottom: 15px;
}

.search-input {
  width: 100%;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.search-results {
  margin-top: 30px;
}

.search-results h2 {
  font-size: 18px;
  margin-bottom: 20px;
  color: #333;
}

.result-card {
  margin-bottom: 15px;
  transition: all 0.3s;
}

.result-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.result-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.result-category {
  background-color: #f0f2f5;
  color: #606266;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 10px;
}

.result-title {
  font-size: 16px;
  font-weight: 500;
  color: #409EFF;
  cursor: pointer;
}

.result-title:hover {
  text-decoration: underline;
}

.result-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
}

.result-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 13px;
}

.result-date {
  color: #909399;
}

:deep(.highlight) {
  background-color: #FFEB3B;
  padding: 0 1px;
  border-radius: 2px;
  font-weight: bold;
}
</style> 