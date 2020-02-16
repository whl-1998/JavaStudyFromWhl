package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

/**
 * @author whl
 * @version V1.0
 * @Title: 283. 移动0
 * @Description: easy
 */
public class MoveZeroes {
    /**
     * 1. 双指针
     * 思路：定义指针j指向非零元素在移动后应该存在的下标, 遍历获取所有非零元素并将其移动到指针j对应的下标
     * 最后数组将剩余的部分赋0即可
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param nums
     */
    public void moveZeroes(int[] nums) {
        int j = 0;
        for (int i : nums) {
            if (i != 0) {
                nums[j++] = i;
            }
        }
        for (int i = j; i < nums.length; i++) {
            nums[i] = 0;
        }
    }

    public static void main(String[] args) {
        MoveZeroes mz = new MoveZeroes();
        mz.moveZeroes(new int[]{0,1,0,3,12});
    }
}
