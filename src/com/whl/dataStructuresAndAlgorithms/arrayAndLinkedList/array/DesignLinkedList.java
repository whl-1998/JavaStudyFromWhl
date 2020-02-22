package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

/**
 * @author whl
 * @version V1.0
 * @Title: 707. 设计链表
 * @Description: normal
 */
public class DesignLinkedList {
    int size;
    Node head;
    Node tail;

    class Node {
        Node next;
        Node prev;
        int data;

        public Node(int data) {
            this.data = data;
        }
    }

    /** Initialize your data structure here. */
    public DesignLinkedList() {
        head = new Node(-1);
        tail = new Node(-1);
        head.next = tail;
        tail.prev = head;
    }

    /** Get the value of the index-th node in the linked list. If the index is invalid, return -1. */
    public int get(int index) {
        if (index >= size) return -1;
        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.next == null ? -1 : curr.next.data;
    }

    /** Add a node of value val before the first element of the linked list. After the insertion, the new node will be the first node of the linked list. */
    public void addAtHead(int val) {
        Node node = new Node(val);
        Node headNext = head.next;
        node.next = headNext;
        node.prev = head;
        head.next = node;
        headNext.prev = node;
        size++;
    }

    /** Append a node of value val to the last element of the linked list. */
    public void addAtTail(int val) {
        Node node = new Node(val);
        Node tailPrev = tail.prev;
        tailPrev.next = node;
        tail.prev = node;
        node.next = tail;
        node.prev = tailPrev;
        size++;
    }

    /** Add a node of value val before the index-th node in the linked list. If index equals to the length of linked list, the node will be appended to the end of linked list. If index is greater than the length, the node will not be inserted. */
    public void addAtIndex(int index, int val) {
        if (index > size) return;
        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        Node node = new Node(val);
        Node currNext = curr.next;
        curr.next = node;
        node.prev = curr;
        node.next = currNext;
        currNext.prev = node;
        size++;
    }

    /** Delete the index-th node in the linked list, if the index is valid. */
    public void deleteAtIndex(int index) {
        if (size == 0 || index >= size) return;
        Node curr = head;
        for (int i = 0; i <= index; i++) {
            curr = curr.next;
        }
        Node currPrev = curr.prev;
        Node currNext = curr.next;
        currPrev.next = currNext;
        currNext.prev = currPrev;
        size--;
    }

    public static void main(String[] args) {
        DesignLinkedList linkedList = new DesignLinkedList(); // Initialize empty LinkedList
//        linkedList.addAtHead(7);
//        linkedList.addAtHead(2);
//        linkedList.addAtHead(1);
//        linkedList.addAtIndex(3, 0);  // linked list becomes 1->2->3
//        linkedList.deleteAtIndex(2);
//        linkedList.addAtHead(6);
//        linkedList.addAtTail(4);
        linkedList.addAtHead(8);
        linkedList.deleteAtIndex(1);
        System.out.println(linkedList.get(4));
    }
}
