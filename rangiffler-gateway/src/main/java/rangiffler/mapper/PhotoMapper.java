package rangiffler.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.grpc.Photo;
import rangiffler.model.GqlCountry;
import rangiffler.model.GqlPhoto;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Утилитный класс для преобразования объекта типа Photo из gRPC сообщения в GraphQL модель GqlPhoto.
 * Этот класс выполняет маппинг данных о фотографии из gRPC сообщений в объекты GqlPhoto и GqlCountry.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhotoMapper {

  /**
   * Преобразует gRPC сообщение о фотографии в объект GqlPhoto.
   *
   * @param photoMessage gRPC сообщение, содержащее информацию о фотографии
   * @return объект GqlPhoto, полученный на основе gRPC сообщения
   */
  public static @Nonnull GqlPhoto fromGrpcMessage(@Nonnull Photo photoMessage) {
    // Преобразование идентификатора в UUID
    UUID photoId = UUID.fromString(photoMessage.getId());
    // Преобразование массива байтов изображения в строку
    String src = new String(photoMessage.getSrc().toByteArray(), StandardCharsets.UTF_8);

    // Преобразование данных о стране
    GqlCountry country = new GqlCountry(
            UUID.fromString(photoMessage.getCountryId()),
            null, null, null // Пример данных для страны, которые могут быть дополнены позже
    );

    // Преобразование даты создания фотографии
    LocalDate creationDate = LocalDate.ofInstant(
            Instant.ofEpochSecond(photoMessage.getCreationDate().getSeconds()),
            ZoneId.of("UTC")
    );

    // Преобразование лайков фотографии
    var likes = LikeMapper.fromGrpcMessage(photoMessage.getLikes());

    return new GqlPhoto(photoId, src, country, photoMessage.getDescription(), creationDate, likes);
  }
}
