package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.queue;


/**
 * @author whl
 * @version V1.0
 * @Title: 顺序队列
 * @Description:
 */
public class ArrayQueue {
    private int[] datas;
    private int head;
    private int tail;

    public ArrayQueue() {
        this.datas = new int[10];
    }

    public ArrayQueue(int length) {
       this.datas = new int[length];
    }

    public boolean offer(int data) {
        if (tail == datas.length) {
            if (head == 0) {
                return false;
            }
            System.arraycopy(datas, head, datas, 0, tail - head);
            tail -= head;
            head = 0;
            for (int i = tail; i < datas.length; i++) {
                datas[i] = 0;
            }
        }
        datas[tail] = data;
        tail++;
        return true;
    }

    public int poll() {
        if (head == tail) {
            return -1;
        }
        int res = datas[head];
        datas[head++] = 0;
        return res;
    }

    public int peek() {
        if (head == tail) {
            return -1;
        }
        return datas[head];
    }

    public static void main(String[] args) {
        ArrayQueue arr = new ArrayQueue(3);
        arr.offer(1);
        arr.offer(2);
        arr.offer(3);
        System.out.println(arr.poll());
        arr.offer(4);
        System.out.println(arr.peek());
        System.out.println(arr.poll());
        System.out.println(arr.poll());
        System.out.println(arr.poll());

    }
}
