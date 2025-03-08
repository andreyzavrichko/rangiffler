package rangiffler.test.web.travel;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.repository.CountryRepository;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.jupiter.annotation.ApiLogin;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.page.MyTravelsPage;

import static io.qameta.allure.SeverityLevel.BLOCKER;


@WebTest
@DisplayName("Путешествия")
public class AddPhotoTest {
    @Inject
    private MyTravelsPage myTravelsPage;

    @Inject
    private CountryRepository countryRepository;
    @Inject
    private PhotoRepository photoRepository;


    @CreateUser
    @ApiLogin
    @Test
    @Story("Путешествия")
    @Feature("Добавление фотокарточки")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("travel"), @Tag("smoke")})
    @DisplayName("Проверка создания карточки без фотографии")
    void shouldAddCardWithoutPhotoTest() {
        myTravelsPage.open()
                .clickAddPhotoButton()
                .clickSavePhotoButton()
                .checkPhotoError("Please upload an image");
    }


    @CreateUser
    @ApiLogin
    @Test
    @Story("Путешествия")
    @Feature("Добавление фотокарточки")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("travel"), @Tag("smoke")})
    @DisplayName("Проверка успешного создания карточки с фотографией")
    void shouldSuccessAddCardTest() {
        final var country = countryRepository.findRequiredCountryByCode("us");
        myTravelsPage.open()
                .addPhoto("image/Amsterdam.png", country.getCode(), "test");
    }


}
