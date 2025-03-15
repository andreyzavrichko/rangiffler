package rangiffler.test.grpc.statistic;


import org.junit.jupiter.api.BeforeEach;
import rangiffler.config.Config;
import rangiffler.grpc.ChannelProvider;
import rangiffler.grpc.RangifflerPhotoServiceGrpc;
import rangiffler.grpc.RangifflerStatisticServiceGrpc;
import rangiffler.jupiter.annotation.meta.GrpcTest;

@GrpcTest
public abstract class GrpcStatisticTestBase {

  protected static final Config CFG = Config.getInstance();
  protected RangifflerStatisticServiceGrpc.RangifflerStatisticServiceBlockingStub blockingStub;
  protected RangifflerPhotoServiceGrpc.RangifflerPhotoServiceBlockingStub photoServiceBlockingStub;

  @BeforeEach
  void before() {
    var channel = ChannelProvider.INSTANCE.channel(CFG.photoHost(), CFG.photoPort());
    blockingStub = RangifflerStatisticServiceGrpc.newBlockingStub(channel);
    photoServiceBlockingStub = RangifflerPhotoServiceGrpc.newBlockingStub(channel);
  }
}
