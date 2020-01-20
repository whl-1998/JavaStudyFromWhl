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
	//若key或value为空, 则抛出空指针异常
	if (key == null || value == null) throw new NullPointerException();
	//计算hash值
	int hash = spread(key.hashCode());
	//用于记录插入元素所在桶的节点个数
	int binCount = 0;
	//死循环, 结合CAS进行自旋操作, 若CAS操作失败, 则会重新获取桶进行下面的流程
	for (Node<K,V>[] tab = table;;) {
		Node<K,V> f; 
		int n, i, fh;
		//若桶未被初始化或者桶的长度为0, 则初始化桶
		if (tab == null || (n = tab.length) == 0)
			tab = initTable();
		//若要put的节点所在桶还没有元素, 则把这个节点插入到这个桶
		else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
			//使用CAS插入元素, 如果插入失败, 则自旋重新插入
			if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null)))
				//如果插入成功, 结束本次循环
				break;// no lock when adding to empty bin
		}
		//若要put的节点所在桶的第一个元素的hash值是MOVED, 则让当前线程帮忙迁移元素
		else if ((fh = f.hash) == MOVED)
			tab = helpTransfer(tab, f);
		else {
			V oldVal = null;
			//若这个桶不为空且不存在需要迁移的元素, 则锁住这个桶(分段锁)
			synchronized (f) {
				//再次检测桶的第一个元素是否有变化, 如果变化则进行下一次自旋
				if (tabAt(tab, i) == f) {
					//若桶中头节点的hash值大于0, 说明不需要迁移, 也不是树节点
					if (fh >= 0) {
						//桶中元素个数赋值为1
						binCount = 1;
						//遍历整个桶, 每次结束后binCount++
						for (Node<K,V> e = f;; ++binCount) {
							K ek;
							//遍历桶找到了相同key值的节点, 那么替换相同key节点的value值
							if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
								//当put为更新操作时, 记录旧值
								oldVal = e.val;
								//若找到了相同key值的节点, 进行更新操作
								if (!onlyIfAbsent)
									e.val = value;
								//成功更新后break
								break;
							}
							Node<K,V> pred = e;
							//若遍历到链表尾部还没有找到相同key的节点, 那么就插入到链表结尾并break
							if ((e = e.next) == null) {
								pred.next = new Node<K,V>(hash, key, value, null);
								break;
							}
						}
					}
					//若桶中第一个节点是树节点
					else if (f instanceof TreeBin) {
						Node<K,V> p;
						binCount = 2;
						//则调用红黑树的插入方式插入newNode
						if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key, value)) != null) {
							oldVal = p.val;
							//若找到了相同key节点, 则赋予新值
							if (!onlyIfAbsent)
								p.val = value;
						}
					}
				}
			}
			//若binCount不为0, 说明成功插入或更新
			if (binCount != 0) {
				//判断插入后是否需要树化
				if (binCount >= TREEIFY_THRESHOLD)
					treeifyBin(tab, i);
				//若oldValue不为空的情况下, 则表示为更新操作, 返回旧值
				if (oldVal != null)
					return oldVal;
				//结束自旋
				break;
			}
		}
	}
	//成功插入, map元素个数+1, 并检查是否需要扩容
	addCount(1L, binCount);
	//若插入元素则返回null
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