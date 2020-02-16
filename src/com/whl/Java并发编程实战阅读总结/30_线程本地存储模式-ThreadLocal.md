### ThreadLocal

当多个线程同时读写同一共享变量时存在并发问题，那么除了不变模式（只读）以外，还可以通过“避免共享”来解决并发问题。既然多个线程操作同一个共享变量会出问题，那么给每个线程都分发一个变量，彼此之间不共享，也就不存在并发问题了。

线程本地存储模式采用的是“避免共享”的思路。如果需要在并发场景下使用一个线程不安全的工具类，一种方案是将这个工具类作为局部变量使用，缺点是在高并发场景下会频繁的创建对象；另一种方案就是使用ThreadLocal，该方案只需要创建一个工具类实例，不存在频繁创建对象的问题。两种方案孰优孰劣，非常清楚明了

针对上述思想的实现，就是Java提供的ThreadLocal。ThreadLocal的使用示例如下所示：

```java
static class ThreadId {
    static final AtomicLong nextId = new AtomicLong(0);
    
    //定义ThreadLocal变量
    static final ThreadLocal<Long> tl = ThreadLocal.withInitial(() -> nextId.getAndIncrement());
    
    //此方法会为每个线程分配一个唯一的Id
    static long get() {
        return tl.get();
    }
}
```

上述静态类ThreadId会给每个线程分配一个唯一的线程Id，如果一个线程调用两次get()，获取的返回值是相同的。但如果两个线程分别调用ThreadId的get()方法，获取的返回值却是不同的。这样的实现很眼熟，有一种key - value的既视感，实际上ThreadLocal底层也确实利用了Map来实现，key是线程，value是线程拥有的变量。原理会在后续再详细分析。

SimpleDateFormat不是线程安全的，如果需要在并发场景使用它，其中一个方案就是用ThreadLocal解决，下面代码就是具体实现：

```java
static class SafeDateFormat {
    //定义ThreadLocal变量
    static final ThreadLocal<DateFormat> tl = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    static DateFormat get() {
        return tl.get();
    }
}

//不同线程执行下面代码返回的df是不同的
DateFormat df = SafeDateFormat.get();
```

可以发现，不同线程调用SafeDateFormat的get()方法将返回不同的SimpleDateFormat对象实例，由于不同的线程不共享SimpleDateFormat，因此是线程安全的。



### ThreadLocal工作原理

ThreadLocal内部持有一个map，key是线程，value是对应线程持有的变量，类似实现的代码如下所示：

```java
class MyThreadLocal<T> {
    Map<Thread, T> locals = new ConcurrentHashMap<>();
    
    //获取线程变量
    T get() {
        return locals.get(Thread.currentThread());
    }
    
    //设置线程变量
    void set(T t) {
        locals.put(Thread.currentThread(), t);
    }
}
```

上述代码只是实现思路，在Java中的ThreadLocal中的Map并不是ConcurrentHashMap，而是ThreadLocalMap。并且持有ThreadLocalMap的并不是ThreadLocal，而是Thread。示例如下列精简后的代码所示：

```java
class Thread {
    //内部持有ThreadLocalMap
    ThreadLocal.ThreadLocalMap threadLocals;
	...
}

class ThreadLocal<T> {
    //获取调用get()的线程持有的变量
    public T get() {
        //首先获取当前线程持有的ThreadLocalMap
        ThreadLocalMap map = Thread.currentThread().threadLocals;
        //在ThreadLocalMap中查找变量
        Entry e = map.getEntry(this);
        return e.value;
    }
    
    static class ThreadLocalMap{
        //内部是数组而不是Map
        Entry[] table;
        
        //根据ThreadLocal查找Entry
        Entry getEntry(ThreadLocal key){
            //省略查找逻辑
            ...
        }
        
        //Entry定义
        static class Entry extends WeakReference<ThreadLocal> {
            Object value;
        }
    }
}
```

可以发现，线程的本地变量并没有放在ThreadLocal中，而是放在了Thread中，ThreadLocal只是充当了一个代理的角色。这样设计的原因也很容易理解，线程的持有的本地变量就应该是属于这个线程对象的，从数据亲缘性上看，ThreadLocalMap相比起ThreadLocal，属于Thread也更合理。

除此以外，ThreadLocalMap放在Thread中还有一个更加深层次的原因，那就是不容易产生内存泄漏。在 MyThreadLocal 这个模拟实现的代码中，Map属于ThreadLocal，并且Map持有者Thread对象的引用。这就意味着，只要ThreadLocal对象存在，那么Map中的Thread对象就永远都不会被垃圾回收，而ThreadLocal的生命周期往往比线程更长，所以这种方案很容易导致内存泄露。

而Java中的Thread持有ThreadLocalMap，并且ThreadLocalMap中对ThreadLocal的引用还是弱引用WeakReference，这意味着只要Thread对象被回收了，ThreadLocalMap就能够被回收。





### ThreadLocal与内存泄漏

Java中的ThreadLocal实现已经是非常深思熟虑的了，但还是不能够百分之百避免内存泄漏，例如在线程池中使用ThreadLocal，就很容易导致内存泄漏。这是因为线程池中的线程存活时间很长，往往是和程序共生死的，这就意味着Thread持有的ThreadLocalMap一直都不会被回收。再者，ThreadLocalMap中的Entry是对ThreadLocal弱引用的，源码如下所示：

```java
static class ThreadLocalMap {
    static class Entry extends WeakReference<ThreadLocal<?>> {
        Object value;

        Entry(ThreadLocal<?> k, Object v) {
            super(k);
            value = v;
        }
    }
}
```

这就意味着，只要Entry中的key——ThreadLocal结束了自己的生命周期，那么就可以被回收。但是Entry中的value却是被Entry强引用的，即便value的生命周期结束了，也无法被回收，从而导致内存泄漏。

既然无法被自动回收，那么只好手动回收了，提到手动释放内存，就能够马上想到try - finally：

```java
ExecutorService es;
ThreadLocal tl;

//线程池提交任务
es.execute(() -> {
    //ThreadLocal增加变量
    tl.set(obj);
    
    try {
        // 省略业务逻辑代码
    }finally {
        //手动清理ThreadLocal 
        tl.remove();
    }
});
```





### InheritableThreadLocal与继承性

通过ThreadLocal创建的线程本地变量，其子线程是无法继承的。那么如果需要子线程继承父线程的本地变量，就需要InheritableThreadLocal支持这种特性，InheritableThreadLocal是ThreadLocal的子类，用法与ThreadLocal相同。但最好不要在线程池中使用InheritableThreadLocal，因为线程池中创建的线程是动态的，很容易导致继承关系错乱，从而导致业务逻辑计算错误，这是比内存泄露更加致命的。





