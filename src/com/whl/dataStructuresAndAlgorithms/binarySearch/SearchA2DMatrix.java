package com.whl.dataStructuresAndAlgorithms.binarySearch;

/**
 * @author whl
 * @version V1.0
 * @Title: 74. 搜索二维矩阵
 * @Description:
 */
public class SearchA2DMatrix {
    /**
     * 1. 二分查找
     * 思路：遍历二维矩阵中的所有一维数组, 若一维数组的最后一个元素 >= target, 那么就在该一维数组进行二分查找
     * 时间复杂度：O(m * logN)
     * 空间复杂度：O(1)
     * 执行用时：0ms
     * @param matrix
     * @param target
     * @return
     */
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return false;
        for (int i = 0; i < matrix.length; i++) {
            if (target <= matrix[i][matrix[i].length - 1]) {
                int m = 0, n = matrix[i].length - 1;
                while (m <= n) {
                    int mid = (m + n) >> 1;
                    if (matrix[i][mid] == target) {
                        return true;
                    } else if (matrix[i][mid] < target) {
                        m = mid + 1;
                    } else {
                        n = mid - 1;
                    }
                }
            }
        }
        return false;
    }
}
