package rangiffler.model.gql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GqlResponse<T> {

  protected T data;
  protected List<GqlError> errors;
}
