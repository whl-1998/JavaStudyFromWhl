package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;

/**
 * @author whl
 * @version V1.0
 * @Title: 105. 根据前序遍历和中序遍历构建二叉树
 * @Description: normal
 */
public class ConstructBinaryTreeFromPreorderAndInorderTraversal {

    /**
     * 1. 递归
     * 思路：根据前序遍历根左右的特性, 可以确定前序遍历第一个节点为根节点, 再通过中序遍历的特性, 可以确定根节点的左/右子树
     * 因此, 通过前序遍历的方式逐个创建节点即可(根, 左, 右)
     * 时间复杂度O(n)
     * 空间复杂度O(1)
     * 执行用时：16ms
     * @param preorder
     * @param inorder
     * @return
     */
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        return recur(preorder, inorder, 0, 0, inorder.length - 1);
    }

    private TreeNode recur(int[] preorder, int[] inorder, int preStart, int inStart, int inEnd) {
        if (preStart > preorder.length || inStart > inEnd) {
            return null;
        }
        TreeNode curr = new TreeNode(preorder[preStart]);
        //遍历inorder, 寻找根节点在中序遍历中的idx
        int idx = 0;
        for (int i = inStart; i <= inEnd; i++) {
            if (curr.val == inorder[i]) {
                idx = i;
                break;
            }
        }
        // currNode.left指向preorder[preStart + 1], inorder[instart, idx - 1]这部分左子树构建的二叉树根节点
        curr.left = recur(preorder, inorder, preStart + 1, inStart, idx - 1);
        // currNode.right指向preorder[preStart + 1 + idx - inStart], inorder[idx + 1, inEnd]这部分右子树构建的二叉树根节点
        // idx - inStart = 前序遍历中currNode+左子树的序列长度, preStart + 1 + idx - inStart = 前序遍历中右子树部分的起始索引
        curr.right = recur(preorder, inorder, preStart + 1 + idx - inStart, idx + 1, inEnd);
        return curr;
    }

    public static void main(String[] args) {
        ConstructBinaryTreeFromPreorderAndInorderTraversal cs = new ConstructBinaryTreeFromPreorderAndInorderTraversal();
        cs.buildTree(new int[] {3,9,20,15,7}, new int[] {9,3,15,20,7});
    }
}
