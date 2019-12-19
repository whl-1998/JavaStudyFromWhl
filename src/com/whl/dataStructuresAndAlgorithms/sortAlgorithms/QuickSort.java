package com.whl.dataStructuresAndAlgorithms.sortAlgorithms.changeSort;

import java.util.Arrays;
import java.util.Stack;

/**
 * @author whl
 * @version V1.0
 * @Title: 快速排序
 * @Description:
 */
public class QuickSort {
    public static void quickSort(int array[]){
        Stack<Integer> stack = new Stack<Integer>();
        stack.push(0);
        stack.push(array.length-1);

        while (!stack.isEmpty()) {
            int right = stack.pop();
            int left = stack.pop();

            int ref = partition(array,left,right);


            if(ref > left) {
                stack.push(0);
                stack.push(ref - 1);
            }
            if(ref < right && ref >= 0) {
                stack.push(ref + 1);
                stack.push(right);
            }
        }
    }

    private static int partition(int[] array,int low,int high){
        if(low >= high) return -1;
        int left = low;
        int right = high;
        int ref = array[left];
        while (left < right) {
            while (left < right && array[right] >= ref) {
                right--;
            }
            array[left] = array[right];
            while (left < right && array[left] <= ref) {
                left++;
            }
            array[right] = array[left];
        }
        array[left] = ref;
        return left;
    }


    public static void quickSortRecursive(int array[], int low, int high){
        if(low >= high) return;
        int left = low;
        int right = high;
        int ref = array[left];
        while (left<right){
            while (left < right && array[right] >= ref) {
                right--;
            }
            array[left] = array[right];
            while (left < right && array[left] <= ref) {
                left++;
            }
            array[right] = array[left];
        }
        array[left] = ref;

        quickSortRecursive(array,0,left-1);
        quickSortRecursive(array,right+1,high);
    }


    public static void main(String[] args) {
        int array[] =  new int[]{9,8,7,6,5,4,3,2,1};
        //quickSortRecursive(array,0,array.length-1);
        quickSort(array);
        System.out.println(Arrays.toString(array));

    }
}
