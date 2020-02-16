package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.stack;

import java.util.LinkedList;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 链式栈实现
 * @Description:
 */
public class StackByLinkedList {
    List<Integer> datas;
    int size;

    public StackByLinkedList() {
        datas = new LinkedList<>();
    }

    /**
     * 入栈
     * @param data
     */
    public void push(int data) {
        datas.add(data);
        size++;
    }

    public int pop() {
        return datas.remove(--size);
    }

    public int peek() {
        return datas.get(size - 1);
    }

    public static void main(String[] args) {
        StackByLinkedList stack = new StackByLinkedList();
        stack.push(1);
        stack.push(2);
        stack.push(3);
//        stack.push(4);
        System.out.println(stack.pop());
    }
}
