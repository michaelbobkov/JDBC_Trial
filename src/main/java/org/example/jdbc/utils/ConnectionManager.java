package org.example.jdbc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private final static String URL_KEY ="db.url",
            USERNAME_KEY = "db.user",
            PASSWORD_KEY = "db.password";

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(
                PropertiesUtil.get(URL_KEY),
                PropertiesUtil.get(USERNAME_KEY),
                PropertiesUtil.get(PASSWORD_KEY)
        );
    }
    public ConnectionManager() {
    }
}
