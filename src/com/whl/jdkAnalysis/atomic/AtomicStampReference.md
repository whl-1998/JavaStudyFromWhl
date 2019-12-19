# AtomicStampedReference
AtomicStampedReference是j.u.c.atomic包下提供的一个原子类，它能通过维护一个版本号解决其它原子类无法解决的ABA问题。

### ABA问题
我们知道CAS操作底层是依赖于Unsafe.compareAndSwap操作实现的，它通过比较源内存地址与更新时期望的内存地址是否相同来决定是否执行更新操作，但如果某线程1在读取了内存地址X = A的同时，线程2读取并修改了内存地址X的地址 = B之后又再次读取并修改回内存地址X = A，这时线程1就会认为内存地址X是没有没修改过的。

用代码表示ABA过程如下所示：
```java
public class ABATest {
    public static void main(String[] args) {
        AtomicInteger at = new AtomicInteger(1);
		
		//线程1先读取到value, 然后阻塞1s
        new Thread(() -> {
            int value = at.get();
            System.out.println("thread1 read value = " + value);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (at.compareAndSet(value, 3)) {//if update success return true
                System.out.println("thread1 update from " + value + " to 3");
            } else {
                System.out.println("thread1 update fail");
            }
        }).start();

		//在线程1阻塞的同时, 线程2对value进行了从1->2->1的更新
        new Thread(() -> {
            int value = at.get();
            System.out.println("thread2 read value = " + value);

            if (at.compareAndSet(value, 2)) {
                System.out.println("firstly, thread2 update from " + value + " to 2");
                value = at.get();
                System.out.println("thread2 read value after update " + value);
                if (at.compareAndSet(value, 1)) {
                    System.out.println("Secondly, thread2 update from " + value + " to 1");
                }
            }
        }).start();
    }
}
```
执行结果：
```
thread1 read value = 1
thread2 read value = 1
thread2 update once from 1 to 2
thread2 read value after update once = 2
thread2 update second from 2 to 1
thread1 update from 1 to 3
```
看上去好像没什么问题，但假设我们有一个无锁的栈结构如下：[1, 2, 3, 4, 5]，线程A读取了栈顶元素1，但还未执行pop时就被阻塞了，与此同时线程B执行了两次pop，并push(1)到栈顶，此时栈结构如下：[1, 3, 4, 5]，线程A通过CAS操作比较之后继续执行pop，然而结果本该是[2, 3, 4, 5]，此时却因为ABA问题变成了[3, 4, 5]，这就是ABA问题的危害。

### ABA问题解决
1. 我们可以通过在上述栈结构中增加一个版本号用于控制，每执行一次更新操作，版本号 + 1，且执行更新操作时，检查元素值的同时也检查版本号是否一致，这样就能够保证CAS的安全。
2. 不仅如此，在上述线程B执行push(1)操作时，创建一个新的引用地址value传入，而不是复用之前头元素1的引用地址。

同样的AtomicStampedReference采用了将元素值与版本号绑定的方式，并在更新操作时，创建新的引用地址进行更新，实现了ABA问题的解决。下面是AtomicStampedReference的源码分析：

### 内部类
实现将元素值与版本号进行绑定
```java
    private static class Pair<T> {
        final T reference;//元素值
        final int stamp;//邮戳(版本号)
        private Pair(T reference, int stamp) {
            this.reference = reference;
            this.stamp = stamp;
        }
        static <T> Pair<T> of(T reference, int stamp) {
            return new Pair<T>(reference, stamp);
        }
    }
```

### 属性
```java
	//volatile保证Pair的修改对所有线程可见
	private volatile Pair<V> pair;
	//unsafe实例
	private static final sun.misc.Unsafe UNSAFE = sun.misc.Unsafe.getUnsafe();
    //获取pair的偏移量
    private static final long pairOffset =
        objectFieldOffset(UNSAFE, "pair", AtomicStampedReference.class);
```

### compareAndSet()
```java
    public boolean compareAndSet(V   expectedReference,
                                 V   newReference,
                                 int expectedStamp,
                                 int newStamp) {
        //获取当前(元素值 + 版本号)对象
        Pair<V> current = pair;
        return
        	//若元素地址没变
            expectedReference == current.reference &&
            //版本号没变
            expectedStamp == current.stamp &&
            //新的元素地址 = 旧的元素地址
            ((newReference == current.reference &&
              //新的版本号 = 旧的版本号
              newStamp == current.stamp) ||
             //根据新的元素地址和版本号构造新的Pair并CAS更新
             casPair(current, Pair.of(newReference, newStamp)));
    }
```

### 总结
（1）在多线程环境下，使用无锁结构进行CAS操作时要注意ABA问题。  
（2）AtomicStampedReference通过版本号控制，并且每次添加元素时都会新建一个节点执行update操作实现了ABA问题的解决。  