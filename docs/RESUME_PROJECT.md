# 简历项目整理：学生信息与权限管理系统

## 1. 项目名称

推荐写法：

**基于 Spring Boot + JWT + Redis 的学生信息与权限管理系统**

这个名称比“学生管理系统”更偏后端，能直接体现项目的认证鉴权、缓存、数据权限和后端分层能力。

## 2. 技术栈

当前项目实际使用：

- Java 21
- Spring Boot
- Spring MVC
- MyBatis
- MySQL
- Redis
- JWT
- BCrypt
- Lombok
- Spring Validation
- Spring Transaction
- Swagger / OpenAPI
- Maven

说明：当前业务代码使用的是 MyBatis XML，并未在 `pom.xml` 中接入 MyBatis-Plus。MyBatis-Plus 可作为学习对比或 Demo 讲法，但不建议在简历技术栈中写成当前项目已正式使用。

## 3. 项目描述

本项目是一个基于 Spring Boot 的后端管理系统，围绕学生信息管理、用户注册登录、JWT 鉴权和数据权限控制进行设计与实现。项目采用 Controller / Service / Mapper 分层结构，使用 DTO / VO 区分请求对象和响应对象，并封装统一返回结果和全局异常处理。用户登录后通过 JWT 访问受保护接口，后端使用拦截器解析登录用户信息，并基于 `create_user_id` 控制学生数据归属。学生详情查询引入 Redis 缓存，缓存未命中时查询 MySQL，修改或删除学生后删除对应缓存。项目还补充了 MySQL 索引、`EXPLAIN` 执行计划、事务隔离级别、MVCC、快照读和当前读等学习实践材料。

## 4. 项目亮点

- 基于 JWT + Spring MVC 拦截器实现登录鉴权，对 `/students/**` 等受保护接口进行统一拦截和身份校验。
- 使用 BCrypt 对用户密码进行加密存储，避免明文密码落库，并通过 `PasswordEncoder.matches` 完成登录校验。
- 使用 ThreadLocal / `UserContext` 保存当前请求的登录用户信息，请求结束后清理上下文，避免线程复用导致用户信息污染。
- 基于 `student.create_user_id` 实现数据权限控制，确保当前登录用户只能修改、删除和批量删除自己创建的学生数据。
- 引入 Redis 缓存学生详情，查询时先读 `student:detail:{id}`，缓存未命中再查 MySQL；修改、删除学生后删除缓存，降低返回旧数据的风险。
- 使用 MyBatis XML 实现学生查询、条件分页、批量删除等基础业务 SQL，保留对 SQL 的可控性和可读性。
- 补充 MyBatis-Plus Demo 学习对比，理解 `BaseMapper`、`LambdaQueryWrapper`、分页查询等用法；当前业务代码仍以 MyBatis XML 为主。
- 使用 `EXPLAIN` 分析执行计划，并对 `create_user_id`、`score` 建立索引，验证按创建人查询和成绩范围查询的索引效果。
- 使用 `@Transactional` 管理添加、修改、删除、批量删除等写操作，保证业务写操作的一致性。

## 5. 项目难点和解决方案

### 5.1 JWT 如何实现登录状态

**问题背景：**  
HTTP 请求本身是无状态的，用户登录成功后，后续访问学生接口时后端需要识别当前用户是谁。

**解决方案：**  
用户登录成功后，后端使用 `JwtUtil` 生成 JWT，返回给前端。前端后续请求在 `Authorization` 请求头中携带该凭证。`JwtInterceptor` 拦截受保护接口，解析 JWT 中的 `userId` 和 `username`，校验通过后放入 `UserContext`。

**面试可讲点：**  
JWT 的作用不是保存服务端 Session，而是把用户身份信息签名后交给客户端携带。后端每次请求解析并校验签名，从而恢复当前登录用户信息。

### 5.2 为什么使用 BCrypt 存密码

**问题背景：**  
用户密码不能明文存储，普通哈希算法如果没有盐值和计算成本控制，容易被彩虹表或暴力破解攻击。

**解决方案：**  
项目使用 `BCryptPasswordEncoder` 加密密码。注册时保存 BCrypt 密文，登录时使用 `passwordEncoder.matches` 比对明文密码和密文。

**面试可讲点：**  
BCrypt 自带随机盐，并且计算成本相对更高，比 MD5 这类快速哈希更适合密码存储。

### 5.3 如何实现当前用户只能操作自己的数据

**问题背景：**  
学生数据有创建人归属，不能让一个用户修改或删除其他用户创建的数据。

**解决方案：**  
添加学生时，后端从 `UserContext` 获取当前用户 id 和用户名，写入 `create_user_id`、`create_username`。修改、删除、批量删除前，先查询学生数据并判断 `create_user_id` 是否等于当前登录用户 id，不一致则抛出业务异常。

**面试可讲点：**  
数据权限不能依赖前端传参，必须以后端解析出的登录态为准。这个项目通过 JWT + UserContext + create_user_id 串起了认证和授权。

### 5.4 Redis 缓存和 MySQL 数据一致性怎么处理

**问题背景：**  
学生详情可能被重复查询，如果每次都访问 MySQL，会产生重复数据库查询。但引入缓存后，需要避免修改或删除后返回旧数据。

**解决方案：**  
项目使用 `student:detail:{id}` 缓存学生详情 JSON，设置 10 分钟过期时间。查询详情时先查 Redis，未命中再查 MySQL 并写入缓存。修改、删除、批量删除成功后删除对应缓存。

**面试可讲点：**  
这是典型的 Cache Aside 模式。当前项目采用“更新数据库后删除缓存”的策略，适合学习和中小规模系统，不夸大成强一致高并发方案。

### 5.5 MyBatis 和 MyBatis-Plus 的区别

**问题背景：**  
项目业务代码使用 MyBatis XML，需要自己编写 SQL。学习过程中也了解了 MyBatis-Plus 的通用 CRUD 能力。

**解决方案：**  
当前项目使用 MyBatis XML 编写查询、分页、批量删除等 SQL，适合学习 SQL 和执行计划。MyBatis-Plus Demo 可用于演示 `BaseMapper` 通用 CRUD、`LambdaQueryWrapper` 条件构造和分页查询，减少简单 CRUD 样板代码。

**面试可讲点：**  
MyBatis 更强调手写 SQL 的灵活性；MyBatis-Plus 在 MyBatis 基础上提供通用 CRUD 和条件构造器，适合减少重复代码。当前项目主线是 MyBatis XML，MyBatis-Plus 是学习对比点。

### 5.6 MySQL 索引优化怎么验证

**问题背景：**  
不能只说“加索引会变快”，需要能通过工具验证索引是否被使用。

**解决方案：**  
使用 `EXPLAIN` 分析 SQL 执行计划。项目中验证了 `id` 主键查询使用 `PRIMARY`，`type=const`，`rows=1`；`create_user_id` 建索引前是 `type=ALL`，建索引后使用 `idx_student_create_user_id`，`type=ref`；`score` 范围查询建索引后可以出现 `type=range`。

**面试可讲点：**  
重点看 `type`、`possible_keys`、`key`、`rows`、`Extra`。索引优化要结合真实 SQL、数据量、执行计划来判断。

### 5.7 REPEATABLE READ 下快照读和当前读的区别

**问题背景：**  
MySQL 默认隔离级别是 `REPEATABLE READ`，普通查询和加锁查询的表现不一样。

**解决方案：**  
通过 MySQL Workbench 两个窗口实验验证：普通 `SELECT` 在同一个事务内读取一致性快照；`SELECT ... FOR UPDATE` 是当前读，会读取最新数据并加锁，可能阻塞其他事务修改。

**面试可讲点：**  
MVCC 主要服务于普通 `SELECT` 这种快照读；`FOR UPDATE`、`UPDATE`、`DELETE`、`INSERT` 属于当前读相关操作，会涉及锁。

## 6. 2 分钟项目介绍话术

我做的是一个基于 Spring Boot 的学生信息与权限管理系统，主要练习 Java 后端常见的分层开发、登录鉴权、数据权限和缓存设计。项目里我实现了用户注册登录，密码使用 BCrypt 加密，登录成功后返回 JWT，后续访问学生接口时通过拦截器解析 JWT，把当前用户信息放到 UserContext 中。

学生模块支持新增、修改、删除、批量删除、条件分页查询等功能。为了保证数据权限，添加学生时会记录创建人，修改和删除时会校验当前登录用户是否是创建人，避免操作别人的数据。项目还使用 Redis 缓存学生详情，查询时先查缓存，未命中再查 MySQL，修改或删除后删除对应缓存。

除了业务功能，我还整理了 MySQL 索引和事务的练习材料。比如用 `EXPLAIN` 验证 `create_user_id` 和 `score` 索引效果，用两个 Workbench 窗口实验 `READ COMMITTED`、`REPEATABLE READ`、MVCC、快照读和 `SELECT ... FOR UPDATE` 当前读加锁。这个项目不是大型高并发系统，主要是为了把后端实习常见的认证、权限、缓存、SQL 和事务基础打扎实。

## 7. 面试官可能追问的问题

### 1. JWT 是什么，为什么用 JWT？

JWT 是一种带签名的用户身份凭证。项目用它保存登录后的用户 id 和用户名，后端每次请求解析并校验 JWT，从而识别当前用户。

### 2. Token 放在哪里？

通常由前端放在请求头 `Authorization` 中，后端拦截器从请求头读取并解析。本项目使用 `Bearer` 格式。

### 3. JWT 过期怎么办？

后端解析 JWT 时如果发现过期，会返回“登录已过期，请重新登录”。当前项目采用重新登录获取新 JWT 的方式，没有实现刷新令牌。

### 4. BCrypt 为什么比 MD5 更适合存密码？

BCrypt 自带随机盐，并且计算成本较高，可以增加暴力破解成本。MD5 速度太快，不适合直接用于密码存储。

### 5. ThreadLocal 是什么，在项目里怎么用？

ThreadLocal 是线程本地变量。项目中 `UserContext` 使用 ThreadLocal 保存当前请求解析出的 `userId` 和 `username`，让 Service 层可以直接获取当前登录用户。

### 6. 为什么请求结束要清理 UserContext？

Web 容器线程会复用。如果不清理 ThreadLocal，可能导致上一次请求的用户信息残留到下一次请求，造成数据错乱或权限风险。

### 7. 数据权限怎么实现？

添加学生时记录 `create_user_id`。修改、删除、批量删除前先查询学生归属，判断 `create_user_id` 是否等于当前登录用户 id，不一致就抛出无权限异常。

### 8. Redis 在项目里缓存了什么？

缓存学生详情接口返回的 `StudentVO` JSON，减少重复查询同一学生详情时对 MySQL 的访问。

### 9. Redis key 怎么设计？

使用 `student:detail:{id}`，例如 `student:detail:903`。前缀表示业务模块和缓存类型，id 表示具体学生。

### 10. Redis 缓存什么时候删除？

修改学生、删除学生、批量删除学生成功后删除对应学生详情缓存，避免后续查询读到旧数据。

### 11. 缓存穿透、击穿、雪崩是什么？

缓存穿透是查询不存在的数据，每次都打到数据库；缓存击穿是热点 key 失效瞬间大量请求打到数据库；缓存雪崩是大量 key 同时失效或 Redis 不可用导致请求集中访问数据库。

### 12. 当前项目怎么处理缓存穿透？

当前项目没有做完整的缓存穿透治理。如果要改进，可以对不存在的数据缓存空值并设置短 TTL，或者使用布隆过滤器。

### 13. MyBatis 和 MyBatis-Plus 区别？

MyBatis 主要通过 XML 或注解手写 SQL，灵活但样板代码多。MyBatis-Plus 基于 MyBatis 提供 `BaseMapper`、条件构造器、分页等通用能力，可以减少简单 CRUD 代码。当前项目业务代码使用 MyBatis XML。

### 14. EXPLAIN 看哪些字段？

重点看 `type`、`possible_keys`、`key`、`rows`、`Extra`。它们分别能反映访问类型、可能使用的索引、实际使用的索引、预估扫描行数和额外执行信息。

### 15. type=const/ref/range/ALL 分别是什么意思？

`const` 通常是主键或唯一索引等值查询，效率很高；`ref` 是普通索引等值查询；`range` 是索引范围查询；`ALL` 是全表扫描，通常需要重点关注。

### 16. 为什么 create_user_id 适合建索引？

项目有 `/students/my` 按当前用户查询自己创建的学生，本质是按 `create_user_id` 过滤。这个字段作为高频查询条件，适合建立普通索引减少扫描行数。

### 17. 为什么 LIKE '%张%' 不容易走普通索引？

普通 B+ 树索引依赖从左到右的有序匹配。`'%张%'` 左边有通配符，MySQL 很难定位索引扫描起点，所以通常不能有效使用普通索引。

### 18. 什么是事务？

事务是一组数据库操作的执行单元，要么全部成功提交，要么失败回滚，用来保证数据一致性。

### 19. @Transactional 有什么作用？

`@Transactional` 声明事务边界。方法正常执行完提交事务，抛出运行时异常时回滚事务。本项目写操作使用它保证一致性。

### 20. READ COMMITTED 和 REPEATABLE READ 区别？

`READ COMMITTED` 每次查询都能看到其他事务已提交的新数据，可能出现不可重复读。`REPEATABLE READ` 下同一事务内普通 `SELECT` 通常看到一致性快照，可以避免不可重复读。

### 21. 什么是快照读和当前读？

普通 `SELECT` 一般是快照读，读取一致性快照，不加锁。`SELECT ... FOR UPDATE`、`UPDATE`、`DELETE`、`INSERT` 属于当前读相关操作，读取最新数据并可能加锁。

### 22. SELECT ... FOR UPDATE 为什么会阻塞其他事务？

`FOR UPDATE` 是当前读，会对读取到的记录或范围加锁。在当前事务提交或回滚前，其他事务修改相关数据可能需要等待锁释放。

### 23. 批量删除为什么更需要事务？

批量删除涉及多条数据。如果中间出错，不能只删除一部分。事务可以保证要么全部删除成功，要么失败回滚。

### 24. DTO 和 VO 为什么要分开？

DTO 面向请求参数，VO 面向响应结果。分开后可以避免前端传入不该传的字段，也能控制返回给前端的数据结构。

### 25. 全局异常处理有什么作用？

全局异常处理可以统一捕获业务异常和参数校验异常，返回统一格式的错误响应，避免每个 Controller 重复写 try-catch。

## 8. 简历写法

**项目名称：**  
基于 Spring Boot + JWT + Redis 的学生信息与权限管理系统

**技术栈：**  
Spring Boot、Spring MVC、MyBatis、MySQL、Redis、JWT、BCrypt、Lombok、Spring Validation、Spring Transaction、Swagger / OpenAPI、Maven

**项目描述：**  
该项目是一个面向学生信息管理场景的 Java 后端系统，支持用户注册登录、JWT 鉴权、学生信息 CRUD、条件分页查询、数据权限控制和 Redis 学生详情缓存。系统采用 Controller / Service / Mapper 分层架构，使用 DTO / VO 分离请求和响应模型，并通过统一返回结果和全局异常处理提升接口规范性。

**核心工作：**

- 实现用户注册登录流程，使用 BCrypt 加密存储密码，登录成功后生成 JWT。
- 基于 Spring MVC 拦截器解析 JWT，将当前用户信息保存到 `UserContext`，并在请求结束后清理 ThreadLocal。
- 实现学生信息新增、修改、删除、批量删除、分页查询等接口。
- 基于 `create_user_id` 实现数据权限控制，限制用户只能操作自己创建的学生数据。
- 使用 Redis 缓存学生详情，缓存未命中时查询 MySQL，修改或删除后删除对应缓存。
- 使用 MyBatis XML 编写业务 SQL，并整理 MySQL 索引、事务、MVCC 和锁相关实验材料。

**项目亮点：**

- 通过 JWT + 拦截器实现统一鉴权，避免在每个接口中重复编写登录校验逻辑。
- 使用 ThreadLocal 保存当前用户上下文，在 Service 层完成数据归属校验。
- 使用 Redis Cache Aside 思路优化学生详情查询，并通过更新数据库后删除缓存降低脏数据风险。
- 使用 `@Transactional` 管理写操作，保证添加、修改、删除、批量删除等操作的一致性。
- 使用 `EXPLAIN` 分析执行计划，对 `create_user_id` 和 `score` 建立索引并验证查询效果。
- 通过 MySQL Workbench 实验理解 `READ COMMITTED`、`REPEATABLE READ`、MVCC、快照读、当前读和 `SELECT ... FOR UPDATE` 加锁行为。
