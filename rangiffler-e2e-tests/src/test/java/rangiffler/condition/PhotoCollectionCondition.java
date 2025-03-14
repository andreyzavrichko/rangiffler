package rangiffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.impl.CollectionSource;
import org.openqa.selenium.WebElement;
import rangiffler.model.testdata.TestPhoto;
import rangiffler.page.component.PhotoCard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;

public class PhotoCollectionCondition {

  public static WebElementsCondition photosExactly(TestPhoto... expectedPhotos) {
    return new WebElementsCondition() {

      @Nonnull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (elements.size() != expectedPhotos.length) {
          return CheckResult.rejected("Incorrect table size", elements);
        }

        boolean isCheckSuccess = true;
        List<TestPhoto> actualPhotos = new ArrayList<>();

        for (var i = 0; i < elements.size(); i++) {
          final var photoCard = new PhotoCard($(elements.get(i)));
          actualPhotos.add(photoCard.toTestPhoto());

          var isUserTableDataMath = photoCard.equalWithLikes(expectedPhotos[i]);
          if (!isUserTableDataMath) {
            isCheckSuccess = false;
          }
        }

        if (isCheckSuccess) {
          return CheckResult.accepted();
        } else {
          var errorMsg = String.format("Incorrect photo content. Expected photo list: %s, actual photo list: %s",
              Arrays.toString(expectedPhotos), actualPhotos);
          return CheckResult.rejected(errorMsg, elements);
        }
      }

      @Override
      public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause,
          long timeoutMs) {
        throw new AssertionError(lastCheckResult.message());
      }

      @Override
      public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
      }
    };
  }

  public static WebElementsCondition containsPhoto(TestPhoto expectedPhoto) {
    return new WebElementsCondition() {

      @Nonnull
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        List<TestPhoto> actualPhotos = new ArrayList<>();

        for (var element : elements) {
          final var photoCard = new PhotoCard($(element));
          actualPhotos.add(photoCard.toTestPhoto());

          var isUserTableDataMath = photoCard.equalWithLikes(expectedPhoto);
          if (isUserTableDataMath) {
            return CheckResult.accepted();
          }
        }

        var errorMsg = String.format("Incorrect photo content. Expected photo: %s, actual photo list: %s",
            expectedPhoto, actualPhotos);
        return CheckResult.rejected(errorMsg, elements);
      }

      @Override
      public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause,
          long timeoutMs) {
        throw new AssertionError(lastCheckResult.message());
      }

      @Override
      public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
      }
    };
  }
}
