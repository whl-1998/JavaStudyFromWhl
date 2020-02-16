package com.whl.dataStructuresAndAlgorithms.sortAlgorithms;

/**
 * @author whl
 * @version V1.0
 * @Title: 493. 翻转对
 * @Description: hard
 */
public class ReversePairs {
    /**
     * 解法1：暴力求解
     * 思路：枚举所有i < j 的组合, 当nums[i]/2.0 > 2 * nums[j]时, count++, 注意使用"/2.0"是为了在出现Integer.MAX_VALUE时避免越界
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     * 执行用时：超时
     * @param nums
     * @return
     */
    public int reversePairs1(int[] nums) {
        int count = 0;
        for (int i = nums.length - 1; i >= 0; i--) {
            for (int j = 0; j < i; j++) {
                if (nums[j]/2.0 > nums[i]) count++;
            }
        }
        return count;
    }

    /**
     * 解法2：归并排序
     * 思路：基于归并排序的思路, 左半区域的index一定是小于右半区域的, 这样就可以满足翻转对的第一个条件i < j
     * 		接下来只需要在归并操作时, 以左半区作为nums[i], 右半区作为nums[j], 统计nums[i] > 2 * nums[j]的个数
     * 		因为归并排序的特性, 检索过的区间都被置为有序, 也就意味着不会出现重复判断的情况
     * 时间复杂度：O(log2N)
     * 空间复杂度：O(n)
     * 执行用时：59ms
     * @param nums
     * @return
     */
    public int reversePairs2(int[] nums) {
        return mergeSort(nums, 0, nums.length - 1);
    }

    private int mergeSort(int[] nums, int left, int right) {
        if (left >= right) return 0;
        int mid = left + (right - left)/2;
        int[] temp = new int[right - left + 1];
        int count = mergeSort(nums, left, mid) + mergeSort(nums, mid + 1, right);//统计子归并的翻转对个数
        int i = left, reverseIdx = left, k = 0;
        for (int j = mid + 1; j <= right; j++) {
            //满足(i < j && nums[i] > 2*nums[j])的条件时, 结束循环开始统计翻转对, 顺便执行归并操作
            while (reverseIdx <= mid && nums[reverseIdx]/2.0 <= nums[j]) {
                //若不满足条件, 查看下一个数是否满足条件
                reverseIdx++;
            }
            //合并有序数组操作
            //在这一步, 所有左半区中比右半区的nums[j]小的元素都被放入了temp数组
            while (i <= mid && nums[i] < nums[j]) {
                temp[k++] = nums[i++];
            }
            count += mid + 1 - reverseIdx;//翻转对个数 = reverseIdx到mid之间的距离 + 1
            temp[k++] = nums[j];
        }
        //如果循环结束, 左半区中还有剩余元素未放入temp, 继续赋值操作
        while (i <= mid) {
            temp[k++] = nums[i++];
        }
        System.arraycopy(temp, 0, nums, left, temp.length);
        return count;
    }

    public static void main(String[] args) {
        ReversePairs rp = new ReversePairs();
        rp.reversePairs2(new int[] {6, 4, 3, 5, 1, 2});
    }
}
