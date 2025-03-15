package rangiffler.test.grpc.geo;


import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import rangiffler.config.Config;
import rangiffler.grpc.RangifflerGeoServiceGrpc;
import rangiffler.grpc.interceptor.GrpcConsoleInterceptor;
import rangiffler.jupiter.annotation.meta.GrpcTest;

@GrpcTest
public abstract class GrpcGeoTestBase {

    protected static final Config CFG = Config.getInstance();
    protected static final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.geoHost(), CFG.geoPort())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();
    protected static final RangifflerGeoServiceGrpc.RangifflerGeoServiceBlockingStub blockingStub
            = RangifflerGeoServiceGrpc.newBlockingStub(channel);

}
