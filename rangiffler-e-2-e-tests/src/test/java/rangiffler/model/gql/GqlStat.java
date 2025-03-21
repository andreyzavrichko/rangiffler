package rangiffler.model.gql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GqlStat extends GqlResponseType {

  private Integer count;
  private GqlCountry country;
}
