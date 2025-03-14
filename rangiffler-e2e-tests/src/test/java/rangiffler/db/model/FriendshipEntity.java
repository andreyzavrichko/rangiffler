package rangiffler.db.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class FriendshipEntity {

  private UUID id;
  private UUID requesterId;
  private UUID addresseeId;
  private Timestamp createdDate;
  private FriendshipStatus status;
}
