package org.example.jdbc.utils;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionManager {
    private final static String URL_KEY ="db.url",
            USERNAME_KEY = "db.user",
            PASSWORD_KEY = "db.password";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static BlockingQueue<Connection> pool;

    static {
        try {
            initConnectionPool();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initConnectionPool() throws SQLException {
        String poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();

            var proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close") ?
                    pool.add((Connection) proxy):
                    method.invoke(connection, args));
            pool.add(proxyConnection);
        }
    }

    public static Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }

    }

    private static Connection open() throws SQLException {
        return DriverManager.getConnection(
                PropertiesUtil.get(URL_KEY),
                PropertiesUtil.get(USERNAME_KEY),
                PropertiesUtil.get(PASSWORD_KEY)
        );
    }
    public ConnectionManager() {
    }
}
