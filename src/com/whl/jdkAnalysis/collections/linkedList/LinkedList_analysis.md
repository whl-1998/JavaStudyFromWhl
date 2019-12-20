# LinkedList
LinkedList采用双端链表的数据结构实现，且由于双端链表的特性，LinkedList也可以作为队列、双端队列来使用，不仅如此也可以作为栈使用。  

```java
public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, Serializable {
```
（1）实现了List接口，具备List的特性
（2）实现Deque接口，具备双端链表特性
（3）实现Clonable接口，可以被克隆
（4）实现了Serializable，可以被序列化

### 属性
```java
    //元素个数
    transient int size;
    //头结点
    transient LinkedList.Node<E> first;
    //尾结点
    transient LinkedList.Node<E> last;

	//双链表节点内部类
	private static class Node<E> {
        E item;//数据
        Node<E> next;//next指针
        Node<E> prev;//prev指针

		//初始化构造方法
        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
```

### 构造方法
```java
    /**
     * 1. 默认构造
     */
	public LinkedList() {
    }

    /**
     * 2. 通过传入一个集合创建链表
     */
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }
```

### 头部或末尾添加元素
作为双端链表，可以在链表头部添加节点，也可以在链表尾部添加节点，其时间复杂度都是O(1)  ，而作为双端队列时所用到的offerFirst，offerLast都是调用了下面的方法。
```java
    /**
     * 1. 添加元素到链表头部
     */
    private void linkFirst(E e) {
    	//头结点
        final Node<E> f = first;
        //创建newNode.value = e, newNode.next-> 头结点 
        final Node<E> newNode = new Node<>(null, e, f);
        //将newNode置为新的头结点
        first = newNode;
        //判断是否是第一个添加的节点
        if (f == null)
        	//如果是, 则把newNode也置为尾节点
            last = newNode;
        else
        	//否则原来的头节点.prev指向newNode
            f.prev = newNode;
        size++;
        modCount++;
    }
    
    /**
     * 2. 链表尾部添加元素
     */
    void linkLast(E e) {
    	//获取末尾节点
        final Node<E> l = last;
        //创建newNode.value = e, newNode.prev = 尾结点
        final Node<E> newNode = new Node<>(l, e, null);
        //将newNode置为新的尾结点
        last = newNode;
        //若第一次添加节点
        if (l == null)
        	//则把newNode也置为first节点
            first = newNode;
        else
        	//否则原尾结点.next指向newNode
            l.next = newNode;
        size++;
        modCount++;
    }
```

### add(int index, E element) index位置添加元素
由于node(int index)涉及到链表的遍历，因此add(int i, E e)方法时间复杂度是O(n)
```java
    public void add(int index, E element) {
        //判断是否越界
        checkPositionIndex(index);
		//若插入的节点是末尾节点
        if (index == size)
        	//直接调用linkLast
            linkLast(element);
        else
        	//否则先通过index检索到插入节点的后置节点
        	//再调用linkBefore进行插入
            linkBefore(element, node(index));
    }

	/**
     * 通过index检索到目标节点
     */
    Node<E> node(int index) {
    	//若index < size/2, 则从头节点开始遍历
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
        //若index >= size/2, 则从尾结点开始遍历
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

	/**
     * 在某个不为null的节点前插入一个新节点
     */
    void linkBefore(E e, Node<E> succ) {
        // 找到待插入节点的前置节点pred
        final Node<E> pred = succ.prev;
        // 创建newNode.prev = 前置节点, newNode.next = 后置节点succ
        final Node<E> newNode = new Node<>(pred, e, succ);
        // 后置节点succ.prev = newNode
        succ.prev = newNode;
        // 若前置节点为空, 说明succ原本是头节点
        if (pred == null)
        	//此时将newNode置为新的头节点
            first = newNode;
        else
        	//否则前序节点pred.next = newNode
            pred.next = newNode;
        size++;
        modCount++;
    }
```

### 头部或末尾删除元素
作为双端链表，可以在链表头部删除节点，也可以在链表尾部删除节点，其时间复杂度都是O(1)  ，而作为双端队列时所用到的pollFirst，pollLast都是调用了下面的方法。  

```java
    /**
     * 删除链表头节点
     */
    private E unlinkFirst(Node<E> f) {
        // 获取当前头节点value
        final E element = f.item;
        // 获取头节点的后置节点next
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        // 后置节点next置为新的头节点
        first = next;
        // 若删除了原头节点链表为空, 证明原头节点也末尾节点
        if (next == null)
        	//那么把末尾节点也置为空
            last = null;
        else
        	//否则后置节点.prev置为空
            next.prev = null;
        size--;
        modCount++;
        //返回删除节点value
        return element;
    }

    /**
     * 删除末尾节点
     */
    private E unlinkLast(Node<E> l) {
        // 获取当前尾节点value
        final E element = l.item;
        // 获取尾节点的前置节点prev
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        // prev置为新的尾结点
        last = prev;
        // 如果prev == null, 说明原尾节点也是头节点
        if (prev == null)
        	//头节点置为空
            first = null;
        else
        	//否则prev.next置为空
            prev.next = null;
        size--;
        modCount++;
        return element;
    }
```

### remove(int index) index位置删除元素
```java
    public E remove(int index) {
        checkElementIndex(index);
        //同样的, 先调用node(int index)检索到指定节点
        return unlink(node(index));
    }

    /**
     * 删除指定节点x
     */
    E unlink(Node<E> x) {
        // x的元素值
        final E element = x.item;
        // x的后置
        final Node<E> next = x.next;
        // x的前置
        final Node<E> prev = x.prev;
		// 如果前置节点为空, 说明x是头节点
        if (prev == null) {
        	//将头节点置为后置节点next
            first = next;
        } else {// 否则修改前置节点prev.next = x的后置
            prev.next = next;
            x.prev = null;//help GC
        }
		
		//如果后置节点为空, 说明x是尾结点
        if (next == null) {
        	//将前置prev置为新的尾节点
            last = prev;
        } else {//否则修改后置节点next.prev = x的前置
            next.prev = prev;
            x.next = null;//help GC
        }

        x.item = null;// help GC
        size--;
        modCount++;
        return element;
    }
```

### 总结
（1）LinkedList是以双端链表实现的List
（2）同时具备双端队列，队列，栈的特性
（3）首尾增、删元素非常高效，时间复杂度为O(1)
（4）中间增、删元素时间复杂度为O(n)