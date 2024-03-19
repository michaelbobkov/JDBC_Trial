package org.example.jdbc.utils;


import java.io.IOException;
import java.util.Properties;

public final class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();
    static {
        try {
            loadProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadProperties() throws IOException {
        var inputStream = PropertiesUtil.class
                .getClassLoader()
                .getResourceAsStream("application.properties");
        PROPERTIES.load(inputStream);
    }

    private PropertiesUtil() {
    }

    public static String get(String key){
        return PROPERTIES.getProperty(key);
    }
}
