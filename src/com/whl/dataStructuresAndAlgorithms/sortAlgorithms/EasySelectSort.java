package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.selectSort;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 简单选择排序
 * @Description:
 */
public class EasySelectSort {
    public static void easySelectSort(int array[] ){
        for(int i = 0; i < array.length; i++){
            int temp = i;
            for(int j = i;j<array.length;j++){
                if(array[j] < array[temp]){
                    temp = j;
                }
            }
            int tempArr = array[temp];
            array[temp] = array[i];
            array[i] = tempArr;
        }
    }

    public static void main(String[] args) {
        int array[] = new int[]{9,8,6,4,3,2,1};
        easySelectSort(array);
        System.out.println(Arrays.toString(array));
    }
}
