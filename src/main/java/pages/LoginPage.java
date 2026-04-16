package pages;

import java.util.Map;

public class LoginPage extends CommonPage {

    /*******************************************************************************************************************
     *                                  Локаторы элементов на страницы
     ******************************************************************************************************************/
    // Поле [Электронная почта]
    private static final String USER_EMAIL_ID = "email";
    //--------------------------------------------------------------------------------------------------------------------------
    // Поле [Пароль]
    private static final String USER_PASSWORD_ID = "password";
    //--------------------------------------------------------------------------------------------------------------------------
    // IFRAME токена капчи
    private static final String RECAPTCHA_IFRAME_XPATH = "//iframe[contains(@title, 'reCAPTCHA')]";
    //--------------------------------------------------------------------------------------------------------------------------
    // Чек-бокс токена капчи
    private static final String RECAPTCHA_TOKEN_XPATH = "//div[@class='recaptcha-checkbox-border']";
    //--------------------------------------------------------------------------------------------------------------------------
    // Кнопка [Вход]
    private static final String ENTER_BUTTON_HEADER_XPATH = "//a[contains(text(), 'Вход')]";
    //--------------------------------------------------------------------------------------------------------------------------
    // Кнопка [Войти]
    private static final String ENTER_BUTTON_XPATH = "//button[contains(text(), 'Войти')]";
    //--------------------------------------------------------------------------------------------------------------------------
    /*******************************************************************************************************************
     *                                        Методы страницы
     ******************************************************************************************************************/


    /**
     * Устанавливает данные для входа в личный кабинет
     *
     * @param credentials - параметр для выбора логина и пароля
     */
    public void setLoginCredentials(String credentials) {
        Map<String, String> loginCredentials = switch (credentials) {
            case "credentialsUser" -> Map.of(
                    config.getConfigParameter("USER_EMAIL"),
                    config.getConfigParameter("USER_PASSWORD")
            );
            case "credentialsAdmin" -> Map.of(
                    config.getConfigParameter("ADMIN_EMAIL"),
                    config.getConfigParameter("ADMIN_PASSWORD")
            );
            default -> throw new AssertionError("Передан неверный параметр - " + credentials);
        };
        clickOnElement(ENTER_BUTTON_HEADER_XPATH);
        waitForPageLoad();
        logger.info("Входит в систему под - " + loginCredentials.keySet().stream().findFirst().get());
        setValueForIdElement(USER_EMAIL_ID, loginCredentials.keySet().stream().findFirst().get());
        setValueForIdElement(USER_PASSWORD_ID, loginCredentials.values().stream().findFirst().get());
        clickElementInsideIframe(RECAPTCHA_IFRAME_XPATH, RECAPTCHA_TOKEN_XPATH);
        waitForPageLoad();
        clickOnElement(ENTER_BUTTON_XPATH);
        logger.info("Проверка редиректа на главную страницу");
        waitForUrlContains("/home", 2);
        logger.info("Авторизация пользователя прошла успешно");
    }
}
