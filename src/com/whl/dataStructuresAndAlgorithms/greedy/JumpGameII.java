package com.whl.dataStructuresAndAlgorithms.greedy;

/**
 * @author whl
 * @version V1.0
 * @Title: 45. 跳跃游戏II
 * @Description:
 */
public class JumpGameII {
    /**
     * 1. 从后往前贪心
     * 思路：每次遍历序列, 寻找一个能够直接到达终点pos的位置idx, 更新pos = idx并且跳跃步数 + 1, 继续寻找第二个, 直到pos = 0
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：174ms
     * @param nums
     * @return
     */
    public int jump1(int[] nums) {
        int count = 0;
        int pos = nums.length - 1;
        while (pos != 0) {
            for (int i = 0; i < nums.length; i++) {
                if (nums[i] + i >= pos) {
                    pos = i;
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    /**
     * 2. 从前往后贪心
     * 思路：遍历数组, 每一次遍历都寻找当前位置所能到达的范围内跳的最远的position, 当遍历到end位置, 更新end为当前跳跃能够到达的最远距离maxLen并且跳跃步数+1
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：
     * @param nums
     * @return
     */
    public int jump2(int[] nums) {
        int maxLen = 0;
        int end = 0;
        int count = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            maxLen = Math.max(maxLen, nums[i] + i);
            if (i == end) {
                end = maxLen;
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        JumpGameII jg = new JumpGameII();
        jg.jump2(new int[] {2,3,1,2,5});
    }
}
