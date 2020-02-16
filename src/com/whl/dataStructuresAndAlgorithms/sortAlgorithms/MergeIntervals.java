package com.whl.dataStructuresAndAlgorithms.sortAlgorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 56. 合并区间
 * @Description:
 */
public class MergeIntervals {
    /**
     * 解法1：遍历比较二维数组
     * 思路：若相邻的区间, 前一位的末尾大于等于后一位的起始, 我们就可以将其合并为一个区间, 新的区间末尾为前一位末尾和后一位末尾的较大值
     * 时间复杂度：O(nLogN + n)
     * 空间复杂度：O(n)
     * 执行用时：11ms
     * @param intervals
     * @return
     */
    public int[][] merge(int[][] intervals) {
        //对二维数组按前一位进行排序
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> list = new ArrayList<>();
        int[] temp = intervals[0];
        list.add(temp);
        for (int[] ints : intervals) {
            //若temp的末位 >= ints的首位, 那么则可以合并
            if (ints[0] <= temp[1]) {
                //合并后的首位是temp[0]不需要修改, 末位是ints[1]和temp[1]中的较大值
                temp[1] = Math.max(temp[1], ints[1]);
            } else {//若能合并, 则ints作为当前temp, 继续判断是否能与下一个二维数组合并
                temp = ints;
                list.add(temp);
            }
        }
        //将list中的结果作为二维数组输出
        return list.toArray(new int[list.size()][]);
    }

    public static void main(String[] args) {
        MergeIntervals mi = new MergeIntervals();
        mi.merge(new int[][] {
                {1, 3},
                {2, 6},
                {8, 10},
                {15, 18}
        });
    }
}
