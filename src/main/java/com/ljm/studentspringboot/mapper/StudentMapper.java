package com.ljm.studentspringboot.mapper;

import com.ljm.studentspringboot.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


import java.util.List;

@Mapper
public interface StudentMapper {

    List<Student> findAll();

    List<Student> findByCreateUserId(Long createUserId);

    List<Student> findByIds(@Param("ids") List<String> ids);

    Student findById(String id);

    int addStudent(Student student);

    int updateStudent(Student student);

    int deleteStudent(String id);

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
