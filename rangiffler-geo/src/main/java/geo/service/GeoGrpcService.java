package geo.service;

import com.google.protobuf.Empty;
import geo.data.entity.CountryEntity;
import geo.data.repository.CountryRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import rangiffler.grpc.CodeRequest;
import rangiffler.grpc.Countries;
import rangiffler.grpc.Country;
import rangiffler.grpc.GeoServiceGrpc;

import java.util.Collection;
import java.util.Objects;

@GrpcService
public class GeoGrpcService extends GeoServiceGrpc.GeoServiceImplBase {

    private final CountryRepository countryRepository;

    @Autowired
    public GeoGrpcService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getAllCountries(Empty request, StreamObserver<Countries> responseObserver) {
        responseObserver.onNext(convertToCountries(countryRepository.findAllByOrderByNameAsc()));
        responseObserver.onCompleted();
    }

    @Override
    public void getCountryByCode(CodeRequest request, StreamObserver<Country> responseObserver) {
        responseObserver.onNext(Objects.requireNonNull(countryRepository.findByCode(request.getCode())).toGrpc());
        responseObserver.onCompleted();
    }

    private Countries convertToCountries(Collection<CountryEntity> entities) {
        return Countries.newBuilder()
                .addAllCountries(entities.stream()
                        .map(CountryEntity::toGrpc)
                        .toList()
                ).build();
    }

}