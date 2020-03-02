在看了后面几篇设计模式的文章后，我发现大多数设计模式都是基于**面向对象**这个特性实现的（详细来说，是先通过面向对象特性总结设计原则，再在设计原则的基础之上总结设计模式）。这些所谓的 “设计模式” 其实就是针对某些特定场景，将最优（例如高可扩展、高可维护等）的实现方式总结为设计模式。总而言之，要学好设计模式的第一步，就是将面向对象编程理解透彻。



### 什么是面向对象编程

首先我们要知道，**面向对象编程语言与面向对象编程**并不是同一个概念。

**面向对象编程**是一种编程范式或编程风格，它以类或对象作为组织代码的基本单元，并以封装、继承、抽象、多态四个特性作为代码设计和实现的基石。而**面向对象语言**则是支持类或对象的语法载体，能够很方便地实现面向对象编程的四大特性。

一般面向对象编程都是通过面向对象语言进行的，但采用非面向对象语言依然可以进行面向对象编程。反之，使用面向对象语言写出的代码也不一定是具备面向对象编程风格的。



### 封装

封装也称作信息隐藏或数据访问保护。类对数据进行封装，外部只能通过该类提供的方法访问内部信息或数据。示例如下代码所示：

```java

public class Wallet {
    private String id;
    private long createTime;
    private BigDecimal balance;//钱包余额
    private long balanceLastModifiedTime;//上一次余额修改的时间
    // ...

    public Wallet() {
        this.id = IdGenerator.getInstance().generate();
        this.createTime = System.currentTimeMillis();
        this.balance = BigDecimal.ZERO;
        this.balanceLastModifiedTime = System.currentTimeMillis();
    }

    // 读方法
    public String getId() { return this.id; }
    public long getCreateTime() { return this.createTime; }
    public BigDecimal getBalance() { return this.balance; }
    public long getBalanceLastModifiedTime() { return this.balanceLastModifiedTime; }

    // 增加余额
    public void increaseBalance(BigDecimal increasedAmount) {
        if (increasedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("...");
        }
        this.balance.add(increasedAmount);
        this.balanceLastModifiedTime = System.currentTimeMillis();
    }
	
    // 减少余额
    public void decreaseBalance(BigDecimal decreasedAmount) {
        if (decreasedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("...");
        }
        if (decreasedAmount.compareTo(this.balance) > 0) {
            throw new InsufficientAmountException("...");
        }
        this.balance.subtract(decreasedAmount);
        this.balanceLastModifiedTime = System.currentTimeMillis();
    }
}
```

在Wallet类中，对所有属性私有化，对外只提供了所有属性的get方法和两个修改方法。之所以这样设计是因为id、createTime在Wallet实例被创建后就不需要修改，因此对外并不提供它们的set方法，从而保证了这两个属性的封装性。

而对于balance和balanceLastModifiedTime属性，我们先对其分析能够得知：

* balance被修改的同时，balanceLastModifiedTime也需要被修改。
* balance被修改的方式只能通过增加/减少来执行，不允许值的覆盖。
* balance增加/减少值必须合法，例如增加的值不允许小于0。

因此，我们仅对外开放 increaseBalance() 与decreaseBalance() 这两个方法，用于满足上述条件的balance增/减操作，具体如上述代码所示。

可以发现，封装这个特性需要编程语言提供的访问权限控制关键字来实现，例如上述代码中private、public等关键字就是Java实现封装特性的核心。

除了保证数据的安全性，封装也可以提高类的可用性。对属性进行封装，对外仅暴露少许必要的接口给调用者，调用者就不需要了解更多的业务细节，大大提高了易用性。

**总的来说，封装的主要功能就是隐藏信息、保护数据，同时提高类的可用性。**



### 抽象

抽象指的是：如何隐藏方法的具体实现，让调用者只需要关心方法提供的功能，而不需要关心方法内部的实现。在面向对象编程中，常常借助接口类或者抽象类来实现抽象的特性。示例代码如下所示：

```java
//接口
public interface IPictureStorage {
    void savePicture(Picture picture);

    Image getPicture(String pictureId);
    
    void deletePicture(String pictureId);
    
    void modifyMetaInfo(String pictureId, PictureMetaInfo metaInfo);
}

//实现类
public class PictureStorage implements IPictureStorage {
    @Override
    public void savePicture(Picture picture) { ... }
    
    @Override
    public Image getPicture(String pictureId) { ... }
    
    @Override
    public void deletePicture(String pictureId) { ... }
    
    @Override
    public void modifyMetaInfo(String pictureId, PictureMetaInfo metaInfo) { ... }
}
```

上述代码是图片存储的接口和实现类，当调用者需要使用图片存储功能时，只需要了解IPictureStorage接口中提供的抽象方法，而不需要了解PictureStorage实现类中方法的具体实现。

实际上，“抽象” 特性无处不在，而且它的好处也显而易见。就好比我们在初学HashMap时不可能一上来就直接就看源码，只需要了解各个API的功能就能够使用HashMap了。这就是 “抽象” 带来的好处。

我们在定义类的方法名时，也要让名称足够 “抽象” ，例如一个图片存储方法名 getAliyunPictureUrl() 就不够抽象，假如某天图片服务器从阿里云迁移到腾讯云，这个方法的名字还得改。因此，我们只需要抽取关键的部分 —— getPictrueUrl() 就可以了。

除此以外，很多的设计原则也都体现了抽象这种设计思想。比如 “开闭原则” 、“基于接口而非实现编程” 等。



### 继承

继承最大的好处就是代码复用。假设两个类A、B都具备一些相同的属性和方法，我们就可以将A、B相同的部分抽取到父类C中，并且让A、B都继承父类C，这样就可以达到代码重用的目的。

但继承的缺点也十分明显，如果父类的代码修改了，将会直接对子类造成影响。继承会导致代码可读性、可维护性变差，因此在Go语言中就把继承的特性移除了。因此对于继承，能不用就尽量别用。

其实很多设计模式也都是通过继承或implements接口实现的，例如代理模式、装饰器模式等。除此以外，继承这个特性也基本可以认为是与 “多态” 绑定了，因为一般我们都是通过子类继承父类，然后调用不同的子类以此实现多态的特性。



### 多态

多态指一种实现的多种状态，例如下面这段代码中，DynamicArray是动态数组的实现，SortedDynamicArray需要在动态数组基础上增加排序的功能。因此SortedDynamicArray继承了DynamicArray，复用了DynamicArray的大部分基础实现，仅仅只需要重写add方法，增加添加元素时排序的逻辑，就能够实现一个有序动态数组了。

```java

public class DynamicArray {
    private static final int DEFAULT_CAPACITY = 10;//默认长度
    protected int size = 0;
    protected int capacity = DEFAULT_CAPACITY;
    protected Integer[] elements = new Integer[DEFAULT_CAPACITY];

    public int size() { return this.size; }
    public Integer get(int index) { return elements[index];}

    public void add(Integer e) {
        ensureCapacity();
        elements[size++] = e;
    }

    protected void ensureCapacity() {
        //...如果数组满了就扩容...代码省略...
    }
}

public class SortedDynamicArray extends DynamicArray {
    
    //重写add()方法, 在添加元素时排序
    @Override
    public void add(Integer e) {
        ensureCapacity();
        int i;
        for (i = size - 1; i >= 0; --i) { //保证数组中的数据有序
            if (elements[i] > e) {
                elements[i + 1] = elements[i];
            } else {
                break;
            }
        }
        elements[i + 1] = e;
        ++size;
    }
}

public class Example {
    public static void test(DynamicArray dynamicArray) {
        dynamicArray.add(5);
        dynamicArray.add(1);
        dynamicArray.add(3);
        for (int i = 0; i < dynamicArray.size(); ++i) {
            System.out.println(dynamicArray.get(i));
        }
    }

    public static void main(String args[]) {
        DynamicArray dynamicArray1 = new SortedDynamicArray();
        test(dynamicArray1); // 打印结果：1、3、5
        DynamicArray dynamicArray2 = new DynamicArray();
        test(dynamicArray2); //打印结果：5、1、3
    }
}
```

我之前在**继承**那一块的分析中埋了个伏笔 “继承和多态是绑定的”。你可以看到上述代码中，我们可以通过同一个类型DynamicArray来创建不同的实现，这就是所谓的多态。

通常多态这种特性，通常采用继承加方法重写或者接口来实现。上面介绍了继承实现多态的方式，而接口方式的实现也很常见，比如集合类大多都实现了Iterator这个迭代器接口，因此我们能够获取多种不同型态的Iterator迭代器实例。代码如下所示：

```java
public interface Iterator {
    String hasNext();
    String next();
    String remove();
}

public class Array implements Iterator {
    private String[] data;

    public String hasNext() { ... }
    public String next() { ... }
    public String remove() { ... }
    //...省略其他方法...
}

public class LinkedList implements Iterator {
    private LinkedListNode head;

    public String hasNext() { ... }
    public String next() { ... }
    public String remove() { ... }
    //...省略其他方法... 
}

public class Demo {
    private static void print(Iterator iterator) {
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    public static void main(String[] args) {
        Iterator arrayIterator = new Array();
        print(arrayIterator);

        Iterator linkedListIterator = new LinkedList();
        print(linkedListIterator);
    }
}
```

上述代码中，LinkedList和Array都实现了Iterator接口，并且支持了Iterator接口下的抽象方法实现。可以看到，在main方法中创建的两个实例都是Iterator类型的，但是它们的实现却是不同的，这就是多态。

在上面的代码中的print方法中，如果不采用多态的特性，只能通过下面代码中例子的实现：

```java
private static void printLinkedList(LinkedList iterator) {
    while (iterator.hasNext()) {
        System.out.println(iterator.next());
    }
}

private static void prinArray(Array iterator) {
    while (iterator.hasNext()) {
        System.out.println(iterator.next());
    }
}
```

可以发现，如果使用多态，我们只需要一个print方法就能够实现遍历不同集合的操作。但没有多态，就需要针对不同集合实现不同的print方法，这显然违背了代码复用的目的。

除此以外，多态也是很多设计原则的基石，例如 “基于接口而非实现编程”、“利用多态去掉冗长的 if - else 语句”、“里式替换原则”等。

**总的来说，多态能够提高代码的可扩展性可代码复用性。**