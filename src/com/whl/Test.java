package com.whl;

import sun.applet.AppletClassLoader;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class Test {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
//        System.out.println(Class.class.getClassLoader());//null --> BootStrapClassLoader
        Field field = Unsafe.class.getDeclaredField("theUnsafe");//getField只能获取public属性
        field.setAccessible(true);//关闭访问安全检查开关
        Unsafe unsafe = (Unsafe) field.get(null);
    }
}
