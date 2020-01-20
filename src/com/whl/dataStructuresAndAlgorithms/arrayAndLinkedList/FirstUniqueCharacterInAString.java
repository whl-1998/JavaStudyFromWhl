package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class FirstUniqueCharacterInAString {
    /**
     * 1. hashmap
     * 思路：创建一个count数组用于记录字符串中每个字符出现的次数, 遍历字符串, 观察每一个字符对应count数组中的值是否为1
     * 时间复杂度：O(N)
     * 空间复杂度：O(N)
     * @param s
     * @return
     */
    public int firstUniqChar(String s) {
        int[] counter = new int[26];
        char[] temp = s.toCharArray();
        for (char c : temp) {
            counter[c - 'a']++;
        }
        for (int i = 0; i < temp.length; i++) {
            if (counter[temp[i] - 'a'] == 1) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
        FirstUniqueCharacterInAString f = new FirstUniqueCharacterInAString();
        System.out.println(f.firstUniqChar("z"));
    }
}
