### 什么是 MyBatis？

MyBatis 是一款优秀的持久层框架，所谓持久层也就是程序中负责持久化的部分。而持久化简单来说就是将程序的数据写入到数据库。我们可以把持久化的过程理解为写日记，我们脑子里的记忆就是内存，内存断电及失，记忆也并不能一直存在，俗话说好记性不如烂笔头，把记忆写在日记本上才能永久保存，这里的日记本就是数据库（硬盘）。

mybatis主要做的事就是：

1. 简化 JDBC 的代码。就好比填表时，表结构已经被打印好了，我们只需要在表上写关键信息即可。同样的，mybatis框架帮我们把重复的部分都做好了，我们只需要关心业务逻辑就好。
2. sql语句和代码分离，解耦这种特性在开发中是非常重要的。
3. 可以使用简单的 xml 或注解来配置或映射基础数据类型、接口和 Java 的 pojo 为数据库中的记录



### 通过maven构建一个简单的mybatis程序

首先，既然是持久化框架，那么必须要有操作的数据库，下面是mysql的建表语句：

```mysql
create table `user` (
	`id` int(20) primary key,
	`username` varchar(50),
	`password` varchar(50)
) ENGINE = innodb default charset = utf8;
```

其次，需要在 pom.xml 中导入相关的依赖：

```xml
<dependencies>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.6</version>
    </dependency>

    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.12</version>
    </dependency>

    <dependency>
        <groupId>org.mybatis</groupId>
        <artifactId>mybatis</artifactId>
        <version>3.5.3</version>
    </dependency>
</dependencies>
```

然后就是核心步骤了，此时需要构建SqlSessionFactory。

每个基于Mybatis的应用，核心都基于SqlSessionFactory。SqlSessionFactory顾名思义就是构建SqlSession的工厂，这里就采用了工厂模式。而SqlSessionFactory对象的实例则需要通过SqlSessionFactoryBuilder获取，这里是采用的构建器模式，它可以从 mybatis-config.xml 配置文件或定制的 Configuration 实例构建SqlSessionFactory的实例。例如下列代码就是通过 mybatis-config.xml 配置文件构建SqlSessionFactory的示例：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis?useUnicode=true&amp;characterEncoding=utf8&amp;serverTimezone=UTC"/>
                <property name="username" value="root"/>
                <property name="password" value="123456"/>
            </dataSource>
        </environment>
    </environments>

    <!-- 每个 Mapper.xml 都需要在 mybatis-config.xml 中注册 -->
    <mappers>
        <mapper resource="mapper/...Mapper.xml"/>
    </mappers>
</configuration>
```

```java
public class MybatisUtils {
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            //配置文件的classpath
            String resource = "mybatis-config.xml";
            //读取配置文件流
            InputStream inputStream = Resources.getResourceAsStream(resource);
            //通过传入配置文件流获取sqlSessionFactory对象
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取SqlSession静态方法
    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }
}
```

可以看到，上述工具类中包含一个返回SqlSession实例的静态方法。SqlSession类似于JDBC的Connection，它包含了面向数据库执行sql语句的所有方法。

之前也提到过，mybatis可以将sql语句和代码分离，mybatis中sql语句都是放在xml文件中的，它需要绑定一个接口，并实现接口中的方法，例如下面代码所示：

```java
public interface UserDao {
    User selectUser(int id);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!-- 绑定接口 -->
<mapper namespace="com.dao.UserDao">
    <!-- 实现接口方法
		 如果返回的是List类型, 只需要指定List泛型中的类型即可
 		 指定参数类型为int
	-->
    <select id="selectUser" resultType="com.pojo.User" parameterType="type">
    	<!-- sql语句 -->
        select * from user where id = #{id};
    </select>
</mapper>
```

到这里准备工作就全部结束了，就差测试了：

```java
public class UserDaoTest {
    @Test
    public void test() {
        // try-with-resource 获取SqlSession对象
        try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
            // 通过sqlSession获取到UserDao实例
            UserDao userDao = sqlSession.getMapper(UserDao.class);
            // 执行方法
            // 这里提一下, 这里就是典型的面向对象中的抽象特性, 调用时只需要关注方法的抽象即可
            User user = userDao.selectUser(1);
			
            // 第二种执行sql的方式, 不常用            
            // User user = sqlSession.selectOne("com.dao.UserDao.selectUser", 1);
        }
    }
}
```



### 作用域和生命周期

#### SqlSessionFactoryBuilder

SqlSessionFactoryBuilder采用了构建器模式，当利用SqlSessionFactoryBuilder构建好一个SqlSessionFactory时它的生命周期就结束了。因此SqlSessionFactoryBuilder最好还是作为局部变量放在方法中，生命周期随着方法执行完成结束。

#### SqlSessionFactory

SqlSessionFactory采用了工厂模式和单例模式，它只需要一个实例，并且会随着应用的运行一直存在，用于创建执行sql语句的SqlSession。

#### SqlSession

SqlSession的实例不是线程安全的，每个线程都应该拥有属于自己的 SqlSession 实例，因此我们可以采用ThreadLocal的方式将SqlSession作为线程本地变量分配给各个线程，也可以通过线程池的方式，在执行任务时以局部变量的方式创建SqlSession。只要注意别把SqlSession作为类属性就可以了，不然很可能造成并发问题。

正确的使用方式，例如Web应用中，每次收到一个 HTTP请求就创建一个SqlSession用于执行sql并返回响应。需要注意的是，SqlSession在使用后必须手动关闭，最好的做法是try-with-resource或try-finnally。