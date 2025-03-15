package rangiffler.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.db.model.CountryEntity;
import rangiffler.model.testdata.TestCountry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountryMapper {

  public static TestCountry toTestCountry(CountryEntity countryEntity) {
    return new TestCountry(
        countryEntity.getId(),
        countryEntity.getCode(),
        countryEntity.getName(),
        countryEntity.getFlag()
    );
  }
}
