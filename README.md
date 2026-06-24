# student-springboot

基于 Spring Boot + MyBatis + MySQL 的学生管理系统后端项目。

## 技术栈

- Java 21
- Spring Boot
- MyBatis
- MySQL
- Maven

## 功能

- 查询全部学生
- 根据 id 查询学生
- 添加学生
- 修改学生
- 删除学生
- 批量删除
- 姓名模糊查询
- 成绩范围查询
- 动态 SQL 多条件查询
- 分页查询
- 排序查询
- 条件分页查询
- 统一返回 Result
- 全局异常处理
- 基础业务校验

## 数据库表

```sql
create table student (
    id varchar(20) primary key,
    name varchar(50) not null,
    age int,
    score int
);