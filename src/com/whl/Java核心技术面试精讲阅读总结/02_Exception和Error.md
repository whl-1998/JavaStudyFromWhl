Java在设计之初就提供了相对完善的异常处理机制，大大降低了编写和维护可靠程序的门槛。而异常这个功能主要是通过 java.lang 包下的 Throwable 类实现的，在 Java 中只有 Throwable 类型的实例才允许被抛出或者捕获，而Exception 和 Error 都继承了 Throwable 类，分别用于应对不同场景下的异常情况。



### Exception和Error

Exception是程序在正常运行中可以预料的意外情况，可能并且应当被捕获处理。

而Exception又被细分为可检查异常和非检查异常，可检查异常则必须被显示捕获，否则就会在编译器报错，例如： IO 操作需要捕获的IOException；反射操作需要捕获的ClassNotFoundException等。非检查异常则是所谓的运行时异常RuntimeException，常见的 NullPointerException、ArrayIndexOutOfBoundsException 都是运行时异常的子类。我们一般不对运行时异常进行捕获，而是通过 if - else 的逻辑判断来避免出现这类异常（throw early原则），示例如下：

```java
public void bubbleSort(int[] arr) {
    //避免空指针异常
    if (arr == null || arr.length == 0) return;
	//...省略实现逻辑
}
```

而Error则是不大可能出现的程序错误，一般Error都会导致程序处于非正常且不可恢复的状态。例如：OOM Error，StackOverflowError 等常见错误都是Error的子类。



### ClassNotFoundException和NoClassDefFoundError有什么区别

首先，最显著的区别就是前者是Exception后者是Error，更具体的区别如下列代码所示：

```java
public class TestNoClassDefFoundError {
    public static void main(String[] args) {
        new A();
    }
}
 
class A{
}
```

通过成功执行这段代码后，我们可以获得javac会编译生成两个字节码文件TestNoClassDefFoundError.class和A.class，此时删除掉A.class再次执行main方法就会抛出NoClassDefFoundError，也就是类加载流程需要的 .class文件不存在时；或者是 .class文件存在，但是无法正常被获取时，就会抛出这个异常。而ClassNotFoundException的成因一般在尝试获取某个不存在的类时抛出，如反射获取某个类的Class对象等。



### try - catch块处理异常的流程

当一个方法出现错误引发异常时，方法会创建一个异常对象交付给运行时系统，该异常对象包含了异常类型和异常出现时的程序状态等异常信息。之后运行时系统会去寻找合适的异常处理器（ExceptionHandler）并执行其方法。若运行时系统遍历了调用栈（也就是声明的catch块）都没有找到合适的异常处理器，则运行时系统终止，Java程序终止。

整个异常的处理流程可以通过下面的代码所示：

```java
public class ExceptionHandlerMechanism {
    public int doMethod() {
        try {
            int i = 10/0;// 抛出一个异常对象, 直接进入到catch块寻找合适的异常处理器
            System.out.println(i);
        } catch (ArithmeticException e){// 自顶向下寻找合适的异常处理器, 在这里成功匹配到了异常对象, 因此后序的catch块不会执行
            System.out.println("ArithmeticException");// 打印异常信息
            return 10;// 不会执行这个return, 直接进入到finally块执行剩下的逻辑
        } catch (Exception e){
            System.out.println("Exception");
            return 5;
        } finally {
            System.out.println("finally");
            return 0;
        }
    }
}
```

需要注意的是，创建一个异常对象也是一笔不小的性能开销。因此在遇到性能瓶颈时，分析异常堆栈信息寻找频繁创建异常对象的代码区域并且对其进行优化，这也是一种常见优化的思路。



### try - catch / throws

之前说过，Java对于异常的处理是采取抛出（throws ）或者捕获（try - catch）。那么两者在异常的处理上有什么不同呢？

区别其实很简单，throws 是将异常丢给上层处理，例如父类的某个方法抛出了异常，那么子类重写这个方法时就需要对这个异常进行处理：

```java
public class Test extends Father{
    @Override
    public void test() throws InterruptedException {
        super.test();
    }
}

class Father {
    public void test() throws InterruptedException {
        Thread.sleep(1000);
    }
}
```

try - catch 则是在当前层就对异常进行捕获处理:

```java
public class Test extends Father{
    @Override
    public void test() {
        super.test();
    }
}

class Father {
    public void test() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

根据二者对异常不同的处理方式，也分别适用于不同的场景。例如在Web项目的三层架构中，往往是Controller层持有着更清晰的业务逻辑，也更清楚合适的异常处理方式。因此一般我们选择在底层将异常抛出，交给高层对异常进行捕获处理。

诸如此类的原则还有很多，我们可以阅读下面这段代码，观察这段代码违背了哪些原则：

```java
try {
    // 业务代码
    // …
    Thread.sleep(1000L);
} catch (Exception e) {
    // Ignore it
}
```

1. **捕获泛型异常**

   代码中可以看到，我们将捕获的异常定义为Exception。这样做就导致了代码可读性差的问题，我们无法通过这段代码得知具体会在哪个层面抛出异常。其次，也可能捕获到我们不希望捕获的异常，例如我们更希望RuntimeException被扩散出来而不是被捕获。

2. **生吞异常**

   可以看到，我们在代码中捕获到异常对象后却不对其进行处理，这样就导致了代码出问题时难以诊断问题所在。这里最好的做法是将异常信息持久化到日志文件中，以便于后期诊断问题。

3. **用一个大 try - catch 包住所有代码**

   try-catch代码块虽然简单好用，但是也会带来额外的性能开销。因此，尽可能使 try - catch 的覆盖范围局限于可能抛出异常的代码段。



总结下来，Java提供的异常处理机制是非常的简单好用的。需要注意的点也就是上面几个原则了，只要平时写代码时稍加思考即可。