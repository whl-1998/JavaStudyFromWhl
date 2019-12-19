package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 47. 全排列II
 * @Description:
 */
public class PermutationsII {
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        Arrays.sort(nums);
        boolean[] flags = new boolean[nums.length];
        recur(result, temp, flags, nums);
        return result;
    }

    private void recur(List<List<Integer>> result, List<Integer> temp, boolean[] flags, int[] nums) {
        if (temp.size() == nums.length) {
            result.add(new ArrayList<>(temp));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (flags[i]) continue;
            if (i > 0 && nums[i] == nums[i - 1] && !flags[i - 1]) continue;
            flags[i] = true;
            temp.add(nums[i]);
            recur(result, temp, flags, nums);
            temp.remove(temp.size() - 1);
            flags[i] = false;
        }
    }

    public static void main(String[] args) {
        PermutationsII p = new PermutationsII();
        p.permuteUnique(new int[]{1,1,2});
    }
}
