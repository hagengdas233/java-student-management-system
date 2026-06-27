package com.ljm.studentspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ljm.studentspringboot.dto.StudentAddDTO;
import com.ljm.studentspringboot.dto.StudentUpdateDTO;
import com.ljm.studentspringboot.entity.PageResult;
import com.ljm.studentspringboot.entity.Result;
import com.ljm.studentspringboot.entity.Student;
import com.ljm.studentspringboot.mapper.StudentPlusMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/mp/students")
public class StudentPlusController {

    private final StudentPlusMapper studentPlusMapper;

    public StudentPlusController(StudentPlusMapper studentPlusMapper) {
        this.studentPlusMapper = studentPlusMapper;
    }

    @GetMapping("/{id}")
    public Result<Student> selectById(@PathVariable String id) {
        Student student = studentPlusMapper.selectById(id);

        if (student == null) {
            return Result.error("没有该学生");
        }

        return Result.success(student);
    }

    @PostMapping
    public Result<Void> insert(@RequestBody @Valid StudentAddDTO studentAddDTO) {
        Student student = new Student();
        student.setId(studentAddDTO.getId());
        student.setName(studentAddDTO.getName());
        student.setAge(studentAddDTO.getAge());
        student.setScore(studentAddDTO.getScore());

        studentPlusMapper.insert(student);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateById(@PathVariable String id,
                                   @RequestBody @Valid StudentUpdateDTO studentUpdateDTO) {
        Student student = new Student();
        student.setName(studentUpdateDTO.getName());
        student.setAge(studentUpdateDTO.getAge());
        student.setScore(studentUpdateDTO.getScore());

        int rows = studentPlusMapper.update(
                student,
                new LambdaUpdateWrapper<Student>()
                        .eq(Student::getId, id)
        );

        if (rows <= 0) {
            return Result.error("修改失败，学生不存在");
        }

        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable String id) {
        int rows = studentPlusMapper.deleteById(id);

        if (rows <= 0) {
            return Result.error("没有该学生");
        }

        return Result.success();
    }

    @GetMapping
    public Result<List<Student>> list(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) Integer minScore,
                                      @RequestParam(required = false) Integer maxScore) {
        LambdaQueryWrapper<Student> queryWrapper = buildQueryWrapper(name, minScore, maxScore);
        return Result.success(studentPlusMapper.selectList(queryWrapper));
    }

    @GetMapping("/page")
    public Result<PageResult<Student>> page(@RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
                                            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) Integer minScore,
                                            @RequestParam(required = false) Integer maxScore) {
        Page<Student> page = Page.of(pageNum, pageSize);
        Page<Student> resultPage = studentPlusMapper.selectPage(page, buildQueryWrapper(name, minScore, maxScore));
        return Result.success(new PageResult<>(resultPage.getTotal(), resultPage.getRecords()));
    }

    private LambdaQueryWrapper<Student> buildQueryWrapper(String name, Integer minScore, Integer maxScore) {
        return new LambdaQueryWrapper<Student>()
                .like(name != null && !name.isBlank(), Student::getName, name)
                .ge(minScore != null, Student::getScore, minScore)
                .le(maxScore != null, Student::getScore, maxScore)
                .orderByDesc(Student::getScore);
    }
}
