package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.stack;

import java.util.EmptyStackException;

/**
 * @author whl
 * @version V1.0
 * @Title: 顺序栈实现
 * @Description:
 */
public class StackByArray {
    private int[] datas;
    private int size;//栈中元素大小
    private int length;//栈的长度

    public StackByArray() {
        this.length = 10;
        this.datas = new int[length];//默认构造创建栈的长度为10
    }

    public StackByArray(int length) {
        this.length = length;
        this.datas = new int[length];
    }

    /**
     * 入栈
     * @param data
     */
    public void push(int data) {
        if (size + 1 > length) {
            throw new RuntimeException("栈已满");
        }
        datas[size++] = data;
    }

    /**
     * 出栈
     * @return
     */
    public int pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        int res = datas[size - 1];
        datas[size - 1] = 0;
        size--;
        return res;
    }

    /**
     * 查看栈顶元素
     * @return
     */
    public int peek() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return datas[size - 1];
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        StackByArray stack = new StackByArray(3);
        stack.push(1);
        stack.push(2);
        stack.push(3);
//        stack.push(4);
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
    }
}
