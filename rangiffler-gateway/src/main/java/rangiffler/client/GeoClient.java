package rangiffler.client;

import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import rangiffler.grpc.GetCountryByCodeRequest;
import rangiffler.grpc.GetCountryRequest;
import rangiffler.grpc.RangifflerGeoServiceGrpc;
import rangiffler.mapper.CountryMapper;
import rangiffler.model.GqlCountry;

import java.util.List;
import java.util.UUID;

/**
 * Класс для взаимодействия с gRPC сервисом географической информации.
 * Предоставляет методы для получения данных о странах через gRPC.
 */
@Component
public class GeoClient {

  @GrpcClient("grpcGeoClient")
  private RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub rangifflerGeoServiceBlockingStub;

  /**
   * Получает список всех стран.
   *
   * @return Список всех стран, преобразованных в объекты типа GqlCountry.
   */
  public List<GqlCountry> getAllCountries() {
    var response = rangifflerGeoServiceBlockingStub.getAllCountries(Empty.getDefaultInstance());

    // Преобразуем полученные данные в объекты GqlCountry
    return response.getAllCountriesList()
            .stream()
            .map(CountryMapper::fromGrpcMessage)
            .toList();
  }

  /**
   * Получает информацию о стране по её идентификатору.
   *
   * @param countryId Идентификатор страны в виде UUID.
   * @return Информацию о стране в виде объекта GqlCountry.
   */
  public GqlCountry getCountry(UUID countryId) {
    var request = GetCountryRequest.newBuilder().setId(countryId.toString()).build();
    var response = rangifflerGeoServiceBlockingStub.getCountry(request);

    // Преобразуем ответ gRPC в объект GqlCountry
    return CountryMapper.fromGrpcMessage(response);
  }

  /**
   * Получает информацию о стране по её коду.
   *
   * @param code Код страны.
   * @return Информацию о стране в виде объекта GqlCountry.
   */
  public GqlCountry getCountryByCode(String code) {
    var response = rangifflerGeoServiceBlockingStub.getCountryByCode(
            GetCountryByCodeRequest.newBuilder().setCode(code).build()
    );

    // Преобразуем ответ gRPC в объект GqlCountry
    return CountryMapper.fromGrpcMessage(response);
  }
}
