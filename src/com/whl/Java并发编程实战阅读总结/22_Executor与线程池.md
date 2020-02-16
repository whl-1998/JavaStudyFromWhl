### 线程池

在Java语言中，创建一个线程看似跟创建一个对象没什么区别，但创建对象仅仅需要在JVM堆内存中分配一块内存，而线程却需要调用操作系统内核API为线程分配一系列的资源，这个开销导致了线程是一个重量级的对象，应该避免被频繁的创建和销毁。为此，我们可以通过线程池对线程的创建和销毁进行管理。

线程池采用了生产者 - 消费者的设计模式（其实线程池本身的应用也属于Worker-Thread模式，后面章节会提及），阻塞队列作为生产者，提交的任务都会交给阻塞队列；线程作为消费者，提取阻塞队列中的任务并执行。下面这段代码是一个简单的线程池实现：

```java
class MyThreadPool{
   //利用阻塞队列实现生产者-消费者模式
    BlockingQueue<Runnable> workQueue;
    //通过List保存池中的工作线程
    List<WorkerThread> threads = new ArrayList<>();
    // 构造方法
    MyThreadPool(int poolSize, BlockingQueue<Runnable> workQueue){
        this.workQueue = workQueue;
        // 通过poolSize指定的工作线程个数创建工作线程
        for(int idx = 0; idx < poolSize; idx++){
            WorkerThread work = new WorkerThread();
            work.start();//启动线程
            threads.add(work);//将创建的工作线程添加到List
        }
    }
    
    // 提交任务
    void execute(Runnable command) throws InterruptedException {
        workQueue.put(command);
    }
    
    // 工作线程负责消费任务，并执行任务
    class WorkerThread extends Thread{
        public void run() {
            //循环取任务并执行
            while(true){
                Runnable task = null;
                try {
                    //从阻塞队列中阻塞获取任务, 如果有任务就获取, 没有就阻塞等待
                    task = workQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //执行任务
                task.run();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建有界阻塞队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(2);
        // 创建线程池
        MyThreadPool pool = new MyThreadPool(10, workQueue);
        // 提交任务
        pool.execute(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
            }
        });
    }
}
```

在MyThread内部，我们通过维护一个大小为2的阻塞队列用于存放Runnable类型的任务对象，并指定工作线程为10个创建线程池。用户通过pool.execute()方法提交Runnable任务到队列，也就是生产过程；线程池会在内部消费阻塞队列中的任务并交给工作线程执行。



### Java中的线程池

JUC包中提供的线程池相关工具类中，最核心的是ThreadPoolExecutor，它强调的是Executor（执行者），而不是一般意义上的池化资源。但线程池确实也是一个用于创建并管理工作线程，执行Runnable任务的存在。

ThreadPoolExecutor的构造函数参数如下：

```java
public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
	//.....
}

```

1. corePoolSize：指定线程池保证的最少工作线程数。

2. maximumPoolSize：指定线程池创建的最大线程数，当判断需要继续创建工作线程并行执行任务时，maximumPoolSize限制了线程池此时创建工作线程数的最大值。

3. keepAliveTime & unit：当某些线程在执行完任务进入闲置状态后，如果闲置超过指定时间，那么就可以回收它们。

4. workQueue：工作队列，也就是用于存放Runnable任务的。

5. threadFactory：指定这个参数可以自定义如何创建工作线程，例如指定线程名字等。

6. handler：通过这个参数可以自定义任务的拒绝策略。如果线程池中所有的线程都处于工作状态，并且workQueue也满了（前提是工作队列有界），那么此时再提交Runnable任务到工作队列的请求会被拒绝，而拒绝的策略就可以通过handler指定。ThreadPoolExecutor已经提供了下面4种策略：

   * CallerRunsPolicy：提交任务的线程选择不交给线程池执行，而是自己执行。就好像你让同学帮你写作业，他不帮你写，此时你只能决定自己写。

   * AbortPolicy：默认的拒绝策略，会抛出一个RejectedExecutionException异常。

   * DiscardPolicy：直接丢弃这个任务，不抛出异常。

   * DiscardOldestPolicy：丢弃最老的任务，也就是把最早进入工作队列中的任务丢弃，然后把新任务加入到工作队列。



### 使用线程池的注意事项

1. 由于ThreadPoolExecutor的构造函数比较复杂，Java为我们提供了静态工厂类Executors，它对ThreadPoolExecutor进行了封装，利用Executors可以快速创建线程池。但是它存在一个很大的问题，因为利用Executors创建的线程池默认都是使用的无界阻塞队列，这很可能因为提交任务过多从而导致OOM。
2. 如果使用有界队列创建线程池，当线程池中所有的线程都处于工作状态，并且阻塞队列已满，线程池默认的拒绝策略会抛出RejectedExecutionException这个运行时异常，因此实际工作中最好自定义拒绝策略。

3. 使用线程池还要注意异常处理的问题，例如线程池中的线程在执行任务的过程中出现运行时异常，就会导致执行任务的线程终止。最致命的并不是抛出异常，而是线程因为异常终止并不会抛出任何信息，这会让我们误以为任务执行得很正常。虽然线程池提供了很多用于异常处理的方法，但是最稳妥和简单的方案还是捕获所有异常并按需处理，你可以参考下面的示例代码：

   ```java
   try {
       //业务逻辑
   } catch (RuntimeException x) {
       //按需处理
   } catch (Throwable x) {
       //按需处理
   } 
   ```