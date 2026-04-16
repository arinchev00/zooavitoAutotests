package api;

import DTO.AnnouncementResponse;
import DTO.ApiResponse;
import helpers.ApiClient;
import helpers.ConfigContainer;
import static pages.AbstractPage.logger;

public class Announcement {

    private final ConfigContainer config = ConfigContainer.getInstance();
    private final ApiClient apiClient = new ApiClient();

    /**
     * Создание объявления из конфига по ключам
     * @param configKey ключ в конфиге с JSON объявления (например, "CAT_ANNOUNCEMENT_JSON")
     * @param imageKey ключ в конфиге с путём к изображению (например, "IMAGE")
     * @return ID созданного объявления или JSON-ответ от сервера
     */
    public ApiResponse<AnnouncementResponse> createAnnouncementFromConfig(String configKey, String imageKey) {
        logger.info("Создание объявления из конфига: {}", configKey);

        String imagePath = config.getConfigParameter(imageKey);
        String announcementJson = config.getConfigParameter(configKey);

        logger.info("JSON для отправки: {}", announcementJson);
        return apiClient.createAnnouncement(announcementJson, imagePath);
    }
}