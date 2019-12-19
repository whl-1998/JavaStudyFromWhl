package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 70. 爬楼梯
 * @Description:
 */
public class ClimbingStairs {
    /**
     * 1. 斐波那契数列递归
     * 思路：如果爬第n级台阶, 那么只存在两种可能, 从n-1级台阶跨一步到n级台阶, 或者从n-2级跨2步到n级台阶
     * 时间复杂度：O(2^n)
     * 空间复杂度：O(1)
     * 提交超时
     * @param n
     * @return
     */
    public int climbStairs1(int n) {
        if (n <= 2) return n;
        return climbStairs1(n - 1) + climbStairs1(n - 2);
    }

    /**
     * 2. 记忆化递归
     * 思路：通过数组缓存每一步的计算结果, 避免重复计算
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param n
     * @return
     */
    public int climbStairs2(int n) {
        int[] cache = new int[n + 1];
        return recur(cache, n);
    }

    private int recur(int[] cache, int n) {
        if (cache[n] != 0) return cache[n];
        if (n <= 2) return n;
        return cache[n] = recur(cache, n - 1) + recur(cache, n - 2);
    }

    /**
     * 3：动态规划
     * 思路：
     * 状态定义：DP[i] = 第i级台阶的走法数
     * 递推方程：DP[i] = DP[i - 1] + DP[i - 2]
     * 时间复杂度O(n)
     * 空间复杂度O(n)
     * 执行用时：0ms
     * @param n
     * @return
     */
    public int climbStairs3(int n) {
        if (n <= 2) return n;
        int[] dp = new int[n];
        dp[0] = 1;
        dp[1] = 2;
        for (int i = 2; i < n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        return dp[n - 1];
    }

    /**
     * 4. DP空间优化
     * 思路：由于动态规划方法每次都只需要操作三个变量, 因此将数组赋值操作简化为变量之间的赋值操作
     * 时间复杂度O(n)
     * 空间复杂度O(1)
     * 执行用时：0ms
     * @param n
     * @return
     */
    public int climbStairs4(int n) {
        if (n <= 2) return n;
        int temp = 2, prev = 1, curr = 0;
        for (int i = 3; i <= n; i++) {
            curr = prev + temp;
            prev = temp;
            temp = curr;
        }
        return curr;
    }



    public static void main(String[] args) {
        ClimbingStairs cs = new ClimbingStairs();
        cs.climbStairs2(5);
    }
}
