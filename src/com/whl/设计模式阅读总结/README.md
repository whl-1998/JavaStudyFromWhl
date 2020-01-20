# 设计模式分类
按照应用目标分类，设计模式可以分为创建型、结构型、行为型三种。
1. 创建型模式：是对对象创建过程中的各种问题和解决方案的总结，例如：
* 要求每次获取的对象都是同一个实例，可以采用单例模式实现，反之也可采取多例（原型）模式
* 将对象的创建、销毁交给容器管理以此实现解耦的目的，可以采取工厂模式
* 因为需要输入的参数过多从而导致构造函数的组合繁杂，最终导致代码的可维护性变差问题，可以采取构建器模式解决

2. 结构型模式：
* 若需要对类的行为进行增强，可以采取装饰器模式
* 若需要对多个类都需要实现的某些操作，如日志、安全等进行抽取，并通过代理类对这些类行为进行控制，可以采取代理模式

3. 行为型模式：
* 若要使用户通过特定的接口访问容器的数据，不需要了解容器内部的数据结构，可以采取迭代器模式
* 若需要定义一个算法的步骤，并要求子类在不改变算法架构的情况下，重新定义算法中的某些步骤时，可以采取模版方法模式
### 代理模式和装饰器模式的区别
代理模式主要适用于抽取通用模块，例如Service层的UserService需要实现日志、安全的功能，ShopService也同样需要实现日志、安全的功能，那么可以对日志、安全的部分进行抽取，通过代理类去增强Service类需要增强的方法，Service类只需要专注业务逻辑即可。  
装饰器模式主要适用于对对象功能的扩展，例如IO流中BufferedInputStream在InputStream的基础上增加了缓存的功能，类似这种装饰器还可以多层嵌套，不断增加不同层次的功能。

### 单例模式
常见的单例模式实现如Unsafe类，我们只需要使用同一个theUnsafe实例完成一系列操作，并且需要在获取时要实现权限的验证，因此只能通过getUnsafe()方法中进行权限校验以及获取实例的操作。

```java
public class SingletonDemo {
	//实例对象通过私有静态类型声明
    private static SingletonDemo singleton;
    
	//构造器私有化避免外部通过构造器创建实例
    private SingletonDemo() {
    }
    
	//通过静态方法获取到实例对象, 若为空就创建, 若不为空就返回实例
    private static SingletonDemo getInstance() {
        if (singleton == null) {
            singleton = new SingletonDemo();
        }
        return singleton;
    }
}
```

上面的代码只适用于单线程环境下，并发场景下的单例模式如下所示：
```java
public class SingletonDemo {
	//通过声明volatile字段保证可见性, 避免出现获取到的实例为旧值的情况
    private static volatile SingletonDemo singleton;

    private SingletonDemo() {
    }

    private static SingletonDemo getInstance() {
        //避免实例不为空时进入同步块争夺锁
        if (singleton == null) {
            //对SingletonDemo.class进行加锁, 避免出现多个线程同时进行初始化的问题
            synchronized (SingletonDemo.class) {
                if (singleton == null) {
                    singleton = new SingletonDemo();
                }
            }
        }
        return singleton;
    }
}
```
代码使用了synchronized块保证了一致性，那么为什么还要将singleton声明为volatile呢？这是因为当线程尝试获取的singleton不为空时，我们不希望线程进入synchronized块进行锁争夺，只需要直接进行返回即可。那么就需要通过volatile保证一致性，避免获取到旧值的情况。