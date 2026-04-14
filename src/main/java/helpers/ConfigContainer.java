package helpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigContainer implements Serializable {
    static final Logger LOGGER = LoggerFactory.getLogger(ConfigContainer.class);

    // Статический экземпляр этого класса (собственно сам ConfigContainer)
    private static volatile ConfigContainer instance;

    // Настройки тестовой среды (считываются из файла config.properties и используются во всех тестовых сценариях)
    private final Properties properties = new Properties();

    private ConfigContainer() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);  // ← ЗАГРУЖАЕМ файл!
                System.out.println("Конфиг загружен, размер: " + properties.size());
            } else {
                System.out.println("Файл config.properties не найден!");
            }
        } catch (IOException e) {
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
        //String siteUrl = null;
        LOGGER.info("Переменной среды SITE_URL_TUNNEL установлено значение " + siteUrl);
        if (siteUrl != null) {
            if (!(!siteUrl.isEmpty() && siteUrl.charAt(siteUrl.length() - 1) == '/')) {
                siteUrl = siteUrl + "/";
            }
        } else siteUrl = SiteURL.SITE_URL_TUNNEL.getUrl();
        return siteUrl;
    }

    public String getConfigParameter(String key) {
        return properties.getProperty(key);
    }
}
