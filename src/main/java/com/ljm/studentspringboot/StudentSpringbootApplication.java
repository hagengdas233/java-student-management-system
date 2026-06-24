package com.ljm.studentspringboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.ljm.studentspringboot.mapper")
@SpringBootApplication
public class StudentSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentSpringbootApplication.class, args);
    }
}