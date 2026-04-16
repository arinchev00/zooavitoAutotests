package tests.api;

import DTO.ApiResponse;
import DTO.AuthResponse;
import helpers.ApiClient;
import helpers.ConfigContainer;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static pages.AbstractPage.logger;

@Epic("API Тесты")
@Feature("Авторизация")
public class AuthTokenTest {

    private ApiClient apiClient = new ApiClient();
    private ConfigContainer config = ConfigContainer.getInstance();

    @Test
    @DisplayName("Получение токена через API")
    @Description("Тест проверяет авторизацию через API и получение JWT токена")
    @Story("API авторизация")
    public void testLoginViaApi() {
        String email = config.getConfigParameter("ADMIN_EMAIL");
        String password = config.getConfigParameter("ADMIN_PASSWORD");

        ApiResponse<AuthResponse> apiResponse = apiClient.loginAndGetTokenResponse(email, password);

        // === ПРОВЕРКИ ===
        // 1. Проверяем статус код
        assertEquals(200, apiResponse.getStatusCode(), "Статус код должен быть 200 OK");

        // 2. Проверяем, что тело ответа не null
        assertNotNull(apiResponse.getBody(), "Тело ответа не должно быть null");

        // 3. Проверяем, что токен не пустой
        assertNotNull(apiResponse.getBody().getToken(), "Токен не должен быть null");
        assertTrue(apiResponse.getBody().getToken().length() > 10, "Токен слишком короткий");

        // 4. Проверяем, что email в ответе совпадает с запрошенным (опционально)
        assertEquals(email, apiResponse.getBody().getUser().getEmail(),
                "Email в ответе не совпадает с запрошенным");
    }

    @Test
    @DisplayName("Сохранение токена в ConfigContainer")
    @Description("Тест проверяет авторизацию и сохранение токена для последующих запросов")
    @Story("Сохранение токена")
    public void testLoginAndSaveToken() {
        // Получаем и сохраняем токен
        apiClient.loginAndSaveToken(
                config.getConfigParameter("ADMIN_EMAIL"),
                config.getConfigParameter("ADMIN_PASSWORD")
        );

        String savedToken = config.getParameter("AUTH_TOKEN");
        assertNotNull(savedToken, "Токен должен быть сохранён");
        assertTrue(savedToken.length() > 10, "Сохранённый токен слишком короткий");

        logger.info("Токен сохранён: {}...", savedToken.substring(0, Math.min(30, savedToken.length())));
    }
}