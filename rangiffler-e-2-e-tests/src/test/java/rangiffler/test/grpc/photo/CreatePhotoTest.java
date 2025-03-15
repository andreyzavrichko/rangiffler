package rangiffler.test.grpc.photo;


import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import rangiffler.db.model.PhotoEntity;
import rangiffler.db.repository.CountryRepository;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.grpc.CreatePhotoRequest;
import rangiffler.grpc.Photo;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.model.testdata.TestUser;

import java.time.Duration;
import java.util.UUID;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("[GRPC] Photo")
class CreatePhotoTest extends GrpcPhotoTestBase {

    @Inject
    private PhotoRepository photoRepository;
    @Inject
    private CountryRepository countryRepository;
    private Photo response;


    @CreateUser
    @Test
    @Story("Добавление фотокарточки")
    @Feature("Фото")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка добавления фотокарточки")
    void shouldCreatePhotoTest(TestUser user) {
        var country = countryRepository.findRequiredCountryByCode("ru");
        final String description = UUID.randomUUID().toString();
        final CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setSrc(ByteString.EMPTY)
                .setCountryId(country.getId().toString())
                .setDescription(description)
                .build();

        response = blockingStub.createPhoto(request);

        final PhotoEntity expectedPhoto = Awaitility.await("Ожидаем появления созданного фото в БД")
                .atMost(Duration.ofMillis(5000))
                .pollInterval(Duration.ofMillis(1000))
                .ignoreExceptions()
                .until(
                        () -> photoRepository.findByUserId(user.getId()),
                        photoEntities -> !photoEntities.isEmpty()
                ).getFirst();

        assertNotNull(expectedPhoto, "Фото не найдено в БД");
        assertEquals(user.getId().toString(), expectedPhoto.getUserId().toString(), "ID пользователя не совпадает");
        assertEquals(country.getId().toString(), expectedPhoto.getCountryId().toString(), "ID страны не совпадает");
        assertEquals(description, expectedPhoto.getDescription(), "Описание не совпадает");

    }

}
