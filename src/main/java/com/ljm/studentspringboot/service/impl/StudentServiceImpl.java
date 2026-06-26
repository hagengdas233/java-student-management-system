package com.ljm.studentspringboot.service.impl;

import com.ljm.studentspringboot.dto.StudentAddDTO;
import com.ljm.studentspringboot.dto.StudentQueryDTO;
import com.ljm.studentspringboot.dto.StudentUpdateDTO;
import com.ljm.studentspringboot.entity.Student;
import com.ljm.studentspringboot.mapper.StudentMapper;
import com.ljm.studentspringboot.service.StudentService;
import com.ljm.studentspringboot.util.UserContext;
import com.ljm.studentspringboot.vo.StudentVO;
import org.springframework.stereotype.Service;

import java.util.List;
import com.ljm.studentspringboot.exception.BusinessException;import com.ljm.studentspringboot.entity.PageResult;import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    private StudentVO toVO(Student student) {
        StudentVO vo = new StudentVO();
        vo.setId(student.getId());
        vo.setName(student.getName());
        vo.setAge(student.getAge());
        vo.setScore(student.getScore());
        vo.setCreateUserId(student.getCreateUserId());
        vo.setCreateUsername(student.getCreateUsername());
        return vo;
    }

    private List<StudentVO> toVOList(List<Student> students) {
        return students.stream()
                .map(this::toVO)
                .toList();
    }

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentVO> findAll() {
        List<Student> students = studentMapper.findAll();
        return toVOList(students);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentVO findById(String id) {
        Student student = studentMapper.findById(id);

        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        return toVO(student);
    }

    @Transactional
    @Override
    public void deleteStudent(String id) {
        int rows = studentMapper.deleteStudent(id);

        if (rows <= 0) {
            throw new BusinessException("删除失败，学生不存在");
        }
    }

    @Transactional
    @Override
    public void deleteBatch(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的学生");
        }

        int rows = studentMapper.deleteBatch(ids);

        if (rows <= 0) {
            throw new BusinessException("删除失败，学生不存在");
        }
    }

    @Transactional
    @Override
    public void addStudent(StudentAddDTO studentAddDTO) {
        Student student = new Student();

        student.setId(studentAddDTO.getId());
        student.setName(studentAddDTO.getName());
        student.setAge(studentAddDTO.getAge());
        student.setScore(studentAddDTO.getScore());
        student.setCreateUserId(UserContext.getUserId());
        student.setCreateUsername(UserContext.getUsername());

        int rows = studentMapper.addStudent(student);

        if (rows <= 0) {
            throw new BusinessException("添加学生失败");
        }
    }

    @Transactional
    @Override
    public void updateStudent(String id, StudentUpdateDTO studentUpdateDTO) {
        Student student = new Student();

        student.setId(id);
        student.setName(studentUpdateDTO.getName());
        student.setAge(studentUpdateDTO.getAge());
        student.setScore(studentUpdateDTO.getScore());

        int rows = studentMapper.updateStudent(student);

        if (rows <= 0) {
            throw new BusinessException("修改失败，学生不存在");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<StudentVO> pageQuery(StudentQueryDTO queryDTO) {
        if (queryDTO.getMinScore() != null
                && queryDTO.getMaxScore() != null
                && queryDTO.getMinScore() > queryDTO.getMaxScore()) {
            throw new BusinessException("最低成绩不能大于最高成绩");
        }

        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();

        List<Student> students = studentMapper.findByConditionPage(
                queryDTO.getName(),
                queryDTO.getMinScore(),
                queryDTO.getMaxScore(),
                offset,
                queryDTO.getPageSize()
        );

        Long total = studentMapper.countByCondition(
                queryDTO.getName(),
                queryDTO.getMinScore(),
                queryDTO.getMaxScore()
        );

        List<StudentVO> voList = toVOList(students);

        return new PageResult<>(total, voList);
    }
}
