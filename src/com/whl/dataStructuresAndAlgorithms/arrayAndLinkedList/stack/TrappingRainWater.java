package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.stack;

import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 42. 接雨水
 * @Description:
 */
public class TrappingRainWater {
    /**
     * 1. 暴力
     * 思路：遍历除了最左边和最右边的所有柱子, 寻找当前柱子左边和右边最高的柱子, 并观察min(左柱子高度, 右柱子高度)是否大于当前柱子的高度
     *       如果大于, 积攒雨水的量 = min - height[i]
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     * 执行用时：91ms
     * @param height
     * @return
     */
    public int trap1(int[] height) {
        int res = 0;
        for (int i = 1; i < height.length - 1; i++) {
            int leftMax= 0;
            int rightMax = 0;
            for (int m = 0; m < i; m++) {
                leftMax = Math.max(leftMax, height[m]);
            }
            for (int n = height.length - 1; n > i; n--) {
                rightMax = Math.max(rightMax, height[n]);
            }
            int min = Math.min(leftMax, rightMax);
            res +=  min > height[i] ? min - height[i]: 0;
        }
        return res;
    }

    /**
     * 2. DP
     * 思路：
     * 状态定义：leftMax[i], rightMax[i]分别用于存放第i根柱子的左边最高柱子和右边最高柱子
     * 递推方程：leftMax[i] = max(上一根柱子的高度, 上一根柱子所持有的左柱子高度) 同理右柱子
     * 剩下的步骤和解法1一致
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：1ms
     * @param height
     * @return
     */
    public int trap2(int[] height) {
        int[] leftMax = new int[height.length];
        int[] rightMax = new int[height.length];
        int res = 0;
        for (int i = 1; i < height.length; i++) {
            leftMax[i] = Math.max(height[i - 1], leftMax[i - 1]);
        }
        for (int j = height.length - 2; j >= 0; j--) {
            rightMax[j] = Math.max(height[j + 1], rightMax[j + 1]);
        }
        for (int i = 1; i < height.length - 1; i++) {
            int min = Math.min(leftMax[i], rightMax[i]);
            res +=  min > height[i] ? min - height[i]: 0;
        }
        return res;
    }

    /**
     * 3. 栈
     * 思路：维护一个的栈, 遍历所有柱子, 若当前柱子比栈顶元素矮, 当前柱子的左边界就是栈顶元素, 当前柱子入栈
     *                                 若当前柱子比栈顶元素高, 说明找到了栈顶元素的右边界
     *       计算左右边界之间的积水量, 直到栈顶元素高度 >= 当前柱子高度或者栈为空。
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：8ms
     * @param height
     * @return
     */
    public int trap3(int[] height) {
        int res = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < height.length; i++) {
            while (!stack.isEmpty() && height[stack.peek()] < height[i]) {
                int h = height[stack.pop()];
                if (stack.isEmpty()) break;
                int len = i - stack.peek() - 1;
                int min = Math.min(height[stack.peek()], height[i]);
                res += (min - h) * len;
            }
            stack.push(i);
        }
        return res;
    }

    public static void main(String[] args) {
        TrappingRainWater tr = new TrappingRainWater();
        tr.trap3(new int[]{2, 0 , 2});
    }
}
