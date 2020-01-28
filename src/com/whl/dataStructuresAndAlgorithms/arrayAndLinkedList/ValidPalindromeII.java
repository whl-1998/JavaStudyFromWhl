package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title: 680. 验证回文串II
 * @Description: easy
 */
public class ValidPalindromeII {
    /**
     * 1. 双指针
     * 思路：双指针分别指向字符串两端, 当第一次遇到左右指针指向的字符不相同时, 可以删除一次字符
     * 可以继续判断[left + 1, right]和[left, right - 1]部分是否符合回文串要求
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：5ms
     * @param s
     * @return
     */
    public boolean validPalindrome(String s) {
        char[] temp = s.toCharArray();
        int left = 0;
        int right = temp.length - 1;
        while (left < right && temp[left] == temp[right]) {
            left++;
            right--;
        }
        if (isValid(temp, left + 1, right))
            return true;
        if (isValid(temp, left, right - 1))
            return true;
        return false;
    }

    private boolean isValid(char[] temp, int left, int right) {
        while (left < right) {
            if (temp[left++] != temp[right--]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
    }
}
