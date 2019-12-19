package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 94. 二叉树中序遍历
 * @Description: normal
 */
public class BinaryTreeInorderTraversal {
    /**
     * 1. DFS递归
     * 思路：左 根 右
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param root
     * @return
     */
    List<Integer> res = new ArrayList<>();
    public List<Integer> inorderTraversal(TreeNode root) {
        if (root != null) {
            res.add(root.val);
            inorderTraversal(root.left);
            inorderTraversal(root.right);
        }
        return res;
    }

    /**
     * 2. 迭代
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：1ms
     * @param root
     * @return
     */
    public List<Integer> inorderTraversal2(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        while (!stack.isEmpty() || root != null) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            TreeNode curr = stack.pop();
            res.add(curr.val);
            root = curr.right;
        }
        return res;
    }

    public static void main(String[] args) {
        TreeNode t1 = new TreeNode(1);
        TreeNode t2 = new TreeNode(2);
        TreeNode t3 = new TreeNode(3);
        t1.right = t2;
        t2.left = t3;
        BinaryTreeInorderTraversal bt = new BinaryTreeInorderTraversal();
        bt.inorderTraversal2(t1);

    }
}
