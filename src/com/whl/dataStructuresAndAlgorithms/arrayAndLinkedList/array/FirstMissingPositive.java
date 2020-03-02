package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

import com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.FirstUniqueCharacterInAString;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author whl
 * @version V1.0
 * @Title: 41. 缺失的第一个正数
 * @Description:
 */
public class FirstMissingPositive {
    /**
     * 1. HashSet
     * 思路：将nums中大于0的元素都放入hashSet中, 再遍历1 ~ nums.length, 如果间有某个数不存在于hashSet中, 则直接返回
     * 之所以遍历1 ~ nums.length, 是因为需要遍历的最大次数只存在于nums中的元素由1 ~ nums.length组成的
     * 例如[1,2,3,4,5], 需要遍历5次, 然后才能确定结果为6
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * @param nums
     * @return
     */
    public int firstMissingPositive1(int[] nums) {
        Set<Integer> hashSet = new HashSet<>();
        for (int i : nums) {
            if (i > 0) {
                hashSet.add(i);
            }
        }
        for (int i = 1; i <= nums.length; i++) {
            if (!hashSet.contains(i)) return i;
        }
        return nums.length + 1;
    }

    /**
     * 2. 桶排序 + 抽屉原理
     * 思路：遍历nums, 将位于1 ~ nums.length的nums[i]放在正确的位置(例如, 1放在index = 0的位置; 5放在index = 4的位置)
     *      再次遍历nums, 如果位置i上的元素不满足要求(nums[i] - 1 = i), 那么就可以确定结果为i + 1
     *      否则, 当nums的所有元素有序且连续, 那么结果必定为nums.length + 1
     * @param nums
     * @return
     */
    public int firstMissingPositive2(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            while (nums[i] > 0 && nums[i] <= nums.length && nums[nums[i] - 1] != nums[i]) {
                swap(nums, i, nums[i] - 1);
            }
        }
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] - 1 != i) return i + 1;
        }
        return nums.length + 1;
    }

    private void swap(int[] nums, int idx1, int idx2) {
        int temp = nums[idx1];
        nums[idx1] = nums[idx2];
        nums[idx2] = temp;
    }

    public static void main(String[] args) {
        FirstMissingPositive fm = new FirstMissingPositive();
        fm.firstMissingPositive2(new int[]{3,4,-1,1});
    }
}
