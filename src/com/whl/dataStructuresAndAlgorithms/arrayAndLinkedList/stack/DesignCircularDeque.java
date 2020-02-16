package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.stack;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
class DesignCircularDeque {
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


    /** Initialize your data structure here. Set the size of the deque to be k. */
    public DesignCircularDeque(int k) {
        this.length = k;
        head = new Node(-1);
        tail = new Node(-1);
        head.next = tail;
        head.prev = tail;
        tail.next = head;
        tail.prev = head;
    }

    /** Adds an item at the front of Deque. Return true if the operation is successful. */
    public boolean insertFront(int value) {
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

    /** Adds an item at the rear of Deque. Return true if the operation is successful. */
    public boolean insertLast(int value) {
        if (isFull()) {
            return false;
        }
        Node node = new Node(value);
        Node tailPrev = tail.prev;
        tailPrev.next = node;
        node.prev = tailPrev;
        node.next = tail;
        tail.prev = node;
        size++;
        return true;
    }

    /** Deletes an item from the front of Deque. Return true if the operation is successful. */
    public boolean deleteFront() {
        if (isEmpty()) {
            return false;
        }
        Node headNext = head.next;
        head.next = headNext.next;
        headNext.next.prev = head;
        headNext.next = null;
        headNext.prev = null;
        size--;
        return true;
    }

    /** Deletes an item from the rear of Deque. Return true if the operation is successful. */
    public boolean deleteLast() {
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

    /** Get the front item from the deque. */
    public int getFront() {
        return head.next.data;
    }

    /** Get the last item from the deque. */
    public int getRear() {
        return tail.prev.data;
    }

    /** Checks whether the circular deque is empty or not. */
    public boolean isEmpty() {
        return size == 0 ? true : false;
    }

    /** Checks whether the circular deque is full or not. */
    public boolean isFull() {
        return size == length ? true : false;
    }
}
