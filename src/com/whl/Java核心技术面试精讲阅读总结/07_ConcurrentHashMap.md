### ConcurrentHashMap

ConcurrentHashMap是位于java.util.concurrent包下的高并发HashMap实现，在JDK1.7中采用分段锁实现，内部使用了分段数组Segment + 链表的数据结构，且每一把锁只锁容器中的一部分数据，若多个线程访问的是不同的Segment也就不存在锁竞争。

在JDK1.8之后，ConcurrentHashMap采用了数组 + 链表/红黑树的实现，通过CAS + synchronized作为主要同步手段，以数组中的桶为单位进行并发控制，锁的粒度更加精细。

ConcurrentHashMap总体结构上与HashMap非常相似，默认初始容量为16，并且也采用了相同的散列值计算方式，扩容方式也是通过创建新数组，且大小为原数组的两倍。



### ConcurrentHashMap的put()操作

由于ConcurrentHashMap比较难制造Hash碰撞的场景，因此对于部分操作的源码没有采用Debug，而是硬着头皮看了...因此可能某些地方会有误差。



下面开始源码的分析：

```java
final V putVal(K key, V value, boolean onlyIfAbsent) {
    //对参数进行非法校验
    if (key == null || value == null) throw new NullPointerException();
    //计算hash值
    int hash = spread(key.hashCode());
    int binCount = 0;
    //自旋操作, 在完成put操作后break
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; 
        int n, i, fh;
        //若数组为空, 则先执行初始化操作, 初始化结束后会自旋回来继续put操作
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        //若数组中, 对应散列值位置上的桶为空, 则以CAS的方式进行put, 若成功put则退出自旋
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null)))
                break;
        }
        //若对应散列位置上的桶中有元素, 且第一个结点的hash值 == Moved, 则让当前线程帮忙执行数据搬迁操作
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        //若对应散列位置上的桶中有元素
        else {
            V oldVal = null;
            //对桶上的头结点加锁, 注意是头结点, 而不是整个桶
            synchronized (f) {
                //再次检测头结点是否发生了变化, 因为加锁前可能有其他线程执行了更新动作
                //若发生了变化, 则自旋进行下一次put()操作
                if (tabAt(tab, i) == f) {
                    //若头结点的hash值 >= 0, 说明不需要帮忙迁移
                    //加锁前有可能其他线程执行了某些操作导致当前线程需要帮忙进行数据搬迁, 这里是二次验证
                    if (fh >= 0) {
                        binCount = 1;//用于记录桶中结点数
                        //遍历整个桶, 每遍历一次binCount+1
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
                            //若遍历到了相同key值的结点, 则替换value
                            if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;//完成put操作, 退出自旋
                            }
                            Node<K,V> pred = e;
                            //若遍历到链表尾部还未找到相同key的结点, 那么则在尾部追加新结点
                            if ((e = e.next) == null) {
                                pred.next = new Node<K,V>(hash, key,
                                                          value, null);
                                break;//完成put操作, 退出自旋
                            }
                        }
                    }
                    else if (f instanceof TreeBin) {
                        //..省略红黑树的逻辑
                    }
                }
            }
            if (binCount != 0) {
                //判断执行完put操作后是否需要树化
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                //若旧值不为空, 说明是更新操作, 方法执行结束返回旧值
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
    //成功插入元素后, map元素个数+1, 并检查是否需要扩容
    addCount(1L, binCount);
    //若是插入操作, 则返回null
    return null;
}
```

从ConcurrentHashMap的 put() 执行逻辑来看，重要的点有如下几个：

1. 当散列位置上的桶上没有结点，则以CAS + 自旋的方式进行 put() 操作

2. 当散列位置上的桶上有结点，则对桶上第一个结点加锁（注意加锁的是结点，而不是链表），执行 put() 操作
3. ConcurrentHashMap也涉及到了红黑树与链表之间的转换操作

可见，ConcurrentHashMap的 put() 逻辑相比起HashMap的，只是在原有的基础上增加了一些并发控制，大概思路还是基本一致的。



### ConcurrentHashMap的initTable()操作

在具体分析 initTable() 操作的源码之前，我们需要了解 sizeCtl 这个属性。它是ConcurrentHashMap中的一个volatile属性，用于Map扩容时的信号标识。当值为 -1 表示该map正在初始化，当值为 -n 表示有 n-1个线程正在进行扩容操作（当执行put()操作的线程发现map正在被其他线程扩容时，会调用该线程一起执行扩容操作），当值为 0 表示第一次初始化，当值为 n 表示扩容门槛，若map中元素总量到达扩容门槛时会触发扩容操作。

下面是源码分析：

```java
private final Node<K,V>[] initTable() {
    Node<K,V>[] tab; 
    int sc;
    //当数组为空时, 自旋执行下面的数组初始化操作
    while ((tab = table) == null || tab.length == 0) {
        //若sizeCtl < 0, 说明其他线程正在初始化map或正在扩容, 当前线程让出CPU给其他线程执行
        if ((sc = sizeCtl) < 0)
            Thread.yield();
        //调用Unsafe提供的CAS更新操作, 将当前sizeCtl修改为-1, 表示该map正在初始化
        else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
            try {
                //初始化数组的逻辑
                if ((tab = table) == null || tab.length == 0) {
					//sc若大于0, 则扩容门槛为sc, 否则采用默认容量作为扩容门槛
                    int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                    @SuppressWarnings("unchecked")
                    Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                    table = tab = nt;
                    //设置sc = 当前数组长度的0.75倍
                    sc = n - (n >>> 2);
                }
            } finally {
				//将本地变量sc回写到sizeCtl(扩容门槛)
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```

initTable() 这个方法逻辑比较简单，比较值得关注的就是sizeCtl这个参数以及自旋更新这个操作。



其实总结ConcurrentHashMap，重要的点无外乎如下几个方面：

1. ConcurrentHashMap之所以效率这么高是因为它采用了更细粒度的锁，以及大部分场景采用的是无锁并发操作。
2. JDK1.7中的Segment实现我没看过源码所以也不太了解，只是在1.8版本中，ConcurrentHashMap还保留了一些Segment定义以保证序列化时的兼容性。
3. 同步逻辑上，使用的是synchronized而不是ReenTrantLock，这是因为synchronized相比起ReenTrantLock内存消耗更小，而且高版本中synchronized的性能也进行了相应的优化，并不比ReenTrantLock差。

我觉得要了解ConcurrentHashMap，敲门砖是Unsafe提供的CAS机制。把CAS弄懂了，ConcurrentHashMap的源码也就不是那么晦涩了。

