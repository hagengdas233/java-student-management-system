package com.ljm.student.service;

import com.ljm.student.entity.Student;
import com.ljm.student.mapper.StudentMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Collections;
import java.util.List;

public class StudentServiceImpl implements StudentService {

    private final SqlSessionFactory sqlSessionFactory;

    public StudentServiceImpl(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public List<Student> findAll() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.findAll();
        }
    }

    @Override
    public Student findById(String id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.findById(id);
        }
    }

    @Override
    public boolean addStudent(Student student) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            Student old = studentMapper.findById(student.getId());
            if (old != null) {
                System.out.println("该学号已存在，添加失败");
                return false;
            }

            int rows = studentMapper.addStudent(student);
            sqlSession.commit();

            return rows > 0;
        }
    }

    @Override
    public boolean deleteStudent(String id) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            Student old = studentMapper.findById(id);
            if (old == null) {
                System.out.println("未找到该学生，删除失败");
                return false;
            }

            int rows = studentMapper.deleteStudent(id);
            sqlSession.commit();

            return rows > 0;
        }
    }

    @Override
    public boolean updateStudent(Student student) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            Student old = studentMapper.findById(student.getId());
            if (old == null) {
                System.out.println("未找到该学生，修改失败");
                return false;
            }

            int rows = studentMapper.updateStudent(student);
            sqlSession.commit();

            return rows > 0;
        }
    }

    @Override
    public List<Student> findByPage(int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            System.out.println("页码和每页数量必须大于0");
            return Collections.emptyList();
        }

        int offset = (page - 1) * pageSize;

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.findByPage(offset, pageSize);
        }
    }

    @Override
    public List<Student> findByNameLike(String name) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.findByNameLike(name);
        }
    }

    @Override
    public List<Student> findAllOrderByScoreDesc() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.findAllOrderByScoreDesc();
        }
    }

    @Override
    public int countStudent() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.countStudent();
        }
    }

    @Override
    public double avgScore() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.avgScore();
        }
    }

    @Override
    public int maxScore() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.maxScore();
        }
    }

    @Override
    public int minScore() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.minScore();
        }
    }

    @Override
    public int countPassStudent() {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.countPassStudent();
        }
    }

    @Override
    public List<Student> findByCondition(String name, Integer minScore, Integer maxScore) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.findByCondition(name, minScore, maxScore);
        }
    }

    @Override
    public boolean updateStudentSelective(Student student) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            Student old = studentMapper.findById(student.getId());
            if (old == null) {
                System.out.println("未找到该学生，修改失败");
                return false;
            }

            int rows = studentMapper.updateStudentSelective(student);
            sqlSession.commit();

            return rows > 0;
        }
    }

    @Override
    public boolean deleteByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            System.out.println("没有输入要删除的学号");
            return false;
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            int rows = studentMapper.deleteByIds(ids);
            sqlSession.commit();

            return rows > 0;
        }
    }

    @Override
    public boolean addStudents(List<Student> students) {
        if (students == null || students.isEmpty()) {
            System.out.println("没有要添加的学生");
            return false;
        }

        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            int rows = studentMapper.addStudents(students);

            sqlSession.commit();

            return rows > 0;
        }
    }
}