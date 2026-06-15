import java.util.ArrayList;
import java.util.Scanner;
public interface StudentService {
    void addStudent(ArrayList<Student> students, Scanner sc);

    void deleteStudent(ArrayList<Student> students, Scanner sc);

    void updateStudent(ArrayList<Student> students, Scanner sc);

    void searchStudent(ArrayList<Student> students, Scanner sc);

    void showAllStudent(ArrayList<Student> students);
}
