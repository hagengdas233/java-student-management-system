package com.ljm.studentspringboot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljm.studentspringboot.dto.StudentAddDTO;
import com.ljm.studentspringboot.dto.StudentQueryDTO;
import com.ljm.studentspringboot.dto.StudentUpdateDTO;
import com.ljm.studentspringboot.entity.PageResult;
import com.ljm.studentspringboot.entity.Student;
import com.ljm.studentspringboot.exception.BusinessException;
import com.ljm.studentspringboot.mapper.StudentMapper;
import com.ljm.studentspringboot.service.StudentService;
import com.ljm.studentspringboot.util.UserContext;
import com.ljm.studentspringboot.vo.StudentVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private static final String STUDENT_DETAIL_CACHE_PREFIX = "student:detail:";
    private static final long STUDENT_DETAIL_CACHE_TTL_MINUTES = 10;

    private final StudentMapper studentMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StudentServiceImpl(StudentMapper studentMapper,
                              StringRedisTemplate stringRedisTemplate) {
        this.studentMapper = studentMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

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

    private Student checkStudentOwner(String id) {
        Student student = studentMapper.findById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        Long currentUserId = UserContext.getUserId();
        if (student.getCreateUserId() == null || !student.getCreateUserId().equals(currentUserId)) {
            throw new BusinessException("无权限操作该学生");
        }

        return student;
    }

    private String buildStudentDetailCacheKey(String id) {
        return STUDENT_DETAIL_CACHE_PREFIX + id;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentVO> findAll() {
        List<Student> students = studentMapper.findAll();
        return toVOList(students);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentVO> findMyStudents() {
        List<Student> students = studentMapper.findByCreateUserId(UserContext.getUserId());
        return toVOList(students);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentVO findById(String id) {
        String cacheKey = buildStudentDetailCacheKey(id);
        String cacheJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheJson != null) {
            System.out.println("Redis 缓存命中: " + cacheKey);
            try {
                return objectMapper.readValue(cacheJson, StudentVO.class);
            } catch (JsonProcessingException e) {
                stringRedisTemplate.delete(cacheKey);
            }
        }

        System.out.println("Redis 缓存未命中，查询数据库: " + cacheKey);
        Student student = studentMapper.findById(id);

        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        StudentVO studentVO = toVO(student);
        try {
            String studentJson = objectMapper.writeValueAsString(studentVO);
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    studentJson,
                    Duration.ofMinutes(STUDENT_DETAIL_CACHE_TTL_MINUTES)
            );
        } catch (JsonProcessingException e) {
            throw new BusinessException("学生缓存写入失败");
        }

        return studentVO;
    }

    @Transactional
    @Override
    public void deleteStudent(String id) {
        checkStudentOwner(id);

        int rows = studentMapper.deleteStudent(id);

        if (rows <= 0) {
            throw new BusinessException("学生不存在");
        }

        stringRedisTemplate.delete(buildStudentDetailCacheKey(id));
    }

    @Transactional
    @Override
    public void deleteBatch(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择要删除的学生");
        }

        List<Student> students = studentMapper.findByIds(ids);
        if (students.size() != ids.size()) {
            throw new BusinessException("部分学生不存在");
        }

        Long currentUserId = UserContext.getUserId();
        boolean hasNoPermissionStudent = students.stream()
                .anyMatch(student -> student.getCreateUserId() == null
                        || !student.getCreateUserId().equals(currentUserId));
        if (hasNoPermissionStudent) {
            throw new BusinessException("无权限操作部分学生");
        }

        int rows = studentMapper.deleteBatch(ids);

        if (rows <= 0) {
            throw new BusinessException("批量删除失败");
        }

        ids.forEach(studentId -> stringRedisTemplate.delete(buildStudentDetailCacheKey(studentId)));
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
        checkStudentOwner(id);

        Student student = new Student();

        student.setId(id);
        student.setName(studentUpdateDTO.getName());
        student.setAge(studentUpdateDTO.getAge());
        student.setScore(studentUpdateDTO.getScore());

        int rows = studentMapper.updateStudent(student);

        if (rows <= 0) {
            throw new BusinessException("学生不存在");
        }

        stringRedisTemplate.delete(buildStudentDetailCacheKey(id));
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
