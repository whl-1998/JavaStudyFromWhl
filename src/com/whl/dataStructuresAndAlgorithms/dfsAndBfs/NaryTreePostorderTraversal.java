package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 590. N叉树的后序遍历
 * @Description:
 */
public class NaryTreePostorderTraversal {
    /**
     * 1. 递归
     * 思路：children[0 ~ n] -> 根节点
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：1ms
     * @param root
     * @return
     */
    List<Integer> res = new ArrayList<>();
    public List<Integer> postorder1(Node root) {
        if (root != null) {
            for (Node n : root.children) {
                postorder1(n);
            }
            res.add(root.val);
        }
        return res;
    }

    /**
     * 2. 迭代
     * 思路：按"根->右->左"的顺序逆序输出结果
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：7ms
     * @param root
     * @return
     */
    public List<Integer> postorder2(Node root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) return res;
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node curr = stack.pop();
            res.add(0, curr.val);
            if (curr.children != null) {
                for (Node n : curr.children) {
                    stack.push(n);
                }
            }
        }
        return res;
    }
}
