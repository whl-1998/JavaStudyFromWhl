package com.whl.jdkAnalysis.collections.linkedList;


/**
 * @author whl
 * @version V1.0
 * @Title: 单项链表
 * @Description:
 */
public class SingleLinkedList {
    class Node{
        public Node next;
        public String data;

        public Node(){

        }

        public Node(String data){
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int length;

    public SingleLinkedList(){
        this.head = null;
        this.tail = null;
    }

    public boolean isEmpty(){
        return length == 0;
    }

    public Node insert(String data){
        Node node = new Node(data);
        //若为空 把头节点置为node即可
        if(isEmpty()){
            head = node;
            tail = node;
        }else {
            tail.next = node;
            tail = node;
        }
        length++;
        return node;
    }

    private Node insertHead(String data){
        Node node = new Node(data);
        Node temp = head;
        head = node;
        node.next = temp;
        length++;
        return node;
    }

    public void insert(String data,int index){
        if(index<0 || index > length){
            throw new IndexOutOfBoundsException();
        }
        if(index == 0){
            insertHead(data);
        } else if (isEmpty()) {
            insert(data);
        }else if(index == length){
            insert(data);
        }
        else {
            Node node = new Node(data);
            Node current = head;
            Node nextNode = current.next;
            for(int i = 0;i<length-1;i++){
                if(i == index){
                    current.next = node;
                    node.next = nextNode;
                    break;
                }
                current = current.next;
                nextNode = current.next;
            }
        }
        length++;
    }



    public Node getHead(){
        return head;
    }

    public Node getTail(){
        return tail;
    }

    public void display(){
        if(isEmpty()){
            System.out.println("链表为空");
            return;
        }
        Node current = head;
        while (current!= null){
            System.out.println(current.data);
            current = current.next;
        }
    }

    public int getLength(){
        return length;
    }

    public static void main(String[] args) {
        SingleLinkedList linkedList = new SingleLinkedList();
        linkedList.insert("b");
        linkedList.insert("a");
        linkedList.insert("c");
        linkedList.display();
        linkedList.insertHead("gg");
        linkedList.display();
        System.out.println("---"+linkedList.length);
        linkedList.insert("hahaha",3);
        linkedList.display();
    }
}
