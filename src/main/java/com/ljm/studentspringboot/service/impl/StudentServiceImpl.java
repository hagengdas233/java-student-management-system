package com.ljm.studentspringboot.service.impl;

import com.ljm.studentspringboot.entity.Student;
import com.ljm.studentspringboot.mapper.StudentMapper;
import com.ljm.studentspringboot.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
import com.ljm.studentspringboot.exception.BusinessException;import com.ljm.studentspringboot.entity.PageResult;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentMapper studentMapper;

    private void checkStudent(Student student) {
        if (student.getId() == null || student.getId().isEmpty()) {
            throw new BusinessException("学号不能为空");
        }

        if (student.getName() == null || student.getName().isEmpty()) {
            throw new BusinessException("姓名不能为空");
        }

        if (student.getAge() == null || student.getAge() <= 0) {
            throw new BusinessException("年龄必须大于0");
        }

        if (student.getScore() == null || student.getScore() < 0 || student.getScore() > 100) {
            throw new BusinessException("成绩必须在0到100之间");
        }
    }

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public List<Student> findAll() {
        return studentMapper.findAll();
    }

    @Override
    public Student findById(String id) {
        return studentMapper.findById(id);
    }

    @Override
    public List<Student> findAllOrderByScoreDesc() {
        return studentMapper.findAllOrderByScoreDesc();
    }

    @Override
    public PageResult findByPageOrderByScoreDesc(Integer page, Integer pageSize) {
        if (page == null || page <= 0) {
            throw new BusinessException("页码必须大于0");
        }

        if (pageSize == null || pageSize <= 0) {
            throw new BusinessException("每页条数必须大于0");
        }

        int offset = (page - 1) * pageSize;

        Long total = studentMapper.count();
        List<Student> rows = studentMapper.findByPageOrderByScoreDesc(offset, pageSize);

        return new PageResult(total, rows);
    }

    @Override
    public PageResult findByConditionPage(String name,
                                          Integer minScore,
                                          Integer maxScore,
                                          Integer page,
                                          Integer pageSize) {
        if (page == null || page <= 0) {
            throw new BusinessException("页码必须大于0");
        }

        if (pageSize == null || pageSize <= 0) {
            throw new BusinessException("每页条数必须大于0");
        }

        if (minScore != null && (minScore < 0 || minScore > 100)) {
            throw new BusinessException("最低成绩必须在0到100之间");
        }

        if (maxScore != null && (maxScore < 0 || maxScore > 100)) {
            throw new BusinessException("最高成绩必须在0到100之间");
        }

        if (minScore != null && maxScore != null && minScore > maxScore) {
            throw new BusinessException("最低成绩不能大于最高成绩");
        }

        int offset = (page - 1) * pageSize;

        Long total = studentMapper.countByCondition(name, minScore, maxScore);
        List<Student> rows = studentMapper.findByConditionPage(name, minScore, maxScore, offset, pageSize);

        return new PageResult(total, rows);
    }

    @Override
    public List<Student> searchByName(String name) {
        return studentMapper.searchByName(name);
    }

    @Override
    public List<Student> filterByScore(Integer minScore, Integer maxScore) {
        return studentMapper.filterByScore(minScore, maxScore);
    }

    @Override
    public int addStudent(Student student) {
        checkStudent(student);
        return studentMapper.addStudent(student);
    }

    @Override
    public int updateStudent(Student student) {
        checkStudent(student);
        return studentMapper.updateStudent(student);
    }

    @Override
    public int deleteStudent(String id) {
        return studentMapper.deleteStudent(id);
    }

    @Override
    public PageResult findByPage(Integer page, Integer pageSize) {
        if (page == null || page <= 0) {
            throw new BusinessException("页码必须大于0");
        }

        if (pageSize == null || pageSize <= 0) {
            throw new BusinessException("每页条数必须大于0");
        }

        int offset = (page - 1) * pageSize;

        Long total = studentMapper.count();
        List<Student> rows = studentMapper.findByPage(offset, pageSize);

        return new PageResult(total, rows);
    }

    @Override
    public List<Student> findByCondition(String name, Integer minScore, Integer maxScore) {
        if (minScore != null && (minScore < 0 || minScore > 100)) {
            throw new BusinessException("最低成绩必须在0到100之间");
        }

        if (maxScore != null && (maxScore < 0 || maxScore > 100)) {
            throw new BusinessException("最高成绩必须在0到100之间");
        }

        if (minScore != null && maxScore != null && minScore > maxScore) {
            throw new BusinessException("最低成绩不能大于最高成绩");
        }

        return studentMapper.findByCondition(name, minScore, maxScore);
    }

    @Override
    public int updateStudentSelective(Student student) {
        if (student.getId() == null || student.getId().isEmpty()) {
            throw new BusinessException("学号不能为空");
        }

        if (student.getName() != null && student.getName().isEmpty()) {
            throw new BusinessException("姓名不能为空");
        }

        if (student.getAge() != null && student.getAge() <= 0) {
            throw new BusinessException("年龄必须大于0");
        }

        if (student.getScore() != null && (student.getScore() < 0 || student.getScore() > 100)) {
            throw new BusinessException("成绩必须在0到100之间");
        }

        return studentMapper.updateStudentSelective(student);
    }

    @Override
    public int deleteBatch(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的学生");
        }

        return studentMapper.deleteBatch(ids);
    }
}