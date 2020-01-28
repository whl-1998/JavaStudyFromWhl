假设，需求是对某个对账系统的业务进行优化，业务的流程如下：

1. 用户通过在线商城下单，生成电子订单，保存在订单库。
2. 之后物流生成派送单给用户发货，派送单保存在派送单库。
3. 为了防止漏派送或者重复派送，对账系统每天还会校验是否存在异常订单。

通过代码抽象之后如下所示：

```java
while(存在未对账订单) {
    // 查询未对账订单
    pos = getPOrders();
    // 查询派送单
    dos = getDOrders();
    // 执行对账操作
    diff = check(pos, dos);
    // 差异写入差异库
    save(diff);
}
```

由于订单量和派送单量巨大，所以getPOrders()、getDOrders()这两个操作相对较慢，并且当前对账系统是单线程执行的，如下图所示：

![img](https://static001.geekbang.org/resource/image/cd/a5/cd997c259e4165c046e79e766abfe2a5.png)



### 利用并行优化

首先对于串行执行的系统，优化性能首先能够想到的是能否通过并行处理优化。getPOrders()、getDOrders()这两个操作并没有顺序上的依赖关系，可以通过并行处理这两个最耗时的方法，如下图所示：



![img](https://static001.geekbang.org/resource/image/a5/3b/a563c39ece918578ad2ff33ab5f3743b.png)

可以发现，并行执行的吞吐量几乎是单线程的2倍，优化效果非常明显，代码实现如下所示：

```java
while(存在未对账订单){
    // 查询未对账订单
    Thread T1 = new Thread(()->{
        pos = getPOrders();
    });
    T1.start();
    
    // 查询派送单
    Thread T2 = new Thread(()->{
        dos = getDOrders();
    });
    T2.start();
    
    // 等待T1、T2结束
    T1.join();
    T2.join();
    
    // 执行对账操作
    diff = check(pos, dos);
    // 差异写入差异库
    save(diff);
}
```



### 利用线程池优化

经过上述的优化后，还存在一些不足，每当存在未对账的订单时都会创建新的线程，而创建线程是个耗时的操作，此时我们可以通过线程池来解决这个问题，代码如下：

```java
// 创建2个线程的线程池
Executor executor = Executors.newFixedThreadPool(2);
while(存在未对账订单) {
    // 查询未对账订单
    executor.execute(()-> {
        pos = getPOrders();
    });
    // 查询派送单
    executor.execute(()-> {
        dos = getDOrders();
    });

    // 等待T1、T2结束
    //TODO

    // 执行对账操作
    diff = check(pos, dos);
    // 差异写入差异库
    save(diff);
}
```

我们通过创建了一个固定大小为2的线程池，实现了线程的循环利用，避免了频繁创建线程的开销。但是对账系统的业务逻辑中，需要先等待getPOrders()、getDOrders()这两个方法执行完完毕才能继续后序的对账操作。在没有采用线程池的方案中，我们采用了join()方法实现了等待的操作，但在采用线程池的方案中，join()是失效的。

解决的方案其实很简单，设置一个计数器，初始值为任务个数，每当一个任务执行完毕时计数器值减1。当计数器的值减为0时，说明任务已经执行完毕。对于这个计数器，我们不需要自己实现，因为Java已经给我们提供了现成的轮子——CountDownLatch。

我们可以对上面的代码进行改进，加上CountDownLatch：

```java
// 创建2个线程的线程池
Executor executor = Executors.newFixedThreadPool(2);
while(存在未对账订单) {
    // 计数器初始化为2
    CountDownLatch latch = new CountDownLatch(2);
   
    // 查询未对账订单
    executor.execute(()-> {
        pos = getPOrders();
    	//任务执行完成时, 计数器值-1
        latch.countDown();
    });
    // 查询派送单
    executor.execute(()-> {
        dos = getDOrders();
        //任务执行完成时, 计数器值-1
        latch.countDown();
    });

    // 等待两个查询操作结束
    latch.await();

    // 执行对账操作
    diff = check(pos, dos);
    // 差异写入差异库
    save(diff);
}
```



### 进一步优化

其实经过上面的优化后，性能已经不错了，但其实上面的代码还是存在优化的余地。在上述优化方案中，getPOrders() 和 getDOrders()这两个操作并行了，但这两个查询操作与对账操作之间check()、save()之间还是串行的。我们可以通过生产者 - 消费者模型进一步优化，两个查询操作作为生产者，check()、save()作为消费者，以此达到并行，如下图所示：

![img](https://static001.geekbang.org/resource/image/e6/8b/e663d90f49d9666e618ac1370ccca58b.png)

既然是生产者 - 消费者模型，那么就需要用队列来保存生产者的数据，消费者只需要从队列中消费数据即可。针对这个对账系统，可以设计两个互相存在对应关系的队列，订单查询操作getPOrders()将结果写入订单队列；派送单查询操作getDOrders()将结果写入派送单队列，如下图所示：

![img](https://static001.geekbang.org/resource/image/22/da/22e8ba1c04a3bc2605b98376ed6832da.png)

使用两个互相存在对应关系的队列，每次check时，从两个队列中各取一个元素进行操作，保证了数据一定不会打乱。

上述方案存在着两个重要的条件：

1. 生产者线程T1、T2要保持步调一致，也就是T1生产完成后必须要等待T2也生产完成。
2. 在T1、T2都生产完成后，需要通知消费者线程T3。

其实，理论上只要基于上述计数器方案的实现再添加一个计数器重置的操作就可以了：计数器初始值为2，生产者线程T1、T2在生产出一条数据后计数器的值都减1，如果计数器等于0，则通知消费者线程T3处理数据，于此同时，将计数器重置为2，再次唤醒生产者线程T1、T2生产数据。

但是Java已经给我们提供了相关的实现类：CyclicBarrier，代码实现如下：

```java
// 订单队列
Vector<P> pos;
// 派送单队列
Vector<D> dos;

// 执行回调的线程池
Executor executor = Executors.newFixedThreadPool(1);
// 初始化barrier, 计数器值为2, 当计数器值为0时执行check()
final CyclicBarrier barrier = new CyclicBarrier(2, ()-> {
    executor.execute(() -> check());
});

void check() {
    //
    P p = pos.remove(0);
    D d = dos.remove(0);
    // 执行对账操作
    diff = check(p, d);
    // 差异写入差异库
    save(diff);
}

void checkAll() {
    // 循环查询订单库
    Thread T1 = new Thread(()->{
        while(存在未对账订单){
            // 查询订单库, 并将结果添加到订单队列
            pos.add(getPOrders());
            // 成功添加后, barrier计数器值-1
            barrier.await();
        }
    });
    T1.start();
    
    // 循环查询运单库
    Thread T2 = new Thread(()->{
        while(存在未对账订单){
            // 查询运单库, 并将结果添加到配送单队列
            dos.add(getDOrders());
            // 成功添加后, barrier计数器值-1
            barrier.await();
        }
    });
    T2.start();
}
```

值的一提的是，CyclicBarrier的计数器有自动重置的功能，当减为0时，会自动重置为初始值。