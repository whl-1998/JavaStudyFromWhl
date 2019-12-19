package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 46. 全排列
 * @Description:
 */
public class Permutations {
    /**
     * 1. DFS回溯 + 剪枝
     * 思路：重复操作：依次从中数组取出一个数试着添加到temp中组成全排列, 若数组中存在这个数, 那么组成失败, 继续放下一个数
     *      例如：用1,2,3组成全排列, temp[]初始为空,
     *            尝试添加全排列的第一个数, temp[1]
     *            temp[1]尝试添加全排列第二个数, 发现1添加过了, 继续依次添加2 temp[1,2]
     *            继续添加, 直到temp满足全排列的长度, temp[1,2,3], 添加temp到res之后回溯到temp[1], 尝试添加3, temp[1,3], 然后重复以上步骤...
     * 执行用时：2ms
     * @param nums
     * @return
     */
    public List<List<Integer>> permute1(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        recur(res, temp, nums);
        return res;
    }

    private void recur(List<List<Integer>> res, List<Integer> temp, int[] nums) {
        if (temp.size() == nums.length) {
            res.add(new ArrayList<>(temp));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            //if temp has already add(nums[i]), try to add next nums
            if (temp.contains(nums[i])) continue;
            temp.add(nums[i]);
            recur(res, temp, nums);
            //reverse
            temp.remove(temp.size() - 1);
        }
    }

    /**
     * 2. DFS回溯 + 枝减(交换)
     * 思路：用一个参数start记录递归层数, 交换temp的(start, start ~ nums.length - 1)位置上的元素
     *       例如：temp[1,2,3], 第0层交换(0,0)后得到temp[1,2,3]drill down
     *             temp[1,2,3], 第1层交换(1,1)后得到temp[1,2,3]drill down
     *             temp[1,2,3], 第2层交换(2,2)后得到temp[1,2,3]drill down
     *             temp[1,2,3], 第3层添加结果到res, return到第二层, 第二层reverse之后回到第一层
     *             temp[1,2,3], 第一层交换(1,2)后得到temp[1,3,2]drill down
     *             temp[1,3,2], 第二层交换(2,2)...重复计算直到DFS遍历完所有状态树
     * 执行用时：1ms
     * @param nums
     * @return
     */
    public List<List<Integer>> permute2(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        List<Integer> temp = new ArrayList<>();
        for (int n : nums) {
            temp.add(n);
        }
        recur(res, temp, nums, 0);
        return res;
    }

    private void recur(List<List<Integer>> res, List<Integer> temp, int[] nums, int start) {
        if (start == nums.length) {
            res.add(new ArrayList<>(temp));
            return;
        }
        for (int i = start; i < nums.length; i++) {
            Collections.swap(temp, i, start);
            recur(res, temp, nums, start + 1);
            Collections.swap(temp, i, start);
        }
    }

    public static void main(String[] args) {
        Permutations permutations = new Permutations();
        permutations.permute2(new int[]{1,2,3});
    }
}
