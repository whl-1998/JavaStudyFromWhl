package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 1143. 最长公共子序列
 * @Description: normal
 */
public class LongestCommonSubsequence {
    /**
     * 1. DP
     * 思路：
     * @param text1
     * @param text2
     * @return
     */
    public int longestCommonSubsequence(String text1, String text2) {
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];
        for (int i = 1; i < dp.length; i++) {
            for (int j = 1; j < dp[0].length; j++) {
                int m = i - 1, n = j - 1;
                dp[i][j] = text1.charAt(m) == text2.charAt(n) ? dp[i - 1][j - 1] + 1 : Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[text1.length()][text2.length()];
    }

    public static void main(String[] args) {

    }
}
