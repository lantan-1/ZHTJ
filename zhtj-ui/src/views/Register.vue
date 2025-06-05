<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { registerUser } from '../api/user';
import type { User, Organization } from '../api/user';
import { getOrganizationTree } from '../api/organization';
import { getCaptcha } from '../api/login';

const router = useRouter();

const orgTree = ref([]);
const orgLoading = ref(false);
const captchaImg = ref('');
const captchaImgUrl = ref('');
const captchaKey = ref('');

const refreshCaptcha = async () => {
  try {
    captchaImg.value = '';
    captchaImgUrl.value = '';
    const response = await getCaptcha({ codeType: 'register' });
    if (response && response.success) {
      if (response.data.captchaImg) {
        captchaImg.value = response.data.captchaImg;
      } else if (response.data.imageUrl) {
        captchaImgUrl.value = response.data.imageUrl;
      }
      captchaKey.value = response.data.key;
    }
  } catch (error) {
    ElMessage.error('获取验证码失败');
  }
};

onMounted(() => {
  orgLoading.value = true;
  try {
    getOrganizationTree().then(res => {
      orgTree.value = res.data || [];
    });
  } finally {
    orgLoading.value = false;
  }
  refreshCaptcha();
});

// 表单数据
const registerForm = reactive({
  organizationId: null as number | null,
  user: {
    card: '',
    pwd: '',
    phone: '',
    name: '',
    gender: '',
    joinLeagueDate: ''
  },
  confirmPwd: '',
  captcha: ''
});

// 计算属性和setter用于安全访问嵌套属性
const userCard = computed({
  get: () => registerForm.user?.card || '',
  set: (val) => { ensureUserExists(); registerForm.user!.card = val; }
});

const userPwd = computed({
  get: () => registerForm.user?.pwd || '',
  set: (val) => { ensureUserExists(); registerForm.user!.pwd = val; }
});

const userPhone = computed({
  get: () => registerForm.user?.phone || '',
  set: (val) => { ensureUserExists(); registerForm.user!.phone = val; }
});

const userName = computed({
  get: () => registerForm.user?.name || '',
  set: (val) => { ensureUserExists(); registerForm.user!.name = val; }
});

const userGender = computed({
  get: () => registerForm.user?.gender || '',
  set: (val) => { ensureUserExists(); registerForm.user!.gender = val; }
});

const userJoinLeagueDate = computed({
  get: () => registerForm.user?.joinLeagueDate || '',
  set: (val) => { ensureUserExists(); registerForm.user!.joinLeagueDate = val; }
});

// 表单验证规则
const rules = {
  organizationId: [{ required: true, message: '请选择组织', trigger: 'change' }],
  'user.card': [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { min: 18, max: 18, message: '身份证号长度应为18位', trigger: 'blur' }
  ],
  'user.pwd': [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== registerForm.user?.pwd) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  'user.phone': [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  'user.name': [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  'user.gender': [{ required: true, message: '请选择性别', trigger: 'change' }],
  'user.joinLeagueDate': [{ required: true, message: '请选择入团日期', trigger: 'change' }],
  captcha: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
};

const formRef = ref();
const loading = ref(false);

// 确保user对象存在
const ensureUserExists = () => {
  if (!registerForm.user) {
    registerForm.user = {
      card: '',
      pwd: '',
      phone: '',
      name: '',
      gender: '',
      joinLeagueDate: ''
    };
  }
};

// 注册处理
const handleRegister = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      try {
        // 构造请求数据
        const requestData = {
          user: registerForm.user,
          organization: registerForm.organizationId,
          captcha: registerForm.captcha,
          captchaKey: captchaKey.value
        };
        
        const result = await registerUser(requestData);
        
        if (result && (result.code === 200 || result.success)) {
          ElMessage.success('注册成功，请等待管理员激活您的账号');
          router.push({
            path: '/login',
            query: { needActivation: 'true' }
          });
        } else {
          ElMessage.error(result?.message || '注册失败，请检查输入信息');
        }
      } catch (error: any) {
        console.error('注册出错', error);
        ElMessage.error(`注册失败：${error.response?.data?.message || error.message || '未知错误'}`);
      } finally {
        loading.value = false;
      }
    } else {
      ElMessage.warning('请完成表单填写');
      return false;
    }
  });
};

// 返回登录页
const goToLogin = () => {
  router.push('/login');
};
</script>

<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <h2>智慧团建系统 - 用户注册</h2>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        label-position="top"
      >
        <el-form-item label="组织名称" prop="organizationId">
          <el-tree-select
            v-model="registerForm.organizationId"
            :data="orgTree"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择组织"
            :loading="orgLoading"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="身份证号" prop="user.card">
          <el-input
            v-model="userCard"
            placeholder="请输入18位身份证号"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="user.pwd">
          <el-input
            v-model="userPwd"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPwd">
          <el-input
            v-model="registerForm.confirmPwd"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="电话号码" prop="user.phone">
          <el-input
            v-model="userPhone"
            placeholder="请输入电话号码"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="姓名" prop="user.name">
          <el-input
            v-model="userName"
            placeholder="请输入姓名"
            clearable
          />
        </el-form-item>
        
        <el-form-item label="性别" prop="user.gender">
          <el-select
            v-model="userGender"
            placeholder="请选择性别"
            style="width: 100%"
          >
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="入团日期" prop="user.joinLeagueDate">
          <el-date-picker
            v-model="userJoinLeagueDate"
            type="date"
            placeholder="选择入团日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="验证码" prop="captcha">
          <div style="display: flex; align-items: center; width: 100%;">
            <el-input
              v-model="registerForm.captcha"
              placeholder="请输入验证码"
              maxlength="4"
              style="flex: 1; margin-right: 12px;"
            />
            <div v-if="captchaImg || captchaImgUrl" class="captcha-img" title="点击刷新验证码" @click="refreshCaptcha" style="flex-shrink: 0;">
              <img v-if="captchaImg" :src="'data:image/png;base64,' + captchaImg" alt="验证码" style="height: 38px;" />
              <img v-else-if="captchaImgUrl" :src="captchaImgUrl" alt="验证码" style="height: 38px;" />
            </div>
            <div v-else class="captcha-loading" @click="refreshCaptcha" style="flex-shrink: 0;">
              <el-icon class="is-loading"><Position /></el-icon>
            </div>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleRegister"
            style="width: 100%"
          >
            注 册
          </el-button>
        </el-form-item>
        
        <div class="login-link">
          <span>已有账号？</span>
          <el-button link @click="goToLogin">返回登录</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px 0;
  background-color: #f5f7fa;
}

.register-card {
  width: 500px;
  border-radius: 8px;
}

.card-header {
  text-align: center;
}

.login-link {
  margin-top: 15px;
  text-align: center;
}

.captcha-img {
  cursor: pointer;
  margin-left: 8px;
}

.captcha-loading {
  cursor: pointer;
  margin-left: 8px;
}
</style> 