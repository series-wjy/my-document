package com.bjhy.core.plugin;

/**
 * @ClassName IllegalPluginDefineException.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年2月7日 15:51:54
 */
public class IllegalPluginDefineException extends Exception {
    public IllegalPluginDefineException(String define) {
        super("Illegal plugin define : " + define);
    }
}