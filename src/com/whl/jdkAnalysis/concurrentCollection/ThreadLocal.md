# ThreadLocal简介

在并发编程中，通常采用synchronized或者ReenTrantLock.lock()避免线程对共享资源操作造成的线程安全问题，但由于对共享资源加锁会使得没有获取到锁的线程进入阻塞状态，而进入阻塞状态的线程可能自旋消耗CPU，也可能会进行一次上下文切换。总的来说，对共享资源加锁多少都会对性能造成一定的影响。那么有没有办法，即不需要通过对共享资源加锁，也能够保证线程安全呢？答案使肯定的，我们可以通过“空间换时间”的策略，让每个线程都持有一个只属于自己的“共享资源”，各用各的。这种方式的优点是：同步操作相比起加锁更快；缺点是：内存消耗比起加锁更多。

## ThreadLocal核心方法

### void set(T value)

```java
public void set(T value) {
    //获取到当前线程实例
    Thread t = Thread.currentThread();
    //通过当前线程实例获取到ThreadLocalMap对象
    ThreadLocalMap map = getMap(t);
    if (map != null)
        //如果map不为空, 则在map中设置当前threadLocal实例为key, 值为value
        map.set(this, value);
    else
        //map如果为空, 则新建map并存入value
        createMap(t, value);
}
```

可以看到，value是存放在ThreadLocalMap这个容器中的，并且是以key为当前ThreadLocal的实例，value为传入的value进行存放的。

#### ThreadLocal getMap(Thread t)

该方法就是返回属于线程 t 的threadLocals属性。也就是说每一个线程都具备一个自己独有的ThreadLocalMap用于存放该线程独有变量，它被指名为threadLocals，并且初始值为空，需要手动添加。

```java
ThreadLocalMap getMap(Thread t) {
    return t.threadLocals;
}
```

#### void createMap(Thread t, T firstValue)

该方法就是创建一个ThreadLocalMap的实例，以当前ThreadLocal实例作为key，value为传入value值存放到ThreadLocalMap中，然后将当前线程的threadLocals赋值为创建的这个ThreadLocalMap。这就是一个手动添加threadLocals的过程。

```java
void createMap(Thread t, T firstValue) {
    t.threadLocals = new ThreadLocalMap(this, firstValue);
}
```

因此ThreadLocal.set()方法其实本质就是把我们需要set的value放入Thread.threadLocals这个属性中，如果threadLocals初始为空，则以当前ThreadLocal为key，value即为传入value新建ThreadLocalMap。如果threadLocals不为空，则以当前ThreadLocal为key，value即为传入value添加值。

### T get()

获取当前线程中的threadLocal变量值。

```java
public T get() {
    //获取到当前线程实例
    Thread t = Thread.currentThread();
    //获取map
    ThreadLocalMap map = getMap(t);
    //如果map不为空
    if (map != null) {
        //则获取到key为当前ThreadLocal实例的Entry
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            //如果Entry不为空
            @SuppressWarnings("unchecked")
            //则获取到Entry的value并返回
            T result = (T)e.value;
            return result;
        }
    }
    //若map为空或者entry为空的情况下, 就调用这个方法初始化并返回值
    return setInitialValue();
}
```

#### T setInitialValue()

这个方法的逻辑和set()方法基本一致。默认情况下通过initialValue方法获取到的value值为null，然后根据set()方法的逻辑新建ThreadLocalMap，key为当前ThreadLocal实例，value为null。值的注意的是intialValue()这个方法，当某个类继承自ThreadLocal并重写这个initialValue()值后，就可以默认设置初始化ThreadLocalMap时的初始值了。

```java
private T setInitialValue() {
    T value = initialValue();
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
    return value;
}
```

### void remove()

删除操作很简单，也就是获取到当前线程的threadLocalMap，如果不为空的话调用map.remove()移除掉key为当前ThreadLocal的Entry。

```java
public void remove() {
    ThreadLocalMap m = getMap(Thread.currentThread());
    if (m != null)
        m.remove(this);
}
```