package com.whl.dataStructuresAndAlgorithms.binarySearch;

/**
 * @author whl
 * @version V1.0
 * @Title: 367. 完全平方数
 * @Description: easy
 */
public class ValidPerfectSquare {
    /**
     * 1. 暴力
     * 思路：一个数的平方根一定小于num/2, 那么从num/2 + 1 ~ 0遍历寻找一个数的平方 = num, 找到返回true, else false
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：424ms
     * @param num
     * @return
     */
    public boolean isPerfectSquare1(int num) {
        int res = num/2 + 1;
        while (res >= 0) {
            if (res * res == num) {
                return true;
            }
            res--;
        }
        return false;
    }

    /**
     * 2. 二分查找
     * 思路：参考69. SqrtX的题解2
     * 时间复杂度：O(logN)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param num
     * @return
     */
    public boolean isPerfectSquare2(int num) {
        int i = 0, j = num/2 + 1;
        while (i < j) {
            int mid = (i + j + 1) >> 1;
            if (mid * mid == num) {
                return true;
            } else if (mid * mid > num){
                j = mid - 1;
            } else {
                i = mid;
            }
        }
        return false;
    }

    /**
     * 3. 牛顿迭代
     * 思路：res = (res + num/res)/2
     * 执行用时：0ms
     * @param num
     * @return
     */
    public boolean isPerfectSquare3(int num) {
        long res = num;
        while (res * res >= num) {
            res = (res + num/res)/2;
            if (res * res == num) {
                return true;
            }
        }
        return false;
    }



    public static void main(String[] args) {
        ValidPerfectSquare vp = new ValidPerfectSquare();
        vp.isPerfectSquare2(1);
    }
}
