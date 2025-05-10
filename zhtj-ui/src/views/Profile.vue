<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useUserStore } from '../stores/user';
import { useRouter } from 'vue-router';
import { updateUserInfo, updateUserPassword, deleteUser, type User } from '../api/user';
import { UserFilled, Key, Lock, Warning, OfficeBuilding } from '@element-plus/icons-vue';

const router = useRouter();
const userStore = useUserStore();

// 用户信息表单
const userForm = reactive<User>({
  card: '',
  name: '',
  tel: '',
  sex: '',
  birthday: '',
  organization: 0
});

// 修改密码表单
const pwdForm = reactive({
  oldPwd: '',
  newPwd: '',
  confirmPwd: ''
});

// 默认组织设置
const defaultOrgForm = reactive({
  organization: '',
});

// 组织选项模拟数据
const orgOptions = ref([
  { label: '计算机学院团委', value: '1' },
  { label: '机械工程学院团委', value: '2' },
  { label: '经济管理学院团委', value: '3' },
  { label: '土木工程学院团委', value: '4' },
]);

// 激活的标签页
const activeTab = ref('info');

// 加载状态
const infoLoading = ref(false);
const pwdLoading = ref(false);
const deleteLoading = ref(false);
const orgLoading = ref(false);

// 表单引用
const infoFormRef = ref();
const pwdFormRef = ref();
const orgFormRef = ref();

// 信息表单验证规则
const infoRules = {
  tel: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthday: [{ required: true, message: '请选择出生日期', trigger: 'change' }]
};

// 密码表单验证规则
const pwdRules = {
  oldPwd: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== pwdForm.newPwd) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
};

// 默认组织表单验证规则
const orgRules = {
  organization: [
    { required: true, message: '请选择默认组织', trigger: 'change' }
  ]
};

// 组织名称
const organizationName = computed(() => {
  console.log('organizationName computed, current value:', userStore.organization);
  return userStore.organization || '电子工程学院团委';
});

// 组织全称
const organizationFullName = computed(() => {
  console.log('organizationFullName computed, current value:', userStore.organizationFullName);
  return userStore.organizationFullName || '清华大学电子工程学院团委';
});

// 组织类型
const organizationType = computed(() => {
  console.log('organizationType computed, current value:', userStore.organizationType);
  return userStore.organizationType || '高校';
});

// 密码强度
const passwordStrength = computed(() => {
  const pwd = pwdForm.newPwd;
  if (!pwd) return 0;
  
  let score = 0;
  
  // 长度检查
  if (pwd.length >= 8) score += 1;
  if (pwd.length >= 10) score += 1;
  
  // 包含数字
  if (/\d/.test(pwd)) score += 1;
  
  // 包含小写字母
  if (/[a-z]/.test(pwd)) score += 1;
  
  // 包含大写字母
  if (/[A-Z]/.test(pwd)) score += 1;
  
  // 包含特殊字符
  if (/[^a-zA-Z0-9]/.test(pwd)) score += 1;
  
  return Math.min(5, score);
});

// 密码强度文字
const strengthText = computed(() => {
  const scores = ['', '非常弱', '弱', '一般', '强', '非常强'];
  return scores[passwordStrength.value];
});

// 密码强度颜色
const strengthColor = computed(() => {
  const colors = ['', '#F56C6C', '#E6A23C', '#909399', '#67C23A', '#409EFF'];
  return colors[passwordStrength.value];
});

// 初始化用户信息
const initUserInfo = () => {
  userForm.card = userStore.card;
  userForm.name = userStore.name;
  userForm.tel = userStore.phone || ''; 
  userForm.sex = userStore.gender || ''; 
  userForm.birthday = userStore.birthday || ''; 
  userForm.organization = userStore.organizationId || 0;
  
  // 设置默认组织为当前组织
  const currentOrg = orgOptions.value.find(org => org.label === userStore.organization);
  if (currentOrg) {
    defaultOrgForm.organization = currentOrg.value;
  }
  
  // 确保组织信息已正确加载
  if (!userStore.organizationFullName || !userStore.organizationType) {
    console.log('组织信息不完整，重新获取用户信息');
    userStore.fetchUserInfo();
  }
  
  console.log('初始化后的组织信息:', {
    organization: userStore.organization,
    organizationFullName: userStore.organizationFullName,
    organizationType: userStore.organizationType
  });
};

// 更新用户信息
const updateInfo = async () => {
  if (!infoFormRef.value) return;

  await infoFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      infoLoading.value = true;
      try {
        const result = await updateUserInfo({
          user: userForm,
          name: organizationName.value
        });
        
        if (result) {
          ElMessage.success('个人信息更新成功');
          // 刷新用户信息
          await userStore.fetchUserInfo();
        } else {
          ElMessage.error('个人信息更新失败');
        }
      } catch (error) {
        console.error('更新信息出错', error);
        ElMessage.error('更新过程出现错误');
      } finally {
        infoLoading.value = false;
      }
    } else {
      ElMessage.warning('请完成表单填写');
      return false;
    }
  });
};

// 更新密码
const updatePwd = async () => {
  if (!pwdFormRef.value) return;

  await pwdFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      pwdLoading.value = true;
      try {
        // 这里应该先验证旧密码是否正确，但API中没有提供验证接口
        // 直接调用更新密码接口
        const result = await updateUserPassword({
          user: {
            card: userForm.card,
            pwd: pwdForm.newPwd
          },
          name: organizationName.value
        });
        
        if (result) {
          ElMessage.success('密码更新成功，请重新登录');
          await userStore.doLogout();
          router.push('/login');
        } else {
          ElMessage.error('密码更新失败');
        }
      } catch (error) {
        console.error('更新密码出错', error);
        ElMessage.error('更新过程出现错误');
      } finally {
        pwdLoading.value = false;
      }
    } else {
      ElMessage.warning('请完成表单填写');
      return false;
    }
  });
};

// 更新默认组织
const updateDefaultOrg = async () => {
  if (!orgFormRef.value) return;

  await orgFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      orgLoading.value = true;
      try {
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 800));
        
        const selectedOrg = orgOptions.value.find(org => org.value === defaultOrgForm.organization);
        
        ElMessage.success(`默认组织已设置为: ${selectedOrg?.label}`);
        // 这里应该调用API更新默认组织
      } catch (error) {
        console.error('更新默认组织出错', error);
        ElMessage.error('设置默认组织失败');
      } finally {
        orgLoading.value = false;
      }
    } else {
      ElMessage.warning('请选择默认组织');
      return false;
    }
  });
};

// 删除账户
const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '此操作将永久删除您的账户，是否继续？', 
      '警告', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    deleteLoading.value = true;
    
    try {
      const result = await deleteUser();
      
      if (result) {
        ElMessage.success('账户已成功删除');
        userStore.resetUserInfo();
        router.push('/login');
      } else {
        ElMessage.error('账户删除失败');
      }
    } catch (error) {
      console.error('删除账户出错', error);
      ElMessage.error('删除过程出现错误');
    } finally {
      deleteLoading.value = false;
    }
  } catch {
    // 用户取消操作
  }
};

// 组件挂载时获取用户信息
onMounted(async () => {
  console.log('Profile组件挂载开始');
  
  if (!userStore.isLoggedIn) {
    console.log('用户未登录，获取用户信息');
    await userStore.fetchUserInfo();
  } else {
    console.log('用户已登录，检查组织信息完整性');
    if (!userStore.organizationFullName || !userStore.organizationType) {
      console.log('组织信息不完整，重新获取');
    await userStore.fetchUserInfo();
  }
  }
  
  // 添加调试输出
  console.log('当前用户组织信息:', {
    organization: userStore.organization,
    organizationFullName: userStore.organizationFullName,
    organizationType: userStore.organizationType
  });
  
  // 手动设置组织信息（临时）
  if (!userStore.organizationFullName) {
    console.log('手动设置组织全称');
    userStore.$patch({
      organizationFullName: '清华大学电子工程学院团委'
    });
  }
  
  if (!userStore.organizationType) {
    console.log('手动设置组织类型');
    userStore.$patch({
      organizationType: '高校'
    });
  }
  
  if (!userStore.organization) {
    console.log('手动设置组织名称');
    userStore.$patch({
      organization: '电子工程学院团委'
    });
  }
  
  initUserInfo();
  
  console.log('Profile组件挂载完成，当前组织信息:', {
    organization: userStore.organization,
    organizationFullName: userStore.organizationFullName,
    organizationType: userStore.organizationType
  });
});
</script>

<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <el-col :xs="24" :sm="6" :md="5" :lg="4">
        <el-card class="menu-card">
          <div class="profile-sidebar">
            <div class="profile-avatar">
              <el-avatar :size="90" :src="userStore.userAvatar" />
              <h3 class="profile-name">{{ userStore.name }}</h3>
              <p class="profile-role">{{ userStore.leaguePosition }}</p>
            </div>
            
            <el-menu
              :default-active="activeTab"
              class="profile-menu"
              @select="activeTab = $event"
            >
              <el-menu-item index="info">
                <el-icon><UserFilled /></el-icon>
                <span>基本信息</span>
              </el-menu-item>
              <el-menu-item index="password">
                <el-icon><Key /></el-icon>
                <span>修改密码</span>
              </el-menu-item>
              <el-menu-item index="organization">
                <el-icon><OfficeBuilding /></el-icon>
                <span>设置默认组织</span>
              </el-menu-item>
              <el-menu-item index="security">
                <el-icon><OfficeBuilding /></el-icon>
                <span>安全中心</span>
              </el-menu-item>
            </el-menu>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="18" :md="19" :lg="20">
        <el-card v-if="activeTab === 'info'" shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <el-icon><UserFilled /></el-icon>
              <span>基本信息</span>
            </div>
          </template>
          
          <el-form
            ref="infoFormRef"
            :model="userForm"
            :rules="infoRules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="身份证号">
              <el-input v-model="userForm.card" disabled />
            </el-form-item>
            
            <el-form-item label="所在组织">
              <el-input v-model="organizationName" disabled />
            </el-form-item>
            
            <el-form-item label="组织信息">
              <el-input v-model="organizationFullName" disabled />
            </el-form-item>
            
            <el-form-item label="组织类型">
              <el-input v-model="organizationType" disabled />
            </el-form-item>
            
            <el-form-item label="姓名" prop="name">
              <el-input v-model="userForm.name" placeholder="请输入姓名" />
            </el-form-item>
            
            <el-form-item label="电话号码" prop="tel">
              <el-input v-model="userForm.tel" placeholder="请输入电话号码" />
            </el-form-item>
            
            <el-form-item label="性别" prop="sex">
              <el-select v-model="userForm.sex" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="出生日期" prop="birthday">
              <el-date-picker
                v-model="userForm.birthday"
                type="date"
                placeholder="选择出生日期"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                :loading="infoLoading"
                @click="updateInfo"
                class="profile-submit-btn"
              >
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card v-else-if="activeTab === 'password'" shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <el-icon><Key /></el-icon>
              <span>修改密码</span>
            </div>
          </template>
          
          <el-form
            ref="pwdFormRef"
            :model="pwdForm"
            :rules="pwdRules"
            label-width="100px"
            class="profile-form"
          >
            <el-form-item label="当前密码" prop="oldPwd">
              <el-input
                v-model="pwdForm.oldPwd"
                type="password"
                placeholder="请输入当前密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPwd">
              <el-input
                v-model="pwdForm.newPwd"
                type="password"
                placeholder="请输入新密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item v-if="pwdForm.newPwd" label="密码强度">
              <div class="password-strength">
                <div class="strength-bar">
                  <div 
                    class="strength-value" 
                    :style="{ width: `${passwordStrength * 20}%`, backgroundColor: strengthColor }"
                  ></div>
                </div>
                <span class="strength-text" :style="{ color: strengthColor }">{{ strengthText }}</span>
              </div>
              <div class="password-tips">
                <p>密码建议：</p>
                <ul>
                  <li>至少8个字符</li>
                  <li>包含大写字母、小写字母、数字和特殊符号</li>
                  <li>不要使用容易被猜到的信息，如生日</li>
                </ul>
              </div>
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPwd">
              <el-input
                v-model="pwdForm.confirmPwd"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                :loading="pwdLoading"
                @click="updatePwd"
                class="profile-submit-btn"
              >
                更新密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card v-else-if="activeTab === 'organization'" shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <el-icon><OfficeBuilding /></el-icon>
              <span>设置默认组织</span>
            </div>
          </template>
          
          <el-form
            ref="orgFormRef"
            :model="defaultOrgForm"
            :rules="orgRules"
            label-width="100px"
            class="profile-form"
          >
            <div class="org-description">
              <p>设置默认组织后，您登录系统时将直接进入该组织的管理界面。</p>
              <p>如果您拥有多个组织的权限，可以通过此设置来选择默认显示的组织。</p>
            </div>
            
            <el-form-item label="当前组织" class="mt-4">
              <el-input v-model="organizationName" disabled />
            </el-form-item>
            
            <el-form-item label="默认组织" prop="organization">
              <el-select 
                v-model="defaultOrgForm.organization" 
                placeholder="请选择默认组织"
                style="width: 100%"
              >
                <el-option 
                  v-for="option in orgOptions" 
                  :key="option.value" 
                  :label="option.label" 
                  :value="option.value" 
                />
              </el-select>
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                :loading="orgLoading"
                @click="updateDefaultOrg"
                class="profile-submit-btn"
              >
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card v-else-if="activeTab === 'security'" shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <el-icon><OfficeBuilding /></el-icon>
              <span>安全中心</span>
            </div>
          </template>
          
          <div class="security-content">
            <div class="security-item">
              <div class="security-icon secure">
                <el-icon><Lock /></el-icon>
              </div>
              <div class="security-info">
                <h4>账号密码</h4>
                <p>定期修改密码可以提高账号安全性</p>
                <el-button 
                  type="primary" 
                  size="small" 
                  plain 
                  @click="activeTab = 'password'"
                >
                  修改密码
                </el-button>
              </div>
            </div>
            
            <div class="security-item">
              <div class="security-icon secure">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="security-info">
                <h4>个人信息</h4>
                <p>确保您的个人信息准确无误</p>
                <el-button 
                  type="primary" 
                  size="small" 
                  plain 
                  @click="activeTab = 'info'"
                >
                  更新信息
                </el-button>
              </div>
            </div>
            
            <div class="security-item">
              <div class="security-icon warning">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="security-info">
                <h4>注销账户</h4>
                <p>注销账户后，所有数据将被永久删除</p>
                <el-button
                  type="danger"
                  size="small"
                  plain
                  :loading="deleteLoading"
                  @click="handleDeleteAccount"
                >
                  注销账户
                </el-button>
              </div>
            </div>
            
            <div class="security-tips">
              <h4>安全提示</h4>
              <ul>
                <li>不要将密码告诉他人，包括客服人员</li>
                <li>不要在公共场合的设备上保存登录状态</li>
                <li>定期更换密码，提高账号安全性</li>
                <li>请确保手机号码为最新，以便接收安全通知</li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.profile-container {
  padding: 20px;
}

.menu-card,
.content-card {
  height: 100%;
  box-shadow: var(--el-box-shadow-light);
  transition: all 0.3s;
}

.menu-card:hover,
.content-card:hover {
  box-shadow: var(--el-box-shadow);
}

.profile-sidebar {
  display: flex;
  flex-direction: column;
}

.profile-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

/* 添加头像放大样式 */
:deep(.el-avatar) {
  overflow: hidden;
}

:deep(.el-avatar img) {
  transform: scale(1.3); /* 将头像放大到130% */
  transform-origin: center center;
  transition: transform 0.3s;
}

.profile-name {
  margin: 15px 0 5px;
  font-size: 18px;
  font-weight: 500;
}

.profile-role {
  color: #909399;
  font-size: 13px;
  margin: 0;
}

.profile-menu {
  border-right: none;
}

.profile-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 5px 0;
  border-radius: 4px;
}

.profile-menu :deep(.el-menu-item.is-active) {
  background-color: #f0f6ff;
  color: #c62828;
  font-weight: 500;
}

.profile-menu :deep(.el-menu-item:hover) {
  background-color: #f5f7fa;
}

.profile-menu .el-icon {
  margin-right: 10px;
  font-size: 18px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.card-header .el-icon {
  margin-right: 10px;
  font-size: 20px;
  color: #c62828;
}

.profile-form {
  max-width: 600px;
  margin: 20px auto;
}

.profile-submit-btn {
  width: 120px;
}

/* 密码强度样式 */
.password-strength {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.strength-bar {
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  width: 200px;
  margin-right: 10px;
  overflow: hidden;
}

.strength-value {
  height: 100%;
  border-radius: 4px;
  transition: all 0.3s;
}

.strength-text {
  font-size: 14px;
}

.password-tips {
  margin-top: 15px;
  padding: 10px 15px;
  background-color: #f8f8f8;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
}

.password-tips p {
  margin: 0 0 5px;
  font-weight: 500;
}

.password-tips ul {
  margin: 0;
  padding-left: 20px;
}

.password-tips li {
  margin-bottom: 5px;
}

/* 组织设置样式 */
.org-description {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f8f8;
  border-radius: 4px;
  color: #606266;
}

.org-description p {
  margin: 0 0 10px;
  line-height: 1.5;
}

.org-description p:last-child {
  margin-bottom: 0;
}

/* 安全中心样式 */
.security-content {
  padding: 10px;
}

.security-item {
  display: flex;
  align-items: flex-start;
  padding: 20px;
  margin-bottom: 20px;
  background-color: #f8f8f8;
  border-radius: 8px;
  transition: all 0.3s;
}

.security-item:hover {
  background-color: #f0f0f0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.security-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  flex-shrink: 0;
}

.security-icon .el-icon {
  font-size: 24px;
  color: white;
}

.security-icon.secure {
  background-color: #67c23a;
}

.security-icon.warning {
  background-color: #e6a23c;
}

.security-info {
  flex: 1;
}

.security-info h4 {
  margin: 0 0 10px;
  font-size: 16px;
  font-weight: 500;
}

.security-info p {
  margin: 0 0 15px;
  color: #606266;
  font-size: 14px;
}

.security-tips {
  padding: 20px;
  background-color: #fff8e6;
  border-radius: 8px;
  margin-top: 30px;
}

.security-tips h4 {
  margin: 0 0 15px;
  font-size: 16px;
  font-weight: 500;
  color: #e6a23c;
}

.security-tips ul {
  margin: 0;
  padding-left: 20px;
}

.security-tips li {
  margin-bottom: 10px;
  color: #606266;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .profile-sidebar {
    margin-bottom: 20px;
  }
  
  .security-item {
    flex-direction: column;
  }
  
  .security-icon {
    margin-right: 0;
    margin-bottom: 15px;
  }
}
</style> 