package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 152. 乘积最大子序和
 * @Description:
 */
public class MaximumProductSubarray {
    public int maxProduct(int[] nums) {
        int[][] dp = new int[nums.length][2];
        dp[0][0] = nums[0];
        dp[0][1] = nums[0];
        int max = dp[0][0];
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < 0) {
                int temp = dp[i - 1][1]; dp[i - 1][1] = dp[i - 1][0]; dp[i - 1][0] = temp;
            }
            dp[i][0] = Math.max(dp[i - 1][0] * nums[i], nums[i]);
            dp[i][1] = Math.min(dp[i - 1][1] * nums[i], nums[i]);
            max = Math.max(dp[i][0], max);
        }
        return max;
    }

    public static void main(String[] args) {
        MaximumProductSubarray mp = new MaximumProductSubarray();
        mp.maxProduct(new int[]{2, 3, -2, 4});
    }
}
