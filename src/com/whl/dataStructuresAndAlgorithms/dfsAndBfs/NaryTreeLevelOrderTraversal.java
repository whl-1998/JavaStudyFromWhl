package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import com.whl.dataStructuresAndAlgorithms.Node;

import java.util.*;

/**
 * @author whl
 * @version V1.0
 * @Title: 429. N叉树的层序遍历
 * @Description: normal
 */
public class NaryTreeLevelOrderTraversal {
    /**
     * 1. BFS
     * 思路：按层遍历N叉树, 将每一层的节点值添加到res对应该层的数组中
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：4ms
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrder1(Node root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> list = new ArrayList<>();
            while (size-- > 0) {
                Node currNode = queue.poll();
                list.add(currNode.val);
                for (Node n : currNode.children) {
                    if (n != null) {
                        queue.offer(n);
                    }
                }
            }
            res.add(list);
        }
        return res;
    }

    /**
     * 2. DFS
     * 思路：深度优先遍历N叉树, 将所有节点按照level添加到res对应下标的数组中
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param root
     * @return
     */
    public List<List<Integer>> levelOrder2(Node root) {
        List<List<Integer>> res = new ArrayList<>();
        dfs(root, res, 0);
        return res;
    }

    private void dfs(Node root, List<List<Integer>> res, int level) {
        if (root == null) return;
        if (res.size() == level) {
            res.add(new ArrayList<>());
        }
        res.get(level).add(root.val);
        if (root.children == null) return;
        for (Node n : root.children) {
            dfs(n, res, level + 1);
        }
    }

    public static void main(String[] args) {
        Node node = new Node(1);
        Node node2 = new Node(3);
        Node node3 = new Node(2);
        Node node4 = new Node(4);
        Node node5 = new Node(5);
        Node node6 = new Node(6);
        List<Node> list = new ArrayList<>();
        list.add(node2);
        list.add(node3);
        list.add(node4);
        List<Node> list2 = new ArrayList<>();
        list2.add(node5);
        list2.add(node6);
        node.children = list;
        node2.children = list2;
        NaryTreeLevelOrderTraversal nt = new NaryTreeLevelOrderTraversal();
        nt.levelOrder2(node);
    }
}
