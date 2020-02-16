package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.classicSortingAlgorithms;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 折半插入排序
 * @Description:
 */
public class BinaryInsertSort {
    public static void binaryInsertSort(int array[]){
        for(int i = 1;i<array.length;i++){
            int left = 0;
            int right = i-1;
            int mid;
            int temp = array[i];
            while (left <= right){
                mid = (left + right )/2;
                if(array[i] > array[mid]){
                    left = mid + 1;
                }else {
                    right = mid - 1;
                }
            }
            for(int j = i-1;j>=left;j--){
                array[j+1] = array[j];
            }
            array[left] = temp;
        }
    }

    public static void main(String[] args) {
        int array[] = new int[]{9,8,6,4,3,2,1};
        binaryInsertSort(array);
        System.out.println(Arrays.toString(array));
    }
}
