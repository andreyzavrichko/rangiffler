package rangiffler.test.grpc.photo.validation;


import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import io.grpc.Status;
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
import rangiffler.grpc.CreatePhotoRequest;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.model.testdata.TestUser;
import rangiffler.test.grpc.photo.GrpcPhotoTestBase;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static rangiffler.utils.RandomDataUtils.randomName;


@DisplayName("[GRPC] Photo")
class CreatePhotoValidationTest extends GrpcPhotoTestBase {

    @Inject
    private PhotoRepository photoRepository;
    @Inject
    private CountryRepository countryRepository;


    @Test
    @Story("Создание фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка создания фотокарточки с пустым userId")
    void shouldCreatePhotoWithEmptyIdTest() {
        var country = countryRepository.findRequiredCountryByCode("ru");
        final String description = randomName();
        final CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                .setUserId("") // Пустой userId
                .setSrc(ByteString.EMPTY)
                .setCountryId(country.getId().toString())
                .setDescription(description)
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.createPhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Expected ABORTED error due to empty fields");
    }


    @Test
    @Story("Создание фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка создания фотокарточки с пробелом в userId")
    void shouldCreatePhotoWithSpaceIdTest() {
        var country = countryRepository.findRequiredCountryByCode("ru");
        final String description = randomName();
        final CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                .setUserId(" ")
                .setSrc(ByteString.EMPTY)
                .setCountryId(country.getId().toString())
                .setDescription(description)
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.createPhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Expected ABORTED error due to empty fields");
    }

    @CreateUser
    @Test
    @Story("Создание фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка создания фотокарточки с пустым CountryId")
    void shouldCreatePhotoWithEmptyCountryIdTest(TestUser user) {
        final String description = randomName();
        final CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setSrc(ByteString.EMPTY)
                .setCountryId("")
                .setDescription(description)
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.createPhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Expected ABORTED error due to empty fields");
    }

    @CreateUser
    @Test
    @Story("Создание фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("[GRPC] Проверка создания фотокарточки с пробелом в CountryId")
    void shouldCreatePhotoWithSpaceCountryIdTest(TestUser user) {
        final String description = randomName();
        final CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setSrc(ByteString.EMPTY)
                .setCountryId(" ")
                .setDescription(description)
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.createPhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Expected ABORTED error due to empty fields");
    }


}
