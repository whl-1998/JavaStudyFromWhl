package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author whl
 * @version V1.0
 * @Title: 226. 翻转二叉树
 * @Description: easy
 */
public class InvertBinaryTree {

    /**
     * 1. 递归
     * 思路：将root.left指向递翻转之后的right节点, root.right指向指向翻转之后的left节点
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param root
     * @return
     */
    public TreeNode invertTree1(TreeNode root) {
        if (root == null) return root;
        TreeNode left = root.left;
        TreeNode right = root.right;
        root.left = invertTree1(right);
        root.right = invertTree1(left);
        return root;
    }

    /**
     * 2. BFS
     * 思路：按层遍历, 把当前层的所有节点的左右子树交换
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param root
     * @return
     */
    public TreeNode invertTree2(TreeNode root) {
        if (root == null) return null;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode curr = queue.poll();
            TreeNode leftTemp = curr.left;
            curr.left = curr.right;
            curr.right = leftTemp;
            if (curr.left != null) {
                queue.offer(curr.left);
            }
            if (curr.right != null) {
                queue.offer(curr.right);
            }
        }
        return root;
    }
}

