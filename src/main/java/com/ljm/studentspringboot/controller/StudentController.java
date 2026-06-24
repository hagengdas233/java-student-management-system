package com.ljm.studentspringboot.controller;

import com.ljm.studentspringboot.entity.Student;
import com.ljm.studentspringboot.entity.Result;
import com.ljm.studentspringboot.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/student")
    public Student getStudent() {
        return new Student("1", "ljm", 18, 90);
    }

    @GetMapping("/students")
    public Result getStudents() {
        return Result.success(studentService.findAll());
    }

    @GetMapping("/students/{id}")
    public Result getStudentById(@PathVariable String id) {
        return Result.success(studentService.findById(id));
    }

    @GetMapping("/students/order/score")
    public Result findAllOrderByScoreDesc() {
        return Result.success(studentService.findAllOrderByScoreDesc());
    }

    @GetMapping("/students/page/order")
    public Result findByPageOrderByScoreDesc(@RequestParam Integer page,
                                             @RequestParam Integer pageSize) {
        return Result.success(studentService.findByPageOrderByScoreDesc(page, pageSize));
    }

    @GetMapping("/students/page/condition")
    public Result findByConditionPage(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) Integer minScore,
                                      @RequestParam(required = false) Integer maxScore,
                                      @RequestParam Integer page,
                                      @RequestParam Integer pageSize) {
        return Result.success(studentService.findByConditionPage(name, minScore, maxScore, page, pageSize));
    }

    @GetMapping("/students/search")
    public Result searchStudents(@RequestParam String name) {
        return Result.success(studentService.searchByName(name));
    }

    @GetMapping("/students/filter")
    public Result filterStudents(@RequestParam Integer minScore,
                                 @RequestParam Integer maxScore) {
        return Result.success(studentService.filterByScore(minScore, maxScore));
    }

    @PostMapping("/students")
    public Result addStudent(@RequestBody Student student) {
        int rows = studentService.addStudent(student);

        if (rows > 0) {
            return Result.success();
        } else {
            return Result.error("添加失败");
        }
    }

    @PutMapping("/students")
    public Result updateStudent(@RequestBody Student student) {
        int rows = studentService.updateStudent(student);

        if (rows > 0) {
            return Result.success();
        } else {
            return Result.error("修改失败，学生不存在");
        }
    }

    @DeleteMapping("/students/{id}")
    public Result deleteStudent(@PathVariable String id) {
        int rows = studentService.deleteStudent(id);

        if (rows > 0) {
            return Result.success();
        } else {
            return Result.error("删除失败，学生不存在");
        }
    }

    @GetMapping("/students/page")
    public Result findByPage(@RequestParam Integer page,
                             @RequestParam Integer pageSize) {
        return Result.success(studentService.findByPage(page, pageSize));
    }

    @GetMapping("/students/condition")
    public Result findByCondition(@RequestParam(required = false) String name,
                                  @RequestParam(required = false) Integer minScore,
                                  @RequestParam(required = false) Integer maxScore) {
        return Result.success(studentService.findByCondition(name, minScore, maxScore));
    }

    @PatchMapping("/students")
    public Result updateStudentSelective(@RequestBody Student student) {
        int rows = studentService.updateStudentSelective(student);

        if (rows > 0) {
            return Result.success();
        } else {
            return Result.error("修改失败，学生不存在");
        }
    }

    @DeleteMapping("/students/batch")
    public Result deleteBatch(@RequestParam List<String> ids) {
        int rows = studentService.deleteBatch(ids);

        if (rows > 0) {
            return Result.success();
        } else {
            return Result.error("删除失败，学生不存在");
        }
    }
}