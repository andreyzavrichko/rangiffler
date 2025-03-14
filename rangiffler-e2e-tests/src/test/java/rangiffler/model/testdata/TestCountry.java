package rangiffler.model.testdata;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "flag")
public class TestCountry {

  private UUID id;
  private String code;
  private String name;
  private byte[] flag;
}
