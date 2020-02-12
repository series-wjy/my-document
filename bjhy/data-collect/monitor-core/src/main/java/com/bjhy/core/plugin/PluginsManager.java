package com.bjhy.core.plugin;

import com.bjhy.core.loader.AgentClassLoader;
import com.bjhy.core.logging.LoggerFactory;
import com.bjhy.core.logging.api.ILogger;
import com.bjhy.core.plugin.api.Plugin;
import com.bjhy.core.util.StringUtil;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName PluginsManager.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年2月10日 15:43:23
 */
public class PluginsManager {
    private static final ILogger logger = LoggerFactory.getLogger(PluginsManager.class);

    /**
     * 插件列表.
     */
    private static List<Plugin> plugins = new LinkedList<>();

    /**
     * 插件初始化.
     */
    public static void init() {
        AgentClassLoader.initDefaultLoader();
        try {
            Enumeration<URL> enumeration = AgentClassLoader.getDefault().getResources("monitor-plugin.def");
            while (enumeration.hasMoreElements()) {
                URL url = enumeration.nextElement();
                try {
                    plugins.addAll(loadPlugins(url));
                } catch (Throwable t) {
                    logger.error(t, "加载插件异常：" + url);
                }
            }
            logger.info("加载插件：" + plugins.size() + "个");
        } catch (IOException e) {
            logger.error(e, "加载插件列表异常：" + e.getMessage());
        }
    }

    /**
     * 插件匹配规则构建.
     *
     * @return
     */
    public static ElementMatcher<TypeDescription> buildMatch() {
        ElementMatcher.Junction junction = ElementMatchers.none();
        for (Plugin plugin : plugins) {
            junction = junction.or(plugin.buildJunction());
        }
        return junction;
    }

    /**
     * 根据类型匹配的插件对builder进行加强，实现对类的加强.
     *
     * @param builder         原构建器
     * @param typeDescription 类型对象
     * @param classLoader     类加载器
     * @return
     */
    public static DynamicType.Builder<?> enhanceBuilder(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader) {
        List<Plugin> matchedPlugins = findPlugins(typeDescription);
        DynamicType.Builder<?> newBuilder = builder;
        for (Plugin plugin : matchedPlugins) {
            newBuilder = plugin.enhance(newBuilder, typeDescription, classLoader);
        }
        return newBuilder;
    }

    /**
     * 根据typeDescription获取插件列表.
     *
     * @param typeDescription 类型对象
     * @return
     */
    private static List<Plugin> findPlugins(TypeDescription typeDescription) {
        List<Plugin> matchedPlugins = new LinkedList<>();
        for (Plugin plugin : plugins) {
            if (plugin.buildJunction().matches(typeDescription)) {
                matchedPlugins.add(plugin);
            }
        }
        return matchedPlugins;
    }

    /**
     * 加载插件.
     *
     * @param url 插件url
     * @return
     */
    private static List<Plugin> loadPlugins(URL url) throws IOException {
        List<Plugin> plugins = new LinkedList<>();
        InputStream inputStream = url.openStream();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String pluginDefine = null;
            while (((pluginDefine = reader.readLine()) != null)) {
                try {
                    if (StringUtil.isEmpty(pluginDefine) || pluginDefine.startsWith("#")) {
                        continue;
                    }
                    PluginDefine plugin = PluginDefine.build(pluginDefine);
                    Class<?> pluginClazz = Class.forName(plugin.getPluginClassName(), true, AgentClassLoader.getDefault());
                    plugins.add((Plugin) pluginClazz.newInstance());
                } catch (Exception ex) {
                    logger.error(ex, "Failed to format plugin(" + pluginDefine + ") define.");
                }
            }
        } finally {
            inputStream.close();
        }
        return plugins;
    }
}
