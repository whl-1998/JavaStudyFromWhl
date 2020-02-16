package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.linkedList;

import com.whl.dataStructuresAndAlgorithms.ListNode;

/**
 * @author whl
 * @version V1.0
 * @Title: 环形链表II
 * @Description: normal
 */
public class LinkedListCycleII {
    /**
     * 1. 双指针 + 数学推导：
     * 思路：设起始位置到环入口的距离为a, 环入口到相遇点的距离为b, 相遇点到环入口的距离为c
     * 那么慢指针走到相遇点的距离为a + b
     * 快指针在走相遇点与慢指针相遇之前, 可能已经走了N圈环, 因此快指针与慢指针相遇时走过的距离为：a + b + n*(b + c)
     * 因为快指针速度是慢指针2倍, 因此通过"距离 = 速度 * 时间"也可以得出快指针与慢指针相遇时走过的距离为：2(a + b) = a + b + n*(b + c)
     * 移项得出：a = (n - 1)*(b + c) + c
     * 又因为b + c是整个环的距离, 即使走了n - 1次环也不会改变任何位置
     * 因此推导出：a = c
     * 这就意味着, 此时快指针以每次走一步的速度从相遇点走到环入口, 与起始位置走到环入口的距离相同
     * 到此, 代码就很好写了, 整个过程主要是计算比较难, 要求空间复杂度为O(1)的话, 我感觉应该可以算hard级别了...
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * @param head
     * @return
     */
    public ListNode detectCycle(ListNode head) {
        if (head == null || head.next == null) return null;
        ListNode quick = head;
        ListNode slow = head;
        while (quick.next != null && quick.next.next != null) {
            quick = quick.next.next;
            slow = slow.next;
            if (quick == slow) {
                ListNode slow2 = head;
                while (slow2 != slow) {
                    slow2 = slow2.next;
                    slow = slow.next;
                }
                return slow;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        ListNode l1 = new ListNode(3);
        ListNode l2 = l1.next = new ListNode(2);
        l1.next.next = new ListNode(0);
        ListNode l3 = l1.next.next.next = new ListNode(-4);
        l3.next = l2;
        LinkedListCycleII ll = new LinkedListCycleII();
        ll.detectCycle(l1);
    }
}
