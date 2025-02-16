package rangiffler.data.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rangiffler.data.FriendshipEntity;
import rangiffler.data.FriendshipStatus;
import rangiffler.data.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, UUID> {

  Optional<FriendshipEntity> findByRequesterAndAddresseeAndStatus(UserEntity requester, UserEntity addressee,
                                                                  FriendshipStatus status);

  @Query("select f from FriendshipEntity f "
      + "where (f.requester = :user or f.addressee = :user) "
      + "and (f.requester = :friend or f.addressee = :friend)")
  Optional<FriendshipEntity> findFriendship(UserEntity user, UserEntity friend);

  @Query("select f from FriendshipEntity f "
      + "where (f.requester in (:users) or f.addressee in (:users)) "
      + "and (f.requester = :friend or f.addressee = :friend)")
  List<FriendshipEntity> findFriendships(List<UserEntity> users, UserEntity friend);
}
