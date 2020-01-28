### 读写锁

如果只需要利用互斥解决并发问题，理论上只需要通过管程或信号量就能够实现了。但Java为了应对不同场景的优化和易用性，提供了各种各样的工具类。而针对“读多写少”这种并发场景，JUC包下就提供了读写锁ReadWriteLock。

无论是Java还是mysql，读写锁都遵守下面三条原则：

1. 允许多个线程同时读取共享变量
2. 只允许一个线程写共享变量
3. 如果一个线程正在执行写操作，此时禁止读取共享变量

读写锁与互斥锁的区别在于：读写锁允许多个线程同时读取共享变量，而互斥锁不允许，因此在读多写少的场景下，读写锁性能优于互斥锁。

### 通过ReadWriteLock实现通用缓存工具类

我们通过HashMap存储缓存数据，采用ReenTrantReadWriteLock实现读写锁保证线程安全。

```java
class Cache<K,V> {
    final Map<K, V> m = new HashMap<>();
    final ReadWriteLock rwl = new ReentrantReadWriteLock();
    // 读锁
    final Lock r = rwl.readLock();
    // 写锁
    final Lock w = rwl.writeLock();
    // 读缓存
    V get(K key) {
        r.lock();
        try {
            return m.get(key);
        } finally {
            r.unlock();
        }
    }
    // 写缓存
    V put(K key, V value) {
        w.lock();
        try {
            return m.put(key, v);
        } finally {
            w.unlock();
        }
    }
}
```

缓存中数据的加载方式有两种：

1. 如果数据量不大，可以采取将数据一次性全部加载到缓存的策略。
2. 如果数据量大，可以采取lazy-load的方式，在使用时查询缓存，如果发现缓存不存在，再把相关数据加载进缓存。

针对策略2，我们对上述的代码改进实现按需加载的缓存。假设缓存的源头是数据库，如果缓存中没有目标对象，那么就需要先从数据库中加载，写入缓存。

```java

class Cache<K,V> {
    final Map<K, V> m = new HashMap<>();
    final ReadWriteLock rwl = new ReentrantReadWriteLock();
    final Lock r = rwl.readLock();
    final Lock w = rwl.writeLock();

    V get(K key) {
        V v = null;
        //读缓存
        r.lock();
        try {
            v = m.get(key);
        } finally{
            r.unlock();
        }
        //缓存中存在, 则直接返回
        if(v != null) {
            return v;
        }  
        //缓存中不存在，查询数据库
        w.lock();
        try {
            //再次验证
            //其他线程可能已经查询过数据库
            v = m.get(key);
            if(v == null){
                //查询数据库
                v = ...;//省略从数据库中获取值的过程
                m.put(key, v);//存入缓存
            }
        } finally{
            w.unlock();
        }
        return v; 
    }
}
```

上述代码中，在查询数据库操作前进行了一次二次验证，这是因为有可能其他线程已经先一步将数据写入到缓存了，为了避免不必要的数据库查询操作，因此采用了二次验证。



### 读写锁的升降级

需要注意的是ReadWriteLock并不支持锁升级，说白了在持有读锁的期间是不允许再持有写锁的。如果读锁未释放，尝试获取写锁会导致写锁永久等待，导致相关的线程都被阻塞，且永远没有机会被唤醒，如下面代码所示：

```java
//获取读锁
r.lock();
try {
    v = m.get(key);
    //如果缓存中不存在, 尝试从数据库中检索值, 并添加到缓存
    if (v == null) {
        //获取写锁
        w.lock();
        try {
            //再次验证并更新缓存
            //省略详细代码
            ...
        } finally{
            w.unlock();
        }
    }
} finally {
    r.unlock();
}
```

虽然锁升级不允许，但是降级是被允许的，例如先获取写锁再尝试获取读锁。

读写锁类似ReenTrantLock，也支持公平锁，并且也支持tryLock()、lockInterruptibly()等方法，但需要注意的是读锁不支持条件变量，在调用newCondition()时会抛出UnsupportedOperationException异常，只有写锁支持条件变量。

