### HashMap

HashMap 是 Java 类库中提供的对哈希表的实现，底层采用数组 + 链表/红黑树的形式进行数据存储。通常情况下，HashMap的存、取操作能够达到O(1)的时间复杂度，因此HashMap是键值对存取场景的首选数据结构。

HashMap高效的性能秘密在于其哈希算法，一个高效的Hash算法能够让散列值在数组上更加均匀分布，从而减少哈希碰撞的可能。你可以在HashMap的源码中发现：HashMap只允许内部容器为2的n次幂，这个可以通过 tableSizeFor() 方法以及 resize() 方法体现：

```java
public HashMap(int initialCapacity, float loadFactor) {
    //省略参数的校验逻辑..
    this.loadFactor = loadFactor;
    //计算扩容门槛
    this.threshold = tableSizeFor(initialCapacity);
}

//获取Capacity往上取最近的二进制位
static final int tableSizeFor(int cap) {
    int n = cap - 1;//-1是为了避免一个二进制数被转换为更大的二进制数
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}

//put方法核心逻辑
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    //如果table数组为空, 则调用resize()初始化table数组(lazy-load)
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    //散列值计算 p = (table.length - 1) & key.hashCode()
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    //...省略后续逻辑
}

final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        //...省略这部分逻辑, 我们只关注初始化
    }
    //当oldCap == 0, 也就是初始化阶段
    else if (oldThr > 0) 
        newCap = oldThr;//将newCap设置为oldThr值, 也就是tableSizeFor()方法返回的值
    else {              
        //...省略这部分逻辑, 我们只关注初始化
    }
    if (newThr == 0) {
        //计算新的扩容门槛 = newCap * 负载因子
        float ft = (float)newCap * loadFactor;
        //保证新扩容门槛值的正确性
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                  (int)ft : Integer.MAX_VALUE);
    }
    threshold = newThr;
    //...省略新建table数组的逻辑
    return newTab;
}
```

从上述代码中我们能够得知下面几个信息：

1. HashMap采用的是lazy-load机制，当执行 put() 操作时才会真正创建内部存储数据的数组。
2. HashMap会在resize()方法中执行创建数组、数组扩容的操作，并且创建数组时，数组的大小 = 初始设置的容量值 initializeCapacity 往上取最近的二进制位。
3. HashMap会通过```(table.length - 1) & key.hashCode()```计算散列值，而这个公式要求数组的长度必须为2的幂次位才能使结果有效散列。



### HashMap如何高效地扩容

HashMap的扩容操作与ArrayList一样，都是新建一个数组，然后将旧数组中的元素搬迁到新数组中。除此以外，HashMap还需要重新计算内部元素在新数组中的散列位置，然后才能搬迁。

你可能会认为，只需要在初始化HashMap时尽可能按需求设置一个更大初始容量的HashMap就能够解决扩容带来的问题了。虽然这个思路是正确的，但还不算完整。设想我们初始化了一个容量为1000的HashMap，这样确实在使用初期都不会造成扩容带来的性能问题。但如果后期执行一次 put() 操作导致HashMap满了（或者到达HashMap设置的扩容阈值），那么在将元素放入表中之后会涉及到一次扩容操作，而这个扩容操作需要将HashMap中已经存在的所有元素重新计算散列值，并搬迁到新数组中。因此这种场景下，这样一个 put() 操作将会非常耗时。

解决这个问题的思路也很简单，我们只需要将一次性搬迁的动作拆分为多次处理就好了。当放入元素之后，HashMap中的值到达阈值，此时我们只创建新表，并只搬迁一个元素，并且只有当有新数据插入时，才从旧的表中拿出一个数据放入新表中。也就是每插入一个新数据，就搬迁一个旧表中的数据到新表。经过多次插入操作之后，整个数据搬迁动作就完成了。这样就能够成功避免了一次性数据搬迁耗时过多的情况。这个思路可以如下图所示：

![img](https://static001.geekbang.org/resource/image/6d/cb/6d6736f986ec4b75dabc5472965fb9cb.jpg)

但经过阅读源码之后，我发现 Java 并没有这样做，而是采取一次性数据搬迁的策略。但这也不乏是一种HashMap的优化策略，作为了解即可。



### HashMap解决Hash冲突的策略

其实解决Hash冲突的思路主要有两种：线性探测法和拉链法，而 Java 中HashMap采用的是拉链法解决哈希冲突的。其具体实现比较著名：当出现Hash冲突的情况下，会在对应的桶位置拉出一个链表，并且将Hash冲突的结点插入到链表尾部，当链表长度到达8时转换为红黑树、当红黑树大小由8减为6时转换为链表。

针对红黑树你可能会存在这么一个疑问：Hash冲突这种情况毕竟是少数，而大多数情况下HashMap中的元素应该是均匀分布的，就算某个桶拉出了链表也不至于会太长。那么为什么还需要增加一个红黑树的实现呢？

其实 Java的HashMap中链表、红黑树的转换功能主要是为了预防Dos攻击。哈希冲突的情况其实是可以人为制造的，当攻击者恶意制造大量具有相同Hash的数据放入Hash表中，就会导致HashMap的链表越拉越长，同时执行一次 put() 操作的时间也随之递增，这样就会大量消耗CPU或线程资源，从而导致系统无法响应其他请求，以此实现拒绝服务攻击Dos的目的。而红黑树的存在，让恶意攻击的时间复杂度由O(n)降到了O(logN)，降低了攻击对业务造成的影响。



### HashCode的有效性

大多数情况下，我们都是采用字符串作为HashMap的key值，这是为什么呢？

在之前的文章中也提到过，String是一个具备不变性的类，这就意味着一个String实例对象的hashCode一定不会发生改变，这个性质在HashMap中是非常关键的。倘若key是一个可变对象，则很可能在 put() 操作时针对该key计算得到的散列值在不久后就失效了，从而造成针对该key的查询操作无效。**因此，尽可能将key设置为一个具备不变性的对象。**

除此以外，**“重写了equals方法就必须要重写hashCode”** 这个原则也是针对散列表规定的。

我们知道，equals方法的原始功能是比较两个对象的内存地址是否相同，并且String类将equals重写为判断两个字符串对象的内容是否一致。那么我们就假设，String重写了equals，没有重写hashCode，这会带来什么后果：

我们有如下两个字符串对象：

```java
String a = new String("whl1998");
String b = new String("whl1998");

a.equals(b) == true;
a != b;
a.hashCode() != b.hashCode();
```

此时我们将字符串对象a、b作为HashMap中的key进行存储。正常情况下，对相同key（a.equals(b)，两个对象是相同的）执行两次put操作是会将之前的value覆盖的，也就是objA会被覆盖为objB：

```java
HashMap map = new HashMap();
map.put(a, objA);
map.put(b, objB);
```

我们都知道，HashMap会在放置元素之前，根据 key.hashCode() 计算散列值，而字符串a的散列值并不等于字符串b的散列值，因此map中会存在两个 key == "whl1998" 的结点，这很显然是不符合我们预期的。这也是为什么重写 equals() 也需要重写 hashCode() 。



综上所述，HashMap比较关键的几个点如下：

1. HashMap的冲突解决方案
2. HashMap的初始化流程
3. HashMap存、取、扩容的执行流程
4. 如何避免低效的扩容操作
5. HashCode的有效性



### HashTable

HashTable是Java早期提供的线程安全的散列表实现，目前已经被ConcurrentHashMap取代。原因也很简单，HashTable的锁粒度太大从而导致了并发性能较差。你可以在HashTable的源码中看到，大多数方法都被设置为synchronized，这也就意味着HashTable采用的是对象锁解决并发问题。而HashMap是不支持线程安全的，这也是HashMap与HashTable最本质的区别。

并发编程中，优化性能的主要思路就是降低锁的粒度，而ConcurrentHashMap则是采用分段锁实现的并发安全。这篇文章主要是比较HashMap与HashTable，因此对于ConcurrentHashMap只提及它细粒度锁的特点。

除此以外，HashMap也可以通过Collections提供的API快速实现基于对象锁的并发安全实例：

```java
 Collections.synchronizedMap(new HashMap<>());
```

由此可见，HashTable真的是谁都比不过，是时候被淘汰了。



### TreeMap

TreeMap是Java提供的对红黑树的实现，若不通过Comparator指定，那么TreeMap则会以自然排序的方式对结点按key值进行排序。除此以外，TreeMap的get()、put()、remove()之类的操作都是O(logN)的时间复杂度。与HashMap之间最本质的区别，就在于内部元素是否有序。

TreeMap（红黑树）能够实现有序的秘密，就在于它是一种特殊的二叉查找树（或者说近似平衡二叉树），基于二叉查找树 “左结点小于根结点，右结点大于根结点” 的特性，红黑树也就能继承其有序的特性。除此以外，红黑树相比起严格平衡二叉树AVL性能要快不少。毕竟AVL每一次增、删操作之后都需要对树进行维护，以保证左右子树高度差不超过1，而红黑树对维护的要求则没那么严格。

红黑树要求如下几个特性以保证O(logN)时间复杂度的存取性能：

1. 根节点为黑结点
2. 不允许存在相邻的红结点
3. 叶子结点都为黑结点，且都为空
4. 从任何一个结点到其可达叶子结点的路径上都包含相同数目的黑结点

由于红黑树的性能相当稳定，因此在工程中但凡用到动态插入、删除、查找数据的场景都可以使用红黑树实现。