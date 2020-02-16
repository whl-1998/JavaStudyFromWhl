package com.whl.dataStructuresAndAlgorithms.binarySearch;

/**
 * @author whl
 * @version V1.0
 * @Title: 33. 搜索旋转排序数组
 * @Description: normal
 */
public class SearchInRotatedSortedArray {
    /**
     * 1. 二分查找
     * 思路：由于存在偏移点, 可以将数组分为高位区和低位区：
     * 如果target <= nums[nums.lengnth - 1], 那么就二分查找[minIdx, nums.length - 1]
     * 如果target > nums[nums.length - 1], 那么就二分查找[0, minIdx - 1]
     * 时间复杂度：O(logN)
     * 空间复杂度：O(1)
     * 执行用时：1ms
     * @param nums
     * @param target
     * @return
     */
    public int search(int[] nums, int target) {
        if (nums.length == 0 || nums == null) return -1;
        int minIdx = findMinIdx(nums);
        int left = 0;
        int right = nums.length - 1;
        // target在偏移点右侧
        if (target <= nums[right]) {
            left = minIdx;
        } else {
            //target 在偏移点左侧
            right = minIdx - 1;
        }
        while (left <= right) {
            int mid = left + (right - left)/2;
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    private int findMinIdx(int[] nums) {
        int left = 0;
        int right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left)/2;
            if (nums[mid] <= nums[right]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    public static void main(String[] args) {
        SearchInRotatedSortedArray si = new SearchInRotatedSortedArray();
        si.findMinIdx(new int[]{8, 9, 1, 2, 3, 4, 5});

    }
}
