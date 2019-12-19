package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 91. 解码方法
 * @Description: normal
 */
public class DecodeWays {
    /**
     * 1. DP[i]
     * 思路：
     * 状态定义：DP[i]为字符串0 ~ i部分的解码方法总数
     * 转移方程： 1. DP[0] = 1, 确保DP[2]能够被正确递推
     *           2. 当字符串个数只有一个, 解码方法只能为1或0, DP[1] = S[0] == '0' ? 0 : 1;
     *           3. 当字符串个数为N个, S[0 ~ i]的解码方法有两种：
     *                 1. S[0 ~ i]由S[0 ~ i - 1]解码而来, 要求S[i]的值为1 ~ 9：DP[i] += (0 < S[i] < 9) ? DP[i - 1] : 0)
     *                 2. S[0 ~ i]由S[0 ~ i - 2]解码而来, 要求S[i - 2 ~ i - 1]的组合为10 ~ 26：DP[i] += (10 < S[i] < 26) ? DP[i - 2] : 0)
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：1ms
     * @param s
     * @return
     */
    public int numDecodings1(String s) {
        int len = s.length();
        int[] dp = new int[len + 1];
        dp[0] = 1;
        dp[1] = s.charAt(0) == '0' ? 0 : 1;
        for (int i = 2; i <= len; i++) {
            int first = s.charAt(i - 1) - '0';
            int second = (s.charAt(i - 2) - '0') * 10 + first;
            dp[i] += (first > 0 && first < 10) ? dp[i - 1] : 0;
            dp[i] += (second > 9 && second < 27) ? dp[i - 2] : 0;
        }
        return dp[len];
    }

    /**
     * 2. 递归
     * 思路：由题可知, 解码方式有两种, 一种是只选择一个字符进行解码, 且这个字符范围是1 ~ 9; 还有一种是选择两个字符进行解码, 且这两个字符组合的范围是10 ~ 26
     * 例如：字符串226的解码方式F(226) = 选择一个字符'6'解码 F(6) + 选择两个字符解码'26' F(26)
     * 那么这个问题其实就和爬楼梯问题基本一致了, 上到第n级台阶一定是n - 1级台阶跨一步或者n - 2级台阶跨两步
     * 同理解码到第n个字符串一定是从第n + 1个字符串解码而来或者从第n + 2个字符解码而来
     * 重复子问题找到了, 剩下的就是条件判断什么时候可以解码了：
     *      1. 如果当前解码第n个字符, 这个字符首先必须不为0, 否则解码次数为0：S[n] == 0 ? return 0
     *      2. 获取S[n + 1 ~ S.length]解码的方法总数ans1 = recur(S[n + 1])
     *      3. 获取S[n + 2 ~ S.length]解码的方法总数ans2 = S[n + 1 ~ n + 2] <= 26 ? recur(S[n + 2]) : 0
     * 时间复杂度：O(2^n)
     * 空间复杂度：O(1)
     * 执行用时：653ms
     * @param s
     * @return
     */
    public int numDecodings2(String s) {
        return recur(s, 0);
    }

    private int recur(String s, int start) {
        if (start == s.length()) {
            return 1;
        }
        if (s.charAt(start) == '0') {
            return 0;
        }
        int ans1 = recur(s, start + 1);
        int ans2 = 0;
        if (start < s.length() - 1) {
            int num = (s.charAt(start) - '0') * 10 + (s.charAt(start + 1) - '0');
            ans2 = num < 27 ? recur(s, start + 2) : 0;
        }
        return ans1 + ans2;
    }

    /**
     * 3. 记忆化递归
     * 思路：基于解法2加上递归缓存
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：15ms
     * @param s
     * @return
     */
    public int numDecodings3(String s) {
        int[] cache = new int[s.length()];
        return recur(s, 0, cache);
    }

    private int recur(String s, int start, int[] cache) {
        if (cache[start] != 0) return cache[start + 1];
        if (start == s.length()) {
            return cache[start] = 1;
        }
        if (s.charAt(start) == '0') {
            return cache[start] = 0;
        }
        int ans1 = recur(s, start + 1, cache);
        int ans2 = 0;
        if (start < s.length() - 1) {
            int num = (s.charAt(start) - '0') * 10 + (s.charAt(start + 1) - '0');
            ans2 = num < 27 ? recur(s, start + 2, cache) : 0;
        }
        return cache[start] = ans1 + ans2;
    }

    public static void main(String[] args) {
        DecodeWays dw = new DecodeWays();
        System.out.println(dw.numDecodings1("226"));
    }
}
