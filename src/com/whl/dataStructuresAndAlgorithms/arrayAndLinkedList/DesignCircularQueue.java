package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title: 设计循环队列
 * @Description: normal
 */
public class DesignCircularQueue {
    Node tail;
    Node head;
    int size;
    int length;

    class Node {
        int data;
        Node next;
        Node prev;

        public Node(int data) {
            this.data = data;
        }
    }

    /** Initialize your data structure here. Set the size of the queue to be k. */
    public DesignCircularQueue(int k) {
        this.length = k;
        head = new Node(-1);
        tail = new Node(-1);
        head.next = tail;
        head.prev = tail;
        tail.next = head;
        tail.prev = head;
    }

    /** Insert an element into the circular queue. Return true if the operation is successful. */
    public boolean enQueue(int value) {
        if (isFull()) {
            return false;
        }
        Node node = new Node(value);
        Node headNext = head.next;
        head.next = node;
        node.prev = head;
        node.next = headNext;
        headNext.prev = node;
        size++;
        return true;
    }

    /** Delete an element from the circular queue. Return true if the operation is successful. */
    public boolean deQueue() {
        if (isEmpty()) {
            return false;
        }
        Node tailPrev = tail.prev;
        tailPrev.prev.next = tail;
        tail.prev = tailPrev.prev;
        tailPrev.prev = null;
        tailPrev.next = null;
        size--;
        return true;
    }

    /** Get the front item from the queue. */
    public int Front() {
        return tail.prev.data;
    }

    /** Get the last item from the queue. */
    public int Rear() {
        return head.next.data;
    }

    /** Checks whether the circular queue is empty or not. */
    public boolean isEmpty() {
        return size == 0 ? true : false;
    }

    /** Checks whether the circular queue is full or not. */
    public boolean isFull() {
        return size == length ? true : false;
    }

    public static void main(String[] args) {
        DesignCircularQueue circularQueue = new DesignCircularQueue(3); // set the size to be 3
        circularQueue.enQueue(1);
        circularQueue.enQueue(2);
        circularQueue.enQueue(3);
        circularQueue.enQueue(4);
        circularQueue.Rear();
        circularQueue.isFull();
        circularQueue.deQueue();
        circularQueue.enQueue(4);
        circularQueue.Rear();

    }
}
