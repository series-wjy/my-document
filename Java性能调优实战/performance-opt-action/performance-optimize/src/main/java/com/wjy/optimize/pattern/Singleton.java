package com.wjy.optimize.pattern;

import java.util.ArrayList;
import java.util.List;

// 懒汉模式 枚举实现
public class Singleton {
    public List<String> list = null;// list 属性

    private Singleton(){// 构造函数
        list = new ArrayList<String>();
    }
    // 使用枚举作为内部类
    private enum EnumSingleton {
        INSTANCE;// 不实例化
        private Singleton instance = null;

        private EnumSingleton(){// 构造函数
            instance = new Singleton();
        }
        public Singleton getSingleton(){
            return instance;// 返回已存在的对象
        }
    }

    public static Singleton getInstance(){
        return EnumSingleton.INSTANCE.getSingleton();// 返回已存在的对象
    }
}


