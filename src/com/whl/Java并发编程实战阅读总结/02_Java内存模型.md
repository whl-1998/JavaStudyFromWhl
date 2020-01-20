### Java内存模型

导致可见性问题的源头是缓存，导致有序性问题的源头是指令重排，那么解决可见性、有序性问题最直接的办法就是禁用缓存和指令重排，但是如果直接禁用缓存和指令重排问题是解决了，但程序的性能却得不到保障。

因此我们需要根据Java内存模型的规范按照需求禁用缓存和指令重排，具体就是根据volatile、synchronized、final三个关键字和6项Happens-Before原则进行的。

### Happens-Before原则

这个原则要表达的核心思想是：**前面的操作结果对后续的操作是可见的**。Happens-Before约束了编译器优化的行为，虽然允许优化，但是要保证优化后一定遵循Happens-Before原则。下面通过一段示例代码对这6个规则进行说明：

```java
class VolatileExample { 
    int x = 0; 
    volatile boolean v = false; 
    
    public void writer() { 
        x = 42; 
        v = true; 
    } 
    
    public void reader() { 
        if (v == true) {
            // 这里 x 会是多少呢？ 
        } 
    }
}
```

##### 1. 程序的顺序性规则

在一个线程中，按照程序的顺序，前面的操作Happens-Before后续的操作。例如上述代码writer()方法中，x = 42的写操作 Happens-Before v = true 的写操作。

##### 2. volatile变量规则

对一个volatile变量的写操作Happens-Before对这个volatile变量的读操作。例如上述代码中，writer()方法中 v = true 的写操作 Happens-Before reader()方法中对 v 的读操作。

##### 3. 传递性规则

如果A Happens-Before B；B Happens-Before C，那么可以认定A Happens-Before C，例如上述代码中，x = 42 的写操作 Happens-Before v = 42 的写操作，v = true 的写操作 Happens-Before v的读操作，那么 x =  42 的写操作 Happens-Before v的读操作，也就是说 x = 42 的写操作对于 v 的读操作也保证可见。

##### 4. 管程中锁的规则

某个线程A释放共享资源的锁 Happens-Before 后续线程B对这个共享资源的加锁。例如下面代码中，假设线程A在进入同步块之前x = 10，当线程A执行完毕释放锁后x = 12，而线程B在进入代码块时，能够看到 x 已经被赋值为12。

```java
synchronized (this) {  
    if (this.x < 12) { 
        this.x = 12; 
    } 
}
```

##### 5. 线程start()规则

当主线程A启动子线程B后，子线程B能够看到主线程A在启动子线程B之前的所有操作。例如下面代码中，当执行B.start() 启动了线程B之后，对于线程B是可以看到位于start()之前 var = 77这个赋值动作的。

```java
Thread threadB = new Thread(() -> { 
    // 主线程调用threadB.start()之前, 所有对共享变量的修改，此处皆可见 
    // 此例中, var==77
});
// 此处对共享变量var修改
var = 77;
threadB.start();
```

##### 6. 线程join()规则

在线程A中调用线程B的join()方法并成功返回，那么线程B对共享变量的操作 Happens-Before 于线程A后续对共享变量的操作。说白了，线程B在join()成功后，对共享变量值的修改对于线程A是可见的，例如下面代码中，threadB.join()执行后 var = 66 这个操作对于线程A是可见的。

```java
Thread threadB = new Thread(() -> { 
    // 此处对共享变量var修改 
    var = 66;
});
B.start();
B.join();
// 子线程所有对共享变量的修改, 在主线程调用B.join()之后皆可见
// 此例中，var==66
```