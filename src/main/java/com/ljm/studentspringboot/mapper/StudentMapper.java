package com.ljm.studentspringboot.mapper;

import com.ljm.studentspringboot.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface StudentMapper {

    List<Student> findAll();

    Student findById(String id);

    List<Student> findAllOrderByScoreDesc();

    List<Student> findByPageOrderByScoreDesc(@Param("offset") Integer offset,
                                             @Param("pageSize") Integer pageSize);

    int addStudent(Student student);

    int updateStudent(Student student);

    int deleteStudent(String id);

    List<Student> searchByName(String name);

    List<Student> filterByScore(@Param("minScore") Integer minScore,
                                @Param("maxScore") Integer maxScore);

    List<Student> findByPage(@Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize);
    Long count();

    List<Student> findByCondition(@Param("name") String name,
                                  @Param("minScore") Integer minScore,
                                  @Param("maxScore") Integer maxScore);

    int updateStudentSelective(Student student);

    int deleteBatch(@Param("ids") List<String> ids);

    Long countByCondition(@Param("name") String name,
                          @Param("minScore") Integer minScore,
                          @Param("maxScore") Integer maxScore);

    List<Student> findByConditionPage(@Param("name") String name,
                                      @Param("minScore") Integer minScore,
                                      @Param("maxScore") Integer maxScore,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);
}