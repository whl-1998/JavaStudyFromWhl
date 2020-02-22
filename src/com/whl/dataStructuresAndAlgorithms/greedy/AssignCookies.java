package com.whl.dataStructuresAndAlgorithms.greedy;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 455. 分发饼干
 * @Description: easy
 */
public class AssignCookies {
    /**
     * 1. 贪心算法
     * 思路：对两个数组进行排序, 保证每次都用最小的饼干满足胃口最小的小朋友, 以留着更大的饼干去满足胃口更大的小朋友
     * 时间复杂度：O(nLogN + n)
     * 执行用时：11ms
     * @param g
     * @param s
     * @return
     */
    public int findContentChildren(int[] g, int[] s) {
        Arrays.sort(g);
        Arrays.sort(s);
        int i = 0;
        int j = 0;
        while (i < g.length && j < s.length) {
            if (g[i] <= s[j]) {
                i++;
                j++;
            } else {
                j++;
            }
        }
        return i;
    }

    public static void main(String[] args) {
        AssignCookies ac = new AssignCookies();
        ac.findContentChildren(new int[]{1,2,3}, new int[]{1,1});
    }
}
