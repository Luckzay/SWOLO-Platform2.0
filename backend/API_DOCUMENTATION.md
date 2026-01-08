# SWOLO平台后端API文档

## 概述
SWOLO平台后端提供REST API，用于管理用户、实验和实验数据，以支持科学分析。

## 基础URL
`http://localhost:8080/api`

## 认证
API使用JWT（JSON Web Token）进行身份验证。用户必须首先使用`/auth/login`端点进行身份验证以获取JWT令牌，后续请求必须在Authorization头中以`Bearer {token}`格式包含该令牌。

## 可用端点

### 认证
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册

### 用户管理
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 根据ID获取用户
- `GET /api/users/employee/{employeeId}` - 根据工号获取用户
- `POST /api/users` - 创建新用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 角色管理
- `GET /api/roles` - 获取所有角色
- `GET /api/roles/{id}` - 根据ID获取角色
- `GET /api/roles/name/{roleName}` - 根据名称获取角色
- `POST /api/roles` - 创建新角色
- `PUT /api/roles/{id}` - 更新角色
- `DELETE /api/roles/{id}` - 删除角色

### 权限管理
- `GET /api/permissions` - 获取所有权限
- `GET /api/permissions/{id}` - 根据ID获取权限
- `GET /api/permissions/role/{roleId}` - 根据角色ID获取权限
- `POST /api/permissions` - 创建新权限
- `PUT /api/permissions/{id}` - 更新权限
- `DELETE /api/permissions/{id}` - 删除权限

### 实验类型管理
- `GET /api/experiment-types` - 获取所有实验类型
- `GET /api/experiment-types/{id}` - 根据ID获取实验类型
- `GET /api/experiment-types/name/{typeName}` - 根据名称获取实验类型
- `POST /api/experiment-types` - 创建新实验类型
- `PUT /api/experiment-types/{id}` - 更新实验类型
- `DELETE /api/experiment-types/{id}` - 删除实验类型

### 实验管理 (v1)
- `GET /api/v1/experiments` - 获取所有实验
- `GET /api/v1/experiments/{id}` - 根据ID获取实验
- `GET /api/v1/experiments/user/{userId}` - 根据用户ID获取实验
- `GET /api/v1/experiments/type/{experimentTypeId}` - 根据实验类型ID获取实验
- `POST /api/v1/experiments` - 创建新实验
- `PUT /api/v1/experiments/{id}` - 更新实验
- `DELETE /api/v1/experiments/{id}` - 删除实验

### 实验数据管理 (v1)
- `GET /api/v1/experiment-data/target-detection` - 获取所有目标检测数据
- `GET /api/v1/experiment-data/target-detection/{id}` - 根据ID获取目标检测数据
- `GET /api/v1/experiment-data/target-detection/experiment/{experimentId}` - 根据实验ID获取目标检测数据
- `GET /api/v1/experiment-data/target-detection/group/{groupNumber}` - 根据组号获取目标检测数据
- `POST /api/v1/experiment-data/target-detection` - 创建目标检测数据
- `PUT /api/v1/experiment-data/target-detection/{id}` - 更新目标检测数据
- `DELETE /api/v1/experiment-data/target-detection/{id}` - 删除目标检测数据

- `GET /api/v1/experiment-data/concentration` - 获取所有浓度数据
- `GET /api/v1/experiment-data/concentration/{id}` - 根据ID获取浓度数据
- `GET /api/v1/experiment-data/concentration/experiment/{experimentId}` - 根据实验ID获取浓度数据
- `GET /api/v1/experiment-data/concentration/group/{groupNumber}` - 根据组号获取浓度数据
- `POST /api/v1/experiment-data/concentration` - 创建浓度数据
- `PUT /api/v1/experiment-data/concentration/{id}` - 更新浓度数据
- `DELETE /api/v1/experiment-data/concentration/{id}` - 删除浓度数据

- `GET /api/v1/experiment-data/general` - 获取所有通用数据
- `GET /api/v1/experiment-data/general/{id}` - 根据ID获取通用数据
- `GET /api/v1/experiment-data/general/experiment/{experimentId}` - 根据实验ID获取通用数据
- `GET /api/v1/experiment-data/general/group/{groupNumber}` - 根据组号获取通用数据
- `POST /api/v1/experiment-data/general` - 创建通用数据
- `PUT /api/v1/experiment-data/general/{id}` - 更新通用数据
- `DELETE /api/v1/experiment-data/general/{id}` - 删除通用数据

## 错误处理
API返回适当的HTTP状态码和JSON格式的错误消息：
- `400 错误请求`: 请求验证失败
- `401 未授权`: 需要身份验证或验证失败
- `404 未找到`: 请求的资源未找到
- `500 内部服务器错误`: 意外的服务器错误

示例错误响应：
```json
{
  "timestamp": "2026-01-08T03:39:01.000+00:00",
  "message": "用户未找到，ID: 1",
  "details": "uri=/api/users/1"
}
```

## 数据库表结构
后端与以下表进行交互：
- users: 存储用户信息 (ID, 姓名, 工号, 角色ID)
- roles: 存储角色信息 (ID, 角色名, 权限)
- permissions: 存储权限信息 (ID, 角色ID, 控制对象)
- experiment_types: 存储实验类型信息 (ID, 类型名称)
- experiments: 存储实验记录 (ID, 时间, 用户ID, 实验类型ID, 描述)
- target_detection_data: 存储目标检测实验数据 (ID, 实验ID, 组号, 类别, 置信度, x, y, 直径)
- concentration_data: 存储浓度测量数据 (ID, 实验ID, 组号, 浓度, 置信度)
- general_data: 存储通用实验数据 (ID, 实验ID, 组号, 数据键, 数据值)