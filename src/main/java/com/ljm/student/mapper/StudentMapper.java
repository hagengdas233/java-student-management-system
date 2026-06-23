package com.ljm.student.mapper;

import com.ljm.student.entity.Student;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface StudentMapper {
    List<Student> findAll();

    Student findById(String id);

    int addStudent(Student student);

    int deleteStudent(String id);

    int updateStudent(Student student);

    List<Student>findByPage(@Param("offset") int offset,@Param("pageSize")int  pageSize);

    List<Student> findByNameLike(String name);

    List<Student> findAllOrderByScoreDesc();

    int countStudent();

    double avgScore();

    int maxScore();

    int minScore();

    int countPassStudent();

    List<Student> findByCondition(@Param("name") String name,
                                  @Param("minScore") Integer minScore,
                                  @Param("maxScore") Integer maxScore);

    int updateStudentSelective(Student student);

    int deleteByIds(@Param("ids") List<String> ids);

    int addStudents(@Param("students") List<Student> students);
}