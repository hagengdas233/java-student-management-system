//TIP 要<b>运行</b>代码，请按 <shortcut actionId="Run"/> 或
// 点击装订区域中的 <icon src="AllIcons.Actions.Execute"/> 图标。
import java.util.Scanner;
import java.util.ArrayList;
public class Main{
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        ArrayList<Student>a=new ArrayList<>();
        a.add(new Student("1","ljm",18,65));
        a.add( new Student("2","王五",20,85));
        a.add( new Student("3","张三",25,95));

        while(true){
            System.out.println("====== 学生管理系统 ======");
            System.out.println("1.添加学生");
            System.out.println("2.删除学生");
            System.out.println("3.修改学生");
            System.out.println("4.查询学生");
            System.out.println("5.展示所有学生");
            System.out.println("6.退出系统");
            System.out.println("请输入你的选择");
            int choice=sc.nextInt();
            switch (choice){
                case 1:
                    addStudent(a,sc);
                    pause(sc);
                    break;
                case 2:
                    deleteStudent(a,sc);
                    pause(sc);
                    break;
                case 3:
                    updateStudent(a,sc);
                    pause(sc);
                    break;
                case 4:
                    searchStudent(a,sc);
                    pause(sc);
                    break;
                case 5:
                    showAllStudent(a);
                    pause(sc);
                    break;
                case 6:
                    System.out.println("已退出");
                    return;
                default:
                    System.out.println("输入有误，请重新输入");
                    pause(sc);
            }
        }
    }
    public static void addStudent(ArrayList<Student> a, Scanner sc ){
        System.out.println("请输入学号：");
        String id=sc.next();
            if(getindexId(a,id)!=-1){
                System.out.println("\n==========================");
                System.out.println("❌ 错误：该学号已存在！添加失败。");
                System.out.println("==========================\n");
                return;
        }
        System.out.println("请输入姓名：");
        String name=sc.next();
        System.out.println("请输入年龄：");
        int age=sc.nextInt();
        if(!checkAge(age)){
            System.out.println("年龄错误");
            return;
        }
        System.out.println("请输入成绩：");
        int score=sc.nextInt();
        if(!checkScore(score)){
            System.out.println("成绩错误");
            return;
        }
        a.add(new Student(id,name,age,score));
        System.out.println("输入成功");
    }
    public static void deleteStudent(ArrayList<Student> a, Scanner sc) {
        boolean deleteFound=false;
        System.out.println("请输入要删除的学生学号：");
        String deleteId=sc.next();
        int index = getindexId(a, deleteId);
            if(index!=-1){
                a.remove(index);
                System.out.println("删除成功");
                deleteFound=true;
            }

        if(!deleteFound) System.out.println("未找到该学生，删除失败");
    }
    public static void updateStudent(ArrayList<Student> a, Scanner sc) {
        System.out.println("请输入要修改的学生学号：");
        String updateId=sc.next();
                int index=getindexId(a,updateId);
                if(index == -1) {
                    System.out.println("未找到该学生，修改失败");
                    return;
                }
                System.out.println("请输入新的学号：");
                String newId=sc.next();
                int newIndex=getindexId(a,newId);
                    if(newIndex!=-1&&newIndex!=index){
                        System.out.println("该学号已存在，修改失败");
                        return;
                    }
                System.out.println("请输入新的姓名：");
                String newName=sc.next();
                System.out.println("请输入新的年龄：");
                int newAge=sc.nextInt();
                if(!checkAge(newAge)){
                    System.out.println("年龄错误");
                    return;
                }
                System.out.println("请输入新的成绩：");
                int newScore=sc.nextInt();
                if(!checkScore(newScore)){
                    System.out.println("成绩错误");
                    return;
                }
                a.get(index).setId(newId);
                a.get(index).setName(newName);
                a.get(index).setAge(newAge);
                a.get(index).setScore(newScore);
                System.out.println("修改成功，修改后的学生信息为：");
                a.get(index).sayHello();


    }

    public static void searchStudent(ArrayList<Student> a, Scanner sc) {

        System.out.println("请输入要查找的学生学号：");
        String searchId=sc.next();
        int index=getindexId(a,searchId);
            if(index==-1){
                System.out.println("未找到该学生");
                return;
            }

        a.get(index).sayHello();
    }

    public static void showAllStudent(ArrayList<Student> a) {
        for(int i=0;i<a.size();i++){
            a.get(i).sayHello();
        }
    }
    public static int getindexId(ArrayList<Student>a,String id){
        for(int i=0;i<a.size();i++){
            if(a.get(i).getId().equals(id)){
                return i;
            }
        }
        return -1;
    }
    public static boolean checkAge(int age){
        return age>0;
    }
    public static boolean checkScore(int score){
        return score>=0&&score<=100;
    }
    public static void pause(Scanner sc) {
        System.out.println("输入任意内容并回车返回菜单...");
        sc.next();
    }
}






