<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { registerUser } from '../api/user';
import type { User, Organization } from '../api/user';

const router = useRouter();

// 确保user对象存在
const ensureUserExists = () => {
  if (!registerForm.user) {
    registerForm.user = {
      card: '',
      pwd: '',
      tel: '',
      name: '',
      sex: '',
      birthday: ''
    };
  }
};

// 表单数据
const registerForm = reactive<Organization>({
  name: '',
  user: {
    card: '',
    pwd: '',
    tel: '',
    name: '',
    sex: '',
    birthday: ''
  }
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

const userTel = computed({
  get: () => registerForm.user?.tel || '',
  set: (val) => { ensureUserExists(); registerForm.user!.tel = val; }
});

const userName = computed({
  get: () => registerForm.user?.name || '',
  set: (val) => { ensureUserExists(); registerForm.user!.name = val; }
});

const userSex = computed({
  get: () => registerForm.user?.sex || '',
  set: (val) => { ensureUserExists(); registerForm.user!.sex = val; }
});

const userBirthday = computed({
  get: () => registerForm.user?.birthday || '',
  set: (val) => { ensureUserExists(); registerForm.user!.birthday = val; }
});

// 确认密码
const confirmPwd = ref('');

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入组织名称', trigger: 'blur' }],
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
  'user.tel': [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  'user.name': [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  'user.sex': [{ required: true, message: '请选择性别', trigger: 'change' }],
  'user.birthday': [{ required: true, message: '请选择出生日期', trigger: 'change' }]
};

const formRef = ref();
const loading = ref(false);

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
          name: registerForm.name,
          captcha: "000000", // 可选的验证码，简化处理
          captchaKey: "register" // 可选的验证码标识，简化处理
        };
        
        const response = await registerUser(requestData);
        const result = response.data;
        
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
      
      <el-alert
        title="用户注册后需要管理员激活才能使用系统功能"
        type="info"
        description="请联系您所在组织的管理员审核激活您的账号"
        show-icon
        :closable="false"
        style="margin-bottom: 20px"
      />
      
      <el-form
        ref="formRef"
        :model="registerForm"
        :rules="rules"
        label-position="top"
      >
        <el-form-item label="组织名称" prop="name">
          <el-input
            v-model="registerForm.name"
            placeholder="请输入组织名称"
            clearable
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
            v-model="confirmPwd"
            type="password"
            placeholder="请再次输入密码"
            show-password
            clearable
          />
        </el-form-item>
        
        <el-form-item label="电话号码" prop="user.tel">
          <el-input
            v-model="userTel"
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
        
        <el-form-item label="性别" prop="user.sex">
          <el-select
            v-model="userSex"
            placeholder="请选择性别"
            style="width: 100%"
          >
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="出生日期" prop="user.birthday">
          <el-date-picker
            v-model="userBirthday"
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
</style> 