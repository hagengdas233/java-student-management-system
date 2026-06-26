CREATE DATABASE IF NOT EXISTS student_system
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE student_system;

DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS student;

CREATE TABLE `user` (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         username VARCHAR(50) NOT NULL UNIQUE,
                         password VARCHAR(100) NOT NULL,
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE student (
                         id VARCHAR(20) PRIMARY KEY COMMENT '学号',
                         name VARCHAR(50) NOT NULL COMMENT '姓名',
                         age INT NOT NULL COMMENT '年龄',
                         score INT NOT NULL COMMENT '成绩',
                         create_user_id BIGINT COMMENT '创建人ID',
                         create_username VARCHAR(50) COMMENT '创建人用户名'
);

INSERT INTO student (id, name, age, score) VALUES
                                               ('1', '张三', 18, 90),
                                               ('2', '李四', 20, 85),
                                               ('3', '王五', 19, 70);
