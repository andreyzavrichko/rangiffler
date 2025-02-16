package rangiffler.mapper;

import com.google.protobuf.ByteString;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.data.UserEntity;
import rangiffler.grpc.FriendStatus;
import rangiffler.grpc.User;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityMapper {

  /**
   * Преобразует объект UserEntity в User.
   *
   * @param entity объект UserEntity
   * @return объект User
   */
  public static User toMessage(UserEntity entity) {
    return createUserBuilder(entity).build();
  }

  /**
   * Преобразует объект UserEntity в User с указанием статуса дружбы.
   *
   * @param entity объект UserEntity
   * @param friendStatus статус дружбы
   * @return объект User
   */
  public static User toMessage(UserEntity entity, FriendStatus friendStatus) {
    return createUserBuilder(entity).setFriendStatus(friendStatus).build();
  }

  /**
   * Преобразует объект User в UserEntity.
   *
   * @param message объект User
   * @return объект UserEntity
   * @throws IllegalArgumentException если message равен null
   */
  public static UserEntity toEntity(User message) {
    if (message == null) {
      throw new IllegalArgumentException("Message cannot be null");
    }
    var userEntity = new UserEntity();
    userEntity.setId(UUID.fromString(message.getId()));
    userEntity.setUsername(message.getUsername());
    userEntity.setLastName(message.getLastName());
    userEntity.setAvatar(message.getAvatar().toByteArray());
    userEntity.setCountryId(message.getCountryId());
    return userEntity;
  }

  /**
   * Приватный метод для создания базового User.Builder.
   *
   * @param entity объект UserEntity
   * @return User.Builder
   */
  private static User.Builder createUserBuilder(UserEntity entity) {
    return User.newBuilder()
            .setId(entity.getId().toString())
            .setUsername(entity.getUsername())
            .setFirstname(entity.getFirstname() != null ? entity.getFirstname() : "")
            .setLastName(entity.getLastName() != null ? entity.getLastName() : "")
            .setAvatar(ByteString.copyFrom(entity.getAvatar() != null ? entity.getAvatar() : new byte[]{}))
            .setCountryId(entity.getCountryId());
  }
}
