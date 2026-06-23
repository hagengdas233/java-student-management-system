package com.ljm.student;

import com.ljm.student.entity.Student;
import com.ljm.student.service.StudentService;
import com.ljm.student.service.StudentServiceImpl;

import org.apache.ibatis.session.SqlSessionFactory;



import java.util.List;
import java.util.Scanner;
import com.ljm.student.util.SqlSessionFactoryUtil;

public class Main {

    public static void main(String[] args) throws Exception {
        SqlSessionFactory sqlSessionFactory =
                SqlSessionFactoryUtil.getSqlSessionFactory();

        StudentService studentService =
                new StudentServiceImpl(sqlSessionFactory);

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("====== MyBatis学生管理系统 ======");
            System.out.println("1. 添加学生");
            System.out.println("2. 删除学生");
            System.out.println("3. 修改学生");
            System.out.println("4. 查询学生");
            System.out.println("5. 展示所有学生");
            System.out.println("6. 退出");
            System.out.println("7. 分页查询学生");
            System.out.println("8. 按姓名模糊查询学生");
            System.out.println("9. 按成绩降序查询学生");
            System.out.println("10. 统计学生总人数");
            System.out.println("11. 统计平均分");
            System.out.println("12. 统计最高分");
            System.out.println("13. 统计最低分");
            System.out.println("14. 统计及格人数和及格率");
            System.out.println("15. 条件查询学生");
            System.out.println("16. 条件修改学生");
            System.out.println("17. 批量删除学生");
            System.out.println("18. 批量添加学生");
            System.out.println("请输入你的选择：");

            int choice;

            try {
                choice = Integer.parseInt(sc.next());
            } catch (NumberFormatException e) {
                System.out.println("输入必须是数字");
                pause(sc);
                continue;
            }

            switch (choice) {
                case 1:
                    addStudent(sc, studentService);
                    break;
                case 2:
                    deleteStudent(sc, studentService);
                    break;
                case 3:
                    updateStudent(sc, studentService);
                    break;
                case 4:
                    findById(sc, studentService);
                    break;
                case 5:
                    showAll(studentService);
                    break;
                case 6:
                    System.out.println("系统退出");
                    return;
                case 7:
                    findByPage(sc, studentService);
                    break;
                case 8:
                    findByNameLike(sc, studentService);
                    break;
                case 9:
                    showOrderByScore(studentService);
                    break;
                case 10:
                    countStudent(studentService);
                    break;
                case 11:
                    avgScore(studentService);
                    break;
                case 12:
                    maxScore(studentService);
                    break;
                case 13:
                    minScore(studentService);
                    break;
                case 14:
                    passRate(studentService);
                    break;
                case 15:
                    findByCondition(sc, studentService);
                    break;
                case 16:
                    updateStudentSelective(studentService,sc);
                    break;
                case 17:
                    deleteByIds(sc, studentService);
                    break;
                case 18:
                    addStudents(sc, studentService);
                    break;
                default:
                    System.out.println("输入有误，请重新输入");
            }

            pause(sc);
        }
    }

    public static void addStudent(Scanner sc, StudentService studentService) {
        System.out.println("请输入学号：");
        String id = sc.next();

        System.out.println("请输入姓名：");
        String name = sc.next();

        System.out.println("请输入年龄：");
        int age = inputAge(sc);
        if (age == -1) {
            System.out.println("年龄有误，添加失败");
            return;
        }

        System.out.println("请输入成绩：");
        int score = inputScore(sc);
        if (score == -1) {
            System.out.println("成绩有误，添加失败");
            return;
        }

        Student student = new Student(id, name, age, score);

        boolean success = studentService.addStudent(student);

        if (success) {
            System.out.println("添加成功");
        } else {
            System.out.println("添加失败");
        }
    }

    public static void deleteStudent(Scanner sc, StudentService studentService) {
        System.out.println("请输入要删除的学生学号：");
        String id = sc.next();

        Student student = studentService.findById(id);

        if (student == null) {
            System.out.println("未找到该学生，删除失败");
            return;
        }

        System.out.println("即将删除该学生：");
        System.out.println(student);
        System.out.println("确认删除吗？输入 y 确认，其他任意内容取消：");

        String confirm = sc.next();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("已取消删除");
            return;
        }

        boolean success = studentService.deleteStudent(id);

        if (success) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }
    }

    public static void updateStudent(Scanner sc, StudentService studentService) {
        System.out.println("请输入要修改的学生学号：");
        String id = sc.next();

        Student oldStudent = studentService.findById(id);

        if (oldStudent == null) {
            System.out.println("未找到该学生，修改失败");
            return;
        }

        System.out.println("当前学生信息：");
        System.out.println(oldStudent);

        System.out.println("请输入新的姓名：");
        String newName = sc.next();

        System.out.println("请输入新的年龄：");
        int newAge = inputAge(sc);
        if (newAge == -1) {
            System.out.println("年龄有误，修改失败");
            return;
        }

        System.out.println("请输入新的成绩：");
        int newScore = inputScore(sc);
        if (newScore == -1) {
            System.out.println("成绩有误，修改失败");
            return;
        }

        Student newStudent = new Student(id, newName, newAge, newScore);

        System.out.println("修改前：");
        System.out.println(oldStudent);

        System.out.println("修改后：");
        System.out.println(newStudent);

        System.out.println("确认修改吗？输入 y 确认，其他任意内容取消：");
        String confirm = sc.next();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("已取消修改");
            return;
        }

        boolean success = studentService.updateStudent(newStudent);

        if (success) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败");
        }
    }

    public static void findById(Scanner sc, StudentService studentService) {
        System.out.println("请输入要查询的学生学号：");
        String id = sc.next();

        Student student = studentService.findById(id);

        if (student == null) {
            System.out.println("未找到该学生");
        } else {
            System.out.println(student);
        }
    }

    public static void showAll(StudentService studentService) {
        List<Student> students = studentService.findAll();

        if (students.isEmpty()) {
            System.out.println("暂无学生数据");
        } else {
            students.forEach(System.out::println);
        }
    }

    public static void findByPage(Scanner sc, StudentService studentService) {
        System.out.println("请输入页码：");
        int page;

        try {
            page = Integer.parseInt(sc.next());
        } catch (NumberFormatException e) {
            System.out.println("页码必须是数字");
            return;
        }

        System.out.println("请输入每页数量：");
        int pageSize;

        try {
            pageSize = Integer.parseInt(sc.next());
        } catch (NumberFormatException e) {
            System.out.println("每页数量必须是数字");
            return;
        }

        List<Student> students = studentService.findByPage(page, pageSize);

        if (students.isEmpty()) {
            System.out.println("当前页没有数据");
        } else {
            students.forEach(System.out::println);
        }
    }

    public static void findByNameLike(Scanner sc, StudentService studentService) {
        System.out.println("请输入姓名关键字：");
        String keyword = sc.next();

        List<Student> students = studentService.findByNameLike(keyword);

        if (students.isEmpty()) {
            System.out.println("没有找到相关学生");
        } else {
            students.forEach(System.out::println);
        }
    }

    public static void showOrderByScore(StudentService studentService) {
        List<Student> students = studentService.findAllOrderByScoreDesc();

        if (students.isEmpty()) {
            System.out.println("暂无学生数据");
        } else {
            students.forEach(System.out::println);
        }
    }

    public static void countStudent(StudentService studentService) {
        int count = studentService.countStudent();

        System.out.println("学生总人数：" + count);
    }

    public static void avgScore(StudentService studentService) {
        double avg = studentService.avgScore();

        System.out.println("学生平均分：" + avg);
    }

    public static void maxScore(StudentService studentService) {
        int max = studentService.maxScore();

        System.out.println("最高分：" + max);
    }

    public static void minScore(StudentService studentService) {
        int min = studentService.minScore();

        System.out.println("最低分：" + min);
    }

    public static void passRate(StudentService studentService) {
        int total = studentService.countStudent();
        int passCount = studentService.countPassStudent();

        if (total == 0) {
            System.out.println("当前没有学生数据");
            return;
        }

        double passRate = passCount * 1.0 / total * 100;

        System.out.println("及格人数：" + passCount);
        System.out.println("及格率：" + passRate + "%");
    }

    public static void findByCondition(Scanner sc, StudentService studentService) {
        System.out.println("请输入姓名关键字，不想按姓名查请输入 no：");
        String name = sc.next();

        if (name.equalsIgnoreCase("no")) {
            name = null;
        }

        System.out.println("请输入最低分，不想限制请输入 no：");
        String minStr = sc.next();

        Integer minScore = null;

        if (!minStr.equalsIgnoreCase("no")) {
            try {
                minScore = Integer.parseInt(minStr);
            } catch (NumberFormatException e) {
                System.out.println("最低分必须是数字");
                return;
            }
        }

        System.out.println("请输入最高分，不想限制请输入 no：");
        String maxStr = sc.next();

        Integer maxScore = null;

        if (!maxStr.equalsIgnoreCase("no")) {
            try {
                maxScore = Integer.parseInt(maxStr);
            } catch (NumberFormatException e) {
                System.out.println("最高分必须是数字");
                return;
            }
        }

        List<Student> students = studentService.findByCondition(name, minScore, maxScore);

        if (students.isEmpty()) {
            System.out.println("没有找到符合条件的学生");
        } else {
            students.forEach(System.out::println);
        }
    }

    public static void updateStudentSelective(StudentService studentService, Scanner sc) {
        System.out.println("请输入要修改的学生学号：");
        String id = sc.next();

        Student oldStudent = studentService.findById(id);

        if (oldStudent == null) {
            System.out.println("未找到该学生，修改失败");
            return;
        }

        System.out.println("当前学生信息：");
        System.out.println(oldStudent);

        System.out.println("请输入新的姓名，若要保留原有姓名，请输入 no：");
        String newNameStr = sc.next();

        String newName = null;
        if (!newNameStr.equalsIgnoreCase("no")) {
            newName = newNameStr;
        }

        System.out.println("请输入新的年龄，若要保留原有年龄，请输入 no：");
        String newAgeStr = sc.next();

        Integer newAge = null;
        if (!newAgeStr.equalsIgnoreCase("no")) {
            try {
                newAge = Integer.parseInt(newAgeStr);
            } catch (NumberFormatException e) {
                System.out.println("年龄必须是数字");
                return;
            }

            if (newAge <= 0) {
                System.out.println("年龄必须大于0");
                return;
            }
        }

        System.out.println("请输入新的成绩，若要保留原有成绩，请输入 no：");
        String newScoreStr = sc.next();

        Integer newScore = null;
        if (!newScoreStr.equalsIgnoreCase("no")) {
            try {
                newScore = Integer.parseInt(newScoreStr);
            } catch (NumberFormatException e) {
                System.out.println("成绩必须是数字");
                return;
            }

            if (newScore < 0 || newScore > 100) {
                System.out.println("成绩必须在0到100之间");
                return;
            }
        }

        if (newName == null && newAge == null && newScore == null) {
            System.out.println("你没有修改任何信息，已取消修改");
            return;
        }

        Student newStudent = new Student(id,newName,newAge,newScore);

        System.out.println("修改前：");
        System.out.println(oldStudent);

        System.out.println("即将修改为：");
        System.out.println("姓名：" + (newName == null ? oldStudent.getName() : newName));
        System.out.println("年龄：" + (newAge == null ? oldStudent.getAge() : newAge));
        System.out.println("成绩：" + (newScore == null ? oldStudent.getScore() : newScore));

        System.out.println("确认修改吗？输入 y 确认，其他任意内容取消：");
        String confirm = sc.next();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("已取消修改");
            return;
        }

        boolean success = studentService.updateStudentSelective(newStudent);

        if (success) {
            System.out.println("修改成功");
        } else {
            System.out.println("修改失败");
        }
    }

    public static void deleteByIds(Scanner sc, StudentService studentService) {
        System.out.println("请输入要删除的学号，多个学号用英文逗号隔开，例如：16,17,20");
        String input = sc.next();

        String[] arr = input.split(",");
        List<String> ids = new java.util.ArrayList<>();

        for (String id : arr) {
            id = id.trim();

            if (!id.isEmpty()) {
                ids.add(id);
            }
        }

        if (ids.isEmpty()) {
            System.out.println("没有有效的学号");
            return;
        }

        System.out.println("确认删除这些学号吗？" + ids);
        System.out.println("输入 y 确认，其他任意内容取消：");

        String confirm = sc.next();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("已取消删除");
            return;
        }

        boolean success = studentService.deleteByIds(ids);

        if (success) {
            System.out.println("批量删除成功");
        } else {
            System.out.println("批量删除失败，可能这些学号不存在");
        }
    }

    public static void addStudents(Scanner sc, StudentService studentService) {
        System.out.println("请输入要添加的学生数量：");

        int count;

        try {
            count = Integer.parseInt(sc.next());
        } catch (NumberFormatException e) {
            System.out.println("数量必须是数字");
            return;
        }

        if (count <= 0) {
            System.out.println("数量必须大于0");
            return;
        }

        List<Student> students = new java.util.ArrayList<>();

        for (int i = 1; i <= count; i++) {
            System.out.println("请输入第 " + i + " 个学生的学号：");
            String id = sc.next();

            System.out.println("请输入第 " + i + " 个学生的姓名：");
            String name = sc.next();

            System.out.println("请输入第 " + i + " 个学生的年龄：");
            int age = inputAge(sc);
            if (age == -1) {
                System.out.println("年龄有误，批量添加失败");
                return;
            }

            System.out.println("请输入第 " + i + " 个学生的成绩：");
            int score = inputScore(sc);
            if (score == -1) {
                System.out.println("成绩有误，批量添加失败");
                return;
            }

            students.add(new Student(id, name, age, score));
        }

        System.out.println("即将添加以下学生：");
        students.forEach(System.out::println);

        System.out.println("确认添加吗？输入 y 确认，其他任意内容取消：");
        String confirm = sc.next();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("已取消批量添加");
            return;
        }

        boolean success = studentService.addStudents(students);

        if (success) {
            System.out.println("批量添加成功");
        } else {
            System.out.println("批量添加失败");
        }
    }

    public static int inputAge(Scanner sc) {
        int age;

        try {
            age = Integer.parseInt(sc.next());
        } catch (NumberFormatException e) {
            System.out.println("年龄必须是数字");
            return -1;
        }

        if (age <= 0) {
            System.out.println("年龄必须大于0");
            return -1;
        }

        return age;
    }

    public static int inputScore(Scanner sc) {
        int score;

        try {
            score = Integer.parseInt(sc.next());
        } catch (NumberFormatException e) {
            System.out.println("成绩必须是数字");
            return -1;
        }

        if (score < 0 || score > 100) {
            System.out.println("成绩必须在0到100之间");
            return -1;
        }

        return score;
    }

    public static void pause(Scanner sc) {
        System.out.println("输入任意内容并回车返回菜单...");
        sc.next();
    }
}