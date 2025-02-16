package rangiffler.service;

import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import rangiffler.data.PhotoEntity;
import rangiffler.data.StatisticEntity;
import rangiffler.data.repository.LikeRepository;
import rangiffler.data.repository.PhotoRepository;
import rangiffler.data.repository.StatisticRepository;
import rangiffler.exception.IllegalPhotoAccessException;
import rangiffler.grpc.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private StatisticRepository statisticRepository;
    @Mock
    private StreamObserver<Photo> photoObserver;
    @Mock
    private StreamObserver<BoolValue> boolObserver;

    @InjectMocks
    private PhotoService photoService;

    private final UUID userId = UUID.randomUUID();
    private final UUID photoId = UUID.randomUUID();
    private final UUID countryId = UUID.randomUUID();

    @Test
    void getPhotosShouldReturnPaginatedResults() {
        // Arrange
        PhotoRequest request = PhotoRequest.newBuilder()
                .addUserIds(userId.toString())
                .setPage(0)
                .setSize(10)
                .build();

        PhotoEntity photoEntity = new PhotoEntity();
        photoEntity.setId(photoId); // Устанавливаем ID
        photoEntity.setUserId(userId);
        photoEntity.setCountryId(countryId);
        photoEntity.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        Page<PhotoEntity> page = new PageImpl<>(List.of(photoEntity), PageRequest.of(0, 10), 1);
        when(photoRepository.findByUserIdIn(anyList(), any(Pageable.class))).thenReturn(page);

        // Act
        photoService.getPhotos(request, mock(StreamObserver.class));

        // Assert
        verify(photoRepository).findByUserIdIn(List.of(userId), PageRequest.of(0, 10));
    }

    @Test
    void createPhotoShouldSaveNewEntityAndUpdateStats() {
        // Arrange
        CreatePhotoRequest request = CreatePhotoRequest.newBuilder()
                .setUserId(userId.toString())
                .setCountryId(countryId.toString())
                .build();

        ArgumentCaptor<PhotoEntity> photoCaptor = ArgumentCaptor.forClass(PhotoEntity.class);

        when(photoRepository.save(any())).thenAnswer(inv -> {
            PhotoEntity entity = inv.getArgument(0);
            entity.setId(UUID.randomUUID()); // Устанавливаем ID вручную
            return entity;
        });

        // Act
        photoService.createPhoto(request, photoObserver);

        // Assert
        verify(photoRepository).save(photoCaptor.capture());
        verify(statisticRepository).findByUserIdAndCountryId(userId, countryId);

        PhotoEntity savedPhoto = photoCaptor.getValue();
        assertEquals(userId, savedPhoto.getUserId());
        assertEquals(countryId, savedPhoto.getCountryId());
        assertNotNull(savedPhoto.getId(), "ID фото должен быть установлен");
    }


    @Test
    void updatePhotoShouldThrowWhenUserNotOwner() {
        // Arrange
        UUID randomUserId = UUID.randomUUID(); // Пользователь, не являющийся владельцем
        UpdatePhotoRequest request = UpdatePhotoRequest.newBuilder()
                .setId(photoId.toString())
                .setUserId(randomUserId.toString())
                .build();

        PhotoEntity photo = new PhotoEntity();
        photo.setId(photoId); // Устанавливаем ID, чтобы избежать NPE
        photo.setUserId(userId); // Оригинальный владелец фото

        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        // Act & Assert
        IllegalPhotoAccessException thrown = assertThrows(IllegalPhotoAccessException.class, () ->
                photoService.updatePhoto(request, photoObserver));

        assertEquals("Фото с идентификатором " + photoId + " не может быть модифицирован пользователем " + randomUserId, thrown.getMessage()); // Проверка сообщения ошибки
    }


    @Test
    void deletePhotoShouldUpdateStatistics() {
        // Arrange
        DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
                .setPhotoId(photoId.toString())
                .setUserId(userId.toString())
                .build();

        PhotoEntity photo = new PhotoEntity();
        photo.setUserId(userId);
        photo.setCountryId(countryId);

        // Мокаем поведение репозитория photoRepository
        when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

        // Мокаем поведение репозитория statisticRepository
        // Возвращаем Optional.of() с сущностью статистики, устанавливаем значение для count
        StatisticEntity statisticEntity = new StatisticEntity();
        statisticEntity.setUserId(userId);
        statisticEntity.setCountryId(countryId);
        statisticEntity.setCount(0);  // Устанавливаем count в 0 (или другое значение)
        when(statisticRepository.findByUserIdAndCountryId(userId, countryId)).thenReturn(Optional.of(statisticEntity));

        // Act
        photoService.deletePhoto(request, boolObserver);

        // Assert
        verify(photoRepository).delete(photo);
        verify(statisticRepository).findByUserIdAndCountryId(userId, countryId);
    }


}