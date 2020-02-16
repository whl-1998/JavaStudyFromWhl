利用多线程优化性能，本质上就是把串行操作转换为并行操作，例如下面的代码实现：

```java
//串行执行
doBizA();
doBizB();

//并行执行
new Thread(() -> doBizA()).start();
new Thread(() -> doBizB()).start();
```

通过创建两个子线程去执行这两个方法，主线程就无需等待doBizA、doBizB的执行完成，也就是说doBizA、doBizB这两个操作已经被异步化了。

**异步化是并行方案得以实施的基础，深入来说，异步化就是利用多线程优化性能这个方案得以实施的基础。**Java在1.8版本提供了CompletableFuture支持异步编程。



### CompletableFutrue

为了体现CompletableFuture异步编程的优势，这里采用23讲中的烧水泡茶为例子，通过CompletableFuture实现。我们将整个流程分为3个任务：任务1负责洗水壶、烧开水；任务2负责洗茶壶、洗茶杯、拿茶叶；任务3负责泡茶。其中，任务3要等待任务1、任务2都执行完成后才能开始，如下所示：

```java
//任务1：洗水壶->烧开水
CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
    System.out.println("T1:洗水壶...");
    sleep(1, TimeUnit.SECONDS);

    System.out.println("T1:烧开水...");
    sleep(15, TimeUnit.SECONDS);
});

//任务2：洗茶壶->洗茶杯->拿茶叶
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
    System.out.println("T2:洗茶壶...");
    sleep(1, TimeUnit.SECONDS);

    System.out.println("T2:洗茶杯...");
    sleep(2, TimeUnit.SECONDS);

    System.out.println("T2:拿茶叶...");
    sleep(1, TimeUnit.SECONDS);
    return "龙井";
});

//任务3：任务1和任务2完成后执行：泡茶
CompletableFuture<String> f3 = f1.thenCombine(f2, (__, tf) -> {
    System.out.println("T1:拿到茶叶:" + tf);
    System.out.println("T1:泡茶...");
    return "上茶:" + tf;
});

//等待任务3执行结果
System.out.println(f3.join());

void sleep(int t, TimeUnit u) {
    try {
        u.sleep(t);
    }catch(InterruptedException e){
    	...
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

略过runAsync()、supplyAsync()、thenCombine()这三个方法，从大局上看CompletableFutrue有三个优点：

1. 无需手工维护线程，给线程分配任务的工作也不需要我们关注。
2. 语义更清晰，例如```f3 = f1.thenCombine(f2, () -> {...})```能够清晰表述“任务3需要等待任务1、任务2执行完成后才能开始”。
3. 代码更加简练且专注于1业务逻辑。



### CompletableFutrue对象的创建

CompletableFutrue对象的创建主要依赖如下4个静态方法：

```java
//使用默认线程池
static CompletableFuture<Void> runAsync(Runnable runnable)
static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)

//可以指定线程池
static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
```

上述例子中，runAsync()和supplyAsync()的区别主要在于是否存在返回值，Runnable接口的run()方法是没有返回值的，而Supplier接口的get()方法是有返回值的。

而前两个方法与后两个方法的区别在于，后两个方法可以指定线程池参数。

默认情况下，CompletableFutrue会使用公共的ForkJoinPool线程池，这个线程池默认创建线程数为CPU核数（也可以通过JVM手动设置）。如果所有的CompletableFutrue共享一个线程池，那么一旦有任务执行一些很慢的IO操作，就会导致线程池中的所有线程都阻塞在IO操作上，从而造成线程饥饿，进而影响整个系统性能。因此，必须根据不同的业务类型创建不同的线程池，以避免互相干扰。

在创建完CompletableFutrue对象之后，会异步执行runnable.run()或者supplier.get()，而对于异步的控制，CompletableFutrue实现了Future，可以通过Future接口下的方法进行控制。除此以外，CompletableFutrue实现了CompletionStage接口。



### CompletionStage 接口

任务是有时序关系的，例如串行关系、并行关系、汇聚关系等。例如烧水泡茶的例子中：

洗水壶和烧开水就是串行关系；

![img](https://static001.geekbang.org/resource/image/e1/9f/e18181998b82718da811ce5807f0ad9f.png)

洗茶壶和烧开水相对于洗茶壶和洗茶杯就是并行关系；

![img](https://static001.geekbang.org/resource/image/ea/d2/ea8e1a41a02b0104b421c58b25343bd2.png)

而烧开水、拿茶叶、泡茶就是汇聚关系。

![img](https://static001.geekbang.org/resource/image/3f/3b/3f1a5421333dd6d5c278ffd5299dc33b.png)

而CompletionStage接口可以清晰描述任务之间的这种时序关系，例如```f3 = f1.thenCombine(f2, (...) -> {})``` 描述的就是一种汇聚关系。烧水泡茶程序中的汇聚关系是一种AND聚合关系，AND指的是所有依赖的任务——烧开水和拿茶叶都完成后才能执行当前的泡茶任务。除此以外还有OR的聚合关系，只要依赖的任务有一个执行完成就能够执行当前任务。

除此以外，CompletionStage接口也能够很方便地描述异常处理。



* 描述串行关系

  CompletionStage接口中描述串行关系，主要通过thenApply、thenAccept、thenRun 和 thenCompose 这四个系列的接口：

  ```java
  //串行执行
  CompletionStage<R> thenApply(fn);
  //启动异步流程
  CompletionStage<R> thenApplyAsync(fn);
  
  CompletionStage<Void> thenAccept(consumer);
  CompletionStage<Void> thenAcceptAsync(consumer);
  
  CompletionStage<Void> thenRun(action);
  CompletionStage<Void> thenRunAsync(action);
  
  CompletionStage<R> thenCompose(fn);
  CompletionStage<R> thenComposeAsync(fn);
  ```

  thenApply能够接受参数也支持返回值；thenAccept支持参数不支持返回值；thenRun既不支持参数也不支持返回值；thenCompose会创建一个新的子流程....

  thenApply系列函数使用示例如下：

  ```java
  CompletableFuture<String> f0 = CompletableFuture.supplyAsync(() -> "Hello World").thenApply(s -> s + " QQ").thenApply(String :: toUpperCase);
  
  System.out.println(f0.join());//输出结果：HELLO WORLD QQ
  ```

  

* 描述AND汇聚关系

  ```java
  CompletionStage<R> thenCombine(other, fn);
  CompletionStage<R> thenCombineAsync(other, fn);
  CompletionStage<Void> thenAcceptBoth(other, consumer);
  CompletionStage<Void> thenAcceptBothAsync(other, consumer);
  CompletionStage<Void> runAfterBoth(other, action);
  CompletionStage<Void> runAfterBothAsync(other, action);
  ```

* 描述OR汇聚关系

  ```java
  CompletionStage applyToEither(other, fn);
  CompletionStage applyToEitherAsync(other, fn);
  CompletionStage acceptEither(other, consumer);
  CompletionStage acceptEitherAsync(other, consumer);
  CompletionStage runAfterEither(other, action);
  CompletionStage runAfterEitherAsync(other, action);
  ```

  

* 异常处理

  ```java
  CompletableFuture<Integer> f0 = CompletableFuture.supplyAsync(() -> (7/0)).thenApply(r -> r*10);
  System.out.println(f0.join());
  ```

  对于非异步编程，可以采用try-catch捕获异常，而异步编程中异常处理主要通过如下的几个方法：

  ```java
  CompletionStage exceptionally(fn);
  CompletionStage<R> whenComplete(consumer);
  CompletionStage<R> whenCompleteAsync(consumer);
  CompletionStage<R> handle(fn);
  CompletionStage<R> handleAsync(fn);
  ```

  exceptionally的使用示例如下：

  ```java
  CompletableFuture<Integer> f0 = CompletableFuture.supplyAsync(() -> 7/0)).thenApply(r -> r*10).exceptionally(e -> 0);
  System.out.println(f0.join());
  ```

  除此以外，类似finnally的处理也可以通过whenComplete()或handler()的系列方法来处理。

