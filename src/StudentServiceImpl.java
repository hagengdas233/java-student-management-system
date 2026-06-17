import javax.crypto.spec.PSource;
import javax.swing.text.Style;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class StudentServiceImpl implements StudentService{
    @Override
    public void addStudent(HashMap<String,Student> a, Scanner sc) {
        System.out.println("请输入学号：");
        String id=sc.next();
        if(a.containsKey(id)){
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
        a.put(id,new Student(id,name,age,score));
        System.out.println("输入成功");
    }

    @Override
    public void deleteStudent(HashMap<String,Student> a, Scanner sc) {
        System.out.println("请输入要删除的学生学号：");
        String deleteId=sc.next();
        if(a.containsKey(deleteId)){
            a.remove(deleteId);
            System.out.println("删除成功");
        }
        else System.out.println("未找到该学生，删除失败");
    }

    @Override
    public void updateStudent(HashMap<String,Student> a, Scanner sc) {
        System.out.println("请输入要修改的学生学号：");
        String updateId=sc.next();
        if(!a.containsKey(updateId)) {
            System.out.println("未找到该学生，修改失败");
            return;
        }
        Student s=a.get(updateId);
        System.out.println("请输入新的学号：");
        String newId=sc.next();

        if(!newId.equals(updateId)&&a.containsKey(newId)){
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
        a.remove(updateId);
        s.setId(newId);
        s.setAge(newAge);
        s.setName(newName);
        s.setScore(newScore);
        a.put(newId,s);
        System.out.println("修改成功，修改后的学生信息为：");
        s.sayHello();
    }

    @Override
    public void searchStudent(HashMap<String,Student> a, Scanner sc) {
        System.out.println("请输入要查找的学生学号：");
        String searchId=sc.next();
        stream(a)
                .filter(s->s.getId().equals(searchId))
                .forEach(Student::sayHello);
    }

    @Override
    public void showAllStudent(HashMap<String,Student> a) {
        stream(a)
                .forEach(Student::sayHello);
    }

    @Override
    public void showStudentByScoreDesc(HashMap<String, Student> a) {
        stream(a)
                .sorted(
                        Comparator.comparing(Student::getScore).reversed()
                                .thenComparing(Comparator.comparing(Student::getAge))
                )
                .forEach(s-> System.out.println(
                        s.getName()+"成绩是"+s.getScore()
                ));
    }

    @Override
    public void showStudentByIdDesc(HashMap<String, Student> a) {
        stream(a)
                .sorted(Comparator.comparing( (Student s)->Integer.parseInt(s.getId())
                ).reversed())
                .forEach(s-> System.out.println(
                        s.getName()+"学号是"+s.getId()
                ));
    }

    @Override
    public void CompareStudents(HashMap<String,Student>a, Scanner sc) {
        System.out.println("请输入第一个学生的id");
        String s1=sc.next();
        System.out.println("请输入第二个学生的id");
        String s2=sc.next();
        Student x=a.get(s1);
        Student y=a.get(s2);
        if(x==null||y==null){
            System.out.println("至少有一个学生不存在的，无法查询");
            return;
        }
        if(x.equals(y)){
            System.out.println("两学生相同");
        }
        else System.out.println("两学生不同");
        System.out.println("第一个学生信息");
        x.sayHello();
        System.out.println("第二个学生信息");
        y.sayHello();

    }

    @Override
    public void maxScore(HashMap<String, Student> a) {
        Optional<Student>max=stream(a)
                .max(Comparator.comparing(Student::getScore));
        max.ifPresent(s-> System.out.println(s.getName()+"是第一名，分数是"+s.getScore()));
    }

    @Override
    public void topN(HashMap<String, Student> a,Scanner sc) {
        System.out.println("请输入你要查找前几名:");
        int n=sc.nextInt();
        if(n<=a.size()){
        stream(a)
                .sorted(Comparator.comparing(Student::getScore).reversed())
                .limit(n)
                .forEach(Student::sayHello);
    }
        else System.out.println("输入人数有误");
    }

    @Override
    public void avgScore(HashMap<String, Student> a) {
        double avg=stream(a)
                .mapToInt(Student::getScore)
                .average()
                .orElse(0);
        System.out.println("平均分："+avg);
    }

    @Override
    public void sumScore(HashMap<String, Student> a) {
        int allscore= stream(a)
                .mapToInt(Student::getScore)
                .sum();
        System.out.println("总成绩是："+allscore);
    }

    @Override
    public void groupByPass(HashMap<String, Student> a, Scanner sc) {
        Map<Boolean, List<Student>>mp=
                stream(a)
                        .collect(Collectors.partitioningBy(
                                s->s.getScore()>=60
                        ));
        long count=
                stream(a)
                        .filter(s->s.getScore()>=60)
                        .count();
        double passRate=
                stream(a)
                        .filter(s->s.getScore()>=60)
                        .count()*1.0/a.size();
        System.out.println("及格人数为："+count);
        System.out.println("及格率为："+passRate);
        System.out.println("及格学生：");
        mp.get(true).forEach(Student::sayHello);
        System.out.println("不及格学生：");
        mp.get(false).forEach(Student::sayHello);
    }

    @Override
    public void groupByLevel(HashMap<String, Student> a) {
        Map<String,List<Student>>mp=
                stream(a)
                        .collect(Collectors.groupingBy(s->{
                            if(s.getScore()>=90)return "优秀";
                            else if(s.getScore()>=80)return "良好";
                            else return "一般";
                        }));
        mp.forEach((k,v)->{
            System.out.println("======"+k+"======");
            v.forEach(Student::sayHello);
        });
    }

    @Override
    public void toMapDemo(HashMap<String, Student> a) {
        Map<String,Student>mp=
                stream(a)
                        .collect(Collectors.toMap(
                                Student::getId,
                                s->s
                        ));
        System.out.println(mp);
    }

    private Stream<Student>stream(HashMap<String,Student>a){
        return a.values().stream();
}    //Stream<Student>表示返回一个Student类型的流（stream）

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



    public static void checkAge(int age){
        if(age<=0)throw new AgeException("年龄必须大于0");
    }
    public static void checkScore(int score){
        if(score<0||score>100)throw new ScoreException("成绩必须在0到100之间");
    }

}
