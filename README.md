# MyBatis 学生管理系统

这是一个基于 Java + MyBatis + MySQL 的控制台学生管理系统。

## 技术栈

- Java 21
- Maven
- MyBatis 3.5.16
- MySQL 8
- IntelliJ IDEA

## 项目功能

1. 添加学生
2. 删除学生
3. 修改学生
4. 根据学号查询学生
5. 展示所有学生
6. 分页查询学生
7. 按姓名模糊查询学生
8. 按成绩降序查询学生
9. 统计学生总人数
10. 统计平均分
11. 统计最高分
12. 统计最低分
13. 统计及格人数和及格率
14. 条件查询学生
15. 条件修改学生
16. 批量删除学生
17. 批量添加学生

## 项目结构

```text
src/main/java/com/ljm/student
├── Main.java
├── entity
│   └── Student.java
├── mapper
│   └── StudentMapper.java
├── service
│   ├── StudentService.java
│   └── StudentServiceImpl.java
└── util
    └── SqlSessionFactoryUtil.java

src/main/resources
├── mybatis-config.xml
├── db.example.properties
└── mapper
    └── StudentMapper.xml