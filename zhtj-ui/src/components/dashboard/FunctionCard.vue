<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../stores/user';
import { 
  Calendar, Star, Trophy, CirclePlus, User, 
  Setting, Connection, Check, Briefcase, 
  AddLocation, EditPen, Document, Search,
  Collection, ChatLineRound, Menu
} from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

// 定义功能项的接口
interface FunctionItem {
  title: string;
  icon: any; // 使用Element Plus图标组件
  link: string;
  visible: boolean;
  color?: string; // 可选图标背景颜色
}

// 根据用户角色计算可见功能项
const functionItems = computed(() => {
  // 所有功能项，通过visible属性控制可见性
  const items: FunctionItem[] = [
    // 团员、团支书、团委书记共用功能
    {
      title: '关系转接',
      icon: Connection,
      link: '/dashboard/transfers',
      visible: true,
      color: '#f56c6c'
    },
    {
      title: '年度团籍注册',
      icon: Check,
      link: '/dashboard/register',
      visible: true,
      color: '#67c23a'
    },
    {
      title: '团员教育评议',
      icon: Star,
      link: '/dashboard/evaluations',
      visible: true,
      color: '#e6a23c'
    },
    
    // 团支书和团委书记专有功能
    {
      title: '录入团员',
      icon: CirclePlus,
      link: '/dashboard/members/add',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#409eff'
    },
    {
      title: '团员信息',
      icon: User,
      link: '/dashboard/members',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#409eff'
    },
    {
      title: '团干部信息',
      icon: User,
      link: '/dashboard/cadres',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#67c23a'
    },
    {
      title: '录入团干部',
      icon: CirclePlus,
      link: '/dashboard/cadres/add',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#67c23a'
    },
    {
      title: '录入下级团干部',
      icon: CirclePlus,
      link: '/dashboard/branch-cadres/add',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#67c23a'
    },
    {
      title: '团干部职务变更',
      icon: EditPen,
      link: '/dashboard/cadre-position',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#67c23a'
    },
    
    // 组织关系转接相关
    {
      title: '组织关系接转审批',
      icon: Connection,
      link: '/dashboard/transfers/approve',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#409eff'
    },
    
    // 注册相关
    {
      title: '共青团员注册审批',
      icon: Check,
      link: '/dashboard/member-register/approve',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#409eff'
    },
    {
      title: '团干部注册审批',
      icon: Check,
      link: '/dashboard/cadre-register/approve',
      visible: userStore.isBranchSecretary || userStore.isCommitteeSecretary,
      color: '#67c23a'
    },
    
    // 团委书记专属功能
    {
      title: '创建下级组织',
      icon: AddLocation,
      link: '/dashboard/organizations/create',
      visible: userStore.isCommitteeSecretary,
      color: '#e6a23c'
    },
    {
      title: '管理下级组织',
      icon: Setting,
      link: '/dashboard/organizations',
      visible: userStore.isCommitteeSecretary,
      color: '#e6a23c'
    },
    
    // 公共功能
    {
      title: '会议活动',
      icon: Calendar,
      link: '/dashboard/activities',
      visible: true,
      color: '#f56c6c'
    },
    {
      title: '下级组织会议活动',
      icon: Calendar,
      link: '/dashboard/branch-meetings',
      visible: userStore.isCommitteeSecretary,
      color: '#f56c6c'
    },
    {
      title: '全局搜索',
      icon: Search,
      link: '/dashboard/search',
      visible: true,
      color: '#909399'
    }
  ];
  
  // 过滤出当前用户可见的功能项
  return items.filter(item => item.visible);
});

// 处理功能卡片点击
const handleFunctionClick = (link: string) => {
  // 提取路由名称，用于设置activeIndex
  const path = link.split('/');
  const routeName = path[path.length - 1];
  
  // 特殊处理共青团员注册审批
  if (link === '/dashboard/member-register/approve') {
    // 使用对象形式跳转并添加查询参数
    router.push({
      path: link,
      query: { status: 'pending' }
    }).catch((err: any) => {
      if (err.name !== 'NavigationDuplicated') {
        console.error('导航错误:', err);
      }
    });
    return;
  }
  
  // 使用router.push进行导航
  router.push(link).catch((err: any) => {
    if (err.name !== 'NavigationDuplicated') {
      console.error('导航错误:', err);
    }
  });
  
  console.log('功能卡片点击，导航到:', link);
};
</script>

<template>
  <div class="function-cards">
    <!-- 主要功能模块，大尺寸显示 -->
    <el-row :gutter="25" class="main-functions">
      <el-col :span="24/5" class="card-col">
        <div class="function-card main-card" @click="handleFunctionClick('/dashboard/transfers')">
          <div class="card-icon" style="backgroundColor: #f56c6c20">
            <el-icon color="#f56c6c" class="icon">
              <Connection />
            </el-icon>
          </div>
          <div class="card-title">关系转接</div>
        </div>
      </el-col>
      
      <el-col :span="24/5" class="card-col">
        <div class="function-card main-card" @click="handleFunctionClick('/dashboard/register')">
          <div class="card-icon" style="backgroundColor: #67c23a20">
            <el-icon color="#67c23a" class="icon">
              <Check />
            </el-icon>
          </div>
          <div class="card-title">年度团籍注册</div>
        </div>
      </el-col>
      
      <el-col :span="24/5" class="card-col">
        <div class="function-card main-card" @click="handleFunctionClick('/dashboard/evaluations')">
          <div class="card-icon" style="backgroundColor: #e6a23c20">
            <el-icon color="#e6a23c" class="icon">
              <Star />
            </el-icon>
          </div>
          <div class="card-title">团员教育评议</div>
        </div>
      </el-col>
      
      <el-col :span="24/5" class="card-col">
        <div class="function-card main-card" @click="handleFunctionClick('/dashboard/activities')">
          <div class="card-icon" style="backgroundColor: #f56c6c20">
            <el-icon color="#f56c6c" class="icon">
              <Calendar />
            </el-icon>
          </div>
          <div class="card-title">会议活动</div>
        </div>
      </el-col>
      
      <el-col :span="24/5" class="card-col">
        <div class="function-card main-card" @click="handleFunctionClick('/dashboard/search')">
          <div class="card-icon" style="backgroundColor: #90939920">
            <el-icon color="#909399" class="icon">
              <Search />
            </el-icon>
          </div>
          <div class="card-title">全局搜索</div>
        </div>
      </el-col>
    </el-row>

    <!-- 其他功能模块，按原样显示 -->
    <el-row :gutter="20" class="other-functions">
      <el-col 
        v-for="(item, index) in functionItems.filter(item => 
          !['关系转接', '年度团籍注册', '团员教育评议', '会议活动', '全局搜索'].includes(item.title))" 
        :key="index" 
        :xs="12" 
        :sm="8" 
        :md="6" 
        :lg="4" 
        class="card-col"
      >
        <div class="function-card" @click="handleFunctionClick(item.link)">
          <div class="card-icon" :style="{ backgroundColor: item.color ? `${item.color}20` : '#ffd9d9' }">
            <el-icon :color="item.color || '#c62828'" class="icon">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="card-title">{{ item.title }}</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.function-cards {
  margin-top: 25px;
}

.main-functions {
  margin-bottom: 0px;
}

.other-functions {
  margin-top: 0px;
}

.card-col {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
}

.function-card {
  background-color: white;
  border-radius: 8px;
  width: 120px;
  height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s;
  margin: 0 auto;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.main-card {
  width: 150px;
  height: 150px;
}

.function-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 12px;
}

.main-card .card-icon {
  width: 80px;
  height: 80px;
}

.main-card .icon {
  font-size: 36px;
}

.icon {
  font-size: 30px;
}

.card-title {
  font-size: 14px;
  color: #333;
  text-align: center;
}

.main-card .card-title {
  font-size: 16px;
  font-weight: 500;
  margin-top: 5px;
}
</style> 