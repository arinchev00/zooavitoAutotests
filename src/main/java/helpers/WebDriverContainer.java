package helpers;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.io.File;
import java.util.logging.Level;

import static helpers.ConfigContainer.LOGGER;

public class WebDriverContainer {
    private static final int TIMEOUT_DURATION = 10000;
    /*******************************************************************************************************************
     * Поля класса.
     ******************************************************************************************************************/
    // Статический экземпляр этого класса
    private static WebDriverContainer instance;

    // Статический экземпляр Selenium WebDriver
    private static ChromeDriver driver;


    /**
     * Возвращает статический экземпляр этого класса (если класс еще не имеет экземпляра, то создает новый экземпляр).
     *
     * @return Статический экземпляр этого класса
     */
    public static synchronized WebDriverContainer getInstance() {
        if (instance == null) instance = new WebDriverContainer();
        return instance;
    }

    /**
     * Возвращает статический экземпляр WebDriver (инициализирует его если он еще не инициализирован).
     *
     * @return Статический экземпляр Selenium WebDriver
     */
    public WebDriver getWebDriver() {
        return driver;
    }

    /**
     * Инициализирует статический экземпляр WebDriver.
     */
    public void setDrivers() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver");

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.SEVERE);

        ChromeOptions options = new ChromeOptions();  //
        options.setCapability("goog:loggingPrefs", logPrefs);

        ChromeDriverService service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("src/test/resources/drivers/chromedriver"))
                .usingAnyFreePort()
                .withLogOutput(System.out)
                .build();

        driver = new ChromeDriver(service, options);
        driver.manage().deleteAllCookies();
        WebDriverRunner.setWebDriver(driver);

        Configuration.timeout = TIMEOUT_DURATION;  //
    }

    public static void CloseDrivers() {
        LOGGER.info("Ошибки консоли браузера:");
        driver.manage().logs().get(LogType.BROWSER).getAll().forEach(entry -> LOGGER.error(entry.toString()));
        driver.quit();
    }
}