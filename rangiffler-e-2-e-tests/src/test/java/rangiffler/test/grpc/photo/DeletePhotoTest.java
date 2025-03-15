package rangiffler.test.grpc.photo;

import com.google.inject.Inject;
import com.google.protobuf.BoolValue;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.awaitility.Awaitility;
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

import java.time.Duration;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("[GRPC] Photo")
class DeletePhotoTest extends GrpcPhotoTestBase {

    @Inject
    private PhotoRepository photoRepository;

    @CreateUser(
            photos = @WithPhoto(countryCode = CountryCodes.CA, image = PhotoFiles.CALIFORNIA)
    )
    @Test
    @Story("Удаление своей фотокарточки")
    @Feature("Фото")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка успешного удаления фотокарточки")
    void shouldDeletePhotoTest(TestUser user) {
        final DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setPhotoId(user.getPhotos().getFirst().getId().toString())
                .build();

        BoolValue response = blockingStub.deletePhoto(request);
        assertTrue(response.getValue(), "Запрос на удаление фото должен вернуть true");

        Awaitility.await("Ожидаем удаления фото из БД")
                .atMost(Duration.ofMillis(10000))
                .pollInterval(Duration.ofMillis(1000))
                .ignoreExceptions()
                .until(() -> {
                    return photoRepository.findPhotoById(user.getPhotos().getFirst().getId()).isEmpty();
                });

        assertTrue(photoRepository.findPhotoById(user.getPhotos().getFirst().getId()).isEmpty(),
                String.format("Фото с id %s не должно быть найдено в базе данных после удаления", user.getPhotos().getFirst().getId()));
    }
}
