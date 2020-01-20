### 线程的生命周期和状态转移
在Java5以后，线程状态被明确定义在java.lang.Thread的公共内部枚举类State中，它们分别是：
1. 新建NEW：线程刚被创建出来但是还未真正启动的状态。当执行代码```Thread t = new Thread();```时，就可以认为线程t处于NEW状态。

2. 就绪RUNNABLE：线程此时可能处于正在运行状态，也可能处于就绪队列中排队等待CPU分配时间片的状态。当执行代码```t.start();```时，就可以认为线程t处于RUNNABLE状态。

3. 阻塞BLOCKED：表示线程正在等待获取锁的状态，比如某个线程通过synchronized去获取某个对象的锁，但是发现该锁已经被其他线程独占，此时当前线程就会处于阻塞状态。

4. 等待WAITING：表示线程当前处于无限等待状态，在等待池中等待被其他线程唤醒（notify()或notifyAll()），处于等待池中的线程不会去争夺锁。常见的例如Thread.join()、Object.wait()都会使线程进入WAITING状态。

5. 计时等待TIMED_WAITING：表示线程当前处于计时等待状态，当达到某个指定时间后，线程会被自动唤醒。常见的例如Thread.join(long timeout)、Object.wait(long timeout)都使线程进入TIMED_WAITING状态，并会在指定时间之后唤醒它。

6. 终止TERMINATED：当线程执行完毕，无论是正常执行还是意外退出，都会进入终止状态。

#### 连续调用两次start方法会出现什么情况
此时会抛出IllegalThreadStateException，从Thread.start()的源码中可以发现，当线程当前状态不是NEW，并再次调用start()时则会抛出异常，这也体现了线程状态不可逆的特点。
```java
public synchronized void start() {     
	if (threadStatus != 0)
		throw new IllegalThreadStateException();
	
	....
}
```

### 实现Runnable接口来创建线程
下面是通过继承Thread类的方式创建一个线程，通过lamda表达式重写run方法
```java
Thread t1 = new Thread(() -> {
	...
});
```
Java只允许单继承，因此该线程类是不允许再通过继承扩展其他父类的。而通过实现Runnable接口创建一个线程则不会出现这种问题。
```java 
Runnable task = () -> {
	...
};
```