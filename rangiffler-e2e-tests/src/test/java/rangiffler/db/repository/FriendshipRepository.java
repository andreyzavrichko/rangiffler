package rangiffler.db.repository;



import rangiffler.db.model.FriendshipEntity;
import rangiffler.db.model.FriendshipStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository {

  void createFriendship(UUID firstFriendId, UUID secondFriendId, LocalDateTime createdDate,
      FriendshipStatus friendshipStatus);

  Optional<FriendshipEntity> findFriendshipByRequesterIdAndAddresseeId(UUID requesterId, UUID addresseeId);

  Optional<FriendshipEntity> findFriendshipByRequesterIdAndAddresseeId(UUID requesterId, UUID addresseeId,
      FriendshipStatus status);
}
