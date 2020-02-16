package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.linkedList;

import com.whl.dataStructuresAndAlgorithms.ListNode;

/**
 * @author whl
 * @version V1.0
 * @Title: 24. 两两交换链表中的节点
 * @Description: normal
 */
public class SwapNodesInPairs {
    /**
     * 1. 递归
     * 思路：例如"1->2->3->4->NULL"这么个链表, 只需要将节点1指向swap(3), 此时结构为1->4->3->NULL + 2->4, 将节点2指向节点1即可
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：1ms
     * @param head
     * @return
     */
    public ListNode swapPairs(ListNode head) {
        if (head == null || head.next == null) return null;
        ListNode headNext = head.next;
        head.next = swapPairs(headNext);
        headNext.next = head;
        return headNext;
    }

    public static void main(String[] args) {
        ListNode l1 = new ListNode(1);
        ListNode l2 = new ListNode(2);
        ListNode l3 = new ListNode(3);
        ListNode l4 = new ListNode(4);
        l1.next = l2;
        l2.next = l3;
        l3.next = l4;
        SwapNodesInPairs sn = new SwapNodesInPairs();
        sn.swapPairs(l1);

    }
}
