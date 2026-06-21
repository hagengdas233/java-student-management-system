package com.ljm.student.service;

import com.ljm.student.entity.Student;

import java.util.List;

public interface StudentService {
    boolean addStudent(Student student);

    boolean deleteStudent(String id);

    boolean updateStudent(Student student);

    Student findById(String id);

    List<Student> findAll();

    List<Student> findByPage(int page, int pageSize);

    List<Student> findByNameLike(String name);

    List<Student> findAllOrderByScoreDesc();

    int countStudent();

    double avgScore();

    int maxScore();

    int minScore();

    int countPassStudent();
}