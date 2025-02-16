package rangiffler.model;

import org.springframework.data.domain.Slice;

import java.util.List;

public record GqlFeed(
    String username,
    Boolean withFriends,
    Slice<GqlPhoto> photos,
    List<GqlStat> stat
) {

}
