package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList;

/**
 * @author whl
 * @version V1.0
 * @Title: 11. 盛水最多的容器
 * @Description: normal
 */
public class ContainerWithMostWater {
    /**
     * 1. 暴力求解
     * 思路：遍历所有组合, 求出该组合的最大体积并返回结果
     * 时间复杂度：O(n^2)
     * 空间复杂度：O(1)
     * 执行用时：256ms
     * @param height
     * @return
     */
    public int maxArea1(int[] height) {
        int maxVolume = 0;
        for (int i = 0; i < height.length - 1; i++) {
            for(int j = i + 1; j < height.length; j++) {
                maxVolume = Math.max(maxVolume, (j - i) * (Math.min(height[i], height[j])));
            }
        }
        return maxVolume;
    }

    /**
     * 2. 双指针
     * 思路：左右指针分别指向数组两端, 此时宽度最大, 要寻找更大的体积, 左右指针必须向内收敛,
     * 那么为了获取到更大的体积, 一定是选择两个高度之间更矮的那一个指针进行移动, 以获取一个更大的结果
     * 时间复杂度：O(n)
     * 空间复杂度：O(1)
     * 执行用时：3ms
     * @param height
     * @return
     */
    public int maxArea2(int[] height) {
        int i = 0, j = height.length - 1, maxVolume = 0;
        while (i < j) {
            int minHigh = height[i] < height[j] ? height[i++] : height[j--];
            maxVolume = Math.max(maxVolume, minHigh * (j - i + 1));
        }
        return maxVolume;
    }

    public static void main(String[] args) {
        ContainerWithMostWater cw = new ContainerWithMostWater();
        cw.maxArea1(new int[]{1,8,6,2,5,4,8,3,7});
    }
}
