<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useUserStore } from '../../stores/user';
import { ElMessage } from 'element-plus';

const userStore = useUserStore();
const isLoading = ref(true);

// 简化数据处理 - 直接从store获取值，不添加默认值
const userName = computed(() => userStore.name);
const gender = computed(() => userStore.gender);
const ethnicity = computed(() => userStore.ethnic); 
const card = computed(() => userStore.card);
const phone = computed(() => userStore.phone);
const email = computed(() => userStore.email);
const occupation = computed(() => userStore.occupation);
const politicalStatus = computed(() => userStore.politicalStatus);
const educationLevel = computed(() => userStore.educationLevel);
const educationStatus = computed(() => userStore.educationStatus);

// 根据身份证号计算年龄
const age = computed(() => {
  const card = userStore.card;
  if (!card || card.length < 18) return '';
  
  try {
    // 从身份证获取出生日期 (格式: YYYYMMDD)
    const birthDateStr = card.substring(6, 14);
    const year = parseInt(birthDateStr.substring(0, 4));
    const month = parseInt(birthDateStr.substring(4, 6)) - 1; // 月份从0开始
    const day = parseInt(birthDateStr.substring(6, 8));
    
    const birthDate = new Date(year, month, day);
    const today = new Date();
    
    // 计算年龄
    let age = today.getFullYear() - birthDate.getFullYear();
    
    // 检查是否已过生日
    const isBirthdayPassed = 
      today.getMonth() > birthDate.getMonth() || 
      (today.getMonth() === birthDate.getMonth() && today.getDate() >= birthDate.getDate());
    
    // 如果今年还没过生日，年龄减1
    if (!isBirthdayPassed) {
      age--;
    }
    
    return `${age}岁`;
  } catch (error) {
    console.error('计算年龄出错:', error);
    return '';
  }
});

// 根据入团时间计算团龄
const leagueAge = computed(() => {
  const joinLeagueDate = userStore.joinLeagueDate;
  if (!joinLeagueDate) return '';
  
  try {
    const joinTime = new Date(joinLeagueDate);
    const now = new Date();
    
    // 计算团龄（年）
    let years = now.getFullYear() - joinTime.getFullYear();
    
    // 检查是否过了入团纪念日
    const isAnniversaryPassed = 
      now.getMonth() > joinTime.getMonth() || 
      (now.getMonth() === joinTime.getMonth() && now.getDate() >= joinTime.getDate());
    
    // 如果今年还没过入团纪念日，团龄减1
    if (!isAnniversaryPassed) {
      years--;
    }
    
    return `${years}年`;
  } catch (error) {
    console.error('计算团龄出错:', error);
    return '';
  }
});

// 组件级状态，确保即使store中的数据未更新也能显示正确的值
const localOrgName = ref('');
const localOrgFullName = ref('');
const localOrgType = ref('');
const dataReady = ref(true); // 默认设置为true，确保总是显示表单

// 从本地存储恢复组织信息
const restoreFromLocalStorage = () => {
  try {
    const savedOrgInfo = localStorage.getItem('org_info');
    if (savedOrgInfo) {
      const orgInfo = JSON.parse(savedOrgInfo);
      console.log('从本地存储恢复组织信息:', orgInfo);
      
      if (orgInfo.orgName) localOrgName.value = orgInfo.orgName;
      if (orgInfo.orgFullName) localOrgFullName.value = orgInfo.orgFullName;
      if (orgInfo.orgType) localOrgType.value = orgInfo.orgType;
      
      // 同时更新store
      userStore.$patch({
        organization: orgInfo.orgName || '',
        organizationFullName: orgInfo.orgFullName || '',
        organizationType: orgInfo.orgType || ''
      });
      
      return true;
    }
  } catch (e) {
    console.error('恢复本地存储的组织信息失败:', e);
  }
  return false;
};

// 保存组织信息到本地存储
const saveToLocalStorage = (orgName: string, orgFullName: string, orgType: string) => {
  try {
    const orgInfo = {
      orgName,
      orgFullName,
      orgType,
      timestamp: new Date().getTime() // 添加时间戳便于判断数据新鲜度
    };
    localStorage.setItem('org_info', JSON.stringify(orgInfo));
    console.log('组织信息已保存到本地存储');
  } catch (e) {
    console.error('保存组织信息到本地存储失败:', e);
  }
};

// 重新定义计算属性，优先使用组件级状态
const organization = computed(() => localOrgName.value || userStore.organization);
const organizationFullName = computed(() => localOrgFullName.value || userStore.organizationFullName);
const organizationType = computed(() => localOrgType.value || userStore.organizationType);

// 详细信息：组织全称+（组织类型）
const organizationDetail = computed(() => {
  const fullName = organizationFullName.value;
  const type = organizationType.value;
  
  if (fullName && type) {
    return `${fullName}（${type}）`;
  } else if (fullName) {
    return fullName;
  } else if (type) {
    return `（${type}）`;
  } else {
    return '';
  }
});

// 格式化入团日期
const formattedJoinDate = computed(() => {
  const date = userStore.joinLeagueDate;
  if (!date) return '';
  
  try {
    // 假设日期格式为 "YYYY-MM-DD"
    return date; // 或者可以进一步格式化为 "YYYY年MM月DD日"
  } catch (error) {
    console.error('格式化入团日期错误:', error);
    return date;
  }
});

// 添加计算属性用于调试 - 显示所有用户数据
const userDebugInfo = computed(() => {
  return {
    name: userStore.name,
    gender: userStore.gender,
    ethnic: userStore.ethnic,
    card: userStore.card,
    calculatedAge: age.value,
    joinLeagueDate: userStore.joinLeagueDate,
    calculatedLeagueAge: leagueAge.value,
    organization: userStore.organization,
    organizationFullName: userStore.organizationFullName,
    organizationType: userStore.organizationType,
    organizationDetail: organizationDetail.value,
    phone: userStore.phone,
    email: userStore.email,
    politicalStatus: userStore.politicalStatus,
    occupation: userStore.occupation,
    educationLevel: userStore.educationLevel,
    educationStatus: userStore.educationStatus
  };
});

// 在组件挂载时确保用户信息已加载
onMounted(async () => {
  try {
    isLoading.value = true;
    console.log('%cMemberInfoCard组件初始化', 'color:green;font-weight:bold');
    console.log('当前用户信息:', {
      name: userStore.name,
      gender: userStore.gender,
      card: userStore.card ? userStore.card.substring(0, 4) + '****' : '无',
      organization: userStore.organization
    });
    
    // 始终设置dataReady为true
    dataReady.value = true;
    
    // 获取token并刷新用户信息
    const token = localStorage.getItem('token');
    if (token) {
      console.log('发现token，准备获取用户信息...');
      
      try {
        // 记录开始时间，用于性能分析
        const startTime = performance.now();
        
        // 直接使用userStore的方法获取用户信息
        const userInfo = await userStore.fetchUserInfo();
        
        // 记录完成时间
        const endTime = performance.now();
        console.log(`用户信息获取完成，耗时 ${Math.round(endTime - startTime)}ms`);
        
        if (userInfo) {
          console.log('获取到用户信息：', {
            name: userInfo.name,
            organization: userInfo.organization_name
          });
        } else {
          console.warn('没有获取到用户信息数据，但仍然显示表单');
          // 即使获取不到数据，依然保持dataReady为true
        }
        
        // 确保在完成后设置loading状态为false
        isLoading.value = false;
      } catch (error) {
        console.error('获取用户信息失败:', error);
        ElMessage.error('获取用户信息失败，请刷新页面重试');
        isLoading.value = false;
        // 即使出错，仍然保持dataReady为true，显示表单
      }
    } else {
      console.warn('未找到token，无法获取用户信息，但仍然显示表单');
      isLoading.value = false;
      // 即使没有token，仍然保持dataReady为true，显示表单
    }
  } catch (error) {
    console.error('组件初始化失败:', error);
    ElMessage.error('组件初始化失败，请刷新页面重试');
    isLoading.value = false;
    // 即使初始化失败，仍然保持dataReady为true，显示表单
  }
});
</script>

<template>
  <el-card shadow="hover" class="info-card" v-loading="isLoading">
    <template #header>
      <div class="card-header">
        <h3>团员基本资料</h3>
      </div>
    </template>
    
    <el-descriptions :column="3" border size="small" v-if="dataReady" class="compact-descriptions">
      <!-- 按用户提供的顺序排列字段 -->
      <!-- 第一列 -->
      <el-descriptions-item label="姓名">{{ userName || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ gender || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="身份证号">{{ card || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="民族">{{ ethnicity || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="年龄">{{ age || '暂无数据' }}</el-descriptions-item>

      <!-- 第二列 -->
      <el-descriptions-item label="联系电话">{{ phone || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="政治面貌">{{ politicalStatus || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="职业">{{ occupation || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="电子邮箱">{{ email || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="教育水平">{{ educationLevel || '暂无数据' }}</el-descriptions-item>

      <!-- 第三列 -->
      <el-descriptions-item label="教育状态">{{ educationStatus || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="所在组织">{{ organization || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="入团日期">{{ formattedJoinDate || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="团龄">{{ leagueAge || '暂无数据' }}</el-descriptions-item>
      <el-descriptions-item label="组织信息" :span="3">{{ organizationDetail || '暂无数据' }}</el-descriptions-item>
    </el-descriptions>
    <div v-else-if="!isLoading" class="no-data">
      <el-empty description="暂无用户信息" />
    </div>
  </el-card>
</template>

<style scoped>
.info-card {
  height: 100%;
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
  color: #c62828;
}

.no-data {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 150px;
}

:deep(.el-descriptions__label) {
  width: 80px;
  color: #606266;
  padding: 5px 6px !important;
  font-size: 13px;
  font-weight: bold;
  background-color: #f0f8ff; /* 蓝色背景 */
}

:deep(.el-descriptions__content) {
  width: 100px;
  padding: 5px 8px !important;
  font-size: 13px;
}

:deep(.el-descriptions__body) {
  margin-top: 0;
  margin-bottom: 0;
}

:deep(.el-descriptions__cell) {
  padding: 4px 2px;
}

:deep(.el-card__header) {
  padding: 8px 15px;
}

:deep(.el-card__body) {
  padding: 12px 15px;
}
</style> 