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


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

  public static @Nonnull GqlUser fromGrpcMessage(@Nonnull User userMessage) {
    return new GqlUser(
        UUID.fromString(userMessage.getId()),
        userMessage.getUsername(),
        userMessage.getFirstname(),
        userMessage.getLastName(),
        new String(userMessage.getAvatar().toByteArray(), StandardCharsets.UTF_8),
        userMessage.getFriendStatus() == FRIEND_STATUS_UNSPECIFIED || userMessage.getFriendStatus() == NOT_FRIEND
            ? null
            : GqlFriendStatus.valueOf(userMessage.getFriendStatus().name()),
        null,
        null,
        null,
        new GqlCountry(UUID.fromString(userMessage.getCountryId()), null, null, null)
    );
  }
}
