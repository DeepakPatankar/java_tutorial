import java.util.*;

public class CountDown {
    public static void main(String[] args) throws InterruptedException {
        ;
        Queue<Integer> queue = new LinkedList<Integer>();

        for (int i = 10; i >= 0; i--)
            queue.add(i);

        queue.offer(0);
        System.out.println("Top" + queue.peek());
        queue.poll();

        while (!queue.isEmpty()) {
            System.out.println(queue.remove());
            Thread.sleep(1000);
        }
    }
}
