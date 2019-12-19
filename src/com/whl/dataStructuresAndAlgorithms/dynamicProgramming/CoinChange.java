package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author whl
 * @version V1.0
 * @Title: 322. 零钱兑换
 * @Description:
 */
public class CoinChange {
    /**
     * 1. 暴力递归
     * 思路：例如coins = {1,2,5}, amount = 11, f(11) = min(f(11 - 1), f(11 - 2), f(11 - 5)
     * 时间复杂度：O(coins.length * coins.length ^ n)
     * 空间复杂度：O(1)
     * 提交超时
     * @param coins
     * @param amount
     * @return
     */
    public int coinChange1(int[] coins, int amount) {
        if (amount == 0) return 0;
        int ans = Integer.MAX_VALUE;
        for (int coin : coins) {
            if (amount - coin < 0) continue;
            int subProblem = coinChange1(coins, amount - coin);
            if (subProblem == -1) continue;
            ans = Math.min(subProblem + 1, ans);
        }
        return ans == Integer.MAX_VALUE ? -1 : ans;
    }

    /**
     * 2. 记忆化递归
     * 思路：构建缓存数组存储计算过的的值, 避免重复计算
     * 时间复杂度：O(coins.length * n)
     * 空间复杂度：O(n)
     * 执行用时：21ms
     * @param coins
     * @param amount
     * @return
     */
    public int coinChange2(int[] coins, int amount) {
        int[] cache = new int[amount + 1];
        return recur(cache, coins, amount);
    }

    private int recur(int[] cache, int[] coins, int amount) {
        if (cache[amount] != 0) return cache[amount];
        if (amount == 0) return 0;
        int res = Integer.MAX_VALUE;
        for (int c : coins) {
            if (amount - c < 0) continue;
            int subProblem = recur(cache, coins, amount - c);
            if (subProblem == -1) continue;
            res = Math.min(res, subProblem + 1);
        }
        return cache[amount] = res == Integer.MAX_VALUE ? -1 : res;
    }

    /**
     * 3. DP
     * 思路：状态定义：DP[i] = 兑换金额i所需要的最小硬币数
     *       递推方程：DP[i] = min(DP[当前金额i - coins[0~k]])
     * 时间复杂度：O(k * n)
     * 空间复杂度：O(n)
     * 执行用时：12ms
     * @param coins
     * @param amount
     * @return
     */
    public int coinChange3(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        for (int i = 1; i < dp.length; i++) {
            int res = -1;
            for (int c : coins) {
                if (i - c >= 0 && dp[i - c] != -1) {
                    int subProblem = dp[i - c] + 1;
                    res = res == -1 ? subProblem : Math.min(res, subProblem);
                }
            }
            dp[i] = res;
        }
        return dp[amount];
    }

    /**
     * 4. BFS
     * 思路：通过广度优先遍历F(N)的所有子问题, 若发现子问题F(N - coin[0~K]) == 0, 说明在当前层发现零钱兑换完毕, 兑换的数量就是当前的层数
     * 为了避免大量的重复计算, 采用set去重
     * 执行用时：90ms
     * @param coins
     * @param amount
     * @return
     */
    public int coinChange4(int[] coins, int amount) {
        Queue<Integer> queue = new LinkedList<>();
        HashSet<Integer> set = new HashSet<>();
        queue.offer(amount);
        set.add(amount);
        int res = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            while (size-- > 0) {
                int curr = queue.poll();
                if (curr == 0) return res;
                for (int c : coins) {
                    if (curr - c >= 0 && set.add(curr - c)) {
                        queue.offer(curr - c);
                    }
                }
            }
            res++;
        }
        return -1;
    }

    public static void main(String[] args) {
        CoinChange cc = new CoinChange();
        System.out.println(cc.coinChange4(new int[]{1,2,5}, 11));
    }
}
