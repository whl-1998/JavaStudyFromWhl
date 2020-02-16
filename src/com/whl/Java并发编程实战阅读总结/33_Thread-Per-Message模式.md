### Thread-Per-Message模式

假设，需求是写一个Http Server，那么只能在主线程中接收Http请求，但并不能在主线程中处理请求，如果在主线程中处理Http请求的话，那么同一时间只能处理一个请求，这样效率是非常低的。

此时可以创建一个子线程，委托子线程去处理Http请求。这种委托办理的设计模式，在并发编程领域中被总结为一种设计模式——Thread-Per-Message模式。简单来说，就是为每个线程分配一个独立的线程，委托子线程处理Http请求。



### 实现Thread-Per-Message模式

Thread-Per-Message模式的经典应用场景是网络编程里Server端的实现，Server端为每一个客户端创建一个独立的线程处理请求，当线程处理完请求后则自动销毁，这是一种最简单的并发处理网络请求的方法。

网络中最简单的程序无非是echo程序，echo程序的Server端会原封不动地将客户端的请求回发给客户端。例如，客户端发送TCP请求“Hello World”给Server端，Server端回发一个“Hello World”。

下面的代码通过利用Thread-Per-Message模式，实现echo程序Server端：

```java
final ServerSocketChannel ssc = ServerSocketChannel.open().bind(new InetSocketAddress(8080));

//处理请求    
try {
    while (true) {
        // 接收请求
        SocketChannel sc = ssc.accept();
        // 每个请求都创建一个线程
        new Thread(() -> {
            try {
                // 读取请求
                ByteBuffer rb = ByteBuffer.allocateDirect(1024);
                sc.read(rb);
                
                //模拟处理请求
                Thread.sleep(2000);
                
                // 写Socket(echo)
                ByteBuffer wb = (ByteBuffer)rb.flip();
                sc.write(wb);
                
                // 关闭Socket
                sc.close();
            } catch(Exception e) {
                throw new UncheckedIOException(e);
            }
        }).start();
    }
} finally {
    ssc.close();
}   
```

上述代码中，会为每个客户端请求创建一个线程处理echo请求回发。但这个代码存在一个很大的问题，Java中频繁创建线程是一个重量级的操作，一方面创建线程非常耗时，另一方面线程占用的内存很大。因此，上述方案并不适用于高并发的场景。

提到频繁创建线程导致的资源消耗问题，很自然地就会想到线程池。线程池这个方案思路上是没有问题的，但是引入线程池也会使代码更加复杂。



### 利用Fiber实现Thread-Per-Message模式

其实，除了线程池的方案，还有另一种方案，只是Java这种语言并不支持，那就是**协程**。协程是一种轻量级的线程，Go语言就因为这种特性，在当今的并发编程领域势如破竹。而Java当今也意识到了协程的重要性，于是在OpenJDK中就有了Loom项目专门用于解决Java的协程问题，在Loom项目中，协程被称之为Fiber。利用Fiber实现echo服务的示例代码如下所示：

```java

final ServerSocketChannel ssc = ServerSocketChannel.open().bind(new InetSocketAddress(8080));

//处理请求
try {
    while (true) {
        // 接收请求
        final SocketChannel sc = serverSocketChannel.accept();
        
        Fiber.schedule(() -> {
            try {
                // 读取请求
                ByteBuffer rb = ByteBuffer.allocateDirect(1024);
                sc.read(rb);
                
                // 模拟处理请求
                LockSupport.parkNanos(2000*1000000);
                
                // 写Socket(echo)
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
}
```

可以发现，对比Thread的实现，Fiber方案的代码改动非常少，仅是把new Thread(...).start() 改为 Fiber.schedule(...)即可。