package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title: 66. 加一
 * @Description: easy
 */
public class PlusOne {
    /**
     * 1. 遍历
     * 思路：对序列末位加一, 如果等于10, 那么进位继续加一, 若不等于10, 直接return结果
     * 如果序列首位加一也等于10, 那么结果一定为10^n, 开辟一个长度加一的数组首部赋值为1返回即可
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param digits
     * @return
     */
    public int[] plusOne(int[] digits) {
        for (int i = digits.length - 1; i >= 0; i--) {
            if (++digits[i] % 10 != 0) return digits;
            digits[i] = 0;
        }
        digits = new int[digits.length + 1];
        digits[0] = 1;
        return digits;
    }
}
