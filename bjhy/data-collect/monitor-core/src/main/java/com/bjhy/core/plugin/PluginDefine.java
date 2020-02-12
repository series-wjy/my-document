package com.bjhy.core.plugin;


import com.bjhy.core.util.StringUtil;

/**
 * @ClassName InterceptorInstanceLoader.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年2月7日 17:12:45
 */
public class PluginDefine {
    /**
     * 插件名称.
     */
    private String pluginName;
    /**
     * 插件类名.
     */
    private String pluginClassName;

    /**
     * 构造函数.
     *
     * @param pluginName        插件名
     * @param pluginClassName   插件类名
     */
    public PluginDefine(String pluginName, String pluginClassName) {
        this.pluginName = pluginName;
        this.pluginClassName = pluginClassName;
    }

    /**
     * 根据配置构建插件定义对象.
     *
     * @param define 插件配置字符串（格式：pluginName=pluginClassName）
     * @return
     */
    public static PluginDefine build(String define) throws IllegalPluginDefineException {
        if (StringUtil.isEmpty(define)) {
            throw new IllegalPluginDefineException(define);
        }
        String[] pluginDefine = define.split("=");
        if (pluginDefine.length != 2) {
            throw new IllegalPluginDefineException(define);
        }
        String pluginName = pluginDefine[0];
        String pluginClassName = pluginDefine[1];
        return new PluginDefine(pluginName, pluginClassName);
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getPluginClassName() {
        return pluginClassName;
    }
}
