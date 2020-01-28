package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 115. 不同子序列
 * @Description: hard
 */
public class DistinctSubsequences {
    /**
     * 1. DP
     * 思路：
     * 状态定义：DP[i][j]表示T[0~i]与S[0~j]的包含子序列个数
     * 转移方程：如果S[j]与T[i]相同, S[j]此时可以参与组成子序列, 也可以选择不参与组成子序列
     * 例如：S = babgba, T = ba, S[j]不参与组成子序列, 其子序列个数就是S = babgb, T = ba的结果
     *                           S[j]参与组成子序列, 其子序列个数就是S = babgb, T = b的结果
     *          如果S[i]与T[j]不相同, 那么S[i]一定不参与组成子序列
     *          综上, DP[i][j] = S[i] == T[j] ? DP[i - 1][j - 1] + DP[i][j - 1] : DP[i][j - 1]
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(n^2)
     * 执行用时：4ms
     * @param s
     * @param t
     * @return
     */
    public int numDistinct(String s, String t) {
        if (s.equals(t)) return 1;
        char[] s1 = s.toCharArray();
        char[] s2 = t.toCharArray();
        int[][] dp = new int[s1.length + 1][s2.length + 1];
        for (int i = 0; i < dp.length; i++) {
            dp[i][0] = 1;
        }
        for (int i = 1; i < dp.length; i++) {
            for (int j = 1; j < dp[0].length; j++) {
                dp[i][j] = s1[i - 1] == s2[j - 1] ? dp[i - 1][j - 1] + dp[i - 1][j] : dp[i - 1][j];
            }
        }
        return dp[s1.length][s2.length];
    }

    public static void main(String[] args) {
        DistinctSubsequences ds = new DistinctSubsequences();
        ds.numDistinct("babgbag", "bag");
    }
}
