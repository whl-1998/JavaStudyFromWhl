package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

import java.util.HashMap;

/**
 * @author whl
 * @version V1.0
 * @Title: 146. LRU缓存机制
 * @Description: normal
 * 1. 通过继承LinkedHashMap, 重写淘汰策略进行实现
 * 2. 手写双端链表, 通过HashMap+双端链表实现
 */
public class LRUCache /* extends  LinkedHashMap<Integer, Integer> */ {
//    int capacity;
//
//    public LRUCache(int capacity) {
//        super(capacity, 0.75f, true);
//        this.capacity = capacity;
//    }
//
//    public int get(int key) {
//        return super.getOrDefault(key, -1);
//    }
//
//    public void put(int key, int value) {
//        super.put(key, value);
//    }
//
//    @Override
//    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
//        return size() > capacity;
//    }

    HashMap<Integer, DoubleLinkedList.ListNode> map;
    DoubleLinkedList list;
    int capacity;

    public LRUCache(int capacity) {
        this.map = new HashMap<>();
        this.list = new DoubleLinkedList();
        this.capacity = capacity;
    }

    public int get(int key) {
        //if exist, get and update
        if (map.containsKey(key)) {
            int v = map.get(key).value;
            put(key, v);
            return v;
        }
        return -1;
    }

    public void put(int key, int value) {
        DoubleLinkedList.ListNode x = new DoubleLinkedList.ListNode(key, value);
        //if key is already exist in cache
        if (map.containsKey(key)) {
            //update cache
            DoubleLinkedList.ListNode temp = map.get(key);
            list.remove(temp);
            list.addEnd(x);
            map.put(key, x);
        } else {
            if (list.size >= capacity) {
                //remove oldest, then add
                DoubleLinkedList.ListNode rmv = list.removeFirst();
                map.remove(rmv.key);
            }
            list.addEnd(x);
            map.put(key, x);
        }
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache( 2 /* 缓存容量 */ );

        cache.put(1, 1);
        cache.put(2, 2);
        cache.get(1);       // 返回  1
        cache.put(3, 3);    // 该操作会使得密钥 2 作废
        cache.get(2);       // 返回 -1 (未找到)
        cache.put(4, 4);    // 该操作会使得密钥 1 作废
        cache.get(1);       // 返回 -1 (未找到)
        cache.get(3);       // 返回  3
        cache.get(4);       // 返回  4
    }
}

class DoubleLinkedList {
    private ListNode head;
    private ListNode tail;
    int size;

    public DoubleLinkedList() {
        this.head = new ListNode(0, 0);
        this.tail = new ListNode(0, 0);
        this.head.next = tail;
        this.tail.prev = head;
        this.size = 0;

    }

    public void remove(ListNode node) {
        if (node == head || node == tail) throw new RuntimeException("node is can't to be head or tail");
        ListNode prev = node.prev;
        ListNode next = node.next;
        prev.next = next;
        next.prev = prev;
        size--;
    }


    public ListNode removeFirst() {
        if (head.next != null) {
            ListNode deleteHead = head.next;
            remove(deleteHead);
            return deleteHead;
        }
        return null;
    }

    public void addEnd(ListNode node) {
        if (node == null) throw new RuntimeException("node is can't to be null");
        ListNode tailPrev = tail.prev;
        tailPrev.next = node;
        node.prev = tailPrev;
        node.next = tail;
        tail.prev = node;
        size++;
    }

    static class ListNode {
        int key;
        int value;
        ListNode prev;
        ListNode next;

        public ListNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}

