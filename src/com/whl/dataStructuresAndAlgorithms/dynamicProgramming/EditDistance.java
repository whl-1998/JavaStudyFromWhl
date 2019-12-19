package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 72. 编辑距离
 * @Description:
 */
public class EditDistance {
    /**
     * 1. DP
     * 思路：
     * 状态定义：DP[i][j]表示word1[0~i]和word2[0~j]这部分的编辑距离
     * 动态方程：1. 当word1与word2的末尾字符相同, 那么DP[i][j]的结果就是word1和word2同时去掉末尾字符的编辑距离
     *          2. 当word1与word2的末尾字符不相同, 此时有三种情况：
     *              word1可以选择替换它的末尾字符为word2的末尾字符, 例如：word1：abnd, word2：asg ---> word1：abng, word2：asg ---> word1：abn, word2：as, 此时DP[i][j] = 1 + DP[i - 1][j - 1]
     *              word1可以选择删除它的末尾字符, 例如：word1：abnd, word2：asg ---> word1：abn, word2：asg, 此时DP[i][j] = 1 + DP[i - 1][j]
     *              wrod1可以选择增加它的末尾字符为word2的末尾字符, 例如：word1：abnd, word2：asg ---> word1：abndg, word2：asg ---> word1：abnd, word2：as, 此时DP[i][j] = 1 + DP[i][j - 1]
     *             我们只需要寻找这三种情况的最小值即可获取当前的最小编辑距离
     *          综上：DP[i][j] = word1.charAt(m) == word2.charAt(n) ? dp[i - 1][j - 1] : Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(n^2)
     * 执行用时：7ms
     * @param word1
     * @param word2
     * @return
     */
    public int minDistance1(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 0; i < dp.length; i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i < dp[0].length; i++) {
            dp[0][i] = i;
        }
        for (int i = 1; i < dp.length; i++) {
            for (int j = 1; j < dp[0].length; j++) {
                int m = i - 1, n = j - 1;
                dp[i][j] = word1.charAt(m) == word2.charAt(n) ? dp[i - 1][j - 1] : Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
            }
        }
        return dp[word1.length()][word2.length()];
    }
}
