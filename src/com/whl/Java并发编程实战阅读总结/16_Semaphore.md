### 信号量模型

信号量模型由一个计数器、一个等待队列、三个方法组成，其中计数器、等待队列对外透明，只能通过三个方法间接访问它们，这三个方法分别是：init()、down()、up()，如下图所示：

![img](https://static001.geekbang.org/resource/image/6d/5c/6dfeeb9180ff3e038478f2a7dccc9b5c.png)

其中init()用于初始化计数器初始值，down()用于使计数器的值-1，如果计数器值<0，则当前线程阻塞，否则当前线程继续执行。up()用于使计数器的值+1，如果计数器的值<=0，则唤醒等待队列中的一个线程，并在等待队列中移除该线程。

在Java提供的信号量模型 j.u.c.Semaphore 中，down()、up()分别对应acquire()、release()。



### 如何使用信号量

我们可以通过信号量实现一个线程安全的计数器，示例代码如下所示：

```java
public class Test {
    static final Semaphore sep = new Semaphore(1);
    static int count;

    static void addOne() throws InterruptedException {
        sep.acquire();
        try {
            count++;
        } finally {
            sep.release();
        }
    }

	public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            new Thread (() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        addOne();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(1000);
        System.out.println(Test.count);
    }
}
```

下面我们具体分析，信号量是如何保证互斥的。假设两个线程t1、t2同时访问addOne()，当他们同时调用acquire()时，由于acquire()是一个原子操作，只能有一个线程将计数器的值减为0，另一个线程（假设是t2）则会把信号量的值减为-1，而t2则会进入到等待队列中阻塞，直到t1执行完临界区的代码后调用release()使计数器的值加为0，当值>=0，则会唤醒等待队列中的t2执行，通过信号量的控制，t2只能在t1执行完临界区的代码之后才能执行，从而保证了互斥性。



### 快速实现一个限流器

信号量不仅能实现互斥锁的功能，也可以允许指定数量的线程访问一个临界区，也就是限流器的功能。

在现实中，限流器一般使用在各种池化资源上，例如数据库连接池，在同一时刻是允许多个线程同时使用的，当然每个连接在被释放前，是不允许被其他线程使用的。

在上面的代码中，我们将信号量初始化时，构造方法参数设置为1，表示只允许一个线程进入临界区，如果我们将计数器的值设置为池中的N，就能够实现池的限流功能了。下面代码是一个简单的对象池限流器：

```java
public class ObjPool<T, R> {
    final List<T> pool;
    final Semaphore sep;

    //初始化对象池
    ObjPool(int size, T t) {
        pool = new Vector<T>(){};
        for (int i = 0; i < size; i++) {
            pool.add(t);
        }
        //初始化限流器
        sep = new Semaphore(size);
    }

    R exec(Function<T, R> function) throws InterruptedException {
        T t = null;
        sep.acquire();
        try {
            t = pool.remove(0);
            return function.apply(t);
        } finally {
            pool.add(t);
            sep.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //创建对象池
        ObjPool<Integer, Integer> pool = new ObjPool<Integer, Integer>(10, 2);

        pool.exec(t -> {
            System.out.println(t);
            return t;
        });
    }
}
```



