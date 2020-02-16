package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.classicSortingAlgorithms;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 快速排序
 * @Description:
 */
public class QuickSort {
    public void quickSort(int[] array, int begin, int end) {
        if (begin >= end) return;
        int ref = partition(array, begin, end);//基准
        quickSort(array, begin, ref - 1);
        quickSort(array, ref + 1, end);
    }

    //根据基准划分为左右两块区域 : 这里基准选取序列的末尾元素
    private int partition(int[] array, int begin, int end) {
        int count = begin, pivot = end;
        for (int i = begin; i < end; i++) {
            if (array[i] < array[pivot]) {
                int temp = array[i]; array[i] = array[count]; array[count] = temp;
                count++;
            }
        }
        int temp = array[count]; array[count] = array[pivot]; array[pivot] = temp;
        return count;
    }



    public static void main(String[] args) {
        int array[] =  new int[]{1, 2, 3, 6, 5};
        QuickSort qs = new QuickSort();
        qs.quickSort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));

    }
}
