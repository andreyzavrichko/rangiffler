package rangiffler.db.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class LikeEntity {

  private UUID id;
  private UUID userId;
  private Timestamp createdDate;
}
