package rangiffler.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.grpc.Country;
import rangiffler.model.GqlCountry;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Утилитный класс для преобразования объектов из формата gRPC в формат GraphQL.
 * Этот класс выполняет маппинг данных о стране из gRPC сообщения в объект GqlCountry.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountryMapper {

  /**
   * Преобразует gRPC сообщение о стране в объект GqlCountry.
   *
   * @param countryMessage gRPC сообщение, содержащее информацию о стране
   * @return объект GqlCountry, полученный на основе gRPC сообщения
   * @throws IllegalArgumentException если строка ID из countryMessage некорректного формата
   */
  public static @Nonnull GqlCountry fromGrpcMessage(@Nonnull Country countryMessage) {
    // Преобразование строки ID в UUID
    UUID countryId = UUID.fromString(countryMessage.getId());

    return new GqlCountry(
            countryId,
            countryMessage.getCode(),
            countryMessage.getName(),
            countryMessage.getFlag()
    );
  }
}
