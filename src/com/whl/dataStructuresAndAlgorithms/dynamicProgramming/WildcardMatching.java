package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 44. 通配符匹配
 * @Description: hard
 */
public class WildcardMatching {

    /**
     * 1. DP
     * 思路：解法参考10. 正则表达式匹配
     * 时间复杂度：O(m*n)
     * 空间复杂度：O(m*n)
     * 执行用时：7ms
     * @param s
     * @param p
     * @return
     */
    public boolean isMatch(String s, String p) {
        int sLen = s.length();
        int pLen = p.length();
        boolean[][] dp = new boolean[sLen + 1][pLen + 1];
        dp[0][0] = true;
        for (int i = 1; i < dp[0].length; i++) {
            if (p.charAt(i - 1) == '*') {
                dp[0][i] = dp[0][i - 1];
            }
        }
        for (int i = 1; i < dp.length; i++) {
            for (int j = 1; j < dp[0].length; j++) {
                if (s.charAt(i - 1) == p.charAt(j - 1) || p.charAt(j - 1) == '?') {
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (p.charAt(j - 1) == '*') {
                    dp[i][j] = dp[i - 1][j] || dp[i][j - 1];
                }
            }
        }
        return dp[sLen][pLen];
    }

    public static void main(String[] args) {
        WildcardMatching wm = new WildcardMatching();
        wm.isMatch("aa", "*");
    }
}
