package com.whl.dataStructuresAndAlgorithms.binarySearch;

/**
 * @author whl
 * @version V1.0
 * @Title: 153. 寻找旋转排序数组中的最小值
 * @Description:
 */
public class FindMinimumInRotatedSortedArray {
    /**
     * 1. 二分查找
     * 思路：
     * 当nums[mid] <= nums[right], 说明最小值在[mid ~ right], 而mid当前位置元素可能是偏移点, right = mid
     * 当nums[mid] > nums[right], 说明mid位置一定不可能是偏移点, 最小值在[mid + 1 ~ right], left = mid + 1;
     * 并且退出循环的条件是left >= right, 这个条件只可能由left实现, 因此最后返回的是left指针指向的值。
     * 时间复杂度：O(logN)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param nums
     * @return
     */
    public int findMin(int[] nums) {
        int left = 0;
        int right = nums.length - 1;
        while (left < right) { // 不设置 == 是为了避免死循环
            int mid = left + (right - left)/2;
            if (nums[mid] <= nums[right]) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return nums[left];
    }

    public static void main(String[] args) {
        FindMinimumInRotatedSortedArray fm = new FindMinimumInRotatedSortedArray();
        fm.findMin(new int[] {3,4,5,1,2});
    }
}
