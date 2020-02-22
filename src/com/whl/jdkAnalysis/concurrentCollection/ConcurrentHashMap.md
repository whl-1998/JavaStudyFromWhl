# ConcurrentHashMap
ConcurrentHashMap是HashMap的线程安全版本，在JDK1.7时采用分段数组Segment+链表实现，每一把锁只锁容器中的其中一部分数据，若多个线程访问的是不同的Segment，也就不存在锁竞争；在JDK1.8之后采用了数组+链表/红黑树实现，通过CAS+synchronized实现并发控制，以桶数组的节点数为单位，锁的粒度更加精细。  

在JDK1.8版本，ConcurrentHashMap总体结构上与HashMap非常相似，其内部仍然存在Segment定义，但仅仅是为了保证序列化时的兼容性，不再有任何结构上的用处。因为不再使用Segment，初始化操作则与HashMap相同，即Lazy-load机制，这样可以有效避免初始开销。数据存储通过volatile和CAS操作保证线程安全。  

### sizeCtl
sizeCtl是ConcurrentHashMap的一个volatile属性, 用于Map扩容时信号标识量, 当其值为-1表示正在初始化, 当值为-n表示有n-1个线程正在进行扩容操作, 当值为0表示第一次初始化, 当值为n表示扩容门槛, 若map的size到达扩容门槛时会执行扩容操作, 默认会扩容为原长度的2倍。

#### put

（1）若桶数组未初始化，则初始化
（2）若待插入节点所在的桶为空，则自旋插入newNode
（3）若map正在扩容，则当前线程也加入到扩容过程
（4）若待插入节点所在的桶不为空，且不存在迁移元素，则对该桶加锁，执行并发安全的put操作
（5）若该桶的头节点是链表节点，那么遍历链表执行插入或更新操作
（6）若该桶的头节点是树节点，那么遍历红黑树执行插入或更新操作
（7）在插入或更新完成后，观察是否需要对桶进行树化
（8）成功更新，return oldVal
（9）成功插入，map元素个数+1，并检查是否需要扩容

```java
public V put(K key, V value) {
	return putVal(key, value, false);
}

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

### initTable() 初始化桶数组

（1）使用volatile字段sizeCtl+CAS操作保证当前只有一个线程初始化桶数组
（2）sizeCtl在初始化操作后存储为扩容门槛
（3）扩容门槛写死为桶数组大小的0.75倍
```java
/**
 * Initializes table, using the size recorded in sizeCtl.
 */
private final Node<K,V>[] initTable() {
	Node<K,V>[] tab; 
	int sc;
	while ((tab = table) == null || tab.length == 0) {
		//若sizeCtl小于0, 说明该map正在进行初始化或扩容
		if ((sc = sizeCtl) < 0)
			//若sizeCtl小于-1, 说明存在多个线程在初始化或扩容, 那么当前线程调用yield让出cpu
			//若sizeCtl等于-1, 说明只有当前线程正在自旋初始化, 当前线程线程调用yield后还是由当前线程继续执行扩容操作。 
			Thread.yield(); // lost initialization race; just spin
		//Unsafe.CAS操作, 参数分别为：this对象、SIZECTL的偏移量、 更新时偏移量处的预期值、更新的值
		//若sizeCtl原子更新为-1成功, 告知其他线程当前线程进入初始化; 若原子更新失败, 则自旋直到table.length!=0
		else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
			try {
				//检查table是否为空
				if ((tab = table) == null || tab.length == 0) {
					//若sc == 0, 则代表是第一次初始化, 使用默认值16进行初始化
					int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
					//新建Node数组
					@SuppressWarnings("unchecked")
					Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
					//赋值给table桶数组
					table = tab = nt;
					//设置sc = 数组长度的0.75倍					
					sc = n - (n >>> 2);
				}
			} finally {
				//将sc赋值给sizeCtl, 这里存储的是扩容门槛
				sizeCtl = sc;
			}
			break;
		}
	}
	return tab;
}
```

### HashMap、HashTable、ConcurrentHashMap三者区别
1. HashMap线程不安全, 底层实现为数组+链表+红黑树
2. HashTable线程安全, 底层实现为数组+链表, 采用全局锁
3. ConcurrentHashMap, 底层实现为数组+链表+红黑树, 采用分段锁, 锁的是位桶数组的每一个桶, 以此对锁进行细粒度化, 在更新时采用自旋CAS操作+synchronized, 性能高效。