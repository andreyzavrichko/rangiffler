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
import rangiffler.db.repository.PhotoRepository;
import rangiffler.grpc.DeletePhotoRequest;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.model.testdata.TestUser;

import java.util.UUID;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Photo")
class DeleteOtherUserPhotoTest extends GrpcPhotoTestBase {


    @Inject
    private PhotoRepository photoRepository;

    @CreateUser(
            friends = @Friend(
                    photos = {
                            @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
                    }
            )
    )
    @Test
    @Story("Удаление чужой фотокарточки")
    @Feature("Фото")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("Проверка удаления фотокарточки друга")
    void shouldUserFromDeletingFriendPhotoTest(TestUser user) {
        DeletePhotoRequest deleteRequest = DeletePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setPhotoId(user.getFriends().getFirst().getPhotos().getFirst().getId().toString())
                .build();

        StatusRuntimeException exception = assertThrows(io.grpc.StatusRuntimeException.class,
                () -> blockingStub.deletePhoto(deleteRequest), "Ожидалось исключение PERMISSION_DENIED");

        assertTrue(exception.getMessage().contains("PERMISSION_DENIED"), "Сообщение исключения не содержит 'PERMISSION_DENIED'");
        assertTrue(exception.getMessage().contains(user.getFriends().getFirst().getPhotos().getFirst().getId().toString()),
                "Сообщение исключения не содержит ID фото");

        var photo = photoRepository.findPhotoById(UUID.fromString(deleteRequest.getPhotoId()));
        assertTrue(photo.isPresent(), "Фото не было удалено, как ожидалось");
    }
}
