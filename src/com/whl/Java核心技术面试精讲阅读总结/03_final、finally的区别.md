其实final、finally关键字并没有什么太大的联系。（或许仅仅是因为长得像，初学者容易弄混？）



### final

简单来说 final 用于修饰类、方法、变量，使类不可被继承、方法不可被重写、变量不可被修改。听起来是蛮简单的，但 final 的存在意义远远没有我们看上去那么简单。

在多线程编程模式中有一种被称为 “不变模式” 的线程安全方案，其核心就是通过 final 关键字实现的。“只有当多个线程同时读写同一变量时，才会存在并发问题”，也就是说，通过final使一个共享变量只允许读、不允许修改，就可以实现无锁并发安全。

但需要注意的是，对共享变量设置为 final 并不一定就能保证它的不变性。final修饰变量的语义是：当变量被赋值后就不允许二次赋值了。但如果这个变量是一个集合类，比如ArrayList，在被声明为final之后我们依然可以对其进行操作：

```java
final class Bar {
    final ArrayList array = new ArrayList();

    void addArr(int a) {
		array.add(a);
    }
}
```

因此在使用不变模式时，一定要明确不变性的边界。

除此以外，java.lang包下很多类也都是被声明为final的，第三方类库中的一些基础类也同样如此。这样做的目的是保证核心类不可被继承，避免使用者以继承重写的方式更改基础功能，某种程度上，这也是保证平台安全的必要手段。

聊到这里其实有一个非常经典的问题 —— **我们在初学编程时就应该知道String类是final类型的，并且也多次强调了String类的不可变性，但在String类的 API 中却存在一个 replace() 方法用于替换字符的操作，这不是就跟String的不可变性矛盾了么？**

其实这里采用的是CopyOnWrite模式，从 replace() 方法的源码中（源码不是重点，这里就不放了）我们能够得知，String通过创建一个新的字符数组用于保存替换字符后的结果并返回。说白了，replace() 方法并没有在原字符串上作任何修改，也就并没有违背不可变性的原则。

并且 Java 也考虑到不可变类每一次修改操作都会因为创建过多对象从而造成过多的内存消耗，也就引入了对象池的概念，而这种方式也就是所谓的享元模式。具体会在包装类的分析中详细分析，这里只是稍微提一嘴。



### finally

finally的主要用途也非常明确，由于finally代码块一定会被执行的特性，常用于执行关闭连接、释放资源等操作。唯一一点需要注意的是，try - catch - finally 块中，如果catch代码块中有return的逻辑，并且finally中也有return的逻辑，那么catch代码块中return的逻辑不会被执行，执行的是finally中的return：

```java
public int doMethod() {
    try {
        int i = 10/0;// 抛出一个异常对象, 直接进入到catch块寻找合适的异常处理器
        System.out.println(i);
    } catch (ArithmeticException e){
        System.out.println("ArithmeticException");
        return 10;// 不会执行这个return, 直接进入到finally块执行剩下的逻辑
    } finally {
        System.out.println("finally");
        return 1;
    }
}

//返回结果：1
```

如果catch代码块中有return的逻辑，并且finally中没有return的逻辑，那么会在执行完finally的代码块后执行catch块中的return：

```java
public int doMethod() {
    try {
        int i = 10/0;// 抛出一个异常对象, 直接进入到catch块寻找合适的异常处理器
        System.out.println(i);
    } catch (ArithmeticException e){
        System.out.println("ArithmeticException");
        return 10;// 执行完finally中的代码后return
    } finally {
        System.out.println("finally");
    }
    return 1;
}

//返回结果：10
```

除此以外，当我们需要关闭流、释放资源等操作时，使用 try - with - resource 是一种更加简洁的方式。例如执行IO操作时，必须在使用完毕后手动释放资源：

```java
public void doMethod() {
    InputStream is = null;
    try {
        is = new FileInputStream(new File("..."));
        ...
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } finally {
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

但如果使用 try - with - resource，则非常简单方便：

```java
public void doMethod() {
    try (InputStream is = new FileInputStream(new File("..."))) {
        ...
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```



二者的主要概念以及涉及到的一些核心知识点就综上所述，总的来说，final 主要以不变模式、安全、CopyOnWrite模式等知识点作为重心进行分析；finally 主要从释放资源的角度进行分析。



