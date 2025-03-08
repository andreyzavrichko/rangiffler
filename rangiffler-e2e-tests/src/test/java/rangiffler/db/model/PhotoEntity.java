package rangiffler.db.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class PhotoEntity {

  private UUID id;
  private UUID userId;
  private UUID countryId;
  private String description;
  private byte[] photo;
  private Timestamp createdDate;
}
