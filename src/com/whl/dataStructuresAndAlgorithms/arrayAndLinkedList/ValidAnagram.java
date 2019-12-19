package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title: 242. 字母异位词
 * @Description: easy
 */
public class ValidAnagram {
    /**
     * 1. 计数数组
     * 思路：创建一个包含26位字符的数组，用于存储字符串中26个字符出现的次数
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：2ms
     * @param s
     * @param t
     * @return
     */
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;
        char[] sc = s.toCharArray();
        char[] tc = t.toCharArray();
        int[] temp = new int[26];
        for (int i = 0; i < sc.length; i++) {
            temp[sc[i] - 'a']++;
            temp[tc[i] - 'a']--;
        }
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != 0) return false;
        }
        return true;
    }
}
