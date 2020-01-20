# AtomicInteger
AtomicInteger位于j.u.c.atomic包下，其操作都具备原子性，主要利用了CAS + volatile保证原子操作，从而避免synchronized的高开销，执行效率大大提升。CAS的实现主要通过了Unsafe下的compareAndSwapInt()方法进行了实现，该方法是一个native方法，通过比较源对象地址与更新时预期的源对象地址是否相同来决定是否执行更新操作。  

### 主要属性
```java
//获取Unsafe实例对象, 目的是使用Unsafe.compareAndSwapInt进行update操作
private static final Unsafe unsafe = Unsafe.getUnsafe();
//标识value字段的偏移量
private static final long valueOffset;

//初始化: 通过unsafe获取value的偏移量
static {
	try {
		//获取value字段偏移量
		valueOffset = unsafe.objectFieldOffset
			(AtomicInteger.class.getDeclaredField("value"));
	} catch (Exception ex) { throw new Error(ex); }
}
//存储int类型值的位置, 采用volatile修饰
//保证JVM总能获取到该变量的最新值
private volatile int value;
```

### compareAndSet()
调用Unsafe.compareAndSwapInt()实现CAS操作，保证只有当对应偏移量处的字段是期望值时才会执行更新操作，这样就避免出现当多个线程同时读取value时，某个线程读取到value = a，而在准备更新时value却被其他线程更新为b的情况。
```java
//调用unsafe.compareAndSwapInt()实现, 在Unsafe下通过native方法实现CAS操作
//只有当字段偏移量valueOffset = 期望值expect时才进行更新
public final boolean compareAndSet(int expect, int update) {
	//参数分别是：操作的对象, 对象中字段的偏移量, 期望值, 修改后的值
	return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}
```

### getAndIncrement()
获取到当前的值并自增，底层用到了CAS + 自旋的乐观锁机制，其他的原子自减getAndDecrement()方法，亦或是原子更新getAndUpdate()方法也是同样的实现方式。
```java
public final int getAndIncrement() {
	//底层调用unsafe.getAndAddInt()方法
	//参数分别是: 操作对象, 对象中字段的偏移量, 要增加的值
	return unsafe.getAndAddInt(this, valueOffset, 1);
}

//Unsafe.getAndAddInt
//获取当前的值并增加指定值
public final int getAndAddInt(Object var1, long var2, int var4) {
	int var5;
	do {
		//获取当前的值的
		var5 = this.getIntVolatile(var1, var2);
	  //不断循环尝试更新对应偏移量位置的值, 直到成功为止
	  //经典CAS + 自旋锁
	} while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
	return var5;
}
```

### AtomicInteger对比synchronized
通过同时启100个线程，每个线程自增10w次，在保证线程安全的情况下，我们使用两种不同的方式对其进行实现，并对比它们的执行时间：
1. synchronized：663ms
```java
public static int count = 0;

public static synchronized void increment() {
	count++;
}

public static void main(String[] args) {
	long a = System.currentTimeMillis();
	for (int i = 0; i < 100; i++) {
		new Thread(()->{
			for (int j = 0; j < 100000; j++) {
				increment();
			}
		}).start();
	}
	while (Thread.activeCount() > 2) {
		Thread.yield();
	}
	System.out.println(count);
	System.out.println(System.currentTimeMillis() - a);
}
```
2. AtomicInteger：296ms
```java
public static AtomicInteger count = new AtomicInteger();

public static void increment() {
	count.incrementAndGet();
}

public static void main(String[] args) {
	long a = System.currentTimeMillis();
	for (int i = 0; i < 100; i++) {
		new Thread(()->{
			for (int j = 0; j < 100000; j++) {
				increment();
			}
		}).start();
	}
	while (Thread.activeCount() > 2) {
		Thread.yield();
	}
	System.out.println(count);
	System.out.println(System.currentTimeMillis() - a);
}
```
可以看到同样的线程安全自增，AtomicInteger这种CAS + 自旋的乐观锁实现比起synchronized同步块的悲观锁实现要快上两倍左右，这也是为什么我们要使用AtomicInteger。

### ABA问题
AtomicInteger也并非十全十美，因为它可能会出现ABA问题，对于ABA问题我们可以想象：你有一瓶洗发水，剩余容量500ml，你的舍友某天用了你的洗发水100ml，但怕被你发现，于是舍友在用完后又偷偷加了100ml的水进去使洗发水看上去还是500ml，但洗发水确实已经被用过了。对于ABA问题的解决访问可以参考AtomicStampedReference。