package rangiffler.service;

import com.google.protobuf.Empty;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import rangiffler.data.repository.CountryRepository;
import rangiffler.exception.CountryNotFoundException;
import rangiffler.grpc.*;
import rangiffler.mapper.CountryMapper;

import java.util.UUID;

@GrpcService
public class GeoService extends RangifflerGeoServiceGrpc.RangifflerGeoServiceImplBase {

  private final CountryRepository countryRepository;

  @Autowired
  public GeoService(CountryRepository countryRepository) {
    this.countryRepository = countryRepository;
  }

  @Override
  public void getAllCountries(Empty request, StreamObserver<AllCountriesResponse> responseObserver) {
    var allCountriesEntities = countryRepository.findAll();

    var allCountriesResponse = AllCountriesResponse.newBuilder().addAllAllCountries(
            allCountriesEntities.stream().map(CountryMapper::toMessage).toList()
        )
        .build();

    responseObserver.onNext(allCountriesResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void getCountry(GetCountryRequest request, StreamObserver<Country> responseObserver) {
    var countryEntity = countryRepository.findById(UUID.fromString(request.getId()))
        .orElseThrow(() -> new CountryNotFoundException(request.getId()));

    var countryResponse = CountryMapper.toMessage(countryEntity);
    responseObserver.onNext(countryResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void getCountryByCode(GetCountryByCodeRequest request, StreamObserver<Country> responseObserver) {
    var countryEntity = countryRepository.findByCode(request.getCode())
        .orElseThrow(() -> new CountryNotFoundException(request.getCode()));

    var countryResponse = CountryMapper.toMessage(countryEntity);
    responseObserver.onNext(countryResponse);
    responseObserver.onCompleted();
  }
}
