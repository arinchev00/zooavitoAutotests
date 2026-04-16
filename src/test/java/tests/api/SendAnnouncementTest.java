package tests.api;

import api.Announcement;
import DTO.AnnouncementResponse;
import DTO.ApiResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@Feature("Объявления")
public class SendAnnouncementTest {

    private Announcement announcement = new Announcement();
    private ConfigContainer config = ConfigContainer.getInstance();
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Создание объявления через API")
    @Description("Тест проверяет создание нового объявления через API")
    @Story("Создание объявления с валидными данными")
    public void sendAnnouncement() throws Exception {
        // Получаем ожидаемый заголовок из конфига
        String originalJson = config.getConfigParameter("CAT_ANNOUNCEMENT_JSON");
        JsonNode originalNode = mapper.readTree(originalJson);
        String expectedTitle = originalNode.get("title").asText();

        // Создаём объявление
        ApiResponse<AnnouncementResponse> apiResponse = announcement.createAnnouncementFromConfig("CAT_ANNOUNCEMENT_JSON", "IMAGE");

        // === ПРОВЕРКИ ===
        // 1. Проверяем статус код
        assertEquals(201, apiResponse.getStatusCode(), "Статус код должен быть 201 Created");

        // 2. Проверяем, что тело ответа не null
        assertNotNull(apiResponse.getBody(), "Тело ответа не должно быть null");

        // 3. Проверяем заголовок
        assertEquals(expectedTitle, apiResponse.getBody().getTitle(),
                String.format("Заголовок не совпадает. Ожидалось: '%s'", expectedTitle));
    }
}