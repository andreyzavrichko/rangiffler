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
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.page.MyTravelsPage;

import static io.qameta.allure.SeverityLevel.BLOCKER;

@WebTest
@DisplayName("Удаление фотокарточки")
class DeletePhotoTest {

    @Inject
    private MyTravelsPage myTravelsPage;


    @ApiLogin
    @CreateUser(
            photos = @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
    )
    @Test
    @Story("Путешествия")
    @Feature("Удаление фотокарточки")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("travel"), @Tag("smoke")})
    @DisplayName("Проверка удаления фотокарточки")
    void shouldDeletePhotoCardTest() {
        myTravelsPage.open()
                .clickDeleteCardButton()
                .shouldDeletePostAlert("Post deleted");
    }
}
