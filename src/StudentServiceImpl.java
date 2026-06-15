import java.util.ArrayList;
import java.util.Scanner;
public class StudentServiceImpl implements StudentService{
    @Override
    public void addStudent(ArrayList<Student> a, Scanner sc) {
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
        int age;
        try{
            age=inputAge(sc);
        }catch (NumberFormatException e){
            System.out.println("年龄必须是数字");
            return;
        }
        catch (AgeException e){
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("请输入成绩：");
        int score;
        try{
            score=inputScore(sc);
        }catch (NumberFormatException e){
            System.out.println("成绩必须是数字");
            return;
        }
        catch (ScoreException e){
            System.out.println(e.getMessage());
            return;
        }
        a.add(new Student(id,name,age,score));
        System.out.println("输入成功");
    }

    @Override
    public void deleteStudent(ArrayList<Student> a, Scanner sc) {
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

    @Override
    public void updateStudent(ArrayList<Student> a, Scanner sc) {
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
        int newAge;
        try{
            newAge=inputAge(sc);
        }catch (NumberFormatException e){
            System.out.println("年龄必须是数字");
            return;
        }
        catch (AgeException e){
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("请输入新的成绩：");
        int newScore;
        try{
            newScore=inputScore(sc);
        }catch (NumberFormatException e){
            System.out.println("成绩必须是数字");
            return;
        }
        catch (ScoreException e){
            System.out.println(e.getMessage());
            return;
        }
        a.get(index).setId(newId);
        a.get(index).setName(newName);
        a.get(index).setAge(newAge);
        a.get(index).setScore(newScore);
        System.out.println("修改成功，修改后的学生信息为：");
        a.get(index).sayHello();
    }

    @Override
    public void searchStudent(ArrayList<Student> a, Scanner sc) {
        System.out.println("请输入要查找的学生学号：");
        String searchId=sc.next();
        int index=getindexId(a,searchId);
        if(index==-1){
            System.out.println("未找到该学生");
            return;
        }

        a.get(index).sayHello();
    }

    @Override
    public void showAllStudent(ArrayList<Student> a) {
        for (int i = 0; i < a.size(); i++) {
            a.get(i).sayHello();
        }
    }


    public static int inputAge(Scanner sc){
        String agestr=sc.next();
        int age=Integer.parseInt(agestr);
        checkAge(age);
        return age;
    }

    public static int inputScore(Scanner sc){
        String scorestr=sc.next();
        int score=Integer.parseInt(scorestr);
        checkScore(score);
        return score;
    }


    public static int getindexId(ArrayList<Student>a,String id){
        for(int i=0;i<a.size();i++){
            if(a.get(i).getId().equals(id)){
                return i;
            }
        }
        return -1;
    }
    public static void checkAge(int age){
        if(age<=0)throw new AgeException("年龄必须大于0");
    }
    public static void checkScore(int score){
        if(score<0||score>100)throw new ScoreException("成绩必须在0到100之间");
    }

}
