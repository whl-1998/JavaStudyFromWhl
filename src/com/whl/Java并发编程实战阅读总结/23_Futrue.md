在22讲中，讲到了如何创建线程池，并且可以通过ThreadPoolExecutor的void execute(Runnable command)方法提交任务，但是这种方式并不能获取任务的执行结果，因为execute()方法并不存在返回值，如果需要获取任务的执行结果，那么就需要ThreadPoolExecutor提供的Futrue实现。



### 获取ThreadPoolExecutor提交的任务的执行结果

ThreadPoolExecutor提供了3个submit()方法和1个FutureTask工具类来支持获取任务执行结果的需求：

```java
// 提交Runnable任务
Future<?> submit(Runnable task);
// 提交Callable任务
<T> Future<T> submit(Callable<T> task);
// 提交Runnable任务及结果引用  
<T> Future<T> submit(Runnable task, T result);
```

可以发现，三个submit()方法的返回值都是Future类型，它们的区别主要在于参数的不同：

1. 提交Runnable任务

   Runnable接口的run()方法是没有返回值的，因此submit()这个方法返回的Future仅仅可以用于断言任务执行结束，类似于Thread.join()。

2. 提交Callable任务

   Callable接口下只有一个call()方法，并且这个方法是有返回值的，因此这个方法返回的Future对象可以通过调用get()方法获取任务的执行结果。

3. 提交Runnable任务以及result对象

   假设方法返回的Futrue对象是f，f.get()返回的就是传入的result。这个方法其实就是用于构建主线程与线程池中线程的桥梁，通过这个方法使主线程、子线程之间能够共享数据。使用的示例代码如下所示：

   ```java
   ExecutorService executor = Executors.newFixedThreadPool(1);
   // 创建Result对象r
   Result r = new Result();
   r.setAAA(a);
   // 提交任务
   Future<Result> future = executor.submit(new Task(r), r);  
   Result fr = future.get();//fr == r;
   
   //实现Runnable接口, 用于附带主线程数据result
   class Task implements Runnable{
       Result r;
       //通过构造函数传入result
       Task(Result r){
           this.r = r;
       }
       void run() {
           //可以操作result
           a = r.getAAA();
           r.setXXX(x);
       }
   }
   ```

   

Futrue接口下存在5个方法，如下所示：

```java
// 取消任务
boolean cancel(boolean mayInterruptIfRunning);
// 判断任务是否已取消  
boolean isCancelled();
// 判断任务是否已结束
boolean isDone();
// 获得任务执行结果
get();
// 获得任务执行结果，支持超时
get(long timeout, TimeUnit unit);
```

需要注意的是，两个get()方法都是阻塞式的。如果被调用时任务还未执行完，那么调用get()方法的线程会被阻塞，直到任务执行完才唤醒。



Future是一个接口，而FutureTask是一个工具类，这个工具类包含两个构造函数，它的参数与前面的submit()方法类似：

```java
FutureTask(Callable<V> callable);
FutureTask(Runnable runnable, V result);
```

FutureTask同时实现了Runnable和Future接口。由于实现了Runnable接口，可以将FutureTask对象作为任务提交给ThreadPoolExecutor执行，也可以直接被Thread执行；由于实现了Future接口，也能够用于获取任务执行的结果。如下代码实例：

```java
// 创建FutureTask 任务是执行 1+2
FutureTask<Integer> futureTask = new FutureTask<>(() -> 1 + 2);
// 创建线程池
ExecutorService es = Executors.newCachedThreadPool();
// 提交FutureTask
es.submit(futureTask);
// 获取计算结果
Integer result = futureTask.get();

//交给Thread执行--------
// 创建并启动线程
Thread T1 = new Thread(futureTask);
T1.start();
// 获取计算结果
Integer result = futureTask.get();
```

由此可见，FutureTask对象可以很容易地获取到子线程的执行结果。



### 实现最优的“烧水泡茶”

烧水泡茶的最优工序如下所示：

![img](https://static001.geekbang.org/resource/image/86/ce/86193a2dba88dd15562118cce6d786ce.png)

**并发编程可以总结为三个核心问题：分工、同步、互斥。**根据这个思路，对于烧水泡茶这个程序，一种最优的分工方案可以是：用两个线程T1、T2来完成烧水泡茶程序，T1负责洗水壶、烧开水、泡茶；T2负责洗茶壶、洗茶杯、拿茶叶。其中T1在执行泡茶这个工序之前，需要先等待T2执行完拿茶叶的工序。对于T1这个等待动作，可以采用诸多方式解决，例如join()、CountDownLatch、阻塞队列等，下面采用FutureTask实现：

```java
// 创建任务T2的FutureTask
FutureTask<String> ft2 = new FutureTask<>(new T2Task());
// 创建任务T1的FutureTask
FutureTask<String> ft1 = new FutureTask<>(new T1Task(ft2));
// 线程T1执行任务ft1
Thread T1 = new Thread(ft1);
T1.start();
// 线程T2执行任务ft2
Thread T2 = new Thread(ft2);
T2.start();
// 等待线程T1执行结果, 因为是阻塞式获取, 因此必须要等待T1执行完才能获取到结果
System.out.println(ft1.get());

// T1Task需要执行的任务：
// 洗水壶、烧开水、泡茶
class T1Task implements Callable<String> {
    FutureTask<String> ft2;
    
    // T1任务需要T2任务的FutureTask
    T1Task(FutureTask<String> ft2) {
        this.ft2 = ft2;
    }
    
    @Override
    String call() throws Exception {
        System.out.println("T1:洗水壶...");
        TimeUnit.SECONDS.sleep(1);

        System.out.println("T1:烧开水...");
        TimeUnit.SECONDS.sleep(15);
        
        // 获取T2线程的茶叶
        String tf = ft2.get();
        System.out.println("T1:拿到茶叶:"+tf);

        System.out.println("T1:泡茶...");
        return "上茶:" + tf;
    }
}

// T2Task需要执行的任务:
// 洗茶壶、洗茶杯、拿茶叶
class T2Task implements Callable<String> {
    @Override
    String call() throws Exception {
        System.out.println("T2:洗茶壶...");
        TimeUnit.SECONDS.sleep(1);

        System.out.println("T2:洗茶杯...");
        TimeUnit.SECONDS.sleep(2);

        System.out.println("T2:拿茶叶...");
        TimeUnit.SECONDS.sleep(1);
        return "龙井";
    }
}


// 一次执行结果：
T1:洗水壶...
T2:洗茶壶...
T1:烧开水...
T2:洗茶杯...
T2:拿茶叶...
T1:拿到茶叶:龙井
T1:泡茶...
上茶:龙井
```

上述示例代码中，ft1完成洗水壶、烧开水、泡茶的任务；ft2完成洗茶壶、洗茶杯、拿茶叶的任务。需要注意的是，线程ft1在执行泡茶任务之前，需要阻塞等待tf2的任务执行完成，获取到茶叶，因此ft1内部需要引用ft2，并在执行泡茶之前，调用ft2的get()方法实现阻塞等待。