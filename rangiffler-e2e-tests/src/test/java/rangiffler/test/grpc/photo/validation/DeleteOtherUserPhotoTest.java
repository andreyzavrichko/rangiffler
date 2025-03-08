package rangiffler.test.grpc.photo.validation;

import com.google.inject.Inject;
import com.google.protobuf.BoolValue;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.grpc.DeletePhotoRequest;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.model.testdata.TestUser;
import rangiffler.test.grpc.photo.GrpcPhotoTestBase;

import java.util.UUID;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Photo")
public class DeleteOtherUserPhotoTest extends GrpcPhotoTestBase {
    @Inject
    private PhotoRepository photoRepository;

    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
            }
    )
    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка удаления фото с некорректным userId")
    void shouldFailWhenUserIdIsInvalidTest(TestUser user) {
        DeletePhotoRequest deleteRequest = DeletePhotoRequest.newBuilder()
                .setUserId("invalid-user-id")  // Некорректный ID
                .setPhotoId(user.getPhotos().getFirst().getId().toString())
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
                () -> blockingStub.deletePhoto(deleteRequest), "Ожидалось исключение ABORTED");

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(),
                "Статус исключения не равен 'ABORTED'");
    }


    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
            }
    )
    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка удаления фото с некорректным PhotoId")
    void shouldFailWhenPhotoIdIsInvalidTest(TestUser user) {
        DeletePhotoRequest deleteRequest = DeletePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setPhotoId("invalid-photo-id")  // Некорректный ID фото
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
                () -> blockingStub.deletePhoto(deleteRequest), "Ожидалось исключение ABORTED");

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(),
                "Статус исключения не равен 'ABORTED'");
    }


    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
            }
    )
    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка удаления фото")
    void shouldDeletePhotoSuccessfullyTest(TestUser user) {
        DeletePhotoRequest deleteRequest = DeletePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setPhotoId(user.getPhotos().getFirst().getId().toString())
                .build();

        BoolValue deleteResponse = blockingStub.deletePhoto(deleteRequest);

        assertNotNull(deleteResponse, "Ответ не должен быть null");
        assertTrue(deleteResponse.getValue(), "Фото не было удалено");

        var photo = photoRepository.findPhotoById(UUID.fromString(deleteRequest.getPhotoId()));
        assertFalse(photo.isPresent(), "Фото не было удалено из базы данных");
    }
}
