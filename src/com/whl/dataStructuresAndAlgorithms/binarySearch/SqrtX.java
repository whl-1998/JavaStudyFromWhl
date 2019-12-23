package com.whl.dataStructuresAndAlgorithms.binarySearch;

/**
 * @author whl
 * @version V1.0
 * @Title: 69. x的平方根
 * @Description: easy
 */
public class SqrtX {
    /**
     * 1. 暴力
     * 思路：从0开始遍历, 如果遍历到某个数的平方大于x, 那么就返回这个数 - 1
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：15ms
     * @param x
     * @return
     */
    public int mySqrt1(int x) {
        int res = 0;
        while (res * res <= x) {
            res = res + 1;
        }
        return res - 1;
    }

    /**
     * 2. 二分查找
     * 思路：基于解法1, 由于0 ~ n这部分有序, 因此可以对0 ~ n采用二分查找：
     * 若查找到的数的平方比x大, 那么继续在i ~ j - 1部分查找
     * 若查找到的数的平方比x小, 这个数有可能是结果, 也可能是更大的数, 那么继续在i ~ j部分查找
     * 时间复杂度：O(logN)
     * 空间复杂度：O(1)
     * 执行用时：2ms
     * @param x
     * @return
     */
    public int mySqrt2(int x) {
        long i = 0;
        long j = x/2 + 1;
        while (i < j) {
            long mid = (i + j + 1) >> 1;
            if (mid * mid > x) {
                j = mid - 1;
            } else {
                i = mid;
            }
        }
        return (int)i;
    }

    /**
     * 3. 牛顿迭代
     * 思路：迭代公式：res = (res + x/res)/2
     * 执行用时：1ms
     * @param x
     * @return
     */
    public int mySqrt3(int x) {
        long res = x;
        while (res * res > x) {
            res = (res + x/res)/2;
        }
        return (int)res;
    }


    public static void main(String[] args) {
        SqrtX sx = new SqrtX();
        System.out.println(sx.mySqrt1(8));
    }
}
