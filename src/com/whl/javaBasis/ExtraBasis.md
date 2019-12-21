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

使用final修饰参数或者变量，也可以避免意外赋值从而导致的编程错误，并且final变量产生的某种不可变的效果可以用于保护只读数据，尤其是在并发编程中，因为明确指定不能再对final变量进行赋值，有利于减少额外的同步开销。

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