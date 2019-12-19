package com.whl.dataStructuresAndAlgorithms;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class Node {
    public int val;
    public List<Node> children;

    public Node() {}

    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, List<Node> _children) {
        val = _val;
        children = _children;
    }
}
