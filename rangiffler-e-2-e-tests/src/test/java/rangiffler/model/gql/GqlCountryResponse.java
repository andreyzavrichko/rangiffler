package rangiffler.model.gql;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GqlCountryResponse {

  private List<GqlCountry> countries;
}
