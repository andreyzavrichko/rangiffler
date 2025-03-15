package rangiffler.model.gql;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class GqlLike extends GqlResponseType {

  private UUID user;
  private String userName;
  private LocalDate creationDate;
}
