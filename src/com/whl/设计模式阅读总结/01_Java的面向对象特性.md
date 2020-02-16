### 什么是面向对象编程

面向对象编程是一种编程范式或编程风格，它以类或对象作为组织代码的基本单元，并以封装、继承、抽象、多态四个特性作为代码设计和实现的基石。

而面向对象的语言则是支持类或对象的语法载体，能够很方便地实现面向对象编程的四大特性。

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

在Wallet类中，对所有属性私有化，对外只提供了所有属性的get方法和两个修改方法。之所以这样设计是因为id、createTime在Wallet实例被创建后就不需要修改，因此对外并不提供它们的set方法。

而balance这个属性只能增或减，不能直接覆盖原值，因此对外也只提供了increaseBalance()、decreaseBalance()两个方法。并且在balance被修改时，也会修改balanceLastModifiedTime，这样也保证了balance与balanceLastModifiedTime的一致性。

可以发现，封装需要编程语言提供的访问权限控制来实现，例如上述代码中private、public等关键字就是Java实现封装特性的核心。

除了保证数据的安全性，封装也可以提高类的可用性。对属性进行封装，仅暴露少许必要的方法提供给调用者，调用者就不需要了解更多的业务细节，大大提高了易用性。就好比一个冰箱把外壳拆开，里面有很多按钮，此时我们大概率是用不明白了；但如果加上外壳，仅仅只提供几个必要的开关，那么使用起来就会非常容易。

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

实际上，“抽象”的特性无处不在，而且它的好处也显而易见。就好比我们在初学HashMap时不可能一上来就直接就看源码，只需要了解各个API的功能就能够使用HashMap了。这就是“抽象”带来的好处。

我们在定义类的方法名时，也要让名称足够“抽象”，例如一个图片存储方法名 getAliyunPictureUrl() 就不够抽象，假如某天图片服务器从阿里云迁移到腾讯云，这个方法的名字还得改。因此，我们只需要抽取关键的部分—— getPictrueUrl() 就可以了。



### 继承

继承最大的好处就是代码复用。假设两个类A、B都具备一些相同的属性和方法，我们就可以将A、B相同的部分抽取到父类C中，并且让A、B都继承父类C，这样就可以达到代码重用的目的。

但继承的缺点也十分明显，如果父类的代码修改了，将会直接对子类造成影响。继承会导致代码可读性、可维护性变差，因此在Go语言中就把继承的特性移除了。因此对于继承，能不用就尽量别用。



### 多态

多态指一种实现的多种状态，具体示例代码如下所示：

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
                elements[i+1] = elements[i];
            } else {
                break;
            }
        }
        elements[i+1] = e;
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
        DynamicArray dynamicArray = new SortedDynamicArray();
        test(dynamicArray); // 打印结果：1、3、5
    }
}
```

上述代码中，DynamicArray是动态数组的实现，SortedDynamicArray继承了DynamicArray，重写了add方法，在添加元素时排序。可以发现DynamicArray类型的引用具备了子类SortedDynamicArray的特性。该代码用到了三个语法机制实现多态：

1. 父类对象可以引用子类对象，也就是能够将SortedDynamicArray传递给DynamicArray。
2. SortedDynamicArray继承了DynamicArray
3. SortedDynamicArray重写了DynamicArray的add()方法

多态这种特性，通常采用继承加方法重写或者接口来实现。接口方式的实现代码如下所示：

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

上述代码中，LinkedList和Array都实现了Iterator接口，并且支持了接口下的抽象方法实现。可以看到，在main方法中，创建的两个实例都是Iterator类型的，但是它们的实现却是不同的，这就是多态。

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

可以发现，如果使用多态，我们只需要一个print方法就能够实现遍历集合的操作。但没有多态，就需要针对不同集合的打印实现不同的方法，如果后期增加了其他集合，那么还需要添加更多的方法，这显然违背了代码复用的目的。

**总的来说，多态能够提高代码的可扩展性可代码复用性。**

