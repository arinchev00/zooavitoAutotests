package helpers;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigContainer implements Serializable {
    static final Logger LOGGER = LoggerFactory.getLogger(ConfigContainer.class);

    private static volatile ConfigContainer instance;
    private final Properties properties = new Properties();
    private final Map<String, String> parameters = new HashMap<>();

    private ConfigContainer() {
        try {
            java.net.URL resource = getClass().getClassLoader().getResource("config.properties");
            if (resource != null) {
                Path path = Paths.get(resource.toURI());
                properties.load(Files.newBufferedReader(path, StandardCharsets.UTF_8));
                System.out.println("Конфиг загружен, размер: " + properties.size());

                // Проверка
                String testJson = properties.getProperty("CAT_ANNOUNCEMENT_JSON");
                if (testJson != null) {
                    System.out.println("JSON из properties: " + testJson);
                }
            } else {
                System.out.println("Файл config.properties не найден!");
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static ConfigContainer getInstance() {
        if (instance == null) {
            synchronized (ConfigContainer.class) {
                if (instance == null) {
                    instance = new ConfigContainer();
                }
            }
        }
        return instance;
    }

    public String getCurrentUrl() {
        String siteUrl = System.getenv("SITE_URL_TUNNEL");
        LOGGER.info("Переменная среды SITE_URL_TUNNEL: " + siteUrl);
        if (siteUrl != null && !siteUrl.isEmpty()) {
            if (!siteUrl.endsWith("/")) {
                siteUrl = siteUrl + "/";
            }
        } else {
            siteUrl = SiteURL.SITE_URL_TUNNEL.getUrl();
        }
        return siteUrl;
    }

    public String getConfigParameter(String key) {
        return properties.getProperty(key);
    }

    public String getParameter(String key) {
        String value = parameters.get(key);
        LOGGER.info("Получен ключ: [{}] и параметр: [{}]", key, value);
        return value;
    }

    public void setParameter(String key, String value) {
        LOGGER.info("Установлен ключ: [{}] и параметр: [{}]", key, value);
        parameters.put(key, value);
    }
}