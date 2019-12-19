package com.whl.jdkAnalysis.collections.arrayList;

import java.util.Arrays;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class ArrayListDemo {
    private static final int DEFAULT_CAPACITY = 10;

    private static final int[] EMPTY_ELEMENT_DATA = {};

    int[] elementData;

    private int size;

    public ArrayListDemo() {
        this.elementData = EMPTY_ELEMENT_DATA;
    }

    public ArrayListDemo(int capacity) {
        if(capacity > 0) {
            this.elementData = new int[capacity];
        }else if(capacity == 0) {
            this.elementData = EMPTY_ELEMENT_DATA;
        }else {
            throw new IllegalArgumentException("非法的capacity："+capacity);
        }
    }

    //尾部添加元素
    public boolean add(int data) {
        //确保添加操作之前容量足够
        ensureCapacity(size+1);
        elementData[size++] = data;
        return true;
    }

    //指定index add
    public void add(int index,int data) {
        //检查下标 index只允许在size范围内
        rangeCheck(index);
        ensureCapacity(size + 1);
        System.arraycopy(elementData,index,elementData,index+1,size - index);
        elementData[index] = data;
        size++;
    }

    //获取指定index下的元素
    public int get(int index) {
        //检查下标
        rangeCheck(index);
        return elementData[index];
    }

    //修改指定index下的元素
    public int set(int index, int data) {
        rangeCheck(index);
        int oldVal = elementData[index];
        elementData[index] = data;
        return oldVal;
    }

    public int size(){
        return this.size;
    }

    private void rangeCheck(int index) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("非法的index：" + index);
        }
    }

    private void ensureCapacity(int minCapacity) {
        if(elementData == EMPTY_ELEMENT_DATA) {
            minCapacity = DEFAULT_CAPACITY;
        }

        if(minCapacity - elementData.length > 0) {
            //扩容
            int oldCapacity = elementData.length;
            int newCapacity = oldCapacity+(oldCapacity >> 1);
            if(newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = Arrays.copyOf(elementData,newCapacity);
        }
    }





    public static void main(String[] args) {
        ArrayListDemo arrayList = new ArrayListDemo();
        arrayList.add(0);
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);
        arrayList.add(3,99);
        System.out.println(arrayList.size);


    }
}
