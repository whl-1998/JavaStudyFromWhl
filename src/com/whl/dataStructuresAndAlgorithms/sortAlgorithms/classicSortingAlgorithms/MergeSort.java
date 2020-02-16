package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.classicSortingAlgorithms;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 归并排序
 * @Description:
 */
public class MergeSort {

    public void mergeSort(int[] arr, int low, int high) {
        if (low >= high) return;
        int mid = (low + high)/2;
        mergeSort(arr, low, mid);
        mergeSort(arr, mid + 1, mid);
        merge(arr, low, mid, high);
    }

    private void merge(int[] arr, int low, int mid, int high) {
        int[] temp = new int[high - low + 1];
        int left = low, right = mid + 1, k = 0;
        while (left <= mid && right <= high) {
            temp[k++] = arr[left] < arr[right] ? arr[left++] : arr[right++];
        }
        while (left <= mid) temp[k++] = arr[left++];
        while (right <= high) temp[k++] = arr[right++];
        System.arraycopy(temp, 0, arr, low, temp.length);
    }

    public static void main(String[] args) {
        int a[] = { 4,5,7,1,3,9 };
        MergeSort ms = new MergeSort();
        ms.mergeSort(a, 0, a.length - 1);
        System.out.println("排序结果：" + Arrays.toString(a));
    }
}
