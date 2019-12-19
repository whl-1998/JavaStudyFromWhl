package com.whl.jdkAnalysis.collections.avlTree;

/**
 * @author whl
 * @version V1.0
 * @Title: 二叉查找树
 * @Description:
 */
public class BinarySearchTree {
    private Node root;//根节点

    //节点内部类
    private class Node{
        private int key;
        private Node left;
        private Node right;
        private Node father;

        public Node(int key){
            this.key = key;
        }

        public Node(){}
    }

    //假设用户插入的key没有重复 插入值为key的Node
    public void insert(int key){
        Node node = new Node();//创建一个node节点用于存储插入数据key
        node.key = key;
        if(root == null){
            root = node;//若根节点为空,那么该节点为根节点
            return;
        }
        Node parent = new Node();
        Node current = root;
        while (true){
            parent = current;
            if(key>current.key){
                current = current.right;//右子树
                if(current == null){
                    parent.right = node;
                    node.father = parent;
                    return;
                }
            }else {
                current = current.left;
                if(current == null){
                    parent.left = node;
                    node.father = parent;
                    return;
                }
            }
        }
    }

    //中序遍历
    public void inOrder(Node root){
        if(root != null){
            inOrder(root.left);
            System.out.print(root.key+" ");
            inOrder(root.right);
        }
    }

    //前序遍历
    public void preOrder(Node root){
        if (root != null) {
            System.out.print(root.key + " ");
            preOrder(root.left);
            preOrder(root.right);
        }
    }

    //后序遍历
    public void postOrder(Node root){
        if (root != null) {
            postOrder(root.left);
            postOrder(root.right);
            System.out.print(root.key + " ");
        }
    }

    //查找值为key的Node
    public Node search(int key){
        Node current = root;
        while (current.key != key){
            if(key > current.key){//在右子树查找
                current = current.right;
            }else {
                current = current.left;
            }
            if(current == null){
                return null;
            }
        }
        return current;
    }

    //查找maxNode
    public Node maxNode(Node root){
        Node current = root;
        while (current.right != null){
            current = current.right;
        }
        return current;
    }

    //查找minNode
    public Node minNode(Node root){
        Node current = root;
        while (current.left != null){
            current = current.left;
        }
        return current;
    }

    //查找node节点的前驱
    public Node getPredecessor(int key){
        Node node = search(key);
        if(root == null){
            return null;
        }
        //如果node的左子树不为空：直接在其中寻找最大值返回即可
        if (node.left != null){
            return maxNode(node.left);
        }
        //若node没有左子树，则有两种可能：
        //1.node为右子树，则前驱就是父节点
        //2.node为左子树，则去node的父节点继续寻找
        else {
            Node parent = node.father;
            while (parent != null && node == parent.left){//若为左子树且parent!=NULL
                node = parent;//将parent赋给node
                parent = node.father;//parent节点的parent节点
            }
            return parent;
        }
    }

    //后继
    public Node getSuccessor(int key){
        Node node = search(key);
        if(root == null){
            return null;
        }
        if(node.right != null){
            return minNode(node.right);
        }
        //若node没有右子树，则有两种可能：
        //1. node为左子树，则后继就是父节点
        //2. node为右子树，则去node的父节点继续寻找
        else{
            Node parent = node.father;
            while (parent!= null && node == parent.right){
                node = parent;
                parent = node.father;
            }

            return parent;

        }
    }

    private Node getDelSucNode(Node delNode){
        Node successor = getSuccessor(delNode.key);//后继节点
        Node successorParent = successor.father;//后继节点的parent

//        while (current != null){
//            successorParent = successor;
//            successor = current;
//            current = current.left;
//        }
        //如果后继节点为删除节点的右子树的左孩子 需要预先调整删除节点的右子树
        if(successor != delNode.right){
            successorParent.left = successor.right;
            successor.right = delNode.right;
        }
        return successor;
    }



    public boolean delete(int key){
        Node current = root;
        Node parent = new Node();
        boolean isRightChild = true;
        //从root往下寻找值为key的node
        while (current.key != key){
            parent = current;
            if(key > current.key){
                current = current.right;
                isRightChild = true;
            }else {
                current = current.left;
                isRightChild = false;
            }
            //没找到 直接返回
            if (current == null) return false;
        }

        //情况1：只需要将parent.left或parent.right设置为null
        if(current.right == null && current.left == null){
            if (current == root){//如果删除节点为根节点，整棵树清空
                root = null;
            }
            else {
                if(isRightChild){
                    parent.right = null;
                }else {
                    parent.left = null;
                }
            }
            return true;
        }
        //情况2：要删除结点有一个右孩子
        else if(current.left==null) {
            if(current==root) {//若删除的根节点有左孩子，直接将左孩子置为root
                root = current.right;
            }
            else if(isRightChild)//如果current是右孩子，需要将parent的右指针指向其右孩子
                parent.right=current.right;
            else//...
                parent.left=current.right;
            return true;
        }//要删除结点有一个左孩子
        else if(current.right==null){
            if(current==root) {//若删除的根节点有右孩子，直接将右孩子置为root
                root = current.left;
            }
            else if(isRightChild)
                parent.right=current.left;
            else
                parent.left=current.left;
            return true;
        }
        //要删除结点有两个子结点
        else{
            Node successor=getDelSucNode(current);    //找到要删除结点的后继结点
            if(current==root) {
                root = successor;
            }
            else if(isRightChild) {
                parent.right = successor;
            }
            else {
                parent.left = successor;
            }
            successor.father = parent;
            successor.left=current.left;
            current.left.father = successor;
            return true;
        }
    }

    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        bst.insert(15);
        bst.insert(6);
        bst.insert(18);
        bst.insert(3);
        bst.insert(11);
        bst.insert(17);
        bst.insert(20);
        bst.insert(2);
        bst.insert(4);
        bst.insert(9);
        bst.insert(13);
        bst.insert(10);

        bst.delete(6);
        //bst.preOrder(bst.root);
       bst.inOrder(bst.root);
//        System.out.println();
//        bst.preOrder(bst.root);
//        System.out.println();
//        bst.postOrder(bst.root);

        //bst.search(1);

        //System.out.println(bst.maxNode(bst.root).data);
        //System.out.println(bst.minNode(bst.root).data);


        //System.out.println(bst.getPredecessor(9).key);
        //System.out.println(bst.getSuccessor(13).key);

    }
}


