import java.util.HashSet;

public class HashSetTest {
    public static void main(String[] args) {
        HashSet<String> s = new HashSet<>();
        s.add("ljm");
        s.add("ljm");
        s.add("java");
        s.remove("ljm");
        if(!s.contains("ljm")) System.out.println(123);
        System.out.println(s.size());
    }
}
