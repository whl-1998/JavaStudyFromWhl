package com.whl.dataStructuresAndAlgorithms.string;

/**
 * @author whl
 * @version V1.0
 * @Title: 14. 最长公共前缀
 * @Description:
 */
public class LongestCommonPrefix {
    /**
     * 1. 前缀匹配法
     * 思路：以strs[0]作为"标杆", 遍历strs[1 ~ n], 获取strs[i]与strs[0]的最长公共前缀, 将这个公共前缀作为"标杆"继续遍历
     * 时间复杂度：O(strs.length * Max((String s : strs).length)
     * 空间复杂度：O(Max((String s : strs).length) //如果按字符串为数组为前提进行计算的话是这个复杂度
     * 执行用时：0ms
     * @param strs
     * @return
     */
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0 || strs == null) return "";
        String pre = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (!strs[i].startsWith(pre)) {
                pre = pre.substring(0, pre.length() - 1);
            }
        }
        return pre;
    }

    public static void main(String[] args) {
        LongestCommonPrefix lcp = new LongestCommonPrefix();
        lcp.longestCommonPrefix(new String[]{"flower","flow","flight"});
    }
}
