Spring的基础机制就是其家喻户晓的Aop和IoC了。其中IoC主要采用了工厂模式的思想，用一个或多个工厂（取决于ApplicationContext的实例个数）负责整个程序的对象创建与管理过程。而Aop则主要采用了代理模式的思想，动态地对类中的方法进行增强，实现系统业务逻辑与系统服务（例如日志、安全等）的分离。除此以外，Spring还提供了事务管理、对象关系映射、事件机制等核心模块。



### Spring-IoC

提到IoC我们都会联想到Spring，但IoC并不是Spring独家的，很多框架都采用IoC的思想，只是我们在初学时第一次接触IoC都是通过Spring。IoC其实是一种 Inversion of Controller 原则，它要求将对象创建的控制权由我们自身转交给框架，以此达到解耦的目的。

下面通过一个代码示例解释控制反转：

```java
public class UserServiceTest {
    public static boolean doTest() {
        // ... 
    }

    public static void main(String[] args) {//这部分逻辑可以放到框架中
        if (doTest()) {
            System.out.println("Test succeed.");
        } else {
            System.out.println("Test failed.");
        }
    }
}
```

上面这段代码中，所有的测试流程都是由程序员控制，但如果将测试流程抽象为框架，那么控制权就由程序员转交给了框架，以此实现了控制反转的目的：

```java
public abstract class TestCase {
    public void run() {
        //根据方法执行是否成功, 输出不同的结果
        if (doTest()) {
            System.out.println("Test succeed.");
        } else {
            System.out.println("Test failed.");
        }
    }

    public abstract boolean doTest();
}


public class JunitApplication {
    //任务容器, 包含了测试任务的所有Bean
    private static final List<TestCase> testCases = new ArrayList<>();

    //添加测试样例到容器
    public static void register(TestCase testCase) {
        testCases.add(testCase);
    }

    //遍历容器, 执行测试逻辑
    public static final void main(String[] args) {
        for (TestCase case: testCases) {
            case.run();
        }
    }
}

//测试框架使用-----------------------

//测试类继承抽象类
public class UserServiceTest extends TestCase {
    
	//需要测试的方法逻辑
    @Override
    public boolean doTest() {
        // ... 
    }
}

// 注册操作(还可以通过配置的方式来实现)
JunitApplication.register(new UserServiceTest());
```

上述代码其实就是Junit测试框架的主要思想，将测试逻辑的执行交给框架处理而不是由程序员手动处理。由此可见，IoC的思想其实是普遍存在的。我们学习IoC不能仅仅局限于Spring的IoC逻辑，而是抽取其中最核心思想。

现在我们再回过头来看Spring的IoC，其实本质上就是将我们创建对象的逻辑转交给Spring框架处理。与Junit框架的区别仅仅在于控制反转的对象不同。



### Spring-DI

同样的，DI也不是Spring独家的。依赖注入顾名思义就是不通过 new() 的方式在类内部创建依赖的对象，而是将依赖的对象在外部创建好之后，通过构造函数或其他方式注入给类使用。

在Spring中，我们可以通过容器实现依赖注入。但即便我们不使用Spring，也依然能够通过构造函数实现依赖注入：

```java
//非依赖注入实现方式--------------------
public class Notification {
    private MessageSender messageSender;

    public Notification() {
        this.messageSender = new MessageSender();//Notification内部创建massageSender
    }
}


//依赖注入的实现方式-------------------
public class Notification {
    private MessageSender messageSender;

    // 通过构造函数将messageSender注入, 这里的MessageSender来自Notification外部
    public Notification(MessageSender messageSender) {
        this.messageSender = messageSender;
    }
}
```

上面这段代码虽然实现了依赖注入，但是创建对象、以及组装对象的过程也仅仅是被移动到了上层代码。因此我们依然要在上层代码中进行手动实现：

```java
public class Demo {
    public static final void main(String args[]) {
        MessageSender sender = new SmsSender(); //创建对象
        Notification notification = new Notification(sender);//依赖注入
    }
}
```

考虑到软件开发中，一个项目涉及到了成百上千个类，手动的创建对象以及依赖注入会变得非常复杂，因此才选择将这部分的工作抽象为框架自动完成。比如Spring的IoC容器在负责对象创建的同时，也会根据注解自动实现依赖注入的功能，可以说是非常方便了。



上述内容中，分析了IoC与DI这两种设计思想。如果今后面试中提到IoC与DI，千万不要局限在Spring层面上了，一定要结合设计思想来回答。



### Spring-Aop

Spring-Aop是Spring提供的面向切面编程机制，它可以分离系统业务逻辑和系统服务（日志、安全），实现了业务与其他逻辑的解耦，提高了代码的复用性。

Aop的使用场景一般是对某个功能进行增强，比如：当你开发一个登陆功能时，你需要在用户登陆前后执行权限校验，并将校验信息写入到日志文件中。如果你将校验逻辑都混杂在登陆功能的逻辑中，不满足SOLID原则中的单一职责原则不说，倘若另一个模块的某个功能也需要在前后执行权限校验，那么还得将校验逻辑复制粘贴，这又降低了代码的复用性。

这时，我们可以考虑将业务逻辑无关的代码都抽取出来，并且在程序运行时，在需要这部分逻辑的位置（连接点）进行动态插入。而这种方式的实现，也就是Spring提供的面向切面编程。

SpringAop有如下几个概念：

1. 通知：指定在某个时间点（比如方法被调用前、方法执行结束后等）对方法进行增强。通知有如下5种：
   * Before：在方法调用前增强。
   * After：在方法执行完成后增强，无论是否成功返回。
   * After-returning：在方法成功执行且返回之后增强。
   * After-throwing：在方法抛出异常后增强。
   * Around：在方法调用前、后进行增强。
2. 切入点：也就是所谓的原始类中需要被增强的方法。
3. 连接点：比如方法调用前、方法返回后、抛出异常后。
4. 切面：切入点和通知的集合，一般单独作为一个类，共同定义关于切面的全部内容。
5. 织入：在程序运行时，动态增强方法的这个过程。
6. 引用：允许我们向现有的类添加新的方法或属性。

Spring对Aop的实现同时支持了CGLIB，AspectJ，JDK动态代理，当需要增强的类实现了接口时，Spring会默认采用JDK动态代理，否则采用CGLIB。

下面我们通过一组代码示例具体看看Spring Aop的操作流程：

首先是主题接口：

```java
public interface User {
    
 	void login();
    
    void download();
}
```

实现类：

```java
public class UserImpl implements User {
    
 	public void login() {
    	...
    }
    
    public void download() {
    	...
    }
}
```

假设我们需要对login()和download()增强，login()需要在登陆之前进行登陆校验工作；download()需要在执行前进行权限校验工作。

现在我们需要定义切面类，切面中包含了切点与通知：

```java
public class PermissionVerification {

	public void verifyLogin() {//登陆校验
		...
	}
	
	public void saveMessage() {//权限校验
		...
	}
	
}
```

定义SpringAop.xml文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:tx="http://www.springframework.org/schema/tx"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans-4.2.xsd
        http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop-4.2.xsd">
        
        <bean id="userImpl1" class="xxx.UserImpl" />
        <bean id="userImpl2" class="xxx.UserImpl" />
        <bean id="PermissionVerification" class="xxx.PermissionVerification" />
        <aop:config>
        <!-- 定义一个切面, 切面是切点和通知的集合-->
            <aop:aspect id="do" ref="PermissionVerification">
            	<!-- 定义切点, 后面是expression语言, 表示包括该接口中定义的所有方法都会被执行-->
                <aop:pointcut id="point" expression="execution(* xxx.User.*(..))" />
                <aop:before method="verifyLogin" pointcut-ref="point" />
                <aop:before method="saveMessage" pointcut-ref="point" />
            </aop:aspect>
        </aop:config>
</beans>
```

