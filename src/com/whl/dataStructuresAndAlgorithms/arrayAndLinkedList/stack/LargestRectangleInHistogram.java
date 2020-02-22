package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.stack;

import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 84. 柱状图中最大的矩形
 * @Description:
 */
public class LargestRectangleInHistogram {
    /**
     * 1. 暴力求解
     * 思路：枚举所有的左右边界组合, 计算其左边界到右边界的面积, 然后返回面积的最大值
     * 时间复杂度：O(n^3)
     * 空间复杂度：O(1)
     * 提交超时
     * @param heights
     * @return
     */
    public int largestRectangleArea1(int[] heights) {
        if (heights.length == 1) return heights[0];
        int volume = 0;
        for (int i = 0; i < heights.length; i++) {
            for (int j = i; j < heights.length; j++) {
                int minHigh = Integer.MAX_VALUE;
                for (int k = i; k <= j; k++) {
                    minHigh = Math.min(heights[k], minHigh);
                }
                volume = Math.max(volume, minHigh * (j - i + 1));
            }
        }
        return volume;
    }

    /**
     * 2. 暴力优化
     * 思路：对于任意一根柱子, 只需要知道它的左边界和右边界就能够计算出它的最大高度, 左边界就是左边第一根比它小的柱子, 同理右边界
     * 可以枚举所有的柱子, 计算这根柱子对应的最大面积, 然后返回面积最大值即可
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     * @param heights
     * @return
     */
    public int largestRectangleArea2(int[] heights) {
        int volume = 0;
        for (int i = 0; i < heights.length; i++) {
            int tempLeft = -1;
            int tempRight = heights.length;
            //find the left boundary
            for (int j = i - 1; j >= 0; j--) {
                if (heights[j] < heights[i]) {
                    tempLeft = j;
                    break;
                }
            }
            //find the right boundary
            for (int j = i + 1; j < heights.length; j++) {
                if (heights[j] < heights[i]) {
                    tempRight = j;
                    break;
                }
            }
            volume = Math.max((tempRight - tempLeft - 1) * (heights[i]), volume);
        }
        return volume;
    }

    /**
     * 3. 栈
     * 思路：对于解法2可以发现：每次获取一根柱子的左右边界时, 都要再重新遍历一次左半区和右半区
     * 那么只要将左右边界缓存, 就可以实现获取左右边界的时间复杂度为O(1)
     * 为此, 我们可以通过一个栈来进行优化：
     * 如果遍历到一个柱子比它的上一个柱子高, 那么入栈作为栈顶元素
     * 如果遍历到一个柱子比它的上一个柱子(栈顶元素)矮, 说明找到了上一个柱子(栈顶元素)的右边界, 且上一个柱子(栈顶元素)的左边界就是栈顶元素pop之后新的栈顶元素
     *          如果pop之后新的栈顶元素依旧比当前柱子高, 说明新的栈顶元素也找到了右边界, 继续pop栈顶元素计算面积即可
     * 遍历到最后, 如果栈还有剩下的柱子未比较, 那么栈中剩下的柱子其右边界都是height.length, 左边界都是pop之后的栈顶元素, 继续计算面积
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * @param heights
     */
    public int largestRectangleArea3(int[] heights) {
        Stack<Integer> stack = new Stack<>();
        stack.push(-1);
        int volume = 0;
        for (int i = 0; i < heights.length; i++) {
            //if current height >= stack.peek()'s height, update volume
            while (stack.peek() != -1 && heights[i] < heights[stack.peek()]) {
                volume = Math.max(volume, heights[stack.pop()] * (i - stack.peek() - 1));
            }
            //current height < stack.peek()'s height or after update volume, push this height's index
            stack.push(i);
        }
        //if the stack is not empty, continue to find the max volume until the stack is empty
        while (stack.peek() != - 1) {
            volume = Math.max(volume, heights[stack.pop()] * (heights.length - stack.peek() - 1));
        }
        return volume;
    }

    /**
     * 4. DP
     * 思路：计算每根柱子能获取的最大面积, 需要找到其左右边界, 而题解2每一次寻找时都会进行一次遍历, 进行了很多不必要的重复计算
     * 因此这里可以通过动态规划, 通过递推获得每一根柱子的左右边界, 然后直接通过递推的结果直接进行面积计算即可
     * 第一步状态定义：dpLeft[i] = 左边第一个比它矮的柱子下标, 如果dp[i - 1]的下标对应的柱子不够矮, 那么就去dp[dp[i - 1]]比较, 直到找到更矮的柱子
     * 同理递推dpRight[i]
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：2ms
     * @param heights
     */
    public int largestRectangleArea4(int[] heights) {
        if (heights.length == 0 || heights == null) return 0;
        int volume = 0;
        int[] dpLeft = new int[heights.length];
        int[] dpRight = new int[heights.length];
        dpLeft[0] = -1;
        dpRight[dpRight.length - 1] = heights.length;
        for (int i = 1; i < heights.length; i++) {
            int p = i - 1;
            while (p >= 0 && heights[i] <= heights[p]) {
                p = dpLeft[p];
            }
            dpLeft[i] = p;
        }
        for (int j = heights.length - 2; j >= 0; j--) {
            int p = j + 1;
            while (p <= heights.length - 1 && heights[j] <= heights[p]) {
                p = dpRight[p];
            }
            dpRight[j] = p;
        }
        for (int i = 0; i < heights.length; i++) {
            volume = Math.max(volume, (dpRight[i] - dpLeft[i] - 1) * heights[i]);
        }
        return volume;
    }

    public static void main(String[] args) {
        LargestRectangleInHistogram lg = new LargestRectangleInHistogram();
        System.out.println(lg.largestRectangleArea4(new int[]{2,1,5,6,2,3}));
    }
}
