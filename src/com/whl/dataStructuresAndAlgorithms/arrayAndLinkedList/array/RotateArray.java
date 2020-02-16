package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

/**
 * @author whl
 * @version V1.0
 * @Title: 189. 旋转数组
 * @Description: easy
 */
public class RotateArray {
    /**
     * 1. 暴力反转：
     * 思路：移动k次数组, 每次移动将末尾元素放置到首位
     * 时间复杂度：O(k * n)
     * 空间复杂度：O(1)
     * 执行用时：51ms
     * @param nums
     * @param k
     */
    public void rotate1(int[] nums, int k) {
        k = k % nums.length;
        for (int j = 0; j < k; j++) {
            int prev = nums[nums.length - 1];
            System.arraycopy(nums, 0, nums, 1, nums.length - 1);
            nums[0] = prev;
        }
    }

    /**
     * 2. 反转
     * 思路：先把原序列反转一次, 然后把0 ~ k - 1部分反转, k ~ nums.length - 1部分反转
     * 时间复杂度：O(2 * N)
     * 空间复杂度：O(1)
     * @param nums
     * @param k
     */
    public void rotate2(int[] nums, int k) {
        k = k % nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }

    private void reverse(int[] nums, int start, int end) {
        for (int i = start, j = end; i < j; i++, j--) {
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
    }

    public static void main(String[] args) {
        RotateArray ra = new RotateArray();
        ra.rotate1(new int[]{1,2,3,4,5,6,7}, 3);
    }
}
