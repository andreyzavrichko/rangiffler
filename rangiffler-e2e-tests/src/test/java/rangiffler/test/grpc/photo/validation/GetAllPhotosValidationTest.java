package rangiffler.test.grpc.photo.validation;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.grpc.PhotoRequest;
import rangiffler.grpc.PhotoResponse;
import rangiffler.test.grpc.photo.GrpcPhotoTestBase;

import java.util.List;

import static io.qameta.allure.SeverityLevel.MINOR;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Photo")
public class GetAllPhotosValidationTest extends GrpcPhotoTestBase {

    @Inject
    private PhotoRepository photoRepository;

    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка получения фото с невалидным User ID")
    void shouldFetchPhotosWithInvalidUserIdTest() {
        final PhotoRequest request = PhotoRequest.newBuilder()
                .addAllUserIds(List.of("invalid-user-id"))
                .setPage(0)
                .setSize(10)
                .build();

        assertThrows(Exception.class, () -> blockingStub.getPhotos(request),
                "Ожидалась ошибка при запросе с неверным ID пользователя");
    }

    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка получения фото с пустым User ID")
    void shouldFetchPhotosWithEmptyRequestTest() {
        final PhotoRequest request = PhotoRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        PhotoResponse response = blockingStub.getPhotos(request);

        assertTrue(response.getPhotosList().isEmpty(), "Ответ должен быть пустым");
        assertFalse(response.getHasNext(), "Ответ не должен содержать следующей страницы");
    }


    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка получения фото с page size 10000")
    void shouldFetchPhotosWithTooLargePageSizeTest() {
        final PhotoRequest request = PhotoRequest.newBuilder()
                .addAllUserIds(List.of("valid-user-id"))
                .setPage(0)
                .setSize(10000)
                .build();

        assertThrows(Exception.class, () -> blockingStub.getPhotos(request),
                "Ожидалась ошибка при слишком большом размере страницы");
    }


    @Test
    @Story("Удаление фотокарточки")
    @Feature("Фото")
    @Severity(MINOR)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("regress")})
    @DisplayName("Проверка получения фото с несуществующим friend ID")
    void shouldFetchPhotosWithNonExistingFriendIdTest() {
        final PhotoRequest request = PhotoRequest.newBuilder()
                .addAllUserIds(List.of("valid-user-id", "non-existing-friend-id"))
                .setPage(0)
                .setSize(10)
                .build();

        assertThrows(Exception.class, () -> blockingStub.getPhotos(request),
                "Ожидалась ошибка при запросе с несуществующим ID друга");
    }
}
