package rangiffler.test.grpc.photo.validation;

import io.grpc.StatusRuntimeException;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.grpc.LikePhotoRequest;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.model.testdata.TestUser;
import rangiffler.test.grpc.photo.GrpcPhotoTestBase;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Photo")
public class LikePhotoValidationTest extends GrpcPhotoTestBase {


    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
            })
    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка проставление лайка с несуществующим Photo ID")
    void shouldFailWhenLikingNonExistentPhotoTest(TestUser user) {
        final LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setPhotoId("non-existent-photo-id")
                .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class,
                () -> blockingStub.likePhoto(likeRequest), "Ожидалось исключение");

        System.out.println("Ошибка: " + exception.getMessage());

        assertTrue(exception.getMessage().contains("Invalid UUID string"), "Сообщение исключения не содержит 'Invalid UUID string'");
    }
}
