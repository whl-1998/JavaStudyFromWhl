### Worker Thread模式

在33讲，介绍了以“委托代办”为模型的分工模式——Thread-Per-Message模式，这种模式主要还是在支持协程的语言中使用得比较多，如果使用Java的Thread实现，频繁地创建、销毁线程会非常影响性能，还可能导致OOM，因此在Java领域中主要还是采用Worker Thread模式。

Worker Thread模式的思想就是将任务存放在阻塞队列，然后创建固定数量的线程取出阻塞队列中的任务执行。很明显，Worker Thread的实现就是线程池。

下面的代码还是以echo程序为例，通过Worker Thread模式进行实现：

```java
//创建线程池 最大线程数=500
ExecutorService es = Executors.newFixedThreadPool(500);

final ServerSocketChannel ssc = ServerSocketChannel.open().bind(new InetSocketAddress(8080));

//处理请求    
try {
    while (true) {
        // 接收请求
        SocketChannel sc = ssc.accept();
        // 将请求处理任务提交给线程池
        es.execute(() -> {
            try {
                // 读Socket
                ByteBuffer rb = ByteBuffer.allocateDirect(1024);
                sc.read(rb);
                // 模拟处理请求
                Thread.sleep(2000);
                // 写Socket
                ByteBuffer wb = (ByteBuffer)rb.flip();
                sc.write(wb);
                // 关闭Socket
                sc.close();
            } catch(Exception e) {
                throw new UncheckedIOException(e);
            }
        });
    }
} finally {
    ssc.close();
    es.shutdown();
}
```



### 正确创建线程池

Java的线程池能够避免频繁创建线程带来的开销，也能够避免无限制接受任务导致OOM，只是后者经常被忽略，例如上述代码中，创建线程池时底层的阻塞任务队列就是无界的。在工程应用中，强烈建议采用有界队列来接受任务。

其实Executors下的创建线程池方法基本上都是对ThreadPoolExecutor进行了封装，因此我们可以直接通过ThreadPoolExecutor构建线程池，这也是实际应用中正确的做法，代码如下所示：

```java

ExecutorService es = new ThreadPoolExecutor(
    //线程池保证的最少工作线程数
    50,
    //线程池创建的最大线程数
    500,
    //线程的最久闲置时间, 闲置超过60s后回收
    60L, TimeUnit.SECONDS,
    //创建有界队列, 最大容量2000
    new LinkedBlockingQueue<Runnable>(2000),
    //根据业务需求指定创建线程的名称
    r -> {
        return new Thread(r, "echo-"+ r.hashCode());
    },
    //自定义任务拒绝策略, 如果线程池中所有的线程都处于工作状态，并且workQueue也满了
    //此时再提交任务时, 任务不交给线程池执行, 而是自己执行
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```