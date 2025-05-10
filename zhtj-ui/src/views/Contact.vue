<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { Position, Message, Phone, ChatDotRound, Warning } from '@element-plus/icons-vue';

// 表单数据
const form = reactive({
  name: '',
  contact: '',
  type: '功能使用问题',
  content: ''
});

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入您的姓名', trigger: 'blur' }
  ],
  contact: [
    { required: true, message: '请输入联系方式', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入咨询内容', trigger: 'blur' },
    { min: 10, message: '内容不能少于10个字符', trigger: 'blur' }
  ]
};

// 问题类型选项
const typeOptions = [
  '功能使用问题',
  '账号登录问题',
  '组织管理问题',
  '活动管理问题',
  '数据统计问题',
  '系统故障问题',
  '其他问题'
];

// 表单引用
const formRef = ref();

// 提交状态
const submitting = ref(false);

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return;
  
  await formRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      ElMessage.warning('请完成表单必填项');
      return;
    }
    
    submitting.value = true;
    try {
      // 模拟API提交
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      ElMessage.success('您的反馈已提交，我们会尽快与您联系');
      
      // 重置表单
      formRef.value.resetFields();
    } catch (error) {
      console.error('提交失败', error);
      ElMessage.error('提交失败，请稍后重试');
    } finally {
      submitting.value = false;
    }
  });
};
</script>

<template>
  <div class="contact-container">
    <div class="page-header">
      <h1>联系我们</h1>
      <p class="header-desc">如有任何问题或建议，欢迎随时联系我们</p>
    </div>
    
    <div class="contact-content">
      <div class="contact-info-section">
        <div class="info-cards">
          <div class="info-card">
            <div class="card-icon">
              <el-icon><Phone /></el-icon>
            </div>
            <div class="card-content">
              <h3>电话咨询</h3>
              <p>工作日 9:00-18:00</p>
              <div class="contact-value">400-123-4567</div>
            </div>
          </div>
          
          <div class="info-card">
            <div class="card-icon">
              <el-icon><Message /></el-icon>
            </div>
            <div class="card-content">
              <h3>邮件咨询</h3>
              <p>7×24小时受理，工作日内回复</p>
              <div class="contact-value">lantank2024@163.com</div>
            </div>
          </div>
          
          <div class="info-card">
            <div class="card-icon">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="card-content">
              <h3>在线客服</h3>
              <p>工作日 9:00-18:00</p>
              <div class="contact-value">
                <el-button type="primary" size="small">
                  在线咨询
                </el-button>
              </div>
            </div>
          </div>
          
          <div class="info-card">
            <div class="card-icon">
              <el-icon><Position /></el-icon>
            </div>
            <div class="card-content">
              <h3>微信客服</h3>
              <p>扫描二维码添加客服</p>
              <div class="contact-value qrcode-wrap">
                <img src="/src/assets/img/xiaochengxu.jpg" alt="微信客服" class="qrcode-img" />
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="contact-form-section">
        <el-card class="form-card">
          <template #header>
            <div class="form-header">
              <h2>联系表单</h2>
              <p>填写以下表单，我们会尽快回复您</p>
            </div>
          </template>
          
          <el-form 
            ref="formRef"
            :model="form"
            :rules="rules"
            label-width="100px"
            class="contact-form"
          >
            <el-form-item label="您的姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入您的姓名" />
            </el-form-item>
            
            <el-form-item label="联系方式" prop="contact">
              <el-input v-model="form.contact" placeholder="电话或邮箱" />
            </el-form-item>
            
            <el-form-item label="问题类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择问题类型" style="width: 100%">
                <el-option 
                  v-for="item in typeOptions" 
                  :key="item" 
                  :label="item" 
                  :value="item" 
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="咨询内容" prop="content">
              <el-input 
                v-model="form.content" 
                type="textarea" 
                rows="5"
                placeholder="请详细描述您遇到的问题或建议"
              />
            </el-form-item>
            
            <el-form-item>
              <el-button 
                type="primary" 
                @click="submitForm"
                :loading="submitting"
                class="submit-btn"
              >
                提交
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </div>
    
    <div class="contact-tips">
      <div class="tips-header">
        <el-icon class="tips-icon"><Warning /></el-icon>
      <h3>温馨提示</h3>
      </div>
      <ul>
        <li>
          <span class="tip-dot"></span>
          <span class="tip-content">工作日 9:00-18:00 为服务时间，非工作时间提交的问题将在下一个工作日处理</span>
        </li>
        <li>
          <span class="tip-dot"></span>
          <span class="tip-content">提交问题时，请尽量详细描述您遇到的情况，包括操作步骤、错误提示等</span>
        </li>
        <li>
          <span class="tip-dot"></span>
          <span class="tip-content">如遇紧急问题，建议直接拨打客服电话获取即时帮助</span>
        </li>
        <li>
          <span class="tip-dot"></span>
          <span class="tip-content">您也可以前往<router-link to="/dashboard/help">帮助中心</router-link>查看常见问题解答</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>
.contact-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 500;
  margin: 0 0 10px;
  color: #303133;
}

.header-desc {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.contact-content {
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
  margin-bottom: 30px;
}

.contact-info-section {
  flex: 1;
  min-width: 300px;
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.info-card {
  padding: 20px;
  background-color: #f8f8f8;
  border-radius: 8px;
  display: flex;
  align-items: flex-start;
  transition: all 0.3s;
}

.info-card:hover {
  background-color: #f0f0f0;
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background-color: #c62828;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;
}

.card-icon .el-icon {
  font-size: 24px;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.card-content p {
  margin: 0 0 10px;
  color: #909399;
  font-size: 13px;
}

.contact-value {
  font-size: 16px;
  color: #303133;
  font-weight: 500;
}

.qrcode-wrap {
  margin-top: 10px;
}

.qrcode-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #e0e0e0;
}

.contact-form-section {
  flex: 1;
  min-width: 300px;
}

.form-card {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.form-header {
  text-align: center;
}

.form-header h2 {
  margin: 0 0 8px;
  font-size: 20px;
  font-weight: 500;
}

.form-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.contact-form {
  padding: 20px 0;
}

.submit-btn {
  width: 120px;
}

.contact-tips {
  background-color: #fffbf0;
  border-radius: 10px;
  padding: 24px;
  margin-top: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  border-left: 4px solid #e6a23c;
}

.tips-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  justify-content: center;
}

.tips-icon {
  color: #e6a23c;
  font-size: 20px;
  margin-right: 10px;
}

.contact-tips h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
  color: #e6a23c;
}

.contact-tips ul {
  margin: 0;
  padding-left: 0;
  list-style-type: none;
}

.contact-tips li {
  margin-bottom: 14px;
  padding-left: 5px;
  color: #606266;
  line-height: 1.6;
  display: flex;
  align-items: flex-start;
}

.contact-tips li:last-child {
  margin-bottom: 0;
}

.tip-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #e6a23c;
  display: inline-block;
  margin-right: 12px;
  margin-top: 8px;
  flex-shrink: 0;
}

.tip-content {
  flex: 1;
}

.contact-tips a {
  color: #c62828;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.3s;
}

.contact-tips a:hover {
  text-decoration: underline;
  color: #e03e41;
}

@media (max-width: 768px) {
  .contact-content {
    flex-direction: column;
  }
  
  .info-cards {
    grid-template-columns: 1fr;
  }
}

.contact-value .el-button--primary {
  background-color: #c62828;
  border-color: #c62828;
}

.contact-value .el-button--primary:hover {
  background-color: #b71c1c;
  border-color: #b71c1c;
}
</style> 