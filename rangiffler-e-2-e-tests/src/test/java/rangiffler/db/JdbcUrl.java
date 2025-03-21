package rangiffler.db;


import lombok.RequiredArgsConstructor;
import rangiffler.config.Config;

@RequiredArgsConstructor
public enum JdbcUrl {

  AUTH("jdbc:postgresql://%s:%d/rangiffler-auth"),
  PHOTO("jdbc:postgresql://%s:%d/rangiffler-photo"),
  GEO("jdbc:postgresql://%s:%d/rangiffler-geo"),
  USERDATA("jdbc:postgresql://%s:%d/rangiffler-userdata");

  private final String url;
  private static final Config CFG = Config.getInstance();

  public String getUrl() {
    return String.format(
        url,
        CFG.jdbcHost(),
        CFG.jdbcPort()
    );
  }
}
