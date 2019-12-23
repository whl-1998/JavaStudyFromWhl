package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 17. 电话号码组合
 * @Description: normal
 */
public class LetterCombinationsOfAPhoneNumber {

    /**
     * 1. dfs递归
     * 思路：有点类似77. 组合
     * 获取digits[i]对应字符串, 将字符串的每一个字符与digits[i + 1]对应的字符串进行组合, 当组合后的字符串长度 == digits.length, 添加到res结果集中。
     * 执行用时：0ms
     * @param digits
     * @return
     */
    final String[] numbers = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};
    public List<String> letterCombinations(String digits) {
        List<String> res = new ArrayList<>();
        recur(res, digits, "", 0);
        return res;
    }

    private void recur(List<String> res, String digits, String curr, int level) {
        if (level == digits.length()) {
            res.add(curr);
            return;
        }
        char[] temp = numbers[digits.charAt(level) - '0'].toCharArray();
        for (int i = 0; i < temp.length; i++) {
            recur(res, digits, curr + temp[i], level + 1);
        }
    }

    public static void main(String[] args) {
        LetterCombinationsOfAPhoneNumber lc = new LetterCombinationsOfAPhoneNumber();
        lc.letterCombinations("23");
    }
}
