package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

/**
 * @author whl
 * @version V1.0
 * @Title: 101. 对称二叉树
 * @Description: easy
 */
public class SymmetricTree {
    /**
     * 1. 递归
     * 思路：判断一个二叉树是否是对称二叉树, 只需要判断它的左子树node1和右子树node2是否对称
     *       左子树node1如果和右子树node2对称, 首先左子树根节点的值与右子树根节点的值相等：node1.val == node2.val
     *       然后判断左子树的左子树与右子树的右子树是否对称：recur(node1.left, node2.right), 同理判断右子树的左子树与左子树的右子树是否对称
     * @param root
     * @return
     */
    public boolean isSymmetric(TreeNode root) {
        return recur(root, root);
    }

    private boolean recur(TreeNode node1, TreeNode node2) {
        if (node1 == null && node2 == null) return true;
        if (node1 == null || node2 == null) return false;
        return node1.val == node2.val && recur(node1.left, node2.right) && recur(node1.right, node2.left);
    }
}
