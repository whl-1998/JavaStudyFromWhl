package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

import java.util.*;

/**
 * @author whl
 * @version V1.0
 * @Title: 145. 二叉树的后序遍历
 * @Description: hard
 */
public class BinaryTreePostorderTraversal {
    /**
     * 1. 递归
     * 思路：左 右 根
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     *
     * @param root
     * @return
     */
    List<Integer> res = new ArrayList<>();

    public List<Integer> postorderTraversal(TreeNode root) {
        if (root != null) {
            postorderTraversal(root.left);
            postorderTraversal(root.right);
            res.add(root.val);
        }
        return res;
    }

    /**
     * 2. 迭代
     * 思路：基于前序遍历"根 左 右"的顺序, 我们将其更改为"根 右 左", 然后逆序得到"左 右 根"后序遍历的结果
     * 时间复杂度：O(n^2) 涉及到数组的移动
     * 空间复杂度：O(n)
     * 执行用时：1ms
     * @param root
     * @return
     */
    public List<Integer> postorderTraversal2(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        Stack<TreeNode> stack = new Stack<>();
        while (!stack.isEmpty() || root != null) {
            while (root != null) {
                stack.push(root);
                res.add(0, root.val);
                root = root.right;
            }
            TreeNode curr = stack.pop();
            root = curr.left;
        }
        return res;
    }
}