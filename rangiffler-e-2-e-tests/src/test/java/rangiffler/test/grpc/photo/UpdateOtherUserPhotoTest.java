package rangiffler.test.grpc.photo;

import com.google.inject.Inject;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.repository.CountryRepository;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.grpc.UpdatePhotoRequest;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.model.testdata.TestUser;

import java.util.UUID;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[GRPC] Photo")
class UpdateOtherUserPhotoTest extends GrpcPhotoTestBase {

    @Inject
    private PhotoRepository photoRepository;

    @Inject
    private CountryRepository countryRepository;


    @CreateUser(
            friends = @Friend(
                    photos = {
                            @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.CHINA)
                    }
            )
    )
    @Test
    @Story("Обновление фотокарточки")
    @Feature("Фото")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка изменения фотокарточки другого пользователя")
    void shouldFailWhenAttemptingToUpdateOtherUserPhoto(TestUser user) {
        var country = countryRepository.findRequiredCountryByCode("ru");
        var friendPhoto = user.getFriends().getFirst().getPhotos().getFirst();

        final UpdatePhotoRequest updateRequest = UpdatePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setId(friendPhoto.getId().toString())
                .setCountryId(country.getId().toString())
                .setDescription(UUID.randomUUID().toString())
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
                () -> blockingStub.updatePhoto(updateRequest),
                "Ожидалось исключение 'PERMISSION_DENIED'");

        assertTrue(exception.getMessage().contains("PERMISSION_DENIED"),
                "Сообщение исключения не содержит 'PERMISSION_DENIED'");

        var existingPhoto = photoRepository.findRequiredPhotoById(friendPhoto.getId());

        assertAll("Проверка обновленного фото",
                () -> assertEquals(friendPhoto.getId().toString(), existingPhoto.getId().toString(), "ID фото не совпадает"),
                () -> assertEquals(friendPhoto.getDescription(), existingPhoto.getDescription(), "Описание фото не совпадает"),
                () -> assertEquals(friendPhoto.getCountry().getId().toString(), existingPhoto.getCountryId().toString(), "ID страны не совпадает")
        );
    }
}
