package rangiffler.db.mapper;

import org.springframework.jdbc.core.RowMapper;
import rangiffler.db.model.UserEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class UserEntityRowMapper implements RowMapper<UserEntity> {

  @Override
  public UserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    UserEntity user = new UserEntity();
    user.setId(rs.getObject("id", UUID.class));
    user.setUsername(rs.getString("username"));
    user.setFirstname(rs.getString("firstname"));
    user.setLastName(rs.getString("last_name"));
    user.setAvatar(rs.getBytes("avatar"));
    user.setCountryId(UUID.fromString(rs.getString("country_id")));
    return user;
  }
}
