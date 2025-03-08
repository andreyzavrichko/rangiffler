package rangiffler.mapper;

import com.google.protobuf.ByteString;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.db.model.UserEntity;
import rangiffler.grpc.FriendStatus;
import rangiffler.grpc.User;
import rangiffler.model.testdata.TestCountry;
import rangiffler.model.testdata.TestUser;

import java.util.ArrayList;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntityMapper {

  public static TestUser toUser(UserEntity entity) {
    return TestUser.builder()
        .id(entity.getId())
        .username(entity.getUsername())
        .firstname(entity.getFirstname())
        .lastName(entity.getLastName())
        .avatar(entity.getAvatar() != null ? entity.getAvatar() : new byte[]{})
        .country(new TestCountry(entity.getCountryId(), null, null, null))
        .friends(new ArrayList<>())
        .incomeInvitations(new ArrayList<>())
        .outcomeInvitations(new ArrayList<>())
        .photos(new ArrayList<>())
        .build();
  }

  public static User toMessage(TestUser testUser) {
    return User.newBuilder()
        .setId(testUser.getId().toString())
        .setUsername(testUser.getUsername())
        .setFirstname(testUser.getFirstname())
        .setLastName(testUser.getLastName())
        .setAvatar(ByteString.copyFrom(testUser.getAvatar() != null ? testUser.getAvatar() : new byte[]{}))
        .setCountryId(testUser.getCountry().getId().toString())
        .setFriendStatus(FriendStatus.FRIEND_STATUS_UNSPECIFIED)
        .build();
  }

  public static User toMessage(TestUser testUser, FriendStatus friendStatus) {
    return User.newBuilder()
        .setId(testUser.getId().toString())
        .setUsername(testUser.getUsername())
        .setFirstname(testUser.getFirstname())
        .setLastName(testUser.getLastName())
        .setAvatar(ByteString.copyFrom(testUser.getAvatar() != null ? testUser.getAvatar() : new byte[]{}))
        .setCountryId(testUser.getCountry().getId().toString())
        .setFriendStatus(friendStatus)
        .build();
  }

  public static User toMessage(UserEntity entity) {
    return User.newBuilder()
        .setId(entity.getId().toString())
        .setUsername(entity.getUsername())
        .setFirstname(entity.getFirstname())
        .setLastName(entity.getLastName())
        .setAvatar(ByteString.copyFrom(entity.getAvatar() != null ? entity.getAvatar() : new byte[]{}))
        .setCountryId(entity.getCountryId().toString())
        .build();
  }

  public static UserEntity toEntity(User message) {
    var userEntity = new UserEntity();
    userEntity.setId(UUID.fromString(message.getId()));
    userEntity.setUsername(message.getUsername());
    userEntity.setLastName(message.getLastName());
    userEntity.setAvatar(message.getAvatar().toByteArray());
    userEntity.setCountryId(UUID.fromString(message.getCountryId()));
    return userEntity;
  }
}
