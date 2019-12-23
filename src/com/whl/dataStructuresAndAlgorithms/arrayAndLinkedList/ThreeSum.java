package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 15. 三数之和
 * @Description: normal
 */
public class ThreeSum {
    /**
     * 1. 三指针
     * 思路：将序列排序, 指针i指向当前序列最小值, 指针m, n分别指向指针i右边区域的最小值和最大值
     * 枚举nums[i]：1. 如果三数相加 == 0, 那么添加到结果即可（注意去重）
     *              2. 如果三数相加 < 0, 那么尝试将三数之和增大, m指针右移
     *              3. 如果三数相加 > 0, 那么尝试将三数之和减少, n指针左移
     * 时间复杂度：O(nlogN + n^2)
     * 空间复杂度：O(1)
     * 执行用时：53ms
     * @param nums
     * @return
     */
    public List<List<Integer>> threeSum1(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums == null || nums.length == 0) return res;
        Arrays.sort(nums);//nlogN
        //if the Minimum Of the nums > 0, it can be returned
        if (nums[0] > 0) {
            return res;
        }
        for (int i = 0; i < nums.length - 2; i++) {
            //if current nums[i] == previous nums[i], it can be continued
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            int m = i + 1, n = nums.length - 1;
            while (m < n) {
                int sum = nums[i] + nums[m] + nums[n];
                if (sum == 0) {
                    res.add(Arrays.asList(nums[i], nums[m], nums[n]));
                    while (m < n && nums[m + 1] == nums[m++]);
                    while (m < n && nums[n - 1] == nums[n--]);
                } else if (sum < 0) {
                    while (m < n && nums[m + 1] == nums[m++]);
                } else {
                    while (m < n && nums[n - 1] == nums[n--]);
                }
            }
        }
        return res;
    }

    /**
     * 2. 暴力解法
     * 思路：获取所有不同的三数组合并在三数之和等于0时添加组合添加到List
     * 时间复杂度：O(n^3 + nlogN)
     * 空间复杂度：O(1)
     * 执行超时
     * @param nums
     * @return
     */
    public List<List<Integer>> threeSum2(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        if (nums[0] > 0) return res;
        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            for (int j = i + 1; j < nums.length - 1; j++) {
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;
                for (int k = j + 1; k < nums.length; k++) {
                    if (k > j + 1 && nums[k] == nums[k - 1]) continue;
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        res.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    }
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        ThreeSum ts = new ThreeSum();
        ts.threeSum1(new int[] {-1, 0, 1, 2, -1, -4});
    }
}
