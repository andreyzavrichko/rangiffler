package photo.service.api;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import rangiffler.grpc.CodeRequest;
import rangiffler.grpc.Country;
import rangiffler.grpc.GeoServiceGrpc;

@Component
public class GeoGrpcClient {

    @GrpcClient("grpcGeoClient")
    private GeoServiceGrpc.GeoServiceBlockingStub geoServiceBlockingStub;

    public Country getCountryByCode(String code) {
        return geoServiceBlockingStub.getCountryByCode(getCodeRequest(code));
    }

    private CodeRequest getCodeRequest(String code) {
        return CodeRequest.newBuilder().setCode(code).build();
    }

}