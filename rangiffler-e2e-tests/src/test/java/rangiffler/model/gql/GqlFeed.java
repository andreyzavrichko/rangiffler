package rangiffler.model.gql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GqlFeed extends GqlResponseType {

  private String username;
  private Boolean withFriends;
  private GqlConnection<GqlPhoto> photos;
  private List<GqlStat> stat;
}
