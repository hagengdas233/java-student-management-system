import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
public interface StudentService {
    void addStudent(HashMap<String,Student>a, Scanner sc);

    void deleteStudent(HashMap<String,Student> a, Scanner sc);

    void updateStudent(HashMap<String,Student> a, Scanner sc);

    void searchStudent(HashMap<String,Student> a, Scanner sc);

    void showAllStudent(HashMap<String,Student> a);

    void showStudentByScoreDesc(HashMap<String,Student> a);

    void showStudentByIdDesc(HashMap<String,Student>a);

    void CompareStudents(HashMap<String,Student>a,Scanner sc);

    void maxScore(HashMap<String,Student> a);

    void avgScore(HashMap<String,Student> a);

    void sumScore(HashMap<String,Student> a);

    void topN(HashMap<String,Student>a,Scanner sc);

    void groupByPass(HashMap<String,Student>a,Scanner sc);

    void groupByLevel(HashMap<String, Student> a);

    void toMapDemo(HashMap<String, Student> a);
}
