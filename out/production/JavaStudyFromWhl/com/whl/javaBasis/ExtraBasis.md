### 1. final关键字
final修饰class代表不可被继承，修饰方法代表不可被重写，修饰变量代表不可以被再次赋值。final用于在语义方面标识变量、方法、类不可更改，此处的“不可更改”可能会引发歧义，例如下面代码中声明的final List list，它的不可更改指的是引用地址list不可被更改，并不是list这个引用地址所指向的集合不可被更改。
```java
final static List<Integer> list = new ArrayList<>();

public static void main(String[] args)  {
	//success
	list.add(1);
	//fail
	list = new ArrayList<>();
}
```
可以发现在java.lang包下很多类都被声明为final class，并且第三方类库中的基础类同样如此，这是因为声明为final可以避免使用者通过继承更改基础功能，这也是保证平台安全性的必要手段。  

我们都知道String是不可变的，每当在原本的String上作任何修改，本质上都是创建了一个新的String对象，这并不是因为String是final class，而是因为String底层存储字符串的char数组是被声明为final的，该字符数组不允许被修改。  

使用final修饰参数或者变量也可以避免意外赋值从而导致的编程错误，并且final变量产生的某种不可变的效果可以用于保护只读数据，尤其是在并发编程中，因为明确指定不能再对final变量进行赋值，有利于减少额外的同步开销。

### 2. 深拷贝、浅拷贝
在Java中基础数据类型的浅拷贝是通过值传递进行拷贝，因此浅拷贝对于基本数据类型是不存在影响的，如下列代码所示：
```java
int a = 1998;
int b = a;//浅拷贝
System.out.println(++a);// 1999
System.out.println(b);// 1998
```
而对象之间的浅拷贝是引用传递，这样在拷贝时就要考虑清楚被赋值的对象是否会影响到之前的对象，如下列代码所示：
```java
int[] a = new int[]{1998, 5, 14};
int[] b = a;// b = {1998, 5, 14}
b[0] = 2016;// b = {2016, 5, 14}
System.out.println(a[0]);// 2016
```
如果不希望对b的更改影响到a，可以通过clone()进行深拷贝，或者也可以通过序列化实现，如下列代码所示：
```java
int[] a = new int[]{1998, 5, 14};
int[] b = a.clone();
b[0] = 2016;
System.out.println(a[0]);// 1998
```

### 3. StringBuilder、StringBuffer、String
#### String
String位于java.lang包下，它是典型的Immutable类，被声明为final class，其底层的所有属性也都被声明为final，这意味着对字符串进行任何修改都会产生一个新的String对象，因此在某些频繁操作字符串的场景下，String会对性能有明显的影响。由于String的使用频繁，Java为了避免一个系统中产生大量的String对象引入了字符串常量池，在创建一个字符串时首先会检查常量池中是否存在值相同的字符串对象，如果有则直接获取该字符串对象的引用，如果没有则新建字符串对象，返回其引用并将新建的字符串对象放入池中。需要注意的是通过new String创建的不会通过常量池检查，也不会在创建成功后把字符串放入常量池，常量池只适用于直接给String对象引用赋值的情况。代码如下所示：
```java
String str1 = "whl";// 通过直接赋值的方式，放入字符串常量池（或者直接从常量池中取出）
String str2 = new String("whl");// 通过new的方式，不放入字符串常量池
```
值的一提的是：String提供了inter()方法，在调用该方法时，如果该对象代表的字符串在常量池可以找到，那么返回该常量池中的引用，如果找不到那么去堆中查找，如果堆中存在则将堆中对象的引用放入常量池并返回该引用，如果都没有找到，则将该字面量放入常量池。
```java
String str3 = new String("whl").inter();
```

#### StringBuffer
为了解决字符串拼接操作性能低下而提供的一个类，底层也是采用了字符数组进行字符串存储，但并不是final类型。StringBuffer本质上是一个线程安全的可修改字符序列，可以看到它的方法都被添加了synchronized同步锁，因此带来线程安全的同时也会造成一定性能的影响。StringBuffer在初始构建时创建了一个默认长度为16的字符数组（如果通过传入字符串构建，那么字符数组长度是字符串的长度加16）。和ArrayList一样，StringBuffer会在字符数组长度不够的时候进行扩容操作（创建新的数组，并把原来的数组拷贝到新数组），因此为了避免扩容造成的额外的性能开销，尽可能在初始时就指定StringBuffer的大小。

#### StringBuilder
在StringBuffer基础上去除了线程安全部分，有效减小了开销，如果没有线程安全方面的需求建议使用StringBuilder。值的一提的是，在JDK1.8中javac编译器会在编译期就对字符串的拼接操作进行了优化，将非静态的String拼接逻辑在编译期就完成了拼接转换，在实际运行时并不会出现字符串的拼接操作，因此String拼接操作对性能造成的影响也要结合javac的优化进行考量。






