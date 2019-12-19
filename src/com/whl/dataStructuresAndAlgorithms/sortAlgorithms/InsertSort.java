package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.insertSort;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 直接插入排序
 * @Description:
 */
public class InsertSort {
    public static void insertSort(int array[]){
        for(int i = 1;i<array.length;i++){
            for(int j = i; j>= 1 ;j--){
                if(array[j-1]>array[j]){
                    int temp = array[j];
                    array[j] =  array[j-1];
                    array[j-1] = temp;
                }
            }
        }
    }


    public static void main(String[] args) {
        int array[] =  new int[]{9,8,7,6,5,4,3,2,1};
        insertSort(array);
        System.out.println(Arrays.toString(array));
    }


}
