import java.util.HashMap;
import java.util.Map;

public class HashMapStudentTest {
    public static void main(String[] args){
        HashMap<String,Student>mp=new HashMap<>();
        Student s1=new Student("1","ljm",18,78);
        Student s2=new Student("2","lff",18,79);

        mp.put(s1.getId(),s1);
        mp.put(s2.getId(),s2);

        Student s=mp.get("1");
        s.sayHello();

        if(mp.containsKey("2")){
            System.out.println(mp.get("2"));
        }

        for(Map.Entry<String,Student> entry:mp.entrySet()){
            String id=entry.getKey();
            Student s3=entry.getValue();
            System.out.println("学号"+id);
            s.sayHello();
        }

    }
}
