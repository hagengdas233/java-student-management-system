//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
public class Main{
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        ArrayList<Student> a = loadStudents();
        StudentService service =new StudentServiceImpl();
        while(true){
            System.out.println("====== 学生管理系统 ======");
            System.out.println("1.添加学生");
            System.out.println("2.删除学生");
            System.out.println("3.修改学生");
            System.out.println("4.查询学生");
            System.out.println("5.展示所有学生");
            System.out.println("6.退出系统");
            System.out.println("请输入你的选择");
            int choice;
            try{
                choice=inputChoice(sc);
            }catch (NumberFormatException e){
                System.out.println("输入的必须是数字");
                pause(sc);
                continue;
            }
            switch (choice){
                case 1:
                    service.addStudent(a,sc);
                    pause(sc);
                    break;
                case 2:
                    service.deleteStudent(a,sc);
                    pause(sc);
                    break;
                case 3:
                    service.updateStudent(a,sc);
                    pause(sc);
                    break;
                case 4:
                    service.searchStudent(a,sc);
                    pause(sc);
                    break;
                case 5:
                    service.showAllStudent(a);
                    pause(sc);
                    break;
                case 6:
                    saveStudent(a);
                    System.out.println("数据已保存，系统退出");
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
                    pause(sc);
            }
        }
    }

    public static void saveStudent(ArrayList<Student> a) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("students.txt"))) {
            for (int i = 0; i < a.size(); i++) {
                Student s = a.get(i);
                bw.write(s.getId() + "," + s.getName() + "," + s.getAge() + "," + s.getScore());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("保存数据失败");
        }
    }

    public static ArrayList<Student> loadStudents() {
        ArrayList<Student> a = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("students.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                String id = parts[0];
                String name = parts[1];
                int age = Integer.parseInt(parts[2]);
                int score = Integer.parseInt(parts[3]);

                a.add(new Student(id, name, age, score));
            }
        } catch (IOException e) {
            System.out.println("没有找到历史数据，使用默认数据");
            a.add(new Student("1", "ljm", 18, 65));
            a.add(new Student("2", "王五", 20, 85));
            a.add(new Student("3", "张三", 25, 95));
        }

        return a;
    }
    public static int inputChoice(Scanner sc){
        String choicestr=sc.next();
        int choice=Integer.parseInt(choicestr);
        return choice;
    }
    public static void pause(Scanner sc) {
        System.out.println("输入任意内容并回车返回菜单...");
        sc.next();
    }
}






