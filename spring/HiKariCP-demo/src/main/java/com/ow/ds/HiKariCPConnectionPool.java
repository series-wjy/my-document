package com.ow.ds;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @ClassName HiKariCPConnectionPool.java
 * @Author wangjiayou
 * @Version 1.0.0
 * @Description TODO
 * @Create 2019年11月27日 11:59:00
 */
public class HiKariCPConnectionPool {
    private static class DataSourceHandler {
        private static HikariConfig config = null;
        static {
            config = new HikariConfig();
            config.setMinimumIdle(1);
            config.setMaximumPoolSize(2);
            config.setConnectionTestQuery("SELECT 1");
            //config.setDataSourceClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/kkb?serverTimezone=UTC");
            config.setUsername("root");
            config.setPassword("ouyang");
        }
        private static final DataSource instance = new HikariDataSource(config);
    }

    private static DataSource getDataSource() {
        return DataSourceHandler.instance;
    }

    public static void querySimple() {
        // 创建数据源
        DataSource ds = getDataSource();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // 获取数据库连接
            conn = ds.getConnection();
            // 创建Statement
            stmt = conn.createStatement();
            // 执行SQL
            rs = stmt.executeQuery("select * from depart");
            // 获取结果
            while (rs.next()) {
                int id = rs.getInt(1);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //关闭ResultSet
            close(rs);
            //关闭Statement
            close(stmt);
            //关闭Connection
            close(conn);
        }
    }

    //关闭资源
    static void close(AutoCloseable rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
