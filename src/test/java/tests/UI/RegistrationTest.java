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
import pages.RegistrationPage;
import tests.BaseTest;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

@Epic("UI Тесты")
@Feature("Регистрация")
public class RegistrationTest extends BaseTest {

    private RegistrationPage registrationPage;
    private ConfigContainer config;

    @BeforeEach
    public void setUp() {
        // Инициализация
        registrationPage = new RegistrationPage();
        config = ConfigContainer.getInstance();
    }

    @AfterEach
    public void tearDown() {
        // Закрываем браузер после теста
        closeWebDriver();
    }

    @Test
    @DisplayName("Регистрация нового пользователя")
    @Description("Тест проверяет регистрацию нового пользователя через UI")
    @Story("Регистрация с валидными данными")
    public void testLogin() {
        // Получаем URL из конфига и открываем
        String siteUrl = config.getCurrentUrl();
        open(siteUrl);

        // Выполняем авторизацию
        registrationPage.register("user");
    }
}