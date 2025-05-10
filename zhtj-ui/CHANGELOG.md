# 变更日志

## 1.0.0 (2024-03-24)

### 功能升级

- 将原蓝炭系统前端从Vue 2 + Element UI升级到Vue 3 + Element Plus
- 添加TypeScript类型支持
- 引入Pinia状态管理，替代原有的状态管理方式
- 优化路由配置，增加路由守卫
- 重构API接口，使用Axios进行请求

### 新增功能

- 使用组合式API (Composition API)重写所有组件
- 添加表单验证
- 优化用户体验和界面布局
- 增加类型检查和类型声明
- 更现代化的界面设计
- 添加API接口文档

### 主要组件

- 登录/注册功能
- 个人信息管理
- 活动管理（CRUD操作）
- 组织成员管理
- 权限控制系统

### 技术特性

- 使用Vue 3的`<script setup>`语法
- 使用TypeScript提供类型安全
- 使用Pinia进行状态管理
- 使用Vue Router进行路由管理
- 使用Element Plus提供UI组件
- 使用Axios进行HTTP请求
- 使用Vite作为构建工具 