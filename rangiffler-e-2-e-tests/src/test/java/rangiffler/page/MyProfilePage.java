package rangiffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.nio.charset.StandardCharsets;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MyProfilePage extends BasePage<MyProfilePage> {

  private final SelenideElement pageHeader = $x("//h2[text()='My profile']");
  private final SelenideElement firstnameInput = $x("//input[@id='firstname']");
  private final SelenideElement surnameInput = $x("//input[@id='surname']");
  private final SelenideElement usernameInput = $x("//input[@id='username']");
  private final SelenideElement locationInput = $x("//div[@id='location']");
  private final SelenideElement avatar = $x("//div[contains(@class, 'MuiAvatar-root')]");
  private final SelenideElement saveButton = $x("//button[@type='submit']");
  private final SelenideElement avatarImageInput = $x("//input[@id='image__input']");
  private final SelenideElement successTitle = $x("//div[contains(text(), 'Your profile is successfully updated')]");
  private final SelenideElement errorFirstNameTitle = $("#firstname-helper-text");
  private final SelenideElement errorLastNameTitle = $("#surname-helper-text");

  @Step("Открыть страницу Профиля")
  public MyProfilePage open() {
    Selenide.open("/profile");
    return this;
  }

  @Step("Проверить загрузку страницы")
  public MyProfilePage pageHeaderShouldBeVisible() {
    pageHeader.shouldBe(visible);
    return this;
  }

  @Step("Проверить видимость firstname")
  public MyProfilePage firstnameShouldBe(String expectedFirstname) {
    firstnameInput.shouldHave(value(expectedFirstname));
    return this;
  }

  @Step("Проверить видимость lastname")
  public MyProfilePage lastnameShouldBe(String expectedLastname) {
    surnameInput.shouldHave(value(expectedLastname));
    return this;
  }

  @Step("Проверить видимость username")
  public MyProfilePage usernameShouldBe(String expectedUsername) {
    usernameInput.shouldHave(value(expectedUsername));
    return this;
  }

  @Step("Проверить видимость location")
  public MyProfilePage locationNameShouldBe(String expectedLocationName) {
    locationInput.shouldHave(text(expectedLocationName));
    return this;
  }

  @Step("Проверить видимость flag")
  public MyProfilePage locationFlagShouldBe(byte[] expectedLocationFlag) {
    locationInput.find(By.xpath("img"))
        .shouldHave(attribute("src", new String(expectedLocationFlag, StandardCharsets.UTF_8)));
    return this;
  }

  @Step("Проверить видимость avatar")
  public MyProfilePage avatarShouldBe(byte[] expectedAvatar) {
    avatar.find(By.xpath("img"))
        .shouldHave(attribute("src", new String(expectedAvatar, StandardCharsets.UTF_8)));
    return this;
  }

  @Step("Установить firstName")
  public MyProfilePage setFirstname(String firstname) {
    firstnameInput.setValue(firstname);
    return this;
  }

  @Step("Установить lastName")
  public MyProfilePage setLastname(String lastname) {
    surnameInput.setValue(lastname);
    return this;
  }

  @Step("Установить location")
  public MyProfilePage setLocation(String locationCode) {
    locationInput.click();
    $x("//li[@data-value='" + locationCode + "']").click();
    return this;
  }

  @Step("Установить avatar")
  public MyProfilePage setAvatar(String fileName) {
    avatarImageInput.uploadFromClasspath(fileName);
    return this;
  }

  @Step("Сохранить изменения")
  public MyProfilePage saveChanges() {
    saveButton.click();
    return this;
  }


  @Override
  @Step("Проверить загрузку страницы")
  public MyProfilePage checkThatPageLoaded() {
    return null;
  }

  @Step("Проверить title обновления профиля")
  public MyProfilePage checkSuccessTitle(String value) {
    successTitle.shouldHave(text(value));
    return this;
  }

  @Step("Проверить ошибку firstname")
  public void checkErrorFirstnameTitle(String value) {
    errorFirstNameTitle.shouldHave(text(value));
  }

  @Step("Проверить ошибку lastname")
  public void checkErrorLastnameTitle(String value) {
    errorLastNameTitle.shouldHave(text(value));
  }
}
