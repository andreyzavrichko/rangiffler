package rangiffler.db.repository;


import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import rangiffler.db.DataSourceProvider;
import rangiffler.db.JdbcUrl;
import rangiffler.db.mapper.CountryEntityRowMapper;
import rangiffler.db.model.CountryEntity;

public class CountryRepositoryJdbc implements CountryRepository {

  private final JdbcTemplate countryTemplate = new JdbcTemplate(DataSourceProvider.INSTANCE.dataSource(JdbcUrl.GEO));

  @Override
  public CountryEntity findRequiredCountryById(UUID id) {
    return countryTemplate.queryForObject("SELECT * FROM \"country\" WHERE id = ?", new CountryEntityRowMapper(), id);
  }

  @Override
  public CountryEntity findRequiredCountryByCode(String code) {
    return countryTemplate.queryForObject("SELECT * FROM \"country\" WHERE code = ?", new CountryEntityRowMapper(),
            code);
  }

  @Override
  public CountryEntity findRequiredCountryByIdNot(UUID id) {
    return countryTemplate.queryForObject("SELECT * FROM \"country\" WHERE id != ? LIMIT 1",
            new CountryEntityRowMapper(),
            id);
  }

  @Override
  public Integer count() {
    return countryTemplate.queryForObject("SELECT COUNT(*) FROM \"country\"", Integer.class);
  }
}
