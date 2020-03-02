package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author whl
 * @version V1.0
 * @Title: 169. 求众数
 * @Description: easy
 */
public class MajorityElement {
    /**
     * 1. HashMap
     * 思路：通过map统计nums[i]出现的次数, 若次数大于n/2, 则返回nums[i]
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：23ms
     * @param nums
     * @return
     */
    public int majorityElement1(int[] nums) {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int temp = map.getOrDefault(nums[i], 0);
            map.put(nums[i], temp + 1);
            if (map.get(nums[i]) > (nums.length >> 1)) return nums[i];
        }
        return nums[0];
    }

    /**
     * 2. 投票法
     * 思路：设置一个当前被选举数res = nums[0], 票数count = 1
     * 如果nums[i] = res, 票数增加1
     * 如果nums[i] != res, 票数减少1, 若票数减至0, 更换res = nums[i], count初始化为1
     * 执行用时：2ms
     * @param nums
     * @return
     */
    public int majorityElement2(int[] nums) {
        int res = nums[0];
        int count = 1;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == res) {
                count++;
            } else if (nums[i] != res) {
                count--;
                if (count == 0) {
                    res = nums[i];
                    count = 1;
                }
            }
        }
        return res;
    }

    /**
     * 3. 排序取中法
     * 时间复杂度：O(nlogn)
     * 空间复杂度：O(1)
     * 执行用时：2ms
     * @param nums
     * @return
     */
    public int majorityElement3(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length >> 1];
    }

    public static void main(String[] args) {
        MajorityElement me = new MajorityElement();
        me.majorityElement3(new int[]{1,5,5,4,5});
    }
}
