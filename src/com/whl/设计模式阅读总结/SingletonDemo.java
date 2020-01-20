package com.whl.designPatterns;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class SingletonDemo {
    private static volatile SingletonDemo singleton;

    private SingletonDemo() {
    }

    private static SingletonDemo getInstance() {
        //避免实例不为空时进入同步块争夺锁
        if (singleton == null) {
            //对SingletonDemo.class进行加锁, 避免出现多个线程同时进行初始化的问题
            synchronized (SingletonDemo.class) {
                if (singleton == null) {
                    singleton = new SingletonDemo();
                }
            }
        }
        return singleton;
    }
}
