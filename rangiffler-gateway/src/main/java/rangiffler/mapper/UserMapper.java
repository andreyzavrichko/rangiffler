package rangiffler.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.grpc.User;
import rangiffler.model.GqlCountry;
import rangiffler.model.GqlFriendStatus;
import rangiffler.model.GqlUser;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static rangiffler.grpc.FriendStatus.FRIEND_STATUS_UNSPECIFIED;
import static rangiffler.grpc.FriendStatus.NOT_FRIEND;

/**
 * Утилитный класс для преобразования объекта типа User из gRPC сообщения в GraphQL модель GqlUser.
 * Этот класс выполняет маппинг данных о пользователе, статусе дружбы и других связанных полях из gRPC сообщения в объекты GqlUser и GqlCountry.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

  /**
   * Преобразует gRPC сообщение о пользователе в объект GqlUser.
   *
   * @param userMessage gRPC сообщение, содержащее информацию о пользователе
   * @return объект GqlUser, полученный на основе gRPC сообщения
   */
  public static @Nonnull GqlUser fromGrpcMessage(@Nonnull User userMessage) {
    // Преобразование идентификатора пользователя в UUID
    UUID userId = UUID.fromString(userMessage.getId());

    // Преобразование аватара пользователя из байтов в строку
    String avatar = new String(userMessage.getAvatar().toByteArray(), StandardCharsets.UTF_8);

    // Преобразование статуса дружбы
    GqlFriendStatus friendStatus = (userMessage.getFriendStatus() == FRIEND_STATUS_UNSPECIFIED || userMessage.getFriendStatus() == NOT_FRIEND)
            ? null
            : GqlFriendStatus.valueOf(userMessage.getFriendStatus().name());

    // Создание объекта GqlUser на основе данных из gRPC сообщения
    return new GqlUser(
            userId,
            userMessage.getUsername(),
            userMessage.getFirstname(),
            userMessage.getLastName(),
            avatar,
            friendStatus,
            null,
            null,
            null,
            new GqlCountry(UUID.fromString(userMessage.getCountryId()), null, null, null) // Преобразование страны
    );
  }
}
