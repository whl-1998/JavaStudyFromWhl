### Executors

考虑到ThreadPoolExecutor的构造函数比较复杂，所以Java并发包下提供了能够快速创建线程池的静态工厂类Executors，但是由于Executors提供的很多方法都默认采用无界的LinkedBlockingQueue作为workQueue，因此在高负载的情况下，无界队列很容易导致OOM异常，致使所有请求都无法处理，因此不建议在工程中使用Executors，而是通过ThreadPoolExecutor指定有界队列作为workQueue创建线程池。

使用有界队列，当线程池中工作线程都处于工作状态，并且workQueue已满，那么线程池会触发拒绝策略，默认时拒绝任务提交并抛出RejectedExecutionException这个RuntimeExeception，由于它不需要在编译器显示进行捕获，因此RejectedExecutionException很容易被忽略。所以默认拒绝策略要慎重使用，如果线程池处理的任务非常重要，建议自定义拒绝策略；并且在实际工作中，自定义的拒绝策略往往和降级策略配合使用。

### Executors部分源码分析

利用Executors可以创建不同的线程池以满足不同场景的需求。

#### 1. newFixedThreadPool(int nThreads)

```java
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
}

public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
    return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
}
```

可以看到，这是在内部调用了ThreadPoolExecutor，创建一个“至少保证工作的线程数“为nThreads、最多创建nThreads的线程池，内部任务队列是通过无界队列存储的。当处于工作中的线程数没有达到nThreads，线程池会创建线程并分配任务直到工作线程数满足nThreads。

#### 2. newCacheThreadPool()

```java
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
}

public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory) {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
}
```

创建一个不限制创建线程数量的线程池，但池中线程如果处于闲置状态60s后会被自动回收。这种线程的优点是当系统长时间闲置的时候，也不会消耗太多资源，一般用于处理大量的短时间任务。

#### 3. newSingleThreadExecutor()

```java
public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService
        (new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
}

public static ExecutorService newSingleThreadExecutor(ThreadFactory threadFactory) {
    return new FinalizableDelegatedExecutorService
        (new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory));
}
```

指定工作线程数、池中最大线程数都为1，保证池中只会有一个线程执行任务。如果池中唯一的线程因为异常退出，那么会再次创建一个新的线程继续工作。它内部的工作队列也是无界的。

#### 4. newWorkStealingPool()

```java
public static ExecutorService newWorkStealingPool() {
        return new ForkJoinPool
            (Runtime.getRuntime().availableProcessors(),
             ForkJoinPool.defaultForkJoinWorkerThreadFactory,
             null, true);
    }
```

内部会构建ForkJoinPool，利用“工作窃取算法”并行处理任务，但不保证处理顺序。Fork/Join框架就是把一个大任务拆分成多个小任务，然后将多个小任务执行的结果合并成大任务执行的结果。“工作窃取算法”简单来说就是：当某个线程将属于自己的任务队列（这个队列是双端的）执行完毕后，会去其他的任务队列中窃取任务执行。



