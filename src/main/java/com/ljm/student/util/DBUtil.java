package com.ljm.student.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {
    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            Properties properties = new Properties();

            InputStream is = DBUtil.class
                    .getClassLoader()
                    .getResourceAsStream("db.properties");

            properties.load(is);

            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");

        } catch (Exception e) {
            System.out.println("读取数据库配置失败");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}