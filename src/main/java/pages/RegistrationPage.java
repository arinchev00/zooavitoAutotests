package pages;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;

public class RegistrationPage extends CommonPage {
    /*******************************************************************************************************************
     *                                  Локаторы элементов на страницы
     ******************************************************************************************************************/
    // Поле [Ваше имя]
    private static final String USER_FULL_NAME_ID = "fullName";
    //--------------------------------------------------------------------------------------------------------------------------
    // Поле [Электронная почта]
    private static final String USER_EMAIL_ID = "email";
    //--------------------------------------------------------------------------------------------------------------------------
    // Поле [Номер телефона (необязательно)]
    private static final String USER_TELEPHONE_NUMBER_ID = "telephoneNumber";
    //--------------------------------------------------------------------------------------------------------------------------
    // Поле [Пароль]
    private static final String USER_PASSWORD_ID = "password";
    //--------------------------------------------------------------------------------------------------------------------------
    // Поле [Подтверждение пароля]
    private static final String USER_CONFIRM_PASSWORD_ID = "confirmPassword";
    //--------------------------------------------------------------------------------------------------------------------------
    // IFRAME токена капчи
    private static final String RECAPTCHA_IFRAME_XPATH = "//iframe[contains(@title, 'reCAPTCHA')]";
    //--------------------------------------------------------------------------------------------------------------------------
    // Чек-бокс токена капчи
    private static final String RECAPTCHA_TOKEN_XPATH = "//div[@class='recaptcha-checkbox-border']";
    //--------------------------------------------------------------------------------------------------------------------------
    // Кнопка [Регистрация]
    private static final String REGISTER_BUTTON_HEADER_XPATH = "//a[contains(text(), 'Регистрация')]";
    //--------------------------------------------------------------------------------------------------------------------------
    // Кнопка [Зарегистрироваться]
    private static final String REGISTER_BUTTON_XPATH = "//button[contains(text(), 'Зарегистрироваться')]";
    // Кнопка [Войдите в систему]
    private static final String LOGIN_BUTTON_XPATH = "//a[contains(text(), 'Войдите в систему')]";
    //--------------------------------------------------------------------------------------------------------------------------


    // Сообщение об ошибке
    private static final String ERROR_MESSAGE_XPATH = "//div[contains(@class, 'error')]";
    //--------------------------------------------------------------------------------------------------------------------------
    private static final String EMAIL_EXISTS_ERROR_XPATH = "//div[contains(text(), 'Пользователь с таким email уже существует')]";
    /*******************************************************************************************************************
     *                                        Методы страницы
     ******************************************************************************************************************/


    /**
     * Регистрация нового пользователя (данные из конфига)
     */
    public void register(String userType) {
        String fullName;
        String email;
        String telephone;
        String password;

        switch (userType) {
            case "user":
                fullName = config.getConfigParameter("USER_FULL_NAME");
                email = config.getConfigParameter("USER_EMAIL");
                telephone = config.getConfigParameter("USER_TELEPHONE_NUMBER");
                password = config.getConfigParameter("USER_PASSWORD");
                break;
            case "userTwo":
                fullName = config.getConfigParameter("USER_TWO_FULL_NAME");
                email = config.getConfigParameter("USER_TWO_EMAIL");
                telephone = config.getConfigParameter("USER_TWO_PHONE");
                password = config.getConfigParameter("USER_TWO_PASSWORD");
                break;
            default:
                throw new AssertionError("Неверный параметр - " + userType);
        }

        clickOnElement(REGISTER_BUTTON_HEADER_XPATH);
        waitForPageLoad();
        logger.info("Регистрация пользователя: {}", email);

        setValueForIdElement(USER_FULL_NAME_ID, fullName);
        setValueForIdElement(USER_EMAIL_ID, email);
        setValueForIdElement(USER_TELEPHONE_NUMBER_ID, telephone);
        setValueForIdElement(USER_PASSWORD_ID, password);
        setValueForIdElement(USER_CONFIRM_PASSWORD_ID, password);
        clickElementInsideIframe(RECAPTCHA_IFRAME_XPATH, RECAPTCHA_TOKEN_XPATH);
        waitSomeSeconds(1);
        clickOnElement(REGISTER_BUTTON_XPATH);
        logger.info("Проверка редиректа на страницу логина");
        waitSomeSeconds(2);
        // СНАЧАЛА проверяем, не появилась ли ошибка
        if (isElementExist(EMAIL_EXISTS_ERROR_XPATH)) {

            // Делаем скриншот СРАЗУ
            takeScreenshot();

            // Логируем ошибку
            String errorText = getTextOfElement(EMAIL_EXISTS_ERROR_XPATH);
            logger.error("Ошибка регистрации: {}", errorText);
            Allure.addAttachment("Ошибка валидации", "text/plain", errorText);

            // Проваливаем тест с сообщением
            Assertions.fail("Регистрация не удалась: " + errorText);
        }
        waitForUrlContains("/login",1 );
        logger.info("Регистрация нового пользователя прошла успешно");
    }
}
