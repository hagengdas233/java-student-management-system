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
└── exception       全局异常处理和业务异常