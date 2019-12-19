package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

import java.util.HashMap;
import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 20. 有效的括号
 * @Description: easy
 */
public class ValidParentheses {
    /**
     * 1. Stack + HashMap
     * 思路：遍历字符串, 如果是左括号那么就push到栈, 如果是右括号那么就pop栈,
     * 观察pop的括号是否能够与当前括号组成正确的括号对（注意越界问题）
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：2ms
     * @param s
     * @return
     */
    public boolean isValid1(String s) {
        if ((s.length() & 1) != 0) return false;
        HashMap<Character, Character> map = new HashMap<>();
        map.put(')', '(');
        map.put(']', '[');
        map.put('}', '{');
        Stack<Character> stack = new Stack<>();
        char[] arr = s.toCharArray();
        for (char c : arr) {
            if (map.containsKey(c)) {
                if (!stack.isEmpty() && stack.pop() != map.get(c)) {
                    return false;
                }
            } else {
                stack.push(c);
            }
        }
        return stack.isEmpty();
    }

    /**
     * 2. Stack
     * 思路：遍历序列, 每当遇到一个左括号, push其匹配的右括号入栈, 每当遇到一个右括号, stack.pop观察出栈元素是否与该右括号相同
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：2ms
     * @param s
     */
    public boolean isValid2(String s) {
        if ((s.length() & 1) != 0) return false;
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '{') {
                stack.push('}');
            } else if (c == '[') {
                stack.push(']');
            } else if (c == '(') {
                stack.push(')');
            } else if (stack.isEmpty() || stack.pop() != c) {
                return false;
            }
        }
        return stack.isEmpty();
    }


    public static void main(String[] args) {
        ValidParentheses vp = new ValidParentheses();
        System.out.println(vp.isValid2("((()))"));
    }
}
