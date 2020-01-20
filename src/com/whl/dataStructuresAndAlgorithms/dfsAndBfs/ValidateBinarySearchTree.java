package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.TreeNode;
import com.whl.dataStructuresAndAlgorithms.binarySearch.ValidPerfectSquare;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 98. 验证二叉搜索树
 * @Description:
 */
public class ValidateBinarySearchTree {
    /**
     * 1. 递归
     * 思路：每一层递归暂存其父节点的值, 如果当前节点是左节点, 就比较curr.val是否大于parent.val; 如果当前节点是右节点, 就比较curr.val是否小于parent.val
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：1ms
     * @param root
     * @return
     */
    public boolean isValidBST1(TreeNode root) {
        return recur(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean recur(TreeNode root, Long minVal, Long maxVal) {
        if (root == null) return true;
        if (root.val <= minVal || root.val >= maxVal) {
            return false;
        }
        return recur(root.left, minVal, Long.valueOf(root.val)) && recur(root.right, Long.valueOf(root.val), maxVal);
    }


    /**
     * 2. 中序遍历
     * 思路：基于BST的中序遍历是有序来判断是否是BST
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：2ms
     * @param root
     * @return
     */
    public boolean isValidBST2(TreeNode root) {
        recur2(root);
        long prev = Long.MIN_VALUE;
        for (int i = 0; i < list.size(); i++) {
            long temp = list.get(i);
            if (prev >= temp) return false;
            prev = temp;
        }
        return true;
    }

    List<Long> list = new ArrayList<>();
    private void recur2(TreeNode root) {
        if (root != null) {
            recur2(root.left);
            list.add(Long.valueOf(root.val));
            recur2(root.right);
        }
    }

    /**
     * 3. 迭代
     * 思路：基于题解2的迭代实现
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：3ms
     * @param root
     * @return
     */
    public boolean isValidBST3(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        long minValue = Long.MIN_VALUE;
        while (!stack.isEmpty() || root != null) {
            while (root != null) {
                stack.push(root);
                root = root.left;
            }
            TreeNode curr = stack.pop();
            if (curr.val <= minValue) return false;
            minValue = curr.val;
            root = curr.right;
        }
        return true;
    }

    public static void main(String[] args) {
        TreeNode t1 = new TreeNode(1);
        TreeNode t2 = new TreeNode(2);
        TreeNode t3 = new TreeNode(3);
        t1.right = t3;
        t1.left = t2;
        ValidateBinarySearchTree vb = new ValidateBinarySearchTree();
        System.out.println(vb.isValidBST3(t1));
    }
}
