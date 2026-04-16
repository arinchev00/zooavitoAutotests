package tests.UI;

import helpers.ConfigContainer;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import tests.BaseTest;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

@Epic("UI Тесты")
@Feature("Авторизация")
public class LoginTest extends BaseTest {

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
    @DisplayName("Успешная авторизация администратора")
    @Description("Тест проверяет вход в систему с учётными данными администратора")
    @Story("Авторизация с валидными данными")
    public void testLogin() {
        // Получаем URL из конфига и открываем
        String siteUrl = config.getCurrentUrl();
        open(siteUrl);

        // Выполняем авторизацию
        loginPage.setLoginCredentials("credentialsAdmin");
    }
}