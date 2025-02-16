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

@Component
public class GeoClient {

  @GrpcClient("grpcGeoClient")
  private RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub rangifflerGeoServiceBlockingStub;

  public List<GqlCountry> getAllCountries() {
    var response = rangifflerGeoServiceBlockingStub.getAllCountries(Empty.getDefaultInstance());

    return response.getAllCountriesList()
        .stream()
        .map(CountryMapper::fromGrpcMessage)
        .toList();
  }

  public GqlCountry getCountry(UUID countryId) {
    var request = GetCountryRequest.newBuilder().setId(countryId.toString()).build();
    var response = rangifflerGeoServiceBlockingStub.getCountry(request);
    return CountryMapper.fromGrpcMessage(response);
  }

  public GqlCountry getCountryByCode(String code) {
    var response = rangifflerGeoServiceBlockingStub.getCountryByCode(
        GetCountryByCodeRequest.newBuilder().setCode(code).build()
    );
    return CountryMapper.fromGrpcMessage(response);
  }
}
