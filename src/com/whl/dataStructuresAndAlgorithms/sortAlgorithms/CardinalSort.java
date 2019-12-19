package com.whl.dataStructuresAndAlgorithms.sortAlgorithms;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title: 基数排序
 * @Description:
 */
public class CardinalSort {

    public static void cardinalSort(int array[]){
        int max = 0;
        for(int i = 0;i<array.length;i++){
            if(array[i]>max){
                max = array[i];
            }
        }
        int maxDigit = 0;
        while (max/10 > 0){
            maxDigit++;
            max/=10;
        }
        int base = 10;
        int buckets[][] = new int[10][array.length];
        for(int i = 0;i<maxDigit;i++){
            int [] bucketLen = new int[10];
            for(int j = 0;j<array.length;j++){
                int whichBkt = (array[j]%base)/(base/10);
                buckets[whichBkt][bucketLen[whichBkt]] = array[j];
                bucketLen[whichBkt]++;
            }
            int k = 0;
            for(int a = 0;a<buckets.length;a++){
                for(int b = 0;b<bucketLen[a];b++){
                    array[k++] = buckets[a][b];
                }
            }
            base *= 10;
        }






    }

    public static void main(String[] args) {
        int a[] = { 9,1,2,5,7,4,8,6,3,2 };
        cardinalSort(a);
        System.out.println("排序结果：" + Arrays.toString(a));
    }
}
