package com.hotel.dao;

import com.hotel.util.LogUtil;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 数据库连接工具类 - 简易连接池实现
 */
public class DBUtil {
    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    
    private static final int POOL_SIZE = 10;
    private static BlockingQueue<Connection> connectionPool;

    static {
        try {
            Properties props = new Properties();
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            props.load(is);
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver");
            Class.forName(driver);
            
            // 初始化连接池
            connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
            for (int i = 0; i < POOL_SIZE; i++) {
                connectionPool.offer(createConnection());
            }
            LogUtil.info("数据库连接池初始化完成，连接数: " + POOL_SIZE);
        } catch (Exception e) {
            LogUtil.error("数据库配置加载失败", e);
            throw new RuntimeException("数据库配置加载失败", e);
        }
    }
    
    private static Connection createConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /** 从连接池获取连接 */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = connectionPool.poll();
            if (conn == null || conn.isClosed()) {
                conn = createConnection();
            }
            return conn;
        } catch (SQLException e) {
            LogUtil.error("获取数据库连接失败", e);
            throw e;
        }
    }
    
    /** 归还连接到连接池 */
    private static void returnConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    connectionPool.offer(conn);
                }
            } catch (SQLException e) {
                LogUtil.error("归还连接失败", e);
            }
        }
    }

    /** 关闭资源（使用try-with-resources风格） */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            LogUtil.error("关闭ResultSet失败", e);
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            LogUtil.error("关闭Statement失败", e);
        }
        returnConnection(conn);
    }

    /** 关闭资源（无ResultSet） */
    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }

    /** 获取用于事务的连接（关闭自动提交） */
    public static Connection getTransactionConnection() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    /** 提交事务 */
    public static void commit(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.commit();
        }
    }

    /** 回滚事务 */
    public static void rollback(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.rollback();
        }
    }

    /** 关闭事务连接（恢复自动提交并归还连接池） */
    public static void closeTransactionConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                LogUtil.error("恢复自动提交失败", e);
            }
            returnConnection(conn);
        }
    }
}
