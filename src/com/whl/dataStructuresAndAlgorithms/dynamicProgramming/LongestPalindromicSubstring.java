package com.whl.dataStructuresAndAlgorithms.dynamicProgramming;

/**
 * @author whl
 * @version V1.0
 * @Title: 5. 最长回文子串
 * @Description: normal
 */
public class LongestPalindromicSubstring {
    /**
     * 1. 暴力
     * 思路：遍历获取所有子串, 并且判断该子串是否为回文串且尝试更新最大回文串, 由于判断回文串的方法遍历了整个回文串, 因此复杂度较高
     * 时间复杂度：O(n^3)
     * 空间复杂度：O(1)
     * 执行用时：超时
     * @param s
     * @return
     */
    public String longestPalindrome1(String s) {
        int len = s.length();
        if (len <= 1) return s;
        int maxLen = 1;
        String str = s.substring(0, 1);
        for (int i = 1; i < len; i++) {
            for (int j = 0; j < i; j++) {
                String temp = s.substring(j, i + 1);
                if (isValid(temp) && temp.length() > maxLen) {
                    maxLen = temp.length();
                    str = temp;
                }
            }
        }
        return str;
    }

    private boolean isValid(String str) {
        for (int i = 0, j = str.length() - 1; i < j; i++, j--) {
            if (str.charAt(i) != str.charAt(j))
                return false;
        }
        return true;
    }

    /**
     * 2. 中心扩散法
     * 思路：枚举字符串s从0 ~ len - 2的所有字符, 并以该字符为中心进行扩散:
     * 假设字符串为奇数个数的abcba, 从c开始扩散, 并判断c左右两边的字符是否相同
     * 假设字符串为偶数个数的abba, 从b开始扩散, 先判断中间的两个b是否相同, 如果不同返回, 如果相同再以"bb"为中心进行扩散
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     * 执行用时：12ms
     * @param s
     * @return
     */
    public String longestPalindrome2(String s) {
        int len = s.length();
        if (len < 2) {
            return s;
        }
        int maxLen = 1;
        String res = s.substring(0, 1);
        // 中心位置枚举到 len - 2 即可
        for (int i = 0; i < len - 1; i++) {
            // left = right, 此时回文中心是一个字符, 回文串的长度是奇数
            String oddStr = centerSpread(s, i, i);
            // right = left + 1, 此时回文中心是一个空隙, 回文串的长度是偶数
            String evenStr = centerSpread(s, i, i + 1);
            String maxLenStr = oddStr.length() > evenStr.length() ? oddStr : evenStr;
            if (maxLenStr.length() > maxLen) {
                maxLen = maxLenStr.length();
                res = maxLenStr;
            }
        }
        return res;
    }

    private String centerSpread(String s, int left, int right) {
        int len = s.length();
        int i = left;
        int j = right;
        while (i >= 0 && j < len) {
            if (s.charAt(i) == s.charAt(j)) {
                i--;
                j++;
            } else {
                break;
            }
        }
        //跳出 while 循环时, 恰好满足 s.charAt(i) != s.charAt(j), 因此范围要取i++, j--的部分
        return s.substring(i + 1, j);
    }

    /**
     * 3. DP
     * 思路：
     * 创建一个二维DP用于存储子串S[left, right]是否为回文子串, 每当判断了dp[i][j]=true, 确认当前子串为回文子串后, 尝试更新最大回文子串
     * 这里判断是否为回文子串的细节是：
     *      若当前子串的首尾字符相同, 且去掉首尾后子串长度小于等于1时, 可以确认该子串为回文子串, 例如：aba -> b
     *      若当前子串的首尾字符相同, 且去掉收尾后子串长度大于1, 那么需要再判断去掉首尾的子串是否为回文子串
     * 时间复杂度：O(m^2)
     * 空间复杂度：O(m^2)
     * 执行用时：48ms
     * @param s
     * @return
     */
    public String longestPalindrome3(String s) {
        int len = s.length();
        if (len <= 1) return s;
        String res = s.substring(0, 1);
        int maxLen = res.length();
        boolean[][] dp = new boolean[len][len];
        for (int i = 1; i < len; i++) {
            for (int j = 0; j < i; j++) {
                if (s.charAt(i) == s.charAt(j) && (i - j + 1 - 2 <= 1 || dp[i - 1][j + 1])) {
                    dp[i][j] = true;
                    if (i - j + 1 > maxLen) {
                        maxLen = i - j + 1;
                        res = s.substring(j, i + 1);
                    }
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        LongestPalindromicSubstring lps = new LongestPalindromicSubstring();
        lps.longestPalindrome2("abba");
    }
}
