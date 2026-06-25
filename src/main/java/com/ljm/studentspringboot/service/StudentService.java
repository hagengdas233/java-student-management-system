package com.ljm.studentspringboot.service;

import com.ljm.studentspringboot.entity.PageResult;
import com.ljm.studentspringboot.dto.StudentAddDTO;
import com.ljm.studentspringboot.dto.StudentUpdateDTO;
import com.ljm.studentspringboot.dto.StudentQueryDTO;
import com.ljm.studentspringboot.vo.StudentVO;

import java.util.List;

public interface StudentService {

    List<StudentVO> findAll();

    StudentVO findById(String id);

    void deleteStudent(String id);

    void deleteBatch(List<String> ids);

    PageResult<StudentVO> pageQuery(StudentQueryDTO queryDTO);

    void addStudent(StudentAddDTO studentAddDTO);

    void updateStudent(String id, StudentUpdateDTO studentUpdateDTO);
}