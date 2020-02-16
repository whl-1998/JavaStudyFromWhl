package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.stack;

import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 155. 最小栈
 * @Description: easy
 */

/**
 * 解法1. 双栈
 * 思路：创建一个辅助栈minStack用于存储主栈中的最小值
 */
public class MinStack {
    Stack<Integer> stack;
    Stack<Integer> minStack;

    /** initialize your data structure here. */
    public MinStack() {
        stack = new Stack<>();
        minStack = new Stack<>();
    }

    /**
     * 当主栈push(x)的同时, 如果x的值比最小栈的栈顶还要小, 那么最小栈也push(x)
     * 时间复杂度：O(1)
     * @param x
     */
    public void push(int x) {
        stack.push(x);
        if (minStack.isEmpty() || minStack.peek() >= x){
            minStack.push(x);
        }
    }

    /**
     * 当主栈pop时, 如果pop的是最小元素, 那么最小栈也需要将栈顶元素pop
     * 时间复杂度：O(1)
     */
    public void pop() {
        if (stack.pop().equals(minStack.peek())) {
            minStack.pop();
        }
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }

    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(512);
        minStack.push(-1024);
        minStack.push(-1024);
        minStack.push(512);
        System.out.println(minStack.minStack.peek());
        minStack.pop();
        minStack.getMin();
        minStack.pop();
        minStack.getMin();
        minStack.pop();
        minStack.getMin();
    }
}
