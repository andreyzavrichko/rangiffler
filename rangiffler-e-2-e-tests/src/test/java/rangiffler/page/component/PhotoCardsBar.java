package rangiffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import rangiffler.model.testdata.TestPhoto;


import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$$x;
import static rangiffler.condition.PhotoCollectionCondition.containsPhoto;
import static rangiffler.condition.PhotoCollectionCondition.photosExactly;
import static rangiffler.condition.PhotoCondition.exactlyPhoto;
import static rangiffler.condition.PhotoCondition.photoWithoutLikes;


public class PhotoCardsBar extends BaseComponent<PhotoCardsBar> {

  private final ElementsCollection photoCards = $$x("//div[contains(@class, 'MuiPaper-rounded')]");

  public PhotoCardsBar(SelenideElement self) {
    super(self);
  }

  public PhotoCardsBar exactlyPhotoCardsShouldBePresented(TestPhoto... expectedPhotos) {
    photoCards.shouldHave(photosExactly(expectedPhotos));
    return this;
  }

  public PhotoCardsBar photoCardsShouldBePresented(TestPhoto... expectedPhotos) {
    for (var expectedPhoto : expectedPhotos) {
      photoCards.should(containsPhoto(expectedPhoto));
    }
    return this;
  }

  public PhotoCardsBar photosCountShouldBeEqualTo(int expectedCount) {
    photoCards.shouldHave(size(expectedCount));
    return this;
  }

  public PhotoCard getPhotoCard(TestPhoto photo) {
    return new PhotoCard(photoCards.filter(exactlyPhoto(photo)).get(0));
  }

  public PhotoCard getPhotoWithoutLikesCard(TestPhoto photo) {
    return new PhotoCard(photoCards.filter(photoWithoutLikes(photo)).get(0));
  }
}
