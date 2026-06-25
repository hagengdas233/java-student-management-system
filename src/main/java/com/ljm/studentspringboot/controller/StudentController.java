package com.ljm.studentspringboot.controller;

import com.ljm.studentspringboot.entity.Student;
import com.ljm.studentspringboot.entity.Result;
import com.ljm.studentspringboot.entity.PageResult;
import com.ljm.studentspringboot.dto.StudentAddDTO;
import com.ljm.studentspringboot.dto.StudentUpdateDTO;
import com.ljm.studentspringboot.dto.StudentQueryDTO;
import com.ljm.studentspringboot.vo.StudentVO;
import com.ljm.studentspringboot.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "学生管理接口")
@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }


    @Operation(summary = "查询全部学生")
    @GetMapping("/students")
    public Result<List<StudentVO>> getStudents() {
        return Result.success(studentService.findAll());
    }

    @Operation(summary = "根据ID查询学生")
    @GetMapping("/students/{id}")
    public Result<StudentVO> getStudentById(
            @Parameter(description = "学生学号", example = "1001")
            @PathVariable String id) {
        return Result.success(studentService.findById(id));
    }


    @Operation(summary = "条件分页查询学生")
    @GetMapping("/students/page/query")
    public Result<PageResult<StudentVO>> pageQuery(@Valid StudentQueryDTO queryDTO) {
        PageResult<StudentVO> pageResult = studentService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }


    @Operation(summary = "添加学生")
    @PostMapping("/students")
    public Result<Void> addStudent(@RequestBody @Valid StudentAddDTO studentAddDTO) {
        studentService.addStudent(studentAddDTO);
        return Result.success();
    }

    @Operation(summary = "修改学生")
    @PutMapping("/students/{id}")
    public Result<Void> updateStudent(@PathVariable String id,
                                      @RequestBody @Valid StudentUpdateDTO studentUpdateDTO) {
        studentService.updateStudent(id, studentUpdateDTO);
        return Result.success();
    }

    @Operation(summary = "删除学生")
    @DeleteMapping("/students/{id}")
    public Result<Void> deleteStudent(
            @Parameter(description = "学生学号", example = "1001")
            @PathVariable String id) {
        studentService.deleteStudent(id);
        return Result.success();
    }

    @Operation(summary = "批量删除学生")
    @DeleteMapping("/students/batch")
    public Result<Void> deleteBatch(
            @Parameter(description = "学生学号列表", example = "1,2,3")
            @RequestParam List<String> ids) {
        studentService.deleteBatch(ids);
        return Result.success();
    }
}