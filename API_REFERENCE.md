# SWOLO平台API参考文档

## 概述

SWOLO平台提供了一套完整的API，用于前端界面与后端服务以及YOLO预测服务之间的通信。

## API分类

### 1. 后端REST API

#### 用户管理API
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 获取指定用户
- `POST /api/users` - 创建新用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

#### 认证API
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

#### 实验管理API
- `GET /api/v1/experiments` - 获取所有实验
- `GET /api/v1/experiments/{id}` - 获取指定实验
- `POST /api/v1/experiments` - 创建新实验
- `PUT /api/v1/experiments/{id}` - 更新实验
- `DELETE /api/v1/experiments/{id}` - 删除实验

#### 实验数据API
- `GET /api/v1/experiment-data/target-detection` - 获取目标检测数据
- `POST /api/v1/experiment-data/target-detection` - 添加目标检测数据
- `GET /api/v1/experiment-data/concentration` - 获取浓度数据
- `POST /api/v1/experiment-data/concentration` - 添加浓度数据
- `GET /api/v1/experiment-data/general` - 获取通用数据
- `POST /api/v1/experiment-data/general` - 添加通用数据

### 2. YOLO预测服务API

#### 目标检测API
- `POST /predict_yolo` - 执行目标检测
  - 参数: image (图片文件)
  - 返回: 检测结果数组，包含边界框坐标、类别、置信度

#### 浓度预测API
- `POST /predict_concentration` - 执行浓度预测
  - 参数: image (图片文件)
  - 返回: 浓度值及置信度

#### 模型管理API
- `GET /models` - 获取可用模型列表
- `POST /load_model` - 加载指定模型
- `POST /unload_model` - 卸载模型

## 数据格式

### 用户数据格式
``json
{
  "id": 1,
  "name": "张三",
  "employeeId": "EMP001",
  "roleId": 2
}
```

### 实验数据格式
``json
{
  "id": 1,
  "experimentTime": "2026-01-08T10:30:00",
  "userId": 1,
  "experimentTypeId": 1,
  "description": "实验描述"
}
```

### 目标检测结果格式
``json
{
  "class": "object_class",
  "confidence": 0.95,
  "x": 100.5,
  "y": 200.3,
  "diameter": 50.2
}
```

### 浓度预测结果格式
``json
{
  "concentration": 12.5,
  "confidence": 0.89
}
```

## 错误代码

### 通用错误代码
- 400: 请求参数错误
- 401: 未授权访问
- 403: 禁止访问
- 404: 资源未找到
- 500: 服务器内部错误

### 业务错误代码
- 1001: 用户不存在
- 1002: 实验类型不存在
- 1003: 数据格式错误
- 2001: 模型加载失败
- 2002: 图像处理失败

## 认证机制

### JWT Token认证
- 登录后获取JWT Token
- 后续请求在Header中携带Token: `Authorization: Bearer {token}`
- Token有效期: 24小时

## API使用示例

### JavaScript前端调用示例
```
// 用户登录
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    employeeId: 'EMP001',
    password: 'password'
  })
});

// 获取Token
const { token } = await loginResponse.json();

// 使用Token访问受保护API
const experimentsResponse = await fetch('/api/v1/experiments', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

### Python客户端调用示例
```
import requests

# 调用YOLO预测服务
url = 'http://localhost:5000/predict_yolo'
files = {'image': open('sample.jpg', 'rb')}
response = requests.post(url, files=files)
result = response.json()
print(result)
```

## API性能指标

### 响应时间要求
- 用户认证API: < 500ms
- 实验数据API: < 1000ms
- YOLO预测API: < 3000ms (取决于图像大小和模型复杂度)

### 并发处理能力
- 用户管理API: 支持100并发请求
- 实验数据API: 支持50并发请求
- YOLO预测API: 支持10并发请求 (受GPU/CPU限制)

## 版本管理

### API版本策略
- 当前版本: v1
- 版本号体现在URL路径中 (如 /api/v1/)
- 向后兼容性保证

### 变更通知
- 重大变更提前30天通知
- 提供迁移指南
- 保留旧版本API一段时间

## 安全措施

### 数据加密
- 敏感数据传输使用HTTPS
- 密码使用BCrypt加密存储
- JWT Token使用强密钥签名

### 访问控制
- 基于角色的权限控制
- API调用频率限制
- IP白名单机制 (可选)
