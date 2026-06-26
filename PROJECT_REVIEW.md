# Student Spring Boot 项目复盘

## 1. 项目整体介绍

本项目是一个基于 Spring Boot + MyBatis + MySQL 的学生管理系统，主要实现学生信息管理、用户注册登录、JWT 鉴权、当前用户上下文、学生创建人记录以及基础数据权限控制。

项目从基础 CRUD 开始，逐步补充统一返回、参数校验、异常处理、分页查询、DTO/VO 分层、接口文档、密码加密、JWT 登录认证和基于创建人的数据权限判断，适合作为 Java 后端入门到进阶的练习项目。

## 2. 技术栈说明

- Java 21：项目运行语言。
- Spring Boot：提供 Web 应用基础能力和自动配置。
- Spring MVC：实现 Controller 接口层。
- MyBatis：负责 SQL 映射和数据库访问。
- MySQL：存储用户和学生数据。
- Spring Validation：使用 `@Valid` 和校验注解完成参数校验。
- Lombok：减少 getter、setter、构造方法等样板代码。
- BCrypt：对用户密码进行单向加密存储。
- JJWT：生成和解析 JWT token。
- ThreadLocal：保存当前请求中的登录用户信息。
- Swagger / OpenAPI：生成接口文档。
- Maven：项目构建和依赖管理。

## 3. 分层职责说明

- Controller：接收 HTTP 请求，接收 DTO 参数，调用 Service，返回 `Result<T>`。
- Service：处理业务逻辑，例如注册判重、密码加密、登录校验、创建人记录、权限判断。
- Mapper：负责数据库操作，只做 SQL 映射，不写业务判断。
- DTO：请求参数对象，例如注册、登录、学生新增和修改请求。
- VO：响应数据对象，例如登录返回信息、学生返回信息。
- Entity：数据库实体对象，字段对应数据库表结构。

## 4. 注册流程

1. 前端调用 `POST /users/register`，提交 `username` 和 `password`。
2. Controller 使用 `@Valid` 校验请求参数。
3. Service 根据 `username` 查询用户是否存在。
4. 如果用户名已存在，抛出 `BusinessException("用户名已存在")`。
5. 如果用户名不存在，使用 BCrypt 加密密码。
6. Mapper 将用户写入数据库。
7. 插入失败时抛出 `BusinessException("注册失败")`。
8. 注册成功返回 `Result.success()`。

## 5. BCrypt 密码加密流程

项目通过 `PasswordEncoderConfig` 注册 `BCryptPasswordEncoder`。

注册时：

```java
passwordEncoder.encode(userRegisterDTO.getPassword())
```

登录时：

```java
passwordEncoder.matches(rawPassword, encodedPassword)
```

BCrypt 的特点是同一个明文密码每次加密结果都不同，因为算法内部会生成随机盐值。数据库中只保存密文，不保存明文密码。

## 6. 登录流程

1. 前端调用 `POST /users/login`，提交用户名和密码。
2. Service 根据用户名查询用户。
3. 用户不存在时，抛出 `BusinessException("用户名或密码错误")`。
4. 用户存在时，使用 `passwordEncoder.matches` 校验密码。
5. 密码错误时，仍然抛出 `BusinessException("用户名或密码错误")`，避免暴露具体原因。
6. 登录成功后生成 JWT token。
7. 返回 `UserLoginVO`，包含 `id`、`username` 和 `token`。

## 7. JWT 生成和校验流程

JWT 生成：

1. 登录成功后调用 `JwtUtil.generateToken(userId, username)`。
2. token 中写入 `userId`、`username`、签发时间和过期时间。
3. 使用 HS256 和固定密钥签名。
4. 返回给前端保存。

JWT 校验：

1. 前端访问受保护接口时携带请求头：

```http
Authorization: Bearer your_token
```

2. `JwtInterceptor` 从请求头中取出 token。
3. 判断请求头是否存在、格式是否正确。
4. 调用 `JwtUtil.parseToken(token)` 解析并校验签名和过期时间。
5. token 无效或过期时返回统一错误结果。
6. token 合法时放行请求。

## 8. JwtUtil / JwtInterceptor / WebConfig 的区别

- `JwtUtil`：工具类，只负责生成和解析 JWT，不关心 HTTP 请求。
- `JwtInterceptor`：拦截器，负责从请求头中获取 token，调用 `JwtUtil` 校验，并把用户信息保存到 `UserContext`。
- `WebConfig`：Spring MVC 配置类，负责注册 `JwtInterceptor`，配置哪些路径需要拦截，哪些路径放行。

简单理解：

- `JwtUtil` 负责 token 本身。
- `JwtInterceptor` 负责请求鉴权。
- `WebConfig` 负责拦截规则。

## 9. UserContext / ThreadLocal 的作用

`UserContext` 使用 `ThreadLocal` 保存当前请求的登录用户信息，包括 `userId` 和 `username`。

作用：

- Controller 和 Service 不需要前端传 `userId`。
- Service 可以直接通过 `UserContext.getUserId()` 获取当前登录用户。
- 添加学生时可以自动记录创建人。
- 查询当前用户创建的学生时可以根据当前用户 id 查询。
- 修改、删除、批量删除时可以判断数据归属。

请求结束后，`JwtInterceptor.afterCompletion` 会调用 `UserContext.clear()` 清理数据，避免线程复用导致用户信息残留。

## 10. 学生创建人记录功能

`student` 表新增：

- `create_user_id`：创建人 ID。
- `create_username`：创建人用户名。

添加学生时，前端只提交学生基本信息，不传创建人字段。后端从 `UserContext` 获取当前登录用户：

```java
student.setCreateUserId(UserContext.getUserId());
student.setCreateUsername(UserContext.getUsername());
```

这样可以保证创建人信息由后端可信来源生成，而不是由前端伪造。

## 11. 查询当前用户创建的学生

接口：

```http
GET /students/my
Authorization: Bearer your_token
```

流程：

1. `JwtInterceptor` 校验 token。
2. 校验通过后将 `userId` 和 `username` 保存到 `UserContext`。
3. `StudentServiceImpl.findMyStudents()` 从 `UserContext.getUserId()` 获取当前用户 id。
4. Mapper 根据 `create_user_id` 查询当前用户创建的学生。
5. 返回 `List<StudentVO>`。

该功能体现了数据归属的概念。

## 12. 修改 / 删除 / 批量删除的数据权限控制

目标：当前用户只能操作自己创建的学生。

单个修改：

1. 根据学生 id 查询学生。
2. 学生不存在时抛出 `BusinessException("学生不存在")`。
3. 从 `UserContext` 获取当前用户 id。
4. 判断 `student.createUserId` 是否等于当前用户 id。
5. 不相等时抛出 `BusinessException("无权限操作该学生")`。
6. 相等时允许修改。

单个删除：

1. 根据学生 id 查询学生。
2. 不存在时抛出 `BusinessException("学生不存在")`。
3. 判断创建人是否是当前用户。
4. 不是当前用户时抛出 `BusinessException("无权限操作该学生")`。
5. 是当前用户时允许删除。

批量删除：

1. 校验 ids 不能为空。
2. 根据 ids 查询学生列表。
3. 查询数量和 ids 数量不一致时，抛出 `BusinessException("部分学生不存在")`。
4. 遍历学生列表，判断是否全部由当前用户创建。
5. 如果包含其他用户创建的学生，抛出 `BusinessException("无权限操作部分学生")`。
6. 全部校验通过后执行批量删除。

## 13. 认证和授权的区别

认证 authentication：

- 判断用户是谁。
- 判断用户是否已经登录。
- 本项目中由 JWT 和 `JwtInterceptor` 完成。

授权 authorization：

- 判断当前用户能不能做某个操作。
- 例如是否能修改、删除某个学生。
- 本项目中由 `StudentServiceImpl` 根据 `UserContext` 和 `createUserId` 完成。

一句话概括：

- 认证解决“你是谁”。
- 授权解决“你能做什么”。

## 14. 项目亮点

- DTO / VO / Entity 分层清晰。
- `Result<T>` 统一接口返回格式。
- `PageResult<T>` 封装分页结果。
- `@Valid` 参数校验。
- `GlobalExceptionHandler` 全局异常处理。
- `BusinessException` 表达业务异常。
- `@Transactional` 管理写操作事务。
- BCrypt 加密密码，避免明文存储。
- JWT 实现无状态登录认证。
- 拦截器统一保护学生接口。
- ThreadLocal 保存当前请求用户信息。
- 添加学生自动记录创建人。
- 支持查询当前用户创建的学生。
- 支持修改、删除、批量删除的数据权限控制。
- Swagger / OpenAPI 提供接口文档。

## 15. 常见面试问题和回答

### 1. 这个项目主要做了什么？

这是一个 Spring Boot 学生管理系统，除了基础学生 CRUD，还实现了用户注册登录、BCrypt 密码加密、JWT 鉴权、ThreadLocal 当前用户上下文、学生创建人记录和基于创建人的数据权限控制。

### 2. 为什么要使用 DTO 和 VO？

DTO 用来接收前端请求，VO 用来返回前端响应，Entity 对应数据库表。这样可以避免数据库结构直接暴露给前端，也方便做参数校验和响应字段控制。

### 3. 为什么密码不能明文存储？

明文密码一旦数据库泄露，用户账号会直接暴露。使用 BCrypt 后，数据库保存的是不可逆密文，即使泄露也不能直接还原用户密码。

### 4. BCrypt 和普通 MD5 有什么区别？

BCrypt 会自动加盐，并且计算成本更高，适合密码存储。MD5 速度太快，容易被暴力破解和彩虹表攻击，不适合保存密码。

### 5. JWT 登录的基本原理是什么？

用户登录成功后，服务端生成包含用户信息和过期时间的 token 并签名。前端后续请求携带 token，服务端解析并校验签名，如果合法就认为用户已登录。

### 6. JwtInterceptor 做了什么？

它从请求头读取 `Authorization`，校验 `Bearer token` 格式，解析 JWT，失败时返回错误，成功时把 `userId` 和 `username` 放入 `UserContext` 并放行请求。

### 7. 为什么要使用 ThreadLocal？

因为一次请求通常由一个线程处理，ThreadLocal 可以把当前登录用户信息绑定到当前线程，Service 层可以随时获取当前用户，不需要每个方法都传 userId。

### 8. ThreadLocal 使用时要注意什么？

请求结束必须调用 `remove()` 清理，否则在线程池复用线程时，可能出现上一个请求的用户信息残留，导致数据错乱或安全问题。

### 9. 添加学生时为什么不让前端传 createUserId？

前端传入的数据不可信，用户可以伪造别人的 userId。创建人必须由后端从 JWT 解析后的 `UserContext` 中获取，保证数据来源可信。

### 10. 如何实现“只能修改自己创建的学生”？

修改前先根据学生 id 查询学生，拿到 `createUserId`，再和 `UserContext.getUserId()` 比较。如果不相等，就抛出无权限异常；相等才允许修改。

### 11. 批量删除为什么要先查询学生列表？

因为要同时校验两个问题：学生是否都存在，以及这些学生是否都属于当前用户。只有全部存在且全部由当前用户创建，才允许批量删除。

### 12. 认证和授权有什么区别？

认证是判断用户是否登录，授权是判断用户是否有权限操作某个资源。本项目中 JWT 拦截器负责认证，Service 中的创建人校验负责授权。

### 13. 为什么业务判断放在 Service，不放在 Controller？

Controller 只负责接收请求和返回响应，Service 承担业务规则。这样分层更清晰，也方便后续复用业务逻辑和测试。

### 14. Mapper 为什么不写权限逻辑？

Mapper 只负责数据库访问，权限判断属于业务规则，应放在 Service。这样 SQL 层职责更单一，业务逻辑也更容易维护。

### 15. 这个项目还可以继续优化什么？

可以把 JWT 密钥和过期时间放到配置文件；增加刷新 token；增加统一错误码；补充单元测试和接口测试；增加角色权限；完善批量接口的事务和并发处理；增加日志审计。
