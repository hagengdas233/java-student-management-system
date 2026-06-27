# Student Spring Boot 学生管理系统

## 项目介绍

本项目是一个基于 Spring Boot + MyBatis + MySQL 的学生管理系统，主要用于练习 Java 后端项目开发中的分层架构、RESTful 接口设计、统一返回、参数校验、全局异常处理、分页查询、事务管理和接口文档。

项目从基础 CRUD 出发，逐步重构为较规范的单体后端项目。目前 auth-version 分支已支持用户注册、用户登录和 JWT 鉴权，学生相关接口需要登录后携带 token 才能访问。

## 技术栈

- Java 21
- Spring Boot
- MyBatis
- MySQL
- Maven
- Spring Validation
- Lombok
- Swagger / OpenAPI
- BCrypt
- JWT
- ThreadLocal
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
- BCrypt 密码加密存储
- 用户登录
- 登录成功返回 JWT token
- JWT 拦截器保护学生接口
- 当前登录用户查询 `/users/me`
- UserContext 保存当前请求用户信息
- 添加学生时自动记录创建人
- 查询学生时返回创建人信息
- 查询当前用户创建的学生
- 当前登录用户只能修改自己创建的学生
- 当前登录用户只能删除自己创建的学生
- 修改或删除别人创建的学生时返回 `无权限操作该学生`
- 批量删除学生时进行数据权限校验
- 当前登录用户只能批量删除自己创建的学生
- 批量删除时如果包含不存在的学生，返回 `部分学生不存在`
- 批量删除时如果包含别人创建的学生，返回 `无权限操作部分学生`

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
- BCrypt 密码加密存储
- 用户登录接口
- JWT token 生成
- JWT 拦截器鉴权
- UserContext 当前用户上下文
- `/users/me` 当前登录用户查询接口
- 添加学生时自动记录创建人
- 查询学生时返回 `createUserId` 和 `createUsername`
- `GET /students/my` 查询当前用户创建的学生
- 修改/删除学生前校验当前用户是否为创建人
- 使用 `Authorization: Bearer token字符串` 访问受保护接口
- 学生接口 `/students/**` 需要登录后携带 token 才能访问
- `/users/register` 和 `/users/login` 放行

## 主要接口

- `POST /users/register` 用户注册
- `POST /users/login` 用户登录
- `GET /users/me` 查询当前登录用户，需要携带 token
- `GET /students` 查询全部学生，需要携带 token
- `GET /students/my` 查询当前用户创建的学生，需要携带 token
- `GET /students/{id}` 根据学号查询学生，需要携带 token
- `GET /students/page/query` 条件分页查询学生，需要携带 token
- `POST /students` 添加学生，需要携带 token，后端会自动记录创建人
- `PUT /students/{id}` 修改学生，需要 token，且只能修改自己创建的学生
- `DELETE /students/{id}` 删除学生，需要 token，且只能删除自己创建的学生
- `DELETE /students/batch` 批量删除学生，需要携带 `Authorization: Bearer token`，且只能删除当前登录用户创建的学生；`ids` 中如果包含不存在的学生，返回 `部分学生不存在`；如果包含其他用户创建的学生，返回 `无权限操作部分学生`

## MyBatis-Plus 学习演示分支

`mybatis-plus-demo` 分支只用于学习 MyBatis-Plus，不影响原来的 `/students/**` 正式业务接口。

原来的 `/students/**` 接口仍然保留，继续使用原生 MyBatis Mapper + XML SQL，并包含 JWT 鉴权、`UserContext` 当前用户上下文、学生创建人记录和数据权限控制。

新增的 `/mp/students/**` 是 MyBatis-Plus 学习接口，只演示 MyBatis-Plus 的基础 CRUD、条件查询和分页查询，不做 JWT 鉴权和数据权限控制。

本分支新增内容：

- 引入 MyBatis-Plus 依赖：`mybatis-plus-spring-boot4-starter`，并引入分页解析所需的 `mybatis-plus-jsqlparser`
- `Student` 实体类增加 MyBatis-Plus 注解：`@TableName`、`@TableId`、`@TableField`
- 新增 `StudentPlusMapper`，继承 `BaseMapper<Student>`，不编写 XML
- 新增 `StudentPlusController`，统一提供 `/mp/students/**` 学习接口
- 新增 `MyBatisPlusConfig`，配置 MyBatis-Plus 分页插件
- 在 `test.http` 中补充 MyBatis-Plus 测试用例

### MyBatis-Plus 学习接口

- `GET /mp/students/{id}`：根据 id 查询学生，使用 `selectById`
- `POST /mp/students`：新增学生，使用 `insert`
- `PUT /mp/students/{id}`：修改学生，使用 `LambdaUpdateWrapper`
- `DELETE /mp/students/{id}`：删除学生，使用 `deleteById`
- `GET /mp/students`：条件查询，使用 `LambdaQueryWrapper`
- `GET /mp/students/page`：分页查询，使用 `Page + selectPage`

### MyBatis-Plus 知识点

- `BaseMapper`：MyBatis-Plus 提供的基础 CRUD Mapper，继承后可直接使用 `insert`、`selectById`、`update`、`deleteById`、`selectPage` 等方法。
- `@TableName`：指定实体类对应的数据库表，例如 `@TableName("student")`。
- `@TableId`：指定主键字段，例如学生表的 `id`。
- `@TableField`：指定 Java 字段和数据库字段的映射关系，例如 `createUserId` 对应 `create_user_id`。
- `LambdaQueryWrapper`：用 Java 代码构建查询条件，类似动态 SQL，适合处理姓名模糊查询、成绩范围查询等常见条件。
- `LambdaUpdateWrapper`：用 Java 代码构建更新条件和更新字段，本分支中用于按 id 修改学生信息。
- `Page + selectPage`：MyBatis-Plus 的分页查询方式，`Page` 封装页码和每页数量，`selectPage` 返回总数和当前页记录。

### MyBatis 和 MyBatis-Plus 的区别

- MyBatis 需要手写 Mapper 方法和 XML SQL，适合精确控制复杂 SQL。
- MyBatis-Plus 是 MyBatis 的增强工具，适合简化基础 CRUD 和常见条件查询。
- 复杂 SQL 仍然可以继续使用 MyBatis XML 手写，不需要为了使用 MyBatis-Plus 而删除原来的 XML。

## 认证说明

登录成功后会返回 token，访问学生相关接口时需要在请求头中携带：

```http
Authorization: Bearer token字符串
```

当前 JWT 拦截器只保护 `/students/**` 接口；注册接口 `/users/register` 和登录接口 `/users/login` 不需要 token。

## 认证流程说明

- 注册时先检查用户名是否重复，用户名可用时使用 BCrypt 加密密码后存储。
- 登录时根据用户名查询用户，并使用 `passwordEncoder.matches` 校验明文密码和数据库中的 BCrypt 密文。
- 登录成功后使用 `JwtUtil` 生成 JWT token。
- 访问 `/students/**` 时需要携带 `Authorization: Bearer token` 请求头。
- `JwtInterceptor` 负责校验 token 是否有效。
- token 校验通过后，`JwtInterceptor` 会将 `userId`、`username` 保存到 `UserContext`。
- `StudentServiceImpl` 添加学生时从 `UserContext` 读取当前登录用户，并写入学生创建人字段。
- 查询当前用户创建的学生时，后端从 `UserContext` 获取当前登录用户 id，根据 `student.create_user_id` 查询数据。
- 请求结束后清理 `UserContext`，避免线程复用导致用户信息残留。

## 认证和授权

- 认证 authentication：判断用户是否登录，例如是否携带合法的 JWT token。
- 授权 authorization：判断当前用户是否有权限操作某个学生，例如只能修改、删除或批量删除自己创建的学生。
- `JwtInterceptor` 负责认证。
- `StudentServiceImpl` 中的修改、删除、批量删除权限判断负责授权。

学生修改、删除和批量删除的授权逻辑：

- `JwtInterceptor` 解析 token 后把 `userId`、`username` 保存到 `UserContext`。
- `StudentServiceImpl` 在修改/删除学生前，会先根据学生 id 查询学生。
- 判断 `student.createUserId` 是否等于 `UserContext.getUserId()`。
- 如果不相等，抛出 `BusinessException("无权限操作该学生")`。
- 批量删除学生时，`StudentServiceImpl` 会先根据 ids 查询学生列表。
- 如果查询结果数量和 ids 数量不一致，抛出 `BusinessException("部分学生不存在")`。
- 如果存在不是当前用户创建的学生，抛出 `BusinessException("无权限操作部分学生")`。

## 请求头示例

```http
Authorization: Bearer your_token
```

## 添加学生接口示例

前端添加学生时不需要传 `createUserId` 和 `createUsername`，这两个字段由后端从 token 中解析当前登录用户后自动写入。

```http
POST /students
Authorization: Bearer your_token
Content-Type: application/json

{
  "id": "200",
  "name": "创建人测试",
  "age": 20,
  "score": 88
}
```

## student 表结构补充

`auth-version` 中的 `student` 表包含创建人记录字段：

- `create_user_id BIGINT COMMENT '创建人ID'`
- `create_username VARCHAR(50) COMMENT '创建人用户名'`

查询学生时会返回对应的 `createUserId` 和 `createUsername`。

## 查询当前用户创建的学生

`GET /students/my` 用于查询当前登录用户创建的学生。该接口需要携带 token，后端不会接收前端传入的 `createUserId`，而是从 JWT 解析后的 `UserContext` 获取当前登录用户 id，再根据 `student.create_user_id` 查询当前用户创建的学生。

```http
GET /students/my
Authorization: Bearer your_token
```

该接口体现了“数据归属”的概念：学生数据保留创建人信息，查询当前用户创建的数据时由后端根据登录态判断归属。

## 批量删除接口示例

```http
DELETE /students/batch?ids=401&ids=402
Authorization: Bearer your_token
```

批量删除错误返回示例：

```json
{
  "code": 500,
  "message": "无权限操作部分学生",
  "data": null
}
```

```json
{
  "code": 500,
  "message": "部分学生不存在",
  "data": null
}
```

## 项目亮点

- DTO / VO 分层
- `Result<T>` 统一返回
- `PageResult<T>` 分页封装
- `@Valid` 参数校验
- `GlobalExceptionHandler` 全局异常处理
- `BusinessException` 业务异常
- `@Transactional` 事务管理
- BCrypt 密码加密
- JWT + 拦截器鉴权
- ThreadLocal 当前用户上下文
- JWT + UserContext 当前登录用户识别
- 添加学生自动记录操作用户
- 基于 UserContext 实现学生数据权限控制
- 支持单个修改、单个删除、批量删除的数据权限校验
- 当前用户只能操作自己创建的数据
- Swagger 接口文档
