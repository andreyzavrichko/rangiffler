package rangiffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import rangiffler.page.component.AddNewPhotoForm;
import rangiffler.page.component.Header;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl() + "/my-travels";

    protected final Header header = new Header();

    // Коллекция всех ячеек статистики
    private final ElementsCollection statisticCells = $$("#legend-container li");
    private final SelenideElement statisticCanvas = $("canvas[role='img']");
    private final SelenideElement addPhotoButton = $x("//button[text()='Add photo']");
    private final SelenideElement loginButton = $x("//button[text()='Login']");
    private final SelenideElement saveButton = $x("//button[text()='Save']");
    private final SelenideElement imageRequiredError = $x("//div[text()='Please upload an image']");
    private final AddNewPhotoForm addNewPhotoForm = new AddNewPhotoForm($x("//form[contains(@class, 'MuiGrid-root')]"));
    private final SelenideElement profileButton = $("[href='/profile']");
    private final SelenideElement peopleButton = $("[href='/people']");


    @Nonnull
    public Header getHeader() {
        return header;
    }

    @Nonnull
    public MainPage clickAddPhotoButton() {
        addPhotoButton.click();
        return this;
    }

    @Nonnull
    public MainPage clickProfileButton() {
        profileButton.click();
        return this;
    }

    @Nonnull
    public MainPage clickPeopleButton() {
        peopleButton.click();
        return this;
    }




    @Nonnull
    public MainPage clickSavePhotoButton() {
        saveButton.click();
        return this;
    }




    @Step("Check that page is loaded")
    @Override
    @Nonnull
    public MainPage checkThatPageLoaded() {
        Selenide.open(URL);
        loginButton.click();
        header.getSelf().should(visible);
        return this;
    }

    @Step("Check that page is loaded")
    @Nonnull
    public MainPage checkThatHeaderPageLoaded() {

        header.getSelf().should(visible);
        return this;
    }

    @Nonnull
    public MainPage checkPhotoError(String value) {
        imageRequiredError.shouldHave(text(value));
        return this;
    }

    public MainPage addPhoto(String fileName, String countryCode, String description) {
        addNewPhotoForm.addPhoto(fileName, countryCode, description);

        return this;
    }


}
