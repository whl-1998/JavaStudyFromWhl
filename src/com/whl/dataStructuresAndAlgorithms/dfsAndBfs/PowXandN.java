package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

/**
 * @author whl
 * @version V1.0
 * @Title: 50. Pow(x, n)
 * @Description: normal
 */
public class PowXandN {
    /**
     * 1. 递归
     * 思路：考虑两种情况：n为奇数/n为偶数
     * 若n为奇数, 递归(x*x, n/2)还需要补乘一个x
     * 若n为偶数, 递归(x*x, n/2)
     * 时间复杂度：O(logN)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param x
     * @param n
     * @return
     */
    public double myPow(double x, int n) {
        if (x == -1 && (n & 1) != 0) return -1;
        if (n >= 0) {
            return recur(x, n);
        } else {
            return 1/recur(x, n);
        }
    }

    private double recur(double x, int n) {
        if (n == 0) return 1;
        if ((n & 1) == 0) {
            return recur(x * x, n/2);
        } else {
            return recur(x * x, n/2) * x;
        }
    }

    public static void main(String[] args) {
        PowXandN px = new PowXandN();
        px.myPow(2.00000, 10);
    }
}
