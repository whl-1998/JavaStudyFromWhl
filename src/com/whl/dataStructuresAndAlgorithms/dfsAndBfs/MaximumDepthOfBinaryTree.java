package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author whl
 * @version V1.0
 * @Title: 104. 二叉树的最大深度
 * @Description: easy
 */
public class MaximumDepthOfBinaryTree {
    /**
     * 1：DFS递归
     * 思路：每drill down一次, 深度+1, 最后返回左右子树的最大深度即可
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param root
     * @return
     */
    public int maxDepth1(TreeNode root) {
        if (root == null) return 0;
        int leftDep = maxDepth1(root.left);
        int rightDep = maxDepth1(root.right);
        return Math.max(leftDep, rightDep) + 1;
    }

    /**
     * 2. 迭代
     * 思路：通过维护一个栈记录每一层的节点, 若当前层节点还存在孩子节点, 下探到下一层, level + 1
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：1ms
     * @param root
     * @return
     */
    public int maxDepth2(TreeNode root) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int res = 1;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode curr = queue.poll();
                if (curr.left != null) {
                    queue.offer(curr.left);
                }
                if (curr.right != null) {
                    queue.offer(curr.right);
                }
            }
            res++;
        }
        return res;
    }

    public static void main(String[] args) {
        TreeNode t1 = new TreeNode(3);
        TreeNode t2 = new TreeNode(9);
        TreeNode t3 = new TreeNode(20);
        TreeNode t4 = new TreeNode(15);
        TreeNode t5 = new TreeNode(7);
        t1.left = t2;
        t1.right = t3;
        t2.left = t4;
        t2.right = t5;

        MaximumDepthOfBinaryTree md = new MaximumDepthOfBinaryTree();
        md.maxDepth2(t1);
    }
}
