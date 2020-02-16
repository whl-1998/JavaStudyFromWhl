如果当前需求是实现一个询价应用，这个应用需要从三个电商平台询价，然后保存在本地数据库中。核心代码可以如下所示：

```java
// 向电商S1询价，并保存
r1 = getPriceByS1();
save(r1);
// 向电商S2询价，并保存
r2 = getPriceByS2();
save(r2);
// 向电商S3询价，并保存
r3 = getPriceByS3();
save(r3);
```

由于是串行执行的，因此性能很慢。我们可以采用ThreadPoolExecutor + Futrue的方案对其优化：

```java
// 创建线程池
ExecutorService executor = Executors.newFixedThreadPool(3);
// 异步向电商S1询价
Future<Integer> f1 = executor.submit(() -> getPriceByS1());
// 异步向电商S2询价
Future<Integer> f2 = executor.submit(() -> getPriceByS2());
// 异步向电商S3询价
Future<Integer> f3 = executor.submit(() -> getPriceByS3());

// 获取电商S1报价并保存
r = f1.get();
executor.execute(() -> save(r));
  
// 获取电商S2报价并保存
r = f2.get();
executor.execute(() -> save(r));
  
// 获取电商S3报价并保存  
r = f3.get();
executor.execute(() -> save(r));

```

上述方案本身不存在太大问题，但如果要获取电商S1报价的耗时很长，那么即便获取S2报价的耗时很短，也无法让保存S2的报价操作先执行。因为主线程阻塞在了f1.get()操作上，针对这个问题我们可以利用阻塞队列进行解决：

```java
// 创建阻塞队列
BlockingQueue<Integer> bq = new LinkedBlockingQueue<>();
//电商S1报价异步进入阻塞队列  
executor.execute(() -> bq.put(f1.get()));
//电商S2报价异步进入阻塞队列
executor.execute(() -> bq.put(f2.get()));
//电商S3报价异步进入阻塞队列  
executor.execute(() -> bq.put(f3.get()));
//异步保存所有报价
for (int i = 0; i < 3; i++) {
    Integer r = bq.take();
    executor.execute(() -> save(r));
}
```

上述代码中，先获取到的报价数据会先进入阻塞队列，然后线程池中的线程会阻塞获取队列中的数据，执行存储数据到本地数据库的任务。



### 利用CompletionService实现询价系统

虽然上述代码思路上并不存在问题，但并不建议在实际项目中自己手动实现这么个阻塞队列，因为Java并发包下已经提供了CompletionService，其实现原理也是在内部维护了一个阻塞队列，当任务执行结束后就把执行结果加入到阻塞队列中。不同的是CompletionService是把任务执行结果的Future对象加入到阻塞队列中，而上述代码是把最终的执行结果放入阻塞队列中。



### 如何创建CompletionService

CompletionService接口的实现类是ExecutorCompletionService，这个实现类有两个构造方法如下：

```java
ExecutorCompletionService(Executor executor);

ExecutorCompletionService(Executor executor, BlockingQueue> completionQueue);
```

这两个构造方法都需要传入一个线程池，如果不指定completionQueue，那么默认使用无界的LinkedBlockingQueue。任务执行结果的Futrue对象就是加入到这个队列中。

下列示例代码展示了如何利用CompletionService实现高性能的询价系统：

```java
// 创建线程池
ExecutorService executor = Executors.newFixedThreadPool(3);
// 创建CompletionService, 使用默认无界队列
// 当线程池中的任务执行完成时, 会将结果(Future)写入到阻塞队列中, 可以调用cs.take()获取
CompletionService<Integer> cs = ExecutorCompletionService<>(executor);

// 异步向电商S1询价
cs.submit(() -> getPriceByS1());
// 异步向电商S2询价
cs.submit(() -> getPriceByS2());
// 异步向电商S3询价
cs.submit(() -> getPriceByS3());

// 将询价结果异步保存到数据库
for (int i = 0; i < 3; i++) {
    //阻塞获取到询价结果
    Integer r = cs.take().get();
    //提交存储数据任务给线程池
    executor.execute(() -> save(r));
}
```



### CompletionService接口说明

CompletionService接口提供的方法有5个·，如下所示：

```java
Future<V> submit(Callable<V> task);
Future<V> submit(Runnable task, V result);
Future<V> take() throws InterruptedException;
Future<V> poll();
Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;
```

其中submit方法有两个，有些类似ThreadPoolExecutor的submit方法，一个方法参数是Callable，允许传参；一个方法参数是Runnable task 和 V result，result用于主线程与子线程共享数据。

其余三个方法都与阻塞队列有关，区别在于阻塞队列为空时，take()会阻塞式出队；而poll()方法会返回null值。其中poll(long timeout, TimeUnit unit)支持以超时方式出队，如果等待时间到达，则返回null值。



### 总结

当批量提交异步任务时，建议采用CompletionService。CompletionService将线程池Executor和阻塞队列BlockingQueue成功融合在一起，是的批量异步任务的管理更简单。除此以外，CompletionService能够让异步任务的执行结果有序化，先执行结束的进入阻塞队列，利用这个特性可以轻松实现后序处理的有序性，避免无谓的等待。