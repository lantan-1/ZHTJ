# 智慧团建系统

智慧团建系统是一个基于Spring Boot后端和Vue 3前端的现代化Web应用，专为共青团组织管理设计。系统支持团组织架构管理、团员信息管理、组织活动管理、荣誉激励系统、志愿服务记录、团员评议与考核等核心功能。通过数字化转型，提升团组织管理效率和服务质量。

## 项目结构

项目采用前后端分离架构，包含以下两个主要部分：

- **zhtj-springboot**: Spring Boot后端项目，提供RESTful API
- **zhtj-ui**: Vue 3前端项目，提供用户界面

## 技术栈

### 后端技术栈
- 核心框架: Spring Boot 3.2.3 (Java 17)
- 数据库: MySQL 8.0+
- ORM框架: MyBatis-Plus 3.5.5
- 安全框架: Spring Security + JWT
- 缓存: Redis
- 存储服务: MinIO 8.5.4（可选配置）
- API文档: SpringDoc OpenAPI 2.3.0 (Swagger)
- 消息队列: RocketMQ 2.2.3
- 邮件服务: Spring Boot Mail + Jakarta Mail
- 短信服务: 阿里云短信SDK
- 工具库: Hutool 5.8.25, Apache Commons IO

### 前端技术栈
- 核心框架: Vue 3.5.13 (组合式API)
- 开发语言: TypeScript 5.7.2
- 构建工具: Vite 6.2.0
- UI组件库: Element Plus 2.9.7, Ant Design Vue 4.2.6
- 状态管理: Pinia 3.0.1
- 路由管理: Vue Router 4.5.0
- HTTP客户端: Axios 1.8.4
- PDF生成: jsPDF + html2canvas
- Office文档预览: @vue-office组件库
- Excel处理: xlsx

## 主要功能模块

1. **用户认证与权限管理**
   - 基于JWT的用户登录认证
   - 角色权限管理(RBAC模型)
   - 多层级权限控制

2. **组织架构管理**
   - 多级组织结构维护
   - 组织关系树形结构展示
   - 组织统计数据分析

3. **团员信息管理**
   - 团员基本信息维护
   - 批量导入导出
   - 团籍档案管理

4. **组织活动管理**
   - 多类型活动支持
   - 活动计划与总结
   - 活动数据统计与报表

5. **荣誉激励系统**
   - 自定义荣誉类型与等级
   - 荣誉申请与审批流程
   - 荣誉证书生成与打印

6. **志愿服务管理**
   - 志愿服务记录与时长认证
   - 服务时长统计与排名
   - 服务证明生成

7. **团员评议与考核**
   - 自定义评议指标与评分标准
   - 多维度评价体系
   - 评议结果统计与图表

8. **组织关系转接**
   - 组织间团员转接申请
   - 多级审批流程
   - 转接状态追踪

9. **团籍注册管理**
   - 年度团籍注册与团员统计
   - 批次管理与集中注册
   - 注册状态监控与统计

10. **通知消息系统**
    - 系统通知与公告发布
    - 重要事件提醒与消息推送
    - 消息阅读状态追踪

11. **学习资源管理**
    - 团课资源分类与管理
    - 学习材料上传与下载
    - 文档在线预览

## 系统特色

- 灵活的组织架构管理
- 完善的团员生命周期管理
- 精细的权限控制
- 多维度的统计分析
- 标准化流程
- 响应式设计
- 文档与文件管理
- 安全与稳定的系统设计

## 开发环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+
- npm 8+ 或 yarn 1.22+

## 部署与运行

### 后端部署
1. 克隆仓库：`git clone https://github.com/lantan-1/ZHTJ.git`
2. 进入后端目录：`cd ZHTJ/zhtj-springboot`
3. 配置数据库：修改`src/main/resources/application.yml`中的数据库连接信息
4. 构建项目：`mvn clean package -DskipTests`
5. 运行项目：`java -jar target/zhtj-springboot-0.0.1-SNAPSHOT.jar`

### 前端部署
1. 进入前端目录：`cd ZHTJ/zhtj-ui`
2. 安装依赖：`npm install` 或 `yarn`
3. 开发模式运行：`npm run dev` 或 `yarn dev`
4. 构建生产版本：`npm run build` 或 `yarn build`
5. 预览生产构建：`npm run preview` 或 `yarn preview`

## 数据库初始化

1. 创建数据库：`CREATE DATABASE zhtj CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`
2. 执行SQL脚本：`mysql -u用户名 -p密码 zhtj < zhtj.sql`

## 贡献指南

1. Fork本仓库
2. 创建您的特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交您的更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启一个Pull Request

## 许可证

本项目采用MIT许可证 - 详情请见 [LICENSE](LICENSE) 文件 