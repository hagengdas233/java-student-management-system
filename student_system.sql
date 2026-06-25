CREATE DATABASE IF NOT EXISTS student_system
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE student_system;

DROP TABLE IF EXISTS student;

CREATE TABLE student (
                         id VARCHAR(20) PRIMARY KEY COMMENT '学号',
                         name VARCHAR(50) NOT NULL COMMENT '姓名',
                         age INT NOT NULL COMMENT '年龄',
                         score INT NOT NULL COMMENT '成绩'
);

INSERT INTO student (id, name, age, score) VALUES
                                               ('1', '张三', 18, 90),
                                               ('2', '李四', 20, 85),
                                               ('3', '王五', 19, 70);