### 为什么要使用ReenTrantLock

在JDK1.6版本之后，synchronized做了很多优化，因此性能并不是“重复造轮子”的理由，在05死锁预防中提到过，可以通过破坏不可抢占条件解决死锁，但是这个方案synchronized是无法解决的，当synchronized申请资源时，如果申请不到就会进入阻塞状态，并不能释放线程已经占据的锁。

而ReenTrantLock则具备破坏不可抢占条件的方案：

1. 响应中断：如果线程因为无法申请到锁而进入了阻塞状态，我们可以给阻塞的线程发送中断信号，那么这个线程就有机会释放曾经持有的锁。
2. 支持超时：如果线程在一段时间内没有获取到锁，则返回一个错误并释放曾经持有的锁。
3. 非阻塞获取锁：如果尝试获取锁失败，直接返回一个错误并释放曾经持有的锁。

这三种方案可以全面弥补synchronized的不足，也是为什么我们有了synchronized还需要ReenTrantLock的原因之一。



### ReenTrantLock如何保证可见性

synchronized之所以能够保证可见性，是因为Happens-Before原则中，synchronized的解锁Happens-Before后续另一个线程的加锁。而ReenTrantLock则利用了volatile相关的Happens-Before原则（对volatile变量的写操作Happens-Before后续对volatile的读操作），ReenTrantLock内部持有一个volatile的成员变量state，在获取锁时，会先读取state的值，并尝试执行state++；在解锁的时候也会读取state的值，并尝试执行state--。



### 问题汇总

##### 1. tryLock()支持非阻塞方式获取锁，那么下面这段程序是否存在问题？

```java
class Account {
    private int balance;
    private final Lock lock = new ReentrantLock();
    // 转账
    void transfer(Account tar, int amt) {
        while (true) {
            if(this.lock.tryLock()) {
                try {
                    if (tar.lock.tryLock()) {
                        try {
                            this.balance -= amt;
                            tar.balance += amt;
                        } finally {
                            tar.lock.unlock();
                        }
                    }
                } finally {
                    this.lock.unlock();
                }
            }
        }
    }
}
```

首先，这是一个死循环程序，即使成功执行了转账操作，程序也无法正常退出。其次，如果A与B同时持有自己的锁，并且尝试获取对方的锁，当双方同时获取对方的锁失败，且同时释放自己持有的锁，再同时获取锁... 也就造成了活锁的可能。解决办法是，获取锁失败后，让线程等待一个随机时间再尝试获取锁。

