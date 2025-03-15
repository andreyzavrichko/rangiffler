package rangiffler.db.repository;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import rangiffler.db.DataSourceProvider;
import rangiffler.db.JdbcUrl;
import rangiffler.db.model.FriendshipEntity;
import rangiffler.db.model.FriendshipStatus;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class FriendshipRepositoryJdbc implements FriendshipRepository {

  private final JdbcTemplate udTemplate = new JdbcTemplate(DataSourceProvider.INSTANCE.dataSource(JdbcUrl.USERDATA));

  @Override
  public void createFriendship(UUID firstFriendId, UUID secondFriendId, LocalDateTime createdDate,
      FriendshipStatus friendshipStatus) {
    udTemplate.update("INSERT INTO friendship "
        + "(requester_id, addressee_id, created_date, status) "
        + "VALUES(?, ?, ?, ?)", firstFriendId, secondFriendId, createdDate, friendshipStatus.name()
    );
  }

  @Override
  public Optional<FriendshipEntity> findFriendshipByRequesterIdAndAddresseeId(UUID requesterId, UUID addresseeId) {
    try {
      return Optional.ofNullable(
          udTemplate.queryForObject("SELECT * FROM \"friendship\" WHERE requester_id = ? AND addressee_id = ?",
              (ResultSet rs, int rowNum) -> {
                var friendshipEntity = new FriendshipEntity();
                friendshipEntity.setId(rs.getObject("id", UUID.class));
                friendshipEntity.setRequesterId(rs.getObject("requester_id", UUID.class));
                friendshipEntity.setAddresseeId(rs.getObject("addressee_id", UUID.class));
                friendshipEntity.setCreatedDate(rs.getTimestamp("created_date"));
                friendshipEntity.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                return friendshipEntity;
              }, requesterId, addresseeId)
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<FriendshipEntity> findFriendshipByRequesterIdAndAddresseeId(UUID requesterId, UUID addresseeId,
      FriendshipStatus status) {
    try {
      return Optional.ofNullable(
          udTemplate.queryForObject("SELECT * FROM \"friendship\" WHERE requester_id = ? AND addressee_id = ? AND status = ?",
              (ResultSet rs, int rowNum) -> {
                var friendshipEntity = new FriendshipEntity();
                friendshipEntity.setId(rs.getObject("id", UUID.class));
                friendshipEntity.setRequesterId(rs.getObject("requester_id", UUID.class));
                friendshipEntity.setAddresseeId(rs.getObject("addressee_id", UUID.class));
                friendshipEntity.setCreatedDate(rs.getTimestamp("created_date"));
                friendshipEntity.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                return friendshipEntity;
              }, requesterId, addresseeId, status.name())
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }
}
