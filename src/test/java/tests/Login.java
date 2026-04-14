package tests;

import helpers.ConfigContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pages.LoginPage;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

public class Login {

    private LoginPage loginPage;
    private ConfigContainer config;

    @BeforeEach
    public void setUp() {
        // Инициализация
        loginPage = new LoginPage();
        config = ConfigContainer.getInstance();
    }

    @AfterEach
    public void tearDown() {
        // Закрываем браузер после теста
        closeWebDriver();
    }

    @Test
    public void testLogin() {
        // Получаем URL из конфига и открываем
        String siteUrl = config.getCurrentUrl();
        open(siteUrl);

        // Выполняем авторизацию
        loginPage.setLoginCredentials("credentialsAdmin");

        System.out.println("✅ Тест авторизации выполнен");
    }
}