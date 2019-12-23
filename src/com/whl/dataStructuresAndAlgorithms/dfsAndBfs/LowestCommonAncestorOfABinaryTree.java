package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

/**
 * @author whl
 * @version V1.0
 * @Title: 236. 二叉树的最近公共祖先
 * @Description: normal
 */
public class LowestCommonAncestorOfABinaryTree {
    /**
     * 1. dfs
     * 思路：首先明确题意：p, q为不同节点且均存在于给定的二叉树中
     * 那么就只存在3种情况：
     * 1. root的左子树中存在p, 右子树中存在q, 那么公共节点一定是root
     * 2. root的左子树中存在p, 右子树中不存在q, 那么公共节点一定是左子树中的p
     * 3. root的右子树中存在p, 左子树中不存在q, 那么公共节点一定是右子树中的p
     * 那么如果p, q不是root的情况, 要做的就是在左右子树中深度优先递归寻找节点p, q
     * 执行用时：11ms
     * @param root
     * @param p
     * @param q
     * @return
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == q || root == p) return root;
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        if (left == null) {
            return right;
        } else if (right == null) {
            return left;
        } else {
            return root;
        }
    }

    public static void main(String[] args) {
        TreeNode t1 = new TreeNode(3);
        TreeNode t2 = new TreeNode(5);
        TreeNode t3 = new TreeNode(1);
        TreeNode t4 = new TreeNode(6);
        TreeNode t5 = new TreeNode(2);
        TreeNode t6 = new TreeNode(0);
        TreeNode t7 = new TreeNode(8);
        TreeNode t8 = new TreeNode(7);
        TreeNode t9 = new TreeNode(4);
        t1.left = t2; t1.right = t3;
        t2.left = t4; t2.right = t5;
        t3.left = t6; t3.right = t7;
        t5.left = t8; t5.right = t9;
        LowestCommonAncestorOfABinaryTree lc = new LowestCommonAncestorOfABinaryTree();
        lc.lowestCommonAncestor(t1, t2, t9);
    }
}
