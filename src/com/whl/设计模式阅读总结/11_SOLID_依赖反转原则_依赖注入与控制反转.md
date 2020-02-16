### 控制反转IoC

对于Java工程师，控制反转这个名字常见与spring框架，但是千万别把IoC与spring绑定了，IoC是一种设计思想，而spring也只不过是采用了这个设计思想。下面通过一个代码示例了解什么是控制反转：

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

上述这个测试代码中，所有的流程都由程序员控制，但如果抽象出一个框架，将流程交给框架控制，就能够达到控制反转的目的：

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
JunitApplication.register(new UserServiceTest();
```

上述代码中就是典型的，通过框架来实现“控制反转”的例子，测试逻辑的执行交给框架处理而不是由程序员手动处理。框架提供了一个可扩展的代码骨架，用于组装对象、并管理整个执行流程。程序员利用框架进行开发时，只需要往预留的扩展点添加业务相关的代码（例如上述框架中的doTest抽象方法），就可以通过框架驱动整个程序执行。



### 依赖注入DI

依赖注入，一句话来说就是不通过new()的方式在类内部创建依赖的对象，而是将依赖的对象在外部创建好之后，通过构造函数或其他方式注入给类使用。

下面代码中，Notification类负责消息推送，依赖类MessageSender类实现推送消息给用户。下面分别用依赖注入和非依赖注入实现，代码如下：

```java
//非依赖注入实现方式--------------------
public class Notification {
    private MessageSender messageSender;

    public Notification() {
        this.messageSender = new MessageSender();//Notification内部创建massageSender
    }

    public void sendMessage(String cellphone, String message) {
        //...省略校验逻辑等
        this.messageSender.send(cellphone, message);
    }
}

public class MessageSender {
    public void send(String cellphone, String message) {
        //...
    }
}

//使用notification
Notification notification = new Notification();

//依赖注入的实现方式-------------------
public class Notification {
    private MessageSender messageSender;

    // 通过构造函数将messageSender注入, 这里的MessageSender来自Notification外部
    public Notification(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendMessage(String cellphone, String message) {
        //...省略校验逻辑等...
        this.messageSender.send(cellphone, message);
    }
}

//使用Notification
MessageSender messageSender = new MessageSender();
Notification notification = new Notification(messageSender);
```

通过依赖注入的实现方式能够提高代码的可扩展性，我们可以灵活地替换依赖的类。当然上述代码还有优化空间，可以将MessageSender定义为接口，利用多态的特性实现更灵活的替换，例如要从MessageSender的类型要从SmsSender转换为InboxSender，利用多态的特性，并不需要在Notification的基础上进行代码修改：

```java
public class Notification {
    private MessageSender messageSender;

    public Notification(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void sendMessage(String cellphone, String message) {
        this.messageSender.send(cellphone, message);
    }
}

public interface MessageSender {
    void send(String cellphone, String message);
}

// 短信发送类
public class SmsSender implements MessageSender {
    @Override
    public void send(String cellphone, String message) {
        //....
    }
}

// 站内信发送类
public class InboxSender implements MessageSender {
    @Override
    public void send(String cellphone, String message) {
        //....
    }
}

//使用Notification
MessageSender messageSender = new SmsSender();
Notification notification = new Notification(messageSender);
```



### 依赖注入框架

刚才的例子中，采用依赖注入的Notification虽然不需要在类的内部手动创建MessageSender对象，但是这个创建对象、组装（注入）的过程也仅仅是被移动到了上层代码而已，还是需要手动实现：

```java
public class Demo {
    public static final void main(String args[]) {
        MessageSender sender = new SmsSender(); //创建对象
        Notification notification = new Notification(sender);//依赖注入
        notification.sendMessage("13918942177", "短信验证码：2346");
    }
}
```

而在实际的软件开发中，一个项目涉及到成百上千个类，对象的创建和依赖注入会非常复杂，如果这部分的工作都是依赖程序员自己写代码完成，那么容易出错且开发成本也较高。而对象创建、依赖注入这个过程与业务无关，完全可以抽象为框架来自动完成。

实际上，依赖注入框架有很多，例如Google Guide、Spring、Pico Container等，但Spring声称自己是控制反转容器。这种说法并没有错，但是spring的控制反转主要还是通过依赖注入来实现的。



### 依赖反转原则DIP

Dependency Inversion Principe，DIP，依赖反转原则描述了：高层模块不要依赖低层模块，且高层模块和低层模块应该通过抽象互相依赖，除此以外，抽象不要依赖具体实现，具体实现依赖抽象。

这里的高层模块指的是调用者，低层指被调用者。在平时的业务代码中，高层依赖低层是没有任何问题的，这条原则主要还是针对框架层面的设计。例如：Tomcat是运行Java Web程序的容器，我们编写的Web程序只需要部署在Tomcat容器下就可以被Tomcat执行调用。那么Tomcat属于高层调用者，Web程序就是低层被调用者。Tomcat与程序代码之间并没有直接依赖关系，而是二者都依赖同一个“抽象”——Servlet规范。Servlet规范不依赖于具体Tomcat和Web程序的实现细节，而Tomcat和应用程序依赖Servlet。

