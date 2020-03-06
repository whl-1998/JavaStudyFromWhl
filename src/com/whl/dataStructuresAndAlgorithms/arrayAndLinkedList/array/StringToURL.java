package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description: easy
 */
public class StringToURL {
    /**
     * 1. 字符数组替换
     * 思路：创建一个新的字符数组, 将空格字符替换为 %20 存放到新数组即可
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：12ms
     * @param S
     * @param length
     * @return
     */
    public String replaceSpaces1(String S, int length) {
        char[] temp = S.toCharArray();
        int blankNum = 0;
        for (int i = 0; i < length; i++) {
            if (temp[i] == ' ') {
                blankNum++;
            }
        }
        char[] result = new char[length + 2 * blankNum];
        int k = 0;
        for (int i = 0; i < length; i++) {
            if (temp[i] != ' ') {
                result[k++] = temp[i];
            } else {
                result[k++] = '%';
                result[k++] = '2';
                result[k++] = '0';
            }
        }
        return String.valueOf(result);
    }

    /**
     * 2. String替换法
     * 思路：直接调用String的原生API
     * 时间复杂度：O(n)
     * 执行用时：11ms
     * @param S
     * @param length
     * @return
     */
    public String replaceSpaces2(String S, int length) {
        return S.substring(0, length).replace(" ", "%20");
    }

    public static void main(String[] args) {
        StringToURL stu = new StringToURL();
        stu.replaceSpaces1("Mr John Smith    ", 13);
    }
}
