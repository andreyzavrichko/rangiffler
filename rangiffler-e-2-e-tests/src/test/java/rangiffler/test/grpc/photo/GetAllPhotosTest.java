package rangiffler.test.grpc.photo;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.model.PhotoEntity;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.grpc.PhotoRequest;
import rangiffler.grpc.PhotoResponse;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.Friend;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.model.testdata.TestUser;

import java.util.List;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("[GRPC] Photo")
class GetAllPhotosTest extends GrpcPhotoTestBase {

    @Inject
    private PhotoRepository photoRepository;

    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.CHINA, likes = 1)
            })
    @Test
    @Story("Получение списка фотокарточек")
    @Feature("Фото")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка получения всех фотокарточек пользователя")
    void shouldFetchUserPhotosTest(TestUser user) {
        final PhotoRequest request = PhotoRequest.newBuilder()
                .addAllUserIds(List.of(user.getId().toString()))
                .setPage(0)
                .setSize(10)
                .build();

        final PhotoResponse response = blockingStub.getPhotos(request);

        assertEquals(1, response.getPhotosList().size(), "Размер страницы должен быть равен 1");
        assertFalse(response.getHasNext(), "Ответ не должен содержать следующей страницы");

        final PhotoEntity expectedPhoto = photoRepository.findByUserId(user.getId()).getFirst();
        assertEquals(expectedPhoto.getId().toString(), response.getPhotosList().getFirst().getId(),
                "ID фото не совпадает");
        assertEquals(expectedPhoto.getCountryId().toString(), response.getPhotosList().getFirst().getCountryId(),
                "ID страны не совпадает");
        assertEquals(expectedPhoto.getDescription(), response.getPhotosList().getFirst().getDescription(),
                "Описание фото не совпадает");

    }

    @CreateUser(
            friends = {
                    @Friend(
                            photos = {
                                    @WithPhoto(countryCode = CountryCodes.MX, image = PhotoFiles.CHINA, description = "insertedDescriptionFriend")
                            }),
                    @Friend(
                            photos = {
                                    @WithPhoto(countryCode = CountryCodes.CA, image = PhotoFiles.CALIFORNIA, description = "insertedDescriptionFriend2")
                            })
            },
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.CHINA)
            })
    @Test
    @Story("Получение списка фотокарточек")
    @Feature("Фото")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("[GRPC] Проверка получения всех фотокарточек друзей пользователя")
    void shouldFetchPhotosWithFriendsTest(TestUser user) {
        final PhotoRequest request = PhotoRequest.newBuilder()
                .addAllUserIds(List.of(
                        user.getId().toString(),
                        user.getFriends().get(0).getId().toString(),
                        user.getFriends().get(1).getId().toString())
                )
                .setPage(0)
                .setSize(10)
                .build();

        final PhotoResponse response = blockingStub.getPhotos(request);

        assertEquals(3, response.getPhotosList().size(), "Размер страницы должен быть равен 3");
        assertFalse(response.getHasNext(), "Ответ не должен содержать следующей страницы");
    }
}
