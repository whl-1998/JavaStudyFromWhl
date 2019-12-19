package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 144. 二叉树前序遍历
 * @Description: normal
 */
public class BinaryTreePreorderTraversal {

    /**
     * 1. 递归
     * 思路：中 左 右
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param root
     * @return
     */
    List<Integer> res = new ArrayList<>();
    public List<Integer> preorderTraversal1(TreeNode root) {
        if (root != null) {
            res.add(root.val);
            preorderTraversal1(root.left);
            preorderTraversal1(root.right);
        }
        return res;
    }

    /**
     * 2. 迭代
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：1ms
     * @param root
     * @return
     */
    public List<Integer> preorderTraversal2(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        while (!stack.isEmpty() || root != null) {
            while (root != null) {
                stack.push(root);
                res.add(root.val);
                root = root.left;
            }
            TreeNode curr = stack.pop();
            root = curr.right;
        }
        return res;
    }
}
