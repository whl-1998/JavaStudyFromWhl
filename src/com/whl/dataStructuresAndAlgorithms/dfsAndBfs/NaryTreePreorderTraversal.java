package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 589. N叉树的前序遍历
 * @Description: easy
 */
public class NaryTreePreorderTraversal {

    /**
     * 1. 递归
     * 思路：根 [child[1], child[2] ~ child[n]]
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：1ms
     * @param root
     * @return
     */
    List<Integer> res = new ArrayList<>();
    public List<Integer> preorder1(Node root) {
        if (root != null) {
            res.add(root.val);
            for (Node n : root.children) {
                preorder1(n);
            }
        }
        return res;
    }

    /**
     * 2. 迭代
     * 思路：以[rightChild ~ midChild ~ leftChild]的顺序压栈, 这样stack.pop()获取到的就是leftChild节点, 然后以leftChild节点作为根节点继续遍历
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：4ms
     * @param root
     * @return
     */
    public List<Integer> preorder2(Node root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) return res;
        Stack<Node> stack = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node curr = stack.pop();
            res.add(curr.val);
            if (curr.children != null) {
                for (int i = curr.children.size() - 1; i >= 0; i--) {
                    stack.push(curr.children.get(i));
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        Node node5 = new Node(5, null);
        Node node6 = new Node(6, null);
        List<Node> l2 = new LinkedList<>();
        l2.add(node5);
        l2.add(node6);

        Node node2 = new Node(3, l2);
        Node node3 = new Node(2, null);
        Node node4 = new Node(4, null);
        List<Node> l1 = new LinkedList<>();
        l1.add(node2);
        l1.add(node3);
        l1.add(node4);

        Node node1 = new Node(1, l1);
        NaryTreePreorderTraversal nt = new NaryTreePreorderTraversal();
        nt.preorder2(node1);
    }
}
