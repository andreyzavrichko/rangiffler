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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeMapper {

  public static GqlLikes fromGrpcMessage(Likes likesMessage) {
    return new GqlLikes(
        likesMessage.getTotal(),
        likesMessage.getLikesList().stream().map(LikeMapper::fromGrpcMessage).toList()
    );
  }

  public static GqlLike fromGrpcMessage(Like likeMessage) {
    return new GqlLike(
        UUID.fromString(likeMessage.getUserId()),
        likeMessage.getUserId(),
        LocalDate.ofInstant(Instant.ofEpochSecond(likeMessage.getCreationDate().getSeconds()), ZoneId.of("UTC"))
    );
  }
}
