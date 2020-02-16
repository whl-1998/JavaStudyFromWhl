package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.linkedList;

import com.whl.dataStructuresAndAlgorithms.ListNode;

/**
 * @author whl
 * @version V1.0
 * @Title: 21. 合并两个有序链表
 * @Description:
 */
public class MergeTwoSortedLists {
    /**
     * 1. 递归
     * 思路：若当前l1.val <= l2.val, 那么l1的next指针就指向(l1.next, l2)这部分递归的result, 同理l2
     * 时间复杂度：O(l1.length + l2.length)
     * 空间复杂度：O(1)
     * 执行用时：1ms
     * @param l1
     * @param l2
     * @return
     */
    public ListNode mergeTwoLists1(ListNode l1, ListNode l2) {
        if (l1 == null || l2 == null) {
            return l1 == null ? l2: l1;
        }
        if (l1.val <= l2.val) {
            l1.next = mergeTwoLists1(l1.next, l2);
            return l1;
        } else {
            l2.next = mergeTwoLists1(l1, l2.next);
            return l2;
        }
    }

    /**
     * 2. 迭代
     * 思路：创建一个resultNode作为排序好的链表头节点, 和一个tempNode用作暂存节点
     * 每当l1.val <= l2.val, 那么tempNode.next就指向l1, 之后tempNode需要指向其next节点用于存放(l1.next, l2)这部分比较的结果, 再继续比较(l1.next, l2)的部分
     * 同理l2....
     * 时间复杂度：O(l1.length + l2.length)
     * 空间复杂度：O(1)
     * 执行用时：1ms
     * @param l1
     * @param l2
     * @return
     */
    public ListNode mergeTwoLists2(ListNode l1, ListNode l2) {
        ListNode res = new ListNode(-1);
        ListNode temp = res;
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                temp.next = l1;
                l1 = l1.next;
            } else {
                temp.next = l2;
                l2 = l2.next;
            }
            temp = temp.next;

        }
        if (l1 == null || l2 == null) {
            temp.next = l1 == null ? l2 : l1;
        }
        return res.next;
    }

    public static void main(String[] args) {
        ListNode l1 = new ListNode(1);
        l1.next = new ListNode(2);
        l1.next.next = new ListNode(4);
        ListNode l2 = new ListNode(1);
        l2.next = new ListNode(3);
        l2.next.next = new ListNode(4);
        MergeTwoSortedLists mt = new MergeTwoSortedLists();
        mt.mergeTwoLists2(l1, l2);
    }
}
