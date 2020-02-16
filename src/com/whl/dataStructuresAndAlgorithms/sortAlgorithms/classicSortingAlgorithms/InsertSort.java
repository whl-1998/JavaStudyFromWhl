package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.classicSortingAlgorithms;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 直接插入排序
 * @Description:
 */
public class InsertSort {
    public void insertSort(int array[]){
        for(int i = 1; i < array.length; i++){
            int value = array[i];//无序区第一个元素
            int j = i - 1;//有序区最后一个元素下标
            //数据搬迁
            for (; j >= 0; i--) {
                if (array[j] > value) {
                    array[j + 1] = array[j];
                } else {
                    //如果待插入元素已经在正确位置, 直接break
                    break;
                }
            }
            array[j + 1] = value;
        }
    }

    public static void main(String[] args) {
        InsertSort is = new InsertSort();
        int array[] =  new int[]{5, 4, 3, 2, 1};
        is.insertSort(array);
        System.out.println(Arrays.toString(array));
    }


}
