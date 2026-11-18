package edu.course.selection.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("找不到数据库配置文件 db.properties");
            }
            PROPERTIES.load(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        String url = setting("DB_URL", "db.url");
        String user = setting("DB_USER", "db.user");
        String password = setting("DB_PASSWORD", "db.password");
        return DriverManager.getConnection(url, user, password);
    }

    private static String setting(String environmentKey, String propertyKey) {
        String environmentValue = System.getenv(environmentKey);
        return environmentValue != null ? environmentValue : PROPERTIES.getProperty(propertyKey, "");
    }
}
