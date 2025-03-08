package rangiffler.test.grpc.photo.validation;

import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.grpc.UpdatePhotoRequest;
import rangiffler.test.grpc.photo.GrpcPhotoTestBase;

import java.util.UUID;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Photo")
class UpdateUserPhotoValidationTest extends GrpcPhotoTestBase {


    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка обновления несуществующей фотокарточки")
    void shouldUpdateNonExistentPhotoTest() {
        var request = UpdatePhotoRequest.newBuilder()
                .setId(new UUID(0, 0).toString())
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
                () -> blockingStub.updatePhoto(request),
                "Ожидалось исключение 'NOT_FOUND'");

        assertTrue(exception.getMessage().contains("NOT_FOUND"),
                "Сообщение исключения не содержит 'NOT_FOUND'");
    }

}
