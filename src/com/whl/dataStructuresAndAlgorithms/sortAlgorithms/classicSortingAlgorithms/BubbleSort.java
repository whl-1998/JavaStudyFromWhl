package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.classicSortingAlgorithms;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 冒泡排序
 * @Description: 交换排序
 */
public class BubbleSort {
    public void bubbleSort(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    public void bubbleSort2(int arr[]) {
        for (int i = 0; i < arr.length - 1; i++) {
            boolean flag = false;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    flag = true;
                }
            }
            if (!flag) break;
        }
    }


    public static void main(String[] args) {
        BubbleSort bs = new BubbleSort();
        int array[] = new int[]{1,2,3,4,5,6};
        bs.bubbleSort2(array);
        System.out.println(Arrays.toString(array));
    }
}
