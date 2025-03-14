package rangiffler.model.gql;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class GqlCountry extends GqlResponseType {

  private String code;
  private String name;
  private String flag;
}
