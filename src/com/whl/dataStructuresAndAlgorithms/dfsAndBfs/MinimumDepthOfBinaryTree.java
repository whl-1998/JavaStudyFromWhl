package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

/**
 * @author whl
 * @version V1.0
 * @Title: 111. 二叉树的最小深度
 * @Description: easy
 */
public class MinimumDepthOfBinaryTree {
    /**
     * 1. dfs
     * 思路：首先要明确, 题目要求返回的是叶子节点到根节点的最小深度
     * 1. 如果左右子树都为空, 说明该节点是叶子节点, return 1
     * 2. 如果左右子树有一个为空, 那么返回不为空的深度
     * 3. 如果左右子树都不为空, 那么返回二者的较小值
     * 执行用时：0ms
     * @param root
     * @return
     */
    public int minDepth(TreeNode root) {
        if (root == null) return 0;
        if (root.left == null && root.right == null) return 1;
        int leftDep = minDepth(root.left);
        int rightDep = minDepth(root.right);
        if (root.left == null || root.right == null) {
            return leftDep + rightDep + 1;
        }
        return Math.min(leftDep, rightDep) + 1;
    }
}
