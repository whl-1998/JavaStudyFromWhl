package com.whl.dataStructuresAndAlgorithms.sortAlgorithms;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 归并排序
 * @Description:
 */
public class MergeSort {
    public static void merge(int array[],int low,int high,int mid){
        int[] temp = new int[high - low+1];
        int left = low;
        int right = mid+1;
        int k = 0;

        while (left <= mid && right <= high){
            if(array[left] > array[right]){
                temp[k++] = array[right++];
            }else {
                temp[k++] = array[left++];
            }
        }

        while (left <= mid){
            temp[k++] = array[left++];
        }
        while (right <= high){
            temp[k++] = array[right++];
        }

        for(int i = 0;i < temp.length ;i++){
            array[i+low] = temp[i];
        }
    }

    private static void mergeSort(int array[],int low ,int high){
        if(low<high) {
            int mid = (low + high) / 2;
            mergeSort(array,low,mid);
            mergeSort(array,mid+1,high);
            merge(array, low,high,mid);
        }
    }

    public static void main(String[] args) {
        int a[] = { 4,5,7,1,3,9 };
        mergeSort(a, 0, a.length - 1);
        System.out.println("排序结果：" + Arrays.toString(a));
    }
}
