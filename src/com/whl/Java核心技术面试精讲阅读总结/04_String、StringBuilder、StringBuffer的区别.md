### String

在之前final的分析中，我提到过String是典型的immutable类的实现，如果涉及到对字符串的修改动作，采用的是CopyOnWrite模式 —— 创建新的字符串并在此基础上进行修改操作返回。

并且String为了避免在创建同一个字符串对象时，重复创建相同的字符串对象，String采用了享元模式（常量池）来优化。例如，创建两个内容相同的字符串，如果采用 new 的方式那么返回的对象是不相同的：

```java
String a = "son of bc";
String b = new String("son of bc");
//a != b 
```

而以下三种创建字符串的操作，都是尝试从常量池中获取，而不是直接新建字符串对象：

```java
String a = "son of bc";// 通过直接赋值的方式，放入字符串常量池（或者直接从常量池中取出）

String b = "son of bc";// 从常量池中获取
String c = String.valueOf("son of bc");
String d = new String("son of bc").intern();// 若常量池中存在那么返回常量池中的引用, 若不存在则取堆中查找; 若堆中存在则将堆中字符串对象的引用放入常量池并返回, 若不存在则在堆中新建字符串对象并将其引用放入常量池并返回

```

其实从创建对象的角度来看，这也是一种单例与多例的实现。如果是通过 new 创建则是多例创建；如果是通过常量池，则是单例创建。设计模式就真无处不在...



### StringBuilder与StringBuffer

对于String对象的修改操作是基于CopyOnWrite实现的，而频繁的创建对象势必会影响到性能，因此 Java 提供了专门用于字符串修改的类 —— StringBuilder、StringBuffer。其实二者除了线程是否安全的特性，都没有太大的区别。至于这俩为何能够原地（不创建新字符串）进行修改字符串的操作，其实稍微思考下就能猜到，那多半就是底层用于存储字符串的 char[] array 不声明为final就行了。

其实StringBuilder与StringBuffer也确实是这样做的，并且它俩的设计是采用了模版方法设计模式 —— 都继承自AbstractStringBuilder类，并且大部分对于字符数组的操作都在AbstractStringBuilder类中实现好了，具体可以源码中自行查看。

**除此以外，Java 中 String字符串的拼接操作真的就跟我们看上去那样么？**

```java
public static String concat(String str) {
    return str + "whl" + "1998";
}
```

如果从表象上来看，上面这个字符串的拼接操作会先创建两个String对象，然后再新建一个字符串对象保存两个字符串的合并字符，伪代码如下所示：

```java
String a = String.valueOf("whl");
String b = String.valueOf("1998");
new String(
  	//内部创建字符数组长度 == 7, 将String a, String b 同时写入字符数组
    ...
           );
```

但是实际上，Java 在编译器将这段字符串拼接操作自动转换为了StringBuilder的拼接操作，并且 "whl" + "1998" 这个操作也直接被转换为了一个字符串 "whl1998" ，我们可以查看反编译的指令验证：

```jvm
0 : new            #2                 // 初始化
3 : dup
4 : invokespecial  #3                 // 调用构造方法StringBuilder构造方法
7 : aload_0							  // 读取方法参数
8 : invokevirtual  #4                 // 执行StringBuilder.append()操作, 拼接参数字符串
11: ldc            #5                 // 获取待拼接字符串"whl1998"
13: invokevirtual  #4                 // 执行StringBuilder.append()操作, 拼接"whl1998"
16: invokevirtual  #6                 // 调用toString()将结果转换为字符串
19: areturn							  // 返回结果
```



### 为什么String需要被声明为final

基本上所有人都知道String类是final类型的，但为什么String需要被声明为final呢？答案并不仅仅局限在 “基础类为了保证安全性需要声明为final”，而且涉及到了常量池、线程安全、以及HashCode的不可变性。

**只有当常量池中的字符串是不可变的，常量池才有存在的意义。**你可以设想，当常量池中存储的都是可变字符串的引用，那么当我们获取某个引用时，该引用指向的字符串对象总是会莫名其妙发生变化，这种结果是不堪设想的。

**因为字符串是不可变的，所以是天然线程安全的。**“多个线程同时对同一个共享变量进行读写操作时会出现线程安全问题”，这意味着只读不写没有线程安全问题。

**因为字符串是不可变的，所以在它创建的时候HashCode就被缓存了，不需要重新计算。**这也是为什么HashMap中的key往往是String字符串类型，要是key变来变去，hashcode也随之变来变去，结点在Map中存储的位置岂不是乱套了。



总结下来，String类的分析主要从下面几个方面入手：

1. final关键字的作用
2. 常量池的概念
3. CopyOnWrite模式
4. 与StringBuilder、StringBuffer之间的差异性