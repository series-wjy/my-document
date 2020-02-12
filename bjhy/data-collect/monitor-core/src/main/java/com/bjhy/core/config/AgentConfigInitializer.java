package com.bjhy.core.config;

import com.bjhy.core.logging.LoggerFactory;
import com.bjhy.core.logging.api.ILogger;
import com.bjhy.core.util.PropertiesUtil;
import com.bjhy.core.util.StringUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * @ClassName AgentConfigInitializer.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2020年02月03日 11:11:33
 */
public class AgentConfigInitializer {
    private static final ILogger logger = LoggerFactory.getLogger(AgentConfigInitializer.class);

    private static String DEFAULT_CONFIG_FILE_NAME = "/config/agent.config";

    /**
     * 初始化标识..
     */
    private static boolean inited;

    /**
     * 获取初始化标识.
     *
     * @return
     */
    public static boolean isInited() {
        return inited;
    }

    /**
     * 初始化配置.
     */
    public static void init() {
        InputStreamReader configFileReader;
        try {
            configFileReader = loadConfigAsReader();
            Properties properties = new Properties();
            properties.load(configFileReader);
            for (String key : properties.stringPropertyNames()) {
                properties.put(key, PropertiesUtil.parseValue(key, properties));
            }
            initConfig(properties, AgentConfig.class);
        } catch (Exception e) {
            logger.error(e, "加载配置文件失败，采用默认配置");
        }

        AgentConfig.Agent.SERVER_INSTANCE_ID = UUID.randomUUID().toString();
        try {
            InetAddress ia = null;
            ia = InetAddress.getLocalHost();
            AgentConfig.Agent.HOSTNAME = ia.getHostName();
            AgentConfig.Agent.IP = ia.getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn(e, e.getMessage());
        }
        inited = true;
    }

    /**
     * 根据配置初始化配置.
     *
     * @param properties  properties对象
     * @param configClass 配置类
     */
    private static void initConfig(Properties properties, Class<AgentConfig> configClass) throws IllegalAccessException {
        initConfig(properties, configClass, new ArrayList<>());
    }

    /**
     * 配置迭代初始化.
     *
     * @param properties properties对象
     * @param clazz      配置类
     * @param perfixs    前缀名列表
     */
    private static void initConfig(Properties properties, Class<?> clazz, List<String> perfixs) throws IllegalAccessException {
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                String configKey = (String.join(".", perfixs) + "." + field.getName()).toLowerCase();
                String value = properties.getProperty(configKey);
                if (value != null) {
                    Class<?> type = field.getType();
                    if (type.equals(int.class)) {
                        field.set(null, Integer.valueOf(value));
                    } else if (type.equals(String.class)) {
                        field.set(null, value);
                    } else if (type.equals(long.class)) {
                        field.set(null, Long.valueOf(value));
                    } else if (type.equals(boolean.class)) {
                        field.set(null, Boolean.valueOf(value));
                    } else if (type.equals(List.class)) {
                        field.set(null, convert2List(value));
                    } else if (type.isEnum()) {
                        field.set(null, Enum.valueOf((Class<Enum>) type, value.toUpperCase()));
                    }
                }
            }
        }
        Class[] classes = clazz.getClasses();
        for (Class<?> cls : classes) {
            perfixs.add(cls.getSimpleName());
            initConfig(properties, cls, perfixs);
            perfixs.remove(cls.getSimpleName());
        }
    }

    /**
     * 字符串转list.
     *
     * @param value 配置值
     * @return
     */
    private static List convert2List(String value) {
        List result = new LinkedList();
        if (StringUtil.isEmpty(value)) {
            return result;
        }

        String[] segments = value.split(",");
        for (String segment : segments) {
            String trimmedSegment = segment.trim();
            if (!StringUtil.isEmpty(trimmedSegment)) {
                result.add(trimmedSegment);
            }
        }
        return result;
    }

    /**
     * 读取配置文件.
     *
     * @return
     */
    private static InputStreamReader loadConfigAsReader() {
        File configFile = new File(AgentPackagePath.getAgentDir(), DEFAULT_CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile()) {
            try {
                logger.info("Config file found in {}.", configFile);
                return new InputStreamReader(new FileInputStream(configFile), "UTF-8");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Fail to load agent.config", e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Fail to load agent.config", e);
            }
        }
        throw new RuntimeException("Fail to load agent config file.");
    }
}
