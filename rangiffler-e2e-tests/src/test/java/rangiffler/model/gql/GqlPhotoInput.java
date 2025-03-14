package rangiffler.model.gql;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GqlPhotoInput {

  private UUID id;
  private String src;
  private GqlCountryInput country;
  private String description;
  private GqlLikeInput like;
}
