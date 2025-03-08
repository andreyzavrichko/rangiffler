package rangiffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import rangiffler.model.testdata.TestPhoto;
import rangiffler.page.component.AddNewPhotoForm;
import rangiffler.page.component.PhotoCardsBar;
import rangiffler.page.component.WorldMap;


import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MyTravelsPage extends BasePage<MyTravelsPage> {

  private final SelenideElement travelsMapHeader = $x("//h2[text() = 'Travels map']");
  private final SelenideElement onlyMyTravelsButton = $x("//button[text()='Only my travels']");
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement likeButton = $x("//button[@aria-label=\"like\"]");
  private final SelenideElement withFriendsButton = $x("//button[text()='With friends']");
  private final SelenideElement likeAlert = $x("//div[text()='Post was succesfully liked']");
  private final WorldMap worldMap = new WorldMap($x("//figure[@class='worldmap__figure-container']"));
  private final SelenideElement addPhotoButton = $x("//button[text()='Add photo']");
  private final SelenideElement editPhotoButton = $x("//button[text()='Edit']");
  private final SelenideElement saveButton = $x("//button[text()='Save']");
  private final SelenideElement deletePostAlert = $x("//div[text()='Post deleted']");
  private final SelenideElement deletePhotoButton = $x("//button[text()='Delete']");
  private final SelenideElement imageRequiredError = $x("//div[text()='Please upload an image']");
  private final SelenideElement updatePhotoButton = $x("//div[text()='Post updated']");
  private final PhotoCardsBar photoCardsBar = new PhotoCardsBar($x("//div[contains(@class,'MuiGrid-root MuiGrid-container')]"));
  private final AddNewPhotoForm addNewPhotoForm = new AddNewPhotoForm($x("//form[contains(@class, 'MuiBox-root')]"));
  SelenideElement likeCount = $x("//button[@aria-label='like']/preceding-sibling::p");



  public MyTravelsPage open() {
    Selenide.open("/my-travels");
    return this;
  }

  public MyTravelsPage travelsMapHeaderShouldBeVisible() {
    travelsMapHeader.shouldBe(visible);
    return this;
  }

  public MyTravelsPage clickLikeButton() {
    likeButton.click();
    return this;
  }


  public MyTravelsPage clickSaveButton() {
    saveButton.click();
    return this;
  }

  public MyTravelsPage setDescription(String value) {
    descriptionInput.setValue(value);
    return this;
  }


  public MyTravelsPage shouldUpdatePhotoButton(String value) {
    updatePhotoButton.shouldHave(text(value));
    return this;
  }

  public MyTravelsPage shouldDeletePostAlert(String value) {
    deletePostAlert.shouldHave(text(value));
    return this;
  }

  public MyTravelsPage clickDeleteCardButton() {
    deletePhotoButton.click();
    return this;
  }

  public MyTravelsPage checkLikeCount(String value) {
    likeCount.shouldHave(text(value));
    return this;
  }



  public MyTravelsPage checkLikeAlert(String value) {
    likeAlert.shouldHave(text(value));
    return this;
  }

  public MyTravelsPage clickWithFriendsButton() {
    withFriendsButton.click();
    return this;
  }

  public MyTravelsPage clickEditPhotoButton() {
    editPhotoButton.click();
    return this;
  }

  public MyTravelsPage exactlyPhotoCardsShouldBePresented(TestPhoto... expectedPhotos) {
    photoCardsBar.exactlyPhotoCardsShouldBePresented(expectedPhotos);
    return this;
  }

  public MyTravelsPage photoCardsShouldBePresented(TestPhoto... expectedPhotos) {
    photoCardsBar.photoCardsShouldBePresented(expectedPhotos);
    return this;
  }

  public MyTravelsPage photosCountShouldBeEqualTo(int expectedCount) {
    photoCardsBar.photosCountShouldBeEqualTo(expectedCount);
    return this;
  }

  public MyTravelsPage statisticShouldBePresented(String countryCode, String countryName, int expectedCount) {
    worldMap.statisticShouldBePresented(countryCode, countryName, expectedCount);
    return this;
  }

  public void addPhoto(String fileName, String countryCode, String description) {
    addPhotoButton.click();
    addNewPhotoForm.addPhoto(fileName, countryCode, description);
    Selenide.refresh();
  }

  public MyTravelsPage editPhoto(TestPhoto photo, String newCountryCode, String newDescription) {
    var photoCard = photoCardsBar.getPhotoCard(photo);
    photoCard.editPhoto()
            .imageShouldBePresented(photo.getPhoto())
            .editPhoto(newCountryCode, newDescription);
    Selenide.refresh();
    return this;
  }

  public MyTravelsPage likePhoto(TestPhoto photo) {
    var photoCard = photoCardsBar.getPhotoCard(photo);
    photoCard.likePhoto();
    return this;
  }

  public MyTravelsPage dislikePhoto(TestPhoto photo) {
    var photoCard = photoCardsBar.getPhotoWithoutLikesCard(photo);
    photoCard.dislikePhoto();
    return this;
  }

  public MyTravelsPage deletePhoto(TestPhoto photo) {
    var photoCard = photoCardsBar.getPhotoCard(photo);
    photoCard.deletePhoto();
    return this;
  }

  @Override
  public MyTravelsPage checkThatPageLoaded() {
    return null;
  }

  @Nonnull
  public MyTravelsPage clickAddPhotoButton() {
    addPhotoButton.click();
    return this;
  }

  @Nonnull
  public MyTravelsPage clickSavePhotoButton() {
    saveButton.click();
    return this;
  }

  @Nonnull
  public MyTravelsPage checkPhotoError(String value) {
    imageRequiredError.shouldHave(text(value));
    return this;
  }

}
