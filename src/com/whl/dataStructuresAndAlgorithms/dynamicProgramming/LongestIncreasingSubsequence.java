package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 300. 最长上升子序列
 * @Description: normal
 */
public class LongestIncreasingSubsequence {
    /**
     * 1. DP
     * 思路：
     * 状态定义：DP[i] = 以nums[i]结尾的最大上升子序列长度, 注意并不是0 ~ i部分的最大子序列长度, 如果考虑到[10, 9, 2, 5, 3, 1], 结尾DP[5]的结果仍然是1
     * 转移方程：遍历到nums[i]时, 只要遍历j = 0 ~ (i - 1)这部分, 找到比nums[i]小的数, 那么nums[i]就可以追加到以nums[j]结尾的最大上升子序列, 长度 = DP[j] + 1
     *          并且目的是寻找最大值, 因此DP[i] = nums[i] > nums[j = 0 ~ (i - 1)] ? max(DP[i], DP[j] + 1 : DP[i])
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     * 执行用时：16ms
     * @param nums
     * @return
     */
    public int lengthOfLIS1(int[] nums) {
        if (nums.length == 1) return 1;
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        int res = 0;
        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                dp[i] = nums[j] < nums[i] ? Math.max(dp[j] + 1, dp[i]) : dp[i];
            }
            res = Math.max(res, dp[i]);
        }
        return res;
    }

    /**
     * 2. 贪心算法 + 二分查找
     * 思路：只要让前面的升序子序列中每一个数都尽量最小, 那么这个升序子序列长度增长的可能性就越大
     *       例如：升序子序列1：[1,2,3,4], 升序子序列2：[1,2,3,99], 一定是子序列1增长的可能性更大
     *       那么就可以通过构建这么一个"每一个数都尽量小"的子序列, 并返回它的长度即可
     *       遍历nums, 当nums[i]比最小升序子序列的末尾元素大, 那么就添加到序列末尾
     *                 当nums[i]比最小升序子序列的末尾元素小, 那么就更新这个最小升序子序列: 通过二分查找获取nums[i]应该覆盖的位置并覆盖
     *                  (上面的两步操作可以通过binarySearch一步完成, 通过二分查找range = [0 ~ 子序列末尾idx + 1], target = nums[i], 也就是寻找有序数组的偏移点算法)
     * 时间复杂度：O(nlogN)
     * 空间复杂度：O(n)
     * 执行用时：1ms
     * @param nums
     * @return
     */
    public int lengthOfLIS2(int[] nums) {
        int[] tails = new int[nums.length];
        int res = 0;
        for (int x : nums) {
            //binary search to find the update index(point left)
            int left = 0, right = res;
            while (left < right) {
                int mid = (left + right) >> 1;
                if (tails[mid] < x) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }
            tails[left] = x;//update the tails
            if (left == res) {
                res++;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        LongestIncreasingSubsequence li = new LongestIncreasingSubsequence();
        System.out.println(li.lengthOfLIS2(new int[]{3,5,6,2,5,4,19,5,6,7,12}));
    }
}
