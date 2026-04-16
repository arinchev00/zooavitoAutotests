package tests;

import helpers.WebDriverContainer;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    public static void setUpDriver() {
        // Инициализируем драйвер один раз перед всеми тестами
        WebDriverContainer.getInstance().setDrivers();
    }
}
