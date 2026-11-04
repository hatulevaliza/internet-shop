package shop.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private final Properties properties = new Properties();

    public ConfigReader() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "application.properties не найден в classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить application.properties", e);
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public String getShopName() {
        return properties.getProperty("shop.name");
    }

    public String getEnv() {
        return properties.getProperty("shop.env");
    }
}