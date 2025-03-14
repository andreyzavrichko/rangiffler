package rangiffler.db.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StatisticEntity {

  private UUID id;
  private UUID userId;
  private UUID countryId;
  private Integer count;
}
