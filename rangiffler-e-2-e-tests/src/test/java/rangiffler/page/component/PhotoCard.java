package rangiffler.page.component;

import com.codeborne.selenide.SelenideElement;

import org.openqa.selenium.By;
import rangiffler.model.testdata.TestCountry;
import rangiffler.model.testdata.TestPhoto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$x;

public class PhotoCard extends BaseComponent<PhotoCard> {

  private final SelenideElement photo = self.$x(".//img");
  private final SelenideElement description = self.$x("(//p[contains(@class,'MuiTypography-root MuiTypography-body2')])[2]");
  private final SelenideElement likes = self.$x(".//p[text()=' likes']");
  private final SelenideElement country = self.$x(".//h3");
  private final SelenideElement editButton = self.$x(".//*[text() = 'Edit']");
  private final SelenideElement likeButton = self.$x(".//*[@data-testid='FavoriteBorderOutlinedIcon']");
  private final SelenideElement dislikeButton = self.$x("(.//*[@data-testid='FavoriteOutlinedIcon'])[2]");
  private final SelenideElement deleteButton = self.$x(".//button[text() = 'Delete']");

  public PhotoCard(SelenideElement self) {
    super(self);
  }

  public byte[] getPhoto() {
    return Objects.requireNonNull(photo.getAttribute("src")).getBytes(StandardCharsets.UTF_8);
  }

  public String getDescription() {
    return description.getText();
  }

  public Integer getLikesCount() {
    var likesText = likes.getText();
    return Integer.parseInt(likesText.split(" ")[0]);
  }

  public TestCountry getCountry() {
    return new TestCountry(
        null,
        null,
        country.getText(),
        country.findElement(By.xpath("img")).getAttribute("src").getBytes(StandardCharsets.UTF_8)
    );
  }

  public boolean equalWithoutLikes(TestPhoto expectedPhoto) {
    return getDescription().equals(expectedPhoto.getDescription())
        && getCountry().getName().equals(expectedPhoto.getCountry().getName())
        && Arrays.equals(getCountry().getFlag(), expectedPhoto.getCountry().getFlag())
        && Arrays.equals(getPhoto(), expectedPhoto.getPhoto());
  }

  public boolean equalWithLikes(TestPhoto expectedPhoto) {
    return equalWithoutLikes(expectedPhoto) && getLikesCount() == expectedPhoto.getLikes().size();
  }

  public TestPhoto toTestPhoto() {
    return TestPhoto.builder()
        .country(getCountry())
        .description(getDescription())
        .photo(getPhoto())
        .build();
  }

  public EditPhotoForm editPhoto() {
    editButton.click();
    return new EditPhotoForm($x("//form"));
  }

  public PhotoCard likePhoto() {
    likeButton.click();
    return this;
  }

  public PhotoCard dislikePhoto() {
    dislikeButton.click();
    return this;
  }

  public PhotoCard deletePhoto() {
    deleteButton.click();
    return this;
  }
}
