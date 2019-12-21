世界上不可能存在不会出错的程序，对于一门优秀的语言，它必定具备容灾机制的特性，这样才能保证程序的可靠性。  

Java在设计之初就提供了相对完善的异常处理机制，该机制大大降低了编写和维护可靠程序的门槛，或许这也是Java如今这么火爆的原因之一。  

在此，先抛出两个面试中常问到的问题——Exception和Error的区别，RuntimeException和CheckedException的区别。  

### Exception和Error的区别
共同点：Exception和Error都继承自Throwable类，在Java中只有继承了Throwable类型的实例才能够被抛出或捕获。  

不同点：  
Exception：是程序正常运行中可以预料的异常，它可以且应该尽可能被捕获并处理，且Exception又分为检查异常和非检查异常：
1. CheckedException：泛指那些编译期就被强制要求抛出或者捕获并进行相应处理的异常，比如常见的IOException，FileNotFoundException等；

2. UncheckedException：泛指运行时异常RuntimeException，通常是可以编写代码时避免的逻辑错误，不会在编译器强制要求。对于UncheckedException一般都采用if-else语句判断处理而不是通过try-catch块进行捕获，因为try-catch语句块会对性能造成一定的影响，正确的示例参照下列代码：
```java
public void method(int[] arr) {
	if (arr.length == 0 || arr == null) {
		//do something to avoid NullPointException
		...
	}
	//logical
}
```

Error：是正常情况下不大可能出现的系统错误，一般Error都出现在JVM层面，诸如：内存溢出，栈溢出等不可恢复的状态。既然是非正常的情况，不便于也不需要捕获，因此编译器不会对Error作检查。

### 常见的Exception和Error
![ExceptionAndError](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9zdGF0aWMwMDEuZ2Vla2Jhbmcub3JnL3Jlc291cmNlL2ltYWdlL2FjLzAwL2FjY2JhNTMxYTM2NWU2YWUzOTYxNGViZmEzMjczOTAwLnBuZw?x-oss-process=image/format,png)

在此，抛出一个问题：ClassNotFoundException和NoClassDefFoundError有什么区别？  

首先，最显著的区别就是前者是Exception后者是Error，但这个答案面试官是不会满意的。更具体的区别如下列代码所示：
```java
public class TestNoClassDefFoundError {
    public static void main(String[] args) {
        new A();
    }
}
 
class A{
}
```
通过成功执行这段代码后，我们可以获得javac会编译生成两个字节码文件TestNoClassDefFoundError.class和A.class，此时删除掉A.class再次执行main方法就会抛出NoClassDefFoundError。  

那么可以说明NoClassDefFoundError的成因也就是类依赖的class文件或者jar不存在，或者是class文件存在，但是无法正常被获取。而ClassNotFoundException的成因一般在尝试获取某个不存在的类时抛出，如反射获取某个类的Class对象等。

### 处理异常
当一个方法出现错误引发异常时，方法会创建一个异常对象交付给运行时系统，该异常对象包含了异常类型和异常出现时的程序状态等异常信息。之后运行时系统会去寻找合适的异常处理器（ExceptionHandler）并执行其方法。若运行时系统遍历了调用栈（也就是声明的catch块）都没有找到合适的异常处理器，则运行时系统终止，Java程序终止。

整个异常的处理流程可以通过下面的代码所示：
```java
public class ExceptionHandlerMechanism {
    public int doMethod() {
        try {
            int i = 10/0;// 抛出一个异常对象, 直接进入到catch块寻找合适的异常处理器
            System.out.println(i);
        }catch (ArithmeticException e){// 自顶向下寻找合适的异常处理器, 在这里成功匹配到了异常对象, 因此后序的catch块不会执行
            System.out.println("ArithmeticException");// 打印异常信息
            return 10;// 不会执行这个return, 直接进入到finally块执行剩下的逻辑
        }catch (Exception e){
            System.out.println("Exception");
            return 5;
        }finally {
            System.out.println("finally");
            return 0;
        }
    }
}
```

### 规范
#### 1. throw early, catch late
```java
public void readPreferences(String fileName){
	//read fileStream 
	InputStream in = new FileInputStream(fileName);
}
```
上述代码中，由于没有遵循throw early的原则，如果传入了fileName为null的参数时会抛出空指针异常，由于没有尽早的对可能出现的空指针异常进行处理，当出问题时堆栈信息会难以定位到问题所，下面是遵循throw early原则的示例：
```java

public void readPreferences(String fileName){
    if (fileName == null || fileName.length() == 0) {
		//do something
	}
	InputStream in = new FileInputStream(fileName);
}
```
那么catch later，则是要求当前层处理不了的异常尽量不对其捕获，而是交给具备更清晰业务逻辑的上层进行处理。

#### 2. 不要生吞异常, 并且不要泛化异常
下面是一段错误的示例：
```
try {  
	Thread.sleep(1000L);
} catch (Exception e) { 
	// Ignore it
}
```
捕获异常这个行为就是为了明确问题所在，若生吞异常就会造成：出现了错误也无法定位到问题所在。不仅如此，我们应该捕获特定的异常，这样不仅能够给代码阅读者提供更多信息，并且也不会捕获到那些并不想捕获的异常。

#### 3. 尽量选择将异常信息持久化到日志系统
在分布式系统中，出现了异常是无法通过堆栈轨迹定位问题的，因此应该将异常信息持久化到日志系统

#### 4. 合理使用try-catch代码块
try-catch代码段会产生额外的性能开销，它往往会影响JVM对代码进行优化（指令重排），所以仅捕获可能会出现异常的代码段，尽量不要用一个try包住整段的代码；与此同时，利用异常控制代码流程也不是一个好主意，远比if else这类判断语句低效。因为每当代码出现异常时，实例化一个Exception的同时JVM会对当时的栈进行快照，这是一个相对繁重的操作。如果实例化异常非常频繁，这个开销就是不可忽略的。