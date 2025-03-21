package rangiffler.model.gql;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GqlResponseType {

  @JsonProperty("__typename")
  private String typename;
}
