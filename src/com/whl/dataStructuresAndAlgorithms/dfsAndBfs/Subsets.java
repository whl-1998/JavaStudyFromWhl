package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 78. 子集
 * @Description: normal
 */
public class Subsets {
    /**
     * 1. DFS回溯 + 枝减
     * 思路：参考77. 组合
     * 执行用时：1ms
     * @param nums
     * @return
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        recur(res, new ArrayList<>(), nums, 0);
        return res;
    }

    private void recur(List<List<Integer>> res, List<Integer> temp, int[] nums, int start) {
        res.add(new ArrayList<>(temp));
        for (int i = start; i < nums.length; i++) {
            temp.add(nums[i]);
            recur(res, temp, nums, i + 1);
            temp.remove(temp.size() - 1);
        }
    }

    public static void main(String[] args) {
        Subsets ss = new Subsets();
        ss.subsets(new int[]{1, 2, 3});
    }
}
