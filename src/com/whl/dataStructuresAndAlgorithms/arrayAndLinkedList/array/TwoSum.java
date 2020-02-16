package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

import java.util.HashMap;

/**
 * @author whl
 * @version V1.0
 * @Title: 1. 两数之和
 * @Description: easy
 */
public class TwoSum {
    /**
     * 1. HashMap
     * 思路：遍历nums, 每一次遍历都观察map中是否存在target - nums[i]的结果, 若存在返回结果, 若不存在将当前nums[i]添加到map
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：3ms
     * @param nums
     * @param target
     * @return
     */
    public int[] twoSum1(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(target - nums[i])) {
                return new int[]{i, map.get(target - nums[i])};
            }
            map.put(nums[i], i);
        }
        return null;
    }

    /**
     * 1. 暴力解法
     * 思路：枚举所有不同组合, 观察组合是否相加等于target
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     * 执行用时：24ms
     * @param nums
     * @param target
     * @return
     */
    public int[] twoSum2(int[] nums, int target) {
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        TwoSum twoSum = new TwoSum();
        twoSum.twoSum1(new int[]{3,2,4}, 6);
    }
}
