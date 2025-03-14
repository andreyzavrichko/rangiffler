package rangiffler.model.gql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GqlConnection<T> extends GqlResponseType {

  private List<T> edges;
  private GqlPageInfo pageInfo;
}
