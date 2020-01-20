# int和Integer有什么区别
int是Java八个基本数据类型之一，而Integer是int对应的包装类，它有一个```final int value```字段用于存储int类型的数据，并提供了一些相应的方法实现”字符串转换整数“、“数学运算”等操作。在Java 5时期引入了自动装箱和拆箱的机制，使得Java可以根据上下文自动对int或者Integer进行转换。不仅如此，还新增了Integer的缓存机制，使得在通过静态工厂方法Integer.valueOf创建-128~127这个范围的Integer对象时，通过缓存进行创建，明显地带来了性能的改善。不仅是Integer，其他的包装类也同样实现了缓存机制。

### Integer的自动装箱与拆箱
自动装箱可以简单理解为Java平台为我们自动进行了一些转换，保证不同的写法在运行时等价，它们发生在编译阶段，也就是生成的字节码是一致的。例如```Integer val = 123;```
javac替我们把1998自动装箱转换为Integer.valueOf()，同样的```int val = new Integer(123)```javac也替我们把Integer对象自动拆箱转换为Integer.intValue()。那么既然自动装箱是通过Integer.valueOf实现的，自然能够享受到缓存带来的性能提升。

### 关于Integer的注意事项
避免无意中的装箱、拆箱行为，因为创建1w个对象相比起1w个基本数据类型的开销无论是内存占用还是处理速度，都不是一个量级的。同样的，如果要追求极致性能，那么能使用数组也尽量采用数组而不是ArrayList。总而言之，尽量避免创建过多的对象。例如一个常见的线程安全计数器实现的代码如下：
```java
class Counter { 
	private final AtomicLong counter = new AtomicLong(); 
	public void increase() { 
		counter.incrementAndGet();
	}
}
```
那么如果通过基本数据类型来实现，可以将其修改为：
```java
public class Counter {
    private volatile long count;
    private static AtomicLongFieldUpdater<Counter> update = AtomicLongFieldUpdater.newUpdater(Counter.class, "count");

    public void increament() {
        count = update.incrementAndGet(this);
    }
}
```

### Integer缓存机制
Integer的缓存范围默认是-128~127，我们可以通过JVM提供的参数设置进行修改：
```
-XX:AutoBoxCacheMax=N
```
而这部分的实现细节，在Integer源码中的static静态块中有所体现：
```java
private static class IntegerCache {
	static final int low = -128;
	static final int high;
	static final Integer cache[];

	static {
		// 缓存的最大值, 默认127
		int h = 127;
		// 获取JVM的缓存配置参数
		String integerCacheHighPropValue =
			sun.misc.VM.getSavedProperty("java.lang.Integer.IntegerCache.high");
		// 如果缓存配置参数不为空, 那么根据自定义的参数计算缓存范围
		if (integerCacheHighPropValue != null) {
			try {
				int i = parseInt(integerCacheHighPropValue);
				i = Math.max(i, 127);
				// Maximum array size is Integer.MAX_VALUE
				h = Math.min(i, Integer.MAX_VALUE - (-low) -1);
			} catch( NumberFormatException nfe) {
				// If the property cannot be parsed into an int, ignore it.
			}
		}
		// high默认被赋值为127
		high = h;
		// 创建缓存数组
		cache = new Integer[(high - low) + 1];
		int j = low;
		// 初始化缓存数组
		for(int k = 0; k < cache.length; k++)
			cache[k] = new Integer(j++);
		// range [-128, 127] must be interned (JLS7 5.1.7)
		assert IntegerCache.high >= 127;
	}

	private IntegerCache() {}
}
```

### Integer的安全考量以及线程安全问题
上文中提到过，Integer底层的int value是被声明为private final的，因此Integer也是不可变类型。这样设计也带来了基本的信息安全和并发编程中的线程安全，假设我们通过一个Integer对象来设置服务器某个服务的端口，如果能够轻易地获取并且更改这个数值，就会带来很严重的安全方面问题。  

需要注意的是，虽然Integer是不可变类型，但如果想要在多线程场景下实现Integer的自增功能，一定要采用AtomicInteger，下面是通过Integer实现的线程计数器代码：
```java
public class Counter {
    private static Integer count = 0;

    public static void increament() {
        count = count + 1;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    increament();
                }
            }).start();
        }
        System.out.println(count);
    }
}
```
执行结果是276，与我们预期的1000相距甚远，此时通过反编译查看increament的字节码：
```
public static void increament();
	descriptor: ()V
	flags: ACC_PUBLIC, ACC_STATIC
	Code:
	  stack=2, locals=0, args_size=0
		 0: getstatic     #2                  // Field count:Ljava/lang/Integer;
		 3: invokevirtual #3                  // Method java/lang/Integer.intValue:()I
		 6: iconst_1
		 7: iadd
		 8: invokestatic  #4                  // Method java/lang/Integer.valueOf:(I)Ljava/lang/Integer;
		11: putstatic     #2                  // Field count:Ljava/lang/Integer;
		14: return
	  LineNumberTable:
		line 26: 0
		line 27: 14
```
可以发现我们在increament方法中对Integer的自增是先通过intValue拆箱转换为int类型，然后对该int值加1之后再次调用Integer.valueOf进行装箱操作，导致出现线程安全问题的本质不在于Integer，而是在于```Integer = Integer + 1;```其实Integer自增本质上就是i++加上装箱拆箱的操作，因此要想实现对Integer的自增操作，还是得通过AtomicInteger来实现。