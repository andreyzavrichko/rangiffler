package gateway.model;

import java.util.List;

public record GqlLikes(Integer total, List<GqlLike> likes) {

}
