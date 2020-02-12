package com.bjhy.core.config;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName AgentConfigInitializer.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月03日 13:32:54
 */
public class AgentPackagePath {
    /**
     * Agent代理的目录路径.
     */
    private static File AGENT_PATH;

    /**
     * 获取Agent代理的目录路径.
     *
     * @return
     */
    public static File getAgentDir() {
        if (AGENT_PATH == null) {
            AGENT_PATH = findAgentDir();
        }
        return AGENT_PATH;
    }

    /**
     * 初始化agent所在的目录.
     *
     * @return
     */
    private static File findAgentDir() {
        String classResourcePath = AgentPackagePath.class.getName().replaceAll("\\.", "/") + ".class";
        URL resource = AgentPackagePath.class.getClassLoader().getSystemClassLoader().getResource(classResourcePath);
        if (resource != null) {
            String urlPath = resource.toString();
            int insidePathIndex = urlPath.indexOf("!");
            boolean isInJar = false;
            if (insidePathIndex > -1) {
                isInJar = true;
            }
            // 当前类路径是否在jar包中
            if (isInJar) {
                // 根据jar文件路径获取(jar:file:path/TestInJar-1.0-SNAPSHOT.jar!/test/TestInJar.class-->file:path)
                urlPath = urlPath.substring(urlPath.indexOf("file:"), insidePathIndex);
                File jarFile = null;
                try {
                    jarFile = new File(new URL(urlPath).getFile());
                    if (jarFile.exists()) {
                        return jarFile.getParentFile();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                // 根据class文件路径获取(file:/I:/gy4j/skywalking/git/gy4j-watcher/TestInJar/target/classes/test/TestInJar.class-->file:/I:/gy4j/skywalking/git/gy4j-watcher/TestInJar/target/classes)
                String classLocation = urlPath.substring(urlPath.indexOf("file:"), urlPath.length() - classResourcePath.length());
                try {
                    return new File(new URL(classLocation).getFile());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
