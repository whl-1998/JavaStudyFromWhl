package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 77. 组合
 * @Description: normal
 */
public class Combinations {
    /**
     * 1. DFS回溯 + 枝减
     * 思路：
     * 假设求C(3, 2):
     * start = 1: temp添加1 [1], drill down
     * start = 2, temp添加2 [1, 2], drill down
     * start = 3, 发现满足条件, 添加temp到result, return回到start2, 枝减temp末尾 [1]
     * start = 2, temp添加3 [1, 3], drill down, 满足条件reverse直到temp为 []
     * start = 1, temp添加2 [2], drill down........
     * 直到所有组合添加完毕
     * 执行用时：41ms
     * @param n
     * @param k
     * @return
     */
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> res = new ArrayList<>();
        recur(n, k, res, new ArrayList<>(), 1);
        return res;
    }

    private void recur(int n, int k, List<List<Integer>> res, ArrayList<Integer> temp, int start) {
        if (temp.size() == k) {
            res.add(new ArrayList<>(temp));
            return;
        }
        for (int i = start; i <= n; i++) {
            temp.add(i);
            recur(n, k, res, temp, i + 1);
            temp.remove(temp.size() - 1);
        }
    }

    /**
     * 2. 递归
     * 思路：C(n, k) = C(n - 1, k - 1).append(n) + C(n - 1, k)
     * 当n == k时, return [[0][1]...[k]]
     * 当k == 0时, return [[]]
     * 假设求C(3, 2)：C(3, 2) = C(2, 1).append(3) + C(2, 2)
     * C(2, 1) = C(1, 1)[[1]] + C(1, 0)[[]].append(2) = [[1], [2]]
     * C(2, 1).append(3) = [[1,3], [2, 3]]
     * C(2, 2) = [[1], [2]]
     * C(2, 1).append + C(2, 2) = [[1, 2], [1, 3], [2, 3]]
     * 执行用时：7ms
     * @param n
     * @param k
     * @return
     */
    public List<List<Integer>> combine2(int n, int k) {
        if (k == n || k == 0) {
            List<Integer> row = new LinkedList<>();
            for (int i = 1; i <= k; ++i) {
                row.add(i);
            }
            return new LinkedList<>(Arrays.asList(row));
        }
        List<List<Integer>> result = combine2(n - 1, k - 1);
        for (List<Integer> l : result) {
            l.add(n);
        }
        result.addAll(combine2(n - 1, k));
        return result;
    }

    public static void main(String[] args) {
        Combinations cm = new Combinations();
        cm.combine2(3, 1);
    }
}
