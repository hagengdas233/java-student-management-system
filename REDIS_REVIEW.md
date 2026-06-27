# Redis 学习复盘与面试讲法

## 1. Redis 是什么

Redis 是一个基于内存的高性能 key-value 数据库，常用来做缓存、计数器、排行榜、分布式锁等，也可以通过 List、Pub/Sub、Stream 等能力实现一些简单消息队列场景。

在本项目中，Redis 只用于一个很基础、很常见的场景：缓存学生详情，减少重复查询 MySQL 的压力。

## 2. Redis 为什么快

Redis 快主要有几个原因：

- 数据主要存放在内存中，读写速度比磁盘数据库更快。
- Redis 的核心命令执行模型简单，单线程处理命令可以减少线程切换和锁竞争。这里主要指核心命令执行流程，后续版本在网络 I/O 等方面也做了多线程优化。
- Redis 使用高效的数据结构，例如 String、Hash、List、Set、ZSet。
- Redis 使用 I/O 多路复用，可以高效处理大量客户端连接。
- 常见操作时间复杂度低，例如根据 key 查询 String 通常是 O(1)。

面试可以这样说：

> Redis 快不是因为它能替代 MySQL，而是因为它把热点数据放在内存里，并用简单高效的执行模型处理请求。它适合做缓存，但不适合作为本项目学生数据的唯一存储。

## 3. Redis 在本项目中解决了什么问题

本项目的 `GET /students/{id}` 是学生详情查询接口。如果同一个学生详情被频繁查询，每次都访问 MySQL，会产生重复数据库查询。

Redis 在这里解决的是热点数据重复查询问题：

- 第一次查询学生详情时，从 MySQL 读取数据。
- 读取成功后，把学生详情写入 Redis。
- 后续 10 分钟内再次查询同一个学生时，直接从 Redis 返回。
- 这样可以减少 MySQL 查询次数，提高详情接口响应速度。

## 4. 本项目 Redis 缓存 key 设计

学生详情缓存 key：

```text
student:detail:{id}
```

例如：

```text
student:detail:903
```

这个 key 的含义：

- `student`：业务模块是学生。
- `detail`：缓存内容是学生详情。
- `{id}`：具体学生的学号或学生 id。

这种命名方式比较清晰，方便在 Redis 中排查和管理。

## 5. 本项目学生详情缓存流程

接口：

```http
GET /students/{id}
```

完整流程：

1. 根据学生 id 拼接缓存 key：`student:detail:{id}`。
2. 先用 `StringRedisTemplate` 查询 Redis。
3. 第一次查询时，Redis 通常未命中。
4. Redis 未命中后，查询 MySQL。
5. 如果 MySQL 中学生不存在，保持原来的业务异常逻辑。
6. 如果 MySQL 查到学生，把学生实体转换成 `StudentVO`。
7. 使用 `ObjectMapper` 把 `StudentVO` 转成 JSON 字符串。
8. 把 JSON 字符串写入 Redis。
9. 设置过期时间为 10 分钟。
10. 返回 `StudentVO`。
11. 第二次查询同一个学生时，如果 Redis 中缓存还没过期，就直接命中 Redis。
12. Redis 命中后，用 `ObjectMapper` 把 JSON 字符串转回 `StudentVO` 并返回。

简化流程：

```text
第一次查询 -> Redis 未命中 -> 查 MySQL -> 写入 Redis -> 设置 10 分钟过期 -> 返回数据
第二次查询 -> Redis 命中 -> 直接返回缓存数据
```

## 6. 修改学生时为什么要删除缓存

修改学生接口：

```http
PUT /students/{id}
```

如果修改数据库成功后不删除缓存，就可能出现这种情况：

1. Redis 中缓存的是旧学生数据。
2. MySQL 中学生数据已经被修改。
3. 用户再次查询学生详情时，接口命中 Redis。
4. 返回的仍然是旧数据。

所以本项目采用更新数据库后删除缓存的方式。

这样下一次查询时：

1. Redis 查不到旧缓存。
2. 接口重新查询 MySQL。
3. 查到新数据后重新写入 Redis。

这是一种常见的缓存更新策略，叫做 Cache Aside Pattern，也就是旁路缓存模式。

## 7. 删除学生时为什么要删除缓存

删除学生接口：

```http
DELETE /students/{id}
```

如果删除数据库成功后不删除缓存，就可能出现这种情况：

1. MySQL 中学生已经被删除。
2. Redis 中还保留这个学生的详情缓存。
3. 用户再次查询 `GET /students/{id}` 时命中 Redis。
4. 接口返回了一个数据库中已经不存在的学生。

所以删除学生成功后必须删除对应缓存：

```text
student:detail:{id}
```

这样可以避免返回脏数据。

## 8. 批量删除时如何处理缓存

批量删除接口：

```http
DELETE /students/batch?ids=401&ids=402
```

本项目已经处理了批量删除缓存。

处理方式：

1. 先检查 ids 是否为空。
2. 查询这些学生是否存在。
3. 检查当前用户是否有权限删除这些学生。
4. 数据库批量删除成功后，遍历 ids。
5. 对每个学生 id 删除对应缓存 key。

缓存 key 示例：

```text
student:detail:401
student:detail:402
```

这种做法简单直观，适合当前学习项目。

## 9. StringRedisTemplate 的作用

`StringRedisTemplate` 是 Spring Data Redis 提供的 Redis 操作工具类，专门用于操作字符串类型的 key 和 value。

本项目中 Redis 存储的是 JSON 字符串，所以使用 `StringRedisTemplate` 很合适。

本项目主要用到：

```java
stringRedisTemplate.opsForValue().get(cacheKey)
```

用于读取缓存。

```java
stringRedisTemplate.opsForValue().set(cacheKey, studentJson, Duration.ofMinutes(10))
```

用于写入缓存并设置过期时间。

```java
stringRedisTemplate.delete(cacheKey)
```

用于删除缓存。

## 10. ObjectMapper 的作用

`ObjectMapper` 是 Jackson 提供的 JSON 工具类。

本项目中 Redis 保存的是字符串，Java 代码中使用的是 `StudentVO` 对象，所以需要进行对象和 JSON 字符串之间的转换。

写入 Redis 时：

```text
StudentVO -> JSON 字符串 -> Redis
```

读取 Redis 时：

```text
Redis -> JSON 字符串 -> StudentVO
```

也就是说，`ObjectMapper` 负责序列化和反序列化。

## 11. Redis 和 MySQL 的关系

本项目中：

- MySQL 是主数据库。
- Redis 是缓存。
- Redis 过期不代表 MySQL 数据消失。
- Redis 删除缓存也不代表 MySQL 删除数据。
- 数据最终以 MySQL 为准。

可以这样理解：

```text
MySQL：负责保存真实业务数据
Redis：负责保存临时热点数据，加快查询
```

所以 Redis 的作用是提高性能，不是替代 MySQL。

## 12. 本项目代码对应位置

本项目 Redis 学习分支中的代码和配置主要对应这些位置：

- Redis 依赖：`pom.xml`
- Redis 配置：`src/main/resources/application.properties`
- 缓存逻辑：`StudentServiceImpl.findById()`
- 修改后删除缓存：`StudentServiceImpl.updateStudent()`
- 删除后删除缓存：`StudentServiceImpl.deleteStudent()`
- 批量删除缓存：`StudentServiceImpl.deleteBatch()`
- 测试用例：`test.http`

## 13. 什么是缓存穿透

缓存穿透是指查询一个缓存和数据库中都不存在的数据。

例如一直查询：

```text
student:detail:999999
```

如果 Redis 没有这个 key，MySQL 也没有这个学生，那么每次请求都会打到 MySQL。恶意请求大量不存在的 id 时，会给数据库造成压力。

常见解决方案：

- 缓存空值，例如把不存在的结果短时间缓存起来。
- 使用布隆过滤器提前判断 id 是否可能存在。
- 对请求参数做校验。

本项目当前没有专门处理缓存穿透，因为这是学习基础缓存阶段，暂时保持简单。

## 14. 什么是缓存击穿

缓存击穿是指某个热点 key 突然过期，同时有大量请求访问这个 key，导致这些请求同时打到数据库。

例如：

```text
student:detail:903
```

这个学生特别热门，缓存刚好过期，很多请求同时查询它。Redis 都未命中，于是请求都去查 MySQL。

常见解决方案：

- 热点 key 设置更长过期时间。
- 使用互斥锁，只允许一个请求回源查数据库。
- 提前刷新热点缓存。

本项目没有做分布式锁或复杂缓存一致性处理，只实现基础缓存模式。

## 15. 什么是缓存雪崩

缓存雪崩是指大量缓存 key 在同一时间过期，导致很多请求同时打到数据库。

例如大量学生详情缓存都设置了完全相同的过期时间，并且在同一时间集中失效，MySQL 压力会突然升高。

常见解决方案：

- 给缓存过期时间增加随机值，避免同一时间集中失效。
- 对热点数据设置更长过期时间。
- 做限流、降级、熔断。
- 提前预热缓存。

本项目统一设置 10 分钟过期时间，适合学习演示；生产环境可以考虑加随机过期时间。

## 16. 什么是缓存和数据库一致性问题

缓存和数据库一致性问题，是指 Redis 中的数据和 MySQL 中的数据不一致。

常见例子：

1. MySQL 中学生姓名已经改了。
2. Redis 中还保存旧姓名。
3. 查询接口命中 Redis。
4. 返回了旧数据。

本项目的处理方式：

- 修改学生成功后删除缓存。
- 删除学生成功后删除缓存。
- 下次查询再从 MySQL 加载新数据并写入 Redis。

这能解决大多数简单场景下的缓存旧数据问题。

但它不是最复杂、最强一致的方案。如果系统并发很高，还可能出现短时间不一致，需要更深入的方案，比如延迟双删、消息队列补偿、binlog 同步等。

## 17. 本项目当前缓存方案的优点和不足

优点：

- 实现简单，适合学习。
- 查询逻辑清晰：先 Redis，未命中再 MySQL。
- 能减少重复查询学生详情造成的数据库压力。
- 修改和删除后会删除缓存，避免长期返回旧数据。
- 使用 10 分钟过期时间，避免缓存长期占用内存。
- 使用 `student:detail:{id}` 作为 key，命名清晰。

不足：

- 没有处理缓存穿透。
- 没有处理热点 key 缓存击穿。
- 缓存过期时间没有随机值，理论上可能出现缓存雪崩。
- 没有做复杂并发场景下的强一致性保证。
- Redis 异常时可能影响详情查询接口。
- 当前日志使用临时打印，后续可以替换为正式日志框架。

面试可以这样说：

> 这个项目实现的是最基础的旁路缓存模式。它适合学习和中小流量场景，核心思路是查询先读缓存，缓存没有再读数据库，写操作后删除缓存。它能减少数据库压力，但还没有处理穿透、击穿、雪崩和复杂一致性问题。

## 18. 常见 Redis 面试问题和回答

### Redis 一般用来做什么？

Redis 常用来做缓存、排行榜、计数器、验证码、登录态、限流、分布式锁，也可以通过 List、Pub/Sub、Stream 等能力实现一些简单消息队列场景。

本项目中只使用 Redis 做学生详情缓存。

### 为什么不用 MySQL 直接查，还要加 Redis？

MySQL 是主数据库，适合持久化存储；Redis 是内存数据库，适合保存热点数据。对于频繁查询但不频繁变化的数据，可以先放到 Redis，减少 MySQL 查询压力。

### 本项目 Redis 缓存了什么？

缓存的是学生详情接口 `GET /students/{id}` 的返回结果，也就是 `StudentVO` 序列化后的 JSON 字符串。

### 本项目的 Redis key 是什么？

```text
student:detail:{id}
```

例如：

```text
student:detail:903
```

### 本项目缓存过期时间是多少？

10 分钟。

### 为什么缓存要设置过期时间？

防止缓存长期占用内存，也可以让数据在一定时间后自动回源 MySQL，降低旧数据长期存在的风险。

### 修改学生时为什么删除缓存，而不是更新缓存？

删除缓存更简单，也更不容易写错。修改数据库成功后删除缓存，下一次查询再从 MySQL 加载新数据并写入 Redis。

### 删除学生时为什么删除缓存？

因为 MySQL 中学生已经不存在，如果 Redis 中还保留详情缓存，后续查询可能返回已经删除的数据。

### Redis 和 MySQL 谁是准的？

MySQL 是准的。Redis 只是缓存，缓存没有了可以从 MySQL 重新加载。

### Redis 缓存失效后，数据是不是没了？

不是。Redis 缓存失效只代表缓存中的临时数据没了，MySQL 中的真实业务数据仍然存在。

### 什么是缓存穿透？

查询 Redis 和 MySQL 都不存在的数据，导致请求每次都打到数据库。

### 什么是缓存击穿？

某个热点 key 过期后，大量请求同时访问这个 key，导致请求集中打到数据库。

### 什么是缓存雪崩？

大量缓存 key 同时过期，导致大量请求同时访问数据库。

### 本项目有没有解决穿透、击穿、雪崩？

没有做复杂处理。本项目是 Redis 学习分支，只实现基础学生详情缓存，重点理解先查 Redis、未命中查 MySQL、写入缓存、修改删除后清理缓存这条主线。

### 如果 Redis 挂了怎么办？

当前项目没有做 Redis 异常降级处理。更完善的做法是捕获 Redis 异常，在 Redis 不可用时继续查询 MySQL，保证核心业务可用。

### 本项目属于哪种缓存模式？

属于旁路缓存模式，也叫 Cache Aside Pattern。

读流程：

```text
先查缓存 -> 缓存没有 -> 查数据库 -> 写入缓存
```

写流程：

```text
先修改数据库 -> 删除缓存
```

### 如何一句话介绍本项目 Redis 功能？

可以这样说：

> 我在学生管理系统中给学生详情查询加了 Redis 缓存。查询时先根据 `student:detail:{id}` 查 Redis，命中就直接返回；未命中再查 MySQL，查到后把 `StudentVO` 转成 JSON 写入 Redis，并设置 10 分钟过期时间。修改和删除学生成功后会删除对应缓存，避免返回旧数据。MySQL 仍然是主数据库，Redis 只是用来减少重复查询压力的缓存。
