# Unsafe
Unsafe为我们提供了访问底层的机制，这种机制仅供Java核心类库使用，该特点在获取Unsafe的实例对象上可以很明显地看出，但是我们依旧可以通过反射获取Unsafe实例并使用它。

### 1. 获取Unsafe实例
```java
	private static final Unsafe theUnsafe;
	
	//经典单例模式
	//构造器私有化, 通过静态方法返回同一个对象
	private Unsafe() {
    }

    @CallerSensitive
    public static Unsafe getUnsafe() {
    	//获取调用Unsafe的Class对象
        Class var0 = Reflection.getCallerClass();
        //若Class对应的类加载器采用的不是BootStrapClassLoader, 证明其并非核心类, 那么就抛出异常
        if (!VM.isSystemDomainLoader(var0.getClassLoader())) {
            throw new SecurityException("Unsafe");
        } else {
            return theUnsafe;
        }
    }
```
Unsafe提供了一个静态方法getUnsafe()用于获取它的实例对象，但直接调用会抛出SecurityException异常，这是因为Unsafe只提供给Java核心类使用，那么可以通过反射获取它的实例：
```java
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        // System.out.println(Class.class.getClassLoader()); //null --> BootStrapClassLoader
        Field field = Unsafe.class.getDeclaredField("theUnsafe");//getField只能获取public属性
        field.setAccessible(true);//关闭访问安全检查开关
        Unsafe unsafe = (Unsafe) field.get(null);
    }
```

### 2. 使用Unsafe实例化一个类
```java
	//Unsafe通过一个native方法实现实例化一个类, 但是只会分配内存, 并不会调用该类的构造方法
	public native Object allocateInstance(Class<?> var1) throws InstantiationException;
```

### 3. 修改任意私有字段的值
我们可以通过Unsafe.putInt()修改User的私有int属性age的值，如果是其它类型的属性也可以调用对应的put方法进行修改，其底层都是通过native方法获取对象的地址 + 偏移值实现update。
```java
public class UnsafeTest {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
    	//获取unsafe实例
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
		//获取user实例, 并指定age = 19
        User user = new User(19);
        System.out.println(user.getAge());//19
		//通过反射获取User.class的age属性
        Field age = user.getClass().getDeclaredField("age");
        //调用unsafe.putInt, 参数为: 指定对象, 偏移量, 修改后的值
        //putInt方法通过获取user对象的起始地址, 加上objectFieldOffset()获取到的偏移值得到对应的update地址, 然后将20写入对应的update地址
        unsafe.putInt(user, unsafe.objectFieldOffset(age), 20);
        System.out.println(user.getAge());//20
    }
}

class User {
    private int age;

    public User(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }
}
```
这部分也可以说体现了Unsafe不安全的特点，因为Unsafe是通过一个native方法直接对内存进行读写操作实现了更新：
```java
	//通过传入更新对象var1, 偏移值var2, 更新的值var3完成更新
	public native void putInt(Object var1, long var2, int var4);

	//调用native方法, 获取Field属性的偏移值
	public native long objectFieldOffset(Field var1);
```
不仅如此，我们也可以直接通过反射进行私有属性值的修改，如下所示：  
```java
public class UnsafeTest {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        User user = new User(19);
        System.out.println(user.getAge());

        Field age = user.getClass().getDeclaredField("age");
        age.setAccessible(true);
        age.set(user, 20);
        System.out.println(user.getAge());
    }
}

class User {
	...
}
```

### 4. 抛出checked异常
对于checkedException一般我们都需要在编译器对其进行显示地捕获，可以通过try-catch对其进行捕获，或者在方法上通过throws抛出这个异常交给上层处理。而通过Unsafe，我们可以直接抛出一个checked异常，同时却不用捕获或在方法签名上定义它。
```java
private static Unsafe unsafe;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public static void readFile() throws IOException {
        throw new IOException();
    }

    public static void readFileUnsafe() {
        unsafe.throwException(new IOException());
    }
```

### 5. 使用堆外内存
若进程在运行过程中JVM内存不足就会导致频繁的进行GC，理想情况下可以考虑通过Unsafe.allocateMemory()申请使用堆外内存，这块堆外内存由于不受JVM管理，因此必须通过freeMemory()方法手动释放它。

下面是一个基于Unsafe实现的堆外数组，（我还是第一次知道数组能够手写出来...）
```java
public class OffHeapArray {
    private static final int INT = 4;//一个int位占用的字节数
    private long size;
    private long addr;

    private static Unsafe unsafe;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //指定堆外数组的大小, 并通过unsafe.allocateMemory()分配内存
    public OffHeapArray(long size) {
        this.size = size;
        addr = unsafe.allocateMemory(size * INT);//内存大小 = 数组长度 * 32bit(4字节)
    }

    //获取指定索引处的元素
    public int get(long index) {
        //通过unsafe.getInt方法获取到指定内存地址上的值
        return unsafe.getInt(addr + index * INT);
    }

    //设置指定索引处的元素
    public void set(long index, int value) {
        unsafe.putInt(addr + index * INT, value);
    }

    //数组长度
    public long getSize() {
        return size;
    }

    //释放堆外内存
    public void freeMem() {
        unsafe.freeMemory(addr);
    }
}
```

### 6. CompareAndSwap（CAS）
Juc包下大量使用了CAS操作，其底层都是通过Unsafe的CompareAndSwap操作实现的，这种方式广泛运用于无锁算法，与Java中标准的悲观锁机制相比，CAS的效率更加高效。  

##### 关于CAS的思想这里稍微提一下：
有三个参数：当前内存值object、更新时预期的内存值expect、即将更新的值update，当且仅当expect和value相同时，才将object修改为update并返回true，否则什么都不做并返回false。  

这样配合volatile关键字就能够避免在多线程环境下，多个线程同时读取同一个资源并修改造成的线程不安全问题。试想一下，线程A读取并修改了资源stock，但还未回写到主内存，此时线程B通过volatile发现线程A修改了资源stock，那么更新时的预期内存值expect就与当前内存值object不相同，也就不会进行更新操作。

下面是AtomicInteger实现的核心方法——compareAndSwapInt()
```java
	/**
	//compareAndSwapInt的实现逻辑可以用如下代码所示
	if (object == expect) {
		object = update;
		return true;
	} else {
		return false;
	}
	*/
	public final native boolean compareAndSwapInt(Object object, long valueOffset, int expect, int update);
```

### 7. 阻塞或唤醒线程
JVM在上下文切换时调用了Unsafe.park()和Unsafe.unpark()，当一个线程用完了分配给它的CPU时间片，则JVM会调用Unsafe.park()方法来阻塞此线程；当一个被阻塞的线程需要再次运行时，JVM调用Unsafe.unpark()方法来唤醒此线程。