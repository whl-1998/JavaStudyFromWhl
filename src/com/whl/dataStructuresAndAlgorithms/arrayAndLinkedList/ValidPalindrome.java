package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title: 125. 验证回文串
 * @Description: easy
 */
public class ValidPalindrome {
    /**
     * 1. 双指针
     * 思路：设置左右指针, 判断两端字符是否相同, 如果相同左右指针各进一位, 如果不相同return false
     * 值的注意的是: 需要通过while判断字符是否合法, 如果不合法则指针左/右移
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：5ms
     * @param s
     * @return
     */
    public boolean isPalindrome(String s) {
        s = s.toLowerCase();
        char[] temp = s.toCharArray();
        int left = 0;
        int right = temp.length - 1;
        while (left < right) {
            while (left < right && ((temp[left] < 'a' || temp[left] > 'z') && (temp[left] < '0' || temp[left] > '9'))) {
                left++;
            }
            while (left < right && ((temp[right] < 'a' || temp[right] > 'z') && (temp[right] < '0' || temp[right] > '9'))) {
                right--;
            }
            if (s.charAt(left++) != s.charAt(right--)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        ValidPalindrome vp = new ValidPalindrome();
        System.out.println(vp.isPalindrome("race a car"));
        String str = "A man, a plan, a canal: Panama";
        System.out.println(str.toLowerCase());
    }
}
