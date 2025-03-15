package rangiffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import rangiffler.model.testdata.TestPhoto;
import rangiffler.page.component.AddNewPhotoForm;
import rangiffler.page.component.PhotoCardsBar;
import rangiffler.page.component.WorldMap;


import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static java.util.Objects.requireNonNull;

public class MyTravelsPage extends BasePage<MyTravelsPage> {


  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement likeButton = $x("//button[@aria-label=\"like\"]");
  private final SelenideElement withFriendsButton = $x("//button[text()='With friends']");
  private final SelenideElement likeAlert = $x("//div[text()='Post was succesfully liked']");
  private final SelenideElement addPhotoButton = $x("//button[text()='Add photo']");
  private final SelenideElement saveButton = $x("//button[text()='Save']");
  private final SelenideElement deletePostAlert = $x("//div[text()='Post deleted']");
  private final SelenideElement deletePhotoButton = $x("//button[text()='Delete']");
  private final SelenideElement imageRequiredError = $x("//div[text()='Please upload an image']");
  private final PhotoCardsBar photoCardsBar = new PhotoCardsBar($x("//div[contains(@class,'MuiGrid-root MuiGrid-container')]"));
  private final AddNewPhotoForm addNewPhotoForm = new AddNewPhotoForm($x("//form[contains(@class, 'MuiGrid-root')]"));
  private final SelenideElement worldMap = $x("//figure[contains(@class, 'worldmap__figure-container')]");

  SelenideElement likeCount = $x("//button[@aria-label='like']/preceding-sibling::p");


  @Step("Открыть главную страницу")
  public MyTravelsPage open() {
    Selenide.open("/my-travels");
    return this;
  }


  @Step("Поставить лайк")
  public MyTravelsPage clickLikeButton() {
    likeButton.click();
    return this;
  }

  @Step("Установить description")
  public MyTravelsPage setDescription(String value) {
    descriptionInput.setValue(value);
    return this;
  }

  @Step("Проверить текст удаления фотокарточки")
  public MyTravelsPage shouldDeletePostAlert(String value) {
    deletePostAlert.shouldHave(text(value));
    return this;
  }

  @Step("Нажать на кнопку удаления фотокарточки")
  public MyTravelsPage clickDeleteCardButton() {
    deletePhotoButton.click();
    return this;
  }

  @Step("Проверить количество лайков")
  public MyTravelsPage checkLikeCount(String value) {
    likeCount.shouldHave(text(value));
    return this;
  }


  @Step("Проверить текст установки лайка")
  public MyTravelsPage checkLikeAlert(String value) {
    likeAlert.shouldHave(text(value));
    return this;
  }

  @Step("Нажать на фильтр С Друзьями")
  public MyTravelsPage clickWithFriendsButton() {
    withFriendsButton.click();
    return this;
  }


  @Step("Добавить фотокарточку")
  public void addPhoto(String fileName, String countryCode, String description) {
    addPhotoButton.click();
    addNewPhotoForm.addPhoto(fileName, countryCode, description);
    Selenide.refresh();
  }

  @Step("Изменить фотокарточку")
  public MyTravelsPage editPhoto(TestPhoto photo, String newCountryCode, String newDescription) {
    var photoCard = photoCardsBar.getPhotoCard(photo);
    photoCard.editPhoto()
            .imageShouldBePresented(photo.getPhoto())
            .editPhoto(newCountryCode, newDescription);
    Selenide.refresh();
    return this;
  }

  @Step("Удалить фотокарточку")
  public MyTravelsPage deletePhoto(TestPhoto photo) {
    var photoCard = photoCardsBar.getPhotoCard(photo);
    photoCard.deletePhoto();
    return this;
  }

  @Override
  @Step("Проверить загрузку страницы")
  public MyTravelsPage checkThatPageLoaded() {
    return null;
  }

  @Nonnull
  @Step("Нажать на кнопку добавления фотокарточки")
  public MyTravelsPage clickAddPhotoButton() {
    addPhotoButton.click();
    return this;
  }

  @Nonnull
  @Step("Нажать на кнопку сохранения фотокарточки")
  public MyTravelsPage clickSavePhotoButton() {
    saveButton.click();
    return this;
  }

  @Nonnull
  @Step("Проверить текст ошибки обязательности фотокарточки")
  public MyTravelsPage checkPhotoError(String value) {
    imageRequiredError.shouldHave(text(value));
    return this;
  }


  @Nonnull
  @Step("Получить скриншот мировой карты")
  public BufferedImage worldMapScreenshot() throws IOException {
    return ImageIO.read(requireNonNull(worldMap.screenshot()));
  }

}
