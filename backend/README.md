# SWOLO平台后端

基于Spring Boot构建的SWOLO平台后端服务，提供用于管理用户、实验和实验数据的REST API。

## 功能特性

- 用户管理与身份验证和授权
- 基于角色的访问控制
- 实验管理系统
- 支持多种类型的实验数据:
  - 目标检测数据（来自YOLO模型）
  - 浓度测量数据
  - 通用实验数据
- JWT身份验证
- 完整的CRUD操作
- RESTful API设计

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Lombok
- JWT身份验证
- Maven依赖管理

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

## 设置说明

1. 克隆仓库
2. 设置MySQL数据库:
   ```sql
   CREATE DATABASE swole_platform;
   ```
3. 在[application.yml](file:///F:/SWOLO-Platform2.0/backend/src/main/resources/application.yml)中更新数据库凭证:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/swole_platform?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
       username: your_username  # 替换为您的MySQL用户名
       password: your_password  # 替换为您的MySQL密码
       driver-class-name: com.mysql.cj.jdbc.Driver
   ```
4. 构建项目:
   ```bash
   mvn clean install
   ```
5. 运行应用程序:
   ```bash
   mvn spring-boot:run
   ```

## API文档

详细API端点和使用方法请参见[API_DOCUMENTATION.md](file:///F:/SWOLO-Platform2.0/backend/API_DOCUMENTATION.md)。

## 安全性

应用程序使用JWT（JSON Web Token）进行身份验证。用户必须首先使用`/api/auth/login`端点进行身份验证以获取JWT令牌，后续请求必须在Authorization头中以`Bearer {token}`格式包含该令牌。

## 项目结构

```
src/
├── main/
│   ├── java/com/swole/platform/
│   │   ├── PlatformApplication.java
│   │   ├── config/
│   │   │   ├── DatabaseConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── auth/
│   │   │   │   └── AuthController.java
│   │   │   ├── v1/
│   │   │   │   ├── ExperimentController.java
│   │   │   │   └── ExperimentDataController.java
│   │   │   ├── ExperimentTypeController.java
│   │   │   ├── PermissionController.java
│   │   │   ├── RoleController.java
│   │   │   └── UserController.java
│   │   ├── exception/
│   │   │   └── ResourceNotFoundException.java
│   │   ├── handler/
│   │   │   ├── ErrorDetails.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── model/
│   │   │   └── entity/
│   │   │       ├── ConcentrationData.java
│   │   │       ├── DataGroup.java
│   │   │       ├── Experiment.java
│   │   │       ├── ExperimentType.java
│   │   │       ├── GeneralData.java
│   │   │       ├── Permission.java
│   │   │       ├── Role.java
│   │   │       ├── TargetDetectionData.java
│   │   │       └── User.java
│   │   ├── payload/
│   │   │   ├── request/
│   │   │   │   └── LoginRequest.java
│   │   │   └── response/
│   │   │       └── JwtResponse.java
│   │   ├── repository/
│   │   ├── security/
│   │   ├── service/
│   │   │   ├── impl/
│   │   │   └── interfaces/
│   │   └── util/
│   │       └── JwtUtil.java
│   └── resources/
│       └── application.yml
```

## 运行测试

运行单元测试:
```bash
mvn test
```

## 贡献

1. Fork仓库
2. 创建功能分支
3. 进行修改
4. 为新功能添加测试
5. 确保所有测试通过
6. 提交拉取请求

## 许可证

本项目采用MIT许可证。