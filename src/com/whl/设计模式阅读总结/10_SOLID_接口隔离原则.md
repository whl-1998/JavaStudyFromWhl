Interface Segregation Principle，ISP，接口隔离原则，描述了客户端不应该强迫依赖它不需要的接口。其中客户端，可以理解为接口的调用者。而理解接口隔离原则的核心，就是理解其中“接口”的含义，我们可以把接口理解为下面三种东西：

1. 一组API接口集合
2. 单个API接口或者函数
3. OOP中的接口概念



### 把“接口”理解为一组API接口的集合

例如，用户系统提供的与用户相关的API代码：

```java
public interface UserService {
    boolean register(String cellphone, String password);
    boolean login(String cellphone, String password);
    UserInfo getUserInfoById(long id);
    UserInfo getUserInfoByCellphone(String cellphone);
}

public class UserServiceImpl implements UserService {
    //...
}
```

现在系统需要实现删除用户的功能，此时应该如何做呢？我们可能会在UserService中直接新增一个deleteUserById()的抽象方法，然后在实现类中实现该方法即可。这个方法非常简单，但是也隐藏了一些隐患。

删除用户是一个非常慎重的操作，考虑到安全因素，我们最好还是将其放在一个独立的接口中，避免接口在UserService被误调用导致的用户被删除的可能。下面代码中，我们创建了一个新的接口RestrictedUserService，其中包含了一些安全敏感的抽象方法，避免被误调用：

```java

public interface UserService {
    boolean register(String cellphone, String password);
    boolean login(String cellphone, String password);
    UserInfo getUserInfoById(long id);
    UserInfo getUserInfoByCellphone(String cellphone);
}

public interface RestrictedUserService {
    boolean deleteUserByCellphone(String cellphone);
    boolean deleteUserById(long id);
}

public class UserServiceImpl implements UserService, RestrictedUserService {
    // ...省略实现代码...
}
```

上述例子中，我们把接口隔离中的“接口”理解为一组接口集合，如果部分接口只被部分调用者使用，例如上述例子中，删除的接口只需要被后台管理系统调用，那么就需要将这部分接口隔离出来，单独给对应的调用者使用。



### 把“接口”理解为单个API或函数

函数的设计功能要单一，不要将多个不同功能逻辑在同一个函数中实现，有点类似单一职责原则：

```java
public class Statistics {
    private Long max;
    private Long min;
    private Long average;
    private Long sum;
    private Long percentile99;
    private Long percentile999;
    //...省略constructor/getter/setter等方法...
}

public Statistics count(Collection<Long> dataSet) {
    Statistics statistics = new Statistics();
    //...省略计算逻辑...
    return statistics;
}
```

例如上述代码中，count()函数的职责就不够单一，它可以计算最大值、最小值、平均数等，按照接口隔离原则，我们应该把count()函数拆分为更小粒度的函数：

```java
public Long max(Collection<Long> dataSet) { //... 
}

public Long min(Collection<Long> dataSet) { //... 
}

public Long average(Colletion<Long> dataSet) { //... 
}

// ...省略其他统计函数...
```

其实接口隔离原则与单一职责原则比较相似，具备强烈的主观性，例如上述代码中，如果存在某个业务对Statistics中定义的统计信息都有涉及，那么count()的设计也是合理的。但如果有的业务只需要最大值、最小值，有的业务只需要平均数和总和值，那么count()的设计就会导致每次执行都会有很多无用功的计算，最好还是拆分为更细粒度的函数。

接口隔离原则和单一职责的区别在于，单一职责原则针对的是模块、类、接口的设计，而接口隔离原则更加侧重于接口的设计，思考的角度不同。



### 把“接口”理解为OOP中接口的概念

假设项目中用到了三个外部系统，Redis、Mysql、Kafka，每个系统都对应一系列配置信息，为了在内存中存储这些配置信息，分别设计了三个Configuration类，代码如下所示：

```java
//Redis配置类
public class RedisConfig {
    private ConfigSource configSource; //配置中心（比如zookeeper）
    private String address;
    private int timeout;
    private int maxTotal;
    //省略其他配置: maxWaitMillis,maxIdle,minIdle...

    public RedisConfig(ConfigSource configSource) {
        this.configSource = configSource;
    }

    public String getAddress() {
        return this.address;
    }
    //...省略其他get()、init()方法...

    public void update() {
      //从configSource加载配置到address/timeout/maxTotal...
    }
}

public class KafkaConfig { //...省略... 
}

public class MysqlConfig { //...省略... 
}
```

现在有一个新的需求，希望支持Redis、Kafka配置的热更新，如果在配置中心更改了配置信息，我们希望不用重启就能将最新的配置信息加载到内存，但是不希望Mysql支持热更新。

为了实现这样的需求，设计实现了一个ScheduleUpdate类，以固定时间频率调用RedisConfig、KafkaConfig的update方法更新配置信息，如下所示：

```java
public interface Updater {
    void update();
}

public class RedisConfig implements Updater {
    //...省略其他属性和方法...
    
    //实现update方法, 以固定频率更新配置信息
    @Override
    public void update() { //... 
    }
}

public class KafkaConfig implements Updater {
    //...省略其他属性和方法...
    
    @Override
    public void update() { //... 
    }
}

public class MysqlConfig { //...省略其他属性和方法... 
}

//用于执行热更新, 采用线程池实现
public class ScheduledUpdater {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private long initialDelayInSeconds;
    private long periodInSeconds;
    private Updater updater;

    public ScheduleUpdater(Updater updater, long initialDelayInSeconds, long periodInSeconds) {
        this.updater = updater;
        this.initialDelayInSeconds = initialDelayInSeconds;
        this.periodInSeconds = periodInSeconds;
    }

    public void run() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updater.update();
            }
        }, this.initialDelayInSeconds, this.periodInSeconds, TimeUnit.SECONDS);
    }
}

//执行类
public class Application {
    ConfigSource configSource = new ZookeeperConfigSource(/*省略参数*/);
    public static final RedisConfig redisConfig = new RedisConfig(configSource);
    public static final KafkaConfig kafkaConfig = new KakfaConfig(configSource);
    public static final MySqlConfig mysqlConfig = new MysqlConfig(configSource);

    public static void main(String[] args) {
        //执行热更新redis配置
        ScheduledUpdater redisConfigUpdater = new ScheduledUpdater(redisConfig, 300, 300);
        redisConfigUpdater.run();
		//执行热更新kafka配置
        ScheduledUpdater kafkaConfigUpdater = new ScheduledUpdater(kafkaConfig, 60, 60);
        redisConfigUpdater.run();
    }
}
```

到此，热更新的需求就实现了。现在如果有一个新的监控功能需求，需要通过更方便的方式查看Zookeeper的配置信息，那么可以在项目中开发一个内嵌的SimpleHttpServer，输出项目的配置信息到一个固定的Http地址，例如 

http://127.0.0.1:2389/config ，我们只需要通过浏览器输入这个地址就能够获取到配置信息。但是出于某些原因，只能暴露mysql和redis的配置信息，不能暴露kafka的配置信息。

为了实现这个功能，还需要对上述代码进行进一步改造：

```java
public interface Updater {
    void update();
}

public interface Viewer {
    String outputInPlainText();
    Map<String, String> output();
}

//redis支持http返回配置信息、热更新
public class RedisConfig implemets Updater, Viewer {
    //...省略其他属性和方法...

    @Override
    public void update() { //... 
    }

    @Override
    public String outputInPlainText() { //... 
    }
    
    @Override
    public Map<String, String> output() { //...
    }
}

//kafka只支持热更新
public class KafkaConfig implements Updater {
    //...省略其他属性和方法...

    @Override
    public void update() { //... 
    }
}

//mysql只支持http返回配置信息
public class MysqlConfig implements Viewer {
    //...省略其他属性和方法...
    @Override
    public String outputInPlainText() { //... 
    }
    @Override
    public Map<String, String> output() { //...
    }
}

//用于执行热更新, 采用线程池实现
public class ScheduledUpdater {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private long initialDelayInSeconds;
    private long periodInSeconds;
    private Updater updater;

    public ScheduleUpdater(Updater updater, long initialDelayInSeconds, long periodInSeconds) {
        this.updater = updater;
        this.initialDelayInSeconds = initialDelayInSeconds;
        this.periodInSeconds = periodInSeconds;
    }

    public void run() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updater.update();
            }
        }, this.initialDelayInSeconds, this.periodInSeconds, TimeUnit.SECONDS);
    }
}

//将配置信息通过Http返回
public class SimpleHttpServer {
    private String host;
    private int port;
    private Map<String, List<Viewer>> viewers = new HashMap<>();

    public SimpleHttpServer(String host, int port) { //...
    }

    public void addViewers(String urlDirectory, Viewer viewer) {
        if (!viewers.containsKey(urlDirectory)) {
            viewers.put(urlDirectory, new ArrayList<Viewer>());
        }
        this.viewers.get(urlDirectory).add(viewer);
    }

    public void run() { //... 
    }
}

public class Application {
    ConfigSource configSource = new ZookeeperConfigSource();
    public static final RedisConfig redisConfig = new RedisConfig(configSource);
    public static final KafkaConfig kafkaConfig = new KakfaConfig(configSource);
    public static final MySqlConfig mysqlConfig = new MySqlConfig(configSource);
    
    public static void main(String[] args) {
        ScheduledUpdater redisConfigUpdater = new ScheduledUpdater(redisConfig, 300, 300);
        redisConfigUpdater.run();
        
        ScheduledUpdater kafkaConfigUpdater = new ScheduledUpdater(kafkaConfig, 60, 60);
        redisConfigUpdater.run();
        
        SimpleHttpServer simpleHttpServer = new SimpleHttpServer(“127.0.0.1”, 2389);
        simpleHttpServer.addViewer("/config", redisConfig);
        simpleHttpServer.addViewer("/config", mysqlConfig);
        simpleHttpServer.run();
    }
}
```

可以发现，ScheduleUpdater只依赖Updater这个热更新相关的接口，不需要去依赖Viewer接口，满足接口隔离原则。同样的，SimpleHttpServer也只依赖查看信息相关的Viewer接口，不依赖Updater接口，也满足接口隔离原则。

那么不遵守接口隔离原则的代码是什么样的呢？我们可以将View、Updater合并为一个Config接口，让RedisConfig、KafkaConfig、MysqlConfig都实现它，这样会出什么问题呢，代码如下：

```java
//合并Viewer、Updater
public interface Config {
    void update();
    String outputInPlainText();
    Map<String, String> output();
}

public class RedisConfig implements Config {
    //...需要实现Config的三个接口update/outputIn.../output
}

public class KafkaConfig implements Config {
    //...需要实现Config的三个接口update/outputIn.../output
}

public class MysqlConfig implements Config {
    //...需要实现Config的三个接口update/outputIn.../output
}

//热更新
public class ScheduledUpdater {
    //...省略其他属性和方法..
    private Config config;

    public ScheduleUpdater(Config config, long initialDelayInSeconds, long periodInSeconds) {
        this.config = config;
        //...
    }
    //...
}

//Http显示配置
public class SimpleHttpServer {
    private String host;
    private int port;
    private Map<String, List<Config>> viewers = new HashMap<>();

    public SimpleHttpServer(String host, int port) {//...
    }

    public void addViewer(String urlDirectory, Config config) {
        if (!viewers.containsKey(urlDirectory)) {
            viewers.put(urlDirectory, new ArrayList<Config>());
        }
        viewers.get(urlDirectory).add(config);
    }

    public void run() { //... 
    }
}
```

这种实现虽然也能够工作，但是还是存在很多问题：

1. 我们要求KafkaConfig只支持热更新，并不需要实现output()，MysqlConfig只支持http返回配置信息，并不需要实现update()，但是合并的Config接口却要求KafkaConfig、MysqlConfig必须实现这些方法。除此以外，如果Config接口中新增了接口，那么所有的实现类还要改动。
2. 第一种设计方式思路更加灵活、易扩展、易复用，例如我们再新增一个性能统计模块，也仅仅只需要新增一个Metrics功能接口，然后设计执行的类即可，并不需要修改原有的代码。

