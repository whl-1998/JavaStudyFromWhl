package com.whl.jdkAnalysis.collections.linkedList;



/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class LinkedListDemo {
    private static class Node {
        int data;
        Node next;
        Node prev;

        Node(Node prev, int data, Node next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    Node first;

    Node last;

    int size;

    public LinkedListDemo() {
    }

    public boolean add(int data) {
        linkLast(data);
        return true;
    }

    /**
     * 在指定位置插入
     * @param index
     * @param data
     */
    public void add(int index, int data) {
        checkIndex(index);
        if (index == size) {
            linkLast(data);
        }
        else {
            linkBefore(data,node(index));
        }
    }

    /**
     * 获取头节点value
     * @return
     */
    public int getFirst() {
        return first.data;
    }

    /**
     * 获取指定index位置上节点的value
     * @param index
     * @return
     */
    public int get(int index) {
        checkIndex(index);
        return node(index).data;
    }

    /**
     * 获取尾结点value
     * @return
     */
    public int getLast() {
        return last.data;
    }


    /**
     * 删除指定index位置的node
     * @param index
     * @return
     */
    public int remove(int index) {
        checkIndex(index);
        return unlink(node(index));
    }


    /**
     * 删除头节点
     * @return
     */
    public int removeFirst() {
        Node f = first;
        return unlinkFirst(f);
    }

    /**
     * 删除尾结点
     * @return
     */
    public int removeLast() {
        Node l = last;
        return unlinkLast(l);
    }

    private int unlinkFirst(Node f) {
        int element = f.data;
        Node next = f.next;
        f.next = null;//help gc
        first = next;
        //若f的next节点为空 则表中只剩下f节点 last置为null
        if (next == null) {
            last = null;
        } else {
            next.prev = null;
        }
        size--;
        return element;
    }

    private int unlink(Node node) {
        int element = node.data;
        Node prev = node.prev;
        Node next = node.next;

        if (prev == null) {//若删除节点为头节点
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {//若删除节点为尾结点
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        size--;
        return element;
    }

    private int unlinkLast(Node l) {
        int element = l.data;
        Node prev = l.prev;
        l.prev = null;//help gc
        last = prev;
        if(prev == null) {
            first = null;
        } else {
          prev.next = null;
        }
        size--;
        return element;
    }

    private void checkIndex(int index) {
        if (index<0 || index >size) {
            throw new NullPointerException("index error："+index);
        }
    }

    private void linkFirst(int data) {
        Node f = first;
        Node newNode = new Node(null,data,first);
        if (f == null) {
           last = newNode;
        }
        else {
            f.prev = newNode;
        }
        size++;
    }

    private void linkBefore(int data, Node succ) {
        Node pred = succ.prev;
        Node newNode = new Node(pred,data,succ);
        succ.prev = newNode;
        //succ为头节点 或者可能为空表
        if (pred == null) {
            first = newNode;
        } else {
            pred.next = newNode;
        }
        size++;
    }

    private void linkLast(int data) {
        Node l = last;
        Node newNode = new Node(l,data,null);
        last = newNode;
        //若linkedList为空链表
        if (l == null) {
            first = newNode;
        }
        else {
            l.next = newNode;
        }
        size++;
    }

    Node node(int index) {
        checkIndex(index);
        if (index < (size>>1)) {
            //前半部分遍历
            Node x = first;
            for(int i = 0;i<index;i++) {
                x = x.next;
            }
            return x;
        } else {
            //后半部分遍历
            Node x = last;
            for(int i = size-1;i>index;i--) {
                x = x.prev;
            }
            return x;
        }
    }

    public static void main(String[] args) {
        LinkedListDemo linkedListDemo = new LinkedListDemo();
        linkedListDemo.add(1);
        linkedListDemo.add(2);
        linkedListDemo.add(3);
        linkedListDemo.remove(1);
        for(int i = 0;i<linkedListDemo.size;i++){
            System.out.println(linkedListDemo.get(i));
        }
    }
}
