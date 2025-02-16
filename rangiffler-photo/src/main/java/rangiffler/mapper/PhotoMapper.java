package rangiffler.mapper;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.data.PhotoEntity;
import rangiffler.grpc.Like;
import rangiffler.grpc.Likes;
import rangiffler.grpc.Photo;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhotoMapper {

  public static Photo toMessage(PhotoEntity photoEntity) {
    return Photo.newBuilder()
        .setId(photoEntity.getId().toString())
        .setSrc(ByteString.copyFrom(photoEntity.getPhoto() != null ? photoEntity.getPhoto() : new byte[]{}))
        .setCountryId(photoEntity.getCountryId().toString())
        .setDescription(photoEntity.getDescription() != null ? photoEntity.getDescription() : "")
        .setCreationDate(Timestamp.newBuilder()
            .setSeconds(photoEntity.getCreatedDate().toInstant().getEpochSecond())
            .setNanos(photoEntity.getCreatedDate().toInstant().getNano())
        )
        .setLikes(
            Likes.newBuilder()
                .setTotal(photoEntity.getLikes().size())
                .addAllLikes(
                    photoEntity.getLikes().stream().map(likeEntity -> Like.newBuilder()
                        .setId(likeEntity.getId().toString())
                        .setUserId(likeEntity.getUserId().toString())
                        .setCreationDate(
                            Timestamp.newBuilder()
                                .setSeconds(likeEntity.getCreatedDate().toInstant().getEpochSecond())
                                .setNanos(likeEntity.getCreatedDate().toInstant().getNano())
                        )
                        .build()
                    ).toList()
                )
        )
        .build();
  }
}
