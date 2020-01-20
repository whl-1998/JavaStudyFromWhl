# CopyOnWriteArrayList
CopyOnWriteArrayList是ArrayList的线程安全版本，内部也是通过Object数组实现，每一次对数组的写操作都是通过拷贝一份新数组进行修改，修改完成后再替换老的数组，这样保证了并发场景下只阻塞写操作而不会阻塞读操作。
```java
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
```
（1）实现了List接口，提供了List基础的添加、删除、遍历等操作  
（2）实现了RandomAccess，标识CopyOnWriteArrayList具备随机访问的能力  
（3）实现了Cloneable，标识CopyOnWriteArrayList可以被克隆  
（4）实现了Serializable，标识CopyOnWriteArrayList可以被序列化  

### 属性
```java
//底层采用ReenTrantLock进行修改时的加锁
final transient ReentrantLock lock = new ReentrantLock();

//真正存储元素的数组, 用volatile声明保证其可见性, 只能通过getArray()/setArray()访问
private transient volatile Object[] array;
```

### 构造方法
```java
/**
 * 1. 默认构造：创建一个空数组
 */
public CopyOnWriteArrayList() {
	setArray(new Object[0]);
}

/**
 * 2. 传入集合c进行构造
 */
public CopyOnWriteArrayList(Collection<? extends E> c) {
	Object[] elements;
	//若c是CopyOnWriteArrayList类型的, 直接将它的数组赋值给当前数组
	if (c.getClass() == CopyOnWriteArrayList.class)
		elements = ((CopyOnWriteArrayList<?>)c).getArray();
	//如果不是, 则进行拷贝
	else {

		elements = c.toArray();
		// c.toArray或许不能正确地转换为Object数组
		if (elements.getClass() != Object[].class)
			//通过数组拷贝进行赋值操作
			elements = Arrays.copyOf(elements, elements.length, Object[].class);
	}
	setArray(elements);
}

/**
 * 3. 传入数组进行构造
 */
public CopyOnWriteArrayList(E[] toCopyIn) {
	//通过数组拷贝进行赋值操作 
	setArray(Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class));
}
```

### add(E e) 添加元素到末尾
（1）加锁  
（2）获取旧数组，并通过旧数组创建新数组，长度为旧数组长度+1  
（3）新数组末尾赋值为待添加元素  
（4）执行完毕后释放锁  
```java
public boolean add(E e) {
	//获取锁
	final ReentrantLock lock = this.lock;
	//加锁
	lock.lock();
	try {
		//获取旧数组
		Object[] elements = getArray();
		//获取旧数组长度
		int len = elements.length;
		//创建新数组, 值为旧数组长度加一的拷贝
		Object[] newElements = Arrays.copyOf(elements, len + 1);
		//新数组末尾添加上值e
		newElements[len] = e;
		//将新数组设置为底层存储元素的array
		setArray(newElements);
		//成功执行到此, 返回true
		return true;
	} finally {
		//无论失败与否, 释放锁
		lock.unlock();
	}
}
```

### add(int index, E e) 指定index添加元素
（1）加锁  
（2）获取旧数组  
（3）判断是否越界  
（4）创建新数组，长度为旧数组的长度+1  
（5）如果是在尾部插入，新数组末尾赋值为待添加元素即可  
（6）如果是在其他位置插入，先将插入位置的后序元素全部右移之后，在插入位置赋值  
（7）执行完毕释放锁  
```java
/**
 * Inserts the specified element at the specified position in this
 * list. Shifts the element currently at that position (if any) and
 * any subsequent elements to the right (adds one to their indices).
 *
 * @throws IndexOutOfBoundsException {@inheritDoc}
 */
public void add(int index, E element) {
	//获取锁
	final ReentrantLock lock = this.lock;
	//加锁
	lock.lock();
	try {
		//获取到旧数组
		Object[] elements = getArray();
		//获取旧数组的长度
		int len = elements.length;
		//判断是否越界
		if (index > len || index < 0)
			throw new IndexOutOfBoundsException("Index: "+index+
												", Size: "+len);
		//创建新数组
		Object[] newElements;
		//插入之后, 要右移的元素个数
		int numMoved = len - index;
		//如果是在尾部插入
		if (numMoved == 0)
			//直接通过数组拷贝, 然后末尾赋值即可
			newElements = Arrays.copyOf(elements, len + 1);
		else {
			//如果插入位置不是尾部, 那么新建一个长度为len+1的数组
			newElements = new Object[len + 1];
			//将旧数组拷贝到新数组
			System.arraycopy(elements, 0, newElements, 0, index);
			//将插入元素位置的右边所有元素通过数组拷贝右移一位
			System.arraycopy(elements, index, newElements, index + 1, numMoved);
		}
		//插入元素位置赋值
		newElements[index] = element;
		//将新数组设置为底层存储元素的array
		setArray(newElements);
	} finally {
		//释放锁
		lock.unlock();
	}
}
```

### get(int index) 获取指定索引位置的元素
（1）先通过getArray()获取到底层存储元素的数组  
（2）再从该数组中返回index位置的元素  
```java
public E get(int index) {
	//get()底层是通过getArray()获取数组
	return get(getArray(), index);
}

final Object[] getArray() {
	return array;
}

@SuppressWarnings("unchecked")
private E get(Object[] a, int index) {	
	return (E) a[index];
}
```

### remove(int index) 删除指定索引位置的元素
（1）加锁  
（2）获取待删除元素的值  
（3）如果待删除元素是末尾元素，那么直接通过拷贝一份旧数组长度-1的新数组返回即可  
（4）如果不是末尾元素，那么先拷贝[0, index-1]部分, 再拷贝[index+1, oldArray.len-1], 覆盖掉待删除元素  
（5）将新数组赋值给底层数组  
（6）解锁并返回旧值  
```java
/**
 * Removes the element at the specified position in this list.
 * Shifts any subsequent elements to the left (subtracts one from their
 * indices).  Returns the element that was removed from the list.
 *
 * @throws IndexOutOfBoundsException {@inheritDoc}
 */
public E remove(int index) {
	//获取锁
	final ReentrantLock lock = this.lock;
	lock.lock();
	try {
		//获取旧数组 
		Object[] elements = getArray();
		int len = elements.length;
		//获取待删除位置的旧址
		E oldValue = get(elements, index);
		//删除元素后, 后序需要左移的元素个数
		int numMoved = len - index - 1;
		if (numMoved == 0)
			//如果删除的是末尾节点, 那么将底层数组array赋值为旧数组长度-1的拷贝即可
			setArray(Arrays.copyOf(elements, len - 1));
		else {
			//否则新建一个数组, 长度为旧数组的长度-1
			Object[] newElements = new Object[len - 1];
			//将旧数组[0, index - 1]部分的元素拷贝到新数组
			System.arraycopy(elements, 0, newElements, 0, index);
			//将旧数组[index + 1, oldArray.len - 1]部分的元素以index为起始位置拷贝到新数组
			System.arraycopy(elements, index + 1, newElements, index, numMoved);
			setArray(newElements);
		}
		return oldValue;
	} finally {
		lock.unlock();
	}
}
```

### 总结
（1）CopyOnWriteArrayList通过ReenTrantLock加锁，以此保证线程安全  
（2）CopyOnWriteArrayList的写操作都需要通过原数组拷贝一份新数组，在新数组上作修改后再赋值给原数组，因此每次写操作都涉及到新建数组，时间复杂度为O(n)  
（3）CopyOnWriteArrayList采用读写分离的思想，读操作不加锁，通过volatile保证可见性；写操作加锁，且写操作会占用较大的内存空间，因此适合读多写少的场景  
（4）CopyOnWriteArrayList只能保证最终一致性，不能保证实时一致性  




























