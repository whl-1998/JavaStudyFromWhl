package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author whl
 * @version V1.0
 * @Title: 1103. 分糖果II
 * @Description:
 */
public class DistributeCandiesToPeople {
    /**
     * 1. 暴力解法
     * 思路：直接用代码模拟场景即可
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     * 执行用时：1ms
     * @param candies
     * @param num_people
     * @return
     */
    public int[] distributeCandies1(int candies, int num_people) {
        int[] res = new int[num_people];
        int count = 1;
        int i = 0;
        while (candies > 0) {
            if (candies - count >= 0) {
                res[i] += count;
                candies -= count;
            } else {
                res[i] += candies;
                candies = 0;
            }
            count++;
            i++;
            if (i == num_people) {
                i = 0;
            }
        }
        return res;
    }

    /**
     * 2. 暴力化简
     * @param candies
     * @param num_people
     * @return
     */
    public int[] distributeCandies2(int candies, int num_people) {
        int[] res = new int[num_people];
        int curr_given = 0;
        while (candies > 0) {
            res[curr_given % num_people] += Math.min(++curr_given, candies);
            candies -= curr_given;
        }
        return res;
    }

    public static void main(String[] args) {
        DistributeCandiesToPeople dc = new DistributeCandiesToPeople();
        dc.distributeCandies2(7, 4);
    }
}
