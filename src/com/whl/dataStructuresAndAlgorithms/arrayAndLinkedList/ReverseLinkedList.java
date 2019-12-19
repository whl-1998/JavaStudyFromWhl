package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

import com.whl.dataStructuresAndAlgorithms.ListNode;

/**
 * @author whl
 * @version V1.0
 * @Title: 206. 反转链表
 * @Description: easy
 */
public class ReverseLinkedList {
    /**
     * 递归
     * 思路：递归方法体中用一个参数存储当前节点的前序节点, 重复完成"head.next指向前序节点"这个操作即可
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：1ms
     * @param head
     * @return
     */
    public ListNode reverseList1(ListNode head) {
        return recur(head, null);
    }

    private ListNode recur(ListNode head, ListNode prev) {
        if (head == null) return prev;
        ListNode nextTemp = head.next;
        head.next = prev;
        return recur(nextTemp, head);
    }

    /**
     * 2. 迭代解法
     * 思路：和递归思路基本一致
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param head
     * @return
     */
    public ListNode reverseList2(ListNode head) {
        ListNode curr = head;
        ListNode prev = null;
        while (curr != null) {
            ListNode nextTemp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nextTemp;
        }
        return prev;
    }
}
