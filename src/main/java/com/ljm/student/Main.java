package com.ljm.student;

import com.ljm.student.dao.StudentDao;
import com.ljm.student.entity.Student;
import com.ljm.student.service.StudentService;
import com.ljm.student.service.StudentServiceImpl;

import javax.swing.plaf.synth.SynthUI;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentService studentService = new StudentServiceImpl();

        while (true) {
            System.out.println("====== JDBC学生管理系统 ======");
            System.out.println("1. 添加学生");
            System.out.println("2. 删除学生");
            System.out.println("3. 修改学生");
            System.out.println("4. 查询学生");
            System.out.println("5. 展示所有学生");
            System.out.println("6. 退出");
            System.out.println("7.分页查询学生");
            System.out.println("8.按姓名模糊查找学生");
            System.out.println("9. 按成绩降序查询学生");
            System.out.println("10. 统计学生总人数");
            System.out.println("11. 统计平均分");
            System.out.println("12. 统计最高分");
            System.out.println("13. 统计最低分");
            System.out.println("14. 统计及格人数和及格率");
            System.out.println("请输入你的选择：");

            int choice;
            try {
                choice = Integer.parseInt(sc.next());
            }catch(NumberFormatException e){
                System.out.println("输入的必须是数字");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("请输入学号：");
                    String id = sc.next();

                    System.out.println("请输入姓名：");
                    String name = sc.next();

                    System.out.println("请输入年龄：");
                    int age = inputAge(sc);
                    if (age == -1) {
                        System.out.println("年龄有误");
                        pause(sc);
                        break;
                    }

                    System.out.println("请输入成绩：");
                    int score = inputScore(sc);
                    if (score == -1) {
                        System.out.println("成绩有误");
                        pause(sc);
                        break;
                    }

                    Student student = new Student(id, name, age, score);
                    boolean success=studentService.addStudent(student);
                    if(success){
                        System.out.println("添加成功");
                    }
                    else System.out.println("添加失败");
                    pause(sc);
                    break;

                case 2:
                    System.out.println("请输入要删除的学生学号：");
                    String deleteId = sc.next();
                    Student deleteStudent= studentService.findById(deleteId);
                    if(deleteStudent==null){
                        System.out.println("未找到该学生，删除失败");
                        pause(sc);
                        break;
                    }
                    System.out.println("即将删除该学生");
                    System.out.println(deleteStudent);
                    System.out.println("输入'y'确认删除，输入其他退出");
                    String deleteConfirm=sc.next();
                    if(!deleteConfirm.equalsIgnoreCase("y")){
                        System.out.println("退出成功");
                        pause(sc);
                        break;
                    }
                    boolean seccess=studentService.deleteStudent(deleteId);
                    if(seccess){
                        System.out.println("删除成功");
                    }
                    else System.out.println("删除失败");
                    pause(sc);
                    break;

                case 3:
                    System.out.println("请输入要修改的学生学号：");
                    String updateId = sc.next();
                    Student oldStudent=studentService.findById(updateId);
                    if(oldStudent==null){
                        System.out.println("未找到该学生");
                        pause(sc);
                        break;
                    }

                    System.out.println("请输入新的姓名：");
                    String newName = sc.next();

                    System.out.println("请输入新的年龄：");
                    int newAge = inputAge(sc);
                    if (newAge == -1) {
                        System.out.println("年龄有误");
                        pause(sc);
                        break;
                    }

                    System.out.println("请输入新的成绩：");
                    int newScore = inputScore(sc);
                    if (newScore == -1) {
                        System.out.println("成绩有误");
                        pause(sc);
                        break;
                    }

                    Student newStudent = new Student(updateId, newName, newAge, newScore);
                    System.out.println("修改前");
                    System.out.println(oldStudent);
                    System.out.println("修改后");
                    System.out.println(newStudent);
                    System.out.println("输入'y'确认删除，输入其他退出");
                    String updateConfirm=sc.next();
                    if(!updateConfirm.equalsIgnoreCase("y")){
                        System.out.println("退出成功");
                        pause(sc);
                        break;
                    }
                    boolean stccess=studentService.updateStudent(newStudent);
                    if(stccess){
                        System.out.println("修改成功");
                    }
                    else System.out.println("修改失败");
                    pause(sc);
                    break;

                case 4:
                    System.out.println("请输入要查询的学生学号：");
                    String searchId = sc.next();

                    Student s = studentService.findById(searchId);

                    if (s == null) {
                        System.out.println("未找到该学生");
                    } else {
                        System.out.println(s);
                    }

                    pause(sc);
                    break;

                case 5:
                    List<Student> students = studentService.findAll();
                    students.forEach(System.out::println);

                    pause(sc);
                    break;

                case 6:
                    System.out.println("系统退出");
                    pause(sc);
                    return;
                case 7:
                    System.out.println("请输入页码");
                    int page;
                    try {
                        page = Integer.parseInt(sc.next());
                    }catch (NumberFormatException e){
                        System.out.println("页码必须是数字");
                        pause(sc);
                        break;
                    }
                    System.out.println("请输入每页数量");
                    int pagesize;
                    try{
                        pagesize=Integer.parseInt(sc.next());
                    }catch (NumberFormatException e){
                        System.out.println("每页数量必须是数字");
                        pause(sc);
                        break;
                    }
                    List<Student>pageStudent=studentService.findByPage(page,pagesize);
                    if(pageStudent.isEmpty()){
                        System.out.println("当页没有数据");
                        pause(sc);
                        break;
                    }
                    else {
                        pageStudent.forEach(System.out::println);
                        pause(sc);
                        break;
                    }
                case 8:
                    System.out.println("请输入关键字");
                    String keyWord=sc.next();
                    List<Student> result = studentService.findByNameLike(keyWord);
                    if (result.isEmpty()) {
                        System.out.println("没有找到相关学生");
                    } else {
                        result.forEach(System.out::println);
                    }
                    pause(sc);
                    break;
                case 9:
                    List<Student> sortedStudents = studentService.findAllOrderByScoreDesc();

                    if (sortedStudents.isEmpty()) {
                        System.out.println("暂无学生数据");
                    } else {
                        sortedStudents.forEach(System.out::println);
                    }

                    pause(sc);
                    break;
                case 10:
                    int count = studentService.countStudent();

                    if (count == -1) {
                        System.out.println("统计学生人数失败");
                    } else {
                        System.out.println("学生总人数：" + count);
                    }

                    pause(sc);
                    break;
                case 11:
                    double avg = studentService.avgScore();

                    if (avg == -1) {
                        System.out.println("统计平均分失败");
                    } else {
                        System.out.println("学生平均分：" + avg);
                    }

                    pause(sc);
                    break;
                case 12:
                    int max = studentService.maxScore();

                    if (max == -1) {
                        System.out.println("统计最高分失败");
                    } else {
                        System.out.println("最高分：" + max);
                    }

                    pause(sc);
                    break;
                case 13:
                    int min = studentService.minScore();

                    if (min == -1) {
                        System.out.println("统计最低分失败");
                    } else {
                        System.out.println("最低分：" + min);
                    }

                    pause(sc);
                    break;
                case 14:
                    int total = studentService.countStudent();
                    int passCount = studentService.countPassStudent();

                    if (total == -1 || passCount == -1) {
                        System.out.println("统计失败");
                    } else if (total == 0) {
                        System.out.println("当前没有学生数据");
                    } else {
                        double passRate = passCount * 1.0 / total * 100;
                        System.out.println("及格人数：" + passCount);
                        System.out.println("及格率：" + passRate + "%");
                    }
                    pause(sc);
                    break;
                default:
                    System.out.println("输入有误，请重新输入");
            }
        }
    }
    public static void pause(Scanner sc) {
        System.out.println("输入任意内容并回车返回菜单...");
        sc.next();
    }
    public static int inputAge(Scanner sc){
        int age;
        try{
            age=Integer.parseInt(sc.next());
        }catch (NumberFormatException e){
            System.out.println("年龄必须是数字");
            return -1;
        }
        if(age<=0){
            System.out.println("年龄必须是正整数");
            return -1;
        }
        return age;
    }
    public static int inputScore(Scanner sc){
        int score;
        try{
            score=Integer.parseInt(sc.next());
        }catch (NumberFormatException e){
            System.out.println("成绩必须是数字");
            return -1;
        }
        if(score<0||score>100){
            System.out.println("成绩必须是0~100之间的数");
            return -1;
        }
        return score;
    }

}