package com.ljm.studentspringboot.service;

import com.ljm.studentspringboot.entity.Student;
import com.ljm.studentspringboot.entity.PageResult;

import java.util.List;

public interface StudentService {

    List<Student> findAll();

    Student findById(String id);

    List<Student> findAllOrderByScoreDesc();

    PageResult findByPageOrderByScoreDesc(Integer page, Integer pageSize);

    PageResult findByConditionPage(String name,
                                   Integer minScore,
                                   Integer maxScore,
                                   Integer page,
                                   Integer pageSize);

    List<Student> searchByName(String name);

    List<Student> filterByScore(Integer minScore, Integer maxScore);

    int addStudent(Student student);

    int updateStudent(Student student);

    int deleteStudent(String id);

    PageResult findByPage(Integer page, Integer pageSize);

    List<Student> findByCondition(String name, Integer minScore, Integer maxScore);

    int updateStudentSelective(Student student);

    int deleteBatch(List<String> ids);
}