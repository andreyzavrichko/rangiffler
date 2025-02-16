package rangiffler.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rangiffler.data.CountryEntity;
import rangiffler.grpc.Country;

import java.nio.charset.StandardCharsets;

/**
 * Маппер для преобразования сущностей страны в сообщения gRPC.
 * Этот класс предоставляет метод для конвертации {@link CountryEntity} в {@link Country}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CountryMapper {

  /**
   * Преобразует сущность страны в gRPC-сообщение.
   *
   * @param countryEntity Сущность страны, которую нужно преобразовать в сообщение.
   * @return Сообщение gRPC, представляющее страну.
   */
  public static Country toMessage(CountryEntity countryEntity) {
    return Country.newBuilder()
            .setId(countryEntity.getId().toString())  // Преобразование идентификатора в строку
            .setCode(countryEntity.getCode())  // Код страны
            .setName(countryEntity.getName())  // Название страны
            .setFlag(new String(countryEntity.getFlag(), StandardCharsets.UTF_8))  // Преобразование флага в строку
            .build();
  }
}
