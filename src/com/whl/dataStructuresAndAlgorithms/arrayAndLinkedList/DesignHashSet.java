package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title: 705. 设计HashSet
 * @Description: easy
 */
public class DesignHashSet {
    final Node[] data;
    final Object present = new Object();

    class Node {
        int key;
        Object value;
        Node next;

        public Node(int key) {
            this.key = key;
            this.value = present;
        }
    }

    /** Initialize your data structure here. */
    public DesignHashSet() {
        data = new Node[10000];
    }

    public void add(int key) {
        int idx = getIndex(key);
        if (data[idx] == null) {
            data[idx] = new Node(-1);
        }
        Node node = find(data[idx], key);
        if (node.next == null) {
            node.next = new Node(key);
        } else {
            return;
        }
    }

    public void remove(int key) {
        int idx = getIndex(key);
        if (data[idx] == null) {
            return;
        }
        Node node = find(data[idx], key);
        if (node.next != null) {
            node.next = node.next.next;
        }
    }

    /** Returns true if this set contains the specified element */
    public boolean contains(int key) {
        int idx = getIndex(key);
        if (data[idx] == null) {
            return false;
        }
        Node node = find(data[idx], key);
        return node.next == null ? false : true;
    }

    private int getIndex(int key) {
        return Integer.hashCode(key) % data.length;
    }

    private Node find(Node node, int key) {
        Node data = node, prev = null;
        while (data != null && data.key != key) {
            prev = data;
            data = data.next;
        }
        return prev;
    }
}
