package pages;

public class CommonPage extends AbstractPage {
    // Сообщение - Непредвиденная ошибка (может появляться в различных местах)
    protected static final String UNEXPECTED_ERROR_XPATH = "//*[contains(text(), 'Непредвиденная ошибка')]";
    //--------------------------------------------------------------------------------------------------------------------------
    // Нотификатор или элемент с текстом '%s'
    protected static final String DIV_WITH_TEXT_XPATH = "//div[contains(., \"%s\")]";
    //--------------------------------------------------------------------------------------------------------------------------

    /**
     * Проверяет, отобразился ли элемент с указанным текстом
     *
     * @param text - указанный текст
     * @return true - если элемент с указанным текстом отобразился, false - если элемент с указанным текстом не отобразился
     */
    public boolean isNotifierOrElementWithTextVisible(String text) {
        return isElementVisible(String.format(DIV_WITH_TEXT_XPATH, text));
    }

    public void waitNotifierOrElementWithTextVisible(String text) {
        waitVisibleElement(String.format(DIV_WITH_TEXT_XPATH, text));
    }
}
