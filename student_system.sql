CREATE DATABASE IF NOT EXISTS student_system;

USE student_system;

DROP TABLE IF EXISTS student;

CREATE TABLE student (
                         id VARCHAR(20) PRIMARY KEY,
                         name VARCHAR(20) NOT NULL,
                         age INT NOT NULL,
                         score INT NOT NULL
);

INSERT INTO student(id, name, age, score) VALUES
                                              ('1', 'ljm', 18, 65),
                                              ('2', '王五', 20, 85),
                                              ('3', '张三', 25, 95);