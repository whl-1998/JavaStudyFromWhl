package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.stack;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author whl
 * @version V1.0
 * @Title: 255. 用队列实现栈
 * @Description: easy
 */
public class ImplementStackUsingQueues {
    Queue<Integer> queue;

    /** Initialize your data structure here. */
    public ImplementStackUsingQueues() {
        queue = new LinkedList<>();
    }

    /** Push element x onto stack. */
    public void push(int x) {
        queue.offer(x);
        for (int i = 0; i < queue.size() - 1; i++) {
            queue.add(queue.poll());
        }
    }

    /** Removes the element on top of the stack and returns that element. */
    public int pop() {
        return queue.poll();
    }

    /** Get the top element. */
    public int top() {
        return queue.peek();
    }

    /** Returns whether the stack is empty. */
    public boolean empty() {
        return queue.isEmpty();
    }

    public static void main(String[] args) {
        ImplementStackUsingQueues stack = new ImplementStackUsingQueues();

        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.top();   // returns 2
        stack.pop();   // returns 2
        stack.empty(); // returns false
    }
}
