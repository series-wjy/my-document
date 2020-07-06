package com.autumn.framework.constant;

/**
 * @ClassName ConfigConstants.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description 配置文件配置项
 * @Create 2020年04月19日 22:10:00
 */
public interface ConfigConstants {
    String CONFIG_FILE = "autumn.properties";

    String JDBC_URL = "autumn.framework.jdbc.url";
    String JDBC_DRIVER = "autumn.framework.jdbc.driver";
    String JDBC_USERNAME = "autumn.framework.jdbc.username";
    String JDBC_PASSWORD = "autumn.framework.jdbc.password";

    String APP_BASE_PACKAGE = "autumn.framework.app.base_package";
    String APP_JSP_PATH = "autumn.framework.app.jsp_path";
    String APP_ASSET_PATH = "autumn.framework.app.asset_path";
}
