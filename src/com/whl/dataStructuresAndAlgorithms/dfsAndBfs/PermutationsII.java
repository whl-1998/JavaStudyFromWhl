package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 47. 全排列II
 * @Description: normal
 */
public class PermutationsII {
    /**
     * 1. dfs + 枝减
     * 思路：在47. 全排列的基础上添加一个boolean数组进行判重
     * 1. 将nums进行排序, 保证判重有效性
     * 2. 如果当前元素已经添加到组合中, continue
     * 3. 如果nums[i]与nums[i - 1]相同, 并且nums[i - 1]尚未使用, 说明一定会出现重复组合, continue
     * 执行用时：2ms
     * @param nums
     * @return
     */
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(nums);
        recur(res, new ArrayList<>(), new boolean[nums.length], nums);
        return res;
    }

    private void recur(List<List<Integer>> res, List<Integer> temp, boolean[] flag, int[] nums) {
        if (temp.size() == nums.length) {
            res.add(new ArrayList<>(temp));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (flag[i]) continue;
            if (i > 0 && nums[i] == nums[i - 1] && !flag[i - 1]) continue;
            temp.add(nums[i]);
            flag[i] = true;
            recur(res, temp, flag, nums);
            flag[i] = false;
            temp.remove(temp.size() - 1);
        }
    }

    public static void main(String[] args) {
        PermutationsII p = new PermutationsII();
        p.permuteUnique(new int[]{1,1,2});
    }
}
