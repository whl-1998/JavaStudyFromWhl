package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title: 641. 设计循环双端队列
 * @Description: normal
 */

/**
 * 1. 双端链表
 * 思路：基于双端链表实现, 头结点.prev指向尾结点, 尾结点next指向头结点即可实现循环双端队列
 * 执行用时：6ms
 */
public class MyCircularDeque {
    private int capacity;
    private DoubleNode tail;
    private DoubleNode head;
    private int size;

    /** Initialize your data structure here. Set the size of the deque to be k. */
    public MyCircularDeque(int k) {
        this.capacity = k;
        this.tail = new DoubleNode(-1);
        this.head = new DoubleNode(-1);
        tail.prev = head;
        tail.next = head;
        head.next = tail;
        head.prev = tail;
    }

    /**
     * 时间复杂度O(1)
     * @param value
     * @return
     */
    public boolean insertFront(int value) {
        if (size >= capacity) return false;
        DoubleNode x = new DoubleNode(value);
        DoubleNode headNext = head.next;
        headNext.prev = x;
        x.next = headNext;
        head.next = x;
        x.prev = head;
        size++;
        return true;
    }

    /**
     * 时间复杂度O(1)
     * @param value
     * @return
     */
    public boolean insertLast(int value) {
        if (size >= capacity) return false;
        DoubleNode x = new DoubleNode(value);
        DoubleNode tailPrev = tail.prev;
        x.next = tail;
        tail.prev = x;
        tailPrev.next = x;
        x.prev = tailPrev;
        size++;
        return true;
    }

    /**
     * 时间复杂度O(1)
     * @param
     * @return
     */
    public boolean deleteFront() {
        if (head.next == tail || tail.prev == head) return false;
        DoubleNode headNext = head.next;
        DoubleNode headNextNext = headNext.next;
        head.next = headNextNext;
        headNextNext.prev = head;
        size--;
        return true;
    }

    /**
     * 时间复杂度O(1)
     * @param
     * @return
     */
    public boolean deleteLast() {
        if (head.next == tail || tail.prev == head) return false;
        DoubleNode tailPrev = tail.prev;
        DoubleNode tailPrevPrev = tailPrev.prev;
        tailPrevPrev.next = tail;
        tail.prev = tailPrevPrev;
        size--;
        return true;
    }

    /**
     * 时间复杂度O(1)
     * @param
     * @return
     */
    public int getFront() {
        return head.next.value;
    }

    /**
     * 时间复杂度O(1)
     * @param
     * @return
     */    public int getRear() {
        return tail.prev.value;
    }

    /** Checks whether the circular deque is empty or not. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Checks whether the circular deque is full or not. */
    public boolean isFull() {
        return size == capacity;
    }
}

class DoubleNode {
    DoubleNode prev;
    DoubleNode next;
    int value;

    public DoubleNode(int value) {
        this.value = value;
    }
}


