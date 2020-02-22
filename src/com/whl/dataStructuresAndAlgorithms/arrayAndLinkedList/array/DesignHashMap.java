package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

/**
 * @author whl
 * @version V1.0
 * @Title: 706. 设计哈希表
 * @Description: easy
 */
public class DesignHashMap {
    final Node[] data;

    class Node {
        int key;
        int value;
        Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    /** Initialize your data structure here. */
    public DesignHashMap() {
        data = new Node[10000];
    }

    /** value will always be non-negative. */
    public void put(int key, int value) {
        int idx = indexOf(key);
        if (data[idx] == null) {
            data[idx] = new Node(-1, -1);
        }
        Node prev = find(data[idx], key);//寻找桶位置是否存在key相同的node
        if (prev.next == null) {
            prev.next = new Node(key, value);
        } else {
            prev.next.value = value;
        }
    }

    /** Returns the value to which the specified key is mapped, or -1 if this map contains no mapping for the key */
    public int get(int key) {
        int idx = indexOf(key);
        if (data[idx] == null) {
            return -1;
        }
        Node node = find(data[idx], key);
        return node.next == null ? -1 : node.next.value;
    }



    /** Removes the mapping of the specified value key if this map contains a mapping for the key */
    public void remove(int key) {
        int idx = indexOf(key);
        if (data[idx] == null) return;
        Node prev = find(data[idx], key);
        if (prev.next == null) return;
        prev.next = prev.next.next;
    }

    private int indexOf(int key) {
        return Integer.hashCode(key) % data.length;
    }

    private Node find(Node data, int key) {
        Node node = data, prev = null;
        while (node != null && node.key != key) {
            prev = node;
            node = node.next;
        }
        return prev;
    }

    public static void main(String[] args) {
        DesignHashMap hashMap = new DesignHashMap();
        hashMap.put(1, 1);
        hashMap.put(2, 2);
        hashMap.get(1);  // returns 1
        hashMap.get(3);  // returns -1 (not found)
        hashMap.put(2, 1);  // update the existing value
        hashMap.get(2);
        hashMap.remove(2);
        hashMap.get(2);
    }
}
