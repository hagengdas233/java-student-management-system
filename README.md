# Student Spring Boot 学生管理系统

## 项目介绍

本项目是一个基于 Spring Boot + MyBatis + MySQL 的学生管理系统，主要用于练习 Java 后端项目开发中的分层架构、RESTful 接口设计、统一返回、参数校验、全局异常处理、分页查询、事务管理和接口文档。

项目从基础 CRUD 出发，逐步重构为较规范的单体后端项目。

## 技术栈

- Java 21
- Spring Boot
- MyBatis
- MySQL
- Maven
- Lombok
- Spring Validation
- Swagger / OpenAPI
- Git / GitHub

## 项目功能

- 查询全部学生
- 根据学号查询学生
- 添加学生
- 修改学生
- 删除学生
- 批量删除学生
- 条件分页查询学生
- 按姓名模糊查询
- 按成绩范围查询
- 参数校验
- 统一异常处理
- 统一返回结果
- Swagger 接口文档
- 用户注册
- 用户登录
- 密码加密
- JWT 登录认证
- 学生接口鉴权保护

## 项目结构

```text
src/main/java/com/ljm/studentspringboot
├── controller      控制层，接收请求并返回响应
├── service         业务层，处理业务逻辑
│   └── impl
├── mapper          数据访问层，操作数据库
├── entity          实体类，对应数据库表
├── dto             请求参数对象
├── vo              返回结果对象
├── config          配置类，包含 PasswordEncoder 和 WebMvc 拦截器配置
├── interceptor     JWT 拦截器
├── util            工具类，包含 JWT 生成和解析
└── exception       全局异常处理和业务异常
```

## auth-version 新增内容

- 用户注册接口
- BCrypt 密码加密
- 用户登录接口
- JWT token 生成
- JWT 拦截器鉴权
- 使用 `Authorization: Bearer token字符串` 访问受保护接口
- 学生接口 `/students/**` 需要登录后携带 token 才能访问
- `/users/register` 和 `/users/login` 放行

## 主要接口

- `POST /users/register` 用户注册
- `POST /users/login` 用户登录
- `GET /students` 查询全部学生，需要携带 token
- `GET /students/{id}` 根据学号查询学生，需要携带 token
- `GET /students/page/query` 条件分页查询学生，需要携带 token
- `POST /students` 添加学生，需要携带 token
- `PUT /students/{id}` 修改学生，需要携带 token
- `DELETE /students/{id}` 删除学生，需要携带 token
- `DELETE /students/batch` 批量删除学生，需要携带 token

## 认证说明

登录成功后会返回 token，访问学生相关接口时需要在请求头中携带：

```http
Authorization: Bearer token字符串
```

当前 JWT 拦截器只保护 `/students/**` 接口；注册接口 `/users/register` 和登录接口 `/users/login` 不需要 token。
