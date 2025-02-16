package rangiffler.mapper;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.grpc.Country;
import rangiffler.model.GqlCountry;

import javax.annotation.Nonnull;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountryMapper {

  public static @Nonnull GqlCountry fromGrpcMessage(@Nonnull Country countryMessage) {
    return new GqlCountry(
        UUID.fromString(countryMessage.getId()),
        countryMessage.getCode(),
        countryMessage.getName(),
        countryMessage.getFlag()
    );
  }
}
