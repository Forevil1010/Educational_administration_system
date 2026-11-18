package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBUtil {
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("config/db.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            throw new ExceptionInInitializerError("无法读取 config/db.properties：" + ex.getMessage());
        }
        URL = properties.getProperty("url");
        USER = properties.getProperty("user");
        PASSWORD = properties.getProperty("password");
        if (URL == null || USER == null || PASSWORD == null) {
            throw new ExceptionInInitializerError("config/db.properties 必须包含 url、user 和 password");
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("未找到 MySQL JDBC 驱动，请把 mysql-connector-j 加入 classpath");
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
