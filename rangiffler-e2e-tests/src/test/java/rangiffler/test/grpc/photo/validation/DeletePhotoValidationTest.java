package rangiffler.test.grpc.photo.validation;

import com.google.inject.Inject;
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
import rangiffler.test.grpc.photo.GrpcPhotoTestBase;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Photo")
class DeletePhotoValidationTest extends GrpcPhotoTestBase {

    @Inject
    private PhotoRepository photoRepository;


    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка удаления фото с пустым userId")
    void shouldDeletePhotoWithEmptyUserIdTest() {
        final DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .setUserId("")
                .setPhotoId("valid-photo-id")
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.deletePhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Ожидается ошибка ABORTED из-за пустого userId");
    }


    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка удаления фото с пустым photoId")
    void shouldDeletePhotoWithEmptyPhotoIdTest() {
        final DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .setUserId("valid-user-id")
                .setPhotoId("")
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.deletePhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Ожидается ошибка ABORTED из-за пустого photoId");
    }

    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка удаления фото с несуществующим userId")
    void shouldDeletePhotoWithNonExistentUserIdTest() {
        final DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .setUserId("non-existent-user-id")
                .setPhotoId("valid-photo-id")
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.deletePhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Ожидается ошибка ABORTED из-за несуществующего userId");
    }

    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка удаления фото с несуществующим photoId")
    void shouldDeletePhotoWithNonExistentPhotoIdTest() {
        final DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .setUserId("valid-user-id")
                .setPhotoId("non-existent-photo-id")
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.deletePhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Ожидается ошибка ABORTED из-за несуществующего photoId");
    }

    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка удаления фото без указания userId и photoId")
    void shouldDeletePhotoWithoutUserIdAndPhotoIdTest() {
        final DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            blockingStub.deletePhoto(request);
        });

        assertEquals(Status.Code.ABORTED, exception.getStatus().getCode(), "Ожидается ошибка ABORTED из-за отсутствия обязательных полей");
    }


}
