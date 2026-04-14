package pages;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Selenide.switchTo;
import helpers.ConfigContainer;
import helpers.WebDriverContainer;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.url;

public abstract class AbstractPage {
    private static final int REPEAT_TIMEOUT = 500;
    private static final int MAX_COUNT_OF_ATTEMPTS = 10;
    private static final int OK_STATUS_CODE = 200;
    private static final Duration FILE_LOAD_DURATION = Duration.ofMinutes(3);
    public static final String DIR_DOWNLOAD = System.getProperty("user.dir") + "\\src\\test\\resources\\download\\";
    private static final String SPINNER_XPATH = "//div[contains(@class, 'k-loading-mask')]";
    public static WebDriver driver;
    protected ConfigContainer config;
    protected Duration duration = Duration.ofMinutes(1);
    public static final Logger logger = LoggerFactory.getLogger(AbstractPage.class);

    public AbstractPage() {
        driver = WebDriverContainer.getInstance().getWebDriver();
        config = ConfigContainer.getInstance();
    }

    /**
     * Возвращает элемент типа SelenideElement ($x или $) автоматически распознав тип локатора по его содержимому.
     * @param locator CSS или Xpath локатор элемента
     * @return элемент типа SelenideElement ($x или $)
     */
    private SelenideElement getSelenideElement(String locator) {
        return locator.contains("//") ? $x(locator) : $(locator);
    }

    /**
     * Ожидает в течение указанного количества секунд
     * @param seconds количество секунд
     */
    public void waitSomeSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            logger.error("Ошибка при ожидании: " + e.getMessage());
        }
    }

    //region Методы ожидания состояний

    /**
     * Ожидает окончания выполнения всех AJAX в течение 1 минуты
     * На сайте не используется jQuery, поэтому метод отключён
     */
    public void waitForAjaxControls() {
        // jQuery не используется на сайте, поэтому просто пропускаем
        logger.debug("jQuery не используется, пропускаем waitForAjaxControls");
    }

    /**
     * Ожидает, что элемент станет {@code visible} в течение 1 минуты, предварительно выполнив {@link #waitForAjaxControls()}
     * @param locator CSS или Xpath локатор элемента
     * @see #waitVisibilityOfElementWithRefresh(String)
     */
    public void waitVisibleElement(String locator) {
        getSelenideElement(locator).shouldBe(visible, duration);
    }

    /**
     * Ожидает, что элемент станет {@code visible} в течение 1 минуты, проверяя состояние элемента каждые 10 секунд и обновляя
     * страницу
     * @param locator CSS или Xpath локатор элемента
     * @see #waitVisibleElement(String)
     * @see #waitElementExistWithRefresh(String)
     */
    protected void waitVisibilityOfElementWithRefresh(String locator) {
        int nTry = 0;
        while ((!isElementVisible(locator))) {
            Assertions.assertTrue(nTry < 6, "Допустимое число попыток превышено, элемент не отображается");
            refreshPage();
            nTry++;
        }
    }

    /**
     * Ожидает, что элемент станет {@code exist} в течение 1 минуты, проверяя состояние элемента каждые 10 секунд и обновляя
     * страницу
     * @param locator CSS или Xpath локатор элемента
     * @see #waitVisibilityOfElementWithRefresh(String)
     */
    protected void waitElementExistWithRefresh(String locator) {
        int nTry = 0;
        while (!isElementExist(locator)) {
            Assertions.assertTrue(nTry < 6, "Допустимое число попыток превышено, элемент не отображается");
            refreshPage();
            nTry++;
        }
    }

    /**
     * Ожидает исчезновения элемента в течение 1 минуты, предварительно выполнив {@link #waitForAjaxControls()}
     * @param locator CSS или Xpath локатор элемента
     * @see #waitUntilElementDisappearForExternalPage(String)
     */
    protected void waitUntilElementDisappear(String locator) {
        getSelenideElement(locator).shouldBe(disappear, duration);
    }

    /**
     * Ожидает исчезновения элемента в течение 1 минуты
     * @param locator CSS или Xpath локатор элемента
     * @see #waitUntilElementDisappear(String)
     */
    protected void waitUntilElementDisappearForExternalPage(String locator) {
        getSelenideElement(locator).shouldBe(disappear, duration);
    }

    /**
     * Ожидает окончания загрузки страницы в течение одной минуты
     */
    public void waitForPageLoaded() {
        try {
            new WebDriverWait(driver, duration).until(
                    (ExpectedCondition<Boolean>) wd ->
                            Objects.requireNonNull(executeJavaScript("return document.readyState"))
                                    .toString()
                                    .equals("complete")
            );
        } catch (WebDriverException e) {
            logger.error("Произошла ошибка при ожидании загрузки страницы - " + e.getMessage());
        }
    }

    /**
     * Ожидает, что грид станет {@code visible} в течение 1 минуты, проверяя его состояние каждые 10 секунд и обновляя страницу
     */
    protected void waitGridAvailable() {
        int nTry = 0;
        while (!isIdElementVisible("All")) {
            Assertions.assertTrue(nTry < 6, "Допустимое число попыток превышено, грид не отображается");
            refreshPage();
            nTry++;
        }
    }

    /**
     * Ожидает значение атрибута {@code disabled = false} в течение 1 минуты, проверяя значение этого атрибута каждую секунду
     * @param buttonLocator CSS или Xpath локатор элемента
     * @see #getValueOfAttributeOfElement(String locator, String attribute)
     */
    protected void waitButtonActive(String buttonLocator) {
        int nTry = 0;
        while ((Boolean.parseBoolean(getValueOfAttributeOfElement(buttonLocator, "disabled"))) && (nTry != 60)) {
            nTry++;
            waitSomeSeconds(1);
        }
    }

    /**
     * Вводит текст в поле с автокомплитом, убирая последний символ.
     * Ожидает ответ от поля с автокомплитом в течение 1 минуты и выбирает его после появления
     * @param inputPath       CSS или Xpath локатор поля с автокомплитом
     * @param value           текст, который будет введен в поле
     * @param locatorResponse CSS или Xpath локатор ответа
     */
    protected void waitForListResponse(String inputPath, String value, String locatorResponse) {
        WebElement element = getSelenideElement(inputPath);
        element.clear();
        element.click();
        element.sendKeys(value);
        element.sendKeys(Keys.BACK_SPACE);
        getSelenideElement(locatorResponse).shouldBe(exist, duration).click();
    }

    /**
     * Ожидает появления текста в элементе в течение 1 минуты кроме пустого значения и {@code "empty"}
     * @param locator CSS или Xpath локатор элемента
     */
    protected void waitElementText(String locator) {
        getSelenideElement(locator).shouldNot(exactText("empty"), duration).shouldNot(exactText(""), duration);
    }

    /**
     * Ожидает исчезновение спиннера, если он отображается в данный момент
     * @see #SPINNER_XPATH
     */
    public void waitUntilSpinnerDisappear() {
        if (getSelenideElement(SPINNER_XPATH).isDisplayed())
            waitUntilElementDisappear(SPINNER_XPATH);
    }

    /**
     * Ожидает загрузки файла с сервера в течение 3 минут, проверяя его состояние каждые 500 мс
     * @param nameOfFile фрагмент имени загружаемого файла
     */
    public void waitFileDownload(String nameOfFile) {
        String finalNameOfFile = nameOfFile.replace(":", "");
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(FILE_LOAD_DURATION)
                .pollingEvery(Duration.ofMillis(REPEAT_TIMEOUT))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
        wait.until((x) -> {
            File[] files = new File(DIR_DOWNLOAD).listFiles();
            return files != null && Arrays.stream(files).anyMatch(file -> file.getName().contains(finalNameOfFile));
        });
    }
    //endregion
    //region Методы проверки состояний

    /**
     * Проверяет свойство {@code isDisplayed()} элемента в течение 10 секунд через каждую 1 секунду до тех пор, пока не вернет
     * {@code true}
     * @param locator CSS или Xpath локатор элемента
     * @return значение свойства {@code isDisplayed()} элемента
     * @see #isIdElementVisible(String)
     */
    public boolean isElementVisible(String locator) {
        int nTry = 0;
        while ((!getSelenideElement(locator).isDisplayed()) && (nTry < MAX_COUNT_OF_ATTEMPTS)) {
            waitSomeSeconds(1);
            nTry++;
        }
        return getSelenideElement(locator).isDisplayed();
    }

    /**
     * Проверяет свойство {@code isDisplayed()} элемента в течение 10 секунд через каждую 1 секунду до тех пор, пока не вернет
     * {@code true}
     * @param id id элемента
     * @return значение свойства {@code isDisplayed()} элемента
     * @see #isElementVisible(String)
     */
    protected boolean isIdElementVisible(String id) {
        int nTry = 0;
        while ((!$(By.id(id)).isDisplayed()) && (nTry < MAX_COUNT_OF_ATTEMPTS)) {
            waitSomeSeconds(1);
            nTry++;
        }
        return $(By.id(id)).isDisplayed();
    }

    /**
     * Проверяет свойство {@code exists()} элемента в течение 10 секунд через каждую 1 секунду до тех пор, пока не вернет
     * {@code true}
     * @param locator CSS или Xpath локатор элемента
     * @return значение свойства {@code exists()} элемента
     */
    public boolean isElementExist(String locator) {
        int nTry = 0;
        while ((!(getSelenideElement(locator).exists())) && (nTry != MAX_COUNT_OF_ATTEMPTS)) {
            waitSomeSeconds(1);
            nTry++;
        }
        return getSelenideElement(locator).exists();
    }

    /**
     * Проверяет значение свойства {@code isSelected()} чекбокса, предварительно ожидая что элемент станет {@code exist} в
     * течение минуты
     * @param id id чекбокса
     * @return значение свойства {@code isSelected()} чекбокса
     */
    protected boolean isCheckboxWithIdChecked(String id) {
        return $(By.id(id)).shouldBe(exist, duration).isSelected();
    }
    //endregion
    //region Методы получения значений атрибутов элементов

    /**
     * Получает значение указанного атрибута элемента
     * @param locator   CSS или Xpath локатор элемента
     * @param attribute атрибут, значение которого необходимо получить
     * @return строковое значение указанного атрибута
     * @see #getValueOfAttributeOfIdElement(String)
     */
    protected String getValueOfAttributeOfElement(String locator, String attribute) {
        return getSelenideElement(locator).shouldBe(exist, duration).getAttribute(attribute);
    }

    /**
     * Получает значение атрибута {@code value} элемента
     * @param id id элемента
     * @return строковое значение атрибута {@code value}
     * @see #getValueOfAttributeOfElement(String, String)
     */
    protected String getValueOfAttributeOfIdElement(String id) {
        return $(By.id(id)).shouldBe(exist, duration).getAttribute("value");
    }

    /**
     * Получает значение CSS-атрибута элемента
     * @param locator   CSS или Xpath локатор элемента
     * @param attribute атрибут, значение которого необходимо получить
     * @return строковое значение CSS-атрибута
     */
    protected String getValueOfElementCSSAttribute(String locator, String attribute) {
        return getSelenideElement(locator).shouldBe(visible, duration).getCssValue(attribute);
    }

    /**
     * Получает текст, содержащийся в элементе
     * @param locator CSS или Xpath локатор элемента
     * @return текст, содержащийся в элементе
     * @see #getTextOfIdElement(String)
     */
    public String getTextOfElement(String locator) {
        String textInElement = getSelenideElement(locator).shouldBe(exist, duration).getText();
        logger.info("Текст, содержащийся в элементе - " + textInElement);
        return textInElement;
    }

    /**
     * Получает текст, содержащийся в элементе
     * @param id id элемента
     * @return текст, содержащийся в элементе
     * @see #getTextOfElement(String)
     */
    protected String getTextOfIdElement(String id) {
        String textInElement = $(By.id(id)).shouldBe(exist, duration).getText();
        logger.info("Текст, содержащийся в элементе - " + textInElement);
        return textInElement;
    }

    /**
     * Получает текущий URL
     * @return текущий URL
     */
    public String getSiteUrl() {
        return url();
    }

    public String getDocumentGUIDFromSiteURL() {
        return getSiteUrl().substring(getSiteUrl().lastIndexOf('/') + 1);
    }

    /**
     * Получает количество элементов с указанным локатором на странице
     * @param locator CSS или Xpath локатор элемента
     * @return количество элементов на странице
     */
    protected int getNumberOfElements(String locator) {
        List<WebElement> webElementList = driver.findElements(By.xpath(locator));
        return webElementList.size();
    }
    //endregion
    //region Методы нажатия на элемент

    /**
     * Нажимает на элемент, когда он становится {@code visible}, предварительно ожидая завершения выполнения всех AJAX и
     * исчезновения спиннера
     * @param locator CSS или Xpath локатор элемента
     * @see #clickOnIdElement(String)
     * @see #clickOnElementOnExternalPage(String)
     */
    public void clickOnElement(String locator) {
        waitUntilSpinnerDisappear();
        getSelenideElement(locator).shouldBe(visible, duration).click();
        waitUntilSpinnerDisappear();
    }

    /**
     * Нажимает на элемент с помощью JavaScript, когда он становится {@code visible}, предварительно ожидая завершения
     * выполнения всех AJAX и исчезновения спиннера
     * @param locator CSS или Xpath локатор элемента
     * @see #clickOnElement(String)
     */
    protected void clickOnElementByJS(String locator) {
        waitUntilSpinnerDisappear();
        SelenideElement element = getSelenideElement(locator);
        element.shouldBe(visible, duration);
        executeJavaScript("arguments[0].click()", element);
        waitUntilSpinnerDisappear();
    }

    /**
     * Нажимает на элемент, когда он становится {@code visible}, предварительно ожидая завершения выполнения всех AJAX
     * @param id id элемента
     * @see #clickOnElement(String)
     */
    protected void clickOnIdElement(String id) {
        $(By.id(id)).shouldBe(visible, duration).click();
    }

    /**
     * Нажимает на элемент, когда он становится {@code visible}
     * @param locator CSS или Xpath локатор элемента
     * @see #clickOnElement(String)
     */
    protected void clickOnElementOnExternalPage(String locator) {
        getSelenideElement(locator).shouldBe(visible, duration).click();
    }

    /**
     * Переключается в iframe и кликает по элементу внутри него
     * @param iframeLocator XPath локатор iframe
     * @param elementLocator XPath локатор элемента внутри iframe
     */
    public void clickElementInsideIframe(String iframeLocator, String elementLocator) {
        logger.info("Переключение в iframe: {}", iframeLocator);

        // Ждём появления iframe
        waitVisibleElement(iframeLocator);

        // Переключаемся в iframe
        SelenideElement iframeElement = $(By.xpath(iframeLocator));
        switchTo().frame(iframeElement);

        // Кликаем по элементу внутри iframe
        clickOnElement(elementLocator);

        // Возвращаемся на основную страницу
        switchTo().defaultContent();

        logger.info("Выход из iframe");
    }

    /**
     * Переключается в iframe по индексу (если несколько iframe)
     * @param frameIndex индекс iframe (начинается с 0)
     * @param elementLocator XPath локатор элемента внутри iframe
     */
    public void clickElementInsideIframe(int frameIndex, String elementLocator) {
        logger.info("Переключение в iframe с индексом: {}", frameIndex);

        driver.switchTo().frame(frameIndex);
        clickOnElement(elementLocator);
        driver.switchTo().defaultContent();
    }

    //endregion
    //region Методы прокрутки страницы

    /**
     * Прокручивает элемент в зону видимости, предварительно выполнив {@link #waitForAjaxControls()}
     * @param locator    CSS или Xpath локатор элемента
     * @param alignToTop указывает необходимость расположения по верхнему краю страницы. {@code True} - располагает элемент по
     *                   верхнему краю страницы. {@code False} - располагает элемент по нижнему краю страницы
     */
    protected void scrollElementIntoView(String locator, boolean alignToTop) {
        getSelenideElement(locator).shouldBe(visible, duration).scrollIntoView(alignToTop);
    }

    /**
     * Прокручивает элемент в середину страницы с помощью JavaScript
     * @param locator CSS или Xpath локатор элемента
     */
    protected void scrollElementIntoMiddle(String locator) {
        String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, " +
                "window.innerHeight || 0);"
                + "var elementTop = arguments[0].getBoundingClientRect().top;"
                + "window.scrollBy(0, elementTop-(viewPortHeight/2));";
        executeJavaScript(scrollElementIntoMiddle, getSelenideElement(locator));
    }

    /**
     * Прокручивает страницу вниз с помощью JavaScript, предварительно выполнив {@link #waitForAjaxControls()}
     * @see #scrollUpPage()
     */
    protected void scrollDownPage() {
        executeJavaScript("window.scrollBy(0,250)", "");
    }

    /**
     * Прокручивает страницу вверх с помощью JavaScript, предварительно выполнив {@link #waitForAjaxControls()}
     * @see #scrollDownPage()
     */
    protected void scrollUpPage() {
        executeJavaScript("window.scrollBy(0, -250)", "");
    }
    //endregion
    //region Методы установки значения элементам

    /**
     * Устанавливает значение атрибуту {@code value} элемента, предварительно выполнив {@link #waitForAjaxControls()}
     * @param locator CSS или Xpath локатор элемента
     * @param value   устанавливаемое значение
     * @see #setValueForIdElement(String, String)
     */
    public void setValueForElement(String locator, String value) {
        getSelenideElement(locator).shouldBe(visible, duration).scrollTo().setValue(value);
    }

    /**
     * Устанавливает значение атрибуту {@code value} элемента, предварительно выполнив {@link #waitForAjaxControls()}
     * @param id    id элемента
     * @param value устанавливаемое значение
     * @see #setValueForElement(String, String)
     */
    protected void setValueForIdElement(String id, String value) {
        $(By.id(id)).shouldBe(visible, duration).scrollTo().setValue(value);
    }

    /**
     * Устанавливает заданный контент для элемента, выполнив {@link #waitForAjaxControls()} до и после
     * @param locator CSS или Xpath локатор элемента
     * @param value   контент для установки в элемент
     * @see #sendKeysForIdElement(String, String)
     * @see #sendKeysForElementOnExternalPage(String, String)
     */
    public void sendKeysForElement(String locator, String value) {
        getSelenideElement(locator).shouldBe(exist, duration).scrollTo().sendKeys(value);
    }

    /**
     * Устанавливает заданный контент для элемента, выполнив {@link #waitForAjaxControls()} до и после
     * @param id    id элемента
     * @param value контент для установки в элемент
     * @see #sendKeysForElement(String, String)
     */
    protected void sendKeysForIdElement(String id, String value) {
        $(By.id(id)).shouldBe(exist, duration).scrollTo().sendKeys(value);
    }

    /**
     * Устанавливает заданный контент для элемента на внешней странице
     * @param locator CSS или Xpath локатор элемента
     * @param value   контент для установки в элемент
     * @see #sendKeysForElement(String, String)
     */
    protected void sendKeysForElementOnExternalPage(String locator, String value) {
        getSelenideElement(locator).shouldBe(visible, duration).scrollTo().sendKeys(value);
    }

    /**
     * Имитирует нажатие клавиш или сочетаний клавиш внутри элемента
     * @param locator CSS или Xpath локатор элемента
     * @param keys    клавиша или сочетание клавиш
     * @see #sendKeysForElement(String, String)
     * @see #sendKeysForIdElement(String, Keys)
     */
    public void sendKeysForElement(String locator, Keys keys) {
        getSelenideElement(locator).shouldBe(visible, duration).scrollTo().sendKeys(keys);
    }

    /**
     * Имитирует нажатие клавиш или сочетаний клавиш внутри элемента
     * @param id   id элемента
     * @param keys клавиша или сочетание клавиш
     * @see #sendKeysForIdElement(String, String)
     * @see #sendKeysForElement(String, Keys)
     */
    protected void sendKeysForIdElement(String id, Keys keys) {
        $(By.id(id)).shouldBe(visible, duration).scrollTo().sendKeys(keys);
    }

    /**
     * Загружает 2 документа в одно поле, выполнив {@link #waitForAjaxControls()} до и после загрузки и
     * {@link #waitUntilSpinnerDisappear()} после загрузки
     * @param fileUploadField CSS или Xpath локатор элемента
     * @param filePath1       путь к 1 документу
     * @param filePath2       путь к 2 документу
     */
    protected void sendTwoFilesToElement(String fileUploadField, String filePath1, String filePath2) {
        getSelenideElement(fileUploadField).shouldBe(exist, duration).sendKeys(filePath1 + "\n" + filePath2);
        waitUntilSpinnerDisappear();
    }

    /**
     * Очищает поле ввода элемента
     * @param locator CSS или Xpath локатор элемента
     * @see #clearIdElementInput(String)
     */
    protected void clearElementInput(String locator) {
        WebElement element = getSelenideElement(locator);
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        if (!element.getText().isEmpty())
            throw new AssertionError("Элемент не был очищен");
    }

    /**
     * Очищает поле ввода элемента
     * @param id - id элемента
     * @see #clearElementInput(String)
     */
    protected void clearIdElementInput(String id) {
        WebElement element = driver.findElement(By.id(id));
        element.sendKeys(Keys.CONTROL + "a");
        element.sendKeys(Keys.DELETE);
        if (!element.getText().isEmpty())
            throw new AssertionError("Элемент не был очищен");
    }

    /**
     * Нажимает клавишу Enter на элементе
     * @param locator CSS или Xpath локатор элемента
     * @see #pressEnterForIdElement(String)
     */
    protected void pressEnterForElement(String locator) {
        getSelenideElement(locator).pressEnter();
    }

    /**
     * Нажимает клавишу Enter на элементе
     * @param id id элемента
     * @see #pressEnterForElement(String)
     */
    protected void pressEnterForIdElement(String id) {
        $(By.id(id)).pressEnter();
    }

    /**
     * Удаляет последний символ из поля
     * @param locator CSS или Xpath локатор элемента
     */
    protected void removeLastSymbolFromField(String locator) {
        getSelenideElement(locator).shouldBe(exist, duration).sendKeys(Keys.BACK_SPACE);
    }
    //endregion
    //region Методы взаимодействия со вкладками

    /**
     * Переключается на вкладку с указанным номером
     * @param tabNumber номер вкладки, на которую необходимо переключиться
     */
    public void switchToTab(int tabNumber) {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        if (tabs.size() >= tabNumber)
            driver.switchTo().window(tabs.get(tabNumber - 1));
        else
            throw new AssertionError("Невозможно переключиться на вкладку " + tabNumber +
                    " так как количество вкладок меньше указанного числа");
    }

    /**
     * Закрывает текущую вкладку
     */
    public void closeCurrentTab() {
        driver.close();
    }

    /**
     * Создает новую пустую вкладку в браузере с помощью JavaScript
     */
    public void createNewTab() {
        executeJavaScript("window.open()");
    }

    /**
     * Переходит по указанной ссылке
     * @param link ссылка, по которой необходимо перейти
     */
    public void gotoGivenLink(String link) {
        logger.info("Переходит по ссылке - " + link);
        driver.get(link);
    }

    /**
     * Обновляет страницу
     */
    public void refreshPage() {
        refresh();
    }
    //endregion

    /**
     * Возвращает уникальное имя
     * @param namePrefix префикс уникального имени
     * @return уникальное имя
     */
    protected String generateUniqueName(String namePrefix) {
        String dateString = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
        return namePrefix != null ? namePrefix + dateString : dateString;
    }

    /**
     * Возвращает GUID
     * @return GUID
     */
    public static String generateGUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Ожидает загрузки страницы в течение 1 минуты
     */
    protected void waitForPageLoad() {
        for (int i = 0; i < 60; i++) {
            waitSomeSeconds(1);
            if (Objects.requireNonNull(executeJavaScript("return document.readyState")).toString().equals("complete"))
                break;
        }
    }

    /**
     * Получает статус код страницы. Ожидает получение статус-кода страницы {@value #OK_STATUS_CODE} в течение 1 минуты
     * @param url адрес страницы
     * @param nTry номер повторения
     */
    public void getStatusCode(String url, int nTry) {
        int responseCode;
        try {
            responseCode = Request.Get(url).execute().returnResponse().getStatusLine()
                    .getStatusCode();
        } catch (IOException e) {
            logger.error("Ошибка при получении кода состояния страницы: " + e.getMessage());
            responseCode = 0;
        }
        if (responseCode == OK_STATUS_CODE) {
            logger.info("Сайт " + url + " доступен для работы");
            refreshPage();
        } else {
            if (nTry < MAX_COUNT_OF_ATTEMPTS) {
                logger.info("Сайт " + url + " не доступен для работы. Код состояния " + responseCode);
                waitSomeSeconds(6);
                nTry++;
                getStatusCode(url, nTry);
            } else {
                logger.info("Нет доступа к сайту " + url);
            }
        }
    }
}