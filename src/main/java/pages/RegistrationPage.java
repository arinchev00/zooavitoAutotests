package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class RegistrationPage {

    private final String FORM_TITLE = "Student Registration Form";
    private SelenideElement
            formTitle = $(".practice-form-wrapper"),
            firstNameInput = $("#firstName"),
            lastNameInput = $("#lastName"),
            userEmailInput = $("#userEmail"),
            genderInput = $("label[for='gender-radio-1']"),
            numberInput = $("#userNumber"),
            calendarClick = $("#dateOfBirthInput"),
            monthCheck = $(".react-datepicker__month-select"),
            yearCheck = $(".react-datepicker__year-select"),
            hobbyesChechBox = $("label[for='hobbies-checkbox-1']"),
            subjectsInput = $("#subjectsInput"),
            uploadFile = $("#uploadPicture"),
            currentAdressInput = $("#currentAddress"),
            state = $("#state"),
            city = $("#city"),
            stateInput = $("#stateCity-wrapper"),
            bottom = $("[id=submit]");

    public RegistrationPage openPage(String value) {
        open(value);
        formTitle.shouldHave(text(FORM_TITLE));
        return this;
    }

    public RegistrationPage typeFirstName(String value) {
        firstNameInput.setValue(value);
        return this;
    }

    public RegistrationPage typeLastName(String value) {
        lastNameInput.setValue(value);
        return this;
    }

    public RegistrationPage typeUserEmail(String value) {
        userEmailInput.setValue(value);
        return this;
    }

    public RegistrationPage typeGender() {
        genderInput.click();
        return this;
    }

    public RegistrationPage typeNumber(String value) {
        numberInput.setValue(value);
        return this;
    }

    public RegistrationPage typeCalendar(String day, String month, String year) {
        calendarClick.click();
        monthCheck.selectOption(month);
        yearCheck.selectOption(year);
        $(".react-datepicker__day--0" + day +
                ":not(.react-datepicker__day--outside-month)").click();
        return this;
    }

    public RegistrationPage typeHobbies() {
        hobbyesChechBox.click();
        return this;
    }

    public RegistrationPage typeSubjects(String value) {
        subjectsInput.setValue(value).pressEnter();
        return this;
    }

    public RegistrationPage typeFile(String file) {
        uploadFile.uploadFromClasspath(file);
        return this;
    }

    public RegistrationPage typeCurrentAress(String value) {
        currentAdressInput.setValue(value);
        return this;
    }

    public RegistrationPage typeStateCity(String value) {
        state.scrollIntoView(true).click();
        stateInput.$(byText(value)).click();
        return this;
    }

    public RegistrationPage typeCity(String value) {
        city.click();
        stateInput.$(byText(value)).click();
        return this;
    }

    public RegistrationPage clickBottom() {
        bottom.click();
        return this;
    }

    public RegistrationPage checkResultsValueStudentName(String key, String value) {
        $x("//td[text()='" + key + "']").parent()
                .shouldHave(text(value));
        return this;
    }

    public RegistrationPage checkResultsValueOther(String key, String value) {
        $x("//td[text()='" + key + "']")
                .closest("tr")
                .find("td:nth-child(2)")
                .shouldHave(text(value));
        return this;
    }
}
