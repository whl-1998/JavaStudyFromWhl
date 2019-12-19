package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

import com.whl.dataStructuresAndAlgorithms.ListNode;
import java.util.HashSet;
import java.util.Set;

/**
 * @author whl
 * @version V1.0
 * @Title: 141. 环形链表
 * @Description:
 */
public class LinkedListCycle {
    /**
     * 1. 双指针
     * 思路：定义快慢指针, 快指针一次走两步, 慢指针一次走一步, 若快指针与慢指针相遇说明存在环, 否则在快指针走到链表末尾时return
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param head
     * @return
     */
    public boolean hasCycle1(ListNode head) {
        if (head == null || head.next == null) return false;
        ListNode quick = head;
        ListNode slow = head;
        while (quick.next != null && quick.next.next != null) {
            quick = quick.next.next;
            slow = slow.next;
            if (quick == slow) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解法2：HashSet判重
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：5ms
     * @param head
     * @return
     */
    public boolean hasCycle2(ListNode head) {
        if (head == null || head.next == null) return false;
        Set<ListNode> set = new HashSet<>();
        set.add(head);
        while (head.next != null) {
            head = head.next;
            if (!set.add(head)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        ListNode l1 = new ListNode(3);
        l1.next = new ListNode(2);
        ListNode l2 = l1.next.next = new ListNode(0);
        ListNode l3 = l1.next.next.next = new ListNode(-4);
        l3.next = l2;
        LinkedListCycle ll = new LinkedListCycle();
        ll.hasCycle2(l1);
    }
}
