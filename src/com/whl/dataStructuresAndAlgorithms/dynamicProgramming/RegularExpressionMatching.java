package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 10. 正则表达式匹配
 * @Description: hard
 */
public class RegularExpressionMatching {
    /**
     * 1. DP
     * 思路：
     * 通过创建一个二维数组来存储S[0~i]和P[0~j]是否满足表达式匹配
     * 		首先可以明确一种最简单的情况：
     * 			1. 当S[:i]和P[:j]的最后一个字符如果相同, 那么只需要判断S[:i - 1]和P[:j - 1]的部分即可
     * 			2. 当P[:j]的最后一个字符为'.', 此时可以匹配任意单个字符, 那么判断方式与第一种相同
     * 				以上两种情况的动态方程可以确定为：DP[i][j] = DP[i - 1][j - 1]
     * 			3. 当P[:j]的最后一个字符为'*', 可以选择匹配0个或n个前一个字符, 那么情况如下：
     * 				3.1 当S[:i]和P[:j - 1]的最后一个字符不相同, 且P[j] = '*'时, 可以考虑匹配0个前字符看是否能够匹配
     * 					此时DP[i][j] = DP[i][j - 2]
     * 				3.2	当S[:i]和P[:j - 1]的最后一个字符相同, 可以选择匹配n个前字符, 那么'*'选择匹配了n个字符后需要观察选择匹配n - 1个字符是否能匹配
     * 					例如：s：aaa		p：a*
     * 					'*'匹配1个a：s：aaa		p：a  --->  s：aa	p："" 	false
     * 					'*'匹配2个a：s：aaa		p：aa --->  s：aa	p: ""	false
     * 					'*'匹配3个a：s：aaa		p：aaa--->	s: aaa	p: ""	true
     * 					S[a, a, a]  P[a, *]	需要观察S[a, a]  p[a, *]是否成功匹配了, 如果成功了, 那么*可以再多匹配一个a
     * 					此时DP[i][j] = DP[i - 1][j]
     * 时间复杂度：O(m*n)
     * 空间复杂度：O(m*n)
     * 执行用时：5ms
     * @param s
     * @param p
     * @return
     */
    public boolean isMatch(String s, String p) {
        int sLen = s.length();
        int pLen = p.length();
        boolean[][] dp = new boolean[sLen + 1][pLen + 1];
        dp[0][0] = true;
        for (int i = 2; i < dp[0].length; i++) {
            if (p.charAt(i - 1) == '*') {
                dp[0][i] = dp[0][i - 2];
            }
        }
        for (int i = 1; i < dp.length; i++) {
            for (int j = 1; j < dp[0].length; j++) {
                if (s.charAt(i - 1) == p.charAt(j - 1) || p.charAt(j - 1) == '.') {
                    dp[i][j] = dp[i - 1][j - 1];
                } else if (p.charAt(j - 1) == '*') {
                    dp[i][j] = (dp[i][j - 2] || (dp[i - 1][j] && (p.charAt(i - 2) == s.charAt(i - 1) || p.charAt(i - 2) == '.')));
                }
            }
        }
        return dp[sLen][pLen];
    }

    public static void main(String[] args) {
        RegularExpressionMatching rem = new RegularExpressionMatching();
        rem.isMatch("aa", "a*");
    }
}
