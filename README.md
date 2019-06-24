# Collections and Exceptions

Welcome to my introduction to Collections and Exceptions. This tutorial guides you step by step through all the concepts and the implementation part. Backed by short and simple code samples you'll learn how to use collection methods, common collections, and exceptions. 

## Table of Contents

* Collection
	* The List Interface
	* The Set Interface
	* The Queue Interface
	* Priority Queue
	* The Map Interface
	* Object Ordering
* Exceptions
	* How exceptions work ?
	* Types of exception
	* Catch and Specify Exception
	* Specifying exception
	* Exception Chaining
	* Creating our own Exception


## The Collection Interface

![Collection hierarchy](https://github.com/DeepakPatankar/java_tutorial/blob/master/java-collection-hierarchy.png)

A collection helps us to store and manipulate objects. We can do operations like addition, deletion, searching, sorting, modifying.

|  Type | Operation | Syntax |
| --- | --- | --- |
|  Modification | public boolean add(E e) | list.add("a"); |
|   | public boolean remove(Object element) | list.remove(obj); |
|   | public int clear() | list.clear(); |
|  Query | public void size() | list.size(); |
|   | public boolean contains(Object element) | list.contains(obj); |

For Iterations  we have two methods:
1.  for-each method
```java
for (Object o : collection)
           System.out.println(o);
```
	
2.  Iterator
     The interface of iterator is :
```java 
public interface Iterator<E> {
   	 boolean hasNext();
   	 E next();
   	 void remove(); //optional
}
```
	Remove function deletes the last element returned by the function `next`.

Some bulk operations supported are : 
* **containsAll** — returns true if the target Collection contains all of the elements in the specified Collection.
* **addAll** — adds all of the elements in the specified Collection to the target Collection.
* **removeAll** — removes from the target Collection all of its elements that are also contained in the specified Collection.
* **retainAll** — removes from the target Collection all its elements that are not also contained in the specified Collection. That is, it retains only those elements in the target Collection that are also contained in the specified Collection.


### The Set Interface
---
A set can't contain duplicate entry.
1. Example : 
	```java
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
	```
	
	There are three ways in which sets can be implemented

|  **Implementation** | **Data Structure Used** | **Addition** | **Deletion** | **Searching** |
| --- | --- | --- | --- | --- |
|  HashSet | Hash table | O(1) | O(1) | O(1) |
|  TreeSet | Red Black Tree | O(logn) | O(logn) | O(logn) |
|  LinkedHashSet | Hash Table + Linked List | O(1) | O(1) | O(1) |

LinkedTreeSet is used to maintain the order of the set.
All the above time complexity assumes no collisions.


### The List Interface
---
Operations
* Positional access and Searching:

|  Operation | Sytax |
| --- | --- |
|  E get(int index) | list.get(2); |
|  E set(int index, E element) | list.set(2,"cool") |
|  int indexOf(Object o) | list.indexOf("cool") |

* ListIterator
```java
for (ListIterator<Type> it = list.listIterator(list.size()); it.hasPrevious(); ) {
    Type t = it.previous();
    ...
}
```
* Range view operation
```java
	reqlist = list.subList(fromIndex, toIndex)
```

Is this correct ?
```java
ArrayList<Integer> arr=new ArrayList<Integer>(10);
arr.add(5, 10);
```

Difference between ArrayList and LinkedList

|   | ArrayList | LinkedList |
| --- | --- | --- |
|  Implemented using | Dynamic Array | Doubly Linked List |
|  Element Removal | Slow | Faster than ArrayList |
|  Implements | List | List and Deque |
|  When to use ? | For storing and Accessing | When manipulation required |


Example : 
```java
import java.util.*;
public class ListExample {
    public static void main(String[] args) {
        List<Integer> s = Arrays.asList(new Integer[10]);
        System.out.println(s.size());
        for(int i = 0; i < 10; i++ )
            s.set(i,5*i);

        System.out.println("The third element is " + s.get(2));

        List<Integer> l2 = s.subList(2,7);

        for(ListIterator<Integer> i = l2.listIterator(); i.hasNext();)
            System.out.println(i.next());
    }
}
```

### The Queue Interface
---
```java
public interface Queue<E> extends Collection<E> {
    E element();
    boolean offer(E e);
    E peek();
    E poll();
    E remove();
}
```

* Insert : offer(e)
* Remove : poll() and remove()
* Examine : peek() and element()

Is there a difference between poll() and remove() ? When to use which one ?
	`When the queue is empty poll() return null while remove() throws exception.`

Example:
```java
import java.util.*;

public class CountDown {
    public static void main(String[] args) throws InterruptedException {
        ;
        Queue<Integer> queue = new LinkedList<Integer>();

        for (int i = 10; i >= 0; i--)
            queue.add(i);

        queue.offer(-1);
        System.out.println("Top" + queue.peek());
        queue.poll();

        while (!queue.isEmpty()) {
            System.out.println(queue.remove());
            Thread.sleep(1000);
        }
    }
}
```

### Priority Queue
---
`public PriorityQueue(int initialCapacity,
             Comparator<? super E> comparator)
`
Example: 
```java
import java.util.*;
public class PriorityEX{
    public static void main(String args[]){
        PriorityQueue<String> queue=new PriorityQueue<String>();
        queue.add("Deepak");
        queue.add("Rohit");
        queue.add("Meenakshi");
        queue.add("Ujjawal");
        queue.add("Shubhanshu");
        queue.add("Aman");
        System.out.println("head:" + queue.element());
        System.out.println("iterating the queue elements:");
        Iterator itr=queue.iterator();
        while(itr.hasNext()){
            System.out.println(itr.next());
        }
        queue.remove();
        queue.poll();
        System.out.println("after removing two elements:");
        Iterator<String> itr2=queue.iterator();
        while(itr2.hasNext()){
            System.out.println(itr2.next());
        }
    }
}
```

For general class:
1.	Pass Custom` Comparator`:
```java
	Comparator<String> stringLengthComparator = new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.length() - s2.length();
            }
        };
		PriorityQueue<String> namePriorityQueue = new PriorityQueue<>(stringLengthComparator);
```
2.  Override `compareTo` :
```java
public int compareTo(Employee employee) {
        if(this.getSalary() > employee.getSalary()) {
            return 1;
        } else if (this.getSalary() < employee.getSalary()) {
            return -1;
        } else {
            return 0;
        }
    }
```


### The Map Interface
![Map Hierarchy](https://github.com/DeepakPatankar/java_tutorial/blob/master/java-map-hierarchy.png)
---
Contains (key,value) pair. Map does not allow duplicate key.
* HashMap : Implements Map
* LinkedHashMap : Maintains insertion order.
* TreeMap: Maintains Ascending order.

`Map map=new HashMap();  `

|   | Operation | Syntax |
| --- | --- | --- |
|  Modification | V put(Object key, Object value) | mp.put(1,"one"); |
|   | V remove(Object key) | mp.remove(1); |
|  Query | Set keySet() | s = mp.keySet(); |
|   | V get(Object key) | v = mp.get(key); |

```java
import java.util.*;  
public class MapExample1 {  
public static void main(String[] args) {  
    Map map=new HashMap(); 
	
    //Adding elements to map  
    map.put(1,"Amit");  
    map.put(5,"Rahul");  
    map.put(2,"Jai");  
    map.put(6,"Amit");  
	
    //Traversing Map  
    Set set=map.entrySet();//Converting to Set so that we can traverse  
	
    Iterator itr=set.iterator();  
    while(itr.hasNext()){  
        //Converting to Map.Entry so that we can get key and value separately  
        Map.Entry entry=(Map.Entry)itr.next();  
        System.out.println(entry.getKey()+" "+entry.getValue());  
    }  
}  
}  
```

Could have also used : 
`Map<Integer,String> map=new HashMap<Integer,String>();  `


**How is HashMap implemented ?**

HashMap is a part of the Java collection framework. It uses a technique called Hashing. It implements the map interface. It stores the data in the pair of Key and Value. HashMap contains an array of the nodes, and the node is represented as a class. It uses an array and LinkedList data structure internally for storing Key and Value. There are four fields in HashMap.

![Map data](https://github.com/DeepakPatankar/java_tutorial/blob/master/working-of-hashmap-in-java.png)



### Object Ordering
---
* Using Comparator
```java
import java.util.*;
public class EmpSort {
    static final Comparator<Employee> SENIORITY_ORDER = 
                                        new Comparator<Employee>() {
            public int compare(Employee e1, Employee e2) {
                return e2.hireDate().compareTo(e1.hireDate());
            }
    };

    // Employee database
    static final Collection<Employee> employees = ... ;

    public static void main(String[] args) {
        List<Employee> e = new ArrayList<Employee>(employees);
        Collections.sort(e, SENIORITY_ORDER);
        System.out.println(e);
    }
}
```

*Using Comparable
```java
import java.util.*;

public class Name implements Comparable<Name> {
    private final String firstName, lastName;

    public Name(String firstName, String lastName) {
        if (firstName == null || lastName == null)
            throw new NullPointerException();
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String firstName() { return firstName; }
    public String lastName()  { return lastName;  }

    public boolean equals(Object o) {
        if (!(o instanceof Name))
            return false;
        Name n = (Name) o;
        return n.firstName.equals(firstName) && n.lastName.equals(lastName);
    }

    public int hashCode() {
        return 31*firstName.hashCode() + lastName.hashCode();
    }

    public String toString() {
	return firstName + " " + lastName;
    }

    public int compareTo(Name n) {
        int lastCmp = lastName.compareTo(n.lastName);
        return (lastCmp != 0 ? lastCmp : firstName.compareTo(n.firstName));
    }
}
```
### Algorithms

|  Algorithm | Syntax |
| --- | --- |
|  Sort | Collections.sort(list); |
|  Shuffle | Collections.shuffle(list); |
|  reverse | Collections.reverse(list); |
|  swap | collection.swap(list,1,2); |
|  binary search | int pos = Collections.binarySearch(list, key); |


## Exceptions

When an error occurs within a method, the method creates an object and hands it off to the runtime system. The object, called an exception object, contains information about the error, including its type and the state of the program when the error occurred. Creating an exception object and handing it to the runtime system is called throwing an exception.

After a method throws an exception, the runtime system attempts to find something to handle it. The set of possible "somethings" to handle the exception is the ordered list of methods that had been called to get to the method where the error occurred. The list of methods is known as the call stack.

![Exception Handling](https://github.com/DeepakPatankar/java_tutorial/blob/master/exceptions-errorOccurs.gif)

### The Catch and Specify

A program must satisfy the catch and specify requirement:
* A try statement that catches the exception. The try must provide a handler for the exception, as described in Catching and Handling Exceptions.
* A method that specifies that it can throw the exception. The method must provide a throws clause that lists the exception, as described in Specifying the Exceptions Thrown by a Method.

**Types**
1. Checked Error
2. Error : These are exceptional conditions that are external to the application, and that the application usually cannot anticipate or recover from. For example, suppose that an application successfully opens a file for input, but is unable to read the file because of a hardware or system malfunction. 
3. These are exceptional conditions that are internal to the application, and that the application usually cannot anticipate or recover from. These usually indicate programming bugs, such as logic errors or improper use of an API.

try this code : 
```java
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class ListOfNumbers {

    private List<Integer> list;
    private static final int SIZE = 10;

    public ListOfNumbers () {
        list = new ArrayList<Integer>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            list.add(new Integer(i));
        }
    }

    public void writeList() {
	// The FileWriter constructor throws IOException, which must be caught.
        PrintWriter out = new PrintWriter(new FileWriter("OutFile.txt"));

        for (int i = 0; i < SIZE; i++) {
            // The get(int) method throws IndexOutOfBoundsException, which must be caught.
            out.println("Value at: " + i + " = " + list.get(i));
        }
        out.close();
    }
}
```

Syntax 
```java
try {
    code
}
catch and finally blocks . . .
```

example : 

```
try {
} catch (IndexOutOfBoundsException e) {
    System.err.println("IndexOutOfBoundsException: " + e.getMessage());
} catch (IOException e) {
    System.err.println("Caught IOException: " + e.getMessage());
}
```

**finally**
It will be executed even when try or catch happens. 

**Why finally ?**
 It allows the programmer to avoid having cleanup code accidentally bypassed by a return, continue, or break. Putting cleanup code in a finally block is always a good practice, even when no exceptions are anticipated.

## Specifying Exception
`public void writeList() throws IOException, IndexOutOfBoundsException {`

A function above in the call stack handles the exception.

## Throwing exception 
```java
public Object pop() {
    Object obj;

    if (size == 0) {
        throw new EmptyStackException();
    }

    obj = objectAt(size - 1);
    setObjectAt(size - 1, null);
    size--;
    return obj;
}
```

### Exception chaining
```java
try {

} catch (IOException e) {
    throw new SampleException("Other IOException", e);
}
```

** Why throw early, catch later ? **
We want to catch the exception at a particular point where we have enought information about the error.

### Creating Exception
```java
// A Class that represents use-defined expception 
class MyException extends Exception 
{ 
    public MyException(String s) 
    { 
        // Call constructor of parent Exception 
        super(s); 
    } 
} 
  
// A Class that uses above MyException 
public class Main 
{ 
    // Driver Program 
    public static void main(String args[]) 
    { 
        try
        { 
            // Throw an object of user defined exception 
            throw new MyException("New Exception Caught"); 
        } 
        catch (MyException ex) 
        { 
            System.out.println("Caught"); 
  
            // Print the message from MyException object 
            System.out.println(ex.getMessage()); 
        } 
    } 
} 
```


