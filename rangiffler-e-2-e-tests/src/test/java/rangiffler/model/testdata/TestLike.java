package rangiffler.model.testdata;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestLike {

  private UUID id;
  private UUID userId;
  private LocalDateTime creationDate;
}
