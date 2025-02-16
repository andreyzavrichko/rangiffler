package rangiffler.service;

import com.google.protobuf.BoolValue;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import rangiffler.data.LikeEntity;
import rangiffler.data.PhotoEntity;
import rangiffler.data.StatisticEntity;
import rangiffler.data.repository.LikeRepository;
import rangiffler.data.repository.PhotoRepository;
import rangiffler.data.repository.StatisticRepository;
import rangiffler.exception.IllegalPhotoAccessException;
import rangiffler.exception.PhotoNotFoundException;
import rangiffler.exception.StatisticNotFoundException;
import rangiffler.grpc.*;
import rangiffler.mapper.PhotoMapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Сервис для работы с запросами фотосервиса.
 * Этот сервис предоставляет возможность работы с фотографиями.
 */
@GrpcService
public class PhotoService extends RangifflerPhotoServiceGrpc.RangifflerPhotoServiceImplBase {

    private final PhotoRepository photoRepository;
    private final LikeRepository likeRepository;
    private final StatisticRepository statisticRepository;

    @Autowired
    public PhotoService(PhotoRepository photoRepository, LikeRepository likeRepository,
                        StatisticRepository statisticRepository) {
        this.photoRepository = photoRepository;
        this.statisticRepository = statisticRepository;
        this.likeRepository = likeRepository;
    }

    /**
     * Получает список фотографий для пользователей по их идентификаторам.
     *
     * @param request запрос, содержащий список идентификаторов пользователей, номер страницы и размер страницы.
     * @param responseObserver наблюдатель для отправки ответа с фотографиями.
     */
    @Override
    public void getPhotos(PhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
        var photos = photoRepository.findByUserIdIn(
                request.getUserIdsList().stream().map(UUID::fromString).toList(),
                PageRequest.of(request.getPage(), request.getSize())
        );

        var photoResponse = PhotoResponse.newBuilder()
                .addAllPhotos(photos.map(PhotoMapper::toMessage).toList())
                .setHasNext(photos.hasNext())
                .build();
        responseObserver.onNext(photoResponse);
        responseObserver.onCompleted();
    }

    /**
     * Создает новую фотографию для пользователя.
     *
     * @param request запрос с данными для создания фотографии.
     * @param responseObserver наблюдатель для отправки ответа с созданной фотографией.
     */
    @Transactional
    @Override
    public void createPhoto(CreatePhotoRequest request, StreamObserver<Photo> responseObserver) {
        var photoEntity = new PhotoEntity();

        photoEntity.setUserId(UUID.fromString(request.getUserId()));
        photoEntity.setPhoto(request.getSrc().toByteArray());
        photoEntity.setCountryId(UUID.fromString(request.getCountryId()));
        photoEntity.setDescription(request.getDescription());
        photoEntity.setCreatedDate(Timestamp.from(Instant.now()));

        photoRepository.save(photoEntity);
        updateStatistics(photoEntity, true);

        var photoResponse = PhotoMapper.toMessage(photoEntity);
        responseObserver.onNext(photoResponse);
        responseObserver.onCompleted();
    }

    /**
     * Обновляет информацию о фотографии (например, страну или описание).
     *
     * @param request запрос с новыми данными для обновления фотографии.
     * @param responseObserver наблюдатель для отправки ответа с обновленной фотографией.
     * @throws PhotoNotFoundException если фотография с указанным ID не найдена.
     * @throws IllegalPhotoAccessException если пользователь не имеет прав на редактирование фотографии.
     */
    @Transactional
    @Override
    public void updatePhoto(UpdatePhotoRequest request, StreamObserver<Photo> responseObserver) {
        var photoEntity = photoRepository.findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new PhotoNotFoundException(request.getId()));
        var originalCountryId = photoEntity.getCountryId();

        if (!photoEntity.getUserId().equals(UUID.fromString(request.getUserId()))) {
            throw new IllegalPhotoAccessException(photoEntity.getId().toString(), request.getUserId());
        }

        photoEntity.setCountryId(UUID.fromString(request.getCountryId()));
        photoEntity.setDescription(request.getDescription());

        photoRepository.save(photoEntity);
        updateStatistics(photoEntity, false, originalCountryId);

        var photoResponse = PhotoMapper.toMessage(photoEntity);
        responseObserver.onNext(photoResponse);
        responseObserver.onCompleted();
    }

    /**
     * Ставит или удаляет лайк на фотографии.
     *
     * @param request запрос с ID фотографии и ID пользователя, ставящего или удаляющего лайк.
     * @param responseObserver наблюдатель для отправки ответа с обновленной фотографией.
     * @throws PhotoNotFoundException если фотография с указанным ID не найдена.
     */
    @Transactional
    @Override
    public void likePhoto(LikePhotoRequest request, StreamObserver<Photo> responseObserver) {
        var photoEntity = photoRepository.findById(UUID.fromString(request.getPhotoId()))
                .orElseThrow(() -> new PhotoNotFoundException(request.getPhotoId()));

        var existedLikeEntity = photoEntity.getLikes().stream()
                .filter(s -> UUID.fromString(request.getUserId()).equals(s.getUserId()))
                .findFirst();

        if (existedLikeEntity.isEmpty()) {
            var likeEntity = new LikeEntity();
            likeEntity.setUserId(UUID.fromString(request.getUserId()));
            likeEntity.setCreatedDate(Timestamp.from(Instant.now()));
            photoEntity.getLikes().add(likeEntity);
            photoRepository.save(photoEntity);
        } else {
            photoEntity.getLikes().remove(existedLikeEntity.get());
            likeRepository.delete(existedLikeEntity.get());
            photoRepository.save(photoEntity);
        }

        var photoResponse = PhotoMapper.toMessage(photoEntity);
        responseObserver.onNext(photoResponse);
        responseObserver.onCompleted();
    }

    /**
     * Удаляет фотографию.
     *
     * @param request запрос с ID фотографии и ID пользователя, удаляющего фотографию.
     * @param responseObserver наблюдатель для отправки ответа о результате операции.
     * @throws PhotoNotFoundException если фотография с указанным ID не найдена.
     * @throws IllegalPhotoAccessException если пользователь не имеет прав на удаление фотографии.
     */
    @Transactional
    @Override
    public void deletePhoto(DeletePhotoRequest request, StreamObserver<BoolValue> responseObserver) {
        var photoEntity = photoRepository.findById(UUID.fromString(request.getPhotoId()))
                .orElseThrow(() -> new PhotoNotFoundException(request.getPhotoId()));

        if (!photoEntity.getUserId().equals(UUID.fromString(request.getUserId()))) {
            throw new IllegalPhotoAccessException(photoEntity.getId().toString(), request.getUserId());
        }

        photoRepository.delete(photoEntity);
        updateStatistics(photoEntity, false);

        responseObserver.onNext(BoolValue.of(true));
        responseObserver.onCompleted();
    }

    /**
     * Обновляет статистику для пользователя и страны при добавлении или удалении фотографии.
     *
     * @param photoEntity сущность фотографии, с которой связана статистика.
     * @param isAdd флаг, указывающий, нужно ли добавлять или удалять фотографию из статистики.
     */
    private void updateStatistics(PhotoEntity photoEntity, boolean isAdd) {
        updateStatistics(photoEntity, isAdd, null);
    }

    /**
     * Обновляет статистику для пользователя и страны при добавлении или удалении фотографии.
     *
     * @param photoEntity сущность фотографии, с которой связана статистика.
     * @param isAdd флаг, указывающий, нужно ли добавлять или удалять фотографию из статистики.
     * @param originalCountryId исходный идентификатор страны фотографии, если необходимо учесть изменения.
     * @throws StatisticNotFoundException если статистика для пользователя и страны не найдена.
     */
    private void updateStatistics(PhotoEntity photoEntity, boolean isAdd, UUID originalCountryId) {
        var statisticEntity = statisticRepository.findByUserIdAndCountryId(photoEntity.getUserId(), photoEntity.getCountryId());

        if (isAdd) {
            statisticEntity.ifPresentOrElse(
                    stat -> {
                        stat.setCount(stat.getCount() + 1);
                        statisticRepository.save(stat);
                    },
                    () -> {
                        var newStatisticEntity = new StatisticEntity();
                        newStatisticEntity.setUserId(photoEntity.getUserId());
                        newStatisticEntity.setCountryId(photoEntity.getCountryId());
                        newStatisticEntity.setCount(1);
                        statisticRepository.save(newStatisticEntity);
                    }
            );
        } else {
            if (statisticEntity.isPresent()) {
                StatisticEntity stat = statisticEntity.get();
                stat.setCount(stat.getCount() - 1);
                if (stat.getCount() == 0) {
                    statisticRepository.delete(stat);
                } else {
                    statisticRepository.save(stat);
                }
            } else {
                throw new StatisticNotFoundException(photoEntity.getUserId(), photoEntity.getCountryId());
            }
        }
    }
}
