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
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.jupiter.annotation.meta.WebTest;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.model.testdata.TestUser;
import rangiffler.page.MyTravelsPage;

import java.util.UUID;

import static io.qameta.allure.SeverityLevel.BLOCKER;

@WebTest
@DisplayName("Изменение фотокарточки")
class UpdatePhotoTest {

    @Inject
    private MyTravelsPage myTravelsPage;
    @Inject
    private CountryRepository countryRepository;
    @Inject
    private PhotoRepository photoRepository;


    @ApiLogin
    @CreateUser(
            photos = @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
    )
    @Test
    @Story("Путешествия")
    @Feature("Изменение фотокарточки")
    @Severity(BLOCKER)
    @Tags({@Tag("web"), @Tag("travel"), @Tag("smoke")})
    @DisplayName("Проверка успешного изменения фото")
    void shouldUpdatePhotoTest(TestUser user) {
        final var country = countryRepository.findRequiredCountryByCode("ru");
        final var newDescription = UUID.randomUUID().toString();

        myTravelsPage.open()
                .editPhoto(user.getPhotos().getFirst(), country.getCode(), newDescription);

    }
}
