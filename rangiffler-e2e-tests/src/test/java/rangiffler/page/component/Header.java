package rangiffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import io.qameta.allure.Step;
import rangiffler.page.LoginPage;
import rangiffler.page.MainPage;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    public Header() {
        super($("#root header"));
    }

    private final SelenideElement mainPageLink = self.$("a[href*='/main']");
    private final SelenideElement addSpendingBtn = self.$("a[href*='/spending']");
    private final SelenideElement menuBtn = self.$("button");
    private final SelenideElement menu = $("ul[role='menu']");
    private final ElementsCollection menuItems = menu.$$("li");





    @Step("Sign out")
    @Nonnull
    public LoginPage signOut() {
        menuBtn.click();
        menuItems.find(text("Sign out")).click();
        return new LoginPage();
    }



    @Step("Go to main page")
    @Nonnull
    public MainPage toMainPage() {
        mainPageLink.click();
        return new MainPage();
    }
}
