package rangiffler.db.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CountryEntity {

  private UUID id;
  private String code;
  private String name;
  private byte[] flag;
}
