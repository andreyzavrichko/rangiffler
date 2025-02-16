package rangiffler.mapper;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.data.PhotoEntity;
import rangiffler.grpc.Like;
import rangiffler.grpc.Likes;
import rangiffler.grpc.Photo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhotoMapper {

    public static Photo toMessage(PhotoEntity photoEntity) {
        return Photo.newBuilder()
                .setId(photoEntity.getId().toString())
                .setSrc(ByteString.copyFrom(Optional.ofNullable(photoEntity.getPhoto()).orElse(new byte[]{})))
                .setCountryId(photoEntity.getCountryId().toString())
                .setDescription(photoEntity.getDescription() != null ? photoEntity.getDescription() : "")
                .setCreationDate(toTimestamp(photoEntity.getCreatedDate().toLocalDateTime()))
                .setLikes(
                        Likes.newBuilder()
                                .setTotal(photoEntity.getLikes().size())
                                .addAllLikes(
                                        photoEntity.getLikes().stream().map(PhotoMapper::toLikeMessage).toList()
                                )
                )
                .build();
    }

    private static Timestamp toTimestamp(LocalDateTime dateTime) {
        return Timestamp.newBuilder()
                .setSeconds(dateTime.toInstant(ZoneOffset.UTC).getEpochSecond())
                .setNanos(dateTime.toInstant(ZoneOffset.UTC).getNano())
                .build();
    }

    private static Like toLikeMessage(rangiffler.data.LikeEntity likeEntity) {
        return Like.newBuilder()
                .setId(likeEntity.getId().toString())
                .setUserId(likeEntity.getUserId().toString())
                .setCreationDate(toTimestamp(likeEntity.getCreatedDate().toLocalDateTime()))
                .build();
    }
}
