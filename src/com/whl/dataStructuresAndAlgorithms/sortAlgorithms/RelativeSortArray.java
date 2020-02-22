package com.whl.dataStructuresAndAlgorithms.sortAlgorithms;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * @author whl
 * @version V1.0
 * @Title: 1122. 数组的相对排序
 * @Description: easy
 */
public class RelativeSortArray {
    /**
     * 解法1：暴力解法
     * 思路：遍历arr2, 找到arr1中与arr2[i]相等的元素, 并存入temp数组,
     * 		arr1中判断过的元素置为-1, 然后将剩下arr1中不为-1的值依次添加到temp末尾, 再对这部分进行排序
     * 时间复杂度：O(m * n)
     * 空间复杂度：O(n)
     * 执行用时：4ms
     * @param arr1
     * @param arr2
     * @return
     */
    public int[] relativeSortArray1(int[] arr1, int[] arr2) {
        int[] temp = new int[arr1.length];
        int k = 0;
        int count = 0;
        for (int i : arr2) {
            for (int j = 0; j < arr1.length; j++) {
                if (arr1[j] == i) {
                    temp[k++] = arr1[j];
                    arr1[j] = -1;
                    count++;
                }
            }
        }
        for (int j = 0; j < arr1.length; j++) {
            if (arr1[j] != -1)
                temp[k++] = arr1[j];
        }
        Arrays.sort(temp, count, temp.length);
        return temp;
    }

    /**
     * 2. 计数排序
     * 思路：采用了计数排序的思想, 创建一个1001大小的bucket数组用于统计arr1中所有元素出现的次数, 下标就是对应元素值, value就是出现的频次
     *      遍历arr2, 观察arr2[i]这个值在bucket数组中出现了多少次, 并按该次数赋值给arr1, 到目前为止, 按arr2的相对排序就完成了
     *      剩下的一些未出现在arr2中的值, 只需要在计数数组中寻找不为0的元素, 并依次添加到arr1尾部即可
     * 时间复杂度：O(1)
     * 空间复杂度：O(1) 常数的数组大小
     * 执行用时：0ms
     * @param arr1
     * @param arr2
     * @return
     */
    public int[] relativeSortArray2(int[] arr1, int[] arr2) {
        int[] bucket = new int[1001];
        int k = 0;
        for (int i : arr1) {
            bucket[i]++;
        }
        //按arr2的顺序将结果覆盖到arr1中
        for (int j : arr2) {
            while (bucket[j]-- > 0) {
                arr1[k++] = j;
            }
        }
        //对于arr2中未出现的值, 按桶的位置依次追加到arr1中
        for (int i = 0; i < bucket.length; i++) {
            while (bucket[i]-- > 0) {
                arr1[k++] = i;
            }
        }
        return arr1;
    }

    /**
     * 解法2.1：计数排序法TreeMap实现
     * 思路：如果不限制arr1和arr2的范围, 那么我们可以用基于红黑树的TreeMap对arr1出现的次数进行统计, key = 元素值, value = 元素出现的频次
     *      TreeMap存储的是一个有序的集合, 若未通过比较器指定, 集合内元素默认自然排序
     * 时间复杂度：O(n * log2N)
     * 空间复杂度：O(n)
     *
     * @param arr1
     * @param arr2
     * @return
     */
    public int[] relativeSortArray3(int[] arr1, int[] arr2) {
        TreeMap<Integer, Integer> map = new TreeMap<>();
        int k = 0;
        for (int i : arr1) {
            map.put(i, map.getOrDefault(i, 0) + 1);
        }
        for (int i : arr2) {
            while (map.get(i) != 0) {
                arr1[k++] = i;
                map.put(i, map.get(i) - 1);
            }
        }
        for (int i : map.keySet()) {
            while (map.get(i) != 0) {
                arr1[k++] = i;
                map.put(i, map.get(i) - 1);
            }
        }
        return arr1;
    }

    public static void main(String[] args) {
        RelativeSortArray rs = new RelativeSortArray();
        rs.relativeSortArray3(new int[]{2,3,1,3,2,4,6,7,9,2,19}, new int[]{2,1,4,3,9,6});
    }
}
