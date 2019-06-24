import java.util.*;

public class FindDups {
    public static void main(String[] args) {
        Set<String> s = new HashSet<String>();
        for (String a : args)
            s.add(a);
        System.out.println(s.size() + " distinct words: " + s);
        s.remove(args[0]);
        Iterator<String> itr = s.iterator();
        while(itr.hasNext())
            System.out.println(itr.next());
    }
}