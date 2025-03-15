package rangiffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import rangiffler.page.component.AddNewPhotoForm;
import rangiffler.page.component.Header;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl() + "/my-travels";

    protected final Header header = new Header();

    private final SelenideElement loginButton = $x("//button[text()='Login']");



    @Override
    @Nonnull
    @Step("Проверить загрузку страницы")
    public MainPage checkThatPageLoaded() {
        Selenide.open(URL);
        loginButton.click();
        header.getSelf().should(visible);
        return this;
    }


    @Step("Проверить загрузку страницы")
    public void checkThatHeaderPageLoaded() {
        header.getSelf().should(visible);
    }


}
