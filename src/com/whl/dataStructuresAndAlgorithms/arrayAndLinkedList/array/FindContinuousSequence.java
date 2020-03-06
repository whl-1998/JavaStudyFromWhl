package com.whl.dataStructuresAndAlgorithms.arrayAndLinkedList.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 剑指Offer 57题
 * @Description:
 */
public class FindContinuousSequence {
    public int[][] findContinuousSequence(int target) {
        List<int[]> res = new ArrayList<>();
        for (int i = 1, j = i, targetTemp = target; i <= target/2 + 1; i++, j = i, targetTemp = target) {
            while (targetTemp - j >= 0) {
                targetTemp -= j++;
            }
            if (targetTemp == 0) {
                int[] curr = new int[j - i];
                int k = 0;
                for (int m = i; m < j; m++) {
                    curr[k++] = m;
                }
                res.add(curr);
            }
        }
        return res.toArray(new int[0][]);
    }

    public static void main(String[] args) {
        FindContinuousSequence fc = new FindContinuousSequence();
        fc.findContinuousSequence(15);
    }
}
