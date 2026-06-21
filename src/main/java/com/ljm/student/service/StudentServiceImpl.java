package com.ljm.student.service;

import com.ljm.student.dao.StudentDao;
import com.ljm.student.dao.StudentDaoImpl;
import com.ljm.student.entity.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentServiceImpl implements StudentService {

    private StudentDao studentDao = new StudentDaoImpl();

    @Override
    public boolean addStudent(Student student) {
        Student old = studentDao.findById(student.getId());

        if (old != null) {
            System.out.println("该学号已存在，添加失败");
            return false;
        }

        return studentDao.addStudent(student);
    }

    @Override
    public boolean deleteStudent(String id) {
        Student old = studentDao.findById(id);

        if (old == null) {
            System.out.println("未找到该学生，删除失败");
            return false;
        }

        return studentDao.deleteStudent(id);
    }

    @Override
    public boolean updateStudent(Student student) {
        Student old = studentDao.findById(student.getId());

        if (old == null) {
            System.out.println("未找到该学生，修改失败");
            return false;
        }

        return studentDao.updateStudent(student);
    }

    @Override
    public Student findById(String id) {
        return studentDao.findById(id);
    }

    @Override
    public List<Student> findAll() {
        return studentDao.findAll();
    }

    @Override
    public List<Student> findByPage(int page, int pageSize) {
        if(page<=0||pageSize<=0){
            System.out.println("页码和每页数量必须大于0");
            return new ArrayList<>();
        }
        return studentDao.findByPage(page,pageSize);
    }

    @Override
    public List<Student> findByNameLike(String name) {
        return studentDao.findByNameLike(name);
    }

    @Override
    public List<Student> findAllOrderByScoreDesc() {
        return studentDao.findAllOrderByScoreDesc();
    }

    @Override
    public int countStudent() {
        return studentDao.countStudent();
    }

    @Override
    public double avgScore() {
        return studentDao.avgScore();
    }

    @Override
    public int maxScore() {
        return studentDao.maxScore();
    }

    @Override
    public int minScore() {
        return studentDao.minScore();
    }

    @Override
    public int countPassStudent() {
        return studentDao.countPassStudent();
    }
}