package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

/**
 * @author whl
 * @version V1.0
 * @Title: 26. 删除排序数组中的重复项
 * @Description:
 */
public class RemoveDuplicatesFromSortedArray {
    /**
     * 1. 循环
     * 思路：初始化一个指针k, k用于指向不重复序列末尾
     * 由于原数组有序, 只需要判断当前元素是否大于不重复序列中的最后一个元素即可
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param nums
     * @return
     */
    public int removeDuplicates1(int[] nums) {
        int k = 0;
        for (int i : nums) {
            if (k == 0 || i > nums[k - 1]) {
                nums[k++] = i;
            }
        }
        return k;
    }

    /**
     * 2. 双指针
     * 思路：指针k指向不重复序列末尾, 遍历原序列, 若当前元素与上一个元素不相同, 就在k位置覆盖上当前元素
     * 虽然时间复杂度和上一个解法一样, 但没有利用到题目的有序性
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param nums
     * @return
     */
    public int removeDuplicates2(int[] nums) {
        int k = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i == 0 || nums[i] != nums[i - 1]) {
                nums[k++] = nums[i];
            }
        }
        return k;
    }

    public static void main(String[] args) {
        RemoveDuplicatesFromSortedArray rd = new RemoveDuplicatesFromSortedArray();
        rd.removeDuplicates1(new int[]{1,1,2});
    }
}
