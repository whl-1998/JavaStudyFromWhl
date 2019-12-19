package com.whl.jdkAnalysis.collections.avlTree;


/**
 * @author whl
 * @version V1.0
 * @Title: 平衡二叉树
 * @Description:
 */
public class AvlTree<T extends Comparable>{
    public class AvlNode<T>{
        public AvlNode<T> left;
        public AvlNode<T> right;
        public T data;
        public int height;

        public AvlNode(T data){
            this.data = data;
        }

        public AvlNode(AvlNode left,AvlNode right,T data){
            this.left = left;
            this.right = right;
            this.data = data;
        }

        public AvlNode(AvlNode left,AvlNode right,T data,int height){
            this.left = left;
            this.right = right;
            this.data = data;
            this.height = height;
        }

    }

    public AvlNode<T> root;//根节点

    public int height(AvlNode<T> node){
        //空节点height=-1
        return node==null? -1:node.height;
    }

    public void insert(T data){
        if(data == null){
            throw new RuntimeException("data cant be null");
        }
        this.root = insert(data, root);//root为插入后新平衡二叉树的root
    }

    private AvlNode<T> insert(T data,AvlNode<T> parent){
        if(parent == null){//若传入的parent为空,则可以根据data创建新节点进行插入了
            parent = new AvlNode<>(data);
        }
        //将data与父节点data比较
        int result = data.compareTo(parent.data);

        if(result<0){//去parent左子树寻找插入位置
            parent.left=insert(data, parent.left);

            //插入后计算子树高度，若=2则未平衡
            if(height(parent.left)-height(parent.right) == 2){
                if(data.compareTo(parent.left.data)<0){
                    //左左：右旋转
                }else {
                    //左右：左旋转+右旋转
                }
            }
        }else if(result>0){//去parent右子树寻找插入位置
            parent.right = insert(data,parent.right);

            if(height(parent.right)-height(parent.left) == 2){
                if(data.compareTo(parent.right.data)<0){
                    //右左
                }else {
                    //右右
                }
            }
        }else
        ;//如果data = parent.data 什么都不做 不允许存在相同data

        parent.height = Math.max(height(parent.left),height(parent.right))+1;//parent高度+1
        return parent;
    }
}
