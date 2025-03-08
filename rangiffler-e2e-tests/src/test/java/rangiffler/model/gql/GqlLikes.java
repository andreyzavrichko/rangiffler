package rangiffler.model.gql;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GqlLikes extends GqlResponseType {

  private Integer total;
  private List<GqlLike> likes;
}
