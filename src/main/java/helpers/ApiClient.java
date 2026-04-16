package helpers;

import DTO.AnnouncementResponse;
import DTO.AuthResponse;
import DTO.ApiResponse;
import io.restassured.http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.SneakyThrows;

import java.io.File;
import io.qameta.allure.restassured.AllureRestAssured;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static pages.AbstractPage.logger;

public class ApiClient {

    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_CREATED = 201;

    private final ConfigContainer config = ConfigContainer.getInstance();
    private String authToken;

    /**
     * API авторизация и получение ответа со статусом и телом
     * @param email Email пользователя
     * @param password Пароль пользователя
     * @return ApiResponse с статусом и AuthResponse
     */
    @SneakyThrows
    public ApiResponse<AuthResponse> loginAndGetTokenResponse(String email, String password) {
        logger.info("API авторизация пользователя: {}", email);

        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);
        credentials.put("recaptchaToken", "any_string_works");

        Response responseObj = given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .body(credentials)
                .when()
                .post(config.getConfigParameter("API_AUTH_URL"));

        int statusCode = responseObj.getStatusCode();
        String responseBody = responseObj.asString();

        ObjectMapper mapper = new ObjectMapper();
        AuthResponse authResponse = mapper.readValue(responseBody, AuthResponse.class);

        return new ApiResponse<>(statusCode, authResponse);
    }

    /**
     * API авторизация и получение только токена (для обратной совместимости)
     */
    public String loginAndGetToken(String email, String password) {
        ApiResponse<AuthResponse> response = loginAndGetTokenResponse(email, password);
        return response.getBody().getToken();
    }

    /**
     * API авторизация и сохранение токена в ConfigContainer
     */
    public void loginAndSaveToken(String email, String password) {
        String token = loginAndGetToken(email, password);
        config.setParameter("AUTH_TOKEN", token);
        logger.info("Токен сохранён в ConfigContainer");
    }

    /**
     * Получение токена (из кэша или через авторизацию)
     */
    public String getAuthToken() {
        if (authToken == null) {
            authToken = config.getParameter("AUTH_TOKEN");
            if (authToken == null) {
                logger.warn("Токен не найден, выполняем авторизацию");
                loginAndSaveToken(
                        config.getConfigParameter("ADMIN_EMAIL"),
                        config.getConfigParameter("ADMIN_PASSWORD")
                );
                authToken = config.getParameter("AUTH_TOKEN");
            }
        }
        return authToken;
    }

    /**
     * Создание объявления с возвратом ApiResponse
     * @param announcementJson JSON строка с данными объявления
     * @param fileName путь к изображению
     * @return ApiResponse с статусом и AnnouncementResponse
     */
    @SneakyThrows
    public ApiResponse<AnnouncementResponse> createAnnouncement(String announcementJson, String fileName) {
        File imageFile = new File(fileName);

        Response responseObj = given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.MULTIPART)
                .header("Authorization", "Bearer " + getAuthToken())
                .multiPart("announcement", announcementJson, "application/json")
                .multiPart("images", imageFile)
                .when()
                .post(config.getConfigParameter("API_ANNOUNCEMENT_URL"));

        int statusCode = responseObj.getStatusCode();
        String responseBody = responseObj.asString();

        ObjectMapper mapper = new ObjectMapper();
        AnnouncementResponse announcementResponse = mapper.readValue(responseBody, AnnouncementResponse.class);

        return new ApiResponse<>(statusCode, announcementResponse);
    }

    /**
     * Создание объявления с несколькими изображениями
     */
    public String createAnnouncementWithMultipleImages(String announcementJson, String... imagePaths) {
        var requestSpec = given()
                .contentType(ContentType.MULTIPART)
                .header("Authorization", "Bearer " + getAuthToken())
                .multiPart("announcement", announcementJson, "application/json");

        for (String imagePath : imagePaths) {
            requestSpec.multiPart("images", new File(imagePath));
        }

        String response = requestSpec
                .when()
                .post(config.getConfigParameter("API_ANNOUNCEMENT_URL"))
                .then()
                .assertThat()
                .statusCode(STATUS_CODE_CREATED)
                .extract()
                .asString();

        return response;
    }

    /**
     * Универсальный метод отправки multipart запроса
     */
    public void sendMultipartRequest(String endpoint, Map<String, String> formParams,
                                     Map<String, String> fileParams, String authToken) {
        var requestSpec = given()
                .contentType(ContentType.MULTIPART)
                .header("Authorization", "Bearer " + authToken);

        formParams.forEach(requestSpec::formParam);
        fileParams.forEach((key, path) -> requestSpec.multiPart(key, new File(path)));

        requestSpec
                .when()
                .post(endpoint)
                .then()
                .assertThat()
                .statusCode(STATUS_CODE_OK);
    }
}