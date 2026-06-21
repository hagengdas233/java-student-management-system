import com.ljm.student.dao.StudentDao;
import com.ljm.student.dao.StudentDaoImpl;
import com.ljm.student.entity.Student;

import java.util.List;

public class JDBCTest {
    public static void main(String[] args) {
        StudentDao studentDao = new StudentDaoImpl();

//        com.ljm.student.entity.Student s1=new com.ljm.student.entity.Student("4","ljm",20,98);
//
//        studentDao.addStudent(s1);

//        studentDao.deleteStudent("4");

        Student s1=new Student("1","ljm",18,89);

        studentDao.updateStudent(s1);

        List<Student> students = studentDao.findAll();

        students.forEach(System.out::println);

        Student s=studentDao.findById("5");
        if(s==null){
            System.out.println("未找到该学生");
        }
        else System.out.println(s);
    }
}