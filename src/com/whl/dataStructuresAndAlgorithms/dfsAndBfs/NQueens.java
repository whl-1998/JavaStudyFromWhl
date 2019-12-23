package com.whl.dataStructuresAndAlgorithms.dfsAndBfs;

import java.util.*;

/**
 * @author whl
 * @version V1.0
 * @Title: 51. N皇后
 * @Description: hard
 */
public class NQueens {

    private Set<Integer> col = new HashSet<>();
    private Set<Integer> diag1 = new HashSet<>();
    private Set<Integer> diag2 = new HashSet<>();

    /**
     * 1. 回溯dfs(Set)
     * 思路：
     * 由于皇后的攻击范围是所在的行/列/对角线 ,因此在某一行放置了一个皇后, 该行不可能再放置第二个皇后
     * 因此我们只需要对行数作记录, 判断每一列的位置是否available即可
     * 通过3个Set分别存放当前皇后所占据的列,左/右对角线, 下一个皇后在放置之前通过Set判断当前位置是否可以放置
     * 执行用时：16ms
     *
     * @param n
     * @return
     */
    public List<List<String>> solveNQueens1(int n) {
        List<List<String>> result = new ArrayList<>();
        dfs(result, new ArrayList<String>(), n, 0);
        return result;
    }

    private void dfs(List<List<String>> result, List<String> list, int n, int row) {
        if (row == n) {
            result.add(new ArrayList<>(list));
            return;
        }
        for (int i = 0; i < n; i++) {
            if (col.contains(i) || diag1.contains(row + i) || diag2.contains(row - i)) continue;
            char[] temp = new char[n];
            Arrays.fill(temp, '.');
            temp[i] = 'Q';
            list.add(new String(temp));
            col.add(i);
            diag1.add(row + i);
            diag2.add(row - i);
            //drill down
            dfs(result, list, n, row + 1);
            //reverse
            list.remove(list.size() - 1);
            col.remove(i);
            diag1.remove(row + i);
            diag2.remove(row - i);
        }
    }

    /**
     * 2. 回溯dfs(boolean[])
     * 执行用时：4ms
     * @param n
     * @return
     */
    public List<List<String>> solveNQueens2(int n) {
        List<List<String>> res = new ArrayList<>();
        recur(res, new ArrayList<>(), new boolean[n], new boolean[2 * n], new boolean[2 * n], n, 0);
        return res;
    }

    private void recur(List<List<String>> res, ArrayList<String> temp, boolean[] col, boolean[] diag1, boolean[] diag2, int n, int row) {
        if (row == n) {
            res.add(new ArrayList<>(temp));
            return;
        }
        for (int i = 0; i < n; i++) {
            if (col[i] || diag1[row - i + n - 1] || diag2[row + i]) continue;
            char[] arr = new char[n];
            Arrays.fill(arr, '.');
            arr[i] = 'Q';
            temp.add(String.valueOf(arr));
            col[i] = true;
            diag1[row - i + n - 1] = true;
            diag2[row + i] = true;
            recur(res, temp, col, diag1, diag2, n, row + 1);
            col[i] = false;
            diag1[row - i + n - 1] = false;
            diag2[row + i] = false;
            temp.remove(temp.size() - 1);
        }
    }
}