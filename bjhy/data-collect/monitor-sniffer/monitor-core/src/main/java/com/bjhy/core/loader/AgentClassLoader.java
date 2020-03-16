package com.bjhy.core.loader;

import com.bjhy.common.config.AgentConfig;
import com.bjhy.common.config.AgentPackagePath;
import com.bjhy.common.logging.LoggerFactory;
import com.bjhy.common.logging.api.ILogger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @ClassName AgentConfig.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月07日 14:25:00
 */
public class AgentClassLoader extends ClassLoader {
    private static final ILogger logger = LoggerFactory.getLogger(AgentClassLoader.class);

    /**
     * 插件目录.
     */
    private static final String DICTIONARY_PLUGINS = "plugins";
    /**
     * jar文件夹列表.
     */
    private List<File> jarDictionaries;
    /**
     * 加载的插件jar列表.
     */
    private List<Jar> allJars;
    /**
     * jar扫描加锁.
     */
    private ReentrantLock jarScanLock = new ReentrantLock();
    /**
     * 默认加载器.
     */
    private static AgentClassLoader DEFAULT_LOADER;

    /**
     * 构造函数.
     *
     * @param parent    父类加载器
     */
    public AgentClassLoader(ClassLoader parent) {
        super(parent);
        File agentDictionary = AgentPackagePath.getAgentDir();
        jarDictionaries = new LinkedList<>();
        jarDictionaries.add(new File(agentDictionary, DICTIONARY_PLUGINS));
    }

    public static AgentClassLoader getDefault() {
        return DEFAULT_LOADER;
    }

    /**
     * 初始化默认加载器.
     */
    public static void initDefaultLoader() {
        if (DEFAULT_LOADER == null) {
            synchronized (AgentClassLoader.class) {
                if (DEFAULT_LOADER == null) {
                    DEFAULT_LOADER = new AgentClassLoader(AgentConfig.class.getClassLoader());
                }
            }
        }
    }

    /**
     * 重写：找class.
     *
     * @param name  类名
     * @return
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        List<Jar> allJars = getAllJars();
        String path = name.replace('.', '/').concat(".class");
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(path);
            if (entry != null) {
                try {
                    URL classFileUrl = getJarResourceUrl(jar.sourceFile.getAbsolutePath(), path);
                    byte[] data = null;
                    BufferedInputStream is = null;
                    ByteArrayOutputStream baos = null;
                    try {
                        is = new BufferedInputStream(classFileUrl.openStream());
                        baos = new ByteArrayOutputStream();
                        int ch = 0;
                        while ((ch = is.read()) != -1) {
                            baos.write(ch);
                        }
                        data = baos.toByteArray();
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException ignored) {
                                logger.warn("is.close():ignored IOException");
                            }
                        }
                        if (baos != null) {
                            try {
                                baos.close();
                            } catch (IOException ignored) {
                                logger.warn("baos.close():ignored IOException");
                            }
                        }
                    }
                    return defineClass(name, data, 0, data.length);
                } catch (MalformedURLException e) {
                    logger.error(e, "find class fail:" + e.getMessage());
                } catch (IOException e) {
                    logger.error(e, "find class fail:" + e.getMessage());
                }
            }
        }
        throw new ClassNotFoundException("Can't find " + name);
    }

    /**
     * 重写：找资源文件.
     *
     * @param name  资源名
     * @return
     */
    @Override
    protected URL findResource(String name) {
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                try {
                    return getJarResourceUrl(jar.sourceFile.getAbsolutePath(), name);
                } catch (MalformedURLException ex) {
                    continue;
                }
            }
        }
        return super.findResource(name);
    }


    /**
     * 重写：找资源文件列表.
     *
     * @param name  资源名
     * @return
     */
    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        List<URL> allResources = new LinkedList<>();
        List<Jar> allJars = getAllJars();
        for (Jar jar : allJars) {
            JarEntry entry = jar.jarFile.getJarEntry(name);
            if (entry != null) {
                allResources.add(getJarResourceUrl(jar.sourceFile.getAbsolutePath(), name));
            }
        }
        final Iterator<URL> iterator = allResources.iterator();
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public URL nextElement() {
                return iterator.next();
            }
        };
    }

    /**
     * 文件路径和资源名构建URL.
     *
     * @param jarFilePath   jar路径
     * @param resourceName  资源名
     * @return
     */
    private URL getJarResourceUrl(String jarFilePath, String resourceName) throws MalformedURLException {
        return new URL("jar:file:" + jarFilePath + "!/" + resourceName);
    }

    /**
     * 加载所有的插件jar列表.
     *
     * @return
     */
    private List<Jar> getAllJars() {
        if (allJars == null) {
            jarScanLock.lock();
            try {
                if (allJars == null) {
                    allJars = new LinkedList<>();
                    for (File path : jarDictionaries) {
                        if (path.exists() && path.isDirectory()) {
                            String[] jarFileNames = path.list(new FilenameFilter() {
                                @Override
                                public boolean accept(File dir, String name) {
                                    return name.endsWith(".jar");
                                }
                            });
                            for (String fileName : jarFileNames) {
                                try {
                                    File file = new File(path, fileName);
                                    Jar jar = new Jar(new JarFile(file), file);
                                    allJars.add(jar);
                                    logger.info(file.toString() + " loaded.");
                                } catch (IOException ex) {
                                    logger.info(path + "," + fileName + " jar file can't be resolved.");
                                }
                            }
                        }
                    }
                }
            } finally {
                jarScanLock.unlock();
            }
        }
        return allJars;
    }

    private class Jar {
        private JarFile jarFile;
        private File sourceFile;

        private Jar(JarFile jarFile, File sourceFile) {
            this.jarFile = jarFile;
            this.sourceFile = sourceFile;
        }
    }
}
