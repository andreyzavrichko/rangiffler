package rangiffler.test.grpc.photo;

import com.google.inject.Inject;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.Story;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import rangiffler.db.model.LikeEntity;
import rangiffler.db.model.PhotoEntity;
import rangiffler.db.repository.PhotoRepository;
import rangiffler.grpc.LikePhotoRequest;
import rangiffler.grpc.Photo;
import rangiffler.jupiter.annotation.CreateUser;
import rangiffler.jupiter.annotation.WithPhoto;
import rangiffler.model.testdata.CountryCodes;
import rangiffler.model.testdata.PhotoFiles;
import rangiffler.model.testdata.TestUser;

import java.time.Duration;
import java.util.List;

import static io.qameta.allure.SeverityLevel.BLOCKER;
import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Photo")
class LikePhotoTest extends GrpcPhotoTestBase {

    @Inject
    private PhotoRepository photoRepository;

    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
            })
    @Test
    @Story("Проставление лайка")
    @Feature("Фото")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("Проверка проставление лайка")
    void shouldLikeUserPhotoTest(TestUser user) {
        final LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setPhotoId(user.getPhotos().getFirst().getId().toString())
                .build();

        final Photo updatedPhoto = blockingStub.likePhoto(likeRequest);

        Awaitility.await("Ожидаем появления лайка")
                .atMost(Duration.ofMillis(5000))
                .pollInterval(Duration.ofMillis(1000))
                .until(() -> !photoRepository.findLikesByPhotoId(user.getPhotos().getFirst().getId()).isEmpty());

        final PhotoEntity updatedPhotoEntity = photoRepository.findByUserId(user.getId()).getFirst();
        assertAll("Photo Response Assertions",
                () -> assertEquals(updatedPhotoEntity.getId().toString(), updatedPhoto.getId(), "ID фото не совпадает"),
                () -> assertEquals(updatedPhotoEntity.getCountryId().toString(), updatedPhoto.getCountryId(), "ID страны не совпадает"),
                () -> assertEquals(updatedPhotoEntity.getDescription(), updatedPhoto.getDescription(), "Описание фото не совпадает"),
                () -> assertEquals(1, updatedPhoto.getLikes().getLikesCount(), "Ожидается 1 лайк")
        );
    }


    @CreateUser(
            photos = {
                    @WithPhoto(countryCode = CountryCodes.CN, image = PhotoFiles.FRANCE)
            })
    @Test
    @Story("Проставление лайка")
    @Feature("Фото")
    @Severity(BLOCKER)
    @Tags({@Tag("grpc"), @Tag("photo"), @Tag("smoke")})
    @DisplayName("Проверка снятия лайка")
    void shouldRemoveLikeFromPhotoTest(TestUser user) {
        final LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
                .setUserId(user.getId().toString())
                .setPhotoId(user.getPhotos().getFirst().getId().toString())
                .build();

        blockingStub.likePhoto(likeRequest);

        Awaitility.await("Ожидаем появления лайка в БД")
                .atMost(Duration.ofMillis(5000))
                .pollInterval(Duration.ofMillis(1000))
                .until(() -> !photoRepository.findLikesByPhotoId(user.getPhotos().getFirst().getId()).isEmpty());

        final Photo updatedPhoto = blockingStub.likePhoto(likeRequest);

        final PhotoEntity photoInDb = photoRepository.findByUserId(user.getId()).getFirst();
        assertAll("Disliked Photo Assertions",
                () -> assertEquals(photoInDb.getId().toString(), updatedPhoto.getId(), "ID фото не совпадает"),
                () -> assertEquals(photoInDb.getCountryId().toString(), updatedPhoto.getCountryId(), "ID страны не совпадает"),
                () -> assertEquals(photoInDb.getDescription(), updatedPhoto.getDescription(), "Описание фото не совпадает")

        );

        List<LikeEntity> likes = photoRepository.findLikesByPhotoId(user.getPhotos().getFirst().getId());
        assertTrue(likes.isEmpty(), "Лайк не был удален из БД");
    }
}
