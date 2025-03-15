package rangiffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.openqa.selenium.WebElement;
import rangiffler.model.testdata.TestPhoto;
import rangiffler.page.component.PhotoCard;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public class PhotoCondition {

  public static WebElementCondition exactlyPhoto(TestPhoto expectedPhoto) {
    return new WebElementCondition("Photo with likes condition") {

      @Nonnull
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        final var photoCard = new PhotoCard($(element));
        return new CheckResult(photoCard.equalWithLikes(expectedPhoto), photoCard.toTestPhoto());
      }
    };
  }

  public static WebElementCondition photoWithoutLikes(TestPhoto expectedPhoto) {
    return new WebElementCondition("Photo without likes condition") {

      @Nonnull
      @Override
      public CheckResult check(Driver driver, WebElement element) {
        final var photoCard = new PhotoCard($(element));
        return new CheckResult(photoCard.equalWithoutLikes(expectedPhoto), photoCard.toTestPhoto());
      }
    };
  }
}
