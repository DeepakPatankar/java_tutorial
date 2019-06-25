## Concurrency in Java
Welcome to my introduction to Concurrency in java. This tutorial guides you step by step through all the concepts and the implementation part. Backed by short and simple code samples you'll learn how to make threads, thread polls, wait and notify etc. The code samples are taken from the [Cave of Programming]()https://github.com/Beerkay/JavaMultiThreading 

## Table of Contents

*  Threads
*  Starting a thread
*  Volatile Keyword
*  Synchronisation
*  Intrinsic Lock
*  Executor Service
* Count Down Latch
* Reentrant Lock
* Deadlock
* Semaphores
* Callable and Future
* Atomic Operation

---

### Threads
---
Processing time for a single core is shared among processes and threads through an OS feature called time slicing. Threads are sometimes called lightweight processes. Both processes and threads provide an execution environment, but creating a new thread requires fewer resources than creating a new process.

Threads exist within a process — every process has at least one. Threads share the process's resources, including memory and open files. 

Each thread is associated with an instance of the class Thread. There are two basic strategies for using Thread objects to create a concurrent application.

* To directly control thread creation and management, simply instantiate Thread each time the application needs to initiate an asynchronous task.
* To abstract thread management from the rest of your application, pass the application's tasks to an executor.

### Starting a thread
---
* Implement runnable
```java
class RunnerRunnable implements Runnable {
	public void run() {
		for (int i = 0; i < 5; i++) {
            System.out.println("Hello: " + i + " Thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
public class ApplicationRunnable {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new RunnerRunnable());
        Thread thread2 = new Thread(new RunnerRunnable());
        thread1.start();
        thread2.start();
    }
}
```

* Extends Thread
```java
class Runner extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println("Hello: " + i + " Thread: " + Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
public class ApplicationExtends {
    public static void main(String[] args) {
        Runner runner1 = new Runner();
        runner1.start();

        Runner runner2 = new Runner();
        runner2.start();
    }
}
```

* Anonymous
```java
public class ApplicationAnonymous {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    System.out.println("Hello: " + i + " Thread: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                }
            }
        });
        thread1.start();
    }
}
```

### Volatile  Keyword
---
Suppose that two threads are working on SharedObj. If two threads run on different processors each thread may have its own local copy of sharedVariable. If one thread modifies its value the change might not reflect in the original one in the main memory instantly. This depends on the write policy of cache. Now the other thread is not aware of the modified value which leads to data inconsistency.

 Using volatile variables reduces the risk of memory consistency errors, because any write to a volatile variable establishes a happens-before relationship with subsequent reads of that same variable. This means that changes to a volatile variable are always visible to other threads. What's more, it also means that when a thread reads a volatile variable, it sees not just the latest change to the volatile, but also the side effects of the code that led up the change.

```java
import java.util.Scanner;

class Processor extends Thread {
    private volatile boolean running = true;
    public void run() {
        while (running) {
            System.out.println("Running");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void shutdown() {
        running = false;
    }
}

public class App {
    public static void main(String[] args) {
        Processor pro = new Processor();
        pro.start();
        // Wait for the enter key
        System.out.println("Enter something to stop the thread,\nVolatile variable running will be forced to true :");
        new Scanner(System.in).nextLine();
        pro.shutdown();
    }
}
```

### Synchronisation
---
* Thread Interference
```java
class Counter {
    private int c = 0;
    public void increment() {
        c++;
    }
    public void decrement() {
        c--;
    }
    public int value() {
        return c;
    }
}
```

* Memory Consistency Error
`int counter = 0;`
`counter++;`

If the two statements had been executed in the same thread, it would be safe to assume that the value printed out would be "1". But if the two statements are executed in separate threads, the value printed out might well be "0", because there's no guarantee that thread A's change to counter will be visible to thread B — unless the programmer has established a happens-before relationship between these two statements.
```java
import java.util.logging.Level;
import java.util.logging.Logger;
public class Worker {
    private int count = 0;
    public static void main(String[] args) {
        Worker worker = new Worker();
        worker.doWork();
    }
    /**
     * Run code again by removing the synchronized and join keywords By removing
     * synchronized keyword count variable will vary that is it is not atomic in
     * this case due to the reason that count is shared between the threads or
     * without join keyword count will output wrong.
     */
    public synchronized void increment(String threadName) throws InterruptedException {
        count++;
        //Thread.sleep(1000);
        System.out.println("Thread in Progress: " + threadName + " and count is: " + count);
    }
    public void doWork() {
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        increment(Thread.currentThread().getName());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        thread1.start();
        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        increment(Thread.currentThread().getName());
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        thread2.start();
        /**
         * Join Threads: Finish until thread finishes execution, then progress
         * the code Reminder: your method is also a thread so without joining
         * threads System.out.println("Count is: " + count); will work
         * immediately. Join does not terminate Thread 2, just progress of the
         * code stops until Threads terminate.
         */
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException ignored) {}
        System.out.println("Count is: " + count);
    }
}
```

Making these methods synchronized has two effects:

* First, it is not possible for two invocations of synchronized methods on the same object to interleave. When one thread is executing a synchronized method for an object, all other threads that invoke synchronized methods for the same object block (suspend execution) until the first thread is done with the object.
* Second, when a synchronized method exits, it automatically establishes a happens-before relationship with any subsequent invocation of a synchronized method for the same object. This guarantees that changes to the state of the object are visible to all threads.

synchronized statements
```java
 synchronized(this) 
        {
	}
```

### Intrinsic Lock
---
```java
public class Worker {

    private Random random = new Random();

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    public void stageOne() {

        synchronized (lock1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                //do your work here
                e.printStackTrace();
            }
            list1.add(random.nextInt(100));
        }
    }

    public void stageTwo() {
        synchronized (lock2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                //do your work here
                e.printStackTrace();
            }
            list2.add(random.nextInt(100));
        }

    }

    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }

    public void main() {
        System.out.println("Starting ...");
        long start = System.currentTimeMillis();
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                process();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                process();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("Time taken: " + (end - start));
        System.out.println("List1: " + list1.size() + "; List2: " + list2.size());
    }
}
```

### High Level Concurrency Object
* Lock Object
* Executor
* Concurrent Collections
* Atomic Variables

#### Executor Service
---
```java
class Worker implements Runnable {
    private Random random = new Random();
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    public List<Integer> list1 = new ArrayList<>();
    public List<Integer> list2 = new ArrayList<>();
    @Override
    public void run() {
        process();
    }
    public void process() {
        for (int i = 0; i < 1000; i++) {
            stageOne();
            stageTwo();
        }
    }
    public void stageOne() {
        synchronized (lock1) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                //do your work here
                e.printStackTrace();
            }
            list1.add(random.nextInt(100));
        }
    }

    public void stageTwo() {
        synchronized (lock2) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                //do your work here
                e.printStackTrace();
            }
            list2.add(random.nextInt(100));
        }
    }
}

public class WorkerThreadPool {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);//two threads, try setting by 1 to observe time
        System.out.println("Starting ...");
        long start = System.currentTimeMillis();
        Worker worker = new Worker();
        for (int i = 0; i < 2; i++) {//worker.run is called 2 (threads started) times by two threads
            executor.submit(worker);
        }
        executor.shutdown(); //prevents new tasks from being accepted by the ExecutorService
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
            // How long should I wait for termination If I do not know exactly when threads are done with the tasks ?
            // Source:http://stackoverflow.com/questions/1250643/how-to-wait-for-all-threads-to-finish-using-executorservice
            // For a perpetually running batch kind of thing u need to submit jobs and wait for them to
            // finish before jumping ahead.
            // In Such a case a latch or a barrier makes more sense than a shutdown (see CountDownLatch_6.App).
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start));
        System.out.println("List1: " + worker.list1.size() + "; List2: " + worker.list2.size());
    }
}
```

### CountDown Latch
---
```java
class Processor implements Runnable {
    private CountDownLatch latch;
    public Processor(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        System.out.println("Started.");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {}
        latch.countDown();
    }
}

public class App {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(3);
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executor.submit(new Processor(latch));
        }
        executor.shutdown();
        try {
            // Application’s main thread waits, till other service threads which are
            // as an example responsible for starting framework services have completed started all services.
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Completed.");
    }
}
```

Example : 
Producer Consumer Problem:
```java
@SuppressWarnings("InfiniteLoopStatement")
public class App {

    /**
     * Thread safe implementation of {@link java.util.Queue} data structure so
     * you do not need to worry about synchronization.
     * More specifically {@link java.util.concurrent.BlockingQueue}
     * implementations are thread-safe. All queuing methods are atomic in nature
     * and use internal locks or other forms of concurrency control. If
     * BlockingQueue is not used queue is shared data structure either
     * {@code synchronized} or {@code wait() notify()} (see Course 8) should be
     * used.
     * Java 1.5 introduced a new concurrency library {@link java.util.concurrent}
     * which was designed to provide a higher level abstraction over
     * the wait/notify mechanism.
     */
    private static BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    producer();
                } catch (InterruptedException ignored) {}
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    consumer();
                } catch (InterruptedException ignored) {}
            }
        });
        t1.start();
        t2.start();
//        t1.join();
//        t2.join();

        // Pause for 30 seconds and force quitting the app (because we're
        // looping infinitely)
        Thread.sleep(30000);
        System.exit(0);
    }

    private static void producer() throws InterruptedException {
        Random random = new Random();
        while (true) {//loop indefinitely
            queue.put(random.nextInt(100));//if queue is full (10) waits
        }
    }

    private static void consumer() throws InterruptedException {
        Random random = new Random();
        while (true) {
            Thread.sleep(100);
            if (random.nextInt(10) == 0) {
                Integer value = queue.take();//if queue is empty waits
                System.out.println("Taken value: " + value + "; Queue size is: " + queue.size());
            }
        }
    }
}
```
The correct producer consumer: 
```java
@SuppressWarnings("InfiniteLoopStatement")
public class Processor {

    private LinkedList<Integer> list = new LinkedList<>();
    private final int LIMIT = 10;
    private final Object lock = new Object();

    public void produce() throws InterruptedException {
        int value = 0;
        while (true) {
            synchronized (lock) {
                //whenever the thread is notified starts again from the loop
                while (list.size() == LIMIT) {
                    lock.wait();// wait() is also true
                }
                list.add(value);

                System.out.println("Producer added: " + value + " queue size is " + list.size());
                value++;
                lock.notify();
            }
        }
    }

    public void consume() throws InterruptedException {
        Random random = new Random();
        while (true) {
            synchronized (lock) {
                while (list.size() == 0) {
                    lock.wait();
                }

                int value = list.removeFirst();
                System.out.print("Removed value by consumer is: " + value);
                System.out.println(" Now list size is: " + list.size());
                lock.notify();
            }
            Thread.sleep(random.nextInt(1000)); //force producer fill the queue to LIMIT_SIZE
        }
    }
}
```


### Wait and Notify
---
```java
public class Processor {

    /*
     * public synchronized void getSomething(){ this.hello = "hello World"; }
     * public void getSomething(){ synchronized(this){ this.hello = "hello
     * World"; } }
     * two code blocks by specification, functionally identical.
     */


    public void produce() throws InterruptedException {
        synchronized (this) {
            System.out.println("Producer thread running ....");
            wait();//this.wait() is fine.
            System.out.println("Resumed.");
        }
    }

    public void consume() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        Thread.sleep(2000);
        synchronized (this) {
            System.out.println("Waiting for return key.");
            scanner.nextLine();
            System.out.println("Return key pressed.");
            notify();
            Thread.sleep(5000);
            System.out.println("Consumption done.");
        }
    }
}
```

### Reentrant Lock
---
Lock objects work very much like the implicit locks used by synchronized code. As with implicit locks, only one thread can own a Lock object at a time. Lock objects also support a wait/notify mechanism, through their associated Condition objects.

The biggest advantage of Lock objects over implicit locks is their ability to back out of an attempt to acquire a lock. The tryLock method backs out if the lock is not available immediately or before a timeout expires (if specified). The lockInterruptibly method backs out if another thread sends an interrupt before the lock is acquired.

cond.await() : atomic unlock and wait
https://stackoverflow.com/questions/32774399/why-lock-condition-await-must-hold-the-lock


```java
public class Runner {

    private int count = 0;
    private Lock lock = new ReentrantLock();
    private Condition cond = lock.newCondition();

    private void increment() {
        for (int i = 0; i < 10000; i++) {
            count++;
        }
    }

    public void firstThread() throws InterruptedException {
        lock.lock();
        System.out.println("Waiting ....");
        cond.await();
        System.out.println("Woken up!");
        try {
            increment();
        } finally {
            lock.unlock();
        }
    }

    public void secondThread() throws InterruptedException {
        Thread.sleep(1000);
        lock.lock();
        System.out.println("Press the return key!");
        new Scanner(System.in).nextLine();
        System.out.println("Got return key!");
        cond.signal();
        try {
            increment();
        } finally {
            //should be written to unlock Thread whenever signal() is called
            lock.unlock();
        }
    }

    public void finished() {
        System.out.println("Count is: " + count);
    }
}
```

### DeadLock
---
Deadlock describes a situation where two or more threads are blocked forever, waiting for each other.
```java
public class SimpleDeadLock {

    public static final Object lock1 = new Object();
    public static final Object lock2 = new Object();
    private int index;

    public static void main(String[] a) {
        Thread t1 = new Thread1();
        Thread t2 = new Thread2();
        t1.start();
        t2.start();
    }

    private static class Thread1 extends Thread {

        public void run() {
            synchronized (lock1) {
                System.out.println("Thread 1: Holding lock 1...");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
                System.out.println("Thread 1: Waiting for lock 2...");
                synchronized (lock2) {
                    System.out.println("Thread 2: Holding lock 1 & 2...");
                }
            }
        }
    }

    private static class Thread2 extends Thread {

        public void run() {
            synchronized (lock2) {
                System.out.println("Thread 2: Holding lock 2...");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
                System.out.println("Thread 2: Waiting for lock 1...");
                synchronized (lock1) {
                    System.out.println("Thread 2: Holding lock 2 & 1...");
                }
            }
        }
    }
}
```

### Semaphores
---
A counting semaphore. Conceptually, a semaphore maintains a set of permits. Each acquire() blocks if necessary until a permit is available, and then takes it. Each release() adds a permit, potentially releasing a blocking acquirer. 

```java
public class Connectionn {

    private static Connectionn instance = new Connectionn();
    /*
        limit connections to 10
        true means whichever thread gets first in the waiting pool (queue)
        waiting to acquire a resource, is first to obtain the permit.

        Note that I called it a pool!
        The reason is when you say "Queue", you're saying that things are
        scheduled to be FIFO (First In First Out) .. which is not always the case
        here!
        When you initialize the semaphore with Fairness, by setting its second
        argument to true, it will treat the waiting threads like FIFO.
        But,
        it doesn't have to be that way if you don't set on the fairness. the JVM
        may schedule the waiting threads in some other manner that it sees best
        (See the Java specifications for that).
    */
    private Semaphore sem = new Semaphore(10, true);

    private Connectionn() {
    }

    public static Connectionn getInstance() {
        return instance;
    }

    public void connect() {
        try {

            // get permit decrease the sem value, if 0 wait for release
            sem.acquire();

            System.out.printf("%s:: Current connections (max 10 allowed): %d\n",
                    Thread.currentThread().getName(),
                    sem.availablePermits());

            //do your job
            System.out.printf("%s:: WORKING...\n",
                    Thread.currentThread().getName());
            Thread.sleep(2000);

            System.out.printf("%s:: Connection released. Permits Left = %d\n",
                    Thread.currentThread().getName(),
                    sem.availablePermits());

        } catch (InterruptedException ignored) {
        } finally {
            //release permit, increase the sem value and activate waiting thread
            sem.release();
        }
    }
}
```
### Callable and Future
---
When the call() method completes, return value must be stored in an object in main thread, so that the main thread can know about the result
that the thread returned. For this, a Future object can be used. It holds Future as an object as a result – it may not hold it right now.
but it will do so in the future (once the Callable returns). Thus, a Future is basically one way the main thread can keep track of the progress and result from other threads


```java
class CallableImpl implements Callable<Integer> {

    private int myName;

    CallableImpl(int i) {
        myName = i;
    }

    @Override
    public Integer call() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Thread : " + getMyName() + " value is : " + i);
        }
        return getMyName();
    }

    public int getMyName() {
        return myName;
    }

    public void setMyName(int myName) {
        this.myName = myName;
    }
}

public class CallableTester {

    public static void main(String[] args) throws InterruptedException {

        Callable<Integer> callable = new CallableImpl(2);
        ExecutorService executor = new ScheduledThreadPoolExecutor(1);
        Future<Integer> future = executor.submit(callable);

        try {
            System.out.println("Future value: " + future.get());
        } catch (Exception ignored) {}
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }
}
```
Callable vs Runnable

* For implementing Runnable, the run() method needs to be implemented which does not return anything, while for a Callable, the call() method needs to be implemented which returns a result on completion. Note that a thread can’t be created with a Callable, it can only be created with a Runnable.
* Another difference is that the call() method can throw an exception whereas run() cannot.

### Atomic variables
` private AtomicInteger c = new AtomicInteger(0);`

