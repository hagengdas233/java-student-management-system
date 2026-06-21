package com.ljm.student.dao;

import com.ljm.student.entity.Student;

import java.util.List;

public interface StudentDao {
    List<Student> findAll();
    Student findById(String id);
    boolean addStudent(Student s);
    boolean deleteStudent(String id);
    boolean updateStudent(Student s);
    List<Student> findByPage(int page, int pageSize);
    List<Student> findByNameLike(String name);
    List<Student> findAllOrderByScoreDesc();
    int countStudent();
    double avgScore();
    int maxScore();
    int minScore();
    int countPassStudent();

}