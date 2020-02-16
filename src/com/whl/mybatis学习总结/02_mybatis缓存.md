### Mybatis缓存

我们在通过mybatis执行select的sql语句时，需要连接数据库，这是一个比较耗时的操作，因此mybatis给我们提供了查询缓存，将select执行后将结果写到缓存，如果下次执行相同的select语句时就通过缓存来获取。

Mybatis系统中默认定义了两级缓存：一级缓存、二级缓存，默认情况下只采用一级缓存（SqlSession级别缓存，也称为本地缓存）。二级缓存需要手动开启和配置，它是基于namespace级别的缓存，也就是一个mapper下的所有select操作都会采用缓存。

**一级缓存测试：**

我们可以在一次SqlSession会话中执行两次同样的select语句，如果获取到相同的对象则可以断定第二次select是通过mybatis一级缓存获取的：

```java
@Test
public void test() {
    try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
        //获取到UserDao对象
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        User user = userDao.selectUser(1);
        User user2 = userDao.selectUser(1);
        System.out.println(user == user2);
    }
}
```

执行结果：

```
Opening JDBC Connection
Created connection 257513673.
Setting autocommit to false on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@f5958c9]
==>  Preparing: select * from user where id = ?; 
==> Parameters: 1(Integer)
<==    Columns: id, username, password
<==        Row: 1, whl1, 123
<==      Total: 1
true		#返回ture, 说明select获取到的对象是从缓存中获取到的
Resetting autocommit to true on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@f5958c9]
Closing JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@f5958c9]		#关闭JDBC连接
Returned connection 257513673 to pool.		#将连接返回到连接池
```

Mybatis中的缓存会在写操作执行后自动刷新，并且在缓存已满（1024个引用）时通过清除策略清除不需要的缓存。除此以外，mybatis也会不定时进行刷新缓存。我们可以根据如下代码测试mybatis的自动刷新：

```java
@Test
public void test() {
    try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
        //获取到UserDao对象
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        User user = userDao.selectUser(1);
        userDao.updateUser(new User(2, "whl999", "19980514"));//修改id为2的列表, 与查寻的id=1的列表无关
        User user2 = userDao.selectUser(1);
        System.out.println(user == user2);
    }
}
```

在执行后能够发现，两次select返回的对象并不是同一个：

```java
Opening JDBC Connection
Created connection 257513673.
Setting autocommit to false on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@f5958c9]
==>  Preparing: select * from user where id = ?; 
==> Parameters: 1(Integer)
<==    Columns: id, username, password
<==        Row: 1, whl1, 123
<==      Total: 1
==>  Preparing: update user set username = ?; 
==> Parameters: whl999(String)
<==    Updates: 3
==>  Preparing: select * from user where id = ?; 
==> Parameters: 1(Integer)
<==    Columns: id, username, password
<==        Row: 1, whl999, 123
<==      Total: 1
false		#返回false, 两次select返回的对象不是同一个
Rolling back JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@f5958c9]
Resetting autocommit to true on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@f5958c9]
Closing JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@f5958c9]
Returned connection 257513673 to pool.
```

也可以手动清理缓存：

```java
 @Test
public void test() {
    try (SqlSession sqlSession = MybatisUtils.getSqlSession()) {
        //获取到UserDao对象
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        User user = userDao.selectUser(1);
        sqlSession.clearCache();//清除缓存
        User user2 = userDao.selectUser(1);
        System.out.println(user == user2);
    }
}
```



Mybatis缓存可以自定义清除策略，可用的策略有：

1. LRU缓存，移除最长时间不被使用的对象，这也是mybatis的默认缓存移除策略。
2. FIFO，按照对象进入缓存的顺序移除
3. SOFT，软引用，基于垃圾回收状态和软引用规则移除对象
4. WEAK，弱引用，更积极地基于垃圾收集器状态和弱引用规则移除对象



