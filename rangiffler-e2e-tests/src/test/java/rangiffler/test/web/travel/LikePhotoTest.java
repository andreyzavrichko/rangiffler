package rangiffler.test.web.travel;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.page.MyTravelsPage;

import static io.qameta.allure.SeverityLevel.BLOCKER;

@WebTest
@DisplayName("[WEB] Лайки")
class LikePhotoTest {

    @Inject
    private MyTravelsPage myTravelsPage;


    @ApiLogin
    @CreateUser(
            friends = @Friend(photos = @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.CHINA))
    )
    @Test
    @Story("Путешествия")
    @Feature("Проставление лайка")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("travel"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка успешной установки лайка")
    void shouldLikePhotoTest() {
        myTravelsPage.open()
                .clickWithFriendsButton()
                .clickLikeButton()
                .checkLikeAlert("Post was succesfully liked")
                .checkLikeCount("1");

    }

    @ApiLogin
    @CreateUser(
            friends = @Friend(photos = @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.CHINA))
    )
    @Test
    @Story("Путешествия")
    @Feature("Проставление лайка")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("travel"), @Tag("smoke")})
    @DisplayName("[WEB] Проверка успешного снятия лайка с фото")
    void shouldDislikePhotoTest() {
        myTravelsPage.open()
                .clickWithFriendsButton()
                .clickLikeButton()
                .checkLikeAlert("Post was succesfully liked")
                .clickLikeButton()
                .checkLikeCount("0");

    }
}
