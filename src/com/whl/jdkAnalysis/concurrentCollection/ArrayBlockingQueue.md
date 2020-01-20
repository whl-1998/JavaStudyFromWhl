# ArrayBlockingQueue
ArrayBlockingQueue是juc包下以数组实现的线程安全的阻塞队列。

### 属性
```java
//使用数组存储元素
final Object[] items;

//读取元素的指针
int takeIndex;

//放入元素的指针
int putIndex;

//队列中的元素数量
int count;

//使用ReenTrantLock保证并发安全
final ReentrantLock lock;

//非空条件
private final Condition notEmpty;

//非满条件
private final Condition notFull;
```

### 构造方法
在创建时必须指定队列大小，并且可以通过指定可重入锁是否公平。
```java
/**
 * 1. 通过指定容量创建队列
 */
public ArrayBlockingQueue(int capacity) {
	this(capacity, false);
}

/**
 * 2. 通过指定容量、可重入锁是否公平创建队列
 */
public ArrayBlockingQueue(int capacity, boolean fair) {
	if (capacity <= 0)
		throw new IllegalArgumentException();
	this.items = new Object[capacity];
	//初始化可重入锁以及两个条件
	lock = new ReentrantLock(fair);
	notEmpty = lock.newCondition();
	notFull =  lock.newCondition();
}
```

### 入队
入队存在四个方法，分别是：offer(E e)、put(E e)、add(E e)、offer(E e, long timeout, TimeUnit unit)  

#### add(E e)
```java
//ArrayBlockingQueue.add(E e)
public boolean add(E e) {
	//调用AbstractQueue的add方法
	return super.add(e);
}

//AbstractQueue.add(E e)
public boolean add(E e) {
	//调用offer(E e)方法, 执行成功return true, else抛出队列已满的异常
	if (offer(e))
		return true;
	else
		throw new IllegalStateException("Queue full");
}
```

#### offer(E e)
```java
public boolean offer(E e) {
	//元素不允许为空
	checkNotNull(e);
	//加锁
	final ReentrantLock lock = this.lock;
	lock.lock();
	try {
		//若数组已满return false
		if (count == items.length)
			return false;
		else {
			//若数组未满调用入队方法enqueue
			enqueue(e);
			return true;
		}
	} finally {
		//释放锁
		lock.unlock();
	}
}

private void enqueue(E x) {
	//获取队列存储元素的数组
	final Object[] items = this.items;
	//在入队指针位置赋值添加元素x
	items[putIndex] = x;
	//若入队指针指向数组下标位置+1处, 说明队列已满
	if (++putIndex == items.length)
		//入队指针置为0
		putIndex = 0;
	//队列元素个数++
	count++;
	//唤醒notEmpty的条件, 使得正在阻塞且执行删除操作的线程得到被唤醒的信号
	notEmpty.signal();
}
```

#### put(E e)
```java
public void put(E e) throws InterruptedException {
	//入队元素不能为null
	checkNotNull(e);
	//加锁
	final ReentrantLock lock = this.lock;
	lock.lockInterruptibly();
	try {
		//如果队列已满, 则阻塞当前线程等待, 等到不满的时候唤醒当前线程
		while (count == items.length)
			notFull.await();
		enqueue(e);
	} finally {
		lock.unlock();
	}
}
```
下面是一段测试样例，第一个线程执行到put(3)的时候会一直阻塞，直到1秒后第二个线程执行poll()后，第一个线程被唤醒执行剩下的put(3)操作。
```java
public class Test{
    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<Integer> arr = new ArrayBlockingQueue<>(2);
        new Thread(() -> {
            try {
                arr.put(1);
                arr.put(2);
                arr.put(3);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Thread.sleep(1000);
        new Thread(() -> {
            arr.poll();
        }).start();
    }
}
```

#### offer(E e, long timeout, TimeUnit unit)
```java
public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
	//入队元素不能为null
	checkNotNull(e);
	long nanos = unit.toNanos(timeout);
	//加锁
	final ReentrantLock lock = this.lock;
	lock.lockInterruptibly();
	try {
		//如果队列满了, 就阻塞nanos纳秒
		//如果在nanos纳秒后唤醒这个线程后，依然没有空间则返回false
		while (count == items.length) {
			if (nanos <= 0)
				return false;
			nanos = notFull.awaitNanos(nanos);
		}
		//入队
		enqueue(e);
		return true;
	} finally {
		lock.unlock();
	}
}
```
四者的区别在于：  
（1）add(E e)时若队列满则抛出异常
（2）offer(E e)时若队列满了则返回false
（3）put(E e)时如果队列已满, 则阻塞当前线程等待, 等到不满的时候唤醒当前线程
（4）offer(E e, long timeout, TimeUnit unit)时若队列满了则等待一段时间, 若时间过了队列依然满就返回false
（5）利用入队指针循环使用数组存储元素

### 出队
出队存在五个方法，分别是：remove()、remove(Object o)、poll()、take()、poll(long timeout, TimeUnit unit)  

#### remove()
```java
//AbstractQueue.remove()
public E remove() {
	//调用poll()出队
	E x = poll();
	//若有元素出队就返回该元素
	if (x != null)
		return x;
	else//否则抛出异常
		throw new NoSuchElementException();
}
```

#### remove(Object o)
```java
public boolean remove(Object o) {
	//不允许remove空值
	if (o == null) return false;
	//获取队列存储元素的数组
	final Object[] items = this.items;
	//加锁
	final ReentrantLock lock = this.lock;
	lock.lock();
	try {
		//若队列中存在元素
		if (count > 0) {
			//获取入队指针
			final int putIndex = this.putIndex;
			//获取读取元素的指针
			int i = takeIndex;
			//注意：这里必须使用do-while循环, 因为如果数组已经满了的话, putIndex指针会归0
			//那么takeIndex指针是从0开始的, 为了继续执行判断逻辑, 必须要通过do-while先使得takeIndex++
			//直到takeIndex == item.length时, takeIndex会归0, 与putIndex相等退出循环
			do {//当读取指针还未等于入队指针时, 判断删除元素是否等于读取指针指向的元素
				if (o.equals(items[i])) {
					//如果相同, 则删除后return true
					removeAt(i);
					return true;
				}
				//若读取指针自增到数组长度值, 说明已经完成所有遍历, 读取指针归0循环利用
				if (++i == items.length)
					i = 0;
			} while (i != putIndex);
		}
		//若队列不存在元素, return false
		return false;
	} finally {
		//释放锁
		lock.unlock();
	}
}
```

### poll()
```java
public E poll() {
	//获取锁
	final ReentrantLock lock = this.lock;
	lock.lock();
	try {
		//若队列不存在元素返回null, 否则出队
		return (count == 0) ? null : dequeue();
	} finally {
		//释放锁
		lock.unlock();
	}
}
```

### take()
```java
public E take() throws InterruptedException {
	/加锁
	final ReentrantLock lock = this.lock;
	lock.lockInterruptibly();
	try {
		//若队列不存在元素, 则阻塞当前线程, 直到其他线程在队列中添加了元素, 当前线程被唤醒
		while (count == 0)
			notEmpty.await();
		//执行出队操作
		return dequeue();
	} finally {
		lock.unlock();
	}
}
```

### E poll(long timeout, TimeUnit unit)
```java
public E poll(long timeout, TimeUnit unit) throws InterruptedException {
	long nanos = unit.toNanos(timeout);
	//加锁
	final ReentrantLock lock = this.lock;
	lock.lockInterruptibly();
	try {
		//当队列不存在元素, 则阻塞当前线程nanos纳秒, 若到达时间后还未有其他线程往队列中添加元素, 那么return null
		while (count == 0) {
			if (nanos <= 0)
				return null;
			nanos = notEmpty.awaitNanos(nanos);
		}
		//执行出队操作
		return dequeue();
	} finally {
		lock.unlock();
	}
}
```

#### dequeue()
```java
private E dequeue() {
	// assert lock.getHoldCount() == 1;
	// assert items[takeIndex] != null;
	//获取队列存储元素的数组
	final Object[] items = this.items;
	//获取takeIndex指针指向的元素
	@SuppressWarnings("unchecked")
	E x = (E) items[takeIndex];
	//将takeIndex位置置为空
	items[takeIndex] = null;
	//takeIndex自增直到指向数组末尾后归0
	if (++takeIndex == items.length)
		takeIndex = 0;
	//数组中的元素个数-1
	count--;
	if (itrs != null)
		itrs.elementDequeued();
	//唤醒notFull条件, 使得正在阻塞且执行添加操作的线程得到被唤醒的信号
	notFull.signal();
	return x;
}
```
（1）remove()时, 如果队列为空则抛出异常
（2）poll()时, 如果队列为空则返回null
（3）take()时, 如果队列为空, 则线程阻塞等待条件notEmpty满足后被唤醒, 继续执行出队操作
（4）poll(), take的计时器版本
（5）利用取指针takeIndex循环从数组中去除原宿

### 总结
（1）ArrayBlockingQueue不需要扩容，因为是初始化时指定容量，并且会通过putIndex、takeIndex循环利用数组
（2）利用重入锁和两个条件notEmpty、notFull保证并发安全
（3）由于put或take方法会无限阻塞，若消费或生产的速度不一致，就会导致阻塞线程越积越多