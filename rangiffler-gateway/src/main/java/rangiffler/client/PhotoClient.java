package rangiffler.client;

import com.google.protobuf.ByteString;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Component;
import rangiffler.grpc.*;
import rangiffler.mapper.PhotoMapper;
import rangiffler.model.GqlPhoto;
import rangiffler.model.PhotoInput;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Класс для взаимодействия с gRPC сервисом фотографий.
 * Предоставляет методы для получения, создания, обновления, удаления и лайков фотографий.
 */
@Component
public class PhotoClient {

  @GrpcClient("grpcPhotoClient")
  private RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub rangifflerPhotoServiceBlockingStub;
  @Autowired
  private UsersClient usersClient;
  @Autowired
  private GeoClient geoClient;

  /**
   * Получает фотографии пользователя, включая фотографии друзей, с поддержкой пагинации.
   *
   * @param userName Имя пользователя для получения фотографий.
   * @param page Номер страницы для пагинации.
   * @param size Количество фотографий на странице.
   * @param withFriends Флаг для включения фотографий друзей.
   * @return Slice, содержащий список фотографий.
   */
  public Slice<GqlPhoto> getPhotos(String userName, int page, int size, boolean withFriends) {
    var userIds = new ArrayList<UUID>();
    userIds.add(usersClient.getUser(userName).id());

    // Добавляем идентификаторы друзей, если флаг withFriends установлен в true
    if (withFriends) {
      userIds.addAll(usersClient.getUserFriendsIds(userName));
    }

    var response = rangifflerPhotoServiceBlockingStub.getPhotos(
            PhotoRequest.newBuilder()
                    .addAllUserIds(userIds.stream().map(UUID::toString).toList())
                    .setPage(page)
                    .setSize(size)
                    .build()
    );

    var photos = response.getPhotosList()
            .stream()
            .map(PhotoMapper::fromGrpcMessage)
            .toList();

    // Если фотографии есть, возвращаем их в Slice, иначе null
    return !photos.isEmpty()
            ? new SliceImpl<>(photos, PageRequest.of(page, size), response.getHasNext())
            : null;
  }

  /**
   * Обрабатывает создание, обновление или лайк фотографии в зависимости от состояния объекта PhotoInput.
   *
   * @param userName Имя пользователя, который взаимодействует с фотографией.
   * @param photo Объект входных данных фотографии, включающий описание, страну и другие параметры.
   * @return Обновленный или созданный объект GqlPhoto.
   */
  public GqlPhoto photo(String userName, PhotoInput photo) {
    // Если id фотографии отсутствует, создаем новую
    if (photo.id() == null) {
      return createPhoto(userName, photo);
    }

    // Если в фото есть лайк, обновляем лайк
    if (photo.like() != null) {
      return likePhoto(photo);
    }

    // Иначе, обновляем существующую фотографию
    return updatePhoto(userName, photo);
  }

  /**
   * Создает новую фотографию.
   *
   * @param userName Имя пользователя, добавляющего фотографию.
   * @param photo Объект данных фотографии для создания.
   * @return Созданный объект GqlPhoto.
   */
  public GqlPhoto createPhoto(String userName, PhotoInput photo) {
    var country = geoClient.getCountryByCode(photo.country().code()).id().toString();
    var user = usersClient.getUser(userName);

    var request = CreatePhotoRequest.newBuilder()
            .setUserId(user.id().toString())
            .setSrc(ByteString.copyFrom(photo.src() != null ? photo.src().getBytes(StandardCharsets.UTF_8) : new byte[]{}))
            .setCountryId(country)
            .setDescription(photo.description())
            .build();

    var response = rangifflerPhotoServiceBlockingStub.createPhoto(request);
    return PhotoMapper.fromGrpcMessage(response);
  }

  /**
   * Обновляет существующую фотографию.
   *
   * @param userName Имя пользователя, который обновляет фотографию.
   * @param photo Объект данных фотографии для обновления.
   * @return Обновленный объект GqlPhoto.
   */
  public GqlPhoto updatePhoto(String userName, PhotoInput photo) {
    var country = geoClient.getCountryByCode(photo.country().code()).id().toString();
    var user = usersClient.getUser(userName);

    var request = UpdatePhotoRequest.newBuilder()
            .setId(photo.id().toString())
            .setUserId(user.id().toString())
            .setCountryId(country)
            .setDescription(photo.description())
            .build();

    var response = rangifflerPhotoServiceBlockingStub.updatePhoto(request);
    return PhotoMapper.fromGrpcMessage(response);
  }

  /**
   * Отмечает фотографию лайком.
   *
   * @param photo Объект входных данных фотографии, включая информацию о пользователе, ставящем лайк.
   * @return Обновленный объект GqlPhoto.
   */
  public GqlPhoto likePhoto(PhotoInput photo) {
    var request = LikePhotoRequest.newBuilder()
            .setPhotoId(photo.id().toString())
            .setUserId(photo.like().user().toString())
            .build();

    var response = rangifflerPhotoServiceBlockingStub.likePhoto(request);
    return PhotoMapper.fromGrpcMessage(response);
  }

  /**
   * Удаляет фотографию.
   *
   * @param userName Имя пользователя, который удаляет фотографию.
   * @param photoId Идентификатор фотографии, которую нужно удалить.
   * @return true, если фотография успешно удалена, иначе false.
   */
  public boolean deletePhoto(String userName, UUID photoId) {
    var user = usersClient.getUser(userName);
    var request = DeletePhotoRequest.newBuilder()
            .setPhotoId(photoId.toString())
            .setUserId(user.id().toString())
            .build();

    var response = rangifflerPhotoServiceBlockingStub.deletePhoto(request);
    return response.getValue();
  }
}
