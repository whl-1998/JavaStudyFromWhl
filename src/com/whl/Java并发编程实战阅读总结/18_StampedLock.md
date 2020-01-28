### StampedLock

StampedLock是比ReadWriteLock性能更优的锁，ReadWriteLock只支持读锁、写锁两种模式，而StampedLock支持三种模式，分别是乐观读、悲观读锁、写锁。其中写锁、悲观读锁的语义和ReadWriteLock的写锁、读锁语义非常相似，都是允许多个线程同时获取悲观读锁，只允许一个线程获取写锁，并且悲观读锁与写锁互斥。不同的是，StampedLock在写锁、悲观读锁加锁成功后会返回一个stamp；在解锁时，需要传入这个stamp，示例代码如下：

```java
final StampedLock sl = new StampedLock();

// 获取/释放悲观读锁示意代码
long stamp = sl.readLock();
try {
  //省略业务相关代码
} finally {
  sl.unlockRead(stamp);
}

// 获取/释放写锁示意代码
long stamp = sl.writeLock();
try {
  //省略业务相关代码
} finally {
  sl.unlockWrite(stamp);
}
```

而StampedLock性能比ReadWriteLock更好的关键是它支持乐观读，在多个线程执行读操作时，允许一个线程获取写锁。需要注意的是，StampedLock支持的是乐观读，而不是“乐观读锁”，这意味着乐观读是不需要加锁的。

下面是一个坐标类Point，其中包含了坐标x、y以及StampedLock实例的属性，计算原点到x，y坐标的距离方法distanceFromOrigin()，这个方法首先会尝试用乐观读获取x、y，如果判断乐观读期间没有存在写操作，就直接返回；如果判断乐观读期间存在写操作，那么需要升级为悲观读锁，再次获取x、y：

```java

class Point {
    private int x, y;
    final StampedLock sl = new StampedLock();

    //计算到原点的距离  
    int distanceFromOrigin() {
        // 乐观读
        long stamp = sl.tryOptimisticRead();
        // 读入局部变量，
        // 读的过程数据可能被修改
        int curX = x, curY = y;
        // 判断执行读操作期间，
        // 是否存在写操作，如果存在，则sl.validate返回false
        if (!sl.validate(stamp)) {
            // 升级为悲观读锁
            stamp = sl.readLock();
            try {
                curX = x;
                curY = y;
            } finally {
                //释放悲观读锁
                sl.unlockRead(stamp);
            }
        }
        return Math.sqrt(curX * curX + curY * curY);
    }
}
```

这种锁升级的方式相比起死循环获取乐观读期间不存在写操作的结果，不存在浪费CPU的可能，代码也更加简练。



### StampedLock注意事项

1. StampedLock是不支持可重入的。

2. StampedLock的悲观读、写锁都不支持条件变量。

3. 使用StampedLock时一定不要调用中断操作，若需要支持中断功能，一定使用可中断的悲观读锁readLockInterruptibly()、写锁writeLockInterruptibly()。例如下面代码中：

   ```java
   final StampedLock lock = new StampedLock();
   Thread T1 = new Thread(() -> {
       // 获取写锁
       lock.writeLock();
       // 永远阻塞在此处，不释放写锁
       LockSupport.park();
   });
   T1.start();
   
   // 保证T1获取写锁
   Thread.sleep(100);
   Thread T2 = new Thread(()->
       //阻塞在悲观读锁
       lock.readLock()
   );
   T2.start();
   // 保证T2阻塞在读锁
   Thread.sleep(100);
   // 中断线程T2会导致线程T2所在CPU飙升
   T2.interrupt();
   T2.join();
   ```

   