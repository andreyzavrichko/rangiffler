package rangiffler.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.grpc.Like;
import rangiffler.grpc.Likes;
import rangiffler.model.GqlLike;
import rangiffler.model.GqlLikes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

/**
 * Утилитный класс для преобразования объектов из формата gRPC в формат GraphQL для сущности "Like".
 * Этот класс выполняет маппинг данных о лайках из gRPC сообщений в объекты GqlLike и GqlLikes.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeMapper {

  /**
   * Преобразует gRPC сообщение о списке лайков в объект GqlLikes.
   *
   * @param likesMessage gRPC сообщение, содержащее информацию о лайках
   * @return объект GqlLikes, полученный на основе gRPC сообщения
   */
  public static GqlLikes fromGrpcMessage(Likes likesMessage) {
    return new GqlLikes(
            likesMessage.getTotal(),
            likesMessage.getLikesList().stream().map(LikeMapper::fromGrpcMessage).toList()
    );
  }

  /**
   * Преобразует gRPC сообщение о лайке в объект GqlLike.
   *
   * @param likeMessage gRPC сообщение, содержащее информацию о лайке
   * @return объект GqlLike, полученный на основе gRPC сообщения
   * @throws IllegalArgumentException если строка ID пользователя некорректного формата
   */
  public static GqlLike fromGrpcMessage(Like likeMessage) {
    // Преобразование строки ID в UUID
    UUID userId = UUID.fromString(likeMessage.getUserId());

    return new GqlLike(
            userId,
            likeMessage.getUserId(),
            LocalDate.ofInstant(Instant.ofEpochSecond(likeMessage.getCreationDate().getSeconds()), ZoneId.of("UTC"))
    );
  }
}
