package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.classicSortingAlgorithms;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 堆排序
 * @Description:
 */
public class HeapSort {
    public static void heapSort(int[] array) {
        for(int i = array.length-1;i>0;i--){
            maxHeap(array,i);

            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;
        }
    }

    public static void maxHeap(int[] array, int n) {
        int child;
        for(int i = (n-1)/2; i>=0 ;i--){
            child = i*2 + 1;
            if(child != n && array[child] < array[child+1]){
                child++;
            }
            if(array[i] < array[child]){
                int temp = array[i];
                array[i] = array[child];
                array[child] = temp;
            }
        }
    }


    public static void main(String[] args) {
        int a[] = { 4,5,7,1,3,9 };
        heapSort(a);
        System.out.println("排序结果：" + Arrays.toString(a));
    }

    }
