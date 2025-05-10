<template>
    <div class="register-approve">
      <el-card>
        
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-form :inline="true">
            <el-form-item label="用户名">
              <el-input v-model="searchForm.name" placeholder="请输入用户名" clearable style="width: 150px" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
                <el-option value="pending" label="待审批" />
                <el-option value="activated" label="已审批" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon>
                搜索
              </el-button>
              <el-button style="margin-left: 25px" @click="handleReset">
                <el-icon><Refresh /></el-icon>
                重置
              </el-button>
              <el-button 
                type="success" 
                plain 
                style="margin-left: 25px"
                @click="handleExport"
              >
                <el-icon><Download /></el-icon>
                导出团员
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 用户列表 -->
        <el-table
          :data="userList"
          v-loading="loading"
          row-key="id"
          style="width: 100%"
          border
        >
          <el-table-column 
            type="index" 
            label="序号" 
            width="60" 
            align="center"
            :index="indexMethod"
          />
          <el-table-column prop="name" label="姓名" width="80" />
          <el-table-column prop="card" label="身份证号" width="180" />
          <el-table-column prop="organizationName" label="所属组织" width="140" />
          <el-table-column prop="phone" label="联系电话" width="120" />
          
          <!-- 状态列 -->
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isActivated ? 'success' : 'warning'">
                {{ row.isActivated ? '已审批' : '待审批' }}
              </el-tag>
            </template>
          </el-table-column>
  
          <!-- 操作列 -->
          <el-table-column label="操作" width="180" align="center">
            <template #default="{ row }">
              <el-button 
                type="primary" 
                size="small" 
                :disabled="row.isActivated"
                @click="handleActivate(row)"
              >
                审批
              </el-button>
              <el-button 
                type="primary" 
                link
                size="small"
                @click="handleViewDetails(row)"
              >
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
  
        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
  
      <!-- 用户详情抽屉 -->
      <el-drawer
        v-model="drawerVisible"
        title="用户详情"
        direction="rtl"
        size="30%"
      >
        <el-descriptions direction="vertical" :column="1" border>
          <el-descriptions-item label="姓名">{{ selectedUser?.name }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ selectedUser?.card }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ selectedUser?.gender }}</el-descriptions-item>
          <el-descriptions-item label="民族">{{ selectedUser?.ethnic }}</el-descriptions-item>
          <el-descriptions-item label="政治面貌">{{ selectedUser?.politicalStatus }}</el-descriptions-item>
          <el-descriptions-item label="入团时间">{{ selectedUser?.joinLeagueDate }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedUser?.phone }}</el-descriptions-item>
          <el-descriptions-item label="电子邮箱">{{ selectedUser?.email }}</el-descriptions-item>
          <el-descriptions-item label="教育程度">{{ selectedUser?.educationLevel }}</el-descriptions-item>
          <el-descriptions-item label="职业">{{ selectedUser?.occupation }}</el-descriptions-item>
          <el-descriptions-item label="所属组织">{{ selectedUser?.organizationName }}</el-descriptions-item>
        </el-descriptions>
      </el-drawer>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { ref, reactive, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Search, Refresh, Download } from '@element-plus/icons-vue'
  import { getPendingUsers, activateUser } from '@/api/user'
  import { useUserStore } from '@/stores/user'
  import { useRoute } from 'vue-router'
  import type { User } from '@/api/user'
  import * as XLSX from 'xlsx'
  
  const route = useRoute()
  
  // 搜索表单
  const searchForm = reactive({
    name: '',
    status: undefined as undefined | 'pending' | 'activated'
  })
  
  // 状态变量
  const loading = ref(false)
  const userList = ref<User[]>([])
  const pagination = reactive({
    current: 1,
    pageSize: 10,
    total: 0
  })
  const drawerVisible = ref(false)
  const selectedUser = ref<User | null>(null)
  const userStore = useUserStore()
  
  // 方法定义
  const fetchUserList = async () => {
    loading.value = true
    try {
      const response = await getPendingUsers({
        page: pagination.current,
        size: pagination.pageSize,
        name: searchForm.name,
        status: searchForm.status
      })
      
      if (response.data) {
        userList.value = response.data.list || []
        pagination.total = response.data.total || 0
      } else {
        userList.value = []
        pagination.total = 0
      }
    } catch (error) {
      ElMessage.error('获取用户列表失败')
      console.error('获取用户列表失败:', error)
    } finally {
      loading.value = false
    }
  }
  
  const handleSearch = () => {
    pagination.current = 1
    fetchUserList()
  }
  
  const handleReset = () => {
    searchForm.name = ''
    searchForm.status = undefined
    handleSearch()
  }
  
  const handleSizeChange = (size: number) => {
    pagination.pageSize = size
    fetchUserList()
  }
  
  const handleCurrentChange = (current: number) => {
    pagination.current = current
    fetchUserList()
  }
  
  const handleActivate = async (record: User) => {
    try {
      if (record.id) {
        // 添加确认弹窗
        await ElMessageBox.confirm(
          `确定要审批用户 "${record.name}" 吗？`,
          '审批确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        
        // 用户确认后执行审批操作
        await activateUser(Number(record.id))
        ElMessage.success('用户审批成功')
        fetchUserList() // 刷新列表
      }
    } catch (error) {
      // 用户取消或操作失败
      if (error !== 'cancel') {
        ElMessage.error('用户审批失败')
        console.error('用户审批失败:', error)
      }
    }
  }
  
  const handleViewDetails = (record: User) => {
    selectedUser.value = record
    drawerVisible.value = true
  }
  
  // 导出团员数据
  const handleExport = () => {
    try {
      // 获取当前组织名称
      const orgName = userStore.organization || '智慧团建'
      
      // 构建导出文件名
      const fileName = `${orgName}团员注册审批.xlsx`
      
      // 准备导出数据
      const exportData = userList.value.map((item: User, index: number) => {
        return {
          '序号': index + 1,
          '姓名': item.name || '-',
          '性别': item.gender || '-',
          '身份证号': item.card || '-',
          '所属组织': item.organizationName || '-',
          '联系电话': item.phone || '-',
          '电子邮箱': item.email || '-',
          '政治面貌': item.politicalStatus || '-',
          '入团时间': item.joinLeagueDate || '-',
          '状态': item.isActivated ? '已审批' : '待审批'
        }
      })
      
      // 没有数据时提示
      if (exportData.length === 0) {
        ElMessage.warning('当前没有数据可导出')
        return
      }
      
      // 创建工作簿和工作表
      const worksheet = XLSX.utils.json_to_sheet(exportData)
      const workbook = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(workbook, worksheet, '团员注册审批')
      
      // 设置列宽
      const colWidths = [
        { wch: 6 },   // 序号
        { wch: 10 },  // 姓名
        { wch: 6 },   // 性别
        { wch: 20 },  // 身份证号
        { wch: 20 },  // 所属组织
        { wch: 15 },  // 联系电话
        { wch: 20 },  // 电子邮箱
        { wch: 15 },  // 政治面貌
        { wch: 15 },  // 入团时间
        { wch: 10 }   // 状态
      ]
      worksheet['!cols'] = colWidths
      
      // 导出文件
      XLSX.writeFile(workbook, fileName)
      
      ElMessage.success(`成功导出"${fileName}"`)
    } catch (error) {
      console.error('导出团员列表失败:', error)
      ElMessage.error('导出失败，请稍后重试')
    }
  }
  
  // 自定义序号方法，确保分页时序号连续
  const indexMethod = (index: number) => {
    return (pagination.current - 1) * pagination.pageSize + index + 1
  }
  
  // 生命周期钩子
  onMounted(() => {
    // 读取URL查询参数，设置默认状态过滤
    const statusParam = route.query.status as string | undefined
    
    if (statusParam === 'activated' || statusParam === 'pending') {
      searchForm.status = statusParam
    }
    
    // 获取用户列表
    fetchUserList()
  })
  </script>
  
  <style lang="scss" scoped>
  .register-approve {
    padding: 24px;
  
    .search-bar {
      margin-bottom: 16px;
    }
  
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  
    .pagination-container {
      margin-top: 16px;
      text-align: right;
    }
  }
  </style> 