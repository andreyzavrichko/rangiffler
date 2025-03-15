package rangiffler.model.testdata;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString(exclude = "photo")
public class TestPhoto {

  private UUID id;
  private UUID userId;
  private TestCountry country;
  private String description;
  private byte[] photo;
  private LocalDateTime createdDate;
  private List<TestLike> likes;
}
