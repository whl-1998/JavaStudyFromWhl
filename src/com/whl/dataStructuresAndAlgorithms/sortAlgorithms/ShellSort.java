package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.insertSort;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 希尔排序
 * @Description:
 */
public class ShellSort {
    public static void shellSort(int arr[]){
        for(int i = arr.length/2; i > 0;i = i /2){
            for(int j = i; j< arr.length; j++){
                for(int d = j - i;d >= 0;d = d - i){
                    if(arr[d+ i] < arr[d]){
                        int temp = arr[d + i];
                        arr[d + i] = arr[d];
                        arr[d] = temp;
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        int array[] = new int[]{9,8,6,4,3,2,1};
        shellSort(array);
        System.out.println(Arrays.toString(array));
    }
}

