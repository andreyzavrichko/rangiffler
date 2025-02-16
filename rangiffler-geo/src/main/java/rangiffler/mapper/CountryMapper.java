package rangiffler.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.data.CountryEntity;
import rangiffler.grpc.Country;

import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountryMapper {

  public static Country toMessage(CountryEntity countryEntity) {
    return Country.newBuilder()
        .setId(countryEntity.getId().toString())
        .setCode(countryEntity.getCode())
        .setName(countryEntity.getName())
        .setFlag(new String(countryEntity.getFlag(), StandardCharsets.UTF_8))
        .build();
  }
}
