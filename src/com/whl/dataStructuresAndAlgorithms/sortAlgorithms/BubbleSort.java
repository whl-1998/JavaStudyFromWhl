package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.changeSort;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class BubbleSort {
    public static void bubbleSort(int array[]){
        for(int i = 0 ;i <array.length-1;i++){
            for(int j = 0;j<array.length-1-i;j++){
                if(array[j]>array[j+1]){
                    int temp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        int array[] = new int[]{9,8,7,0,6,5,4,3,21};
        bubbleSort(array);
        System.out.println(Arrays.toString(array));
    }
}
