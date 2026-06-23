package com.ljm.student.service;

import com.ljm.student.entity.Student;
import java.util.List;

public interface StudentService {
    List<Student> findAll();

    Student findById(String id);

    boolean addStudent(Student student);

    boolean deleteStudent(String id);

    boolean updateStudent(Student student);

    List<Student> findByPage(int page, int pageSize);

    List<Student> findByNameLike(String name);

    List<Student> findAllOrderByScoreDesc();

    int countStudent();

    double avgScore();

    int maxScore();

    int minScore();

    int countPassStudent();

    List<Student> findByCondition(String name, Integer minScore, Integer maxScore);

    boolean updateStudentSelective(Student student);

    boolean deleteByIds(List<String> ids);

    boolean addStudents(List<Student> students);
}