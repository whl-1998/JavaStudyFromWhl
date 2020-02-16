### 如何理解开闭原则

开闭原则主要是指“对扩展开放、对修改关闭”，开闭原则是SOLID原则中最难理解、也是最难掌握、同时也是最有用的一条原则。那么什么样的代码改动才能被定义为“扩展”和“修改”呢？怎样才算满足或违反开闭原则呢？

开闭原则的详细表述是，增加一个新的功能应该在已有的代码基础上扩展代码，而非修改已有代码。下面通过一个API接口监控告警的代码示例，进一步解释开闭原则:

```java
public class Alert {
    private AlertRule rule;//告警规则
    private Notification notification;//通知渠道

    public Alert(AlertRule rule, Notification notification) {
        this.rule = rule;
        this.notification = notification;
    }

    public void check(String api, long requestCount, long errorCount, long durationOfSeconds) {
        long tps = requestCount / durationOfSeconds;
        //当tps超过某个阈值, 触发告警
        if (tps > rule.getMatchedRule(api).getMaxTps()) {
            notification.notify(NotificationEmergencyLevel.URGENCY, "...");
        }
        //当接口请求出错, 触发告警
        if (errorCount > rule.getMatchedRule(api).getMaxErrorCount()) {
            notification.notify(NotificationEmergencyLevel.SEVERE, "...");
        }
    }
}
```

现在如果需要新增一个功能，当每秒钟接口请求参数超过阈值时，也需要触发告警，那么可以进行如下更改，在check()方法中添加对应参数，并在方法体中增加新的告警逻辑：

```java
public class Alert {
    // ...

    public void check(String api, long requestCount, long errorCount, long timeoutCount, long durationOfSeconds) {
        long tps = requestCount / durationOfSeconds;
		//当tps超过阈值, 告警
        if (tps > rule.getMatchedRule(api).getMaxTps()) {
            notification.notify(NotificationEmergencyLevel.URGENCY, "...");
        }
        //当接口请求出错, 触发告警
        if (errorCount > rule.getMatchedRule(api).getMaxErrorCount()) {
            notification.notify(NotificationEmergencyLevel.SEVERE, "...");
        }
        //当接口处理超时, 触发告警 
        long timeoutTps = timeoutCount / durationOfSeconds;
        if (timeoutTps > rule.getMatchedRule(api).getMaxTimeoutTps()) {
            notification.notify(NotificationEmergencyLevel.URGENCY, "...");
        }
    }
}
```

上面的改动是基于原有代码的基础上修改实现新功能的，并不符合开闭原则中“对修改关闭”的原则，这意味着调用这个方法的代码都需要做相应的修改。如果遵循开闭原则，如何通过“扩展”的方式实现同样的功能呢？

可以先将Alert代码进行重构，将check()函数的多个入参封装为ApiStatInfo类；并且引入handler，将if的逻辑分散在各个handler中：

```java
public class Alert {
	//用于封装不同告警功能的handler
    private List<AlertHandler> alertHandlers = new ArrayList<>();

    public void addAlertHandler(AlertHandler alertHandler) {
        this.alertHandlers.add(alertHandler);
    }

    //遍历handlers, 处理它们的check方法
    public void check(ApiStatInfo apiStatInfo) {
        for (AlertHandler handler : alertHandlers) {
            handler.check(apiStatInfo);
        }
    }
}

//封装check的入参
public class ApiStatInfo {
    private String api;
    private long requestCount;
    private long errorCount;
    private long durationOfSeconds;
}

//handler抽象类
public abstract class AlertHandler {
    protected AlertRule rule;
    protected Notification notification;
    
    public AlertHandler(AlertRule rule, Notification notification) {
        this.rule = rule;
        this.notification = notification;
    }
    
    public abstract void check(ApiStatInfo apiStatInfo);
}

//继承AlertHander, 实现"当tps超过阈值, 告警"的check()
public class TpsAlertHandler extends AlertHandler {
    public TpsAlertHandler(AlertRule rule, Notification notification) {
        super(rule, notification);
    }

    @Override
    public void check(ApiStatInfo apiStatInfo) {
        long tps = apiStatInfo.getRequestCount()/ apiStatInfo.getDurationOfSeconds();
        if (tps > rule.getMatchedRule(apiStatInfo.getApi()).getMaxTps()) {
            notification.notify(NotificationEmergencyLevel.URGENCY, "...");
        }
    }
}

//继承AlertHander, 实现"当接口请求出错, 告警"的check()
public class ErrorAlertHandler extends AlertHandler {
    public ErrorAlertHandler(AlertRule rule, Notification notification){
        super(rule, notification);
    }

    @Override
    public void check(ApiStatInfo apiStatInfo) {
        if (apiStatInfo.getErrorCount() > rule.getMatchedRule(apiStatInfo.getApi()).getMaxErrorCount()) {
            notification.notify(NotificationEmergencyLevel.SEVERE, "...");
        }
    }
}
```

下面的ApplicationContext是一个单例类，负责Alert的创建、组装（alertRule和notification的依赖注入）、初始化（添加handlers）：

```java
public class ApplicationContext {
    private AlertRule alertRule;
    private Notification notification;
    private Alert alert;

    //组装以及初始化工作
    public void initializeBeans() {
        alertRule = new AlertRule(..省略AlertRule初始化代码); 
        notification = new Notification(..省略notification初始化代码);
        //创建alert实例并添加handlers
        alert = new Alert();
        alert.addAlertHandler(new TpsAlertHandler(alertRule, notification));
        alert.addAlertHandler(new ErrorAlertHandler(alertRule, notification));
    }
    
    //获取alert实例
    public Alert getAlert() {
        return alert;
    }

    // 饿汉式单例
    private static final ApplicationContext instance = new ApplicationContext();
    
    //私有化构造器
    private ApplicationContext() {
        instance.initializeBeans();
    }
    
    //公有获取单例的方法
    public static ApplicationContext getInstance() {
        return instance;
    }
}

public class Demo {
    public static void main(String[] args) {
        //创建包含入参的实例
        ApiStatInfo apiStatInfo = new ApiStatInfo();
        ...
        //调用不同的handler来执行针对apiStatInfo的告警
        ApplicationContext.getInstance().getAlert().check(apiStatInfo);
    }
}
```

现在，基于重构之后的代码，如果要添加之前提到的新功能就能够遵循开闭原则，主要改动如下：

1. 在ApiStatInfo类中添加新的属性——timeoutCount
2. 添加一个新的handler实现类——TimeoutAlertHander，并且实现对应的check()方法
3. 在ApplicationContext的initializeBeans()方法中，初始化新的TimeoutAlertHander实例

代码如下：

```java
public class Alert { // 代码未改动... 
}
    
public class ApiStatInfo {
    private String api;
    private long requestCount;
    private long errorCount;
    private long durationOfSeconds;
    private long timeoutCount; // 改动一：添加新字段
}
    
public abstract class AlertHandler { //代码未改动... 
}
        
public class TpsAlertHandler extends AlertHandler { //代码未改动...
}
           
public class ErrorAlertHandler extends AlertHandler {//代码未改动...
}

// 改动二：添加新的handler
public class TimeoutAlertHandler extends AlertHandler {//省略代码...
}

                    
public class ApplicationContext {
    private AlertRule alertRule;
    private Notification notification;
    private Alert alert;

    public void initializeBeans() {
        alertRule = new AlertRule(...);
        notification = new Notification(...);
        alert = new Alert();
        alert.addAlertHandler(new TpsAlertHandler(alertRule, notification));
        alert.addAlertHandler(new ErrorAlertHandler(alertRule, notification));
        // 改动三：注册handler
        alert.addAlertHandler(new TimeoutAlertHandler(alertRule, notification));
    }
    ...
}

public class Demo {
    public static void main(String[] args) {
        ApiStatInfo apiStatInfo = new ApiStatInfo();
        ...
        ApplicationContext.getInstance().getAlert().check(apiStatInfo);
    }
}
```

重构之后的代码非常灵活，如果想要添加新的告警逻辑，只需要基于扩展的方式创建新的handler即可，并且也不会影响到其他调用到check()方法的类。

但是开闭原则也并不是“免费的”，在上述例子中我们对代码进行了重构，重构之后的代码相比起原本的代码要更加复杂，可读性要更低一些。而很多时候，我们需要对可读性和可扩展性进行权衡，例如，在告警信息非常多的时候可以牺牲一些代码的可读性来换取可扩展性，但是当告警信息比较少就没有必要这样大费周章地重构代码了，可以先等到要扩展方面的需求时再考虑重构。



### 支持开闭原则的一些更加具体的方法论

在众多的设计原则、思想、模式中，最常用于提高程序可扩展性的方法有：多态、依赖注入、基于接口而非实现编程，以及大部分设计模式等。这里重点讲如何利用多态、依赖注入、基于接口而非实现编程，来实现“对扩展开放、对修改关闭”。

下面通过一段代码进行解释：

```java
//消息队列接口 这一部分体现了抽象意识
public interface MessageQueue { //... 
}

//kafka消息队列实现类
public class KafkaMessageQueue implements MessageQueue { //... 
}

//RocketMQ消息队列实现类
public class RocketMQMessageQueue implements MessageQueue {//...
}

//消息格式化接口 抽象
public interface MessageFormatter { //... 
}

//Json消息格式化实现类
public class JsonMessageFormatter implements MessageFormatter {//...
}

//消息格式化实现类
public class MessageFormatter implements MessageFormatter {//...
}

//当我们需要更换消息队列时, 只需要在依赖注入时注入不同的消息队列实现即可实现多态
//当我们需要不同的消息格式化功能时, 也只需要一个sendNotification方法就能够实现不同的格式化功能
public class Demo {
    private MessageQueue msgQueue; // 基于接口而非实现编程
    
    public Demo(MessageQueue msgQueue) { // 依赖注入, 注入消息队列依赖
        this.msgQueue = msgQueue;
    }

    // msgFormatter：多态, 通过接口调用不同实现获取不同的功能
    public void sendNotification(Notification notification, MessageFormatter msgFormatter) {
        //...
    }
}
```

