# SWOLO平台开发指南

本指南介绍SWOLO平台的开发流程、架构设计和最佳实践。

## 项目架构

SWOLO平台采用前后端分离的微服务架构：

```
┌─────────────────┐    ┌──────────────────┐    ┌──────────────────────┐
│   前端界面      │◄──►│    后端服务      │◄──►│  YOLO预测服务        │
│ (Vue.js)       │    │ (Spring Boot)    │    │ (Python/PyTorch)     │
└─────────────────┘    └──────────────────┘    └──────────────────────┘
```

## 技术栈详解

### 前端技术栈
- **框架**: Vue.js 3 + TypeScript
- **构建工具**: Vite
- **样式**: CSS
- **状态管理**: (待添加)
- **UI组件库**: (待添加)

### 后端技术栈
- **框架**: Spring Boot 3.2.0
- **语言**: Java 17
- **数据库**: MySQL 8.0+
- **安全**: Spring Security + JWT
- **持久层**: Spring Data JPA + Hibernate
- **构建工具**: Maven

### 预测服务技术栈
- **语言**: Python 3.8+
- **深度学习**: PyTorch
- **计算机视觉**: OpenCV
- **算法**: YOLO (You Only Look Once)

## 数据库设计

### 核心表结构
1. **用户表** (users)
   - ID, 姓名, 工号, 角色ID
2. **角色表** (roles)
   - ID, 角色名, 权限
3. **权限表** (permissions)
   - ID, 角色ID, 控制对象
4. **实验类型表** (experiment_types)
   - ID, 实验类型
5. **实验表** (experiments)
   - ID, 时间, 用户ID, 实验类型ID, 实验描述
6. **目标检测数据表** (target_detection_data)
   - ID, 实验ID, 组号, 类别, 置信度, x, y, 直径
7. **浓度检测数据表** (concentration_data)
   - ID, 实验ID, 组号, 浓度, 置信度
8. **一般类别实验数据表** (general_data)
   - ID, 实验ID, 组号, 数据键, 数据值

## 开发环境设置

### 前端开发环境
1. 安装Node.js (v16.0或更高版本)
2. 安装npm或yarn
3. 在frontend/SWOLO-Platform目录下运行:
   ```bash
   npm install
   npm run dev
   ```

### 后端开发环境
1. 安装Java 17 JDK
2. 安装Maven 3.6+
3. 安装MySQL 8.0+
4. 在backend目录下运行:
   ```bash
   mvn spring-boot:run
   ```

### 预测服务开发环境
1. 安装Python 3.8+
2. 安装pip
3. 在yolo_prediction_service目录下运行:
   ```bash
   pip install -r requirements.txt
   python server.py
   ```

## API设计规范

### RESTful API设计
后端API遵循RESTful设计原则:

```
GET    /api/users          # 获取用户列表
GET    /api/users/{id}     # 获取特定用户
POST   /api/users          # 创建新用户
PUT    /api/users/{id}     # 更新用户
DELETE /api/users/{id}     # 删除用户
```

### 请求/响应格式
- 请求内容类型: `application/json`
- 响应内容类型: `application/json`
- 字符编码: UTF-8

### 错误处理
- 200 OK: 成功
- 400 Bad Request: 请求参数错误
- 401 Unauthorized: 未授权
- 404 Not Found: 资源未找到
- 500 Internal Server Error: 服务器内部错误

## 安全规范

### JWT身份验证
- 使用HS512算法
- Token有效期: 24小时
- 刷新机制: (待实现)

### 数据验证
- 输入数据验证
- SQL注入防护
- XSS防护

## 代码规范

### Java代码规范
- 遵循Oracle Java编码规范
- 使用Lombok减少样板代码
- 统一异常处理机制

### Vue.js代码规范
- 使用Composition API
- 遵循Vue官方风格指南
- TypeScript类型定义

### Python代码规范
- 遵循PEP 8编码规范
- 使用类型提示
- 文档字符串

## 测试策略

### 单元测试
- 后端: 使用JUnit 5
- 前端: 使用Vitest
- Python: 使用unittest

### 集成测试
- API端点测试
- 数据库集成测试
- 端到端测试

## 部署指南

### 生产环境要求
- Java 17运行时环境
- MySQL 8.0+数据库
- Node.js运行时(前端)
- Python 3.8+环境(预测服务)

### 部署步骤
1. 构建后端: `mvn clean package`
2. 构建前端: `npm run build`
3. 配置生产环境参数
4. 启动服务

## 贡献指南

### 代码提交规范
- 使用Git工作流
- 提交信息格式: `<type>(<scope>): <subject>`
- 例如: `feat(user): add user registration API`

### 分支管理
- `main`: 主分支，生产代码
- `develop`: 开发分支
- `feature/*`: 功能分支
- `bugfix/*`: 修复分支

## 性能优化

### 前端优化
- 组件懒加载
- 图片优化
- 代码分割

### 后端优化
- 数据库索引优化
- 缓存策略
- 连接池配置

## 监控与日志

### 日志记录
- 使用SLF4J + Logback
- 分级日志记录
- 结构化日志格式

### 监控指标
- API响应时间
- 错误率
- 数据库连接池状态

## 常见问题

### 开发环境问题
1. 端口冲突: 更改application.yml中的端口设置
2. 数据库连接失败: 检查数据库服务和配置
3. 依赖冲突: 清理Maven/Gradle缓存

### 部署问题
1. 内存不足: 调整JVM参数
2. 权限问题: 检查文件权限设置
3. 网络问题: 检查防火墙配置

## 联系方式

如有问题，请联系开发团队或提交Issue。
