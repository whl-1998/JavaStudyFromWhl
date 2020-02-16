package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.queue;

import java.util.LinkedList;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 链式队列
 * @Description:
 */
public class LinkedQueue {
    private List<Integer> datas;
    private int size;

    public LinkedQueue() {
        this.datas = new LinkedList();
    }

    public boolean offer(int data) {
        boolean flag = datas.add(data);
        if (!flag)
            return false;
        size++;
        return true;
    }

    public int peek() {
        if (size == 0) {
            return -1;
        }
        return datas.get(0);
    }

    public int poll() {
        if (size == 0) {
            return -1;
        }
        int res = datas.remove(0);
        size--;
        return res;
    }

    public static void main(String[] args) {
        LinkedQueue lq = new LinkedQueue();
        lq.offer(1);
        lq.offer(2);
        lq.offer(3);
        System.out.println(lq.poll());
        System.out.println(lq.poll());
        System.out.println(lq.poll());
        System.out.println(lq.poll());
    }
}
