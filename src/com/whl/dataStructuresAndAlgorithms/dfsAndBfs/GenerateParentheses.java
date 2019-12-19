package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author whl
 * @version V1.0
 * @Title: 22. 括号生成
 * @Description: normal
 */
public class GenerateParentheses {
    /**
     * 1. DFS回溯 + 剪枝
     * 思路：首先获取到所有可能的括号组合,然后添加约束条件,筛选出所有符合要求的str：
     *          左括号可以出现在任意位置,而右括号只能出现在左括号的后面
     * 执行用时：1ms
     * @param n
     * @return
     */
    public List<String> generateParenthesis1(int n) {
        List<String> res = new ArrayList<>();
        recur(res, n, 0,  0, "");
        return res;
    }

    private void recur(List<String> res, int n, int left, int right, String str) {
        if (str.length() == n * 2) {
            res.add(str);
            return;
        }
        if (left < n) {
            recur(res, n, left + 1, right, str + "(");
        }
        if (right < left) {
            recur(res, n, left, right + 1, str + ")");
        }
    }

    /**
     * 2. Bfs
     * 思路：创建一个队列用于维护每次添加的括号组合, 当poll的str长度为n * 2时将其添加到result
     * 执行用时：5ms
     * @param n
     * @return
     */
    public List<String> generateParenthesis2(int n) {
        List<String> result = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        queue.offer("(");

        while (!queue.isEmpty()) {
            String str = queue.poll();
            if (str.length() == n * 2) result.add(str);
            int left = countMethod(str)[0];
            int right = countMethod(str)[1];
            if (left < n) {
                queue.offer(str + "(");
            }
            if (right < left) {
                queue.offer(str + ")");
            }
        }
        return result;
    }

    private int[] countMethod(String str) {
        int[] count = new int[2];
        for (int i = 0; i<str.length(); i++) {
            if (str.charAt(i) == '('){
                count[0]++;
            } else if (str.charAt(i) == ')') {
                count[1]++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        GenerateParentheses gp = new GenerateParentheses();
        gp.generateParenthesis2(3);
    }
}
